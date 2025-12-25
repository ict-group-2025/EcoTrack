#include <Arduino.h>
#include <Wire.h>
#include <WiFi.h>
#include "bmp_280.h"
#include "aht_21.h"
#include "ens_160.h"
#include "dust.h"
#include "mqtt.h" 

#define SDA_PIN 21
#define SCL_PIN 22

const char *ssid = "S20 FE 5G";
const char *password = "68686868";

unsigned long lastSend = 0;
const unsigned long SEND_INTERVAL = 10000;

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
  setupMQTT(); 

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
  loopMQTT();

  if (millis() - lastSend < SEND_INTERVAL)
    return;
  lastSend = millis();
  float dustf = getD(20,25);
       AHTData aht = readAHT();


      BMPData bmp = readBMP();

  float t = aht.valid ? aht.temperature : 25.0;
  float h = aht.valid ? aht.humidity : 50.0;
  ENSData ens = readENS();

  float dust = readDustDensity();

  char json[256];
  snprintf(json, sizeof(json),
           "{\"temp1\":%.2f, \"altitude\":%.2f, \"temp2\":%.2f,\"hum\":%.2f,\"pres\":%.2f,\"aqi\":%d,\"pm25\":%.1f}",
           bmp.temperature,bmp.altitude, aht.temperature, aht.humidity, bmp.pressure, ens.aqi, dust  );

  sendSensorDataMQTT(json); 
 
  digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));
}