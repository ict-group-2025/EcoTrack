@echo off
echo ========================================
echo   BUILD PROJECT SPRING BOOT
echo ========================================
echo.

call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Build thanh cong!
    echo JAR file: target\Final-0.0.1-SNAPSHOT.jar
) else (
    echo.
    echo [ERROR] Build that bai!
)

pause
