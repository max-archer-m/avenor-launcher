# 版本交付文档格式

> 英文语义源：[version-delivery-format.md](version-delivery-format.md)。

## 目的

`docs/delivery/<version>/` 从初始规划到完成始终保存一份版本交付文档及其全部迭代契约。版本目录从当前产品定义中选择工作，不重定义产品，也不表示该版本必须交付当前产品的全部行为。

版本 `delivery.md` 是迭代状态和交付历史的唯一权威来源。每份独立迭代文档是稳定的产品交付契约，不重复维护持续变化的状态、实际证据、commit 或最终结果。

版本或迭代获授权后，当前产品定义仍可能继续变化。因此，版本记录描述的是适用交付基线下的所选范围，不要求匹配当前产品定义之后的每次修订。完成结论只适用于该版本记录的所选范围、验收标准、交付级别和证据。

使用不带 `v` 前缀的准确 `versionName`，例如 `docs/delivery/1.0.0/`。不得添加 `-active`、`-completed` 或 `-archived` 等生命周期后缀。

## 必需结构

存在真实交付输入时创建版本文档；只有存在真实规划输入时才创建迭代契约：

```text
docs/delivery/<version>/
- delivery.md
- delivery.zh-CN.md
- iteration-<number>-<title>.md
- iteration-<number>-<title>.zh-CN.md
```

- `delivery.md` 是版本用户价值、所选范围、排除项、必要技术结论、纳入迭代、迭代状态与交付历史、验证、已知限制、完成条件和结果的英文语义源。当交付边界的理解需要时，应标明适用的产品契约基线。
- 每份 `delivery.md` 必须且只能声明[发布治理](../release.zh-CN.md)中的一个交付级别：`Development build`、`Author daily-use baseline` 或 `Formal release artifact`。它只适用该级别门禁及任何被明确提升的版本特定门禁。`Development build` 不能完成正式应用版本。
- 每个迭代都在 `delivery.md` 同级拥有独立契约，继续使用全项目编号序列，并链接版本文档而不复制版本级规则或执行状态。
- 只有确实需要独立技术评审时才创建单独技术评估。它是支撑分析，不是必需层级。问题解决后，将持久结论写入适用的产品、架构、开发、验证、发布、决定或交付来源，不长期维护重复结论。

## 版本文档格式

```markdown
# <产品> <版本> Delivery

> 语义源说明与授权边界

## Version intent
## Delivery level
## Product references
## Included scope and user journey
## Exclusions
## Technical approach and risks
## Included iterations
| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [迭代 <number>：<标题>](iteration-<number>-<title>.zh-CN.md) | `Planned` | YYYY-MM-DD | <当前状态适用的依据> |

## Iteration evidence and results
## Dependencies and sequence
## Validation
## Artifact and release requirements
## Known limitations and legacy issues
## Completion criteria
## Completion result
```

只有确实不适用时才删除章节。详细产品行为、验证方法、架构和发布规则应链接其权威来源，不复制正文。`Delivery level` 标明一个准确级别及任何明确提升的门禁。`Included scope and user journey` 应定义正向交付边界和可观察结果。`Exclusions` 只记录容易引起范围误解的非目标；只有确认后续归属或承诺时才使用“延期”。完成前，`Completion criteria` 陈述所需证据，`Completion result` 说明尚无最终结果；完成后，`Completion result` 记录事实结果，并明确该结果只适用于所选版本范围。

## 迭代状态和结果规则

- `Included iterations` 列出版本中的每份独立迭代契约，并且是迭代状态的唯一权威位置。每行必须且只能使用以下封闭枚举中的一个状态值：`Planned`、`In Progress`、`Completed` 或 `Cancelled`。
- `Updated` 记录最近一次状态转换日期。`Basis` 简要记录支持当前状态的作者决定或证据。普通契约或交付文档编辑不得改变 `Updated`。
- `Planned` 允许按作者方向修改契约，并开展确认可行性所需的技术调研；它不授权生产代码实现。
- `In Progress` 从项目作者授权生产代码实现时开始。已授权契约范围内的文档、代码、测试和本地验证可以继续进行。
- `Completed` 表示已授权实现完成、项目作者已接受可观察结果，并且实现及适用交付文档均已 commit 并同步到作者指定的共享 Git 历史。建议验证或版本级验证可以尚未完成，但每项已知缺口都必须准确记录，且不得视为通过。
- `Cancelled` 表示作者在完成前停止迭代。必须记录原因、已经交付或部分交付的工作、受影响契约、依赖和迁移后果、已有代码或数据的处置，以及必需的后续动作。取消本身不授权回退或删除工作。
- 状态转换通常在同一变更中同时更新 `delivery.md` 及其持续维护的语言对应稿。不得仅为同步状态、证据、commit 或结果而修改独立迭代契约。
- `Iteration evidence and results` 必须为 `Included iterations` 中的每一行提供一个可明确对应的小节或记录。每条记录链接适用契约，并记录实际命令、环境、设备、结果、无法执行或跳过的检查、失败、已知缺口、后续归属、相关 commit 或 tag，以及最终作者决定。没有对应记录时，不得将状态推进为 `Completed` 或 `Cancelled`。
- 如果契约修订实质改变迭代目标、主要结果、产品范围、架构方向、依赖顺序、验收条件或验证义务，应在完成前将明确修订写入契约。修订必须记录日期、作者决定、原因、原边界、新边界以及受影响的验收或验证义务。若修改会替换迭代身份，应将原迭代标为 `Cancelled`，在交付记录中链接替代契约，并在替代契约中链接已取消迭代。
- 所有纳入迭代均标为 `Completed` 本身并不完成正式版本。版本级完成、验证、签名、制品追踪、归档和发布门禁保持独立。
- 该状态归属分离规则适用于新建及之后修订的记录。已有历史迭代文档可以作为 legacy 历史保留原有状态、证据和结果章节；不得仅为采用本规则而重写它们或回填其版本交付记录。

## 版本完成与历史保护

版本完成后，在 `delivery.md` 中更新事实结果，并保留同一个 `docs/delivery/<version>/` 路径。已完成版本文档及其迭代契约成为受保护的交付历史：除修正已识别的事实、链接或翻译错误且不改变历史含义外，不得改写版本证据或结果，也不得改写契约范围或验收含义。版本完成不要求 tag 或 GitHub Release。

## 里程碑边界

在本项目中，**里程碑**是由作者明确宣告，并具有获批 Git tag 的例外性项目基线。GitHub Release 可选，只有作者同时选择对外发布时才创建。正式版本、迭代、未获批 tag，或者虽已获批但未被作者宣告为里程碑的 tag，都不会自动构成里程碑。普通版本规划不得依赖里程碑。

该定义有意窄于通用项目管理用法；只有获得授权并更新适用治理文档后才能修改。
