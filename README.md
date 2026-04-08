# Remind Assistant

一款面向 Xiaomi/Android 的轻量提醒工具。  
核心目标是“尽量无感”：在跨应用场景下长按文本后，快速弹出提醒入口，并一键落到小米日历或小米笔记待办。

## 平台说明

由于作者当前主要使用小米手机，项目现阶段优先开发和适配小米生态（如小米日历、小米笔记待办、MIUI 权限与后台策略）。  
其他 Android 厂商系统的专项适配会在后续版本中陆续开发与补齐。

## 当前能力

- 跨应用长按文本触发（基于 Accessibility Service）
- 两段式交互：
  - 第一步显示小按钮 `提醒`
  - 第二步点击后展开底部选择面板（`小米日历` / `小米笔记待办` / `关闭`）
- 时间解析（支持数字时间与常见中文时间表达）
- 前台服务常驻与保活
- 通知栏快捷开关：
  - 直接点击通知本体切换“开启/关闭”
  - 右侧动作按钮也可切换

## 为什么使用无障碍

Android 10+ 对后台剪贴板读取限制较严格。  
如果目标是“跨应用全局触发 + 无感交互”，无障碍是当前最可行、最稳定的通用方案。

## 最小影响策略

本项目遵循“最小侵入”原则：

- 仅监听必要事件（长按、点击、选择态变化等）
- 仅在用户明确发生文本交互时触发
- 选择态消失后自动收起悬浮 UI
- 支持通知栏一键暂停，降低对敏感应用的影响

## 技术栈

- Kotlin 1.9.x
- Jetpack Compose
- Hilt
- Target SDK 34
- Min SDK 26

## 关键模块

- `ReminderAccessibilityService`
  - 跨应用事件监听
  - 文本提取与触发控制
- `AccessibilityOverlayController`
  - `提醒` 小按钮
  - 底部选择面板（圆角 + 半透明）
- `TimeParser`
  - 时间语义提取与时间戳解析
- `XiaomiIntentCaller`
  - 小米日历 / 小米笔记待办 Intent 跳转
- `ClipboardMonitorService`
  - 前台服务 + 通知栏快捷切换

## 本地运行

```bash
./gradlew assembleDebug
./gradlew installDebug
```

Release APK：

```bash
./gradlew assembleRelease
```

输出路径：

- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release.apk`（或 `app-release-unsigned.apk`，取决于签名配置）

## 真机调试建议

1. 打开无障碍服务（Remind Assistant）
2. 打开悬浮窗权限
3. 允许自启动 + 关闭电池优化限制
4. 通过通知栏确认状态为“已开启”

## 路线图（短期）

- 提升中文时间解析精度（复杂自然语句）
- 提升不同应用的文本提取稳定性
- 优化小米笔记待办自动填充成功率
