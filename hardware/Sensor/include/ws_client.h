// ========================================
// ws_client.h — WebSocket KHÔNG SSL
// Tương thích với WebSockets v0.1.5
// ========================================
#ifndef WS_CLIENT_H
#define WS_CLIENT_H

#include <WiFi.h>
#include <WebSocketsClient.h>

WebSocketsClient wsClient;

const char *WIFI_SSID = "S20 FE 5G";
const char *WIFI_PASS = "68686868";

const char *WS_HOST = "10.186.229.178";
const uint16_t WS_PORT = 8080;
const char *WS_PATH = "/";

bool isConnected = false;
unsigned long lastReconnect = 0;
const unsigned long RECONNECT_INTERVAL = 10000;

void webSocketEvent(WStype_t type, uint8_t *payload, size_t length)
{
    switch (type)
    {
    case WStype_DISCONNECTED:
        Serial.println(" WS disconnected");
        isConnected = false;
        break;

    case WStype_CONNECTED:
        Serial.println(" WS connected!");
        isConnected = true;
        lastReconnect = millis();
        break;

    case WStype_TEXT:
        Serial.printf(" Received: %s\n", payload);
        break;

    case WStype_ERROR:
        Serial.println(" WS Error");
        break;

    default:
        break;
    }
}

void connectWiFi()
{
    Serial.println("Đang kết nối WiFi...");
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASS);

    int retry = 0;
    while (WiFi.status() != WL_CONNECTED && retry < 30)
    {
        delay(500);
        Serial.print(".");
        retry++;
    }

    if (WiFi.status() == WL_CONNECTED)
    {
        Serial.println("\n WiFi đã kết nối!");
        Serial.print("IP: ");
        Serial.println(WiFi.localIP());
    }
    else
    {
        Serial.println("\n Không thể kết nối WiFi!");
        ESP.restart();
    }
}

void connectWS()
{
    Serial.println("Đang kết nối WS...");

    // Đăng ký event handler
    wsClient.onEvent(webSocketEvent);

    // Kết nối WS (không SSL)
    wsClient.begin(WS_HOST, WS_PORT, WS_PATH);

    // Cấu hình reconnect
    wsClient.setReconnectInterval(5000);
}

void sendSensorDataJson(String json)
{
    if (isConnected)
    {
        if (json.length() > 1000)
        {
            Serial.println(" JSON quá dài");
            return;
        }

        // Gửi dữ liệu qua WebSocket - KHÔNG dùng const String&
        wsClient.sendTXT(json.c_str());
        Serial.println(" Đã gửi");
    }
    else
    {
        Serial.println(" Chưa kết nối WS");

        if (millis() - lastReconnect > RECONNECT_INTERVAL)
        {
            Serial.println(" Thử kết nối lại...");
            connectWS();
            lastReconnect = millis();
        }
    }
}

void wsLoop()
{
    wsClient.loop();
    yield();
}

#endif