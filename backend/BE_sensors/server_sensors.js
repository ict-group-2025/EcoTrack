const mqtt = require('mqtt');
const mongoose = require('mongoose');

// 1. CẤU HÌNH
const MQTT_BROKER = 'mqtt://broker.hivemq.com';
const MQTT_TOPIC = 'ecotrack/sensors/data';

// Sửa lại URI: Thêm tên database vào sau .net/ 
const MONGO_URI = 'mongodb+srv://admin:Longpv.22ba13206@cluster0.aah4xok.mongodb.net/iot_database?retryWrites=true&w=majority&appName=Cluster0';

// 2. SCHEMA
const SensorSchema = new mongoose.Schema({
    location: { type: String, default: "Home_Hanoi" },
    temp: Number,
    hum: Number,
    pres: Number,
    aqi: Number,
    pm25: Number,
    timestamp: { type: Date, default: Date.now }
});

const SensorData = mongoose.model('SensorData', SensorSchema);

// 3. HÀM KHỞI CHẠY (Logic tuần tự: Kết nối DB xong mới chạy MQTT)
async function startApp() {
    try {
        console.log(' Đang kết nối MongoDB...');

        // Kết nối Database trước
        await mongoose.connect(MONGO_URI);
        console.log(' KẾT NỐI MONGODB THÀNH CÔNG!');

        // Sau khi DB OK, mới bắt đầu kết nối MQTT
        connectMQTT();

    } catch (err) {
        console.error(' LỖI KẾT NỐI DATABASE (Kiểm tra lại IP Access trên Atlas):', err.message);
        process.exit(1); // Dừng chương trình nếu không có DB
    }
}

function connectMQTT() {
    const client = mqtt.connect(MQTT_BROKER);

    client.on('connect', () => {
        console.log('📡 Đã kết nối HiveMQ, đang chờ dữ liệu...');
        client.subscribe(MQTT_TOPIC);
    });

    client.on('message', async (topic, message) => {
        const msgString = message.toString();
        try {
            const data = JSON.parse(msgString);

            // Kiểm tra sơ bộ dữ liệu rác
            if (!data.temp && !data.pm25) return;

            const newData = new SensorData({
                temp: data.temp,
                hum: data.hum,
                pres: data.pres,
                aqi: data.aqi,
                pm25: data.pm25 || data['pm2.5'] // Xử lý nếu tên biến khác
            });

            // Vì DB đã kết nối ở trên, lệnh này sẽ chạy ngay
            await newData.save();
            console.log(`[${new Date().toLocaleTimeString()}] Đã lưu`);

        } catch (error) {
            console.error(' Lỗi xử lý tin nhắn:', error.message);
        }
    });

    client.on('error', (err) => {
        console.error(' Lỗi MQTT:', err);
    });
}

// Bắt đầu chạy
startApp();