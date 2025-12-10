#include <Arduino.h>
#include <Wire.h>
#include <WiFi.h>

// Include module
#include "bmp_280.h"
#include "aht_21.h"
#include "ens_160.h"
#include "dust.h"
#include "mqtt.h" 

#define SDA_PIN 18
#define SCL_PIN 19

const char *ssid = "S20 FE 5G";
const char *password = "68686868";

unsigned long lastSend = 0;
const unsigned long SEND_INTERVAL = 5000;

void setupWiFi()
{
  WiFi.begin(ssid, password);
  Serial.print("WiFi Connecting");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" OK!");
}

void setup()
{
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);

  setupWiFi();
  setupMQTT(); // <--- Gọi hàm từ file riêng

  Wire.begin(SDA_PIN, SCL_PIN);

  if (initBMP())
    Serial.println("BMP OK");
  if (initAHT())
    Serial.println("AHT OK");
  if (initENS())
    Serial.println("ENS OK");
  initDust();
}

void loop()
{
  loopMQTT(); // <--- Duy trì kết nối

  if (millis() - lastSend < SEND_INTERVAL)
    return;
  lastSend = millis();

  // Đọc cảm biến
  AHTData aht = readAHT();
  BMPData bmp = readBMP();

  float t = aht.valid ? aht.temperature : 25.0;
  float h = aht.valid ? aht.humidity : 50.0;
  ENSData ens = readENS();

  float dust = readDustDensity();

  // Đóng gói JSON
  char json[256];
  snprintf(json, sizeof(json),
           "{\"temp\":%.2f,\"hum\":%.2f,\"pres\":%.2f,\"aqi\":%d,\"pm25\":%.1f}",
           aht.temperature, aht.humidity, bmp.pressure, ens.aqi, dust);

  // Gửi đi
  sendSensorDataMQTT(json); 
 
  digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));
}