# ADR-0004：用于双击锁屏的用途受限 AccessibilityService 边界

## 状态

- 值：`Active`
- 更新日期：2026-08-16
- 依据：作者接受 debug-only 实现路径的物理设备验证及记录于下的边界。本 ADR 及支持的迭代证据建立了主线集成所需的约束。

## 日期

2026-08-16

## 背景

迭代 10 交付可选的双击锁屏能力，允许用户双击 Home 上符合条件的空白位置来锁定 Android 屏幕。该实现使用 Android 的 `AccessibilityService` API，在用户通过系统设置明确启用服务并查看本地 Privacy 和显著披露后，请求一次 `GLOBAL_ACTION_LOCK_SCREEN` 操作。

无障碍服务是具有重大隐私、安全和平台政策影响的特权平台能力。商店和平台审核对此类服务的使用方式、使用时机和使用原因施加严格约束。当前产品契约明确将服务限制于单一用途和边界。

## 决策

Avenor 的无障碍服务仅用于支持双击锁屏。其边界如下：

- **唯一用途**：在用户双击符合条件的 Home 基础信息空白位置后，执行一次 `GLOBAL_ACTION_LOCK_SCREEN` 操作。
- **Manifest 声明**：服务使用请求全局操作所需的最小 `accessibilityService` XML 配置。不声明 `flagRequestFilterKeyEvents`、`flagIncludeNotImportantViews`、`flagReportViewIds`、`flagRetrieveInteractiveWindows` 或任何提供窗口内容检索的能力。
- **权限**：服务不请求最小 `AccessibilityService` 声明以外的任何 Android 权限。不请求 `BIND_ACCESSIBILITY_SERVICE`、`SYSTEM_ALERT_WINDOW` 或任何设备管理权限。
- **数据访问**：服务不读取或保留任何窗口内容、屏幕内容、无障碍事件或其他应用数据。debug 实现的 `AccessibilityLockProbeService` 确认其忽略 `onAccessibilityEvent`、`onInterrupt` 和除 `onServiceConnected`、`onUnbind`、`onDestroy` 以外的所有其他事件回调。
- **全局操作**：服务仅在用户双击时请求 `GLOBAL_ACTION_LOCK_SCREEN`。不执行任何其他全局操作、后台自动化或持续监控。
- **安全失败行为**：如果服务被禁用、撤销、断连，或锁屏操作不可用或被拒绝，该能力静默不可用。所有独立 Launcher 路径（Home、Drawer、应用启动、Settings）保持不受影响。
- **用户控制**：服务保持禁用状态，直到用户在 Android 无障碍设置中明确启用。用户可随时禁用而不影响独立 Launcher 功能。不存在应用内开关；控制通过平台设置路径进行。
- **披露**：用户启用服务前，Settings 展示当前本地 Privacy 描述和独立显著披露，说明服务唯一用途、数据访问边界和关闭路径。披露使用 `取消` 和 `同意并继续` 选项，不保留已确认历史。
- **Home 手势边界**：双击识别仅在 `editMode` 为 `false` 时在基础信息区符合条件的空白位置启用。时间、日期、收藏、编辑表面和所有其他交互目标从双击锁屏检测中排除。
- **连接模型**：`AccessibilityLockConnection` 是仅 debug 的应用-服务连接点。不拥有任何 `Context`、事件或窗口数据，也不在应用进程外持久化状态。debug `AccessibilityLockProbeService` 在 `onServiceConnected` 时调用 `AccessibilityLockConnection.connected(this)`，在 `onUnbind`/`onDestroy` 时调用 `disconnected(this)`。
- **生产集成**：当此边界满足且服务集成到主线时，实现必须保留相同限制，不得向 release 构建暴露相同的 debug-only 连接模型。

## 理由

此边界确保无障碍服务是一个狭窄的、用户控制的工具，按需执行仅一个操作。它使服务用途、权限和数据访问行为保持透明和可审查。安全失败设计保证任何服务状态变更都不会破坏核心 Launcher 功能。

使用平台的无障碍设置和披露系统保持用户授权明确且平台原生。当前产品 Privacy 声明和显著披露正文提供了服务做什么和不做什么的契约。

连接模型使服务保持无状态，不引入持久后台桥接或共享数据存储，避免需要更广泛的架构审查。

## 考虑的选项

### 使用设备管理员 API

