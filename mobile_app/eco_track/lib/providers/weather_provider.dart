import 'package:eco_track/models/weather_model.dart';
import 'package:eco_track/repositories/weather_repository.dart';
import 'package:flutter/material.dart';

class WeatherProvider extends ChangeNotifier {
  final WeatherRepository _repository = WeatherRepository();

  Weather? _weather;
  bool _isLoading = false;
  String? _error;

  Weather? get weather => _weather;
  bool get isLoading => _isLoading;
  String? get error => _error;

  // Lấy thời tiết theo tọa độ
  Future<void> fetchWeatherByCoordinates(double lat, double lon) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _weather = await _repository.getWeatherByLocation(lat, lon);
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Lấy thời tiết theo tên thành phố
  Future<void> fetchWeatherByCity(String cityName) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _weather = await _repository.getWeatherByCityName(cityName);
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }

 
}
