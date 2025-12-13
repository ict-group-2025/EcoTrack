import 'package:flutter/material.dart';

class WeatherScreen extends StatelessWidget {
  const WeatherScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [Color(0xFF5B6C7D), Color(0xFF2D3E50)],
        ),
      ),
      child: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildHeader(),
              const SizedBox(height: 30),
              _buildCurrentWeather(),
              const SizedBox(height: 20),
              _buildHourlyForecast(),
              const SizedBox(height: 20),
              _buildWeeklyForecast(),
              const SizedBox(height: 20),
              _buildWeatherDetails(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Icon(Icons.menu, color: Colors.white),
        Row(
          children: const [
            Icon(Icons.location_on, color: Colors.white, size: 20),
            SizedBox(width: 5),
            Text(
              'Trương Định',
              style: TextStyle(color: Colors.white, fontSize: 16),
            ),
          ],
        ),
        const Icon(Icons.more_vert, color: Colors.white),
      ],
    );
  }

  Widget _buildCurrentWeather() {
    return Center(
      child: Column(
        children: const [
          Icon(Icons.cloud, color: Colors.white, size: 100),
          SizedBox(height: 10),
          Text(
            '21°',
            style: TextStyle(
              color: Colors.white,
              fontSize: 80,
              fontWeight: FontWeight.w200,
            ),
          ),
          Text(
            'Sương mù',
            style: TextStyle(color: Colors.white70, fontSize: 20),
          ),
          SizedBox(height: 5),
          Text(
            '23° / 20° • Cảm giác như 21°',
            style: TextStyle(color: Colors.white60, fontSize: 14),
          ),
        ],
      ),
    );
  }

  Widget _buildHourlyForecast() {
    return Container(
      padding: const EdgeInsets.all(15),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.white.withOpacity(0.2)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Mưa phùn từ sáng sớm. Thấp 20 độ C.',
            style: TextStyle(color: Colors.white70, fontSize: 13),
          ),
          const SizedBox(height: 15),
          SizedBox(
            height: 100,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemCount: 7,
              itemBuilder: (context, index) {
                final hours = [
                  '11 PM',
                  '12 AM',
                  '1 AM',
                  '2 AM',
                  '3 AM',
                  '4 AM',
                  '5 AM',
                ];
                final temps = ['21°', '21°', '21°', '21°', '21°', '21°', '21°'];
                return Container(
                  width: 60,
                  margin: const EdgeInsets.only(right: 10),
                  child: Column(
                    children: [
                      Text(
                        hours[index],
                        style: const TextStyle(
                          color: Colors.white60,
                          fontSize: 12,
                        ),
                      ),
                      const SizedBox(height: 10),
                      const Icon(Icons.cloud, color: Colors.white70, size: 30),
                      const SizedBox(height: 10),
                      Text(
                        temps[index],
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 14,
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildWeeklyForecast() {
    final days = ['Hôm nay', 'Thứ sáu', 'Thứ bảy', 'Chủ nhật', 'Thứ hai'];
    final highs = ['23°', '24°', '23°', '19°', '22°'];
    final lows = ['20°', '21°', '16°', '15°', '17°'];

    return Container(
      padding: const EdgeInsets.all(15),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.white.withOpacity(0.2)),
      ),
      child: Column(
        children: List.generate(days.length, (index) {
          return Padding(
            padding: const EdgeInsets.symmetric(vertical: 8),
            child: Row(
              children: [
                SizedBox(
                  width: 80,
                  child: Text(
                    days[index],
                    style: const TextStyle(color: Colors.white, fontSize: 14),
                  ),
                ),
                const Icon(Icons.cloud, color: Colors.white70, size: 24),
                const Spacer(),
                Text(
                  highs[index],
                  style: const TextStyle(color: Colors.white, fontSize: 14),
                ),
                const SizedBox(width: 15),
                Text(
                  lows[index],
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.5),
                    fontSize: 14,
                  ),
                ),
              ],
            ),
          );
        }),
      ),
    );
  }

  Widget _buildWeatherDetails() {
    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisSpacing: 15,
      mainAxisSpacing: 15,
      childAspectRatio: 1.3,
      children: [
        _buildDetailCard('Chỉ số UV', 'Thấp', Icons.wb_sunny),
        _buildDetailCard('Độ ẩm', '85%', Icons.water_drop),
        _buildDetailCard('Gió', '5 km/h', Icons.air),
        _buildDetailCard('Hoàng hôn', '17:30', Icons.wb_twilight),
      ],
    );
  }

  Widget _buildDetailCard(String title, String value, IconData icon) {
    return Container(
      padding: const EdgeInsets.all(15),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.white.withOpacity(0.2)),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, color: Colors.white70, size: 35),
          const SizedBox(height: 10),
          Text(
            title,
            style: const TextStyle(color: Colors.white70, fontSize: 12),
          ),
          const SizedBox(height: 5),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }
}
