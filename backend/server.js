const express = require("express");
const http = require("http");
const WebSocket = require("ws");

const app = express();
app.use(express.json());

// Tạo HTTP server (không SSL)
const server = http.createServer(app);

// Tạo WebSocket server
const wss = new WebSocket.Server({ server });

let latestData = {};

wss.on("connection", (ws, req) => {
    const clientIP = req.socket.remoteAddress;
    console.log(`🔌 ESP32 connected from ${clientIP}`);

    // Gửi tin nhắn welcome
    ws.send(JSON.stringify({ status: "connected", message: "Welcome ESP32!" }));

    ws.on("message", (msg) => {
        console.log(" Received:", msg.toString());

        try {
            latestData = JSON.parse(msg.toString());
            latestData.timestamp = new Date().toISOString();

            // Log ngắn gọn
            console.log(` Data updated: temp=${latestData.temp}°C, humidity=${latestData.humidity}%`);
        } catch (e) {
            console.log(" JSON parse error:", e.message);
        }
    });

    ws.on("close", () => {
        console.log(` ESP32 disconnected from ${clientIP}`);
    });

    ws.on("error", (err) => {
        console.log(" WebSocket error:", err.message);
    });

    // Ping/Pong để keep alive
    const interval = setInterval(() => {
        if (ws.readyState === ws.OPEN) {
            ws.ping();
        } else {
            clearInterval(interval);
        }
    }, 30000); // Ping mỗi 30s

    ws.on("pong", () => {
        // console.log(" Pong received");
    });
});

// API REST để xem dữ liệu
app.get("/sensor", (req, res) => {
    res.json(latestData);
});

// Start server
const PORT = 8080;

server.listen(PORT, "0.0.0.0", () => {
    console.log(`🚀 HTTP + WS server đang chạy tại: ws://10.186.229.178:${PORT}`);
});