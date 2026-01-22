package com.usth.repository;

import com.usth.entity.ApiData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApiDataRepository extends JpaRepository<ApiData, Long> {
    // Lấy bản ghi thời tiết mới nhất của 1 địa điểm
    Optional<ApiData> findTopByLocationIdOrderByRecordedAtDesc(Long locationId);
    
    // Tối ưu: Lấy tất cả ApiData mới nhất cho tất cả locations trong 1 query (tránh N+1)
    // Sử dụng JOIN FETCH để load location luôn, tránh lazy loading
    @Query("SELECT DISTINCT a FROM ApiData a " +
           "INNER JOIN FETCH a.location l " +
           "WHERE a.id IN (SELECT MAX(a2.id) FROM ApiData a2 GROUP BY a2.location.id)")
    List<ApiData> findLatestApiDataForAllLocations();
    
    // Tối ưu: Lấy ApiData mới nhất với JOIN FETCH để load location luôn
    @Query("SELECT a FROM ApiData a WHERE a.location.id = :locationId ORDER BY a.recordedAt DESC")
    List<ApiData> findByLocationIdOrderByRecordedAtDesc(@Param("locationId") Long locationId);
}