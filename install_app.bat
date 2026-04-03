@echo off
REM 构建 + 自动安装脚本 - 生成 APK 并安装到设备
REM 使用方法：双击运行此脚本
REM 前提：手机需连接电脑并启用 USB 调试

setlocal enabledelayedexpansion
cd /d %~dp0

echo.
echo ========================================
echo Global Reminder Assistant - 构建+安装
echo ========================================
echo.

REM 检查 adb 是否可用
where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo [!] 警告：未找到 ADB 工具
    echo 请确保 Android SDK Tools 已安装且已添加到环境变量
    echo.
)

echo [1/3] 清除旧的构建...
call .\gradlew.bat clean

echo [2/3] 构建 Debug APK...
call .\gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)
echo [✓] 构建成功

echo [3/3] 安装到已连接的设备...
call .\gradlew.bat installDebug
if %errorlevel% neq 0 (
    echo 安装失败！可能原因：
    echo - 手机未连接
    echo - USB 调试未启用
    echo - ADB 连接问题
    echo.
    echo 你可以手动安装：
    echo   1. 双击 app\build\outputs\apk\debug\app-debug.apk
    echo   2. 或使用 adb install app\build\outputs\apk\debug\app-debug.apk
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✓ 安装成功！应用已安装到设备
echo ========================================
echo.
echo 下一步：
echo - 在手机上找到"Global Reminder Assistant"应用
echo - 打开应用进行测试
echo.

pause
