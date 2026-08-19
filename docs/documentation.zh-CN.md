# 项目文档地图与治理规则

> 本文先以中文起草，并提供英文对应文档：[documentation.md](documentation.md)。两份文档应保持语义一致；面向公共仓库的英文内容以英文版本为准。

## 目的

本文定义 Avenor Launcher 各类项目信息的唯一权威位置、创建条件和维护规则。规划中的路径不代表对应结论已经形成，也不得为保持目录完整而创建空文档。

项目作者是所有项目事项的第一责任人。涉及安全、隐私、法务、财税或平台政策的专业结论时，应取得具备相应能力的人员复核。

## 文档分类

仓库文档只使用四个实用类别。类别用于说明文档职责，不建立额外的审批或生命周期系统。

| 类别 | 回答的问题 | 示例 |
| --- | --- | --- |
| 项目治理 | 项目工作如何路由、授权、记录和维护？ | `AGENTS.md`、本文、交付格式 |
| 当前事实与规则 | 当前适用哪些产品行为、技术配置、开发、验证和发布规则？ | `overview.md`、`docs/product/`、`development.md`、`validation.md`、`release.md` |
| 决策理由 | 为什么存在某项已经确认的重大产品或架构方向？ | 产品决定和 ADR |
| 交付记录 | 某个版本或迭代交付什么，实际发生了什么？ | 稳定版本交付目录、版本交付记录和迭代契约 |

`LICENSE` 继续作为适用法律文件，不需要归入文档类别。临时 prompt、草稿笔记、未核实任务清单和对话记录属于工作材料，不是权威项目文档。

## 单一权威来源

每项持久事实只有一个主要位置。其他文档应链接或简短标识该来源，不复制其规则。

| 信息 | 主要来源 |
| --- | --- |
| 项目路由和项目特定授权边界 | `AGENTS.md` |
| 文档位置和维护规则 | `docs/documentation.md` |
| 产品方向和当前用户行为 | `overview.md`、需求及 `docs/product/` 下的适用文件 |
| 开发环境和构建入口 | `docs/development.md` |
| 验证方法、证据状态和执行权限 | `docs/validation.md` |
| 交付级别、制品、签名和发布操作 | `docs/release.md` |
| 版本范围、所选级别、门禁、迭代状态、证据与结果，以及版本结果 | 该版本的 `delivery.md` |
| 迭代目标、范围、约束、验收条件和验证要求 | 对应迭代契约 |
| 重大技术理由 | 对应的 Active 或 Superseded ADR |
| 角色权限 | Toolkit 角色定义和授权矩阵 |

当两份权威文档陈述同一规则时，应选择一个归属文件，并将另一处重复内容改为链接。交付记录可以概述其选择的范围，但不得成为第二份产品规格、验证指南、架构文档或发布规则。

## 当前产品契约与交付历史

当前产品文档描述持续维护的产品契约和预期产品行为。它们不表示每个历史版本或计划版本都已经完整实现当前契约。

版本交付记录描述所选范围和实际交付历史；迭代契约描述每个迭代的获授权边界和验收条件。完成结果应从版本交付记录出发，根据适用迭代契约、所选版本范围、交付级别和记录证据进行判断。当前产品契约后续发生的变化不会追溯改变该历史结果。

当交付记录需要解释边界时，应优先记录正向的所选范围和可观察验收标准。只有为了避免读者误解范围时才记录排除项。只有已经确认后续归属或未来承诺时才使用“延期”；未选中的能力不会自动成为延期能力。

## 当前权威文档

