package com.usth.controller;

import com.usth.dto.WeatherResponse;
import com.usth.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherService weatherService;

    // API 1: Tìm kiếm (Tạo mới nếu chưa có)
    @GetMapping("/search")
    public ResponseEntity<?> searchCity(@RequestParam String city) {
        try {
            return ResponseEntity.ok(weatherService.getWeatherByCity(city));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // API 2: Lấy danh sách Forum (Các thành phố đã có)
    @GetMapping("/list")
    public ResponseEntity<?> getAllCities() {
        return ResponseEntity.ok(weatherService.getAllLocations());
    }
}