#include "ens_160.h"

ScioSense_ENS160 ens160(0x53);
bool ens_ready = false;

bool initENS()
{
    if (ens160.begin())
    {
        ens160.setMode(ENS160_OPMODE_STD);
        ens_ready = true;
        return true;
    }
    return false;
}

ENSData readENS()
{
    ENSData data = {0, 0, 0, false};
    if (ens_ready && ens160.available())
    {
        ens160.measure(true);
        ens160.measureRaw(true);
        data.aqi = ens160.getAQI();
        data.tvoc = ens160.getTVOC();
        data.eco2 = ens160.geteCO2();
        data.valid = true;
    }
    else if (ens_ready)
    {
        // Giữ giá trị cũ nếu chưa có dữ liệu mới nhưng cảm biến vẫn online
        data.valid = false;
    }
    return data;
}