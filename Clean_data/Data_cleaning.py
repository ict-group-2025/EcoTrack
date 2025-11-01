import os
import pandas as pd
import json

def clean_data():
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    file_path = os.path.join(BASE_DIR, "weather.json")
    output_file = os.path.join(BASE_DIR, "clean_weather_data.csv")

    # Đọc dữ liệu json
    with open(file_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    # Chuyển thành dataframe
    df = pd.json_normalize(data)

    # Chọn cột
    columns_needed = [
        "main.temp", "main.humidity", "main.pressure",
        "wind.speed", "wind.deg", "clouds.all",
        "sys.sunrise", "sys.sunset", "local_time"
    ]
    df = df[columns_needed]
    df.columns = [
        "temp_K", "humidity", "pressure",
        "wind_speed", "wind_deg", "clouds",
        "sunrise", "sunset", "time"
    ]

    # Độ K -> °C
    df["temp_C"] = df["temp_K"] - 273.15

    # Làm sạch dữ liệu
    df["humidity"].fillna(df["humidity"].mean(), inplace=True)
    df.dropna(inplace=True)

    df = df[
        (df['temp_C'].between(-10, 50)) &
        (df['humidity'].between(0, 100)) &
        (df['pressure'].between(900, 1100)) &
        (df['wind_speed'].between(0, 50)) &
        (df['clouds'].between(0, 100))
    ]

    df = df.astype({
        "humidity": float,
        "wind_speed": float
    })

    df.drop_duplicates(subset=["time"], inplace=True)
    df.to_csv(output_file, index=False, encoding="utf-8-sig")
    print(df.tail())
