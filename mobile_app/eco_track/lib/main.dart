import 'package:eco_track/screens/air_quality_screen.dart';
import 'package:eco_track/screens/chat_screen.dart';
import 'package:eco_track/screens/devices_screen.dart';
import 'package:eco_track/screens/news.dart';
import 'package:eco_track/screens/weather_screen.dart';
import 'package:flutter/material.dart';

// Import màn hình News của bạn (đảm bảo đường dẫn đúng)
// Nếu file news.dart nằm cùng thư mục với main.dart thì chỉ cần: import 'news.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'EcoTrack App',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor: const Color(
          0xFF1E1E1E,
        ), // Màu nền tối toàn app
      ),
      home: const MainScreen(),
    );
  }
}

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  // SỬA Ở ĐÂY: Đặt thành 4 để mở App là thấy Tin Tức luôn
  int _currentIndex = 4;

  // Danh sách màn hình
  final List<Widget> _screens = [
    const WeatherScreen(), // Index 0
    const AirQualityScreen(), // Index 1
    const ChatScreen(), // Index 2
    const DevicesScreen(), // Index 3
    const News(), // Index 4 (Class News bạn đã tạo ở bước trước)
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Body hiển thị màn hình theo index hiện tại
      body: _screens[_currentIndex],

      // Bottom Navigation Bar tùy chỉnh của bạn
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: const Color(0xFF2D2D2D),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.3),
              blurRadius: 10,
              offset: const Offset(0, -2),
            ),
          ],
        ),
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _buildNavItem(Icons.wb_sunny_outlined, 'Thời tiết', 0),
                _buildNavItem(Icons.air_outlined, 'Không khí', 1),
                _buildNavItem(Icons.chat_bubble_outline, 'Chat', 2),
                _buildNavItem(Icons.devices_outlined, 'Thiết bị', 3),
                _buildNavItem(Icons.newspaper, 'Tin Tức', 4),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildNavItem(IconData icon, String label, int index) {
    final isSelected = _currentIndex == index;
    return GestureDetector(
      onTap: () => setState(() => _currentIndex = index),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: isSelected ? Colors.blue.withOpacity(0.2) : Colors.transparent,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              icon,
              color: isSelected ? Colors.blue : Colors.grey[400],
              size: 24,
            ),
            const SizedBox(height: 4),
            Text(
              label,
              style: TextStyle(
                color: isSelected ? Colors.blue : Colors.grey[400],
                fontSize: 10,
                fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
              ),
            ),
          ],
        ),
      ),
    );
  }
}




