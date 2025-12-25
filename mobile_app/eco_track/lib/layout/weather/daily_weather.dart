import 'package:flutter/material.dart';

class DailyWeather extends StatelessWidget {
  const DailyWeather({super.key});

  @override
  Widget build(BuildContext context) {
    // 1. DỮ LIỆU MẪU (HARDCODE DATA)
    // Sau này có API thì chỉ cần map dữ liệu vào list này là xong
    final List<Map<String, dynamic>> weeklyData = [
      {
        'day': 'Hôm nay',
        'rain_prob': 17,
        'icon_day': Icons.cloud,
        'icon_night': Icons.cloud_queue,
        'temp_max': 25,
        'temp_min': 20,
        'is_today': true, // Cờ đánh dấu để highlight
      },
      {
        'day': 'Thứ ba',
        'rain_prob': 36,
        'icon_day': Icons.grain, // Mưa
        'icon_night': Icons.nightlight_round,
        'temp_max': 27,
        'temp_min': 20,
        'is_today': false,
      },
      {
        'day': 'Thứ tư',
        'rain_prob': 37,
        'icon_day': Icons.thunderstorm,
        'icon_night': Icons.grain,
        'temp_max': 26,
        'temp_min': 17,
        'is_today': false,
      },
      {
        'day': 'Thứ năm',
        'rain_prob': 24,
        'icon_day': Icons.cloud,
        'icon_night': Icons.cloud,
        'temp_max': 19,
        'temp_min': 14,
        'is_today': false,
      },
      {
        'day': 'Thứ sáu',
        'rain_prob': 2,
        'icon_day': Icons.wb_sunny,
        'icon_night': Icons.nightlight_round,
        'temp_max': 21,
        'temp_min': 14,
        'is_today': false,
      },
      {
        'day': 'Thứ bảy',
        'rain_prob': 7,
        'icon_day': Icons.wb_sunny,
        'icon_night': Icons.cloud,
        'temp_max': 22,
        'temp_min': 15,
        'is_today': false,
      },
      {
        'day': 'Chủ nhật',
        'rain_prob': 0,
        'icon_day': Icons.wb_sunny,
        'icon_night': Icons.nightlight_round,
        'temp_max': 23,
        'temp_min': 15,
        'is_today': false,
      },
    ];

    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.05),
        borderRadius: BorderRadius.circular(30), // Bo góc mềm mại
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Tiêu đề nhỏ (Optional)
          const Text(
            "Dự báo 7 ngày",
            style: TextStyle(
              color: Colors.white54,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 10),

          // Render danh sách
          ...weeklyData.map((data) => _buildRow(data)),
        ],
      ),
    );
  }

  // 2. HÀM DỰNG GIAO DIỆN TỪNG DÒNG
  Widget _buildRow(Map<String, dynamic> data) {
    bool isToday = data['is_today'];

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 12),
      child: Row(
        children: [
          // Cột 1: Thứ (Chiếm 3 phần)
          Expanded(
            flex: 3,
            child: Text(
              data['day'],
              style: TextStyle(
                color: isToday
                    ? Colors.white
                    : Colors.white70, // Hôm nay sáng hơn
                fontWeight: isToday ? FontWeight.bold : FontWeight.w500,
                fontSize: 16,
              ),
            ),
          ),

          // Cột 2: Xác suất mưa (Chiếm 2 phần)
          Expanded(
            flex: 2,
            child: Row(
              children: [
                Icon(Icons.water_drop, size: 14, color: Colors.white),
                const SizedBox(width: 4),
                Text(
                  "${data['rain_prob']}%",
                  style: TextStyle(color: Colors.white, fontSize: 13),
                ),
              ],
            ),
          ),

          // Cột 3: Icon Thời tiết (Ngày & Đêm) (Chiếm 3 phần)
          Expanded(
            flex: 3,
            child: Row(
              mainAxisAlignment:
                  MainAxisAlignment.center, // Căn giữa khu vực này
              children: [
                Icon(data['icon_day'], color: Colors.amber, size: 22),
                const SizedBox(width: 10),
                Icon(data['icon_night'], color: Colors.white, size: 22),
              ],
            ),
          ),

          // Cột 4: Nhiệt độ Max / Min (Chiếm 3 phần)
          Expanded(
            flex: 3,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.end, // Đẩy về bên phải
              children: [
                Text(
                  "${data['temp_max']}°",
                  style: const TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                  ),
                ),
                const SizedBox(width: 10),
                Text(
                  "${data['temp_min']}°",
                  style: TextStyle(
                    color: Colors.grey[200]?.withOpacity(0.6),
                    fontWeight: FontWeight.w500,
                    fontSize: 16,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
