#include "dust.h"



const float SHARP_VOC_STANDARD = 0.3;


const float SHARP_K_COEFF = 170.0;

float smoothedDust = 0;
const float ALPHA = 0.1;

void initDust()
{
    pinMode(DUST_LED_ANALOG, OUTPUT);
    digitalWrite(DUST_LED_ANALOG, HIGH);

    analogSetPinAttenuation(DUST_VO_PIN, ADC_11db);

    smoothedDust = 0;
}
float getD(float i, float a)
{
    return i + static_cast<float>(rand()) / (static_cast<float>(RAND_MAX / (a - i)));
}


float readDustDensity()
{
   
    float rawSum = 0;
    int sampleCount = 10; 

    for (int i = 0; i < sampleCount; i++)
    {
        digitalWrite(DUST_LED_ANALOG, LOW); // Bật IR LED
        delayMicroseconds(280);             

        int raw = analogRead(DUST_VO_PIN); // Đọc giá trị ADC ngay lập tức
        rawSum += raw;

        delayMicroseconds(40);               // Đợi cho đủ chu kỳ xung
        digitalWrite(DUST_LED_ANALOG, HIGH); // Tắt LED
        delayMicroseconds(9680);            
    }

    float rawAvg = rawSum / sampleCount;

    float voltage = rawAvg * (3.3 / 4095.0);

    float dustDensity = 0;

    if (voltage > SHARP_VOC_STANDARD)
    {
        dustDensity = (voltage - SHARP_VOC_STANDARD) * SHARP_K_COEFF;
    }
    else
    {
        dustDensity = 0;
    }

    smoothedDust = (smoothedDust * (1.0 - ALPHA)) + (dustDensity * ALPHA);

    Serial.printf("Volt: %.3f V | Voc_Set: %.2f | Raw: %.1f | Final: %.1f ug/m3 |rawAVG: %.1f\n" ,
                  voltage, SHARP_VOC_STANDARD, dustDensity, smoothedDust, rawAvg);

    return smoothedDust;
}