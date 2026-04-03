# APK 打包和安装指南

## 📦 快速打包

### 最简单的方式 - 双击脚本

#### **只打包（生成APK文件）**
```
双击 build_apk.bat
```
- 清除旧文件
- 构建 Debug APK
- 自动打开APK所在目录

#### **打包 + 自动安装**
```
双击 install_app.bat
```
- 构建 Debug APK  
- 自动安装到已连接的手机/模拟器
- 无需手动操作

---

## 🏗️ Android Studio 方式

### 步骤 1：打开 Build 菜单
```
顶部菜单 → Build → Build Bundle(s) / APK → Build APK(s)
```

### 步骤 2：等待构建
- 底部进度条显示进度
- 看到 `✓ Gradle build finished` 表示完成

### 步骤 3：找到 APK
- 自动弹窗，点击 `locate`
- 或在以下位置找到：
  ```
  app/build/outputs/apk/debug/app-debug.apk
  ```

### 步骤 4：安装到设备
**方式 A：直接安装**
- 连接手机（USB 调试）
- 双击 APK 文件
- 或 `Run` → `Run 'app'`

**方式 B：通过 Android Studio**
- `Run` → `Run 'app'` （Shift + F10）
- 选择目标设备
- 自动安装并运行

---

## 💻 命令行方式

### 打包 Debug APK
```bash
./gradlew assembleDebug
```
✅ 输出：`app/build/outputs/apk/debug/app-debug.apk`

### 打包 + 自动安装
```bash
./gradlew installDebug
```
前提：手机已连接并启用 USB 调试

### 立即运行应用
```bash
./gradlew installDebug
adb shell am start -n com.example.reminderassistant/.MainActivity
```

### 清除后重新打包
```bash
./gradlew clean assembleDebug
```

---

## 🔍 APK 信息

### 文件位置
```
app/build/outputs/apk/debug/app-debug.apk
```

### 文件大小
- 当前项目：约 16 MB
- 包含内容：
  - Jetpack Compose UI 框架
  - Room 数据库
  - Hilt 依赖注入
  - Accessibility Service
  - 所有资源和翻译

### 签名信息
- Debug APK：使用默认调试签名
- 可以直接安装到开发设备
- 不能上传到 Google Play

---

## 📱 设备要求

### 最低配置
- Android 8.0+ (API 26+)
- 100 MB 可用空间
- 启用 USB 调试（仅用于安装）

### 推荐配置
- Android 10+ (API 29+)
- 最新 Google Play 服务

---

## ✅ 安装检查清单

### 准备工作
- [ ] 项目成功构建（`BUILD SUCCESSFUL`）
- [ ] 手机/模拟器已连接
- [ ] USB 调试已启用（手机需要）
- [ ] 足够的存储空间（>100MB）

### 安装步骤
- [ ] APK 文件存在于：`app/build/outputs/apk/debug/`
- [ ] APK 文件大小正常（>10MB）
- [ ] 点击安装或运行脚本
- [ ] 等待安装完成

### 安装后
- [ ] 在手机上找到 "Global Reminder Assistant"
- [ ] 成功打开应用
- [ ] 可以测试功能

---

## 🐛 常见问题

### Q1：安装失败 - "设备未找到"
**A：** 
- 检查数据线是否连接
- 手机是否启用 USB 调试
- 运行 `adb devices` 检查连接
- 重新插拔 USB 线

### Q2：安装失败 - "应用已存在"
**A：**
- 先卸载旧版本：`adb uninstall com.example.reminderassistant`
- 或在手机设置中手动卸载
- 然后重新安装

### Q3：构建失败 - Gradle 错误
**A：**
- 清除缓存：`./gradlew clean`
- 重新同步：`File` → `Sync Now`
- 检查 SDK 版本是否正确

### Q4：APK 是否可以分享给他人？
**A：**
- ✅ 可以分享 APK 文件
- ✅ 他人可以直接安装使用
- ⚠️ 但这是 Debug 版本（包含调试信息，文件较大）
- 📦 正式发布应使用 Release 版本

### Q5：如何生成可发布的 Release 版本？
**A：**
- Release 版本需要签名配置
- 需要创建密钥库（keystore）
- 更详细的步骤请查看 `RELEASE_GUIDE.md`（如果存在）

---

## 📊 构建输出结构

```
app/build/outputs/
├── apk/
│   └── debug/
│       ├── app-debug.apk          ← 这是你要的文件
│       ├── output-metadata.json
│       └── ...
├── bundle/                         ← App Bundle（用于 Play Store）
├── reports/                        ← HTML 测试报告
└── ...
```

---

## 💡 提示

### 加速构建
```bash
# 并行构建任务
./gradlew assembleDebug -x test --parallel
```

### 增量构建
- 只改小部分代码时，构建速度会快得多
- Gradle 会缓存未变的部分

### 监控构建性能
```bash
./gradlew assembleDebug --profile
# 生成详细的性能报告
```

---

## 下一步

✅ **Debug APK 生成**  
📝 可选：配置 Release 签名  
🚀 可选：上传到 Google Play  
🧪 建议：在真机上测试应用功能  
