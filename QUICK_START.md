# 🚀 HƯỚNG DẪN CHẠY NHANH

## ⚠️ LƯU Ý QUAN TRỌNG:
**KHÔNG dùng lệnh `mvn` trực tiếp!** 
- Maven chưa được cài đặt trên máy bạn
- Dự án đã có **Maven Wrapper** (`mvnw.cmd`) - dùng cái này!

---

## ✅ CÁCH CHẠY ĐƠN GIẢN NHẤT:

### **Cách 1: Dùng file `run.bat` (KHUYẾN NGHỊ)**
```cmd
run.bat
```
File này sẽ tự động:
- Build project nếu chưa có JAR
- Chạy ứng dụng Spring Boot

### **Cách 2: Dùng file `build.bat`**
```cmd
build.bat
```
Chỉ build project, không chạy.

### **Cách 3: Dùng Maven Wrapper trực tiếp**

#### Build project:
```cmd
mvnw.cmd clean package -DskipTests
```

#### Chạy ứng dụng:
```cmd
java -jar target\Final-0.0.1-SNAPSHOT.jar
```

#### Hoặc chạy trực tiếp với Maven:
```cmd
mvnw.cmd spring-boot:run
```

---

## 🔧 NẾU VẪN GẶP LỖI:

### Lỗi: "mvnw.cmd is not recognized"
**Giải pháp:**
1. Đảm bảo bạn đang ở thư mục project: `D:\Learning coding\USTH Project\Final`
2. Kiểm tra file `mvnw.cmd` có tồn tại không
3. Chạy từ Command Prompt thay vì PowerShell:
   ```cmd
   cmd
   cd "D:\Learning coding\USTH Project\Final"
   mvnw.cmd clean package -DskipTests
   ```

### Lỗi: "Java not found"
**Giải pháp:**
1. Kiểm tra Java đã cài: `java -version`
2. Nếu chưa có, cài Java 17+ từ: https://adoptium.net/

### Lỗi: "Port 8080 already in use"
**Giải pháp:**
1. Tắt ứng dụng đang chạy trên port 8080
2. Hoặc đổi port trong `application.properties`:
   ```properties
   server.port=8081
   ```

---

## 📋 TÓM TẮT LỆNH:

| Mục đích | Lệnh |
|----------|------|
| **Chạy ứng dụng** | `run.bat` |
| **Build project** | `build.bat` |
| **Build + Run** | `mvnw.cmd spring-boot:run` |
| **Chỉ build** | `mvnw.cmd clean package -DskipTests` |
| **Chạy JAR** | `java -jar target\Final-0.0.1-SNAPSHOT.jar` |

---

## ✅ SAU KHI CHẠY THÀNH CÔNG:

- 🌐 **Web:** http://localhost:8080
- 📡 **API Weather:** http://localhost:8080/api/weather/list
- 💬 **WebSocket:** ws://localhost:8080/chat-websocket

---

## 🎯 NHỚ:
- ✅ Dùng `mvnw.cmd` (Maven Wrapper) - KHÔNG dùng `mvn`
- ✅ Hoặc dùng file `run.bat` để tự động
- ❌ KHÔNG dùng `mvn clean package` trực tiếp!
