/*
  FILE TRUNG TÂM CỦA PHƯƠNG ÁN 2 (DATABASE + RSS)

  1) Đọc danh sách nguồn RSS
  2) Lấy dữ liệu từ RSS
  3) Chuẩn hóa dữ liệu
  4) Phân loại + gán địa phương
  5) Chuẩn hóa link (chống trùng lặp)
  6) Lưu vào DATABASE (database là trung tâm)
  7) THÔNG BÁO (demo): log người nhận

  - Sau khi CÓ bài mới:
    + Chỉ giữ TOP 10 bài mới nhất (trộn chủ đề)
    + Các bài cũ hơn → đưa vào kho (archived_at)
  - KHÔNG có bài mới → KHÔNG đụng gì
*/

require("dotenv").config();
const fs = require("fs");
const path = require("path");
const yaml = require("js-yaml");

const fetchRss = require("./fetchRss");
const normalizeArticles = require("./normalize");
const classifyArticles = require("./classify");
const { normalizeLink } = require("../lib/link");
const db = require("../lib/db");

// ===============================
// Đọc danh sách nguồn RSS
// ===============================
const sourcesPath = path.join(__dirname, "sources.yml");
const sourcesConfig = yaml.load(fs.readFileSync(sourcesPath, "utf8"));

// ===============================
// THÔNG BÁO (demo – giữ nguyên)
// ===============================
async function notifyDemoRecipients(article, newsId) {
  try {
    const q = `
      SELECT s.user_id, d.device_token
      FROM subscriptions s
      LEFT JOIN devices d ON d.user_id = s.user_id
      WHERE (s.category IS NULL OR s.category = $1)
        AND (s.location IS NULL OR s.location = $2)
    `;
    const params = [article.category, article.location || null];
    const { rows } = await db.query(q, params);

    if (!rows || rows.length === 0) return;

    console.log(
      `[Notify] ${rows.length} recipient(s) for news #${newsId}: "${article.title}"`
    );
  } catch (e) {
    console.error("[Notify] Failed:", e.message);
  }
}

// ===============================
// HÀM INGEST CHÍNH
// ===============================
async function runIngest() {
  console.log("Start ingesting news...");

  let insertedCount = 0; // đếm số bài MỚI thật sự được thêm

  for (const source of sourcesConfig.sources) {
    console.log(`[Ingest] Source: ${source.name}`);

    try {
      await db.query("BEGIN");

      // 1) Lấy RSS
      const rawArticles = await fetchRss(source);
      if (!rawArticles || rawArticles.length === 0) {
        await db.query("COMMIT");
        continue;
      }

      // 2) Normalize
      const normalized = normalizeArticles(rawArticles);

      // 3) Classify
      const classified = classifyArticles(normalized);

      // 4) Insert từng bài
      for (const article of classified) {
        try {
          const cleanLink = normalizeLink(article.link);
          if (!cleanLink) continue;
          if (!article.title || !article.title.trim()) continue;

          const insertSQL = `
            INSERT INTO news
              (title, summary, link, published_at, category, source, location,
               image_url, content, author, content_type)
            VALUES
              ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11)
            ON CONFLICT (link) DO NOTHING
            RETURNING id
          `;

          const params = [
            article.title,
            article.summary,
            cleanLink,
            article.published_at,
            article.category,
            article.source,
            article.location || null,
            article.image_url || null,
            article.content || null,
            article.author || null,
            article.content_type || "text"
          ];

          const { rows } = await db.query(insertSQL, params);

          // Nếu INSERT thật sự thành công
          if (rows && rows.length > 0) {
            insertedCount++;
            const newsId = rows[0].id;
            await notifyDemoRecipients(article, newsId);
          }
        } catch (err) {
          console.error(`[Ingest][${source.name}] Article error:`, err.message);
        }
      }

      await db.query("COMMIT");
    } catch (err) {
      console.error(`[Ingest][${source.name}] Transaction failed:`, err.message);
      try {
        await db.query("ROLLBACK");
      } catch {}
    }
  }

  // ===============================
  // ĐƯA BÀI CŨ VÀO KHO (CHỈ KHI CÓ BÀI MỚI)
  // ===============================
  if (insertedCount > 0) {
    console.log(`[Archive] ${insertedCount} new article(s). Rebalancing top 10...`);

    try {
      await db.query(`
        WITH excess AS (
          SELECT id
          FROM news
          WHERE archived_at IS NULL
          ORDER BY published_at DESC NULLS LAST, created_at DESC, id DESC
          OFFSET 10
        )
        UPDATE news
        SET archived_at = NOW()
        WHERE id IN (SELECT id FROM excess)
      `);
    } catch (err) {
      console.error("[Archive] Failed:", err.message);
    }
  } else {
    console.log("[Archive] No new articles. Skip rebalancing.");
  }

  console.log("Ingest completed.");
  process.exit(0);
}

// ===============================
// CHẠY INGEST
// ===============================
runIngest();
