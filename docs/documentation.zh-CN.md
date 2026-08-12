# 项目文档地图与治理规则

> 本文先以中文起草，并提供英文对应文档：[documentation.md](documentation.md)。两份文档应保持语义一致；面向公共仓库的英文内容以英文版本为准。

## 目的

本文定义 Avenor Launcher 各类项目信息的唯一权威位置、创建条件和维护规则。规划中的路径不代表对应结论已经形成，也不得为保持目录完整而创建空文档。

项目作者是所有项目事项的第一责任人。涉及安全、隐私、法务、财税或平台政策的专业结论时，应取得具备相应能力的人员复核。

## 契约文档模型

**契约文档**是仓库中的权威文档，其陈述要么约束明确范围和时期，要么权威记录该范围和时期已经完成的事实。“契约”不代表每份文档都定义当前产品行为。每份契约文档都有契约类型、适用范围和时间边界，必须据此解释。

| 契约类型 | 回答的问题 | 适用范围 |
| --- | --- | --- |
| 项目治理契约 | 项目如何被指导、记录、授权和维护？ | 相关规则保留在当前治理文档期间，适用于项目工作 |
| 当前产品契约 | 当前适用哪些产品行为、用户可见状态、范围、术语和验收意图？ | 适用于当前产品定义，直至授权变更更新对应文档 |
| 交付契约 | 某项 roadmap 边界、正式版本或迭代必须交付、排除、验证和报告什么？ | 适用于具名交付范围；它从当前产品契约中选择工作，但不能独立重定义产品契约 |
| 技术或运营契约 | 当前系统必须如何设计、构建、验证、保护、运营或发布？ | 适用于文档声明的技术或运营边界，并且必须兼容当前产品契约 |
| 历史契约记录 | 某个已完成时期实际适用哪些范围、决定、实现变化、验证证据和结果？ | 对该历史时期持续具有权威性，但不约束当前产品行为 |
| 法律文件 | 适用哪些法律许可、义务或限制？ | 按该文件自身条款和范围适用 |

各类契约按以下关系共同工作：

- 产品方向、Requirements Brief、交互规格、设计基础约束和产品字典分别构成其职责范围内的当前产品契约。
- 交付契约可以承诺实现当前产品契约的全部或一部分，也可以补充交付顺序、受影响区域、非目标、风险和验证证据；但除非通过获得授权的产品决定同步更新适用的当前产品契约，否则交付契约不得引入或改变产品行为。
- 一个正式版本包含一个或多个迭代。迭代是可独立评审的交付范围，不是产品定义的替代品。里程碑专指由作者明确宣告且具有获批 Git tag 的例外性基线；GitHub Release 可选。
- 交付契约完成后，其最终结果继续作为权威交付历史。将其移入版本归档只改变其时间适用范围，不改变其权威性或事实含义。
- 架构、开发、验证、安全、隐私和发布文档仅在具备真实输入并形成结论后，才成为技术或运营契约。
- Git 历史用于支持来源追踪，但不能替代适用的契约文档。
- 临时 prompt、草稿笔记、未核实的任务清单和对话记录属于工作材料，不是契约文档。它们不得作为权威项目文档提交，也不得覆盖任何契约。

## 当前权威文档

