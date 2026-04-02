#!/bin/bash
# 快速测试执行脚本 - macOS/Linux
# 用法: bash run_tests.sh [test_type] [test_filter]
#
# 示例:
#   bash run_tests.sh all            运行所有测试
#   bash run_tests.sh unit          运行所有单元测试
#   bash run_tests.sh ui            运行所有 UI 测试
#   bash run_tests.sh unit Parser   运行包含"Parser"名称的单元测试
#   bash run_tests.sh ui Screen     运行包含"Screen"名称的 UI 测试
#   bash run_tests.sh coverage      生成覆盖率报告

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

TEST_TYPE=${1:-all}
FILTER=$2

cd "$(dirname "$0")"

echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}Remind Assistant 测试执行脚本${NC}"
echo -e "${GREEN}================================${NC}"

case $TEST_TYPE in
    all)
        echo ""
        echo -e "${YELLOW}[1/3] 清理旧构建...${NC}"
        ./gradlew clean
        
        echo ""
        echo -e "${YELLOW}[2/3] 运行单元测试...${NC}"
        if [ -z "$FILTER" ]; then
            ./gradlew test
        else
            ./gradlew test --tests "*$FILTER*"
        fi
        
        echo ""
        echo -e "${YELLOW}[3/3] 运行 UI 测试（需要模拟器）...${NC}"
        if [ -z "$FILTER" ]; then
            ./gradlew connectedAndroidTest
        else
            ./gradlew connectedAndroidTest --tests "*$FILTER*"
        fi
        ;;
    
    unit)
        echo ""
        echo -e "${YELLOW}运行单元测试...${NC}"
        if [ -z "$FILTER" ]; then
            ./gradlew test --info
        else
            ./gradlew test --tests "*$FILTER*" --info
        fi
        ;;
    
    ui)
        echo ""
        echo -e "${YELLOW}运行 UI 测试...${NC}"
        if [ -z "$FILTER" ]; then
            ./gradlew connectedAndroidTest
        else
            ./gradlew connectedAndroidTest --tests "*$FILTER*"
        fi
        ;;
    
    coverage)
        echo ""
        echo -e "${YELLOW}生成覆盖率报告...${NC}"
        ./gradlew test jacocoTestReport
        
        echo ""
        echo -e "${GREEN}覆盖率报告已生成！${NC}"
        echo "打开报告: app/build/reports/jacoco/jacocoTestReport/html/index.html"
        
        # 尝试自动打开报告
        if [ -f "app/build/reports/jacoco/jacocoTestReport/html/index.html" ]; then
            if command -v open &> /dev/null; then
                open app/build/reports/jacoco/jacocoTestReport/html/index.html
            elif command -v xdg-open &> /dev/null; then
                xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html
            fi
        fi
        ;;
    
    *)
        echo ""
        echo -e "${RED}未知的测试类型: $TEST_TYPE${NC}"
        echo ""
        echo "用法:"
        echo "  bash run_tests.sh all              - 运行所有测试"
        echo "  bash run_tests.sh unit [FILTER]    - 运行单元测试"
        echo "  bash run_tests.sh ui [FILTER]      - 运行 UI 测试"
        echo "  bash run_tests.sh coverage         - 生成覆盖率报告"
        echo ""
        echo "示例:"
        echo "  bash run_tests.sh unit Parser      - 运行包含 Parser 的单元测试"
        echo "  bash run_tests.sh ui Screen        - 运行包含 Screen 的 UI 测试"
        echo ""
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}测试执行完成！${NC}"
exit 0
