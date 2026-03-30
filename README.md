# Global Reminder Assistant

一个 Android 应用，帮助用户从任意文本信息中快速创建提醒或日历事件。

🌍 **项目语言**: Kotlin  
⚙️ **UI 框架**: Jetpack Compose  
🏗️ **架构模式**: MVVM + Clean Architecture  
💾 **数据库**: Room  
🔧 **依赖注入**: Hilt  

---

## 项目概述

### 核心价值

本项目解决的问题：
- 快递通知、课程提醒、会议安排等信息散落在不同 app
- 用户需要手动复制粘贴到提醒或日历 app
- 目标：提供统一的、快速的提醒入口，**最高 3 步内完成转化**

### 产品目标（一期）

✅ 导入文本提醒  
✅ 导入系统日历事件  
✅ 本地提醒调度  
✅ 轻量建议入口（剪贴板）  

### 后续目标（二期+）

⏳ 辅助功能增强（长按快捷入口）  
⏳ 厂商备忘录对接  
⏳ OCR 与截图识别  

---

## 功能清单

### Milestone 1 ✅ 已完成

- ✅ 工程骨架
- ✅ MVVM 架构
- ✅ Room 数据库
- ✅ 5 个页面（首页、导入、提醒编辑、日历编辑、设置）
- ✅ 导航系统
- ✅ 基础 UI

### Milestone 2 🔄 计划中

- 🔄 分享文本接收
- 🔄 文本解析引擎
- 🔄 导入确认页面

### Milestone 3-6 📋 待开发

- 📋 Milestone 3: 提醒通知调度
- 📋 Milestone 4: 系统日历集成
- 📋 Milestone 5: 剪贴板建议
- 📋 Milestone 6: 辅助功能增强

---

## 快速开始

### 环境要求

- Android SDK 26+
- Android Studio 2022.1 或更新
- Gradle 8.2.0+
- Kotlin 1.9.20+

### 编译运行

```bash
# 1. 在 Android Studio 中打开项目
File > Open > Remind_assistant

# 2. 等待 Gradle 同步
# (若遇到问题，尝试)
# Android Studio > File > Invalidate Caches > Invalidate and Restart

# 3. 运行应用
Shift + F10 (Windows/Linux) 或 Control + R (Mac)

# 4. 选择运行目标
# 模拟器或已连接的真机
```

### 验证运行

- 应用启动进入首页
- 首页显示空列表（初次运行）
- 点击 "New Reminder" 按钮进入编辑页面
- 输入标题和备注，点击 Save
- 返回首页，列表应显示新提醒

---

## 项目结构

```
Remind_assistant/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/reminderassistant/
│   │   │   ├── ReminderAssistantApp.kt          # Hilt Application
│   │   │   ├── MainActivity.kt
│   │   │   ├── di/                              # Dependency Injection
│   │   │   │   ├── AppModule.kt
│   │   │   │   └── DatabaseModule.kt
│   │   │   ├── navigation/                      # Navigation
│   │   │   │   ├── Routes.kt
│   │   │   │   └── AppNavGraph.kt
│   │   │   ├── ui/                              # Presentation Layer
│   │   │   │   ├── home/
│   │   │   │   ├── importflow/
│   │   │   │   ├── reminder/
│   │   │   │   ├── calendar/
│   │   │   │   ├── settings/
│   │   │   │   └── components/
│   │   │   ├── domain/                          # Domain Layer
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── usecase/
│   │   │   ├── data/                            # Data Layer
│   │   │   │   ├── local/
│   │   │   │   ├── repository/
│   │   │   │   └── mapper/
│   │   │   └── preview/
│   │   ├── res/values/
│   │   │   ├── strings.xml
│   │   │   ├── colors.xml
│   │   │   └── themes.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── gradle.properties
├── local.properties
└── docx/                                         # 开发文档
    ├── 总体方向指导方针
    ├── Global Reminder Assistant - Master Roadmap.md
    ├── Milestone_1_Implementation_Summary.md
    ├── Milestone2, Milestone3, ...
```

---

## 架构设计

### 三层架构

```
┌─────────────────────────────────────────┐
│     Presentation Layer (UI)             │
│  ├─ Screen (Compose)                    │
│  ├─ ViewModel                           │
│  └─ UiState                             │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│     Domain Layer (Business Logic)       │
│  ├─ Model                               │
│  ├─ Repository (Interface)              │
│  └─ UseCase                             │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│     Data Layer (Storage & System)       │
│  ├─ Entity & DAO (Room)                 │
│  ├─ Repository (Implementation)         │
│  └─ Mapper                              │
└─────────────────────────────────────────┘
```

### 核心模型

- **ReminderItem**：提醒项目
- **CalendarDraft**：日历事件草稿
- **ParsedContent**：解析的文本内容
- **SourceContext**：来源上下文