| 信息类型 | 英文或公共入口 | 中文入口 | 契约类型 | 职责 |
| --- | --- | --- | --- | --- |
| 项目入口 | [`README.md`](../README.md) | [`README.zh-CN.md`](../README.zh-CN.md) | 当前产品契约 | 提供当前项目与产品摘要和深层文档链接 |
| 产品概览 | [`overview.md`](../overview.md) | [`overview.zh-CN.md`](../overview.zh-CN.md) | 当前产品契约 | 记录产品方向、原则、边界和待确认范围 |
| Agent 路由 | [`AGENTS.md`](../AGENTS.md) | [`AGENTS.zh-CN.md`](../AGENTS.zh-CN.md) | 项目治理契约 | 记录 Toolkit 入口和项目级工作规则 |
| 文档治理 | [`docs/documentation.md`](documentation.md) | [`docs/documentation.zh-CN.md`](documentation.zh-CN.md) | 项目治理契约 | 定义契约文档模型与维护规则 |
| 产品基础需求 | [`docs/requirements/product-foundation.md`](requirements/product-foundation.md) | [`docs/requirements/product-foundation.zh-CN.md`](requirements/product-foundation.zh-CN.md) | 当前产品契约 | 记录产品问题、作者场景、当前范围、验收意图和开放产品问题 |
| 产品决策与范围变更 | [`docs/product-decisions.md`](product-decisions.md) | [`docs/product-decisions.zh-CN.md`](product-decisions.zh-CN.md) | 项目治理契约；未来的决定记录属于历史契约记录 | 定义决策权、范围变更、契约不一致处理和未来决策记录格式 |
| 产品导航 | [`docs/product/navigation.md`](product/navigation.md) | [`docs/product/navigation.zh-CN.md`](product/navigation.zh-CN.md) | 当前产品契约 | 定义界面层级、进入、退出、Back、恢复和公共过渡 |
| Home 交互 | [`docs/product/home.md`](product/home.md) | [`docs/product/home.zh-CN.md`](product/home.zh-CN.md) | 当前产品契约 | 定义 Home 信息、收藏、启动行为和排序模式 |
| Drawer 交互 | [`docs/product/drawer.md`](product/drawer.md) | [`docs/product/drawer.zh-CN.md`](product/drawer.zh-CN.md) | 当前产品契约 | 定义应用清单、分组、排序、字母索引和实时更新 |
| 应用操作面板 | [`docs/product/app-action-sheet.md`](product/app-action-sheet.md) | [`docs/product/app-action-sheet.zh-CN.md`](product/app-action-sheet.zh-CN.md) | 当前产品契约 | 定义模态应用快捷操作和启动器操作 |
| Settings 交互 | [`docs/product/settings.md`](product/settings.md) | [`docs/product/settings.zh-CN.md`](product/settings.zh-CN.md) | 当前产品契约 | 定义当前 Settings 信息与行为 |
| 产品设计基础约束 | [`docs/product/design-foundations.md`](product/design-foundations.md) | [`docs/product/design-foundations.zh-CN.md`](product/design-foundations.zh-CN.md) | 当前产品契约 | 定义当前主题、布局、字体、图标、无障碍和资源原则 |
| 产品字典 | [`docs/product/glossary.md`](product/glossary.md) | [`docs/product/glossary.zh-CN.md`](product/glossary.zh-CN.md) | 当前产品契约 | 定义规范产品术语 |
| 版本、产物与发布治理 | [`docs/release.md`](release.md) | [`docs/release.zh-CN.md`](release.zh-CN.md) | 技术或运营契约 | 定义应用版本、归档、APK 产物、签名连续性、tag 与 GitHub Release |
| 活动版本交付格式 | [`docs/versions/version-delivery-format.md`](versions/version-delivery-format.md) | [`docs/versions/version-delivery-format.zh-CN.md`](versions/version-delivery-format.zh-CN.md) | 项目治理契约 | 定义活动版本目录、必要输入、格式与项目里程碑边界 |
| 迭代记录格式 | [`docs/iterations/iteration-record-format.md`](iterations/iteration-record-format.md) | [`docs/iterations/iteration-record-format.zh-CN.md`](iterations/iteration-record-format.zh-CN.md) | 项目治理契约 | 定义迭代命名、必需章节、证据与归档处理 |
| 架构决定 | [`docs/decisions/`](decisions/) | - | 技术契约、提案与历史决定记录 | 记录重大技术提案与已接受决定，但不替代系统架构文档；只有已接受的 ADR 才建立其所述架构边界 |
| 许可证 | [`LICENSE`](../LICENSE) | - | 法律文件 | 包含 Apache License 2.0 原文 |

当前已接受的架构决定是 [ADR-0001](decisions/0001-establish-replaceable-launcher-icon-rendering.md)。[ADR-0002](decisions/0002-use-versioned-atomic-file-for-favorites.md) 与 [ADR-0003](decisions/0003-model-profile-completeness-for-favorite-reconciliation.md) 记录已实现的方向；在项目作者明确接受其技术取舍前，两者仍为提案。

## 规划中的权威位置

下列文档仅在具备真实输入时创建：

| 路径 | 唯一职责 | 创建条件 | 语言策略 |
| --- | --- | --- | --- |
| `docs/architecture.md` | 系统边界、组件、依赖、数据流和技术方向 | 技术栈或当前产品契约需要形成架构结论 | 默认英文；存在持续跨语言阅读需求时补充中文版本 |
| `docs/development.md` | 开发环境、构建、运行和故障排查 | 实际技术栈和权威命令得到验证 | 默认英文；按需翻译 |
| `docs/validation.md` | 测试、静态检查、人工验证和发布门禁 | 实际质量工具、命令或验证流程得到验证 | 默认英文；按需翻译 |
| `docs/security.md` | 安全模型、威胁、控制措施和响应流程 | 架构、权限、数据流或发行方式足以支持安全分析 | 默认英文；专业结论须复核，按需翻译 |
| `docs/privacy.md` | 数据清单、处理目的、保留方式和用户权利 | 数据、权限、地区或第三方处理行为得到确认 | 默认英文；专业结论须复核，面向用户的版本按发行要求提供翻译 |
| `CHANGELOG.md` | 用户可感知的版本变化 | 首个用户可感知版本或变更形成 | 英文公共语义源；按实际受众决定是否提供中文版本 |

