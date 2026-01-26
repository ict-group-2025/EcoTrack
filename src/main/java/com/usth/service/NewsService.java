package com.usth.service;

import com.usth.entity.News;
import com.usth.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

/**
 * Service xử lý tin tức
 * - Fetch RSS từ VnExpress
 * - Lưu vào database
 * - Cung cấp API cho frontend
 */
@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private NewsRepository newsRepository;

    // Danh sách RSS feeds
    private static final List<RssSource> RSS_SOURCES = Arrays.asList(
            new RssSource("VnExpress Thời tiết", "weather", "https://vnexpress.net/rss/thoi-tiet.rss"),
            new RssSource("VnExpress Sức khỏe", "health", "https://vnexpress.net/rss/suc-khoe.rss"),
            new RssSource("VnExpress Môi trường", "air", "https://vnexpress.net/rss/moi-truong.rss"));

    /**
     * Lấy tất cả tin, phân trang
     */
    public Page<News> getAllNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsRepository.findAll(pageable);
    }

    /**
     * Lấy tin theo category
     */
    public Page<News> getNewsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsRepository.findByCategoryOrderByPublishedAtDesc(category, pageable);
    }

    /**
     * Lấy tin mới nhất (top 20)
     */
    public List<News> getLatestNews() {
        return newsRepository.findTop20ByOrderByPublishedAtDesc();
    }

    /**
     * Lấy chi tiết tin theo ID
     */
    public Optional<News> getNewsById(Long id) {
        return newsRepository.findById(id);
    }

    /**
     * Tìm kiếm tin theo keyword
     */
    public Page<News> searchNews(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * Thống kê số tin theo category
     */
    public Map<String, Long> getNewsStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("weather", newsRepository.countByCategory("weather"));
        stats.put("health", newsRepository.countByCategory("health"));
        stats.put("air", newsRepository.countByCategory("air"));
        stats.put("total", newsRepository.count());
        return stats;
    }

    /**
     * Scheduled job: Fetch tin mới mỗi 5 phút
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 phút
    public void scheduledIngest() {
        logger.info("⏰ [News Scheduler] Bắt đầu fetch tin tức...");
        int total = ingestAllSources();
        logger.info("✅ [News Scheduler] Hoàn tất: {} tin mới", total);
    }

    /**
     * Fetch tin từ tất cả RSS sources
     */
    public int ingestAllSources() {
        int totalNew = 0;
        for (RssSource source : RSS_SOURCES) {
            try {
                int count = ingestFromRss(source);
                totalNew += count;
                logger.info("📰 {} - {} tin mới", source.name, count);
            } catch (Exception e) {
                logger.error("❌ Lỗi fetch {}: {}", source.name, e.getMessage());
            }
        }
        return totalNew;
    }

    /**
     * Fetch tin từ 1 RSS source
     */
    private int ingestFromRss(RssSource source) throws Exception {
        URL url = new URL(source.url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 EcoTrack/1.0");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int newCount = 0;

        try (InputStream is = conn.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList items = doc.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String guid = getElementText(item, "guid");
                if (guid == null || guid.isEmpty()) {
                    guid = getElementText(item, "link");
                }

                // Skip nếu đã tồn tại
                if (newsRepository.existsByGuid(guid)) {
                    continue;
                }

                News news = new News();
                news.setGuid(guid);
                news.setTitle(cleanHtml(getElementText(item, "title")));
                news.setSummary(cleanHtml(getElementText(item, "description")));
                news.setLink(getElementText(item, "link"));
                news.setCategory(source.category);
                news.setSource(source.name);
                news.setAuthor(getElementText(item, "author"));

                // Parse published date
                String pubDate = getElementText(item, "pubDate");
                if (pubDate != null) {
                    news.setPublishedAt(parseRssDate(pubDate));
                }

                // Extract image từ description hoặc enclosure
                String imageUrl = extractImageUrl(item);
                if (imageUrl != null) {
                    news.setImageUrl(imageUrl);
                }

                newsRepository.save(news);
                newCount++;
            }
        }

        return newCount;
    }

    // === Helper methods ===

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            if (node != null) {
                return node.getTextContent();
            }
        }
        return null;
    }

    private String cleanHtml(String html) {
        if (html == null)
            return null;
        // Remove HTML tags
        String text = html.replaceAll("<[^>]+>", "");
        // Decode entities
        text = text.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&nbsp;", " ");
        return text.trim();
    }

    private String extractImageUrl(Element item) {
        // Try enclosure first
        NodeList enclosures = item.getElementsByTagName("enclosure");
        if (enclosures.getLength() > 0) {
            Element enc = (Element) enclosures.item(0);
            String type = enc.getAttribute("type");
            if (type != null && type.startsWith("image")) {
                return enc.getAttribute("url");
            }
        }

        // Extract from description
        String desc = getElementText(item, "description");
        if (desc != null) {
            Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']");
            java.util.regex.Matcher matcher = pattern.matcher(desc);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    private LocalDateTime parseRssDate(String dateStr) {
        try {
            // RFC 822 format: "Sun, 26 Jan 2026 10:00:00 +0700"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            ZonedDateTime zdt = ZonedDateTime.parse(dateStr, formatter);
            return zdt.toLocalDateTime();
        } catch (Exception e) {
            // Fallback
            try {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e2) {
                return LocalDateTime.now();
            }
        }
    }

    // RSS Source class
    private static class RssSource {
        String name;
        String category;
        String url;

        RssSource(String name, String category, String url) {
            this.name = name;
            this.category = category;
            this.url = url;
        }
    }
}
