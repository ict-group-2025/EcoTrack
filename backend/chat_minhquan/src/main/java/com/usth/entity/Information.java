package com.usth.entity;

import com.usth.entity.ApiData;
import com.usth.entity.Sensor;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "information", indexes = {
        @Index(name = "idx_info_location", columnList = "location_id"),
        @Index(name = "idx_info_sensor", columnList = "sensor_id"),
        @Index(name = "idx_info_api", columnList = "api_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- CÁC KHÓA NGOẠI (Foreign Keys) ---

    // 1. Liên kết Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // 2. Liên kết Sensor (Dữ liệu đo đạc thực tế)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    // 3. Liên kết API (Dữ liệu từ OpenWeather)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id", nullable = false)
    private ApiData apiData;

    // --- CÁC TRƯỜNG DỮ LIỆU KHÁC ---

    @Column(name = "ai_weather_prediction")
    private String aiWeatherPrediction;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Mẹo: Tự động set thời gian khi tạo mới bản ghi
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}