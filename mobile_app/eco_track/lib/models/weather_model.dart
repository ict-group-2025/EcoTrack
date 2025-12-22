class Weather {
  final String cityName;
  final double temperature;
  final double tempMin;
  final double tempMax;
  final String description;
  final String icon;
  final double feelsLike;
  final int humidity;
  final double windSpeed;
  final int pressure;
  final DateTime sunrise;
  final DateTime sunset;

  Weather({
    required this.cityName,
    required this.temperature,
    required this.tempMin,
    required this.tempMax,
    required this.description,
    required this.icon,
    required this.feelsLike,
    required this.humidity,
    required this.windSpeed,
    required this.pressure,
    required this.sunrise,
    required this.sunset,
  });

  factory Weather.fromJson(Map<String, dynamic> json) {
    final main = json['main'] ?? {};
    final weatherList = json['weather'] as List? ?? [];
    final weather = weatherList.isNotEmpty ? weatherList[0] : {};
    final sys = json['sys'] ?? {};
    final wind = json['wind'] ?? {};

    return Weather(
      cityName: json['name'] ?? 'Unknown',
      temperature: (main['temp'] - 273.15 ?? 0).toDouble(),
      tempMin: (main['temp_min'] - 273.15 ?? 0).toDouble(),
      tempMax: (main['temp_max'] - 273.15 ?? 0).toDouble(),
      feelsLike: (main['feels_like'] - 273.15 ?? 0).toDouble(),
      humidity: main['humidity'] ?? 0,
      pressure: main['pressure'] ?? 0,
      description: weather['description'] ?? '',
      icon: weather['icon'] ?? '',
      windSpeed: (wind['speed'] ?? 0).toDouble(),
      sunrise: DateTime.fromMillisecondsSinceEpoch(
        (sys['sunrise'] ?? 0) * 1000,
      ),
      sunset: DateTime.fromMillisecondsSinceEpoch((sys['sunset'] ?? 0) * 1000),
    );
  }

  String get iconUrl => 'https://openweathermap.org/img/wn/$icon@2x.png';
}
