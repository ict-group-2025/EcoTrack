@echo off
echo ========================================
echo   CHAY UNG DUNG SPRING BOOT
echo ========================================
echo.

REM Kiem tra xem JAR da duoc build chua
if exist "target\Final-0.0.1-SNAPSHOT.jar" (
    echo [INFO] Tim thay JAR file, dang chay...
    echo.
    java -jar "target\Final-0.0.1-SNAPSHOT.jar"
) else (
    echo [INFO] JAR file chua duoc build, dang build...
    echo.
    call mvnw.cmd clean package -DskipTests
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo [INFO] Build thanh cong! Dang chay ung dung...
        echo.
        java -jar "target\Final-0.0.1-SNAPSHOT.jar"
    ) else (
        echo [ERROR] Build that bai! Vui long kiem tra loi.
        pause
        exit /b 1
    )
)

pause
