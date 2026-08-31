# Settings 表现规范

> 英文公共语义源：[settings.md](settings.md)。

## 职责

本文档负责 Settings 精确字体与行几何。[Settings 行为](../surfaces/settings.zh-CN.md)负责页面内容、导航和操作结果；[设计基础](../design-foundations.zh-CN.md)负责共享颜色与字号 token。

## 顶部应用栏

- 固定顶部应用栏至少高 `56dp`，使用 `16dp` 水平 padding。
- 可见返回图形使用共享 `24dp` 功能图标 token，位于标准 `48dp x 48dp` 图标按钮目标中，并提供本地化无障碍名称。
- 标题使用 Material 3 `titleLarge` 和共享 `primaryTextColor`。使用共享 `secondaryTextColor` 的全宽分割线将应用栏与可滚动设置项列表分开。

## 主要项目

- 主要标题使用共享 `primaryTextFontSize` 及其行高，尺寸等同 Material 3 `titleMedium`，并使用中等字重和 `primaryTextColor`。
- 支持文字使用共享 `secondaryTextFontSize` 及其行高，尺寸等同 Material 3 `bodyMedium`，并使用正常字重和 `secondaryTextColor`。
- 双行项目至少高 `72dp`。单行项目至少高 `56dp`，并垂直居中标题。
- 主要项目使用 `16dp` 水平内容 padding；行为契约包含箭头时，末端使用 `24dp` Android 或 Material 箭头。

## 次要项目

- 次要项目使用共享 `secondaryTextFontSize` 及其行高，尺寸等同居中的 Material 3 `titleSmall`，并使用中等字重和 `secondaryTextColor`。
- 每个次要项目至少高 `40dp`，内容水平和垂直居中。
