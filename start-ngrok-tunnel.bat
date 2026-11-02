@echo off
title Parking Lot Management System - Ngrok Permanent Tunnel
echo ===================================================================
echo    Parking Lot Management System - Ngrok Permanent Domain
echo ===================================================================
echo.
cd /d "%~dp0"

:: Check Java Web Server
netstat -ano | findstr ":8095 " | findstr "LISTENING" >nul
if errorlevel 1 (
    echo [INFO] Starting Java Web Server in background...
    start /min "ParkingWebServer" cmd /c "cd /d "%~dp0\src" && java -cp . ParkingWebServer 8095 web"
    timeout /t 3 /nobreak >nul
) else (
    echo [OK] Java Web Server is running on port 8095.
)

where ngrok >nul 2>&1
if errorlevel 1 (
    echo.
    echo [NOTE] Ngrok install nahi hai.
    echo Agar aap Ngrok use karna chahte hain:
    echo 1. https://ngrok.com par free account banayein.
    echo 2. Free Static Domain claim karein (e.g. your-name.ngrok-free.app).
    echo 3. 'ngrok http 8095 --domain=YOUR-DOMAIN' chalayein.
    echo.
    pause
    exit /b
)

echo Starting Ngrok on port 8095...
ngrok http 8095
pause