| 信息类型 | 英文或公共入口 | 中文入口 | 类别 | 职责 |
| --- | --- | --- | --- | --- |
| 项目入口 | [`README.md`](../README.md) | [`README.zh-CN.md`](../README.zh-CN.md) | 当前事实与规则 | 提供当前项目与产品摘要和深层文档链接 |
| 产品概览 | [`overview.md`](../overview.md) | [`overview.zh-CN.md`](../overview.zh-CN.md) | 当前事实与规则 | 记录产品方向、原则、边界和待确认范围 |
| Agent 路由 | [`AGENTS.md`](../AGENTS.md) | [`AGENTS.zh-CN.md`](../AGENTS.zh-CN.md) | 项目治理 | 记录 Toolkit 入口和项目级工作规则 |
| 文档治理 | [`docs/documentation.md`](documentation.md) | [`docs/documentation.zh-CN.md`](documentation.zh-CN.md) | 项目治理 | 定义文档职责、位置和维护规则 |
| 开发指南 | [`docs/development.md`](development.md) | [`docs/development.zh-CN.md`](development.zh-CN.md) | 当前事实与规则 | 定义当前开发环境、项目配置以及构建或运行入口 |
| 验证指南 | [`docs/validation.md`](validation.md) | [`docs/validation.zh-CN.md`](validation.zh-CN.md) | 当前事实与规则 | 定义可用检查、执行权限、人工验证、证据和结果报告 |
| 产品基础需求 | [`docs/requirements/product-foundation.md`](requirements/product-foundation.md) | [`docs/requirements/product-foundation.zh-CN.md`](requirements/product-foundation.zh-CN.md) | 当前事实与规则 | 记录产品问题、作者场景、当前范围、验收意图和开放产品问题 |
| 产品决策与范围变更 | [`docs/product-decisions.md`](product-decisions.md) | [`docs/product-decisions.zh-CN.md`](product-decisions.zh-CN.md) | 项目治理与决策理由 | 定义决策权，并在启用时记录已经确认的重大产品选择 |
| 产品导航 | [`docs/product/navigation.md`](product/navigation.md) | [`docs/product/navigation.zh-CN.md`](product/navigation.zh-CN.md) | 当前事实与规则 | 定义界面层级、进入、退出、Back、恢复和公共过渡 |
| Home 交互 | [`docs/product/surfaces/home.md`](product/surfaces/home.md) | [`docs/product/surfaces/home.zh-CN.md`](product/surfaces/home.zh-CN.md) | 当前事实与规则 | 定义 Home 信息、收藏、滚动、启动行为和编辑模式 |
| Drawer 交互 | [`docs/product/surfaces/drawer.md`](product/surfaces/drawer.md) | [`docs/product/surfaces/drawer.zh-CN.md`](product/surfaces/drawer.zh-CN.md) | 当前事实与规则 | 定义应用清单、分组、排序、字母索引和实时更新 |
| 应用操作面板 | [`docs/product/surfaces/app-action-sheet.md`](product/surfaces/app-action-sheet.md) | [`docs/product/surfaces/app-action-sheet.zh-CN.md`](product/surfaces/app-action-sheet.zh-CN.md) | 当前事实与规则 | 定义模态应用快捷操作和启动器操作 |
| Settings 交互 | [`docs/product/surfaces/settings.md`](product/surfaces/settings.md) | [`docs/product/surfaces/settings.zh-CN.md`](product/surfaces/settings.zh-CN.md) | 当前事实与规则 | 定义当前 Settings 信息与行为 |
| 双击锁屏 | [`docs/product/features/double-tap-lock.md`](product/features/double-tap-lock.md) | [`docs/product/features/double-tap-lock.zh-CN.md`](product/features/double-tap-lock.zh-CN.md) | 当前事实与规则 | 定义 Home 手势、可选无障碍服务边界、授权、失败与披露行为 |
| 隐私与数据处理 | [`docs/product/features/privacy.md`](product/features/privacy.md) | [`docs/product/features/privacy.zh-CN.md`](product/features/privacy.zh-CN.md) | 当前事实与用户可见正文 | 定义本地数据处理、备份与删除边界、Privacy 正文、联系行为及独立的无障碍显著披露 |
| 低保真线框图 | [`docs/product/low-fidelity-wireframes.md`](product/low-fidelity-wireframes.md) | [`docs/product/low-fidelity-wireframes.zh-CN.md`](product/low-fidelity-wireframes.zh-CN.md) | 当前产品可视化 | 可视化当前 Home、Drawer、应用操作面板与 Settings 契约，但不替代其规范性文字 |
| 产品设计基础约束 | [`docs/product/design-foundations.md`](product/design-foundations.md) | [`docs/product/design-foundations.zh-CN.md`](product/design-foundations.zh-CN.md) | 当前事实与规则 | 定义当前主题、布局、字体、图标、无障碍和资源原则 |
| 产品字典 | [`docs/product/glossary.md`](product/glossary.md) | [`docs/product/glossary.zh-CN.md`](product/glossary.zh-CN.md) | 当前事实与规则 | 定义规范产品术语 |
| 版本、产物与发布治理 | [`docs/release.md`](release.md) | [`docs/release.zh-CN.md`](release.zh-CN.md) | 当前事实与规则 | 定义交付级别、应用版本、已完成记录、APK 产物、签名连续性、tag 与 GitHub Release |
| 版本交付格式 | [`docs/versions/version-delivery-format.md`](versions/version-delivery-format.md) | [`docs/versions/version-delivery-format.zh-CN.md`](versions/version-delivery-format.zh-CN.md) | 项目治理 | 定义统一交付目录、交付级别选择、格式与迁移例外 |
| 迭代契约格式 | [`docs/iterations/iteration-record-format.md`](iterations/iteration-record-format.md) | [`docs/iterations/iteration-record-format.zh-CN.md`](iterations/iteration-record-format.zh-CN.md) | 项目治理 | 定义迭代命名、必需契约章节、验收边界与历史保护 |
| 1.0.0 已完成交付 | [`docs/delivery/1.0.0/delivery.md`](delivery/1.0.0/delivery.md) | [`docs/delivery/1.0.0/delivery.zh-CN.md`](delivery/1.0.0/delivery.zh-CN.md) | 交付历史 | 记录已完成的作者日常使用基线、纳入迭代、证据和已知缺口 |
| 1.1.0 已完成交付 | [`docs/delivery/1.1.0/delivery.md`](delivery/1.1.0/delivery.md) | [`docs/delivery/1.1.0/delivery.zh-CN.md`](delivery/1.1.0/delivery.zh-CN.md) | 交付历史 | 记录已完成的作者日常使用基线，包括全宽主收藏编辑、应用快捷操作、基础 Settings、可选双击锁屏、静态名称清理和版本收尾 |
| 1.2.0 规划交付 | [`docs/delivery/1.2.0/delivery.md`](delivery/1.2.0/delivery.md) | [`docs/delivery/1.2.0/delivery.zh-CN.md`](delivery/1.2.0/delivery.zh-CN.md) | 交付记录 | 记录当前已选择的版本范围、纳入迭代状态、证据、依赖、验证和最终结果；后续可增加经作者确认的迭代 |
| 架构决定 | [`docs/decisions/`](decisions/) | - | 决策理由 | 记录重大、已实现且已接受的架构决定；只有 Active ADR 才建立其所述当前架构边界 |
| 许可证 | [`LICENSE`](../LICENSE) | - | 法律文件 | 包含 Apache License 2.0 原文 |

