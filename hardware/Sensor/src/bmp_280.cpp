#include "bmp_280.h"

Adafruit_BMP280 bmp;
bool bmp_ready = false;

bool initBMP()
{
    // Thử địa chỉ 0x76 và 0x77
    if (bmp.begin(0x76) || bmp.begin(0x77))
    {
        bmp_ready = true;
        return true;
    }
    return false;
}

BMPData readBMP()
{
    BMPData data = {0, 0, 0, false};
    if (bmp_ready)
    {
        float bmpOffset = -1.5; // Offset nhiệt độ
        data.temperature = bmp.readTemperature() + bmpOffset;
        data.pressure = bmp.readPressure() / 100.0F;
        data.altitude = bmp.readAltitude(1013.25);
        data.valid = true;
    }
    return data;
}