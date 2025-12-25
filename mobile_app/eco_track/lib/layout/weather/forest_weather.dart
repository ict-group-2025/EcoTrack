import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

// Model đơn giản để chứa dữ liệu demo
class HourlyWeather {
  final DateTime time;
  final int temp;
  final int humidity; // Độ ẩm/Khả năng mưa
  final IconData icon;
  final Color iconColor;

  HourlyWeather({
    required this.time,
    required this.temp,
    required this.humidity,
    required this.icon,
    required this.iconColor,
  });
}

class HourlyForecastWidget extends StatelessWidget {
  const HourlyForecastWidget({super.key});

  @override
  Widget build(BuildContext context) {
    // TẠO DỮ LIỆU GIẢ LẬP (Bạn sẽ thay cái này bằng dữ liệu thật từ API)
    final List<HourlyWeather> hourlyData = List.generate(24, (index) {
      final time = DateTime.now().add(Duration(hours: index));
      // Giả lập đổi icon theo giờ
      bool isNight = time.hour < 6 || time.hour > 18;
      return HourlyWeather(
        time: time,
        temp: 20 + (index % 5), // Random nhiệt độ
        humidity: 10 + (index % 20), // Random độ ẩm
        icon: isNight ? Icons.nightlight_round : Icons.wb_sunny_rounded,
        iconColor: isNight ? Colors.amber[200]! : Colors.orange,
      );
    });

    return Container(
      height: 160, // Chiều cao tổng thể của khu vực này
      padding: const EdgeInsets.symmetric(vertical: 10),
      // Giả lập màu nền tối giống ảnh
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.05),
        borderRadius: BorderRadius.circular(20),
      ),
      child: ListView.builder(
        scrollDirection: Axis.horizontal, // Lướt ngang
        itemCount: hourlyData.length,
        itemBuilder: (context, index) {
          final data = hourlyData[index];
          return Container(
            width: 70, // Chiều rộng mỗi cột giờ
            margin: const EdgeInsets.symmetric(horizontal: 4),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                // 1. Giờ (12 AM, 1 AM...)
                Text(
                  DateFormat('h a').format(data.time),
                  style: const TextStyle(color: Colors.white70, fontSize: 12),
                ),

                // 2. Icon thời tiết
                Icon(data.icon, color: data.iconColor, size: 24),

                // 3. Nhiệt độ
                Text(
                  '${data.temp}°',
                  style: const TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                  ),
                ),

                // 4. Đường kẻ Timeline (Line & Dot)
                SizedBox(
                  height: 20,
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      // Đường kẻ ngang nối liền
                      Container(
                        height: 2,
                        width: double.infinity,
                        color: Colors.white24,
                      ),
                      // Chấm tròn trắng
                      Container(
                        width: 8,
                        height: 8,
                        decoration: const BoxDecoration(
                          color: Colors.white,
                          shape: BoxShape.circle,
                        ),
                      ),
                    ],
                  ),
                ),

                // 5. Độ ẩm / Mưa (Icon giọt nước + %)
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.water_drop,
                      size: 12,
                      color: Colors.white.withOpacity(0.7),
                    ),
                    const SizedBox(width: 2),
                    Text(
                      '${data.humidity}%',
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.7),
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
