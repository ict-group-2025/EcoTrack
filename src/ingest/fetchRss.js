/*
  fetchRss.js (bản tối ưu nhẹ)
  Mục tiêu:
  - GIỮ NGUYÊN chức năng cũ + phần bổ sung detail/image
  - TỐI ƯU: timeout, User-Agent, giới hạn số bài, lọc bài lỗi, lọc ngày bất thường
*/

const Parser = require("rss-parser");

// Cấu hình tối ưu: timeout + User-Agent để nhiều nguồn không chặn
const parser = new Parser({
  requestOptions: {
    timeout: 8_000, // 8s: tránh treo khi nguồn chậm
    headers: {
      "User-Agent": "news-service/1.0 (+rss-ingest)"
    }
  }
});

// Giới hạn số bài/nguồn để tránh feed quá lớn (có thể chỉnh qua .env)
const MAX_ITEMS = Number(process.env.RSS_MAX_ITEMS || 50);

/* ===================== Helpers ===================== */

/* Lấy URL ảnh từ các trường media phổ biến của RSS */
function pickImageFromMedia(item) {
  // rss-parser map 'media:content' / 'media:thumbnail' -> thuộc tính cùng tên
  const media = item["media:content"] || item["media:thumbnail"];
  if (media && (media.url || media.$?.url)) {
    return media.url || media.$.url;
  }
  return null;
}

/* Lấy URL ảnh từ enclosure nếu enclosure là ảnh */
function pickImageFromEnclosure(item) {
  const enc = item.enclosure;
  if (!enc) return null;
  if (enc.url && (!enc.type || enc.type.startsWith("image/"))) {
    return enc.url;
  }
  return null;
}

/* Regex đơn giản để bắt <img src="..."> đầu tiên trong HTML */
function pickImageFromHtml(html) {
  if (!html) return null;
  const imgMatch = html.match(/<img[^>]+src=["']([^"']+)["']/i);
  return imgMatch ? imgMatch[1] : null;
}

/* Quyết định content + contentType từ item */
function extractContentAndType(item) {
  // Nhiều RSS dùng 'content:encoded' cho full HTML
  const encoded = item["content:encoded"];
  if (encoded && typeof encoded === "string" && encoded.trim()) {
    return { content: encoded, contentType: "html" };
  }

  // Nếu không có content:encoded, xem 'content' (có thể là HTML hoặc text)
  const raw = item.content;
  if (raw && typeof raw === "string" && raw.trim()) {
    const looksHtml = /<\/?[a-z][\s\S]*>/i.test(raw);
    return { content: raw, contentType: looksHtml ? "html" : "text" };
  }

  // Không có chi tiết → để rỗng (FE vẫn có summary để hiển thị)
  return { content: "", contentType: "text" };
}

/* Lấy author nếu nguồn có cung cấp */
function pickAuthor(item) {
  // rss-parser thường map 'creator' hoặc 'author'
  return item.creator || item.author || null;
}

/* Chuẩn hoá/kiểm tra thời điểm đăng bài (lọc những bản ghi lệch thời gian quá đáng) */
function normalizePublishedAt(item) {
  const raw = item.isoDate || item.pubDate || null;
  if (!raw) return null;
  const ts = Date.parse(raw);
  if (Number.isNaN(ts)) return null;

  const d = new Date(ts);
  const now = Date.now();

  // Bỏ những bài "tương lai" quá 24h (nguồn sai múi giờ)
  if (ts > now + 24 * 60 * 60 * 1000) return null;

  // Tuỳ chọn: bỏ những bài quá cũ (> 2 năm) để tiết kiệm (giữ đơn giản, không bắt buộc)
  // if (ts < now - 730 * 24 * 60 * 60 * 1000) return null;

  return d.toISOString(); // normalize.js sẽ parse tiếp về kiểu Date nếu cần
}

/* ==================================================== */
/*
  Hàm này dùng để lấy danh sách bài viết từ một nguồn RSS cụ thể.
  Đầu vào: { url, name, category, ... } (định nghĩa trong sources.yml)
  Đầu ra: danh sách bài viết THÔ nhưng đã có thêm imageUrl/content/author/contentType.
  (normalize.js sẽ tiếp tục chuẩn hoá cho phù hợp DB)
*/
async function fetchRss(source) {
  try {
    const feed = await parser.parseURL(source.url);
    if (!feed.items || feed.items.length === 0) {
      return [];
    }

    // 1) Giới hạn số bài; 2) Bỏ bài thiếu link (không lưu được); 3) Lọc ngày bất thường
    const items = feed.items
      .slice(0, MAX_ITEMS)
      .filter(it => !!it.link);

    return items
      .map(item => {
        // Ảnh đại diện: theo thứ tự ưu tiên
        const imageByMedia = pickImageFromMedia(item);
        const imageByEnclosure = pickImageFromEnclosure(item);
        const imageByDesc = pickImageFromHtml(
          item.content || item.contentSnippet || item.summary || item.description
        );
        const imageUrl = imageByMedia || imageByEnclosure || imageByDesc || null;

        // Content + Type
        const { content, contentType } = extractContentAndType(item);

        // Author (nếu có)
        const author = pickAuthor(item);

        // Thời gian đăng
        const publishedAt = normalizePublishedAt(item);

        return {
          // Trường CŨ (giữ pipeline)
          title: item.title || "",
          description: item.contentSnippet || item.content || "", // tóm tắt (normalize sẽ xử)
          link: item.link,
          publishedAt, // có thể null nếu dữ liệu không hợp lệ
          category: source.category,
          source: source.name,

          // Trường MỚI cho schema (detail + image)
          imageUrl,
          content,
          contentType,
          author
        };
      })
      // Bỏ bài không có publishedAt hợp lệ nếu bạn muốn dữ liệu sạch hơn:
      // (để mềm dẻo cho đồ án, ta vẫn giữ lại; FE/normalize có thể xử sau)
      // .filter(a => a.publishedAt !== null)
      ;
  } catch (error) {
    // Log gọn tên nguồn + URL giúp debug
    console.error(
      `Failed to fetch RSS from [${source.name}] ${source.url}:`,
      error.message
    );
    return [];
  }
}

module.exports = fetchRss;
