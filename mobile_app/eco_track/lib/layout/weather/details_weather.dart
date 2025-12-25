import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class DetailsWeather extends StatelessWidget {
  final dynamic weather;
  const DetailsWeather({super.key, required this.weather});

  DateTime _getSunrise() {
    try {
      return weather.sunrise;
    } catch (e) {
      return DateTime.now();
    }
  }

  DateTime _getSunset() {
    try {
      return weather.sunset;
    } catch (e) {
      return DateTime.now();
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
      {
        'title': 'Bình minh',
        'value': _getSunrise(),
        'icon': Icons.sunny,
        'ic_color': Colors.amber[300],
      },
      {
        'title': 'Hoàng hôn',
        'value': _getSunset(),
        'icon': Icons.sunny_snowing,
        'ic_color': Colors.redAccent,
      },
      {
        'title': 'Độ ẩm',
        'value': _getHumidity(),
        'icon': Icons.water_drop,
        'ic_color': Colors.blue[500],
      },
      {
        'title': 'Gió',
        'value': _getWindSpeed(),
        'icon': Icons.air,
        'ic_color': Colors.white,
      },
      // {'title': 'Áp suất', 'value': _getPressure(), 'icon': Icons.air,
      //   'ic_color': Colors.greenAccent,
      // },
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
                  Icon(
                    d['icon'] as IconData,
                    color: d['ic_color'] as Color,
                    size: 30,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    d['title'].toString(),
                    style: const TextStyle(color: Colors.white70, fontSize: 12),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    d['value'] is DateTime
                        ? DateFormat('HH:mm').format(d['value'] as DateTime)
                        : d['value'].toString(),
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
