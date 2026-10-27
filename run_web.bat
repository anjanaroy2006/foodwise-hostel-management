@echo off
title FOODWISE - Smart Hostel Meal Management Server
echo ========================================================
echo FOODWISE: Predict. Prepare. Prevent. Preserve.
echo Starting FOODWISE Web Application Server...
echo ========================================================

REM Create bin directory if not exists
if not exist bin mkdir bin

REM Compile all Java source files
echo [1/2] Compiling Java classes...
javac -d bin src\com\foodwise\model\*.java src\com\foodwise\service\*.java src\com\foodwise\dao\*.java src\com\foodwise\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed! Please check if JDK is installed and added to PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Starting HTTP Server on http://localhost:8080 ...
echo.
echo Press Ctrl+C in this console to stop the server.
echo.

REM Launch browser automatically
start http://localhost:8080

REM Run Java Server
java -cp bin com.foodwise.FoodWiseServer
pause
