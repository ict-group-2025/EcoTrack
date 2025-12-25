import 'package:eco_track/providers/weather_provider.dart';
import 'package:eco_track/screens/air_quality_screen.dart';
import 'package:eco_track/screens/chat_screen.dart';
import 'package:eco_track/screens/devices_screen.dart';
import 'package:eco_track/screens/news.dart';
import 'package:eco_track/screens/weather_screen.dart';
import 'package:eco_track/services/gps.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:provider/provider.dart';
import '../providers/location_provider.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    _loadLocation();
  }

  /// Lấy vị trí và fetch dữ liệu thời tiết
  Future<void> _loadLocation() async {
    try {
      // 1. Kiểm tra và yêu cầu quyền truy cập vị trí
      bool granted = await requestLocationPermission();
      if (!granted) {
        _showLocationPermissionDeniedDialog();
        return;
      }

      // 2. Lấy vị trí hiện tại
      Position pos = await getCurrentLocation();

      if (!mounted) return;

      // 3. Lưu vị trí vào LocationProvider
      final locationProvider = Provider.of<LocationProvider>(
        context,
        listen: false,
      );
      locationProvider.setPosition(pos);

      // 4. Fetch dữ liệu thời tiết dựa trên vị trí
      final weatherProvider = Provider.of<WeatherProvider>(
        context,
        listen: false,
      );
      await weatherProvider.fetchWeatherByCoordinates(pos.latitude, pos.longitude);
    } catch (e) {
      debugPrint('Error loading location: $e');
      if (mounted) {
        _showErrorDialog('Không thể lấy vị trí. Vui lòng thử lại.');
      }
    }
  }

  /// Hiển thị dialog khi quyền truy cập vị trí bị từ chối
  void _showLocationPermissionDeniedDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Quyền truy cập vị trí'),
        content: const Text(
          'Ứng dụng cần quyền truy cập vị trí để hiển thị thông tin thời tiết. '
          'Vui lòng cấp quyền trong cài đặt.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Đóng'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              Geolocator.openLocationSettings();
            },
            child: const Text('Mở cài đặt'),
          ),
        ],
      ),
    );
  }

  /// Hiển thị dialog lỗi
  void _showErrorDialog(String message) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Lỗi'),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Đóng'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              _loadLocation(); // Thử lại
            },
            child: const Text('Thử lại'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final screens = [
      const WeatherScreen(),
      const AirQualityScreen(),
      const ChatScreen(),
      const DevicesScreen(),
      const News(),
    ];

    return Scaffold(
      body: screens[_currentIndex],
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
                _buildNavItem(Icons.wb_sunny_outlined, 'Weather', 0),
                _buildNavItem(Icons.air_outlined, 'AQI', 1),
                _buildNavItem(Icons.chat_bubble_outline, 'Chat', 2),
                _buildNavItem(Icons.devices_outlined, 'Device', 3),
                _buildNavItem(Icons.newspaper, 'News', 4),
              ],
            ),
          ),
        ),
      ),
    );
  }

  /// Build navigation item
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
