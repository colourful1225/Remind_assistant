# 🧪 Android 测试快速开始 - 3 步搞定

## 📌 现在已完成什么？

你的项目现在已经配置了**完整的企业级测试框架**：

### ✅ 新增测试依赖
```
✓ JUnit 4 - 基础测试框架
✓ Mockito - Mock 对象库
✓ Turbine - Flow 测试工具
✓ Robolectric - 轻量 Android 框架
✓ Hilt Testing - DI 测试支持
✓ Espresso - UI 测试框架
✓ Compose Testing - Compose 组件测试
```

### ✅ 新增测试文件示例
```
app/src/test/java/
├── parser/DefaultTextParserTest.kt              # 文本解析单元测试
└── domain/usecase/GetAllRemindersUseCaseTest.kt # UseCase 单元测试

app/src/androidTest/java/
├── TestHiltModules.kt                   # Hilt 测试配置
├── ReminderDatabaseTest.kt              # Room 数据库集成测试
├── HomeScreenTest.kt                    # UI 组件集成测试
└── ReminderRepositoryHiltTest.kt        # 带 Hilt DI 的集成测试
```

### ✅ 新增文档和脚本
```
TESTING_GUIDE.md              # 📚 完整的测试指南
TESTING_QUICK_REFERENCE.md    # 📋 快速参考卡片
run_tests.bat                 # 🚀 Windows 快速测试脚本
run_tests.sh                  # 🚀 macOS/Linux 快速测试脚本
```

---

## 🎯 快速开始（3 步）

### 第 1 步：打开 Android Studio 并同步依赖

```
File → Sync Now
或按 Ctrl+Alt+Y
```

等待同步完成（可能需要 2-5 分钟）。

![同步过程](https://docs.google.com/drawings/d/image)

### 第 2 步：运行第一个测试

**选项 A：直接在 Android Studio 中运行**（最简单）

1. 打开测试文件：`app/src/test/java/.../parser/DefaultTextParserTest.kt`
2. 在编辑器中，右键点击类名 `DefaultTextParserTest`
3. 选择 **Run 'DefaultTextParserTest'**
4. 等待结果显示

**或选项 B：使用快速脚本**

```bash
# Windows
run_tests.bat unit Parser

# macOS/Linux
bash run_tests.sh unit Parser
```

### 第 3 步：查看结果

✅ **成功** - 会在 Run 窗口显示绿色 PASSED

❌ **失败** - 会显示红色 FAILED 和错误详情

---

## 📊 测试矩阵图

```
┌─────────────────────────┬──────────────────┬──────────────────┐
│ 测试类型 │ 运行速度 │ 环境要求 │
├─────────────────────────┼──────────────────┼──────────────────┤
│ 单元测试（src/test）  │ ⚡⚡⚡ 快速 │ JVM 仅 │
│ UI 测试（src/androidTest） │ ⚡ 慢 │ 模拟器/真机 │
│ 数据库测试 │ ⚡⚡ 中等 │ 内存 DB │
└─────────────────────────┴──────────────────┴──────────────────┘
```

---

## 🚀 常用命令速查

### 运行测试
```bash
# 【快】所有单元测试
./gradlew test

# 【中】特定测试
./gradlew test --tests "*Parser*"

# 【慢】UI 测试（需要模拟器）
./gradlew connectedAndroidTest

# 【完】所有测试 + 覆盖率
./gradlew test connectedAndroidTest jacocoTestReport
```

### 查看报告
```bash
# Windows
start app\build\reports\tests\testDebugUnitTest\index.html

# macOS
open app/build/reports/tests/testDebugUnitTest/index.html
```

---

## 📝 编写新测试的最快方法

### 1️⃣ 创建单元测试文件

在 `app/src/test/java/com/example/reminderassistant/` 下创建：

```kotlin
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class MyComponentTest {
    @Before
    fun setup() {
        // 初始化
    }
    
    @Test
    fun testSomething() {
        // Arrange-Act-Assert
        val result = compute()
        assertEquals("expected", result)
    }
}
```

### 2️⃣ 右键 Run → 看结果

就这么简单！✨

---

## 💡 实用技巧

### 快捷键加速
- **Ctrl+Shift+F10** - 运行当前测试
- **Ctrl+Shift+D** - Debug 当前测试
- **Ctrl+Shift+A** - 快速搜索命令

### Mock 对象快速模式
```kotlin
// ✅ 快速 Mock
val mockRepo = mock<Repository>()
whenever(mockRepo.get()).thenReturn(data)

// ✅ 快速断言
assertEquals(expected, actual)
assertTrue(condition)
```

### 异步测试等待
```kotlin
@Test
fun testAsync() = runBlocking {
    // Flow 会自动等待
    val result = useCase().first()
}
```

---

## 🎓 学习路径

### 初级（立即可用）
1. ✅ 看这个文档
2. ✅ 运行现有的测试示例
3. ✅ 为自己的代码写简单的单元测试

**预计时间：30 分钟**

### 中级（进阶）
1. 学会 Mock 对象（Mockito）
2. 学会编写 UI 测试（Espresso/Compose)
3. 学会测试异步代码（Flow）

**推荐教程**：[Android Testing Codelabs](https://developer.android.com/codelabs)

### 高级（可选）
1. 参数化测试
2. 测试覆盖率优化
3. CI/CD 集成

---

## ✅ 检查清单

在开始测试前，确认：

- [ ] Android Studio 已同步 Gradle
- [ ] 模拟器已启动（UI 测试需要）
- [ ] 看过 `TESTING_QUICK_REFERENCE.md`
- [ ] 运行过至少一个测试例子
- [ ] 理解了 Arrange-Act-Assert 模式

---

## 🆘 遇到问题？

### 问题 1：找不到测试类
```
→ File → Invalidate Caches → Restart
```

### 问题 2：模拟器找不到
```
→ adb devices
→ 在 Android Studio 中重启模拟器
```

### 问题 3：Hilt 注入失败
```
→ 确保类有 @HiltAndroidTest 注解
→ 确保有 @get:Rule val hiltRule = HiltAndroidRule(this)
```

更多帮助请看 `TESTING_GUIDE.md` 的「常见问题」部分。

---

## 🎉 下一步

1. **现在就试试** - 运行一个现有的测试
2. **写一个测试** - 为你的代码写一个单元测试
3. **优化覆盖率** - 逐步提高测试覆盖率到 70%+

---

**准备好了吗？打开 Android Studio，试试这条命令：**

```bash
./gradlew test --info
```

或在 Android Studio 中右键测试文件，选择 Run！

祝你测试愉快！🚀