当前活跃架构决定是 [ADR-0001](decisions/0001-establish-replaceable-launcher-icon-rendering.md)、[ADR-0002](decisions/0002-use-versioned-atomic-file-for-favorites.md) 和 [ADR-0003](decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)。

## 规划中的权威位置

下列文档仅在具备真实输入时创建：

| 路径 | 唯一职责 | 创建条件 | 语言策略 |
| --- | --- | --- | --- |
| `docs/architecture.md` | 系统边界、组件、依赖、数据流和技术方向 | 技术栈或当前产品定义需要形成架构结论 | 默认英文；存在持续跨语言阅读需求时补充中文版本 |
| `docs/security.md` | 安全模型、威胁、控制措施和响应流程 | 架构、权限、数据流或发行方式足以支持安全分析 | 默认英文；专业结论须复核，按需翻译 |
| `docs/privacy.md` | 数据清单、处理目的、保留方式和用户权利 | 数据、权限、地区或第三方处理行为得到确认 | 默认英文；专业结论须复核，面向用户的版本按发行要求提供翻译 |
| `CHANGELOG.md` | 用户可感知的版本变化 | 首个用户可感知版本或变更形成 | 英文公共语义源；按实际受众决定是否提供中文版本 |

## 产品文档职责

产品信息按以下三种职责分开维护：

1. **产品方向：** `overview.md` 记录持久的目的、原则、能力层级和长期边界。未来的 `docs/roadmap.md` 可以记录从 V1 到 V2、V3、V4 的能力层级演进及其间的重大项目结果，但必须比版本或迭代计划更宏观。
2. **当前产品定义：** Requirements Brief 和交互规格记录当前用户行为、状态、约束和验收意图。它们描述当前产品，不保留按版本累积的叙事历史。
3. **变更理由与交付历史：** 产品决定记录解释重要范围取舍；迭代和版本记录描述项目进展与实现演进，但不成为当前产品定义的第二份副本。

### 交互规格

