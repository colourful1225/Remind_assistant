#!/bin/bash
# 快速测试启动脚本 - 在 macOS/Linux 上运行所有单元测试
# 使用方法：在终端执行 bash run_unit_tests.sh 或 chmod +x && ./run_unit_tests.sh

cd "$(dirname "$0")"

echo ""
echo "========================================"
echo "Global Reminder Assistant - 测试套件"
echo "========================================"
echo ""

echo "[1/3] 构建项目..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo "构建失败！"
    exit 1
fi
echo "[✓] 构建成功"

echo ""
echo "[2/3] 运行单元测试..."
./gradlew testDebugUnitTest
if [ $? -ne 0 ]; then
    echo "单元测试有失败！"
    echo "请查看上面的错误信息"
fi

echo ""
echo "[3/3] 测试报告..."
echo "单元测试报告位置：app/build/reports/tests/testDebugUnitTest/index.html"
echo ""
