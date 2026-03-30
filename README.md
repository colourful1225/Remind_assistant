# Global Reminder Assistant

一款 Android 应用，帮助用户从任意文本中快速创建提醒或系统日历事件。

🌍 **项目语言**: Kotlin  
⚙️ **UI 框架**: Jetpack Compose  
🏗️ **架构模式**: MVVM + Clean Architecture  
💾 **数据层**: Room  
🔧 **依赖注入**: Hilt  

---

## 项目概览

### 核心价值
- 将分散在不同 App 的时间/待办信息快速统一为提醒或日历事件  
- 让用户在 **3 步内完成导入与确认**  
- 兼顾系统日历/通知能力与轻量提示入口

### 当前完成情况
- ✅ Milestone 1：工程骨架、页面与基础架构  
- ✅ Milestone 2：分享文本导入、解析、确认流程  
- ✅ Milestone 3：提醒调度与通知  
- ✅ Milestone 4：系统日历集成与表单完善  
- ✅ Milestone 5：剪贴板建议与冷却策略  
- ✅ Milestone 6：辅助功能入口 PoC

---

## 功能一览

### 已实现
- 分享/剪贴板文本导入  
- 提醒创建与本地调度  
- 系统日历事件创建（含时间/全天/地点等字段）  
- 剪贴板建议卡片与冷却策略  
- 辅助功能入口（Accessibility Service PoC）  
- 设置页统一管理开关与状态展示

### 规划中
- OCR 与截图识别  
- 厂商备忘录互通  
- 更丰富的解析策略与智能建议

---

## 运行思路（整体流程）

1. **入口触发**  
   - 分享文本（系统分享面板）  
   - 剪贴板建议（检测到像时间/事项的文本）  
   - 辅助功能入口（从当前界面抓取文本）
2. **解析文本**  
   - 识别标题、时间、地点、类型（提醒/日历）
3. **确认与编辑**  
   - 进入 ImportConfirm / ReminderEditor / CalendarEditor  
   - 用户检查并补充字段
4. **保存与落地**  
   - 提醒：写入本地 + 通知调度  
   - 日历：调用系统日历写入事件

---

## 使用方法

### 1) 分享文本导入
1. 在任意 App 选中文本 → 分享  
2. 选择 **Global Reminder Assistant**  
3. 确认解析结果 → 保存为提醒或日历事件

### 2) 剪贴板建议
1. 设置页开启 “剪贴板建议”  
2. 复制包含时间/事项的文本  
3. 出现建议卡片 → 一键导入/编辑

### 3) 辅助功能入口（PoC）
1. 设置页开启“辅助功能服务”  
2. 从当前界面抓取文本并进入导入流程  
3. 确认并保存

### 4) 权限说明
- 通知权限（Android 13+）  
- 日历写入权限（系统日历）  
> 权限在首次触发相关功能时请求

---

## 快速开始

### 环境要求
- Android SDK 26+  
- Android Studio 2022.1+  
- Gradle 8.2.0+  
- Kotlin 1.9.20+

### 运行步骤

```bash
# 1. Android Studio 打开项目
File > Open > Remind_assistant

# 2. Gradle 同步完成后运行
Shift + F10 (Windows/Linux) 或 Control + R (Mac)
```

### 常见问题（本地环境）

- 若构建报 SDK 组件缺失，请在 SDK Manager 安装：  
  - `platforms;android-34`  
  - `build-tools;34.0.0`
- 若提示许可证未接受，请执行：  
  - `sdkmanager --licenses`

---

## 项目结构

```
Remind_assistant/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/reminderassistant/
│   │   ├── res/values/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── docx/                                 # 需求与里程碑文档
├── gradle/
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

---

## 关键页面与能力
- **HomeScreen**：提醒列表与快捷入口  
- **ImportConfirmScreen**：文本解析结果确认  
- **ReminderEditorScreen**：提醒标题/备注/时间  
- **CalendarEditorScreen**：系统日历事件创建  
- **SettingsScreen**：剪贴板、辅助功能、权限与状态

---

## 开发规范
- 逻辑在 ViewModel / UseCase 中，UI 只负责展示  
- 系统能力统一通过接口抽象  
- 状态流使用 `StateFlow` / `Flow`

---

## 里程碑文档
详见 `docx/` 目录中的需求与里程碑说明文件。

---

**最后更新**: 2026-03-31  
**项目版本**: 1.0.0 (Milestone 6)  
**开发状态**: 🟢 稳定推进
