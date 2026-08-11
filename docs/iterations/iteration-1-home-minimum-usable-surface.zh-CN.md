# 迭代 1：Home 最低可用界面

> 英文语义源：[iteration-1-home-minimum-usable-surface.md](iteration-1-home-minimum-usable-surface.md)。
>
> 本迭代适用于 [Avenor Launcher 1.0.0 交付契约](../versions/1.0.0/delivery-contract.zh-CN.md)，定义一个产品增量及其必需证据。本文不授权实现、不批准候选架构，也不声明迭代完成。项目作者必须明确授权实现。

## 目标

交付一个可安装的 Avenor 构建，使 Android 能够将其识别为 Home 应用，并在不依赖后续 Drawer 或收藏行为的情况下呈现最低限度的本地化 Home 信息界面。

## 产品和版本引用

- [1.0.0 交付契约](../versions/1.0.0/delivery-contract.zh-CN.md)
- [1.0.0 产品范围](../versions/1.0.0/product-scope.zh-CN.md)
- [1.0.0 技术评估](../versions/1.0.0/technical-assessment.zh-CN.md)
- [产品导航](../product/navigation.zh-CN.md)
- [Home 交互](../product/home.zh-CN.md)
- [产品设计基础约束](../product/design-foundations.zh-CN.md)

链接的产品文档继续作为详细行为的权威来源。本迭代只选择下列 Home 行为。

## 可观察结果

作者能够安装并直接打开 Avenor，Android 将其识别为可用 Home 应用，且 Home 显示本地化的当前时间、日期和星期。选择时间时，若解析到的系统时钟应用公开了主界面，则进入该主界面而不是仅进入闹钟页面；选择日期会安全调用适用的系统所有日历目标。返回 Home 以及进程重建后会回到 Home，不发生崩溃或无效任务栈。

## 纳入工作

- 在产品仓库根目录创建最小 Android 项目。
- 将 `com.avenor.launcher` 配置为已批准的 `applicationId` 和初始 namespace，除非后续项目作者通过明确决定替换它。
- 建立可复现 debug 构建、选定的自动化测试基础、安装和聚焦验证命令。
- 集中管理插件与依赖仓库，提供使用已批准阿里云和腾讯云镜像的中国大陆配置档，以及使用 Google、Maven Central 和 Gradle Plugin Portal 的官方上游配置档。
- 使 Avenor 具备 Android Home 应用资格，同时在未选为默认 Home 时仍可直接启动。
- 按照系统时钟、格式和 locale 行为呈现 Home 时间、日期和星期。
- 提供默认英文和简体中文资源，并为不支持的 locale 使用英文回退。
- 对面向用户的字符串、颜色和可复用尺寸使用资源定义。
- 保持 Home 应用表面、状态栏区域和导航栏区域完全透明，使系统背景保持可见；不绘制 Avenor 自有渐变、Scrim、模糊或不透明背景，并保留平台默认对比度保护。
- 实现纳入内容所需的 Home inset、深色主题前景、字体和触控目标基线。迭代 1 不实现分配给迭代 3 完整 Home/Drawer 转场的 Home 内容透明度动画。
- 在不硬编码厂商包名的前提下打开解析到的系统时钟应用主界面；若未公开主界面则回退到其系统闹钟目标；通过防御性的系统所有 action 调用日历，并提供本地化失败行为。
- 普通进程重建后从 Home 启动并恢复。
- 验证 merged manifest，以及初始工具链或依赖未引入未经批准的声明。

## 排除工作

- Drawer 内容、Home-to-Drawer 交互、应用清单、应用启动、字母索引和实时应用更新。
- 收藏显示、创建、持久化、启动、移除、调和、排序或恢复行为。
- 应用操作面板和 Settings。
- 完整版本兼容性、性能阈值、release 签名、正式 APK 创建、产物移动、归档、tag 或发布操作。
- 已批准 `1.0.0` 产品范围以外的任何面向用户行为。

## 技术影响面

- 根目录 Android 项目和可复现工具链。
- 应用标识、SDK 值、构建变体、资源和测试基础。
- Home/Launcher Manifest 资格、Activity/任务行为、直接启动和进程恢复。
- 时间/日期格式、locale 资源选择、系统栏、inset 和防御性平台 intent。

