# 🚀 HƯỚNG DẪN CHẠY ỨNG DỤNG

## ⚠️ LỖI THƯỜNG GẶP:

### ❌ Lỗi: `mvn : The term 'mvn' is not recognized`
**Nguyên nhân:** Bạn đang dùng lệnh `mvn` nhưng Maven chưa được cài đặt.

**✅ GIẢI PHÁP:** Dùng **Maven Wrapper** (`mvnw.cmd`) thay vì `mvn`!

---

## 🎯 CÁCH CHẠY ĐÚNG:

### **CÁCH 1: Dùng file `run.bat` (DỄ NHẤT) ⭐**

1. Mở **File Explorer**
2. Vào thư mục: `D:\Learning coding\USTH Project\Final`
3. **Double-click** file `run.bat`
4. Đợi ứng dụng khởi động!

**Hoặc từ Terminal:**
```cmd
run.bat
```

---

### **CÁCH 2: Dùng Maven Wrapper**

#### Bước 1: Build project
```cmd
mvnw.cmd clean package -DskipTests
```

#### Bước 2: Chạy ứng dụng
```cmd
java -jar target\Final-0.0.1-SNAPSHOT.jar
```

**Hoặc chạy trực tiếp:**
```cmd
mvnw.cmd spring-boot:run
```

---

### **CÁCH 3: Từ PowerShell (nếu Cách 1 không chạy)**

```powershell
# Chuyển về thư mục project
cd "D:\Learning coding\USTH Project\Final"

# Chạy bằng cmd thay vì PowerShell
cmd /c mvnw.cmd spring-boot:run
```

---

## 📋 SO SÁNH LỆNH:

| ❌ SAI | ✅ ĐÚNG |
|--------|---------|
| `mvn clean package` | `mvnw.cmd clean package` |
| `mvn spring-boot:run` | `mvnw.cmd spring-boot:run` |
| `mvn compile` | `mvnw.cmd compile` |

---

## 🔍 KIỂM TRA:

### 1. Kiểm tra Maven Wrapper có tồn tại:
```cmd
dir mvnw.cmd
```
Nếu thấy file `mvnw.cmd` → OK!

### 2. Kiểm tra Java:
```cmd
java -version
```
Phải có Java 17+!

### 3. Kiểm tra thư mục:
```cmd
cd "D:\Learning coding\USTH Project\Final"
```
Phải ở đúng thư mục project!

---

## ✅ SAU KHI CHẠY THÀNH CÔNG:

Bạn sẽ thấy:
```
Tomcat started on port 8080 (http) with context path ''
Started FinalApplication in X.XXX seconds
```

Truy cập:
- 🌐 **Web:** http://localhost:8080
- 📡 **API:** http://localhost:8080/api/weather/list

---

## 🆘 NẾU VẪN LỖI:

1. **Đảm bảo đang ở đúng thư mục:**
   ```cmd
   cd "D:\Learning coding\USTH Project\Final"
   ```

2. **Thử chạy từ Command Prompt thay vì PowerShell:**
   - Mở **Command Prompt** (cmd.exe)
   - Chạy lệnh: `mvnw.cmd spring-boot:run`

3. **Kiểm tra Java:**
   ```cmd
   java -version
   ```
   Phải có Java 17 trở lên!

4. **Kiểm tra file mvnw.cmd:**
   ```cmd
   dir mvnw.cmd
   ```
   Phải thấy file này!

---

## 📞 TÓM TẮT:

**ĐỪNG dùng `mvn` - DÙNG `mvnw.cmd` hoặc `run.bat`!** 🎯
