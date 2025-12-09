#include "aht_21.h"

Adafruit_AHTX0 aht;
bool aht_ready = false;

bool initAHT()
{
    if (aht.begin())
    {
        aht_ready = true;
        return true;
    }
    return false;
}

AHTData readAHT()
{
    AHTData data = {0, 0, false};
    if (aht_ready)
    {
        sensors_event_t humi, temp;
        aht.getEvent(&humi, &temp);

        float tempOffset = -2.0; // Offset nhiệt độ
        data.temperature = temp.temperature + tempOffset;
        data.humidity = humi.relative_humidity;
        data.valid = true;
    }
    return data;
}