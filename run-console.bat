@echo off
title Parking Lot Management System - Console
echo ===================================================================
echo    Parking Lot Management System (Java OOP Console)
echo ===================================================================
echo.
cd /d "%~dp0"

if not exist "src\Main.class" (
    echo Compiling Java source files...
    javac src\*.java
)

echo Starting Console Application...
echo.
cd src
java Main
pause
