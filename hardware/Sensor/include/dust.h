#ifndef SENSOR_DUST_H
#define SENSOR_DUST_H

#include <Arduino.h>

#define DUST_LED_ANALOG 25
#define DUST_VO_PIN 34

void initDust();
float readDustDensity();
float getD(float m, float t);

#endif