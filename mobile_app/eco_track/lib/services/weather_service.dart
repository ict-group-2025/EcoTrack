import 'package:eco_track/models/weather_model.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class WeatherService {
  final String apiKey = '76805f1ca0234e4568454f73948dbfdb'; // Thay bằng API key của bạn
  final String baseUrl = 'https://api.openweathermap.org/data/2.5/weather';

  // Lấy thời tiết theo tọa độ
  Future<Weather> getWeatherByCoordinates(double lat, double lon) async {
    try {
      final url = Uri.parse('$baseUrl?lat=$lat&lon=$lon&appid=$apiKey');
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return Weather.fromJson(data);
      } else {
        throw Exception('Failed to load weather: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching weather: $e');
    }
  }

  // Lấy thời tiết theo tên thành phố
  Future<Weather> getWeatherByCity(String cityName) async {
    try {
      final url = Uri.parse('$baseUrl?q=$cityName&appid=$apiKey');
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return Weather.fromJson(data);
      } else {
        throw Exception('Failed to load weather: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching weather: $e');
    }
  }
}
