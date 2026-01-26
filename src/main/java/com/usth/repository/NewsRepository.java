package com.usth.repository;

import com.usth.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

        // Tìm theo guid (tránh duplicate khi ingest)
        Optional<News> findByGuid(String guid);

        // Check tồn tại
        boolean existsByGuid(String guid);

        // Lấy tin theo category
        List<News> findByCategoryOrderByPublishedAtDesc(String category);

        // Lấy tin theo category với paging
        Page<News> findByCategoryOrderByPublishedAtDesc(String category, Pageable pageable);

        // Lấy tất cả tin, mới nhất trước
        List<News> findTop20ByOrderByPublishedAtDesc();

        // Tìm kiếm theo keyword
        @Query("SELECT n FROM News n WHERE " +
                        "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "ORDER BY n.publishedAt DESC")
        List<News> searchByKeyword(@Param("keyword") String keyword);

        // Tìm kiếm với paging
        @Query("SELECT n FROM News n WHERE " +
                        "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "ORDER BY n.publishedAt DESC")
        Page<News> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

        // Đếm tin theo category
        long countByCategory(String category);

        // Lấy tin gần đây (trong 24h) - sử dụng parameter thay vì arithmetic
        @Query("SELECT n FROM News n WHERE n.publishedAt >= :since ORDER BY n.publishedAt DESC")
        List<News> findRecentNews(@Param("since") java.time.LocalDateTime since);
}
