package com.usth.config;

import com.usth.repository.LocationRepository;
import com.usth.service.WeatherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(LocationRepository locationRepository, WeatherService weatherService) {
        return args -> {
            if (locationRepository.count() == 0) {
                System.out.println("--- ĐANG KHỞI TẠO DỮ LIỆU FORUM MẪU ---");
                try {
                    // Tự động gọi API để nạp dữ liệu chuẩn
                    weatherService.getWeatherByCity("Hanoi");
                    weatherService.getWeatherByCity("Ho Chi Minh City");
                    weatherService.getWeatherByCity("Da Nang");
                    weatherService.getWeatherByCity("Seoul");
                    weatherService.getWeatherByCity("Tokyo");
                    System.out.println("--- ĐÃ NẠP XONG 5 THÀNH PHỐ ---");
                } catch (Exception e) {
                    System.out.println("Lỗi nạp data: " + e.getMessage());
                }
            }
        };
    }
}