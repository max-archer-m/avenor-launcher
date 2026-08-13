# Avenor Launcher 1.0.0 技术评估

> 英文语义源：[technical-assessment.md](technical-assessment.md)。本文件是其中文对应版本。
>
> 本评估判断已批准的 [1.0.0 产品范围](product-scope.zh-CN.md)是否可行以及交付受到哪些约束。它不改变产品范围，不授权实现或发布活动，也不把技术建议变成已批准的长期架构决定。项目作者保留决定权。

## 评估问题

能否在已记录的 API 31 至 API 37 设备范围内，将选定的 `1.0.0` 用户旅程交付为一个可维护的 Android 主屏幕应用，并采用最小权限、本地优先、能够支持未来三至五年演进的架构，同时不把延后的产品行为带入首个版本？

本评估认为该范围在技术上可行。在完成建议验证前，跨设备、可复现性和实测性能方面的信心仍受证据限制。后续交付契约决定哪些证据是 `1.0.0` 完成所必需的，哪些继续作为未来正式发布制品的建议证据。

## 输入与证据

### 产品和项目输入

- [产品基础需求](../../requirements/product-foundation.zh-CN.md)
- [产品导航](../../product/navigation.zh-CN.md)
- [Home 交互](../../product/home.zh-CN.md)
- [Drawer 交互](../../product/drawer.zh-CN.md)
- [应用操作表](../../product/app-action-sheet.zh-CN.md)
- [产品设计基础](../../product/design-foundations.zh-CN.md)
- [版本与发布治理](../../release.zh-CN.md)
- [1.0.0 产品范围](product-scope.zh-CN.md)

用户可见行为仍以产品契约而不是本评估为准。特别是，`minSdk` 31 是当前产品契约，不是永久兼容承诺；任何提高都需要项目作者另行批准。

### 平台和工具证据

建议基于当前 Android 平台和 AndroidX 文档，包括：

