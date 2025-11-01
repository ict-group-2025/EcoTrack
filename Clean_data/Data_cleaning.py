# Chuyển dữ liệu từ dạng JSON thành dạng bảng 
import os
import pandas as pd
import json
from datetime import datetime, timezone, timedelta

# Kiểm tra và tạo file output nếu chưa có
output_file = "clean_weather_data.csv"

if not os.path.exists(output_file):
    # Tạo DataFrame rỗng với đúng cấu trúc cột
    columns = [
        "temp_K", "humidity", "pressure",
        "wind_speed", "wind_deg", "clouds",
        "timestamp", "sunrise", "sunset",
        "temp_C", "datetime"
    ]
    empty_df = pd.DataFrame(columns=columns)
    empty_df.to_csv(output_file, index=False)
# Đọc dữ liệu từ file weather   
with open("weather.json", "r") as f:
    data = json.load(f)

# Chuyển dữ liệu thành DataFrame
df = pd.json_normalize(data)
print(df.head())
# Loại bỏ các dữ liệu không dùng 
columns_needed = [
    "main.temp", "main.humidity", "main.pressure",
    "wind.speed", "wind.deg", "clouds.all", "dt", "sys.sunrise", "sys.sunset"
]
df = df[columns_needed]
# Đổi tên các biến 
df.columns = [
    "temp_K", "humidity", "pressure",
    "wind_speed", "wind_deg", "clouds", "timestamp", "sunrise", "sunset"
]
# Chuyển từ độ K sang độ C
df["temp_C"] = df["temp_K"] - 273.15

from datetime import datetime, timezone, timedelta
df["datetime"] = df["timestamp"].apply(
    lambda x: datetime.fromtimestamp(x, tz=timezone(timedelta(hours=7)))
)
# Loại các giá trị trả về null
df.isnull().sum()  # kiểm tra cột nào bị thiếu

# Điền trung bình hoặc loại bỏ
df["humidity"].fillna(df["humidity"].mean(), inplace=True)
df.dropna(inplace=True)
# Xử lý các giá trị bất thường 
df = df[(df['temp_C'].between(-10, 50)) &           # Nhiệt độ -10 → 50°C
        (df['humidity'].between(0, 100)) &          # Độ ẩm 0–100%
        (df['pressure'].between(900, 1100)) &       # Áp suất hợp lý
        (df['wind_speed'].between(0, 50)) &         # Tốc độ gió hợp lý
        (df['clouds'].between(0, 100))]             # Mây 0–100%
# Chuẩn hoá dữ liệu 
df["humidity"] = df["humidity"].astype(float)
df["wind_speed"] = df["wind_speed"].astype(float)
# Loại các dữ liệu lặp 
df.drop_duplicates(inplace=True)
# Lưu dữ liệu vào database
df.to_csv("clean_weather_data.csv", index=False)
