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
- 每个次要项目至少高 `40dp`，内容水平和垂直居中。可点击次要项目使用完整的全宽行作为交互目标，普通最小热区为 `40dp`，不在不可见区域扩展至 `48dp`；系统字体缩放需要更多高度时允许该行自然增长。这是作者明确接受且只适用于 Settings 次要项目的目标尺寸例外，不改变主要项目、返回控件或其他普通图标控件。版本信息保持相同行几何，但不可交互。

## 信息 Bottom Sheet

- 双击锁屏说明、Privacy、Avenor License，以及日后经过作者接受的第三方 License，复用[应用操作面板表现规范](app-action-sheet.zh-CN.md)定义的外框几何：占满当前竖屏手机可用宽度、`12dp` 顶部圆角、高度自然增长至状态栏安全边界、全局内容 padding 为 `0dp`，并使用共享的 `32dp x 4dp` 拖动柄及其上下各 `12dp` 垂直 padding。
- 这些信息面板不继承应用身份行、快捷操作行、五个 Launcher 操作槽位或 badge。它们使用一个至少高 `48dp`、带 `16dp` 水平内容 inset 的固定标题行，以及一个使用 `16dp` 内容 padding 的正文区域。内容超过可用高度时只有正文滚动；标题和拖动柄保持可见。
- 无障碍显著披露继续使用独立 Material 3 Dialog，并保留自身的明确同意与取消操作；它不继承 Bottom Sheet 几何。
- Settings 不显示可见的`关于`分组标题。行为文档中的`关于`只用于组织文档，界面通过次要项目顺序表达分组。
