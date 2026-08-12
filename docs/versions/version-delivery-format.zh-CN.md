# 版本交付文档格式

> 英文语义源：[version-delivery-format.md](version-delivery-format.md)。

## 目的

对于 `1.0.0` 之后创建的版本，`docs/delivery/active/<version>/` 保存一份版本交付文档及其全部迭代记录。版本目录从当前产品定义中选择工作，不重定义产品，也不表示该版本必须交付当前产品的全部行为。

使用不带 `v` 前缀的准确 `versionName`，例如 `docs/delivery/active/1.1.0/`。

## 必需结构

存在真实交付输入时创建版本文档；只有存在真实规划输入时才创建迭代记录：

```text
docs/delivery/active/<version>/
- delivery.md
- delivery.zh-CN.md
- iteration-<number>-<title>.md
- iteration-<number>-<title>.zh-CN.md
```

- `delivery.md` 是版本用户价值、所选范围、排除项、必要技术结论、纳入迭代、验证、已知限制、完成条件和结果的英文语义源。
- 每份 `delivery.md` 必须且只能声明[发布治理](../release.zh-CN.md)中的一个交付级别：`Development build`、`Author daily-use baseline` 或 `Formal release artifact`。它只适用该级别门禁及任何被明确提升的版本特定门禁。`Development build` 不能完成正式应用版本。
- 迭代记录与 `delivery.md` 同级存放，继续使用全项目编号序列，并链接版本文档而不复制版本级规则。
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
## Dependencies and sequence
## Validation
## Artifact and release requirements
## Known limitations and legacy issues
## Completion criteria
## Completion result
```

只有确实不适用时才删除章节。详细产品行为、验证方法、架构和发布规则应链接其权威来源，不复制正文。`Delivery level` 标明一个准确级别及任何明确提升的门禁。完成前，`Completion criteria` 陈述所需证据，`Completion result` 说明尚无最终结果；完成后，`Completion result` 记录事实结果。

## 版本完成与归档

版本完成后，更新 `delivery.md`，并将整个目录从 `docs/delivery/active/<version>/` 移至 `docs/delivery/archives/<version>/`。移动时更新入站链接。版本完成不要求 tag 或 GitHub Release。

## 旧结构 `1.0.0` 例外

活动版本 `1.0.0` 早于本格式。其 `product-scope.md`、`technical-assessment.md` 和 `delivery-contract.md` 继续位于 `docs/versions/1.0.0/`，迭代记录继续位于 `docs/iterations/`。不得仅为采用本格式而迁移或重写。`1.0.0` 完成后，按其现有交付文档归档至 `docs/archives/v1.0.0/`。后续版本全部使用上述统一结构。

## 里程碑边界

在本项目中，**里程碑**是由作者明确宣告，并具有获批 Git tag 的例外性项目基线。GitHub Release 可选，只有作者同时选择对外发布时才创建。正式版本、迭代、未获批 tag，或者虽已获批但未被作者宣告为里程碑的 tag，都不会自动构成里程碑。普通版本规划不得依赖里程碑。

该定义有意窄于通用项目管理用法；只有获得授权并更新适用治理文档后才能修改。
