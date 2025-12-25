#include <getAPI.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h> // Cần cài thư viện này

float getPressureFromAPI()
{
    if (WiFi.status() == WL_CONNECTED)
    {
        HTTPClient http;
        // URL của Open-Meteo cho Hà Nội
            String url = "https://api.open-meteo.com/v1/forecast?latitude=21.0468&longitude=105.8015&current=pressure_msl&timezone=Asia%2FBangkok";

        http.begin(url);
        int httpCode = http.GET();

        if (httpCode > 0)
        {
            String payload = http.getString();

            JsonDocument doc; // Tự động cấp phát bộ nhớ (ArduinoJson v7)
            DeserializationError error = deserializeJson(doc, payload);

            if (!error)
            {
                // Lấy giá trị pressure_msl từ JSON
                float seaLevelPressure = doc["current"]["pressure_msl"];

                // API này trả về hPa luôn, không cần chia
                Serial.print("API Pressure: ");
                Serial.println(seaLevelPressure);

                http.end();
                return seaLevelPressure;
            }
            else
            {
                Serial.println("JSON Parse Error");
            }
        }
        http.end();
    }
    return 1013.25; // Nếu mất mạng hoặc lỗi, trả về giá trị mặc định
}

const char *myToken = "237480d44b66017b82438f7e72cdc8552b49909a";

float getPm25FromWAQI(float lat, float lng) 
{
    if (WiFi.status() == WL_CONNECTED)
    {
        HTTPClient http;

        // Cấu trúc URL của WAQI: feed/geo:LAT;LNG/?token=TOKEN
        String url = "https://api.waqi.info/feed/geo:";
        url += String(lat, 4);
        url += ";";
        url += String(lng, 4);
        url += "/?token=";
        url += myToken; 

        http.begin(url);
        int httpCode = http.GET();

        if (httpCode > 0)
        {
            String payload = http.getString();
            JsonDocument doc;
            DeserializationError error = deserializeJson(doc, payload);

            if (!error)
            {
                // Kiểm tra xem API có trả về OK không
                const char *status = doc["status"];
                if (strcmp(status, "ok") == 0)
                {
                    // Lấy PM2.5 từ WAQI
                    float pm25 = doc["data"]["iaqi"]["pm25"]["v"];
                    return pm25;
                }
            }
        }
        http.end();
    }
    return -1.0;
}