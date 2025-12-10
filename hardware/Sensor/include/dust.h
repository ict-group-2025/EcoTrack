#ifndef SENSOR_DUST_H
#define SENSOR_DUST_H

#include <Arduino.h>

// Định nghĩa chân kết nối
#define DUST_LED_ANALOG 27
#define DUST_VO_PIN 34

void initDust();
float readDustDensity();

#endif