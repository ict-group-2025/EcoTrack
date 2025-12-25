import 'package:eco_track/layout/weather/body_weather.dart';
import 'package:eco_track/layout/weather/details_weather.dart';
import 'package:eco_track/layout/weather/header_weather.dart';
import 'package:eco_track/providers/weather_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/location_provider.dart';

class WeatherScreen extends StatelessWidget {
  const WeatherScreen({super.key});

  List<Color> _getWeatherColors(String? icon) {
    if (icon == null || icon.isEmpty) {
      return [const Color(0xFF5B6C7D), const Color(0xFF2D3E50)]; // Mặc định
    }

    final weatherCode = icon.length >= 2 ? icon.substring(0, 2) : icon;

    switch (weatherCode) {
      case '01': // Clear sky - Trời nắng/quang đãng
        return icon.endsWith('d')
            ? [
                const Color(0xFF4A90E2),
                const Color(0xFF50C9C3),
              ] // Xanh dương sáng (ngày)
            : [
                const Color(0xFF0F2027),
                const Color(0xFF203A43),
              ]; // Xanh đậm (đêm)

      case '02': // Few clouds - Ít mây
        return icon.endsWith('d')
            ? [
                const Color(0xFF5B9BD5),
                const Color(0xFF70C1B3),
              ] // Xanh lam nhạt (ngày)
            : [
                const Color(0xFF1C3F51),
                const Color(0xFF2C5F77),
              ]; // Xanh navy (đêm)

      case '03': // Scattered clouds - Mây rải rác
      case '04': // Broken clouds - Nhiều mây
        return [const Color(0xFF607D8B), const Color(0xFF455A64)]; // Xám xanh

      case '09': // Shower rain - Mưa rào
      case '10': // Rain - Mưa
        return [const Color(0xFF536976), const Color(0xFF292E49)]; // Xám đen

      case '11': // Thunderstorm - Giông bão
        return [
          const Color(0xFF373B44),
          const Color(0xFF4286f4),
        ]; // Đen xanh điện

      case '13': // Snow - Tuyết
        return [const Color(0xFFE0EAFC), const Color(0xFFCFDEF3)]; // Trắng xanh

      case '50': // Mist/Fog - Sương mù
        return [const Color(0xFF757F9A), const Color(0xFFD7DDE8)]; // Xám nhạt

      default:
        return [const Color(0xFF5B6C7D), const Color(0xFF2D3E50)]; // Mặc định
    }
  }

  @override
  Widget build(BuildContext context) {
    return Consumer2<LocationProvider, WeatherProvider>(
      builder: (_, locationProvider, weatherProvider, __) {
        // Hiển thị loading
        if (locationProvider.position == null) {
          return Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [Color(0xFF5B6C7D), Color(0xFF2D3E50)],
              ),
            ),
            child: const Center(
              child: CircularProgressIndicator(color: Colors.white),
            ),
          );
        }

        final weather = weatherProvider.weather;
        if (weather == null) {
          return Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [Color(0xFF5B6C7D), Color(0xFF2D3E50)],
              ),
            ),
            child: const Center(
              child: Text(
                'Không lấy được dữ liệu thời tiết',
                style: TextStyle(color: Colors.white, fontSize: 16),
              ),
            ),
          );
        }

        final locationName =
            locationProvider.locationName ?? 'Đang xác định...';

        String? weatherIcon;
        try {
          weatherIcon = weather.icon;
        } catch (e) {
          weatherIcon = null;
        }

        final gradientColors = _getWeatherColors(weatherIcon);

        return Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: gradientColors, // Màu động theo thời tiết
            ),
          ),
          child: SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  HeaderWeather(locationName: locationName),
                  const SizedBox(height: 30),
                  BodyWeather(weather: weather),
                  const SizedBox(height: 20),
                  DetailsWeather(weather: weather),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
