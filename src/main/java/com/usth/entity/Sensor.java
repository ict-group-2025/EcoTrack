package com.usth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor", indexes = {
        @Index(name = "idx_sensor_time", columnList = "recorded_at")
})
@Data // Tự động sinh Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // Tạo Constructor rỗng
@AllArgsConstructor // Tự sinh Constructor đầy đủ tham số
@Builder
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // BIGINT UNSIGNED -> Long

    @Column(name = "temperature_sensor", nullable = false)
    private Double temperatureSensor; // DOUBLE NOT NULL

    @Column(name = "pressure")
    private Double pressure;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "humidity")
    private Double humidity;

    @Column(name = "aqi")
    private Integer aqi; // INT UNSIGNED -> Integer

    @Column(name = "tvoc")
    private Double tvoc;

    @Column(name = "eco2")
    private Double eco2;

    @Column(name = "dust_density")
    private Double dustDensity;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt; // DATETIME -> LocalDateTime
}