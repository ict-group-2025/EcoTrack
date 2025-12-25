require('dotenv').config()
const mqtt = require('mqtt');
const mongoose = require('mongoose');

const MQTT_BROKER = process.env.MQTT_BROKER;
const MQTT_TOPIC = process.env.MQTT_TOPIC;

// Sửa lại URI: Thêm tên database vào sau .net/ 
const MONGO_URI = process.env.MONGO_URI;

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

// ================== HTTP SERVER ==================
app.get('/', (req, res) => {
    res.send('EcoTrack MQTT Server running 🚀');
});

app.get('/health', (req, res) => {
    res.json({ status: 'OK', time: new Date() });
});

app.listen(PORT, () => {
    console.log(`🌐 HTTP server running on port ${PORT}`);
});

// ================== MAIN ==================
async function startApp() {
    try {
        console.log('🔌 Connecting MongoDB...');
        await mongoose.connect(MONGO_URI);
        console.log(' MongoDB connected');

        connectMQTT();
    } catch (err) {
        console.error(' MongoDB error:', err.message);
        process.exit(1);
    }
}

function connectMQTT() {
    const client = mqtt.connect(MQTT_BROKER);

    client.on('connect', () => {
        console.log('📡 Connected to HiveMQ');
        client.subscribe(MQTT_TOPIC);
    });

    client.on('message', async (topic, message) => {
        try {
            const data = JSON.parse(message.toString());
            if (!data.temp && !data.pm25) return;

            const newData = new SensorData({
                temp: data.temp,
                hum: data.hum,
                pres: data.pres,
                aqi: data.aqi,
                pm25: data.pm25 || data['pm2.5']
            });

            await newData.save();
            console.log('💾 Data saved');
        } catch (err) {
            console.error(' MQTT parse error:', err.message);
        }
    });

    client.on('error', err => {
        console.error(' MQTT error:', err.message);
    });
}

startApp();
