/*
  News API – Phương án 2: DB + RSS
  Endpoints: /news, /news/:id, /search, /read, /subscribe, /devices, /notify-preview
  Scheduler: auto-ingest theo INGEST_INTERVAL_MINUTES (mặc định 5)
*/

require("dotenv").config();
const express = require("express");
const { spawn } = require("child_process");
const db = require("../lib/db");

const app = express();
const PORT = process.env.PORT || 3000;

/* -------- Middleware -------- */
// JSON (kể cả application/*+json) + giữ rawBody để debug khi cần
app.use(express.json({
  type: ["application/json", "application/*+json"],
  verify: (req, _res, buf) => { req.rawBody = buf?.toString() || ""; }
}));
// Hỗ trợ x-www-form-urlencoded (lỡ chọn nhầm tab trong Postman)
app.use(express.urlencoded({ extended: false }));
// Bắt lỗi JSON parse -> 400
app.use((err, _req, res, next) => {
  if (err && err.type === "entity.parse.failed") {
    console.error("[JSON Parse Error]", err.message);
    return res.status(400).json({ message: "Invalid JSON body" });
  }
  next(err);
});

/* -------- Helpers -------- */
function parsePaging(qPage, qLimit, defLimit = 10, maxLimit = 50) {
  const page = Math.max(parseInt(qPage || "1", 10) || 1, 1);
  const limitRaw = parseInt(qLimit || `${defLimit}`, 10) || defLimit;
  const limit = Math.min(Math.max(limitRaw, 1), maxLimit);
  const offset = (page - 1) * limit;
  return { page, limit, offset };
}
function getPayload(req) {
  if (req && typeof req.body === "string" && req.body.trim().startsWith("{")) {
    try { return JSON.parse(req.body); } catch { return {}; }
  }
  return req?.body || {};
}

/* -------- Routes -------- */
// GET /news
app.get("/news", async (req, res) => {
  try {
    const { category, location, page, limit } = req.query;
    const { limit: L, offset: O } = parsePaging(page, limit, 10, 50);

    let sql = `
      SELECT id, title, summary, image_url, published_at, category, location, source
      FROM news
      WHERE archived_at IS NULL
    `;
    const p = [];
    if (category) { p.push(category); sql += ` AND category = $${p.length}`; }
    if (location) { p.push(location); sql += ` AND location = $${p.length}`; }
    p.push(L, O);
    sql += `
      ORDER BY published_at DESC NULLS LAST, created_at DESC, id DESC
      LIMIT $${p.length - 1} OFFSET $${p.length}
    `;
    const { rows } = await db.query(sql, p);
    res.set("Cache-Control", "public, max-age=30");
    res.json(rows);
  } catch (e) {
    console.error("[/news]", e);
    res.status(500).json({ message: "Failed to load news" });
  }
});

// GET /news/:id
app.get("/news/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const { rows } = await db.query(
      `
      SELECT id, title, image_url, content, author, content_type,
             source, published_at, category, location, link
      FROM news
      WHERE id = $1
      `,
      [id]
    );
    if (!rows.length) return res.status(404).json({ message: "News not found" });
    res.json(rows[0]);
  } catch (e) {
    console.error("[/news/:id]", e);
    res.status(500).json({ message: "Failed to load news detail" });
  }
});

// GET /search
app.get("/search", async (req, res) => {
  try {
    const { q, page, limit } = req.query;
    if (!q || typeof q !== "string" || q.trim().length === 0 || q.length > 100)
      return res.status(400).json({ message: "Invalid keyword" });

    const { limit: L, offset: O } = parsePaging(page, limit, 10, 50);
    const { rows } = await db.query(
      `
      SELECT id, title, summary, image_url, published_at, category, location, source
      FROM news
      WHERE archived_at IS NULL
        AND (title ILIKE $1 OR summary ILIKE $1)
      ORDER BY published_at DESC NULLS LAST, created_at DESC, id DESC
      LIMIT $2 OFFSET $3
      `,
      [`%${q}%`, L, O]
    );
    res.set("Cache-Control", "public, max-age=15");
    res.json(rows);
  } catch (e) {
    console.error("[/search]", e);
    res.status(500).json({ message: "Search failed" });
  }
});

