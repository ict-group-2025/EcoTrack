package com.usth.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class WeatherResponse {
    // 1. Thông tin định danh (Để Chat)
    private Long locationId;
    private String cityName;
    private String country;

    // 2. Thông tin thời tiết (Weather API)
    private Double temperature;      // Nhiệt độ
    private Double humidity;         // Độ ẩm (QUAN TRỌNG)
    private Double pressure;         // Áp suất (QUAN TRỌNG)
    private Double windSpeed;        // Tốc độ gió
    private String weatherDescription; // Mô tả (VD: Mây rải rác)
    private String weatherIcon;      // Icon ảnh

    // 3. Thông tin ô nhiễm (Air Pollution API)
    private Double co;   // Khí CO
    private Double no2;  // Khí NO2
    private Double so2;  // Khí SO2

    // 4. Thời gian cập nhật
    private LocalDateTime recordedAt;
}