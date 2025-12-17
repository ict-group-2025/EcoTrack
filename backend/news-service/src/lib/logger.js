/*
  File này dùng để GHI LOG cho hệ thống.

  Mục đích:
  - Ghi lại quá trình chạy ingest và API
  - Giúp dễ debug khi có lỗi
  - Không ảnh hưởng logic chính
*/

const { createLogger, format, transports } = require("winston");

// Tạo logger dùng chung cho toàn hệ thống
const logger = createLogger({
  level: "info",
  format: format.combine(
    // Ghi thời gian cho mỗi log
    format.timestamp(),
    // Log ở dạng dễ đọc
    format.simple()
  ),
  transports: [
    // In log ra console
    new transports.Console()
  ]
});

module.exports = logger;
