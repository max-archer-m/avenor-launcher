# 1.1.0 聚焦技术验证

> 英文语义源：[focused-technical-validation.md](focused-technical-validation.md)。
>
> 本文是 2026-08-15 所要求 Android 备份门禁与 ADR 前 AccessibilityService 探针的非权威实现证据记录。它不授权生产集成、不建立 ADR、不完成迭代或版本，也不声称未观察的设备行为。

## 范围与环境

- 仓库：`avenor-launcher`
- 主机：Windows PowerShell
- 工程基线：单 Android 应用模块，`minSdk` 31、`targetSdk` 36、`compileSdk` 37
- 已检查变体：当前源码 Manifest、全新生成的 debug merged Manifest，以及全新生成的 release main merged Manifest
- 物理设备：未使用
- 远程、签名、产物发布与 Git 操作：未执行

平台解释依据包括 [Android 自动备份指南](https://developer.android.com/identity/data/autobackup)、[Android 12 备份行为变更](https://developer.android.com/about/versions/12/behavior-changes-12)、[`AccessibilityService`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService.html)、[`AccessibilityServiceInfo`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo) 与 [`AccessibilityManager`](https://developer.android.com/reference/android/view/accessibility/AccessibilityManager)。

## Android 备份确认

### 已观察的持久化与配置

- `AtomicFileFavoriteStore(Context)` 将基础文件解析为 `context.filesDir/favorites.bin`。
- 存储通过 `AtomicFile.startWrite`、`finishWrite` 与 `failWrite` 写入带版本的二进制文档。AtomicFile 伴随文件仍处于相同内部 files 域。
- 源码 Manifest 明确设置 `android:allowBackup="false"` 和 `android:dataExtractionRules="@xml/data_extraction_rules"`。
- `data_extraction_rules.xml` 在 `cloud-backup` 和 `device-transfer` 下分别排除 `root`、`file`、`database`、`sharedpref` 与 `external` 的路径 `.`。
- 全新 debug 和 release main merged Manifest 均保留 `allowBackup=false` 与 `dataExtractionRules` 引用。
- 工程没有 `android:fullBackupContent` 规则。当前受支持运行范围不需要该规则，因为 `minSdk` 31 已排除 Android 11 及更低版本；面向 API 31 及以上且 target API 31 及以上的应用使用 `data-extraction-rules`。

### 评估

当前检入配置满足已记录 Android API 31–37 配置边界，可将 `filesDir/favorites.bin` 及同目录原子伴随文件排除在 Android 云备份和 Android 设备到设备迁移之外。`allowBackup=false` 作为云备份纵深控制保留，但不被视为足以控制 D2D，因为 Android 记录了 target API 31 及以上应用的 OEM 差异；明确的 `device-transfer` 排除才是 D2D 控制证据。

不需要改变持久化格式、数据边界、`minSdk` 或应用身份。现有备份规则源码没有被修改。

以上属于配置和 merged Manifest 证据，不是真实传输观察。未执行实际备份/恢复或设置向导迁移，因此 OEM 是否遵守及 transport 行为均为 `Not run`。未配置跨平台迁移，也不存在对应的非 Android 应用身份；本文不声称跨平台迁移结果。

### 新增回归检查

- `BackupConfigurationTest` 检查已打包应用的 `FLAG_ALLOW_BACKUP` 状态，并验证云备份与设备迁移均具有 file 域排除。
- `FavoriteStoreTest.contextStoreWritesFavoritesInsideTheExcludedFilesDirectory` 验证 Context 构造器把 `favorites.bin` 直接写到 `filesDir`。

这些 instrumentation 检查已经编写，但本次未能编译或运行，因为既有 `HomeScreenTest.kt` 存在无关的 `assertDoesNotExist` unresolved reference，阻断了完整 androidTest 源集。

## AccessibilityService 聚焦探针

### 探针隔离

探针仅存在于 `app/src/debug`。全新 `assembleDebug` 已将其打包；全新 `processReleaseMainManifest` 成功完成，并且 release merged Manifest 对探针服务、`BIND_ACCESSIBILITY_SERVICE` 和无障碍服务 intent 的匹配数均为零。因此探针未集成进 release/main 变体。

### 已有证据支持的边界

- debug 服务仅为 Android 系统绑定而 exported，并在服务组件上受 `android.permission.BIND_ACCESSIBILITY_SERVICE` 保护。
- intent filter 只包含 `android.accessibilityservice.AccessibilityService`，metadata 引用唯一无障碍服务 XML 资源。
- 元数据明确关闭窗口内容读取、手势执行、按键过滤、指纹手势和触摸探索。它只声明 generic feedback 以支持已启用服务枚举，不声明无障碍事件类型、package 过滤、flags、截图或其他能力。
- `onAccessibilityEvent` 与 `onInterrupt` 不执行产品工作，也不保存事件数据。
- 启用状态来自 `AccessibilityManager.getEnabledAccessibilityServiceList(FEEDBACK_ALL_MASK)`，并与精确 package/class `ComponentName` 比较。
- 连接状态由独立 volatile 进程内接缝持有，并在 unbind 或 destroy 时清除。因此系统已启用与当前已连接仍是不同事实。
- 面向应用的 port 只暴露 `requestLock()`。
- 服务断连时返回 `ServiceDisconnected`，不发出操作。
- 服务连接后先检查 `getSystemActions()` 是否包含 `GLOBAL_ACTION_LOCK_SCREEN`，再准确调用一次 `performGlobalAction`。缺少动作和平台返回 false 分别成为 `ActionUnavailable` 与 `ActionRejected`；不重试、不回退。
- 探针未增加网络服务、分析、监控、Device Administrator、持久化、披露确认、事件收集或后台触发操作。

### 未知运行时行为

由于未使用物理设备，下列项目均为 `Not run`：

- 在 OEM 无障碍设置中的展示与启用；
- 启用或撤销前后的精确启用状态查询；
- 服务连接、断连、进程终止和重连时序；
- 实际 `getSystemActions()` 内容；
- `GLOBAL_ACTION_LOCK_SCREEN` 成功或被拒绝的执行；
- OEM 拒绝和面向用户的安全失败行为；以及
- 手势、披露、Settings、Privacy 与本地化集成。

探针没有生产 UI 触发入口，并且有意不测试完整迭代 10 用户路径。

## ADR 输入

证据支持下列拟议 ADR 输入，但不支持建立 Active ADR：

- **唯一用途：** 契约规定的 Home 明确双击后，请求一次 Android 锁屏全局操作。
- **Manifest 边界：** 一个具有无障碍服务 intent、精确 metadata、为系统绑定设置 `exported=true` 并受组件级 `BIND_ACCESSIBILITY_SERVICE` 保护的服务。
- **禁止行为：** 窗口/内容读取、事件处理或保存、package 观察、手势、按键/指纹/触摸探索能力、截图、分析、网络处理、后台触发、无关全局操作和 Device Administrator 回退。
- **状态模型：** 精确组件启用状态来自 Android；当前连接为独立进程内状态；产品 On 要求两者同时成立。
- **应用接口：** 单一锁屏请求，返回已请求、断连、不可用或被拒绝；不提供通用全局操作接口。
- **失败模型：** 未授权、断连、动作不可用、平台返回 false、进程终止或 OEM 拒绝时安全关闭能力；不自动重试、不降低独立 Launcher 路径。
- **数据边界：** 不访问、保留、传输无障碍内容或事件数据，也不用于分析；不保存披露确认历史。
- **披露关系：** Privacy、说明与显著披露保持为独立本地展示；只有契约规定的继续操作可以发生在以启用为目的的系统跳转前。
- **兼容性：** 锁屏动作的加入版本低于 `minSdk` 31，但当前可用性与 OEM 行为需要设备证据。
- **重新审核触发条件：** 任何新增无障碍用途、能力、权限、事件处理、后台操作、数据处理、回退、分发渠道或商店发布。

目前建立 Active ADR 为时过早：主线集成、设备行为、验收、commit 和同步均不存在。运行时证据支持其余边界后，作者还需接受已实现取舍，项目再遵循 ADR 激活规则。

## 验证结果

| 检查 | 结果 | 证据 |
| --- | --- | --- |
| 源码备份配置检查 | `Passed` | 已检查 Manifest、数据提取规则和收藏存储路径 |
| 全新 debug merged Manifest 与打包 | `Passed` | `assembleDebug` 完成；探针和备份属性存在 |
| 全新 release main merged Manifest | `Passed` | `processReleaseMainManifest` 完成；备份属性存在且 debug 探针不存在 |
| Debug instrumentation test APK 编译 | `Failed` | 既有 `HomeScreenTest.kt` unresolved `assertDoesNotExist` 阻断 androidTest 编译 |
| 新增 instrumentation 测试执行 | `Not run` | Test APK 未编译且未使用设备 |
| Android lint | `Failed` | 当前 warnings-as-errors 策略下，既有 `OldTargetApi` 与 `NewerVersionAvailable` 被视为错误 |
| Connected/设备验证 | `Not run` | 未使用作者指定物理设备 |
| API 31 建议证据 | `Not run` | 未使用 API 31 环境 |
| 额外 API 36/37 物理设备 | `Not run` | 未使用额外设备 |
| 额外 OEM/profile 证据 | `Not run` | 未使用额外 OEM/profile 环境 |

首次组合 Gradle 尝试还受到 OneDrive 生成资源目录锁定的影响。只删除了经过验证的 `app/build` 输出；随后干净重试得到上述结果。

## 阻塞与后续发现

1. 已比较英文 Privacy 语义源与简体中文对应文件的联系信息和显著披露边界，未发现重要遗漏；已批准正文没有被修改。
2. 既有 androidTest 编译错误阻止新增备份与探针检查编译和运行。它应由所属实现任务处理，不能被隐藏为验证通过。
3. 未发现必须扩大权限、数据处理、后台行为、持久化、`minSdk` 或产品范围的需求。因此当前没有新的重要范围决策必须返回作者。
