# 版本交付文档

> 英文语义源：[README.md](README.md)。

## 目的

`docs/versions/` 保存每个正式应用版本的活动交付契约及其支撑输入。版本目录从当前产品契约中选择工作，不重定义产品，也不表示该版本必须交付当前产品的全部行为。

使用不带 `v` 前缀的准确 `versionName` 创建 `docs/versions/<version>/`，例如 `docs/versions/1.0.0/`。

## 必需结构

仅在存在真实输入时创建对应文件：

```text
docs/versions/<version>/
├── product-scope.md
├── product-scope.zh-CN.md       # 需要持续维护中文对应文档时
├── technical-assessment.md
└── README.md
```

- `product-scope.md` 是用户价值、所选当前产品范围、排除项和产品验收意图的英文语义源。
- `technical-assessment.md` 记录可行性证据、约束、备选方案、依赖、迁移成本、风险和验证建议，不得静默更改产品范围。
- 当产品范围和技术评估提供足够证据后，`README.md` 成为整合后的版本交付契约。它定义获批迭代集合、依赖、风险、版本退出门禁和必要交接，但不复制详细产品或技术来源。
- 活动迭代记录仍位于 [`docs/iterations/`](../iterations/)，并链接适用的版本契约。

## 产品范围格式

版本产品范围文档使用以下章节顺序；只有确实不适用的章节才可删除：

```markdown
# <产品> <版本> Product Scope

> 语义源与对应文档说明
> 文档职责与非授权边界

## Version intent
## Authoritative product references
## Included user journey
## Included product scope
## Explicitly excluded from <version>
## Product acceptance intent
## Technical assessment inputs
## Version and release boundary
## Completion handoff
```

## 技术评估格式

```markdown
# <产品> <版本> Technical Assessment

> 与产品范围的关系及决策权边界

## Assessment question
## Inputs and evidence
## Platform and compatibility findings
## Proposed system boundaries
## Data, identity, persistence, and migration
## Permissions, security, privacy, and licensing impact
## Dependencies and alternatives
## Build and validation approach
## Quality-gate proposals
## Delivery risks and unresolved decisions
## Iteration recommendations
## Product-scope impact proposals
## Assessment conclusion
```

建立架构决策记录机制后，重大且持久的架构选择应另行写入 ADR。技术评估可以建议决策，但不授权实现，也不改变产品契约。

## 整合版本契约格式

```markdown
# <产品> <版本> Delivery Contract

> 适用产品范围、技术评估与授权边界

## Version outcome
## Included and excluded scope
## Technical feasibility conclusion
## Included iterations
## Dependencies and sequence
## Risks and required decisions
## Validation and exit gates
## Artifact, signing, and archive requirements
## Known limitations and legacy issues
## Completion result
```

完成前，`Completion result` 定义关闭版本所需的证据；完成后，它记录实际结果。不得用生命周期标签代替证据。

## 版本完成与归档

版本正式完成后，按照[发布治理](../release.zh-CN.md)将整合契约、支撑输入和纳入迭代的原始记录移入 `docs/archives/v<version>/`，并在移动时更新链接。版本完成不要求 tag 或 GitHub Release。

## 里程碑边界

在本项目中，**里程碑**是由作者明确宣告，并具有获批 Git tag 的例外性项目基线。GitHub Release 可选，只有作者同时选择对外发布时才创建。正式版本、迭代、未获批 tag，或者虽已获批但未被作者宣告为里程碑的 tag，都不会自动构成里程碑。普通版本规划不得依赖里程碑。

该定义有意窄于通用项目管理用法；只有获得授权并更新适用治理文档后才能修改。