## 产品文档模型

产品文档按以下三层分开维护：

1. **产品方向：** `overview.md` 记录持久的目的、原则、能力层级和长期边界。未来的 `docs/roadmap.md` 可以记录从 V1 到 V2、V3、V4 的能力层级演进及其间的重大项目结果，但必须比版本或迭代计划更宏观。
2. **当前产品契约：** Requirements Brief 和交互规格记录当前用户行为、状态、约束和验收意图。它们描述当前产品，不保留按版本累积的叙事历史。
3. **变更理由与交付历史：** 产品决策记录解释重要范围取舍；迭代记录和版本归档描述项目进展与实现演进，但不成为当前产品契约的第二份副本。

### 交互规格

仅在相关行为已具备定义条件时创建交互规格。按页面、弹窗或相对独立的功能模块拆分，例如：

- `docs/product/home.md`
- `docs/product/drawer.md`
- `docs/product/settings.md`
- `docs/product/<feature>.md`
- 当多份规格实际共用交互契约时，创建 `docs/product/shared-components.md`

每份规格是其职责范围内的当前权威契约。它可以链接到公共组件规则，而不复制内容。交互规格不保留每个版本或迭代的时间顺序历史；该历史由产品决策、迭代记录、版本归档和 Git 历史承担。

当迭代改变当前行为时，在迭代记录中记录交付前后的范围；当作者启用决策记录后，遵循 `docs/product-decisions.md`；并在同一变更中或接入实现前更新受影响的当前产品规格。

## Roadmap、版本、迭代、里程碑与归档

这些交付契约与历史契约记录回答不同问题，不得互相替代，也不得替代当前产品契约。

### Roadmap

未来的 `docs/roadmap.md` 记录长期能力层级方向和重大项目结果。它可以描述 V1、V2、V3 与 V4 之间的演进，但不授权后续能力层级、不规定详细页面行为，也不跟踪普通实现任务。

### 活动版本交付记录

正式应用版本的活动交付输入与整合契约使用 `docs/versions/<version>/`，并遵循 [`docs/versions/version-delivery-format.md`](versions/version-delivery-format.md)。版本目录使用不带 `v` 前缀的准确 `versionName`。真实输入存在时，其中可包含产品范围、技术评估和整合版本 `delivery-contract.md`。

### 里程碑

在本项目中，里程碑是由项目作者明确宣告，并具有获批 Git tag 的例外性基线。GitHub Release 可选，只有作者另行选择对外发布时才创建。正式版本、迭代、未获批 tag，或者虽已获批但未被作者宣告为里程碑的 tag，都不会自动构成里程碑。里程碑不组织普通版本交付，项目不使用 `docs/milestones/` 目录。

### 迭代记录

当实现计划开始且存在真实交付迭代时，使用 `docs/iterations/iteration-<number>-<title>.md`，并遵循 [`docs/iterations/iteration-record-format.md`](iterations/iteration-record-format.md)。

- 迭代标识在全项目范围内使用一组从 `1` 开始、不带前导零且单调递增的正整数序列。
- 版本归档后也不得重编号、复用或重新计数。
- 迭代是可评审的交付单元。其边界由实现难度、预计时间、变更广度、依赖、技术风险和验证成本共同决定，不单纯依据产品层级或固定功能数量。
- 一个迭代可实现当前产品契约中 Feature 的全部或一部分，也可合并产生一个可验证结果所必需的紧密耦合工作；但不得静默引入当前产品文档中不存在的范围。
- 每份记录应包含目标、产品文档引用、适用时的变更前后行为、范围、非目标、依赖、风险、持久层级的受影响代码区域、验证计划与证据、相关决策与 ADR、commit 或 tag，以及最终结果。
- 详细代码演进记录到行为、组件、接口、数据、架构、构建、迁移和验证影响层级。Git commit 和 diff 仍是逐行源码历史的权威来源。

迭代记录在其范围生效时属于交付契约，完成后属于权威历史契约记录；它不是产品需求或架构的永久副本。

### 版本归档

当实际宣告软件版本边界且其包含的迭代已关闭后，创建如 `docs/archives/v1.1.0/` 的版本文件夹。

