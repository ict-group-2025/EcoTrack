#include <Arduino.h>
#include <Wire.h>
#include "ws_client.h"

// Include các file cảm biến đã tách
#include "bmp_280.h"
#include "aht_21.h"
#include "ens_160.h"
#include "dust.h"

#define SDA_PIN 21
#define SCL_PIN 22

unsigned long lastSend = 0;
const unsigned long SEND_INTERVAL = 5000;

// Hàm quét I2C (để debug lúc khởi động)
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
    delay(5);
  }
  if (count == 0)
    Serial.println("❌ Không tìm thấy thiết bị I2C!");
  Serial.println("------------------------\n");
}

void setup()
{
  Serial.begin(115200);
  delay(1000);
  pinMode(LED_BUILTIN, OUTPUT);

  Serial.println("\n=== ESP32 Modular Sensors ===");

  // 1. Kết nối Mạng
  connectWiFi();
  int wifiRetry = 0;
  while (WiFi.status() != WL_CONNECTED && wifiRetry < 20)
  {
    delay(500);
    Serial.print(".");
    wifiRetry++;
  }
  if (WiFi.status() != WL_CONNECTED)
    ESP.restart();
  delay(1000);
  connectWS();

  // 2. Khởi tạo I2C Global
  Wire.begin(SDA_PIN, SCL_PIN);
  Wire.setClock(100000);
  delay(200);
  scanI2C();

  // 3. Khởi tạo từng cảm biến (gọi hàm từ các file con)
  if (initBMP())
    Serial.println("✅ BMP280 sẵn sàng");
  else
    Serial.println("❌ Lỗi BMP280");

  if (initAHT())
    Serial.println("✅ AHT21 sẵn sàng");
  else
    Serial.println("❌ Lỗi AHT21");

  if (initENS())
    Serial.println("✅ ENS160 sẵn sàng");
  else
    Serial.println("❌ Lỗi ENS160");
  initDust(); // Cảm biến bụi analog

  Serial.println("\n🚀 Hệ thống sẵn sàng!");
}

void loop()
{
  wsLoop(); // Duy trì WebSocket

  if (millis() - lastSend < SEND_INTERVAL)
    return;
  lastSend = millis();

  digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));

  // --- Đọc dữ liệu từ các module ---
  BMPData bmpData = readBMP();
  AHTData ahtData = readAHT();
  ENSData ensData = readENS();
  float dust = readDustDensity();

  // --- Tạo JSON ---
  char jsonBuffer[350];
  snprintf(jsonBuffer, sizeof(jsonBuffer),
           "{\"temp\":%.2f,\"tempAHT\":%.2f,\"pressure\":%.2f,\"altitude\":%.2f,\"humidity\":%.2f,\"aqi\":%d,\"tvoc\":%d,\"eco2\":%d,\"pm2.5\":%.2f}",
           bmpData.temperature,
           ahtData.temperature,
           bmpData.pressure,
           bmpData.altitude,
           ahtData.humidity,
           ensData.aqi,
           ensData.tvoc,
           ensData.eco2,
           dust);

  String json = String(jsonBuffer);

  // Debug & Gửi
  Serial.println("\n📊 Dữ liệu:");
  Serial.println(json);
  Serial.printf("Free Heap: %d bytes\n", ESP.getFreeHeap());

  sendSensorDataJson(json);
}