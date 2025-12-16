/*
  FILE KHỞI TẠO / ĐẢM BẢO CẤU TRÚC DATABASE

  Nhiệm vụ:
  - Kết nối tới PostgreSQL
  - Đọc và thực thi schema.sql
  - Đảm bảo các bảng / cột / index tồn tại
  - HỖ TRỢ MIGRATION NHẸ (không làm mất dữ liệu)

  File này được dùng khi:
  - Lần đầu setup hệ thống
  - Sau khi nâng cấp schema (ví dụ thêm detail + image)
*/

const fs = require("fs");
const path = require("path");
const db = require("../lib/db");

async function initDatabase() {
  try {
    console.log("Initializing database schema...");

    // 1. Đọc file schema.sql (định nghĩa toàn bộ cấu trúc DB)
    const schemaPath = path.join(__dirname, "schema.sql");
    const schemaSQL = fs.readFileSync(schemaPath, "utf8");

    /*
      2. Thực thi schema.sql
         - CREATE TABLE IF NOT EXISTS  -> không tạo trùng
         - ALTER TABLE ADD COLUMN IF NOT EXISTS -> migrate nhẹ
         - CREATE INDEX IF NOT EXISTS -> an toàn khi chạy nhiều lần
         - DO $$ ... $$               -> xử lý đổi kiểu dữ liệu (timestamp -> timestamptz)
    */
    await db.query(schemaSQL);

    console.log("Database initialized / migrated successfully.");
    process.exit(0);
  } catch (error) {
    /*
      Nếu có lỗi:
      - In ra message để debug
      - Không nuốt lỗi
      - Thoát process với mã lỗi
    */
    console.error("Failed to initialize database:", error.message);
    process.exit(1);
  }
}

// Chạy khởi tạo database
initDatabase();
