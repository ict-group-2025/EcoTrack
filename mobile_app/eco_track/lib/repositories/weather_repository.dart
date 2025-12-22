import 'package:eco_track/models/weather_model.dart';
import 'package:eco_track/services/weather_service.dart';

class WeatherRepository {
  final WeatherService _weatherService = WeatherService();

  Future<Weather> getWeatherByLocation(double lat, double lon) async {
    return await _weatherService.getWeatherByCoordinates(lat, lon);
  }

  Future<Weather> getWeatherByCityName(String cityName) async {
    return await _weatherService.getWeatherByCity(cityName);
  }
}