- Android Home 角色和 [`RoleManager`](https://developer.android.com/reference/android/app/role/RoleManager)
- [`LauncherApps`](https://developer.android.com/reference/android/content/pm/LauncherApps) 中的 Launcher 清单与启动操作
- 通过 [`AlarmClock.ACTION_SHOW_ALARMS`](https://developer.android.com/reference/android/provider/AlarmClock#ACTION_SHOW_ALARMS) 发现闹钟目标，并通过 [`PackageManager`](https://developer.android.com/reference/android/content/pm/PackageManager#getLaunchIntentForPackage(java.lang.String)) 启动软件包常规入口
- [软件包可见性](https://developer.android.com/training/package-visibility)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) 及其[发布记录](https://developer.android.com/jetpack/androidx/releases/datastore)
- [Android 备份规则](https://developer.android.com/identity/data/autobackup)
- [Android Gradle Plugin 9.2 发布说明](https://developer.android.com/build/releases/agp-9-2-0-release-notes)
- [Compose BOM 指南](https://developer.android.com/develop/ui/compose/bom)
- [Macrobenchmark 指南](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview)和[采集指标](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-metrics)

文档能够确定 API 契约，但不能证明 OEM 行为。因此 Samsung 克隆行为、profile 暴露、badge 可用性和实际启动行为必须成为设备验证证据，不能被当作平台假设。

## 平台与兼容性结论

### SDK 范围

- `minSdk` 为 31。
- 除非产品契约另行变更，`1.0.0` 的 `targetSdk` 为 36。
- 首次构建工具验证必须优先评估稳定 API 37 作为 `compileSdk`。
- 仅当 API 37 需要预览版或候选发布工具，或所选稳定依赖组合无法复现时，才回退到 API 36。
- 未经项目作者另行批准，任何实现不得提高 `minSdk`。

最低版本模拟器提供确定性的 API 31 功能兼容覆盖，但不能证明 OEM 克隆行为，也不能提供权威性能数据。

### Android Home 能力

主 Activity 必须导出，并通过包含 `CATEGORY_HOME` 和 `CATEGORY_DEFAULT` 的 `ACTION_MAIN` intent filter 取得 Home 候选资格。另一个 `CATEGORY_LAUNCHER` 入口使 Avenor 在未被选为 Home 时仍可直接启动。

Home 选择由系统拥有。实现只能在选定产品旅程确有需要时调用系统 Home 角色请求或适用的系统设置路径；不得创建自有默认 Launcher 确认界面，也不得加入已延后的 Settings 入口。

必须验证以下 Activity 和任务行为：

- Avenor 不是默认 Home 时的直接应用启动；
- Avenor 是默认 Home 时的系统 Home 调用；
- 重复调用 Home 不产生重复 Activity 或无效任务栈；
- 进程重建后返回 Home，而不恢复 Drawer、操作表或进行中的手势；
- Avenor 不是默认 Home 时遵循普通 Back 行为；以及
- Avenor 是默认 Home 时，Home 上的 Back 没有可见效果。

具体 Activity `launchMode`、任务 flag 和 intent 处理仍是实现级选择，直到验证工程在全部已记录环境中证明所需行为。

### 可启动条目清单

`LauncherApps` 是清单、名称、图标、badge、回调和启动操作的首选平台边界。清单来源是 Android 为适用用户/profile 暴露的可启动 Activity，而不是全部已安装软件包。

实现不得声明 `QUERY_ALL_PACKAGES`，不得声明 `ACCESS_HIDDEN_PROFILES`、查询 Private Space，或把 Private Space 条目的预期缺失解释为清单错误。

普通、工作 profile 和克隆条目只有在 Android 于该最小权限边界内暴露时才属于范围。平台返回的每个 Activity 都是独立可启动条目；仅使用包名不足以标识或去重。

清单读取和平台回调必须进入同一个由 repository 拥有的快照。UI 代码不得分别查询 `PackageManager` 或 `LauncherApps`，否则排序、消失确认、Retry 和收藏协调会产生不一致。

### 平台目标

时钟、日历、应用信息和应用启动都是外部平台操作。每项操作都必须防御性解析或尝试，并在失败时给出产品定义的本地化反馈而不崩溃。

时钟软件包发现仅使用 `AlarmClock.ACTION_SHOW_ALARMS` 在运行时解析处理应用。随后，Avenor 获取并启动该软件包的常规入口 Activity，而不是直接把用户送入闹钟页面；不得硬编码厂商软件包。若解析到的软件包未公开常规入口 Activity，时钟可以回退到原始隐式闹钟 action；若两种操作均不可用，则给出产品定义的本地化失败反馈。日历继续使用隐式 intent。应用信息面向选定包，同时保留选定可启动身份，供返回后刷新。应用启动应在可用时使用能够面向指定用户/profile 的 Launcher 平台操作。

## 建议的系统边界

### 工程与模块形态

从仓库根目录的单个生产 Android 应用模块开始。不得仅为假设中的规模创建 feature、domain 模块或通用平台抽象。

在关键旅程存在后，如果生成版本退出证据确有需要，可以增加独立 benchmark 或 baseline-profile 测试模块。该例外用于隔离测试插桩，不意味着产品应过早转为多模块系统。

### 运行时边界

建议的运行时边界为：

1. **UI 与交互**——Compose 渲染、无障碍语义、操作表、列表/索引交互和手势仲裁。
2. **呈现状态**——页面状态、加载/错误状态、过渡状态、重复激活抑制和生命周期收集。
3. **应用清单**——`LauncherApps` 及相关软件包/profile 回调的唯一拥有者，输出稳定、不可变的快照。
4. **收藏**——有序收藏身份、添加/移除规则、持久化、读取失败和清单协调。
5. **平台操作**——Home 角色请求、时钟、日历、应用信息和应用启动。

依赖从 Android 适配器指向小型、项目自有的模型和接口。重复阻止、稳定排序和确认消失后移除等产品规则必须能够在无设备环境中测试。

### UI 架构

首选 Jetpack Compose。一个 Activity 拥有一个 Avenor 界面状态，不为 Home 和 Drawer 创建不同 Activity。Home 与 Drawer 应是同一个过渡容器中的可组合状态，使连续拖动、透明度、列表边界转移、取消和反向运动共享唯一状态源。

过渡需要使用 Compose 状态与动画原语的项目自有控制器。标准导航动画或两个互不关联的滚动处理器无法表达已记录的连续交接。控制器必须在以下参与者之间仲裁指针所有权：

- Home 收藏列表滚动；
- Home 到 Drawer 拖动；
- Drawer 列表滚动；
- Drawer 列表到达顶部后的 Drawer 到 Home 拖动；以及
- 在自身活动指针序列期间独占指针的字母索引。

该控制器是狭窄的产品专用组件，不是通用手势框架。`1.0.0` 不实现延后的收藏重排，但指针仲裁边界不应导致未来支持重排时必须替换整个界面模型。

### 并发与状态

异步平台和持久化工作使用 Kotlin coroutine 与 `Flow`/`StateFlow`。UI 通过生命周期感知方式观察不可变状态。清单刷新与收藏写入必须显式串行化，避免回调突发、Retry 和用户操作发布乱序状态。

`1.0.0` 不包含网络客户端、账户层、后台同步服务、分析 SDK 或服务器接口。

## 数据、身份、持久化与迁移

### 可启动身份

候选持久化身份包括：

- Android 用户/profile 的稳定表示，优先采用 `UserManager` serial number，而不是进程内 `UserHandle` hash；
- 可启动 Activity 的准确 `ComponentName`；以及
- 持久化文档级的显式 schema 版本。

显示名称、分组键、图标、badge、启用状态和可用性是派生的清单数据，不能作为收藏事实持久化。

设备验证必须证明该候选能够区分已记录 Samsung 和 Pixel 设备上的主应用、克隆和普通暴露的 profile 条目。如果 Android 没有为某种设备专用克隆暴露稳定的 serial/component 组合，应修订技术评估，不得静默退化为包名身份。

### 收藏持久化

在迭代 4 实现前，Proto DataStore 是首选持久化候选，因为它提供显式类型 schema、有序重复条目、原子更新和迁移路径。候选生产基线优先稳定 DataStore 版本，排除 alpha 依赖。

Active [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md) 记录在依据必需失败不变量和本迭代最小依赖边界评估该候选后，实际实现项目自有版本化 `AtomicFile` serializer 的方向。项目作者已经接受其重要技术取舍。

初始 schema 只包含恢复身份和顺序所需的数据。不得存储应用使用情况、时间戳、缓存图标、名称、分析数据或历史清单。

`1.0.0` 延后完整的损坏恢复产品行为，但实现仍必须保持以下架构不变量：

- 成功读取到空列表与读取失败可以区分；
- 读取失败绝不能转换为空数据写入；
- Retry 只读；
- 只有成功加载的状态可以执行写入；以及
- 不自动替换或清除原始不可读文件。

已实现的项目自有 serializer 必须保持这些不变量。如果无法满足，则必须重新审视持久化决定，不得弱化失败边界，也不得静默引入已排除的恢复 UI。

### 协调

收藏在加载、清单失败和瞬时启动失败时仍保持存储。只有成功清单刷新确认准确持久化身份已经永久消失，才允许自动移除。

协调必须确定且经过独立单元测试。不得根据单次启动失败、缺失图标、缺失名称、回调顺序、锁定 profile 或失败的清单读取推断永久消失。

Active [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md) 记录实际实现的 profile 完整性与准确身份证据方向。项目作者已经接受其重要技术取舍。

### 存储与备份

收藏使用凭据加密的应用私有存储。Avenor 不设置 `directBootAware`；当前用户旅程不要求首次解锁前读取收藏。

产品排除云同步与跨设备备份。因此 Manifest 和 Android 12+ 数据提取规则必须明确阻止收藏进入云备份或设备到设备传输。这是产品隐私边界，不只是默认配置选择。

不存在从 1.0 前生产 schema 迁移的要求，但仍需支持 schema 演进，使后续版本可以迁移 `1.0.0` 文件而不进行破坏性替换。

## 权限、安全、隐私与许可影响

### Manifest 与权限基线

预期基线包含：

- 平台入口所需的已导出 Home/Launcher Activity 声明；
- 仅在平台解析需要时，为通过闹钟 intent 发现时钟应用以及已纳入的隐式日历目标添加软件包可见性 query；
- 不声明 `INTERNET`；
- 不声明 `QUERY_ALL_PACKAGES`；
- 不声明 `ACCESS_HIDDEN_PROFILES`；
- 不包含使用情况访问、通知监听器、联系人、位置、文件、照片或无障碍服务能力；以及
- 显式排除备份。

必须检查最终合并 Manifest，因为依赖可能贡献源 Manifest 中不可见的声明。

### 安全与隐私

所有外部 intent 和 Launcher 操作都跨越平台边界，必须容忍目标缺失、权限撤销、条目禁用和 `SecurityException`。失败处理不得向用户暴露原始异常文本，也不得持久化诊断历史。

收藏即使不包含消息或文件内容，也属于本地用户内容。它必须留在应用私有存储中，release 构建不得完整记录收藏。profile 标识和组件名称应被视为应用清单元数据，维持在同一本地边界中。

### 依赖许可

实际依赖清单包含 AndroidX/Compose 和 Kotlin，收藏持久化未新增 DataStore、Protocol Buffer、Hilt/Dagger 或 KSP。准确 artifact 和传递 runtime 内容仍必须从解析后的 release 依赖图生成。

产品当前排除 Settings 及其第三方许可入口。因此，在形成正式发布制品前，依赖选择必须满足以下条件之一：

1. 所有必须告知的内容可以合法地存在于仓库和打包产物元数据中，不要求用户可见的应用内入口；或
2. 项目作者批准产品范围变更，加入适当的告知界面。

技术角色不能单独作出该法律结论。对于 `1.0.0` 作者日常使用基线，应如实记录尚未解决的依赖与许可证证据；形成正式发布制品或单独提升许可证门禁前，需要解析后的依赖清单和具备资质的审查。

## 依赖与替代方案

### 建议基线

- JDK 17。
- Gradle 9.4.1 与 Android Gradle Plugin 9.2，以工具链验证结果为准。
- 在兼容时使用 AGP 支持的内置 Kotlin 路径。
- 通过稳定 Compose BOM 管理稳定 Jetpack Compose 库。
- 与所选 SDK/工具链兼容的稳定 Activity Compose、Lifecycle 和 Core。
- Kotlin coroutine。
- Active ADR-0002 记录的已实现项目自有版本化 `AtomicFile` serializer。
- 只有准确稳定组合能够干净构建和测试时才采用 Hilt + KSP。

工程建立时必须在 version catalog 和 Gradle wrapper 中锁定版本。“最新”是调研策略，不是可复现构建声明。发现工具或依赖存在较新的稳定版本，属于建议维护信息，不自动形成升级要求或验收失败。项目可以在后续获授权的优化需求或迭代中统一升级，以便完整处理兼容性、迁移成本和验证。

### 仓库来源配置档

插件与依赖解析的仓库声明必须统一放在 `settings.gradle.kts`，并拒绝项目级仓库声明。公开仓库记录两个明确配置档：

- **中国大陆配置档：** 优先使用 `https://maven.aliyun.com/repository/` 下的阿里云镜像代理，并将 `https://mirrors.cloud.tencent.com/nexus/repository/maven-public/` 下的腾讯云 HTTPS Maven 镜像作为获批替代来源。迭代 1 必须验证准确的 Google、Maven Central 和 Gradle Plugin Portal 覆盖范围及最终端点顺序，之后才能将此配置档视为可复现。
- **官方配置档：** 当镜像不可用、同步延迟、内容不完整或在当前网络更慢时，使用 `google()`、`mavenCentral()` 和 `gradlePluginPortal()`。

镜像用于改善中国大陆的实际访问条件，不是产品依赖，也不要求所有公开贡献者使用。开发者可以切换配置档，或将不可用端点替换为经作者批准、使用 HTTPS 且来源可解释的等效来源；不得任意扩展仓库地址或顺序。任何提交到仓库的变更都需要重新审查依赖解析、来源、许可证和最终解析图。默认构建不得加入 JCenter、`mavenLocal()`、需要凭据的仓库或未经审查的第三方镜像。

### 保留的替代方案

- **手工依赖注入替代 Hilt：** 如果 Hilt/KSP 对小型初始运行时依赖图造成不相称的工具链风险，则优先采用手工方案。构造器注入和单一应用 composition root 保留未来迁移 Hilt 的路径。
- **Preferences DataStore 替代 Proto DataStore：** 仅当有文档化编码能够保持有序类型身份、区分读取失败与空状态，并提供显式迁移行为时才可接受。
- **API 36 compile SDK 替代 API 37：** 只允许使用产品范围已经定义的回退条件。
- **Views 替代 Compose：** 不建议。它虽然减少即时的 Compose 工具链耦合，却会提高连续过渡、手势协调和未来 UI 迭代成本，且不产生产品收益。

任何依赖都不会仅因为出现在本评估中而自动获批。构建基础迭代必须记录解析依赖图、许可、最低 SDK、Manifest 贡献、发布成熟度和替换成本。

## 构建与验证方法

### 构建基础

首次实现迭代必须在仓库根目录建立可复现工程，包括：

- Gradle wrapper 和 version catalog；
- 单个应用模块；
- 显式 `minSdk`、`targetSdk` 和 `compileSdk`；
- release 和 debug build type；
- 使用资源定义的用户可见字符串、颜色和可复用尺寸；
- 英文默认资源和简体中文资源；
- 单元测试和插桩测试基础；
- 依赖锁定或等效的解析版本证据；以及
- 集中、可切换的中国大陆和官方仓库配置档及其成功解析证据；
- 只有在实际工程中成功执行后才记录的命令。

迭代 1 已引入仓库 wrapper 和初始构建配置，所选工具链也已有初步本地构建证据。权威命令、所需 Android SDK 包和完整验证基线仍必须根据有记录的成功执行来建立，不能由本评估推断。

在部分 Windows 环境中，包含非 ASCII 字符的 checkout 路径可能触发 Android Gradle Plugin 路径错误，或暴露其他 Android 工具的限制。由于项目作者当前的 checkout 需要该解决方法，仓库目前启用了 `android.overridePathCheck=true`。这是面向具体环境的兼容设置，不是项目必须执行的验证分支，也不能证明下游路径兼容性。若出现其他路径相关错误，应查看实际错误信息；受影响的环境可以改用仅含 ASCII 字符的 checkout，或采用其他有证据支持的本机解决方法。

### 测试层级

1. **本地单元测试：** 身份编码、本地化分组与最终排序、收藏添加/移除/去重、协调、状态归约、激活节流和过渡释放决定。
2. **Compose/UI 测试：** Home、Drawer、加载/错误/Retry、操作表、本地化资源、Avenor 内 Back 行为、指针取消和防止意外激活。
3. **插桩平台测试：** Manifest 入口、重建后的持久化、外部 intent 失败、清单回调集成，以及可行时的合并 Manifest 断言。
4. **人工设备验证：** Home 选择、实际 Home 行为、克隆/profile 身份、平台 badge、时钟/日历/应用信息、系统栏、触感、重启和 OEM 特有失败。
5. **物理设备 Macrobenchmark：** 冷启动、完全显示时间、Drawer 过渡、Drawer 滚动、字母索引移动和返回 Home。模拟器 benchmark 数字仅用于诊断。

### 建议的正式发布制品环境

- Android 12/API 31 模拟器，用于最低 SDK 功能兼容。
- Android 16/API 36 的 Samsung Galaxy S23 Ultra，用于已记录的 Samsung 和克隆行为。
- Android 17/API 37 的 Google Pixel 8，用于当前平台行为。

这些环境是本评估为未来正式发布制品建议的矩阵。后续交付契约仅把作者指定的主要设备选为 `1.0.0` 必需环境；其他环境结果继续作为建议证据。执行正式发布制品验证时，证据应记录设备标识、系统/API 级别、构建身份、源代码 commit、APK 摘要、测试步骤和结果。在该矩阵中，模拟器通过不能替代任意物理设备。

## 质量门槛建议

### 确定性门槛

以下是为未来正式发布制品建议的确定性门禁。只有后续交付契约明确选择或收窄的部分，才成为 `1.0.0` 要求：

- 工程能够使用已记录 wrapper 和 JDK 可复现构建；
- release lint 和全部选定自动化测试在获批准的正确性基线下通过；仅报告工具或依赖存在更新版本的检查属于建议信息，不导致该门禁失败；
- 合并后的 release Manifest 不包含未批准权限或组件；
- 全部必需环境中，完整纳入旅程都能离线通过；
- 已记录验收运行中没有崩溃、ANR、意外应用激活、重复收藏、静默删除收藏或数据覆盖；
- 英文、简体中文和英文回退资源覆盖所有纳入的用户可见字符串；
- 进程重建和设备重启保持有效收藏身份与顺序；以及
- 清单读取或启动失败不会删除收藏。

### 实测性能门槛

在目标物理设备上测得可安装实现前，无法负责任地固定绝对性能数字。未来正式发布制品的验证工作应为以下内容产出可重复分布：

- 冷启动初次显示时间与完全显示时间；
- Home 到 Drawer、Drawer 滚动、索引滑动和 Drawer 到 Home 的 frame-overrun 百分位；
- 冷启动 Home 后以及完成完整旅程后的内存；以及
- 足以证明 Avenor 不轮询、不执行后台网络工作的空闲功耗行为。

对于未来正式发布制品，Macrobenchmark 结果应包含多次迭代并保留生成的 JSON 与 trace 证据，项目作者还应批准由此得到的绝对退出阈值。后续交付契约为 `1.0.0` 选择作者日常使用基线，因此这些测量属于建议证据而不是完成门禁。

只有 release 构建实测证明 baseline profile 对关键旅程有实质改善，且生成过程可以复现时才加入它。它不能替代对可避免启动工作、重组或卡顿问题的修复。

## 交付风险与未解决决定

### 验证风险

- Samsung 暴露克隆条目、badge 或用户/profile 身份的方式可能不同于 AOSP 假设。
- 平台回调本身可能无法区分暂时不可用和永久消失；协调可能需要一次成功的完整快照。
- 实际工具链和解析后的依赖图仍需要正式版本记录证据。
- 镜像内容完整性和同步状态可能与官方上游不同，当前包含非 ASCII 字符的 Windows checkout 路径也可能暴露工具特有的路径失败。
- 即使所选收藏持久化没有新增库，解析后的 release 依赖图在形成正式发布制品前仍需要合格的许可证审查。
- 手势仲裁是最高的自定义 UI 风险，需要在真实触摸硬件上尽早验证垂直切片。
- 如果未来将绝对性能、内存和功耗阈值提升为正式发布制品门禁，则需要实现实测和作者批准。

### 保留给项目作者的决定

- 任何 `minSdk` 提高；
- 任何已批准 `1.0.0` 产品范围变更；
- API 37 候选工具链验证失败时是否接受 API 36 回退；
- 具备资质的审查认为必要时，是否增加用户可见的许可界面；
- 任何未来正式发布制品的性能阈值；以及
- 是否接受物理设备验证发现的已知 OEM 限制。

Hilt 与手工依赖注入之间的选择属于实现决定，除非它实质改变交付风险、依赖/许可义务或已批准日程。技术角色应在构建验证后选择较小且已证明的方案，并记录结果。

## 迭代建议

1. **Home 最小可用界面**——创建可复现工程，证明 SDK/工具版本和 Home 候选资格，并交付本地化 Home 信息界面及安全平台目标。
2. **Drawer 应用发现与启动**——证明 Launcher 清单和身份边界，交付核心 Drawer 列表与 Retry 状态，并启动平台暴露的准确条目。
3. **Drawer 导航与实时状态完整性**——完成分组、字母索引、实时更新、位置保留和真实触控 Home/Drawer 手势仲裁。
4. **应用操作表与收藏创建**——交付模态应用操作，并证明有序、无重复的收藏创建与持久化。
5. **收藏生命周期与韧性**——完成收藏启动、移除、重启持久化、协调、瞬态失败处理和非破坏性读取失败行为。
6. **作者日常使用基线收尾**——在作者指定的主要物理设备上验证完整所选路径，记录源码/APK 身份与已知缺口，并准备 `1.0.0` 日常使用完成证据。完整矩阵、性能、许可证和正式发布制品工作继续作为建议或单独授权事项。

每个迭代都需要自身适用的当前契约和明确实现授权。该顺序尽量避免在整个 UI 完成后才发现 OEM 身份行为或手势可行性问题。

## 产品范围影响建议

本评估不提出产品范围变更。

以下发现以后可能需要单独批准的建议，但目前不修改 `1.0.0`：

- 具备资质的许可结论要求应用内第三方许可界面；
- 无法在不产生产品可见限制的情况下持久标识克隆条目；
- API 37 工具链失败，需要使用已经允许的 API 36 回退；或
- 某项设备限制导致纳入行为无法按当前文本满足。

Private Space 仍在当前产品契约之外。损坏检测、源数据保留 UI、只读恢复和相关禁用操作状态仍明确延后于 `1.0.0`，但持久化架构必须避免破坏性覆盖。

## 评估结论

Avenor Launcher `1.0.0` 可以使用现代、可维护的 Android 架构实现，不需要广泛软件包可见性、隐藏 profile 访问、网络能力或过早模块化。

目前已实现、待评审的方向是：单 Activity Compose 应用；项目自有的 `LauncherApps` 清单 repository；稳定的 profile 与 component 组合身份；Active ADR-0002 记录的有序版本化 `AtomicFile` 持久化；显式备份排除；以及分层自动化和物理设备验证。

本评估本身不授权实现或集成；已实现迭代由项目作者另行授权。后续交付契约为 `1.0.0` 选择作者日常使用基线；因此完成需要适用的源码/APK 身份、已接受主要设备路径、已知缺口记录，以及已同步实现和文档，而不要求本评估提出的全部正式发布制品证据。
