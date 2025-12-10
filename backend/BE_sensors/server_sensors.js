const mqtt = require('mqtt');

// 1. Cấu hình kết nối đến HiveMQ (Giống hệt ESP32)
const BROKER_URL = 'mqtt://broker.hivemq.com';
const TOPIC_NAME = 'ecotrack/sensors/data';

console.log(' Đang kết nối tới Broker HiveMQ...');

const client = mqtt.connect(BROKER_URL);

// 2. Khi kết nối thành công
client.on('connect', () => {
    console.log(' Đã kết nối Backend thành công!');

    client.subscribe(TOPIC_NAME, (err) => {
        if (!err) {
            console.log(`Đang lắng nghe dữ liệu tại topic: "${TOPIC_NAME}"`);
        }
    });
});

client.on('message', (topic, message) => {
    const msgString = message.toString();
    try {
        const data = JSON.parse(msgString);
        console.log(`\n Nhận dữ liệu mới lúc: ${new Date().toLocaleTimeString()}`);
        console.log(`   Nhiệt độ:   ${data.temp} °C`);
        console.log(`   Độ ẩm:      ${data.hum} %`);
        console.log(`   Áp suất:    ${data.pres} hPa`);
        console.log(`   AQI:        ${data.aqi}`);
        console.log(`   Bụi PM2.5:  ${data.pm25} µg/m³`);

        // --- CHỖ NÀY LÁT NỮA SẼ VIẾT CODE LƯU VÀO DATABASE ---
        // saveToDatabase(data); 
        console.log('Dữ liệu thô:', msgString);
    } catch (error) {
        console.error(' Lỗi định dạng JSON:', error);
        console.log('Dữ liệu thô:', msgString);
    }
});