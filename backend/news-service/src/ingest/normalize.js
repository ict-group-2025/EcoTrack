/*
  CHUẨN HÓA DỮ LIỆU BÀI VIẾT (normalize.js)

  Mục tiêu:
  - GIỮ ĐẦY ĐỦ các trường đang dùng: title, summary, link, published_at, category, source
  - BỔ SUNG & TỐI ƯU cho trường MỚI: image_url, content, content_type, author
  - Giới hạn mềm content để tránh bài quá nặng (KHÔNG làm mất bài)
  - An toàn khi render HTML ở frontend
*/

/* ================== CẤU HÌNH MỀM ================== */
// Độ dài tối đa summary (ký tự)
const SUMMARY_MAXLEN = Number(process.env.SUMMARY_MAXLEN || 280);

// Giới hạn mềm content (byte). 0 = không giới hạn
const MAX_CONTENT_BYTES = Number(process.env.MAX_CONTENT_BYTES || 0);

/* ================== HELPER ================== */

/* Bỏ thẻ HTML để lấy text gọn cho summary */
function stripHtmlToText(html = "") {
  if (!html) return "";
  const noScript = html
    .replace(/<script[\s\S]*?<\/script>/gi, "")
    .replace(/<style[\s\S]*?<\/style>/gi, "");
  const text = noScript.replace(/<\/?[^>]+>/g, " ");
  return text.replace(/\s+/g, " ").trim();
}

/* Cắt gọn thông minh, không bẻ từ */
function smartTrim(text = "", maxLen = 280) {
  if (!text || text.length <= maxLen) return text || "";
  const cut = text.slice(0, maxLen + 1);
  const lastSpace = cut.lastIndexOf(" ");
  return (lastSpace > 0 ? cut.slice(0, lastSpace) : text.slice(0, maxLen)).trim() + "…";
}

/* Parse thời gian an toàn */
function toDateSafe(input) {
  if (!input) return null;
  const ts = typeof input === "string" ? Date.parse(input) : +input;
  if (Number.isNaN(ts)) return null;
  return new Date(ts);
}

/* Chuẩn hóa category */
function normalizeCategory(cat) {
  if (!cat) return null;
  const v = String(cat).toLowerCase().trim();
  return v === "weather" || v === "air" || v === "health" ? v : null;
}

/* Chuẩn hóa contentType */
function normalizeContentType(t) {
  const v = String(t || "").toLowerCase().trim();
  return v === "html" || v === "text" ? v : "text";
}

/* Làm sạch HTML nguy hiểm nhưng KHÔNG phá cấu trúc */
function sanitizeHtmlLight(html = "") {
  if (!html) return "";
  return html
    .replace(/<script[\s\S]*?<\/script>/gi, "")
    .replace(/<style[\s\S]*?<\/style>/gi, "")
    .replace(/\son\w+="[^"]*"/gi, ""); // bỏ onClick, onLoad...
}

/* Giới hạn mềm content theo byte */
function limitContentSoft(content = "", maxBytes = 0) {
  if (!maxBytes) return content;
  const size = Buffer.byteLength(content, "utf8");
  if (size <= maxBytes) return content;

  // Cắt theo byte, giữ phần đầu
  const buf = Buffer.from(content, "utf8");
  const sliced = buf.slice(0, maxBytes).toString("utf8");

  return sliced + "\n\n[Đã rút gọn nội dung – xem đầy đủ tại nguồn]";
}

/* ================== MAIN ================== */

function normalizeArticles(articles) {
  return articles.map((article) => {
    try {
      // 1) Title
      const title = (article.title || "").toString().trim();

      // 2) Summary
      const rawDesc = article.description ?? article.summary ?? "";
      const summary = smartTrim(stripHtmlToText(rawDesc), SUMMARY_MAXLEN);

      // 3) Link
      const link = article.link;

      // 4) Published time
      const published_at = toDateSafe(article.publishedAt);

      // 5) Category & source
      const category = normalizeCategory(article.category);
      const source = (article.source || "").toString().trim() || null;

      // ===== DETAIL + IMAGE =====

      // 6) Image
      const image_url = article.imageUrl ? String(article.imageUrl).trim() : null;

      // 7) Content
      const rawContent = (article.content || "").toString().trim();
      const contentSanitized =
        normalizeContentType(article.contentType) === "html"
          ? sanitizeHtmlLight(rawContent)
          : rawContent;

      const content = limitContentSoft(contentSanitized, MAX_CONTENT_BYTES);

      // 8) Author
      const author = article.author ? String(article.author).trim() : null;

      // 9) Content type
      const content_type = normalizeContentType(article.contentType);

      return {
        title,
        summary,
        link,
        published_at,
        category,
        source,
        image_url,
        content,
        author,
        content_type
      };
    } catch (err) {
      // Nếu 1 bài lỗi → bỏ bài đó, không làm sập pipeline
      console.error("Normalize failed for article:", err.message);
      return null;
    }
  }).filter(Boolean);
}

module.exports = normalizeArticles;
