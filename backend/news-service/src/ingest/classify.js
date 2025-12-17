/*
  File này dùng để PHÂN LOẠI và GÁN ĐỊA PHƯƠNG cho tin tức.
  Mục tiêu:
  - Xác định tin liên quan đến khu vực nào
  - Giữ logic đơn giản, đủ dùng cho đồ án
  - Không dùng AI, chỉ dựa trên từ khóa
*/

// Danh sách địa phương mẫu để dò trong nội dung tin
const LOCATIONS = [
  "Hà Nội",
  "TP.HCM",
  "Hồ Chí Minh",
  "Đà Nẵng",
  "Cần Thơ",
  "Hải Phòng"
];

function classifyArticles(articles) {
  return articles.map(article => {
    let location = null;

    // Ghép tiêu đề + nội dung để tìm địa phương
    const content = `${article.title} ${article.summary}`.toLowerCase();

    // Duyệt qua danh sách địa phương
    for (const loc of LOCATIONS) {
      if (content.includes(loc.toLowerCase())) {
        location = loc;
        break;
      }
    }

    return {
      ...article,

      // Địa phương liên quan đến tin
      // Nếu không tìm thấy thì để null
      location
    };
  });
}

module.exports = classifyArticles;
