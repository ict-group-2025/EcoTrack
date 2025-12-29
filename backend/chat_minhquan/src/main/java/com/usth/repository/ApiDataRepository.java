package com.usth.repository;

import com.usth.entity.ApiData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ApiDataRepository extends JpaRepository<ApiData, Long> {
    // Lấy bản ghi thời tiết mới nhất của 1 địa điểm
    Optional<ApiData> findTopByLocationIdOrderByRecordedAtDesc(Long locationId);
}