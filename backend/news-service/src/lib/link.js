/*
  File này dùng để XỬ LÝ LINK bài viết.

  Mục đích:
  - Chuẩn hóa link (loại bỏ tracking, khoảng trắng)
  - Giúp kiểm tra trùng lặp chính xác hơn
  - Đảm bảo mỗi bài viết có 1 link nhất quán
*/

const { URL } = require("url");

/*
  Hàm này nhận vào link gốc của bài viết
  và trả về link đã được làm sạch
*/
function normalizeLink(rawLink) {
  try {
    // Nếu link không hợp lệ thì trả về null
    if (!rawLink) return null;

    const url = new URL(rawLink);

    // Loại bỏ các tham số tracking (utm_*)
    url.searchParams.forEach((value, key) => {
      if (key.startsWith("utm_")) {
        url.searchParams.delete(key);
      }
    });

    // Trả về link đã được chuẩn hóa
    return url.toString();
  } catch (error) {
    // Nếu link lỗi thì giữ nguyên link cũ
    return rawLink;
  }
}

module.exports = {
  normalizeLink
};