本迭代验证候选 JDK、Gradle、Android Gradle Plugin、Kotlin、Compose 和 SDK 组合。已证明的重大选择在需要时记录到架构文档或 ADR；普通实现细节留在代码和测试中。

## 依赖和序列

- 项目作者批准 `1.0.0` 前瞻性交付边界并明确授权本迭代。
- 首先评估 API 37 作为稳定 `compileSdk`；使用文档所述 API 36 回退需要项目作者作出保留决定。
- 项目作者接受已交付实现和观察到的 Home 行为足以继续、现有项目基础适合在不替换的情况下扩展，并且每个未完成迭代 1 验证事项均得到明确记录和分配后，可以开始迭代 2。该继续门禁不声明迭代 1 已正式关闭，也不豁免任何版本级退出门禁。

## 迁移和兼容性影响

- 这是首个 Android 实现，因此不存在生产数据或应用版本迁移。
- `minSdk` 保持 31，`targetSdk` 保持 36。
- 已批准的初始 `applicationId` 是 `com.avenor.launcher`。后续仍可由作者批准修改，但正式产物存在后，修改会形成不同的 Android 应用身份，必须明确处理安装、升级、签名、数据连续性、分发和迁移影响。
- 中间 debug 安装不是正式 `1.0.0` 产物。

## 安全、隐私、权限和许可证影响

- 不引入网络、账号、分析、广泛软件包可见性、隐藏资料访问、云备份或设备间迁移。
- 源码 Manifest 为已确认的时钟主界面和闹钟回退行为声明 `com.android.alarm.permission.SET_ALARM`。项目作者已经接受该声明属于当前需求，不将其作为待移除事项。
- 检查 merged manifest 中依赖贡献的权限、组件和备份行为。
- 根据解析后的依赖图记录直接和传递依赖、版本、成熟度、许可证和 Manifest 贡献。
- 未经项目作者单独授权，不创建或存储签名秘密。

## 风险和未解决决定

- 项目作者报告，commit `2e492109482a185f33670e87e86ce562b0279ebf` 所表示的实现已在编辑器中成功构建、安装和运行，并显示了 Home。该报告未标识编辑器及版本、主机环境、构建变体、设备型号、Android/API 版本、准确步骤或保留输出，因此这些证据字段仍未验证。
- 可复现 CLI 构建、自动化测试、release lint、依赖解析、merged-manifest 检查、模拟器验证和必需真机验证仍未完成。
- 已批准镜像相对官方上游可能存在同步延迟或内容缺失；准确端点、顺序和解析产物仍需证据。
- 部分 Windows 环境可能在 checkout 路径包含非 ASCII 字符时报告 Android Gradle Plugin 路径错误。由于项目作者当前的 checkout 需要该解决方法，仓库目前启用了 `android.overridePathCheck=true`。这是面向具体环境的兼容措施，不是验证结果，也不要求所有贡献者复现相同路径；该属性只会绕过 AGP 路径保护，不能保证所有下游工具均支持当前路径。
- API 37 `compileSdk` 可复现性仍依赖证据；API 36 回退由作者保留决定。
- Home 角色、任务栈、重复 Home 调用和直接启动行为可能因环境而异。
- 时钟或日历目标可能不存在，或者在不同设备上行为不同。
- 初始证据形成后，重大的项目、模块或 UI 选择可能需要 ADR。

## 验证计划

- 在清晰描述的环境中使用选定 JDK，通过仓库 wrapper 构建。
- 在可用时分别通过中国大陆与官方配置档解析相同的锁定依赖图，并记录镜像缺失、回退行为、仓库顺序和产物来源。
- 若 Windows 构建仍出现路径相关错误，应检查并记录实际错误信息。受影响的环境可以改用仅含 ASCII 字符的 checkout 路径，或采用其他有证据支持的本机解决方法；不得把作者当前的 workaround 转化为统一的通过或失败流程。
- 运行选定的初始自动化测试，并在可用时运行 release lint。
- 检查 merged manifest 和解析后的依赖图。
- 在 API 31 模拟器和两台已记录真机上安装并直接启动构建。
- 验证 Home 资格、系统所有的 Home 选择、重复 Home 调用、Back 行为和进程重建。
- 验证静止状态下的 Home 内容在系统背景之上完全可见；应用表面与系统栏区域请求完全透明；平台对比度保护保持启用；且不绘制 Avenor 自有渐变、Scrim、模糊或不透明背景。
- 验证时间、日期、星期、12/24 小时制、英文、简体中文和英文回退资源。
- 验证点击时间时，在解析到的时钟应用公开主界面的情况下进入其主界面而不只进入闹钟标签页；验证未公开主界面时的安全回退，以及目标不可用时不崩溃并显示失败反馈；同时验证日历成功与失败行为。
- 记录实际命令、环境标识、构建身份、源码 commit、步骤和结果。

