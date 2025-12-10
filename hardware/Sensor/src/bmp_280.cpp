#include <bmp_280.h>

Adafruit_BMP280 bmp;
bool bmp_ready = false;

bool initBMP()
{
    // Thử khởi động cảm biến
    if (!bmp.begin(0x76) && !bmp.begin(0x77))
    {
        return false; // Không tìm thấy
    }

    // --- ĐÂY LÀ PHẦN QUAN TRỌNG ĐỂ KHẮC PHỤC LỖI ĐỨNG IM ---
    // Cấu hình ép buộc cảm biến chạy chế độ NORMAL (Đo liên tục)
    // Thay vì để mặc định (có thể bị rơi vào mode ngủ)
    bmp.setSampling(Adafruit_BMP280::MODE_NORMAL,     /* Chế độ vận hành */
                    Adafruit_BMP280::SAMPLING_X2,     /* Độ phân giải nhiệt độ */
                    Adafruit_BMP280::SAMPLING_X16,    /* Độ phân giải áp suất */
                    Adafruit_BMP280::FILTER_X16,      /* Bộ lọc nhiễu */
                    Adafruit_BMP280::STANDBY_MS_500); /* Thời gian nghỉ giữa 2 lần đo */

    bmp_ready = true;
    return true;
}

BMPData readBMP()
{
    BMPData data = {0, 0, 0, false};

    if (bmp_ready)
    {
        // Đọc nhiệt độ
        float temp = bmp.readTemperature();

        // Đọc áp suất
        float press = bmp.readPressure();

        // Kiểm tra nếu cảm biến trả về NaN (Lỗi đọc)
        if (isnan(temp) || isnan(press) || press == 0)
        {
            data.valid = false;
            return data;
        }

        // Áp dụng Offset
        float bmpOffset = -3.5; // Offset nhiệt độ (bạn tự chỉnh)
        data.temperature = temp ;

        data.pressure = press / 100.0F; // Đổi sang hPa

        // Tính độ cao (Dùng áp suất thực tế Hà Nội, ví dụ 1018 hPa)
        data.altitude = bmp.readAltitude(1018);

        data.valid = true;
    }
    return data;
}