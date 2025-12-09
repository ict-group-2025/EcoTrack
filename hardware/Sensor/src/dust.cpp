#include "dust.h"

// --- CẤU HÌNH THỰC TẾ (CALIBRATION) ---

// 1. NGƯỠNG ĐIỆN ÁP NỀN (QUAN TRỌNG NHẤT)
// Lý thuyết Sharp là 0.6V. Nhưng thực tế ESP32 của bạn đọc được tầm 0.4V - 0.5V.
// Đặt 0.35V để đảm bảo luôn bắt được tín hiệu, kể cả khi không khí sạch.
const float SHARP_VOC_STANDARD = 0.3;

// 2. HỆ SỐ CHUYỂN ĐỔI (K)
// Giữ nguyên 170 theo chuẩn cộng đồng (tương đương độ nhạy 0.5V/100ug).
const float SHARP_K_COEFF = 170.0;

float smoothedDust = 0;
// Tăng hệ số làm mượt lên một chút để chỉ số đỡ nhảy loạn xạ
const float ALPHA = 0.1;

void initDust()
{
    pinMode(DUST_LED_ANALOG, OUTPUT);
    digitalWrite(DUST_LED_ANALOG, HIGH); // Mặc định tắt LED (High impedance/VCC)

    // ADC_11db cho phép đo dải từ 0 - 3.3V (hoặc 3.6V tùy chip)
    // Đây là setting chuẩn cho ESP32
    analogSetPinAttenuation(DUST_VO_PIN, ADC_11db);

    smoothedDust = 0; // Reset giá trị
}

float readDustDensity()
{
    // --- BƯỚC 1: ĐO ĐIỆN ÁP RAW (QUY TRÌNH CHUẨN) ---
    float rawSum = 0;
    int sampleCount = 10; // Lấy mẫu 10 lần để trung bình cộng, giảm nhiễu

    for (int i = 0; i < sampleCount; i++)
    {
        digitalWrite(DUST_LED_ANALOG, LOW); // Bật IR LED
        delayMicroseconds(280);             // Đợi dòng khí ổn định (theo Datasheet)

        int raw = analogRead(DUST_VO_PIN); // Đọc giá trị ADC ngay lập tức
        rawSum += raw;

        delayMicroseconds(40);               // Đợi cho đủ chu kỳ xung
        digitalWrite(DUST_LED_ANALOG, HIGH); // Tắt LED
        delayMicroseconds(9680);             // Nghỉ (Duty cycle thấp để tránh nóng LED)
    }

    float rawAvg = rawSum / sampleCount;

    // Đổi sang Volt: 3.3V là điện áp tham chiếu, 4095 là độ phân giải 12-bit
    float voltage = rawAvg * (3.3 / 4095.0);

    // --- BƯỚC 2: TÍNH TOÁN (ĐÃ HIỆU CHỈNH) ---
    float dustDensity = 0;

    // Logic: Chỉ tính bụi khi điện áp vượt qua ngưỡng nền (0.35V)
    if (voltage > SHARP_VOC_STANDARD)
    {
        // Công thức tuyến tính: (V_đo - V_nền) * Hệ_số_dốc
        dustDensity = (voltage - SHARP_VOC_STANDARD) * SHARP_K_COEFF;
    }
    else
    {
        // Nếu điện áp < 0.3V -> Không khí cực sạch hoặc nhiễu -> Cho về 0
        dustDensity = 0;
    }

    // --- BƯỚC 3: LỌC MƯỢT KẾT QUẢ (Exponential Moving Average) ---
    // Giúp số trên màn hình không bị giật cục
    smoothedDust = (smoothedDust * (1.0 - ALPHA)) + (dustDensity * ALPHA);

    // --- BƯỚC 4: DEBUG ĐỂ CÂN CHỈNH ---
    // Quan sát dòng này trên Serial Monitor là quan trọng nhất
    Serial.printf("Volt: %.3f V | Voc_Set: %.2f | Raw: %.1f | Final: %.1f ug/m3 |rawAVG: %.1f\n" ,
                  voltage, SHARP_VOC_STANDARD, dustDensity, smoothedDust, rawAvg);

    return smoothedDust;
}