@echo off
REM 快速打包脚本 - 生成 Debug APK
REM 使用方法：双击运行此脚本

setlocal enabledelayedexpansion
cd /d %~dp0

echo.
echo ========================================
echo Global Reminder Assistant - APK 打包
echo ========================================
echo.

echo [1/2] 清除旧的构建文件...
call .\gradlew.bat clean
if %errorlevel% neq 0 (
    echo 清除失败！
    pause
    exit /b 1
)

echo [2/2] 构建 Debug APK...
call .\gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✓ 构建成功！
echo ========================================
echo.
echo APK 文件位置：
echo app\build\outputs\apk\debug\app-debug.apk
echo.
echo 文件大小：
for %%A in ("app\build\outputs\apk\debug\app-debug.apk") do echo %%~zA 字节
echo.

REM 提示打开位置
echo 按任意键在文件管理器中打开 APK 所在目录...
pause

REM 打开文件夹
explorer "%cd%\app\build\outputs\apk\debug\"
