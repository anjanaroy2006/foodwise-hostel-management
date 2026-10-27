@echo off
title FOODWISE - Swing GUI Application
echo ========================================================
echo Launching FOODWISE Desktop Swing GUI...
echo ========================================================

if not exist bin mkdir bin

javac -d bin src\com\foodwise\model\*.java src\com\foodwise\service\*.java src\com\foodwise\dao\*.java src\com\foodwise\gui\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

java -cp bin com.foodwise.gui.FoodWiseSwingApp
pause