- 文件夹名使用已宣告的软件版本，并遵循 [`docs/release.md`](release.md)。是否存在 tag 不影响正式版本归档是否成立。
- 将活动版本契约、支撑输入和纳入该版本的原始迭代记录移入归档文件夹；不得在 `docs/versions/` 或 `docs/iterations/` 下保留第二份权威副本。
- 在版本文件夹内保留 `delivery-contract.md`，作为归档总结和入口。
- 总结使用 `<迭代标识> - <标题>` 列出每个纳入的迭代，并链接到现已位于同一归档文件夹中的原始迭代文件。
- 总结记录版本结果、所含迭代范围或明确集合、重要产品变化、实现演进、决策、迁移、验证证据、已知限制、存在时的相关 tag 或 release，以及宣告该版本边界的理由。
- 归档不会重置全项目迭代序列。若 `docs/archives/v1.1.0/` 包含迭代 `iteration-5-...` 至 `iteration-10-...`，下一份位于 `docs/iterations/` 的活动迭代必须是 `iteration-11-...`。
- 不得改写已归档迭代记录来让后续历史显得更整洁。如需修正事实错误，应显式修正并保留其原始交付含义。
- 将迭代移入归档文件夹时，必须更新所有引用该迭代的链接。
- 每个正式版本包含一个或多个已完成迭代。

在存在真实的计划或实现输入前，不创建空的 roadmap、版本、迭代、里程碑或归档文件。格式文档治理后续记录的创建方式，因此可以先于具体交付记录存在。

## 语言与翻译

- 临时工作笔记和检查清单不要求英文版本，也不得成为已提交权威文档的依赖。
- README、产品概览和 Agent 指令当前维护中英文版本。
- 其他文档按跨语言阅读和外部受众的实际需要决定是否翻译，不为形式对称创建对应文档。
- 当文档从中文开始起草时，应在将该双语文档视为完整前提供英文公共版本；发布后的英文文档是对外语义源。
- 双语文档必须保持范围、约束和规范含义一致。发现实质差异时，应在同一变更中修正；无法同步时应显式记录差异及后续处理。
- Commit message、Pull Request、Issue、发布说明及其他公共仓库输出使用英文。

## 更新与复核

- 当产品范围、架构、数据处理、平台、发行渠道、验证流程或其他文档边界变化时，立即更新受影响的权威文档。
- 每个正式版本和里程碑结束时，统一复核文档入口、链接、状态和跨文档一致性。
- 安全、隐私、许可证和发布文档在相关发布门禁前额外复核。
- 文档中的计划、假设和待确认事项不得表述为已经完成的事实。
- 纯文档变更至少验证本地 Markdown 链接，并检查 Git diff 与中英文语义一致性。

## 版本与归档

- 普通指南和当前状态文档随相关变更在同一提交中更新，不保留失效内容作为正文历史。
- ADR 使用 `0001-<decision>.md` 形式的四位递增编号，追加记录，不重编号、不复用编号、不改写历史决定；被替代时创建新 ADR 并互相链接。
- Requirements Brief 应保持边界和验收标准可追踪。范围发生实质变化时，应显式记录变更，不静默覆盖当前契约。
- 保持当前产品规格为最新契约；在产品决策中保留重要理由，在迭代记录和版本归档中保留交付历史。
- 产品范围变更需要项目作者明确决定，并在适用时完成技术影响评估。一项请求只有写入适用的权威文档后，才成为当前产品范围。
- 安全、隐私和发布记录应保留适用范围、版本或日期，以及必要的专业复核证据。
- 仅当失效文档仍具有决策、审计或迁移价值时才移入历史存储；否则删除。历史材料必须说明替代文档，且不得作为当前规则加载。

## 当前状态文档规则

- 仓库中可见的权威文档描述当前项目或产品状态，不携带生命周期状态字段。
- 尚未准备成为当前项目状态的内容保留在对话或 `max-dev-context` 等外部续接工作区中，不作为有效文档进入本产品仓库。
- 更新权威文档即改变当前契约。先前状态通过 Git 历史、迭代记录和版本归档保留，不在当前文档中使用状态标签。
- 代码存在后，接入变更前对比文档、实现、测试和验证证据。差异属于需要解决的契约不一致，不代表任何一方静默替代另一方。
- `docs/archives/<version>/` 中的历史文件仍是权威历史契约记录。其存储位置将它们排除在当前产品、治理、技术和交付契约之外，但不会取消它们对所述历史时期的权威性。

## Git 与任务工作流

- 项目当前采用单线开发，不维护多分支或多人协作流程。仅在作者以后引入相关需要时，再定义分支与协作约定。
- 将每项可独立审查的操作视为一个任务。完成后报告结果与证据，并等待作者确认，再开始下一个任务。
- 修改文件不代表获得提交授权，提交不代表获得推送授权。只有项目作者明确授权本次串行接续时，Agent 才可以不中断地依次执行修改、提交与推送。
- 宽泛目标本身不授权其交付链中的所有后续写操作。为了报告当前任务而进行的只读检查和验证仍属于当前任务。