// POST /read
app.post("/read", async (req, res) => {
  try {
    const { user_id, news_id } = getPayload(req);
    if (!user_id || !news_id) return res.status(400).json({ message: "Missing data" });
    await db.query(`INSERT INTO news_reads (user_id, news_id) VALUES ($1, $2)`, [user_id, news_id]);
    res.json({ message: "Read history saved" });
  } catch (e) {
    console.error("[/read]", e);
    res.status(500).json({ message: "Failed to save read history" });
  }
});

// POST /subscribe (idempotent, ép kiểu bằng CTE để tránh 42P08)
app.post("/subscribe", async (req, res) => {
  try {
    const { user_id, category, location } = getPayload(req);
    if (!user_id) return res.status(400).json({ message: "Missing user_id" });

    const sql = `
      WITH v AS (
        SELECT
          $1::varchar(100) AS user_id,
          $2::varchar(50)  AS category,
          $3::varchar(100) AS location
      )
      INSERT INTO subscriptions (user_id, category, location)
      SELECT v.user_id, v.category, v.location
      FROM v
      WHERE NOT EXISTS (
        SELECT 1 FROM subscriptions s
        WHERE s.user_id  = v.user_id
          AND s.category IS NOT DISTINCT FROM v.category
          AND s.location IS NOT DISTINCT FROM v.location
      )
    `;
    await db.query(sql, [user_id, category ?? null, location ?? null]);
    res.json({ message: "Subscription saved" });
  } catch (e) {
    console.error("[/subscribe]", e);
    res.status(500).json({ message: "Failed to subscribe" });
  }
});

// POST /devices (idempotent, ép kiểu tương tự)
app.post("/devices", async (req, res) => {
  try {
    const { user_id, device_token } = getPayload(req);
    if (!user_id || !device_token) return res.status(400).json({ message: "Missing data" });

    const sql = `
      WITH v AS (
        SELECT
          $1::varchar(100) AS user_id,
          $2::text         AS device_token
      )
      INSERT INTO devices (user_id, device_token)
      SELECT v.user_id, v.device_token
      FROM v
      WHERE NOT EXISTS (
        SELECT 1 FROM devices d
        WHERE d.user_id = v.user_id AND d.device_token = v.device_token
      )
    `;
    await db.query(sql, [user_id, device_token]);
    res.json({ message: "Device registered" });
  } catch (e) {
    console.error("[/devices]", e);
    res.status(500).json({ message: "Failed to register device" });
  }
});

// POST /notify-preview
app.post("/notify-preview", async (req, res) => {
  try {
    const { news_id } = getPayload(req);
    if (!news_id) return res.status(400).json({ message: "Missing news_id" });

    const { rows: n } = await db.query(
      `SELECT id, title, category, location FROM news WHERE id = $1`,
      [news_id]
    );
    if (!n.length) return res.status(404).json({ message: "News not found" });

    const { rows } = await db.query(
      `
      SELECT s.user_id, d.device_token
      FROM subscriptions s
      LEFT JOIN devices d ON d.user_id = s.user_id
      WHERE (s.category IS NULL OR s.category = $1)
        AND (s.location IS NULL OR s.location = $2)
      `,
      [n[0].category, n[0].location ?? null]
    );
    res.json({ ...n[0], recipients: rows });
  } catch (e) {
    console.error("[/notify-preview]", e);
    res.status(500).json({ message: "Failed to preview notifications" });
  }
});

/* -------- Scheduler -------- */
let isIngesting = false;
function startIngestOnce() {
  if (isIngesting) return;
  isIngesting = true;
  console.log("[Scheduler] Start ingest job...");
  const child = spawn(process.execPath, ["src/ingest/runIngest.js"], { stdio: "inherit", env: process.env });
  child.on("exit", (code) => { console.log(`[Scheduler] Ingest finished with code ${code}.`); isIngesting = false; });
  child.on("error", (err) => { console.error("[Scheduler] Failed to start ingest:", err.message); isIngesting = false; });
}
const INTERVAL_MIN = parseInt(process.env.INGEST_INTERVAL_MINUTES || "5", 10) || 5;
setInterval(startIngestOnce, Math.max(INTERVAL_MIN, 1) * 60 * 1000);

/* -------- Start -------- */
app.listen(PORT, () => {
  console.log(`News API running on port ${PORT}`);
  console.log(`[Scheduler] Auto-ingest every ${INTERVAL_MIN} minute(s).`);
});
