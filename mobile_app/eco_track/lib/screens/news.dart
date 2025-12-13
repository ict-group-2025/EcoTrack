import 'package:flutter/material.dart';

class News extends StatelessWidget {
  const News({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1E1E1E), // Nền tối
      appBar: AppBar(
        backgroundColor: const Color(0xFF2D2D2D),
        title: const Text(
          'Tin Tức Môi Trường',
          style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
        ),
        centerTitle: true,
        elevation: 0,
        actions: [
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.search, color: Colors.white),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 1. Tiêu đề mục Tin nổi bật
            _buildSectionTitle("Tin Nổi Bật"),
            const SizedBox(height: 10),

            // 2. Card Tin to nhất (Featured News) - Không dùng ảnh thật
            _buildFeaturedCard(),

            const SizedBox(height: 25),

            // 3. Danh mục (Categories) - Scroll ngang
            _buildCategoryList(),

            const SizedBox(height: 25),

            // 4. Tiêu đề mục Tin mới
            _buildSectionTitle("Vừa Cập Nhật"),
            const SizedBox(height: 10),

            // 5. Danh sách tin tức dạng liệt kê (List News)
            _buildNewsItem(
              Colors.blueGrey,
              "Dự báo thời tiết: Mưa lớn diện rộng tại Hà Nội",
              "Thời tiết • 30 phút trước",
            ),
            _buildNewsItem(
              Colors.teal,
              "Chất lượng không khí hôm nay cải thiện đáng kể",
              "Môi trường • 1 giờ trước",
            ),
            _buildNewsItem(
              Colors.orange,
              "Cảnh báo chỉ số UV cao vào buổi trưa",
              "Sức khỏe • 2 giờ trước",
            ),
            _buildNewsItem(
              Colors.purple,
              "Công nghệ mới giúp lọc bụi mịn trong nhà",
              "Công nghệ • 5 giờ trước",
            ),
            _buildNewsItem(
              Colors.redAccent,
              "Cháy rừng gia tăng do biến đổi khí hậu",
              "Cảnh báo • 1 ngày trước",
            ),
          ],
        ),
      ),
    );
  }

  // --- CÁC WIDGET CON ĐỂ VẼ GIAO DIỆN ---

  // Tiêu đề nhỏ của từng mục
  Widget _buildSectionTitle(String title) {
    return Text(
      title,
      style: const TextStyle(
        color: Colors.white,
        fontSize: 18,
        fontWeight: FontWeight.bold,
    
      ),
    );
  }

  // Widget vẽ Card tin nổi bật (Placeholder thay ảnh)
  Widget _buildFeaturedCard() {
    return Container(
      height: 220,
      width: double.infinity,
      decoration: BoxDecoration(
        color: const Color(0xFF2D2D2D),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Phần giả lập Ảnh bìa (Chiếm 60% chiều cao)
          Expanded(
            flex: 6,
            child: Container(
              decoration: const BoxDecoration(
                color: Colors.grey, // Màu xám thay cho ảnh
                borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(16),
                  topRight: Radius.circular(16),
                ),
              ),
              child: Center(
                child: Icon(
                  Icons.image,
                  size: 50,
                  color: Colors.white.withOpacity(0.5),
                ),
              ),
            ),
          ),
          // Phần nội dung chữ (Chiếm 40%)
          Expanded(
            flex: 4,
            child: Padding(
              padding: const EdgeInsets.all(12.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 8,
                      vertical: 4,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.red,
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: const Text(
                      "NÓNG",
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(height: 5),
                  const Text(
                    "Bão số 1 đang đổ bộ: Những điều cần lưu ý ngay lập tức",
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  // Widget vẽ danh mục nằm ngang
  Widget _buildCategoryList() {
    final categories = [
      "Tất cả",
      "Thời tiết",
      "Sức khỏe",
      "Môi trường",
      "Đời sống",
    ];
    return SizedBox(
      height: 35,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: categories.length,
        itemBuilder: (context, index) {
          bool isSelected = index == 0; // Chọn cái đầu tiên
          return Container(
            margin: const EdgeInsets.only(right: 10),
            padding: const EdgeInsets.symmetric(horizontal: 20),
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: isSelected ? Colors.blue : const Color(0xFF2D2D2D),
              borderRadius: BorderRadius.circular(20),
              border: isSelected
                  ? null
                  : Border.all(color: Colors.grey.withOpacity(0.3)),
            ),
            child: Text(
              categories[index],
              style: TextStyle(
                color: isSelected ? Colors.white : Colors.grey,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
              ),
            ),
          );
        },
      ),
    );
  }

  // Widget vẽ từng dòng tin tức nhỏ
  Widget _buildNewsItem(Color placeholderColor, String title, String subInfo) {
    return Container(
      margin: const EdgeInsets.only(bottom: 15),
      padding: const EdgeInsets.all(10),
      decoration: BoxDecoration(
        color: const Color(0xFF2D2D2D),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          // 1. Ảnh thumbnail giả lập
          Container(
            width: 80,
            height: 80,
            decoration: BoxDecoration(
              color: placeholderColor.withOpacity(0.2), // Màu nền nhẹ
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(
              Icons.article,
              color: placeholderColor,
            ), // Icon thay ảnh
          ),
          const SizedBox(width: 15),

          // 2. Nội dung chữ bên phải
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                    height: 1.3,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    const Icon(Icons.access_time, size: 12, color: Colors.grey),
                    const SizedBox(width: 5),
                    Text(
                      subInfo,
                      style: const TextStyle(color: Colors.grey, fontSize: 12),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
