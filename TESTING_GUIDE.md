# 测试指南

## 快速开始

### 方式 1：一键启动测试（推荐）

#### Windows
1. 在项目根目录找到 `run_unit_tests.bat`
2. 直接双击运行
3. 等待测试完成，自动显示测试报告

#### macOS / Linux
1. 在项目根目录打开终端
2. 执行 `bash run_unit_tests.sh`
3. 等待测试完成

### 方式 2：Android Studio 内运行

#### 运行所有单元测试
1. 在 Android Studio 菜单栏：`Run` → `Run 'All Tests'`
2. 或按快捷键 `Ctrl + Alt + Shift + F10`（Windows）/ `Control + Shift + R`（Mac）

#### 运行特定测试类
1. 打开测试文件：`app/src/test/java/com/example/reminderassistant/parser/TimeParserTest.kt`
2. 右键点击文件 → `Run 'TimeParserTest'`
3. 或在类名处按 `Ctrl + Shift + F10`（Windows）

#### 运行特定测试方法
1. 在 TimeParserTest 中右键点击某个 `@Test` 方法
2. 选择 `Run 'testParseToday()'`
3. 或在方法处按 `Ctrl + Shift + F10`（Windows）

### 方式 3：命令行运行

#### 仅运行单元测试（不构建 APK）
```bash
./gradlew testDebugUnitTest
```

#### 构建并运行单元测试
```bash
./gradlew build
```

#### 只编译不运行测试
```bash
./gradlew compileDebugUnitTest
```

---

## 测试覆盖范围

### TimeParserTest（32 个测试用例）

✅ **日期解析（4 个测试）**
- `testParseToday()` - 解析"今天"
- `testParseTomorrow()` - 解析"明天"
- `testParseDayAfterTomorrow()` - 解析"后天"
- `testParseMonthDay()` - 解析"5月15日"格式

✅ **时间解析（6 个测试）**
- `testParseMorningTime()` - 解析"早上"
- `testParseAfternoonTime()` - 解析"下午"
- `testParseEveningTime()` - 解析"晚上"
- `testParseNoonTime()` - 解析"中午"
- `testParseTimeWithColon()` - 解析"14:30"格式
- `testParseTimeWithoutPeriod()` - 解析纯数字时间

✅ **组合解析（3 个测试）**
- `testParseDateAndTime()` - 日期+时间组合
- `testParseOnlyTime()` - 仅时间
- `testParseOnlyDate()` - 仅日期

✅ **边界和异常（4 个测试）**
- `testParseNoDateAndTime()` - 无时间信息
- `testParseEmptyString()` - 空字符串
- `testParseInvalidTime()` - 无效时间值
- `testParseConflict()` - 冲突数据

✅ **周几解析（3 个测试）**
- `testParseMonday()` - 周一
- `testParseFriday()` - 周五
- `testParseSunday()` - 周日

✅ **实际场景（3 个测试）**
- `testParseFromRealWorldText1()` - 会议场景
- `testParseFromRealWorldText2()` - 工作场景
- `testParseFromRealWorldText3()` - 周期场景

✅ **置信度验证（2 个测试）**
- `testConfidenceWithBothDateAndTime()` - 完整信息置信度
- `testConfidenceWithOnlyTime()` - 不完整信息置信度

✅ **匹配文本（2 个测试）**
- `testMatchedText()` - 提取的文本验证
- `testNoMatchedText()` - 无法匹配的文本

---

## 查看测试报告

### HTML 报告（推荐）
测试完成后，自动生成详细的 HTML 报告：
```
app/build/reports/tests/testDebugUnitTest/index.html
```

用浏览器打开这个文件，可以看到：
- ✅ 通过的测试数
- ❌ 失败的测试数
- ⏱️ 单个测试的耗时
- 📊 测试覆盖统计

### 控制台输出
测试运行时，`Run` 窗口显示实时输出：
```
TimeParserTest:
  ✓ testParseToday() [15ms]
  ✓ testParseTomorrow() [8ms]
  ✓ testParseMonthDay() [12ms]
  ...
  
PASSED (32 tests)
```

---

## 调试失败的测试

### 在 Android Studio 中调试

1. **设置断点**
   - 在测试方法中，点击行号处添加断点

2. **Debug 运行**
   - 右键点击测试方法
   - 选择 `Debug 'testXXX()'`
   - 或按 `Ctrl + Shift + F9`（Windows）

3. **检查变量**
   - Debug 窗口显示变量值
   - 单步执行查看逻辑流

### 常见失败原因

| 错误信息 | 原因 | 解决方案 |
|---------|------|--------|
| `AssertionError: expected null but was <value>` | 期望为 null 但获得了值 | 检查解析逻辑是否正确 |
| `AssertionError: false` | 条件判断失败 | 查看具体的 assertTrue/assertFalse 条件 |
| `TimeParseResult cannot be found` | 缺少必要的类 | 确保 TimeParser.kt 和相关类存在 |

---

## 添加新的测试

1. 在 `app/src/test/java/com/example/reminderassistant/parser/` 创建新的 `*Test.kt` 文件

2. 继承基本框架：
```kotlin
class NewFeatureTest {
    @Before
    fun setUp() {
        // 测试前的初始化
    }

    @Test
    fun testSomething() {
        // 测试逻辑
        assertEquals(expected, actual)
    }
}
```

3. 使用常用的 assert 方法：
   - `assertEquals(expected, actual)` - 相等判断
   - `assertTrue(condition)` - 真值判断
   - `assertFalse(condition)` - 假值判断
   - `assertNull(value)` - null 判断
   - `assertNotNull(value)` - 非 null 判断

4. 运行新测试：右键 → `Run`

---

## 常用命令

```bash
# 运行所有单元测试
./gradlew testDebugUnitTest

# 运行特定的测试类
./gradlew testDebugUnitTest --tests=*TimeParserTest

# 运行特定的测试方法
./gradlew testDebugUnitTest --tests=*TimeParserTest.testParseToday

# 清除测试缓存并重新运行
./gradlew clean testDebugUnitTest

# 查看详细输出
./gradlew testDebugUnitTest --info

# 生成覆盖率报告（需要依赖配置）
./gradlew testDebugUnitTestCoverage
```

---

## 持续集成建议

如果使用 Git Hooks，可以在提交前自动运行测试：

1. 在 `.git/hooks/pre-commit` 添加：
```bash
#!/bin/bash
./gradlew testDebugUnitTest
if [ $? -ne 0 ]; then
    echo "Tests failed, aborting commit"
    exit 1
fi
```

2. 使脚本可执行：
   ```bash
   chmod +x .git/hooks/pre-commit
   ```

---

## 故障排除

### 问题：测试无法找到 TimeParser
**解决**：确保项目进行了 Gradle 同步
- Android Studio：`File` → `Sync Now`
- 或运行：`./gradlew build`

### 问题：在 Android Studio 中看不到运行按钮
**解决**：设置测试框架配置
1. `File` → `Settings` → `Languages & Frameworks` → `Kotlin`
2. 确保选中 `JUnit` 作为测试框架

### 问题：构建失败
**解决**：
1. 清除缓存：`./gradlew clean`
2. 重新构建：`./gradlew build`
3. 检查 SDK 版本是否正确

---

## 下一步

✅ 测试框架已准备好  
📝 添加更多测试用例（可选）  
🚀 定期运行测试以确保代码质量  
📊 查看覆盖率报告改进测试覆盖
