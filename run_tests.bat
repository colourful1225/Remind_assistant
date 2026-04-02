@echo off
REM 快速测试执行脚本 - Windows
REM 用法: run_tests.bat [test_type] [test_filter]
REM 
REM 示例:
REM   run_tests.bat all            运行所有测试
REM   run_tests.bat unit          运行所有单元测试
REM   run_tests.bat ui            运行所有 UI 测试
REM   run_tests.bat unit Parser   运行包含"Parser"名称的单元测试
REM   run_tests.bat ui Screen     运行包含"Screen"名称的 UI 测试
REM   run_tests.bat coverage      生成覆盖率报告

setlocal enabledelayedexpansion

REM 颜色定义（Windows 10+ 支持 ANSI）
set "GREEN=[92m"
set "YELLOW=[93m"
set "RED=[91m"
set "RESET=[0m"

echo %GREEN%================================%RESET%
echo %GREEN%Remind Assistant 测试执行脚本%RESET%
echo %GREEN%================================%RESET%

set "TEST_TYPE=%1"
set "FILTER=%2"

if "%TEST_TYPE%"=="" (
    set "TEST_TYPE=all"
)

cd /d "%~dp0"

if "%TEST_TYPE%"=="all" (
    echo.
    echo %YELLOW%[1/3] 清理旧构建...%RESET%
    call gradlew clean
    
    echo.
    echo %YELLOW%[2/3] 运行单元测试...%RESET%
    if "%FILTER%"=="" (
        call gradlew test
    ) else (
        call gradlew test --tests "*%FILTER%*"
    )
    
    echo.
    echo %YELLOW%[3/3] 运行 UI 测试（需要模拟器）...%RESET%
    if "%FILTER%"=="" (
        call gradlew connectedAndroidTest
    ) else (
        call gradlew connectedAndroidTest --tests "*%FILTER%*"
    )
    
    goto success
)

if "%TEST_TYPE%"=="unit" (
    echo.
    echo %YELLOW%运行单元测试...%RESET%
    if "%FILTER%"=="" (
        call gradlew test --info
    ) else (
        call gradlew test --tests "*%FILTER%*" --info
    )
    goto success
)

if "%TEST_TYPE%"=="ui" (
    echo.
    echo %YELLOW%运行 UI 测试...%RESET%
    if "%FILTER%"=="" (
        call gradlew connectedAndroidTest
    ) else (
        call gradlew connectedAndroidTest --tests "*%FILTER%*"
    )
    goto success
)

if "%TEST_TYPE%"=="coverage" (
    echo.
    echo %YELLOW%生成覆盖率报告...%RESET%
    call gradlew test jacocoTestReport
    
    echo.
    echo %GREEN%覆盖率报告已生成！%RESET%
    echo 打开报告: app\build\reports\jacoco\jacocoTestReport\html\index.html
    
    REM 尝试自动打开报告
    if exist "app\build\reports\jacoco\jacocoTestReport\html\index.html" (
        start app\build\reports\jacoco\jacocoTestReport\html\index.html
    )
    goto success
)

echo.
echo %RED%未知的测试类型: %TEST_TYPE%%RESET%
echo.
echo 用法:
echo   run_tests.bat all              - 运行所有测试
echo   run_tests.bat unit [FILTER]    - 运行单元测试
echo   run_tests.bat ui [FILTER]      - 运行 UI 测试
echo   run_tests.bat coverage         - 生成覆盖率报告
echo.
echo 示例:
echo   run_tests.bat unit Parser      - 运行包含 Parser 的单元测试
echo   run_tests.bat ui Screen        - 运行包含 Screen 的 UI 测试
echo.

goto error

:success
echo.
echo %GREEN%测试执行完成！%RESET%
exit /b 0

:error
exit /b 1
