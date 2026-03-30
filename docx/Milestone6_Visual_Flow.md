# Milestone 6 可视化说明文档

以下为辅助功能入口 PoC 的可视化流程说明，便于团队理解整体链路与交互位置。

## 1. 总体流程（可视化流程图）

```mermaid
flowchart TD
    A[用户在目标 App 长按消息] --> B[AccessibilityService 接收事件]
    B --> C[AccessibilityEventRouter 过滤与节流]
    C --> D[AccessibilityNodeTextExtractor 提取文本]
    D --> E{是否满足候选条件}
    E -- 否 --> X[静默结束]
    E -- 是 --> F[DetectAccessibilityCandidateUseCase 评估]
    F --> G{高价值?}
    G -- 否 --> X
    G -- 是 --> H[展示轻量快捷入口 Overlay]
    H --> I[用户点击 Add Reminder / Add Calendar]
    I --> J[构建 ImportSession 并写入 ImportSessionStore]
    J --> K[启动 MainActivity]
    K --> L[AppNavGraph 跳转到目标页面]
```

## 2. 事件链路时序图

```mermaid
sequenceDiagram
    participant U as User
    participant T as Target App
    participant S as AccessibilityService
    participant R as EventRouter
    participant X as TextExtractor
    participant D as DetectUseCase
    participant O as Overlay
    participant A as App (MainActivity)
    participant N as AppNavGraph

    U->>T: 长按消息
    T-->>S: AccessibilityEvent
    S->>R: onEvent(event)
    R->>X: extractText(event, root)
    X-->>R: candidateText?
    R->>D: detect(candidate)
    D-->>R: suggestion / null
    R-->>S: suggestion
    S->>O: show quick actions
    U->>O: 点击提醒/日历
    O->>A: 启动 MainActivity
    A->>N: 读取 ImportSessionStore
    N-->>A: 跳转到目标页面
```

## 3. 关键模块与职责

- `ReminderAccessibilityService`：接收 Accessibility 事件并触发入口
- `AccessibilityEventRouter`：节流、目标应用识别、候选提取
- `AccessibilityNodeTextExtractor`：候选文本提取
- `DetectAccessibilityCandidateUseCase`：价值判断与冷却策略
- `AccessibilityOverlayController`：轻量入口 UI
- `AccessibilityImportBridge`：构建 ImportSession 并进入 App

