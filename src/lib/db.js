/*
  File này quản lý KẾT NỐI DATABASE dùng chung.

  Mục tiêu:
  - Database là trung tâm của hệ thống
  - Ingest và API dùng chung kết nối
  - Tự retry khi database chưa sẵn sàng
*/

require("dotenv").config();
const { Pool } = require("pg");

// Tạo pool kết nối database
const pool = new Pool({
  host: process.env.DB_HOST,
  port: Number(process.env.DB_PORT),
  database: process.env.DB_NAME,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD
});

/*
  Hàm chờ database sẵn sàng.
  Khi dùng Docker, DB có thể khởi động chậm.
*/
async function waitForDatabase(retries = 10, delay = 3000) {
  for (let i = 0; i < retries; i++) {
    try {
      await pool.query("SELECT 1");
      return;
    } catch (error) {
      console.log("Waiting for database...");
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }

  throw new Error("Database is not ready after multiple attempts");
}

/*
  Hàm query dùng chung cho toàn hệ thống.
  Đảm bảo database đã sẵn sàng trước khi query.
*/
async function query(text, params = []) {
  await waitForDatabase();
  return pool.query(text, params);
}

module.exports = {
  query
};
