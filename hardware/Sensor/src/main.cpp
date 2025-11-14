#include <Arduino.h>
#include <Wire.h>
#include <Adafruit_BMP280.h>
#include <Adafruit_AHTX0.h>
#include <ScioSense_ENS160.h>
#include "ws_client.h"

// =======================
// Chân I2C và LED onboard
// =======================
#define SDA_PIN 21
#define SCL_PIN 22
#define LED_BUILTIN 2

// =======================
// Cảm biến bụi GP2Y1010AU0F
// =======================
#define DUST_LED_PIN 5
#define DUST_ANALOG_PIN 34

// =======================
// Cảm biến I2C khác
// =======================
Adafruit_BMP280 bmp;
Adafruit_AHTX0 aht;
ScioSense_ENS160 ens160(0x53);

bool bmp_ready = false;
bool aht_ready = false;
bool ens_ready = false;

// Timing control
unsigned long lastSend = 0;
const unsigned long SEND_INTERVAL = 5000; // 5 giây

// =======================
// Quét I2C
// =======================
void scanI2C()
{
  Serial.println("\n--- Quét địa chỉ I2C ---");
  byte count = 0;
  for (byte i = 8; i < 120; i++)
  {
    Wire.beginTransmission(i);
    if (Wire.endTransmission() == 0)
    {
      Serial.printf("Tìm thấy thiết bị tại: 0x%02X\n", i);
      count++;
    }
    delay(5); // Delay nhỏ để tránh spam I2C
  }
  if (count == 0)
    Serial.println("❌ Không tìm thấy thiết bị I2C!");
  Serial.println("------------------------\n");
}

// =======================
// Đọc cảm biến bụi GP2Y
// =======================
float readDustDensity()
{
  digitalWrite(DUST_LED_PIN, LOW);
  delayMicroseconds(280);
  int raw = analogRead(DUST_ANALOG_PIN);
  delayMicroseconds(40);
  digitalWrite(DUST_LED_PIN, HIGH);
  delayMicroseconds(9680);

  float voltage = raw * (3.3 / 4095.0);
  float dustDensity = (voltage - 0.9) / 0.005;

  if (dustDensity < 0)
    dustDensity = 0;
  return dustDensity;
}

// =======================
// Khởi tạo
// =======================
void setup()
{
  Serial.begin(115200);
  delay(1000);

  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(DUST_LED_PIN, OUTPUT);
  digitalWrite(DUST_LED_PIN, HIGH);

  Serial.println("\n=== ESP32 Sensors + WebSocket ===");
  Serial.printf("Free Heap: %d bytes\n", ESP.getFreeHeap());

  // ⚠️ KẾT NỐI WIFI TRƯỚC
  connectWiFi();

  // Đợi WiFi kết nối xong
  int wifiRetry = 0;
  while (WiFi.status() != WL_CONNECTED && wifiRetry < 20)
  {
    delay(500);
    Serial.print(".");
    wifiRetry++;
  }

  if (WiFi.status() != WL_CONNECTED)
  {
    Serial.println("\n❌ WiFi timeout, restart...");
    ESP.restart();
  }

  // SAU ĐÓ MỚI KẾT NỐI WS
  delay(1000); // Delay trước khi kết nối WS
  connectWS();

  // Khởi tạo I2C
  Wire.begin(SDA_PIN, SCL_PIN);
  Wire.setClock(100000); // 100kHz - chậm hơn nhưng ổn định hơn
  delay(200);

  scanI2C();

  // BMP280
  if (bmp.begin(0x76) || bmp.begin(0x77))
  {
    bmp_ready = true;
    Serial.println("✅ BMP280 sẵn sàng");
  }
  else
    Serial.println("❌ Không tìm thấy BMP280!");

  // AHT21
  if (aht.begin())
  {
    aht_ready = true;
    Serial.println("✅ AHT21 sẵn sàng");
  }
  else
    Serial.println("❌ Không tìm thấy AHT21!");

  // ENS160
  if (ens160.begin())
  {
    ens160.setMode(ENS160_OPMODE_STD);
    ens_ready = true;
    Serial.println("✅ ENS160 sẵn sàng (cần 3 phút để hiệu chuẩn)");
  }
  else
    Serial.println("❌ Không tìm thấy ENS160!");

  Serial.println("\n🚀 Hệ thống sẵn sàng!");
  Serial.printf("Free Heap after init: %d bytes\n\n", ESP.getFreeHeap());
}

// =======================
// LOOP chính
// =======================
void loop()
{
  // CRITICAL: wsLoop() phải được gọi liên tục
  wsLoop();

  // Chỉ đọc và gửi dữ liệu mỗi 5 giây
  if (millis() - lastSend < SEND_INTERVAL)
  {
    return; // Thoát sớm, chỉ chạy wsLoop()
  }

  lastSend = millis();

  // Nhấp nháy LED
  digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));

  float temp = 0, pressure = 0, altitude = 0;
  float humidity = 0;
  int aqi = 0, tvoc = 0, eco2 = 0;

  // BMP280
  if (bmp_ready)
  {
    temp = bmp.readTemperature();
    pressure = bmp.readPressure() / 100.0F;
    altitude = bmp.readAltitude(1013.25);
  }
  yield(); // Yield sau mỗi cảm biến

  // AHT21
  if (aht_ready)
  {
    sensors_event_t humi, t;
    aht.getEvent(&humi, &t);
    humidity = humi.relative_humidity;
  }
  yield();

  // ENS160
  if (ens_ready && ens160.available())
  {
    ens160.measure(true);
    ens160.measureRaw(true);
    aqi = ens160.getAQI();
    tvoc = ens160.getTVOC();
    eco2 = ens160.geteCO2();
  }
  yield();

  // GP2Y Bụi
  float dust = readDustDensity();
  yield();

  // ==============================================
  // TẠO JSON STRING - Dùng cách hiệu quả hơn
  // ==============================================
  char jsonBuffer[256]; // Buffer cố định thay vì String động
  snprintf(jsonBuffer, sizeof(jsonBuffer),
           "{\"temp\":%.2f,\"pressure\":%.2f,\"altitude\":%.2f,\"humidity\":%.2f,\"aqi\":%d,\"tvoc\":%d,\"eco2\":%d,\"dust\":%.2f}",
           temp, pressure, altitude, humidity, aqi, tvoc, eco2, dust);

  String json = String(jsonBuffer);

  // IN RA SERIAL ĐỂ DEBUG
  Serial.println("\n📊 Dữ liệu cảm biến:");
  Serial.println(json);
  Serial.printf("Free Heap: %d bytes\n", ESP.getFreeHeap());

  // GỬI LÊN WEBSOCKET
  sendSensorDataJson(json);

  yield();
}