## 验收证据

截至 2026-08-11 可用的证据：

- Commit `75dfdfd221a981db1677a9e5f2873a6e84fab398` 在实现前统一了已批准的应用身份、时钟启动行为、仓库配置档、路径 workaround、透明度边界和 `48dp` Home 日期行契约。
- Commit `2e492109482a185f33670e87e86ce562b0279ebf` 添加了根目录 Android 项目、单 Activity Compose Home 实现、Home/Launcher Manifest 入口、API 与应用标识、时间/日期资源、默认英文与简体中文资源、透明主题、平台目标处理和初始测试源码。
- 仓库检查确认源码 Manifest 包含 Home 和直接启动入口，以及已接受的 `com.android.alarm.permission.SET_ALARM` 声明。这仅是源码证据；merged manifest 尚未得到记录或验收。
- 仓库检查确认日期与星期行使用契约定义的 `48dp` 资源并垂直居中。当前源码中不存在已识别的 `40dp`/`48dp` 契约不匹配。
- 项目作者报告当前项目已从编辑器成功构建、安装和运行，且 Home 正常显示。作者接受该已观察实现结果足以继续向迭代 2 交付。
- 已提供证据没有标识编辑器及版本、主机环境、构建变体、设备型号、Android/API 版本、准确步骤、构建输出、APK 身份或保留证据位置。这些字段未经验证，不得推断。

正式关闭迭代 1 和 `1.0.0` 前仍需完成：

- 可复现 CLI 构建命令与结果、选定自动化测试执行、release lint、依赖和仓库配置档解析，以及 merged-manifest 证据；
- 具有准确环境和构建身份的 API 31 模拟器、Samsung API 36 和 Pixel API 37 验证；
- Home 资格、直接启动、重复 Home 调用、Back、进程重建、透明度、本地化、12/24 小时制，以及时钟/日历成功与失败路径的聚焦证据；
- 每个失败、不可用检查、限制和作者处置的准确记录。

现有证据支持继续交付，不支持正式关闭迭代或最终版本验收。

## 相关决定、commit 和 tag

- 交付对齐 commit：`75dfdfd221a981db1677a9e5f2873a6e84fab398`（`docs: align initial Home delivery contracts`）。
- 实现 commit：`2e492109482a185f33670e87e86ce562b0279ebf`（`feat(home): implement minimum launcher surface`）。
- 架构和 ADR 链接：本迭代尚未记录。只有已证明的重大选择需要时才添加。
- Tag：本迭代未授权也不要求 tag。

## 最终结果

项目作者已经接受已交付实现和观察到的 Home 显示足以在现有基础上继续产品交付。迭代 1 尚未正式关闭，因为上述可复现 CLI、自动化、lint、merged-manifest、模拟器、真机和聚焦行为证据仍不完整。继续交付的接受不把任何缺失检查标记为通过，也不降低最终 `1.0.0` 门禁。

## 剩余问题和交接

可供[迭代 2](iteration-2-drawer-application-discovery-and-launch.zh-CN.md)复用的基础包括：根目录单 Activity Compose 项目、`compileSdk` 37 / `minSdk` 31 / `targetSdk` 36 配置、`com.avenor.launcher` 应用身份、Home 和直接启动 Manifest 入口、本地化 Home 时间/日期实现、默认英文与简体中文资源、透明主题，以及防御性时钟/日历目标基础。项目作者已经确认，在开始 Drawer 工作前不需要替换该结构。

缺失的 CLI、自动化测试、release lint、依赖解析、merged-manifest、API 31 模拟器、Samsung API 36、Pixel API 37 和聚焦 Home 行为证据，继续作为明确的迭代 1 关闭义务。应在下一个适用验证运行中执行，并最迟在迭代 6 和 `1.0.0` 版本门禁前关闭。迭代 2 不得把这些移交证据表述为已完成，也不得以此隐藏后来发现的 Home 契约失败。迭代 2 实现仍需项目作者单独明确授权。
