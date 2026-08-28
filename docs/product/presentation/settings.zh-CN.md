# Settings 表现规范

> 英文公共语义源：[settings.md](settings.md)。

## 职责

本文档负责 Settings 精确字体与行几何。[Settings 行为](../surfaces/settings.zh-CN.md)负责页面内容、导航和操作结果。

## 顶部应用栏

- 固定顶部应用栏至少高 `56dp`，使用 `16dp` 水平 padding。
- 可见返回图形使用共享 `24dp` 功能图标 token，位于标准 `48dp x 48dp` 图标按钮目标中，并提供本地化无障碍名称。
- 标题使用 Material 3 `titleLarge` 和 `onSurface`。使用 `onSurfaceVariant` 的全宽分割线将应用栏与可滚动设置项列表分开。

## 主要项目

- 主要标题使用 Material 3 `titleMedium`：`16sp/24sp`、中等字重和 `onSurface`。
- 支持文字使用 `bodySmall`：`12sp/16sp`、正常字重和 `onSurfaceVariant`。
- 双行项目至少高 `72dp`。单行项目至少高 `56dp`，并垂直居中标题。
- 主要项目使用 `16dp` 水平内容 padding；行为契约包含箭头时，末端使用 `24dp` Android 或 Material 箭头。

## 次要项目

- 次要项目使用居中的 Material 3 `titleSmall`：`14sp/20sp`、中等字重和 `onSurfaceVariant`。
- 每个次要项目至少高 `40dp`，内容水平和垂直居中。
