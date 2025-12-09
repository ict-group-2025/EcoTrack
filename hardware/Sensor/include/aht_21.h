#ifndef aht_21
#define aht_21

#include <Adafruit_AHTX0.h>

struct AHTData
{
    float temperature;
    float humidity;
    bool valid;
};

bool initAHT();
AHTData readAHT();

#endif