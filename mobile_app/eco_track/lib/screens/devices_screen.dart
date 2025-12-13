import 'package:flutter/material.dart';

class DevicesScreen extends StatelessWidget {
  const DevicesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1E1E1E),
      appBar: AppBar(
        backgroundColor: const Color(0xFF2D2D2D),
        title: const Text('Thiết bị', style: TextStyle(color: Colors.white)),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.add, color: Colors.white),
            onPressed: () {},
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          _buildDeviceCard(
            'Cảm biến nhiệt độ',
            'Phòng khách',
            '23°C',
            Icons.thermostat,
            Colors.orange,
            true,
          ),
          const SizedBox(height: 15),
          _buildDeviceCard(
            'Cảm biến độ ẩm',
            'Phòng ngủ',
            '65%',
            Icons.water_drop,
            Colors.blue,
            true,
          ),
          const SizedBox(height: 15),
          _buildDeviceCard(
            'Đèn thông minh',
            'Bếp',
            'Tắt',
            Icons.lightbulb,
            Colors.yellow,
            false,
          ),
          const SizedBox(height: 15),
          _buildDeviceCard(
            'Quạt thông minh',
            'Phòng làm việc',
            'Bật',
            Icons.mode_fan_off,
            Colors.teal,
            true,
          ),
        ],
      ),
    );
  }

  Widget _buildDeviceCard(
    String name,
    String location,
    String status,
    IconData icon,
    Color color,
    bool isActive,
  ) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: const Color(0xFF2D2D2D),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
          color: isActive
              ? color.withOpacity(0.5)
              : Colors.grey.withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Row(
        children: [
          Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(
              color: color.withOpacity(0.2),
              borderRadius: BorderRadius.circular(15),
            ),
            child: Icon(icon, color: color, size: 30),
          ),
          const SizedBox(width: 15),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  name,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 5),
                Text(
                  location,
                  style: TextStyle(color: Colors.grey[400], fontSize: 13),
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                status,
                style: TextStyle(
                  color: isActive ? color : Colors.grey[500],
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 5),
              Container(
                width: 40,
                height: 20,
                decoration: BoxDecoration(
                  color: isActive
                      ? color.withOpacity(0.3)
                      : Colors.grey.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Stack(
                  children: [
                    AnimatedAlign(
                      duration: const Duration(milliseconds: 200),
                      alignment: isActive
                          ? Alignment.centerRight
                          : Alignment.centerLeft,
                      child: Container(
                        width: 16,
                        height: 16,
                        margin: const EdgeInsets.all(2),
                        decoration: BoxDecoration(
                          color: isActive ? color : Colors.grey[500],
                          shape: BoxShape.circle,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