### 关键 UseCase

- GetAllRemindersUseCase
- CreateReminderUseCase
- UpdateReminderUseCase
- DeleteReminderUseCase

---

## 技术栈

| 组件 | 版本 | 用途 |
|------|------|------|
| Kotlin | 1.9.20+ | 编程语言 |
| Jetpack Compose | 2024.01+ | UI 框架 |
| Navigation Compose | 2.7.7+ | 路由导航 |
| Room | 2.6.1+ | 本地数据库 |
| Hilt | 2.49+ | 依赖注入 |
| Coroutines | 1.7.3+ | 异步处理 |
| Material 3 | - | 设计系统 |

---

## 页面说明

### 🏠 HomeScreen

- 显示所有提醒列表
- 提醒卡片显示：标题、备注、提醒时间
- 空列表占位文本
- 导航按钮和浮动按钮

### 📥 ImportConfirmScreen

- 展示原始文本
- 显示解析结果建议
- 用户可选择创建待办或日历事件
- **当前状态**：骨架阶段，等待 Milestone 2 实现

### ✏️ ReminderEditorScreen

- 标题输入（必填）
- 备注输入（可选）
- 时间设置（待实现）
- 保存和取消按钮
- 已连接数据库，能实际保存数据

### 📅 CalendarEditorScreen

- 事件标题输入
- 地点输入
- 描述输入
- 时间设置占位（等待 Milestone 4）

### ⚙️ SettingsScreen

- 剪贴板建议开关
- 辅助功能服务开关
- 显示来源 app 开关
- 本轮支持状态管理，实际功能待实现

---

## 开发规范

### 代码风格

- 使用 Kotlin 和 Compose
- 遵循 Kotlin 官方编码规范
- 每个文件只承担一个职责
- 使用 `StateFlow` 和 `Flow` 处理异步

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| Screen | `XxxScreen.kt` | `HomeScreen.kt` |
| ViewModel | `XxxViewModel.kt` | `HomeViewModel.kt` |
| UiState | `XxxUiState.kt` | `HomeUiState.kt` |
| UseCase | `VerbNounUseCase.kt` | `GetAllRemindersUseCase.kt` |
| Route | Routes.CONSTANT | Routes.HOME |

### 架构原则

- ❌ 不在 Composable 中处理业务逻辑
- ❌ 不直接在 UI 层调用数据库
- ✅ 所有系统能力通过接口围绕
- ✅ Repository 作为数据来源的唯一入口

---

## 常见问题

### Q: 如何在首页保存新提醒？

A: 
1. 点击首页浮动按钮或 "New Reminder" 按钮
2. 进入 ReminderEditorScreen
3. 输入标题（必填）和备注
4. 点击 Save
5. 自动返回首页，列表将显示新提醒

### Q: 目前支持分享文本到应用吗？

A: 目前不支持（待 Milestone 2 实现）。当前版本只支持应用内手动创建提醒。

### Q: 如何切换深色主题？

A: 当前版本只实现了浅色主题。深色主题支持计划在后续版本添加。

### Q: 能否修改已保存的提醒？

A: 当前版本不支持编辑已保存的提醒（待 Milestone 3+ 实现）。

---

## 贡献指南

### 开发规范

1. **创建特性分支**
   ```bash
   git checkout -b feature/milestone-2-text-parsing
   ```

2. **遵循代码规范**
   - 类职责单一
   - 命名清晰明确
   - 添加必要注释

3. **测试验证**
   - 编译通过
   - 基本功能正常
   - 无 Lint 警告

4. **提交信息**
   ```
   [Milestone 2] feat: 实现文本解析引擎
   
   - 添加 TextParser 和相关子模块
   - 支持时间、地点、标题识别
   - 集成到导入流程
   ```

### 报告问题

如遇到问题，请在项目根目录的 `Issues` 板块提交，包含：
- 问题描述
- 复现步骤
- 预期行为 vs 实际行为
- 环境信息（Android 版本、设备等）

---

## 下一步

### Milestone 2 - 文本导入与解析

**目标**：实现分享文本进入应用的完整流程

**核心任务**：
1. 分享接收（ACTION_SEND, ACTION_PROCESS_TEXT）
2. 文本解析引擎（时间、地点、标题、类型识别）
3. 导入确认页面联动

**完成后能做到**：
- 从短信/浏览器分享文本到应用
- 自动识别标题、时间、地点
- 用户只需确认即可保存

---

## 许可证

该项目暂无许可证限制。仅供学习和开发参考。

---

## 联系方式

有任何问题或建议，欢迎在项目文档中提出。

---

**最后更新**: 2026年3月30日  
**项目版本**: 1.0.0 (Milestone 1)  
**开发状态**: 🟢 稳定运行
