@echo off
title Parking Lot Management System - Online Cloudflare Tunnel
echo ===================================================================
echo    Parking Lot Management System - Cloudflare Public Tunnel
echo    Exposing Port 8095 to the Public Internet
echo ===================================================================
echo.
cd /d "%~dp0"

:: 1. Purana atka hua cloudflared process band karo
taskkill /f /im cloudflared.exe >nul 2>&1

:: 2. Check karo agar Java web server 8095 par chal raha hai ya nahi
netstat -ano | findstr ":8095 " | findstr "LISTENING" >nul
if errorlevel 1 (
    echo [INFO] Java Web Server is not running. Starting it now in background...
    start /min "ParkingWebServer" cmd /c "cd /d "%~dp0\src" && java -cp . ParkingWebServer 8095 web"
    timeout /t 3 /nobreak >nul
) else (
    echo [OK] Java Web Server is already running on port 8095.
)

if exist "..\cloudflared.exe" (
    echo.
    echo Starting Cloudflare Tunnel for http://localhost:8095 ...
    echo [NOTE] Quick tunnel link is temporary. Your active public link will appear below:
    echo -------------------------------------------------------------------
    ..\cloudflared.exe tunnel --edge-ip-version 4 --protocol http2 --url http://localhost:8095
) else (
    echo [ERROR] cloudflared.exe not found in parent directory.
)
pause
