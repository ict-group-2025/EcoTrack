package com.usth.repository;

import com.usth.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Hàm này giúp tìm địa điểm theo tên (ví dụ tìm "Hanoi")
    // Dù chưa dùng ngay nhưng rất cần cho logic mở rộng sau này
    Optional<Location> findByCityName(String cityName);
}