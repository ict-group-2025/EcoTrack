package com.usth.config;

import com.usth.repository.LocationRepository;
import com.usth.service.WeatherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(LocationRepository locationRepository, WeatherService weatherService) {
        return args -> {
            System.out.println("--- BẮT ĐẦU KIỂM TRA VÀ NẠP DỮ LIỆU (BACKGROUND) ---");

            // Danh sách 30 thành phố
            List<String> famousCities = Arrays.asList(
                    // Châu Á
                    "Hanoi", "Ho Chi Minh City", "Tokyo", "Seoul", "Beijing", "Bangkok", "Singapore", "Mumbai", "Dubai",
                    // Châu Âu
                    "London", "Paris", "Berlin", "Madrid", "Rome", "Moscow", "Amsterdam", "Istanbul",
                    // Châu Mỹ
                    "New York", "Los Angeles", "Chicago", "Toronto", "Rio de Janeiro", "Buenos Aires",
                    // Khác
                    "Sydney", "Melbourne", "Cairo", "Cape Town"
            );

            // CHẠY Ở LUỒNG RIÊNG ĐỂ KHÔNG TREO APP
            new Thread(() -> {
                int count = 0;
                for (String city : famousCities) {
                    try {
                        // LOGIC MỚI: Kiểm tra từng thành phố, nếu chưa có mới gọi API
                        // (Tránh gọi lại API cho các thành phố đã có -> Tiết kiệm quota)
                        if (locationRepository.findByCityName(city).isEmpty()) {
                            weatherService.getWeatherByCity(city);
                            System.out.println("✅ Đã thêm mới: " + city);
                            count++;
                            Thread.sleep(300); // Nghỉ xíu để API không chặn
                        } else {
                            // System.out.println("Skip: " + city + " (Đã có)");
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi nạp " + city + ": " + e.getMessage());
                    }
                }
                System.out.println("--- HOÀN TẤT: ĐÃ NẠP THÊM " + count + " THÀNH PHỐ MỚI ---");
            }).start();
        };
    }
}