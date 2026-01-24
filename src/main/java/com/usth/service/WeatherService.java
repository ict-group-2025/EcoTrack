package com.usth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usth.dto.WeatherResponse;
import com.usth.entity.ApiData;
import com.usth.entity.Location;
import com.usth.repository.ApiDataRepository;
import com.usth.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({ "nullness", "NullAway" })
public class WeatherService {

    private final LocationRepository locationRepository;
    private final ApiDataRepository apiDataRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.api.key}")
    private String apiKey;

    private static final String GEO_API_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s";
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric&lang=vi";
    private static final String POLLUTION_API_URL = "https://api.openweathermap.org/data/2.5/air_pollution?lat=%f&lon=%f&appid=%s";
    private static final String FORECAST_API_URL = "https://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&appid=%s&units=metric&lang=vi";

    // --- 1. LẤY DANH SÁCH CHO FORUM (TỐI ƯU: Tránh N+1 Query) ---
    public List<WeatherResponse> getAllLocations() {
        List<Location> locations = locationRepository.findAll();

        // Tối ưu: Lấy tất cả ApiData mới nhất trong 1 query thay vì query từng location
        List<ApiData> latestApiDataList = apiDataRepository.findLatestApiDataForAllLocations();

        // Tạo Map để tra cứu nhanh: locationId -> ApiData
        Map<Long, ApiData> apiDataMap = latestApiDataList.stream()
                .collect(Collectors.toMap(
                        apiData -> apiData.getLocation().getId(),
                        apiData -> apiData,
                        (existing, replacement) -> existing // Giữ bản cũ nếu trùng
                ));

        // Map sang DTO
        return locations.stream().map(location -> {
            ApiData latestData = apiDataMap.getOrDefault(location.getId(), new ApiData());
            return convertToDTO(location, latestData);
        }).collect(Collectors.toList());
    }

    // --- 2. TÌM KIẾM & GỌI API ---
    public WeatherResponse getWeatherByCity(String cityName) {
        Optional<Location> existingLocation = locationRepository.findByCityName(cityName);
        Location location = existingLocation.orElseGet(() -> fetchCoordinatesAndCreateLocation(cityName));

        // Gọi API lấy dữ liệu mới nhất
        ApiData apiData = fetchWeatherFromApi(location);

        // Đẩy sang DTO để trả về Frontend hiển thị chi tiết
        return convertToDTO(location, apiData);
    }

    public Object getForecast(String cityName) {
        Optional<Location> existingLocation = locationRepository.findByCityName(cityName);
        Location location = existingLocation.orElseGet(() -> fetchCoordinatesAndCreateLocation(cityName));

        String url = String.format(FORECAST_API_URL, location.getLatitude(), location.getLongitude(), apiKey);
        try {
            return restTemplate.getForObject(url, Object.class);
        } catch (Exception e) {
            log.error("Lỗi khi lấy forecast cho {}: {}", cityName, e.getMessage());
            throw new RuntimeException("Không thể lấy dữ liệu dự báo: " + e.getMessage());
        }
    }

    // --- 3. LOGIC GỌI API ---
    @SuppressWarnings("nullness")
    private Location fetchCoordinatesAndCreateLocation(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thành phố không được để trống");
        }

        try {
            String geoUrl = String.format(GEO_API_URL, cityName.trim(), apiKey);
            String response = Objects.requireNonNull(restTemplate.getForObject(geoUrl, String.class),
                    "Không nhận được phản hồi từ API");

            JsonNode root = objectMapper.readTree(response);

            if (root.isEmpty() || !root.isArray()) {
                throw new RuntimeException("Không tìm thấy thành phố: " + cityName);
            }

            JsonNode firstResult = root.get(0);
            if (firstResult == null) {
                throw new RuntimeException("Dữ liệu API không hợp lệ");
            }

            String standardName = firstResult.path("name").asText();
            if (standardName.isEmpty()) {
                throw new RuntimeException("Không thể lấy tên thành phố từ API");
            }

            // Check trùng lặp
            Optional<Location> check = locationRepository.findByCityName(standardName);
            if (check.isPresent())
                return check.get();

            Double lat = firstResult.path("lat").asDouble();
            Double lon = firstResult.path("lon").asDouble();

            if (lat == null || lon == null) {
                throw new RuntimeException("Không thể lấy tọa độ từ API");
            }

            Location savedLocation = locationRepository.save(Location.builder()
                    .cityName(standardName)
                    .countryCode(firstResult.path("country").asText())
                    .latitude(lat)
                    .longitude(lon)
                    .build());
            return Objects.requireNonNull(savedLocation, "Saved location cannot be null");
        } catch (RestClientException e) {
            log.error("Lỗi kết nối API khi tìm thành phố: {}", cityName, e);
            throw new RuntimeException("Không thể kết nối đến API thời tiết: " + e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi khi tìm thành phố: {}", cityName, e);
            throw new RuntimeException("Lỗi khi tìm thành phố: " + e.getMessage());
        }
    }

    @SuppressWarnings("nullness")
    private ApiData fetchWeatherFromApi(Location location) {
        if (location == null || location.getLatitude() == null || location.getLongitude() == null) {
            throw new IllegalArgumentException("Location không hợp lệ");
        }

        try {
            String weatherUrl = String.format(WEATHER_API_URL,
                    location.getLatitude(), location.getLongitude(), apiKey);
            String pollutionUrl = String.format(POLLUTION_API_URL,
                    location.getLatitude(), location.getLongitude(), apiKey);

            String weatherResponse = Objects.requireNonNull(restTemplate.getForObject(weatherUrl, String.class),
                    "Không nhận được dữ liệu thời tiết từ API");
            String pollutionResponse = restTemplate.getForObject(pollutionUrl, String.class); // Pollution API có thể
                                                                                              // null

            JsonNode wRoot = objectMapper.readTree(weatherResponse);

            if (!wRoot.has("main") || !wRoot.has("weather")) {
                throw new RuntimeException("Dữ liệu thời tiết không hợp lệ");
            }

            JsonNode weatherArray = wRoot.path("weather");
            if (!weatherArray.isArray() || weatherArray.isEmpty()) {
                throw new RuntimeException("Không có dữ liệu weather");
            }

            // MAP TỪ JSON API -> ENTITY APIDATA
            ApiData.ApiDataBuilder builder = ApiData.builder()
                    .location(location)
                    .recordedAt(LocalDateTime.now())
                    .temperatureApi(wRoot.path("main").path("temp").asDouble())
                    .humidity(wRoot.path("main").path("humidity").asDouble())
                    .pressure(wRoot.path("main").path("pressure").asDouble())
                    .windSpeed(wRoot.path("wind").path("speed").asDouble())
                    .weatherMain(weatherArray.get(0).path("main").asText())
                    .weatherDescription(weatherArray.get(0).path("description").asText())
                    .weatherIcon(weatherArray.get(0).path("icon").asText());

            // Xử lý dữ liệu ô nhiễm (có thể null)
            if (pollutionResponse != null) {
                try {
                    JsonNode pRoot = objectMapper.readTree(pollutionResponse);
                    if (pRoot.has("list") && pRoot.path("list").isArray() && !pRoot.path("list").isEmpty()) {
                        JsonNode components = pRoot.path("list").get(0).path("components");
                        builder.co(components.path("co").asDouble(0.0));
                        builder.no2(components.path("no2").asDouble(0.0));
                        builder.so2(components.path("so2").asDouble(0.0));
                    }
                } catch (Exception e) {
                    log.warn("Không thể parse dữ liệu ô nhiễm cho location {}: {}", location.getCityName(),
                            e.getMessage());
                }
            }

            ApiData savedApiData = apiDataRepository.save(builder.build());
            return Objects.requireNonNull(savedApiData, "Saved ApiData cannot be null");
        } catch (RestClientException e) {
            log.error("Lỗi kết nối API khi lấy thời tiết cho location: {}", location.getCityName(), e);
            throw new RuntimeException("Không thể kết nối đến API thời tiết: " + e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi khi lấy thời tiết cho location: {}", location.getCityName(), e);
            throw new RuntimeException("Lỗi khi lấy dữ liệu thời tiết: " + e.getMessage());
        }
    }

    // --- 4. HÀM MAP DỮ LIỆU TỪ ENTITY SANG DTO ---
    private WeatherResponse convertToDTO(Location location, ApiData apiData) {
        if (location == null) {
            throw new IllegalArgumentException("Location không được null");
        }

        WeatherResponse.WeatherResponseBuilder builder = WeatherResponse.builder()
                .locationId(location.getId())
                .cityName(location.getCityName())
                .country(location.getCountryCode());

        // Xử lý ApiData có thể null (khi chưa có dữ liệu)
        if (apiData != null && apiData.getId() != null) {
            builder.temperature(apiData.getTemperatureApi())
                    .humidity(apiData.getHumidity())
                    .pressure(apiData.getPressure())
                    .windSpeed(apiData.getWindSpeed())
                    .weatherMain(apiData.getWeatherMain()) // Map weatherMain
                    .weatherDescription(apiData.getWeatherDescription())
                    .weatherIcon(apiData.getWeatherIcon())
                    .lat(location.getLatitude()) // Map Lat
                    .lon(location.getLongitude()) // Map Lon
                    .co(apiData.getCo())
                    .no2(apiData.getNo2())
                    .recordedAt(apiData.getRecordedAt());

            // Tạo lời khuyên
            String advice = generateAdvice(apiData);
            builder.advice(advice);

            // Tạo cảnh báo (Nếu có)
            String warning = generateWarning(apiData);
            builder.warning(warning);
        }

        return builder.build();
    }

    private String generateWarning(ApiData data) {
        // Trả về null nếu không có cảnh báo nguy hiểm
        String main = data.getWeatherMain().toLowerCase();
        double temp = data.getTemperatureApi();

        if (main.contains("thunderstorm") || main.contains("tornado")) {
            return "NGUY HIỂM: Đang có bão hoặc lốc xoáy! Hạn chế ra khỏi nhà.";
        }
        if (temp > 38) {
            return "CẢNH BÁO NHIỆT ĐỘ: Nắng nóng cực đoan (>38°C). Nguy cơ sốc nhiệt!";
        }
        if (temp < 5) {
            return "CẢNH BÁO RÉT HẠI: Nhiệt độ xuống thấp (<5°C).";
        }
        if (data.getCo() != null && data.getCo() > 3000) {
            return "CẢNH BÁO Ô NHIỄM: Chỉ số CO cực cao, không khí nguy hại!";
        }

        return null; // An toàn
    }

    private String generateAdvice(ApiData data) {
        StringBuilder advice = new StringBuilder();

        // 1. Dựa trên thời tiết chính
        String main = data.getWeatherMain().toLowerCase();
        if (main.contains("rain") || main.contains("drizzle")) {
            advice.append("Trời đang mưa, nhớ mang theo ô hoặc áo mưa nhé! ☔ ");
        } else if (main.contains("thunderstorm")) {
            advice.append("Đang có dông bão, hạn chế ra đường! ⛈️ ");
        } else if (main.contains("clear")) {
            advice.append("Trời quang mây tạnh, rất thích hợp để ra ngoài! ☀️ ");
        } else if (main.contains("snow")) {
            advice.append("Trời có tuyết, hãy mặc thật ấm! ❄️ ");
        }

        // 2. Dựa trên nhiệt độ
        double temp = data.getTemperatureApi();
        if (temp < 15) {
            advice.append("Trời khá lạnh, nhớ mặc áo ấm và quàng khăn. 🧣 ");
        } else if (temp > 35) {
            advice.append("Nắng nóng gay gắt, nhớ bôi kem chống nắng và uống nhiều nước. 🥤 ");
        } else if (temp >= 20 && temp <= 30) {
            advice.append("Nhiệt độ rất dễ chịu. ");
        }

        // 3. Dựa trên ô nhiễm (Nếu có)
        if (data.getCo() != null && data.getCo() > 1000) { // Ví dụ ngưỡng CO cao
            advice.append("Chất lượng không khí không tốt (CO cao), nên đeo khẩu trang khi ra đường. 😷 ");
        }

        if (advice.length() == 0) {
            advice.append("Chúc bạn một ngày tốt lành!");
        }

        return advice.toString();
    }

    // --- 5. TÁC VỤ TỰ ĐỘNG CẬP NHẬT (NHIỆM VỤ 2) ---
    // Chạy mỗi 30 phút (fixedRate = 1800000ms)
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 1800000)
    public void updateWeatherForAllLocations() {
        log.info("--- BẮT ĐẦU CẬP NHẬT THỜI TIẾT ĐỊNH KỲ ---");
        List<Location> locations = locationRepository.findAll();

        if (locations.isEmpty()) {
            log.info("Không có location nào để cập nhật");
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (Location location : locations) {
            try {
                // Gọi lại hàm fetchWeatherFromApi có sẵn để lấy data mới và lưu vào DB
                fetchWeatherFromApi(location);
                successCount++;
                log.debug("Đã cập nhật: {}", location.getCityName());

                // Nghỉ 200ms giữa các request để tránh rate limit
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread bị interrupt khi cập nhật {}", location.getCityName());
                break;
            } catch (Exception e) {
                failCount++;
                log.error("Lỗi cập nhật {}: {}", location.getCityName(), e.getMessage());
            }
        }

        log.info("--- HOÀN TẤT CẬP NHẬT: Thành công {} / Thất bại {} ---", successCount, failCount);
    }
}