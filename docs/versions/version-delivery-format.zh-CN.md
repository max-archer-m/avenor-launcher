# 版本交付文档格式

> 英文语义源：[version-delivery-format.md](version-delivery-format.md)。

## 目的

`docs/delivery/<version>/` 从初始规划到完成始终保存一份版本交付文档及其全部迭代记录。版本目录从当前产品定义中选择工作，不重定义产品，也不表示该版本必须交付当前产品的全部行为。

使用不带 `v` 前缀的准确 `versionName`，例如 `docs/delivery/1.0.0/`。不得添加 `-active`、`-completed` 或 `-archived` 等生命周期后缀。

## 必需结构

存在真实交付输入时创建版本文档；只有存在真实规划输入时才创建迭代记录：

```text
docs/delivery/<version>/
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

## 版本完成与历史保护

版本完成后，在 `delivery.md` 中更新事实结果，并保留同一个 `docs/delivery/<version>/` 路径。已完成版本文档及其迭代记录成为受保护的交付历史：除修正已识别的事实或链接错误且不改变历史含义外，不得改写其范围、证据或结果。版本完成不要求 tag 或 GitHub Release。

## 里程碑边界

在本项目中，**里程碑**是由作者明确宣告，并具有获批 Git tag 的例外性项目基线。GitHub Release 可选，只有作者同时选择对外发布时才创建。正式版本、迭代、未获批 tag，或者虽已获批但未被作者宣告为里程碑的 tag，都不会自动构成里程碑。普通版本规划不得依赖里程碑。

该定义有意窄于通用项目管理用法；只有获得授权并更新适用治理文档后才能修改。
