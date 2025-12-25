#include "mqtt.h"
#include <WiFi.h>
#include <PubSubClient.h>

// --- CẤU HÌNH RIÊNG CHO MQTT ---
const char *mqtt_server = "broker.hivemq.com";
const int mqtt_port = 1883;
const char *mqtt_topic = "ecotrack/sensors/data";

WiFiClient espClient;
PubSubClient client(espClient);

// --- HÀM NỘI BỘ (Chỉ dùng trong file này) ---
void reconnect()
{
    while (!client.connected())
    {
        Serial.print("Connecting to MQTT...");
        String clientId = "ESP32Client-" + String(random(0xffff), HEX);

        if (client.connect(clientId.c_str()))
        {
            Serial.println("Connected!");
        }
        else
        {
            Serial.print("failed, rc=");
            Serial.print(client.state());
            Serial.println(" try again in 5 seconds");
            delay(5000);
        }
    }
}


void setupMQTT()
{
    client.setServer(mqtt_server, mqtt_port);
    // Nếu muốn nhận lệnh điều khiển thì thêm setCallback ở đây
}


void loopMQTT()
{
    if (!client.connected())
    {
        reconnect();
    }
    client.loop();
}


void sendSensorDataMQTT(const char *jsonBuffer)
{
    if (client.connected())
    {
        client.publish(mqtt_topic, jsonBuffer);

        Serial.print(" MQTT Sent: ");
        Serial.println(jsonBuffer);
    }
    else
    {
        Serial.println(" MQTT Disconnected, skip sending.");
    }
}