import requests, json, time, os
from datetime import datetime, timezone, timedelta
from Data_cleaning import clean_data  # ✅ Import hàm vừa tạo

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
file_path = os.path.join(BASE_DIR, "weather.json")

API_KEY = "51fd68be8c2e7e6b965291693ec85e04"
CITY = "Hanoi"
COUNTRY = "vn"
URL = f"http://api.openweathermap.org/data/2.5/weather?q={CITY},{COUNTRY}&appid={API_KEY}"

data_list = []

while True:
    try:
        response = requests.get(URL)
        weather = response.json()

        now = datetime.now(timezone.utc) + timedelta(hours=7)
        weather["local_time"] = now.strftime("%Y-%m-%d %H:%M:%S")

        data_list.append(weather)

        # Lưu file JSON
        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(data_list, f, ensure_ascii=False, indent=4)

        print(f"[{now.strftime('%H:%M:%S')}] {CITY}: {weather['main']['temp'] - 273.15:.1f}°C, {weather['weather'][0]['description']}")

        # ✅ Sau 5 lần thu thập thì làm sạch dữ liệu một lần
        if len(data_list) % 1 == 0:
            clean_data()

        time.sleep(20)

    except KeyboardInterrupt:
        break
    except Exception as e:
        print("Error:", e)
        time.sleep(5)
