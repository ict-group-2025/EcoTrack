import 'package:flutter/material.dart';

class DetailsWeather extends StatelessWidget {
  final dynamic weather;
  const DetailsWeather({super.key, required this.weather});

  // Helper methods để lấy giá trị an toàn
  String _getFeelsLike() {
    try {
      return weather.feelsLike?.toStringAsFixed(1) ??
          weather.temperature.toStringAsFixed(1);
    } catch (e) {
      return 'N/A';
    }
  }

  String _getHumidity() {
    try {
      return '${weather.humidity ?? 0}%';
    } catch (e) {
      return 'N/A';
    }
  }

  String _getWindSpeed() {
    try {
      return '${weather.windSpeed?.toStringAsFixed(1) ?? 0.0} m/s';
    } catch (e) {
      return 'N/A';
    }
  }

  String _getPressure() {
    try {
      return '${weather.pressure ?? 0} hPa';
    } catch (e) {
      return 'N/A';
    }
  }

  @override
  Widget build(BuildContext context) {
    final details = [
      {'title': 'Cảm giác', 'value': _getFeelsLike(), 'icon': Icons.thermostat},
      {'title': 'Độ ẩm', 'value': _getHumidity(), 'icon': Icons.water_drop},
      {'title': 'Gió', 'value': _getWindSpeed(), 'icon': Icons.air},
      {'title': 'Áp suất', 'value': _getPressure(), 'icon': Icons.speed},
    ];
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        GridView.count(
          crossAxisCount: 2,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisSpacing: 15,
          mainAxisSpacing: 15,
          childAspectRatio: 1.5,
          children: details.map((d) {
            return Container(
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.05),
                borderRadius: BorderRadius.circular(15),
                border: Border.all(color: Colors.white.withOpacity(0.1)),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(d['icon'] as IconData, color: Colors.white70, size: 30),
                  const SizedBox(height: 8),
                  Text(
                    d['title'] as String,
                    style: const TextStyle(color: Colors.white70, fontSize: 12),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    d['value'] as String,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            );
          }).toList(),
        ),
      ],
    );
  }
}
