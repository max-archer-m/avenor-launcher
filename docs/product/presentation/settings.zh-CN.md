# Settings 表现规范

> 英文公共语义源：[settings.md](settings.md)。

## 职责

本文档负责 Settings 精确字体与行几何。[Settings 行为](../surfaces/settings.zh-CN.md)负责页面内容、导航和操作结果；[设计基础](../design-foundations.zh-CN.md)负责共享颜色与字号 token。

## 顶部应用栏

- 固定顶部应用栏至少高 `56dp`，标题与末端内容使用 `16dp` 水平 padding。
- 可见返回图形使用共享 `24dp` 功能图标 token，居中于从安全起始边缘 `12dp` 处开始的标准 `48dp x 48dp` 图标按钮目标中，图形起始边缘因此距安全起始边缘 `24dp`，与 [Drawer 顶部应用栏](drawer.zh-CN.md)一致。返回控件提供本地化无障碍名称。
- 标题使用 Material 3 `titleLarge` 和共享 `primaryTextColor`。使用共享 `secondaryTextColor` 的全宽 `1dp` 分割线将应用栏与可滚动设置项列表分开，遵循[设计基础](../design-foundations.zh-CN.md)定义的共享顶栏分割线处理。

## 主要条目

- 主要标题使用共享 `primaryTextFontSize` 及其行高，尺寸等同 Material 3 `titleMedium`，并使用中等字重和 `primaryTextColor`。
- 支持文字使用共享 `secondaryTextFontSize` 及其行高，尺寸等同 Material 3 `bodyMedium`，并使用正常字重和 `secondaryTextColor`。
- 双行条目至少高 `72dp`。单行条目至少高 `56dp`，并垂直居中标题。
- 主要条目使用 `16dp` 水平内容 padding；行为契约包含箭头时，末端使用 `24dp` Android 或 Material 箭头。

## 次要条目

- 次要条目使用共享 `secondaryTextFontSize` 及其行高，尺寸等同居中的 Material 3 `titleSmall`，并使用中等字重和 `secondaryTextColor`。
- 每个次要条目至少高 `40dp`，内容水平和垂直居中。可点击次要条目使用完整的全宽行作为交互目标，普通最小热区为 `40dp`，不在不可见区域扩展至 `48dp`；系统字体缩放需要更多高度时允许该行自然增长。这是作者明确接受且只适用于 Settings 次要条目的目标尺寸例外，不改变主要条目、返回控件或其他普通图标控件。版本信息保持相同行几何，但不可交互。

## 快捷操作设置页与选择弹窗

- `快捷操作设置` 页复用上文定义的 Settings 顶部应用栏与主要设置项行几何。页面标题为 `快捷操作设置`，每个条目由标题与副标题组成。
- 页面上的每个条目都带主要设置项处理所定义的标准右侧箭头，与 Settings 页一致。两个槽位条目同样带箭头，尽管它们打开的是本地选择弹窗而不是另一个界面。
- 选择弹窗是一个带锚点的深色弹窗，内部为单个纵向条目序列。规范只描述可观察结果，不规定容器类型。
- 弹窗使用共享 `darkSurfaceBaseColor` 背景与准确 `12dp` 圆角。每个选项条目高 `48dp`、左右 padding 为 `16dp`，内部为一个前置 `20dp` radio 单选圆点与其后的动作名，两者间距 `12dp`。圆点与动作名都使用 `primaryTextColor`，动作名使用共享 `primaryTextFontSize`。当前绑定在该条目的圆点上标记。
- 每个选项条目提供共享 Material ripple，并被裁剪到不超出弹窗圆角边界；因此首末两个条目的 ripple 遵循弹窗的 `12dp` 圆角。
- 弹窗不展示蒙层，也不使页面变暗。它锚定在被点击的条目上而不是屏幕居中：其顶边与该条目的垂直中点对齐，其尾边距安全尾部屏幕边 `16dp`。由于选项数量很少，不定义底部对齐或底部钳制。
- 弹窗宽度按内容自适应。结合其固定 `16dp` 尾边 inset，弹窗始终位于安全屏幕区域内，且自身不产生横向滚动。

## 信息 Bottom Sheet

- 锁屏说明、Privacy、Avenor License，以及日后经过作者接受的第三方 License，复用[应用操作面板表现规范](app-action-sheet.zh-CN.md)定义的外框几何：占满当前竖屏手机可用宽度、`12dp` 顶部圆角、高度自然增长至状态栏安全边界、全局内容 padding 为 `0dp`，并使用共享的 `32dp x 4dp` 拖动柄及其上下各 `12dp` 垂直 padding。
- 这些信息面板不继承应用身份行、快捷操作行、五个 Launcher 操作槽位或 badge。它们使用一个至少高 `48dp`、带 `16dp` 水平内容 inset 的固定标题行，以及一个使用 `16dp` 内容 padding 的正文区域。内容超过可用高度时只有正文滚动；标题和拖动柄保持可见。
- 无障碍显著披露继续使用独立 Material 3 Dialog，并保留自身的明确同意与取消操作；它不继承 Bottom Sheet 几何。
- Settings 不显示可见的`关于`分组标题。行为文档中的`关于`只用于组织文档，界面通过次要条目顺序表达分组。
