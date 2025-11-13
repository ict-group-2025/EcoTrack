#include <Arduino.h>
#include <Wire.h>
#include <Adafruit_BMP280.h>
#include <Adafruit_AHTX0.h>
#include <ScioSense_ENS160.h>

// =======================
// Chân I2C và LED onboard
// =======================
#define SDA_PIN 21
#define SCL_PIN 22
#define LED_BUILTIN 2

// =======================
// Cảm biến bụi GP2Y1010AU0F
// =======================
#define DUST_LED_PIN 5     // điều khiển LED IR
#define DUST_ANALOG_PIN 34 // chân đọc tín hiệu ADC

// =======================
// Cảm biến I2C khác
// =======================
Adafruit_BMP280 bmp;
Adafruit_AHTX0 aht;
ScioSense_ENS160 ens160(0x53);

bool bmp_ready = false;
bool aht_ready = false;
bool ens_ready = false;

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
      Serial.print("Tìm thấy thiết bị tại: 0x");
      if (i < 16)
        Serial.print("0");
      Serial.println(i, HEX);
      count++;
    }
  }
  if (count == 0)
    Serial.println("❌ Không tìm thấy thiết bị I2C!");
  else
  {
    Serial.print("Tổng số thiết bị: ");
    Serial.println(count);
  }
  Serial.println("------------------------\n");
}

// =======================
// Khởi tạo
// =======================
void setup()
{
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(DUST_LED_PIN, OUTPUT);
  digitalWrite(DUST_LED_PIN, HIGH); // tắt LED GP2Y (mức HIGH = off)

  Serial.begin(115200);
  delay(2000);
  Serial.println("\n=== ESP32 Test ENS160 + BMP280 + AHT21 + GP2Y ===");

  Wire.begin(SDA_PIN, SCL_PIN);
  delay(200);
  scanI2C();

  // BMP280
  Serial.println("Khởi tạo BMP280...");
  if (bmp.begin(0x76) || bmp.begin(0x77))
  {
    Serial.println("✅ BMP280 sẵn sàng");
    bmp_ready = true;
  }
  else
    Serial.println("❌ Không tìm thấy BMP280!");

  // AHT21
  Serial.println("Khởi tạo AHT21...");
  if (aht.begin())
  {
    Serial.println("✅ AHT21 sẵn sàng");
    aht_ready = true;
  }
  else
    Serial.println("❌ Không tìm thấy AHT21!");

  // ENS160
  Serial.println("Khởi tạo ENS160...");
  if (ens160.begin())
  {
    ens160.setMode(ENS160_OPMODE_STD);
    Serial.println("✅ ENS160 sẵn sàng (cần 3 phút để hiệu chuẩn)");
    ens_ready = true;
  }
  else
    Serial.println("❌ Không tìm thấy ENS160!");
}

// =======================
// Đọc cảm biến bụi GP2Y
// =======================
float readDustDensity()
{
  digitalWrite(DUST_LED_PIN, LOW); // bật LED IR
  delayMicroseconds(280);
  int raw = analogRead(DUST_ANALOG_PIN); // đọc ADC
  delayMicroseconds(40);
  digitalWrite(DUST_LED_PIN, HIGH); // tắt LED
  delayMicroseconds(9680);

  float voltage = raw * (3.3 / 4095.0);        // ADC 12-bit
  float dustDensity = (voltage - 0.9) / 0.005; // công thức từ datasheet

  if (dustDensity < 0)
    dustDensity = 0; // tránh giá trị âm
  return dustDensity;
}

// =======================
// Vòng lặp chính
// =======================
void loop()
{
  Serial.println("\n-------------------------------------------");
  Serial.printf("Thời gian: %lus\n", millis() / 1000);

  // BMP280
  if (bmp_ready)
  {
    Serial.println("--- BMP280 ---");
    Serial.printf("Nhiệt độ: %.2f°C\n", bmp.readTemperature());
    Serial.printf("Áp suất: %.2f hPa\n", bmp.readPressure() / 100.0F);
    Serial.printf("Độ cao: %.2f m\n", bmp.readAltitude(1013.25));
  }

  // AHT21
  if (aht_ready)
  {
    sensors_event_t humidity, temp;
    aht.getEvent(&humidity, &temp);
    Serial.println("--- AHT21 ---");
    Serial.printf("Độ ẩm: %.2f%%\n", humidity.relative_humidity);
  }

  // ENS160
  if (ens_ready && ens160.available())
  {
    ens160.measure(true);
    ens160.measureRaw(true);

    Serial.println("--- ENS160 ---");
    Serial.printf("AQI: %d\n", ens160.getAQI());
    Serial.printf("TVOC: %d ppb\n", ens160.getTVOC());
    Serial.printf("eCO2: %d ppm\n", ens160.geteCO2());
  }

  // GP2Y1010AU0F
  Serial.println("--- GP2Y1010AU0F ---");
  float dust = readDustDensity();
  Serial.printf("Nồng độ bụi: %.2f mg/m³\n", dust);

  // LED onboard chớp báo hiệu hoạt động
  digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));

  delay(5000);
}
