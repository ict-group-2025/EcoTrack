import requests
import json
import time
from datetime import datetime

# === CẤU HÌNH ===
API_KEY = "51fd68be8c2e7e6b965291693ec85e04"        # 🔑 Thay bằng API key của bạn
CITY = "Hanoi,vn"               # Thành phố muốn lấy dữ liệu
INTERVAL = 1800                 # Thời gian cập nhật (giây): 1800s = 30 phút
OUTPUT_FILE = "weather.json"    # File lưu dữ liệu

# === VÒNG LẶP LẤY DỮ LIỆU ===
while True:
    try:
        # 1️⃣ Gửi request tới API OpenWeather
        url = f"https://api.openweathermap.org/data/2.5/weather?q={CITY}&appid={API_KEY}"
        response = requests.get(url)
        data = response.json()

        # 2️⃣ Thêm thời gian ghi nhận
        data["local_time"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        # 3️⃣ Ghi vào file JSON
        with open(OUTPUT_FILE, "a", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False)
            f.write("\n")  # xuống dòng để lưu nhiều bản ghi

        # 4️⃣ In ra console để kiểm tra
        temp_c = round(data["main"]["temp"] - 273.15, 1)
        print(f"[{data['local_time']}] {data['name']}: {temp_c}°C, {data['weather'][0]['description']}")

        # 5️⃣ Đợi trước khi cập nhật lần sau
        time.sleep(INTERVAL)

    except Exception as e:
        print("⚠️ Lỗi:", e)
        time.sleep(30)  # chờ 30s rồi thử lại
