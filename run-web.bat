@echo off
title Parking Lot Management System - Web Server
echo ===================================================================
echo    Parking Lot Management System (Embedded Web Server)
echo    Powered by pure Java Standard Library (HttpServer)
echo ===================================================================
echo.
cd /d "%~dp0"

if not exist "src\ParkingWebServer.class" (
    echo Compiling Java source files...
    javac src\*.java
)

echo Starting Parking Lot Web Server on http://localhost:8095 ...
echo Press Ctrl+C in this window to stop the server.
echo.
start http://localhost:8095
cd src
java -cp . ParkingWebServer 8095 web
pause
