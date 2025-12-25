#ifndef BMP_280_H
#define BMP_280_H

#include <Adafruit_BMP280.h>

struct BMPData{
    float temperature;
    float pressure;
    float altitude;
    bool valid;
};
bool initBMP();
BMPData readBMP();

#endif