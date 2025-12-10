#ifndef MQTT_HANDLER_H
#define MQTT_HANDLER_H

#include <Arduino.h>

// Khai báo các hàm sẽ dùng (Prototype)
void setupMQTT();
void loopMQTT();
void sendSensorDataMQTT(const char *jsonBuffer);

#endif