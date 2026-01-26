package com.usth.controller;

import com.usth.entity.News;
import com.usth.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST API cho News
 * - GET /api/news - Lấy danh sách tin
 * - GET /api/news/{id} - Chi tiết tin
 * - GET /api/news/search?q= - Tìm kiếm
 * - GET /api/news/stats - Thống kê
 * - POST /api/news/ingest - Trigger fetch thủ công
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * GET /api/news
     * Lấy danh sách tin tức
     * Query params: category, page, size
     */
    @GetMapping
    public ResponseEntity<?> getNews(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<News> newsPage;

            if (category != null && !category.isEmpty()) {
                newsPage = newsService.getNewsByCategory(category, page, size);
            } else {
                newsPage = newsService.getAllNews(page, size);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", newsPage.getContent());
            response.put("page", newsPage.getNumber());
            response.put("size", newsPage.getSize());
            response.put("totalElements", newsPage.getTotalElements());
            response.put("totalPages", newsPage.getTotalPages());
            response.put("last", newsPage.isLast());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to load news: " + e.getMessage()));
        }
    }

    /**
     * GET /api/news/latest
     * Lấy 20 tin mới nhất
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestNews() {
        try {
            List<News> news = newsService.getLatestNews();
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to load latest news"));
        }
    }

    /**
     * GET /api/news/{id}
     * Chi tiết 1 tin
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsDetail(@PathVariable Long id) {
        try {
            Optional<News> news = newsService.getNewsById(id);
            if (news.isPresent()) {
                return ResponseEntity.ok(news.get());
            }
            return ResponseEntity.status(404)
                    .body(Map.of("error", "News not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to load news detail"));
        }
    }

    /**
     * GET /api/news/search?q=keyword
     * Tìm kiếm tin theo keyword
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchNews(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (q == null || q.trim().isEmpty() || q.length() > 100) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid search keyword"));
            }

            Page<News> results = newsService.searchNews(q.trim(), page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("content", results.getContent());
            response.put("keyword", q);
            response.put("page", results.getNumber());
            response.put("size", results.getSize());
            response.put("totalElements", results.getTotalElements());
            response.put("totalPages", results.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Search failed"));
        }
    }

    /**
     * GET /api/news/stats
     * Thống kê số tin theo category
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            Map<String, Long> stats = newsService.getNewsStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to get stats"));
        }
    }

    /**
     * POST /api/news/ingest
     * Trigger fetch tin thủ công (Admin)
     */
    @PostMapping("/ingest")
    public ResponseEntity<?> triggerIngest() {
        try {
            int count = newsService.ingestAllSources();
            return ResponseEntity.ok(Map.of(
                    "message", "Ingest completed",
                    "newArticles", count));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Ingest failed: " + e.getMessage()));
        }
    }
}
