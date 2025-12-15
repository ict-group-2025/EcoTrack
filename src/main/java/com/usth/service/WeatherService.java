package com.usth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usth.dto.WeatherResponse;
import com.usth.entity.ApiData;
import com.usth.entity.Location;
import com.usth.repository.ApiDataRepository;
import com.usth.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final LocationRepository locationRepository;
    private final ApiDataRepository apiDataRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.api.key}")
    private String apiKey;

    // --- 1. LẤY DANH SÁCH CHO FORUM ---
    public List<WeatherResponse> getAllLocations() {
        return locationRepository.findAll().stream().map(location -> {
            ApiData latestData = apiDataRepository.findTopByLocationIdOrderByRecordedAtDesc(location.getId())
                    .orElse(new ApiData());
            // Đẩy sang DTO để hiển thị ra list
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

    // --- 3. LOGIC GỌI API ---
    private Location fetchCoordinatesAndCreateLocation(String cityName) {
        try {
            String geoUrl = String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s", cityName, apiKey);
            JsonNode root = objectMapper.readTree(restTemplate.getForObject(geoUrl, String.class));

            if (root.isEmpty()) throw new RuntimeException("Không tìm thấy thành phố!");
            JsonNode firstResult = root.get(0);
            String standardName = firstResult.path("name").asText();

            // Check trùng lặp
            Optional<Location> check = locationRepository.findByCityName(standardName);
            if(check.isPresent()) return check.get();

            return locationRepository.save(Location.builder()
                    .cityName(standardName)
                    .countryCode(firstResult.path("country").asText())
                    .latitude(firstResult.path("lat").asDouble())
                    .longitude(firstResult.path("lon").asDouble())
                    .build());
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    private ApiData fetchWeatherFromApi(Location location) {
        try {
            String weatherUrl = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric&lang=vi",
                    location.getLatitude(), location.getLongitude(), apiKey);
            String pollutionUrl = String.format("https://api.openweathermap.org/data/2.5/air_pollution?lat=%f&lon=%f&appid=%s",
                    location.getLatitude(), location.getLongitude(), apiKey);

            JsonNode wRoot = objectMapper.readTree(restTemplate.getForObject(weatherUrl, String.class));
            JsonNode pRoot = objectMapper.readTree(restTemplate.getForObject(pollutionUrl, String.class));

            // MAP TỪ JSON API -> ENTITY APIDATA
            ApiData apiData = ApiData.builder()
                    .location(location)
                    .recordedAt(LocalDateTime.now())
                    .temperatureApi(wRoot.path("main").path("temp").asDouble())
                    .humidity(wRoot.path("main").path("humidity").asDouble()) // Đẩy vào Entity
                    .pressure(wRoot.path("main").path("pressure").asDouble()) // Đẩy vào Entity
                    .windSpeed(wRoot.path("wind").path("speed").asDouble())
                    .weatherMain(wRoot.path("weather").get(0).path("main").asText())
                    .weatherDescription(wRoot.path("weather").get(0).path("description").asText())
                    .weatherIcon(wRoot.path("weather").get(0).path("icon").asText())
                    .build();

            if (pRoot.has("list") && !pRoot.path("list").isEmpty()) {
                JsonNode c = pRoot.path("list").get(0).path("components");
                apiData.setCo(c.path("co").asDouble());
                apiData.setNo2(c.path("no2").asDouble());
                apiData.setSo2(c.path("so2").asDouble());
            }
            return apiDataRepository.save(apiData);
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }



    // --- 4. HÀM MAP DỮ LIỆU TỪ ENTITY SANG DTO (Cái bạn đang cần đây) ---
    private WeatherResponse convertToDTO(Location location, ApiData apiData) {
        return WeatherResponse.builder()
                .locationId(location.getId())
                .cityName(location.getCityName())
                .country(location.getCountryCode())
                // ĐẨY DỮ LIỆU TỪ ENTITY RA DTO
                .temperature(apiData.getTemperatureApi())
                .humidity(apiData.getHumidity())      // Có hiển thị
                .pressure(apiData.getPressure())      // Có hiển thị
                .windSpeed(apiData.getWindSpeed())
                .weatherDescription(apiData.getWeatherDescription())
                .weatherIcon(apiData.getWeatherIcon())
                .co(apiData.getCo())                  // Có hiển thị
                .no2(apiData.getNo2())
                .recordedAt(apiData.getRecordedAt())
                .build();
    }


    // --- 5. TÁC VỤ TỰ ĐỘNG CẬP NHẬT (NHIỆM VỤ 2) ---
    // Chạy mỗi 30 phút (fixedRate = 1800000ms)
    // Lưu ý: Cần thêm @EnableScheduling vào file FinalApplication.java nhé
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 1800000)
    public void updateWeatherForAllLocations() {
        System.out.println("--- BẮT ĐẦU CẬP NHẬT THỜI TIẾT ĐỊNH KỲ ---");
        List<Location> locations = locationRepository.findAll();

        for (Location location : locations) {
            try {
                // Gọi lại hàm fetchWeatherFromApi có sẵn để lấy data mới và lưu vào DB
                fetchWeatherFromApi(location);
                System.out.println("Đã cập nhật: " + location.getCityName());
            } catch (Exception e) {
                System.err.println("Lỗi cập nhật " + location.getCityName() + ": " + e.getMessage());
            }
        }
        System.out.println("--- HOÀN TẤT CẬP NHẬT ---");
    }
}