仅在相关行为已具备定义条件时创建交互规格。将页面、弹窗、面板及其他可独立识别的界面表面契约放在 `docs/product/surfaces/` 下；将具有独立启用、授权、状态、失败、隐私或验收边界的相对独立功能放在 `docs/product/features/` 下；跨界面基础和公共产品规则直接保留在 `docs/product/` 下。例如：

- `docs/product/surfaces/home.md`
- `docs/product/surfaces/drawer.md`
- `docs/product/surfaces/settings.md`
- `docs/product/features/<feature>.md`
- 当多份规格实际共用交互契约时，创建 `docs/product/shared-components.md`

每份规格是其职责范围内的当前权威定义。应按主要产品责任而非实现位置分类：当独立边界正是建立该文档的原因时，一个从单一界面触发的功能仍可归入 `features/`。规格可以链接到界面表面、功能或公共组件规则，而不复制内容。交互规格不保留每个版本或迭代的时间顺序历史；该历史由产品决定、交付记录和 Git 历史承担。

当迭代改变当前行为时，在迭代契约中记录交付前后的范围；当作者启用决策记录后，遵循 `docs/product-decisions.md`；并在同一变更中或接入实现前更新受影响的当前产品规格。

## Roadmap、版本、迭代、里程碑与已完成记录

这些记录回答不同问题，不得互相替代，也不得替代当前产品定义。

### Roadmap

未来的 `docs/roadmap.md` 记录长期能力层级方向和重大项目结果。它可以描述 V1、V2、V3 与 V4 之间的演进，但不授权后续能力层级、不规定详细页面行为，也不跟踪普通实现任务。

### 版本交付记录

每个版本从初始规划到完成始终使用稳定的 `docs/delivery/<version>/` 目录，其中 `<version>` 是不带 `v` 前缀的准确 `versionName`。版本摘要和所属迭代放在一起：

```text
docs/delivery/<version>/
- delivery.md
- delivery.zh-CN.md
- iteration-<number>-<title>.md
- iteration-<number>-<title>.zh-CN.md
```

遵循 [`docs/versions/version-delivery-format.md`](versions/version-delivery-format.md)。`delivery.md` 包含所选产品范围、必要技术结论、纳入迭代、迭代状态与交付历史、验证、限制、完成条件和结果。只有确实需要独立评审时才创建单独技术评估；评估解决后，将持久结论写入其唯一的当前来源或交付来源，不长期维护重复文档。

目录名称和路径不表达生命周期状态。`delivery.md` 记录版本尚未完成还是已经完成，以及支持该结果的证据。

### 里程碑

在本项目中，里程碑是由项目作者明确宣告，并具有获批 Git tag 的例外性基线。GitHub Release 可选，只有作者另行选择对外发布时才创建。正式版本、迭代、未获批 tag，或者虽已获批但未被作者宣告为里程碑的 tag，都不会自动构成里程碑。里程碑不组织普通版本交付，项目不使用 `docs/milestones/` 目录。

### 迭代契约

当实现计划开始且存在真实迭代时，在 `docs/delivery/<version>/` 中与 `delivery.md` 同级为每个迭代创建一份独立迭代契约，并遵循 [`docs/iterations/iteration-record-format.md`](iterations/iteration-record-format.md)。不得用 `delivery.md` 中的内嵌章节替代该契约。

- 迭代标识在全项目范围内使用一组从 `1` 开始、不带前导零且单调递增的正整数序列。
- 版本完成后也不得重编号、复用或重新计数。
- 迭代是可评审的交付单元。其边界由实现难度、预计时间、变更广度、依赖、技术风险和验证成本共同决定，不单纯依据产品层级或固定功能数量。
- 一个迭代可实现当前产品定义的全部或一部分，也可合并产生一个可验证结果所必需的紧密耦合工作；但不得静默引入当前产品文档中不存在的范围。
- 每份契约应包含目标、产品文档引用、适用时的变更前后行为、范围、非目标、依赖、风险、持久层级的受影响代码区域、验收条件、验证要求，以及相关持久决定或评估。
- 契约在持久层级标识预期技术影响面和交付后果。版本 `delivery.md` 记录实际实现演进和证据；Git commit 和 diff 仍是逐行源码历史的权威来源。

迭代契约定义稳定的产品交付边界，不负责 `Planned`、`In Progress`、`Completed` 或 `Cancelled` 状态、转换日期与依据、实际证据、commit、tag、最终结果或剩余交付问题。同目录的版本 `delivery.md` 是这些项目交付事实的唯一权威来源。状态转换通常只修改 `delivery.md` 及其持续维护的语言对应稿；不得仅为同步状态而修改迭代契约。