- 优势：`DevicePolicyManager.lockNow()` 是 Android 中程序化锁屏的既定机制。
- 权衡：需要 `DEVICE_ADMIN` 权限、设备管理注册和通过专门系统路径撤销。平台政策限制其使用，商店可能审查或拒绝非企业用途。当前产品不需要其更广泛的管理能力。

### 添加 Home 覆盖层并手动管理触摸

- 优势：完全避免无障碍服务权限和平台政策边界。
- 权衡：需要系统覆盖权限、复杂手势处理，且在其他覆盖层激活时不可用。还可能干扰 Android 原生手势系统，且不提供更清晰的授权故事。

### 使用用途受限的 AccessibilityService（已选择）

- 优势：使用平台明确的授权和设置路径，仅需一个最小权限，执行所需操作而不进行持续监控。当前产品边界和 Privacy 正文围绕此方法编写。
- 权衡：平台政策和商店可能审查其使用，且服务必须由用户明确启用。边界必须在代码、披露和验证中保持，以满足可接受的平台约束。

## 后果

- 不得通过新的活跃 ADR 将无障碍服务扩展为读取窗口内容、观察其他应用、收集无障碍事件、执行后台自动化或添加任何其他全局操作。
- 即使 GitHub 分发的 debug 构建已验收，商店分发仍需要重新审核平台政策和披露。
- 服务必须保持可选，且不得成为任何独立 Launcher 路径的要求。
- `android:accessibilityService` XML 不得扩展以请求超出当前边界所需的能力。
- 未来平台兼容性测试必须验证服务继续仅请求 `GLOBAL_ACTION_LOCK_SCREEN`，且在 API 31–37 之间保持安全失败行为。
- 集成到主线时，服务必须使用相同的 `AccessibilityLockProbeService` 边界，但包和类解析必须以相同的 manifest 约束从 debug 移至 main。

## 验证证据和缺口

- 在作者主要设备上的物理设备验证确认，当服务启用时，Home 上符合条件的空白位置的有效双击请求一次锁屏操作，且撤销或禁用服务后独立 Launcher 路径不受影响。
- Debug 实现确认 `onAccessibilityEvent` 和 `onInterrupt` 为空操作，仅请求 `GLOBAL_ACTION_LOCK_SCREEN`，且连接模型不拥有持久数据。
- Settings UI、Privacy 呈现和显著披露已实现并观察到显示当前本地正文。
- debug 实现的当前 manifest 和 `accessibilityService` XML 确认未声明任何窗口内容、按键事件或扩展视图能力。
- API 31 和另一台 API 36 或 API 37 物理设备覆盖仍为建议的兼容性证据。未执行的 OEM 和 Private Space 场景本身不使本决定无效，但必须在版本记录中记录为 `Unknown`、`Not run` 或 `Unavailable`。

## 实现说明

- Debug 实现使用 `src/debug` 中的 `AccessibilityLockProbeService` 验证边界。移至主线时，必须保留相同的服务类和 manifest 声明，并保持相同的 `accessibilityService` XML 约束。
- `AccessibilityLockConnection` 是一个仅 debug 的单例，提供无状态 `LockRequestPort` 适配器。它不跨进程重启持久化，且不得转换为后台服务或持久桥接。
- 双击手势使用 `detectTapGestures(onDoubleTap = ...)`，并将 `pointerInput` 限定在符合条件的 Home 空白位置，且仅当 `!editMode`。手势不得移动到更广泛的 Home 表面修饰符。

## 实现和验证证据

- 迭代 10 记录：`docs/delivery/1.1.0/iteration-10-double-tap-lock.md`
- 物理设备验证：作者报告主要设备上成功的双击锁屏行为、Settings 状态刷新、披露流程和撤销/安全失败行为
- Debug 实现文件：`app/src/debug/java/com/avenor/launcher/AccessibilityLockProbeService.kt`、`app/src/main/java/com/avenor/launcher/AccessibilityLock.kt`
- Settings 集成：`SettingsScreen.kt`、`SettingsPlatform.kt` 以及相关的 Privacy 和披露资源
- Home 手势集成：`HomeScreen.kt` 中符合条件的空白位置内的双击检测

## 替换

无

## 停用

- 日期：处于 `Active` 状态时不适用
- 原因：处于 `Active` 状态时不适用
- 替换为：无
- 后果：无