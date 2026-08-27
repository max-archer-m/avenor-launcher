# Settings 表现规范

> 英文公共语义源：[settings.md](settings.md)。

## 职责

本文档负责 Settings 精确字体与行几何。[Settings 行为](../surfaces/settings.zh-CN.md)负责页面内容、导航和操作结果。

## 主要项目

- 主要标题使用 Material 3 `titleMedium`：`16sp/24sp`、中等字重和 `onSurface`。
- 支持文字使用 `bodySmall`：`12sp/16sp`、正常字重和 `onSurfaceVariant`。
- 双行项目至少高 `72dp`。单行项目至少高 `56dp`，并垂直居中标题。
- 主要项目使用 `16dp` 水平内容 padding；行为契约包含箭头时，末端使用 `24dp` Android 或 Material 箭头。

## 次要项目

- 次要项目使用居中的 Material 3 `titleSmall`：`14sp/20sp`、中等字重和 `onSurfaceVariant`。
- 每个次要项目至少高 `40dp`，内容水平和垂直居中。