迭代尚未完成时，获授权的修订可以更新其范围或验收边界。`delivery.md` 将迭代标为 `Completed` 或 `Cancelled` 后，应保留契约的历史含义，后续修改仅限已识别的事实、链接或翻译纠错。已有历史迭代记录不因采用该职责分离而重写。

### 已完成版本记录

版本完成时，保留稳定的 `docs/delivery/<version>/` 路径，并将 `delivery.md` 更新为事实性完成总结和入口。完成改变记录的历史保护规则，不改变其位置。

- 文件夹名始终使用不带 `v` 前缀的准确软件版本，并遵循 [`docs/release.md`](release.md)。不得添加 `-archived` 等生命周期后缀。是否存在 tag 不影响版本是否完成。
- 不得在归档、完成或其他状态专用目录中创建第二份权威副本。
- 总结使用 `<迭代标识> - <标题>` 列出每个纳入的迭代，链接到同一稳定版本目录中的原始迭代契约，并记录其权威状态、最近转换日期和依据。
- 总结记录版本结果、所含迭代范围或明确集合、重要产品变化、实现演进、决策、迁移、验证证据、已知限制、存在时的相关 tag 或 release，以及宣告该版本边界的理由。
- 版本完成不会重置全项目迭代序列。若某个已完成版本包含迭代 `iteration-7-...` 至 `iteration-10-...`，下一活动迭代必须是 `iteration-11-...`。
- 不得改写已完成迭代契约或历史记录来让后续历史显得更整洁。如需修正事实错误，应显式修正并保留其原始交付含义。
- 每个正式版本包含一个或多个已完成迭代。

在存在真实的计划或实现输入前，不创建空的 roadmap、版本或迭代文件。格式文档治理后续记录的创建方式，因此可以先于具体交付记录存在。

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

## 版本与历史保护

- 普通指南和当前状态文档随相关变更在同一提交中更新，不保留失效内容作为正文历史。
- ADR 使用 `0001-<decision>.md` 形式的四位递增编号，追加记录，不重编号、不复用编号、不改写历史决定。适用 Toolkit ADR 规则。
- Requirements Brief 应保持边界和验收标准可追踪。范围发生实质变化时，应显式记录变更，不静默覆盖当前产品定义。
- 保持当前产品规格为最新契约；在产品决定中保留重要理由，在迭代契约中保留迭代边界，在版本交付记录中保留交付历史。
- 产品范围变更需要项目作者明确决定，并在适用时完成技术影响评估。一项请求只有写入适用的权威文档后，才成为当前产品范围。
- 安全、隐私和发布记录应保留适用范围、版本或日期，以及必要的专业复核证据。
- 仅当失效文档仍具有决策、审计或迁移价值时才移入历史存储；否则删除。历史材料必须说明替代文档，且不得作为当前规则加载。

## 当前状态文档规则

- 仓库中可见的权威文档描述当前项目或产品状态，不携带生命周期状态字段。
- 尚未准备成为当前项目状态的内容保留在对话或 `max-dev-context` 等外部续接工作区中，不作为有效文档进入本产品仓库。
- 更新权威当前状态文档即改变适用的当前规则或定义。先前状态通过 Git 历史、决定和已完成交付记录保留，不在当前文档中使用状态标签。
- 代码存在后，接入变更前对比文档、实现、测试和验证证据。实质不一致必须显式解决，任何一方都不能静默替代另一方。
- 已完成的 `docs/delivery/<version>/` 目录对其描述的交付历史保持权威，但不定义当前产品或项目规则。路径稳定不代表它仍是活动工作。

## Git 与任务工作流

- 项目当前采用单线开发，不维护多分支或多人协作流程。仅在作者以后引入相关需要时，再定义分支与协作约定。
- 将每项可独立审查的操作视为一个任务。完成后报告结果与证据，并等待作者确认，再开始下一个任务。
- 修改文件不代表获得提交授权，提交不代表获得推送授权。只有项目作者明确授权本次串行接续时，Agent 才可以不中断地依次执行修改、提交与推送。
- 宽泛目标本身不授权其交付链中的所有后续写操作。为了报告当前任务而进行的只读检查和验证仍属于当前任务。
