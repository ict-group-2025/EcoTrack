import 'package:flutter/material.dart';

class BodyWeather extends StatelessWidget {
  final dynamic weather;
  const BodyWeather({super.key, required this.weather});

  // Helper method để lấy description an toàn
  String _getDescription() {
    try {
      // Thử các tên property khác nhau
      if (weather.description != null) return weather.description;
      if (weather.condition != null) return weather.condition;
      if (weather.main != null) return weather.main;
      return 'Không xác định';
    } catch (e) {
      return 'Không xác định';
    }
  }

  // Helper method để lấy icon an toàn
  String? _getIcon() {
    try {
      return weather.icon;
    } catch (e) {
      return null;
    }
  }

  // Helper method để lấy windSpeed an toàn
  String _getWindSpeed() {
    try {
      if (weather.windSpeed != null) {
        return weather.windSpeed.toStringAsFixed(1);
      }
      return '0.0';
    } catch (e) {
      return '0.0';
    }
  }

  // Helper method để lấy humidity an toàn
  String _getHumidity() {
    try {
      if (weather.humidity != null) {
        return weather.humidity.toString();
      }
      return '0';
    } catch (e) {
      return '0';
    }
  }

  // Helper method để lấy feelsLike an toàn
  String _getFeelsLike() {
    try {
      if (weather.feelsLike != null) {
        return weather.feelsLike.toStringAsFixed(1);
      }
      return weather.temperature.toStringAsFixed(1);
    } catch (e) {
      return weather.temperature.toStringAsFixed(1);
    }
  }

  @override
  Widget build(BuildContext context) {
    final icon = _getIcon();
    return Center(
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                '${weather.temperature.toStringAsFixed(1)}°C',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 50,
                  fontWeight: FontWeight.w200,
                ),
              ),
              if (icon != null && icon.isNotEmpty)
                Image.network(
                  'https://openweathermap.org/img/wn/$icon@4x.png',
                  width: 120,
                  height: 120,
                  errorBuilder: (context, error, stackTrace) {
                    return const Icon(
                      Icons.cloud,
                      color: Colors.white,
                      size: 100,
                    );
                  },
                  loadingBuilder: (context, child, loadingProgress) {
                    if (loadingProgress == null) return child;
                    return const SizedBox(
                      width: 120,
                      height: 120,
                      child: Center(
                        child: CircularProgressIndicator(
                          color: Colors.white,
                          strokeWidth: 2,
                        ),
                      ),
                    );
                  },
                )
              else
                const Icon(Icons.cloud, color: Colors.white, size: 100),
            ],
          ),

          Text(
            _getDescription(),
            style: const TextStyle(
              color: Colors.white70,
              fontSize: 20,
              fontWeight: FontWeight.w300,
            ),
            textAlign: TextAlign.center,
          ),
          Text(
            '${weather.tempMin}°/${weather.tempMax}° Cảm giác như ${_getFeelsLike()}°C',
            style: const TextStyle(color: Colors.white60, fontSize: 14),
          ),

          Text(
            'Gió: ${_getWindSpeed()} m/s • Độ ẩm: ${_getHumidity()}%',
            style: const TextStyle(color: Colors.white60, fontSize: 14),
          ),

          const SizedBox(height: 10),

          // Cảm giác như
        ],
      ),
    );
  }
}
