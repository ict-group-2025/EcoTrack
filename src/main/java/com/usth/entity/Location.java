package com.usth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "location", indexes = {
        // TỐI ƯU 1: Thêm index cho city_name để tìm kiếm nhanh
        @Index(name = "idx_location_name", columnList = "city_name"),
        @Index(name = "idx_location_coords", columnList = "latitude, longitude")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TỐI ƯU 2: Thêm unique = true để đảm bảo 1 thành phố chỉ lưu 1 lần
    @Column(name = "city_name", nullable = false, unique = true)
    private String cityName;

    @Column(name = "country_code", length = 5)
    private String countryCode;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    // --- QUAN HỆ ---

    // Một địa điểm có nhiều dữ liệu API thời tiết (Lịch sử cập nhật 30p/lần)
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ApiData> apiDataList;

    // Một địa điểm có nhiều bình luận chat (Phòng chat)
    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments;

    // Đã xóa Information và Sensor theo yêu cầu của bạn
}