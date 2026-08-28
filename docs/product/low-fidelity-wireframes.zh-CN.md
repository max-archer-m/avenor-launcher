# Avenor Launcher 低保真线框图

> 英文语义源：[low-fidelity-wireframes.md](low-fidelity-wireframes.md)。

`docs/product/wireframes/` 下语言中立的 ASCII 画布用于可视化空间层级、区域关系和主要内容顺序。它们不独立定义产品行为、用户可见文案、精确视觉数值或实现结构。

## 阅读规则

- 画布标签是结构标注，不是用户可见文案。
- 点状填充只表示空间已经分配，不规定纹理、颜色、透明度或其他图层。
- `L1`、`L2` 或带编号应用条目等重复标签仅用于示意，不建立固定内容、容量或身份。
- 画布可以只展示一个代表性内容状态，不因此定义所有有效尺寸、排列、加载、错误或编辑状态。
- 画布边界表达构图，不代表设备配置或像素与字符的换算比例。线框说明中的尺寸是 ASCII 画布尺寸，不是产品 UI 数值。
- 应用名称仅用于示意。本地化、省略、字体、图标尺寸、间距、热区和视觉状态由适用的行为或表现契约定义。
- Home 未分配空间仍是透明背景，不构成未命名的产品模块。

## 契约路由

判断产品事实时应阅读负责该问题的文档，不应从线框图推断：

| 问题 | 行为责任文档 | 精确表现责任文档 |
| --- | --- | --- |
| Home 内容、编辑、拖动结果和容器生命周期 | [Home 交互](surfaces/home.zh-CN.md) | [Home 表现](presentation/home.zh-CN.md) |
| Drawer 清单、排序、选择、索引行为和设置操作 | [Drawer 交互](surfaces/drawer.zh-CN.md) | [Drawer 表现](presentation/drawer.zh-CN.md) |
| 应用操作的可用性、顺序语义和结果 | [应用操作面板](surfaces/app-action-sheet.zh-CN.md) | [应用操作面板表现](presentation/app-action-sheet.zh-CN.md) |
| Settings 内容、导航和结果 | [Settings 交互](surfaces/settings.zh-CN.md) | [Settings 表现](presentation/settings.zh-CN.md) |
| 跨界面转换和系统返回行为 | [导航](navigation.zh-CN.md) | 适用界面的表现规范 |

## 线框索引

- [Home](wireframes/home.txt) — 完整画布，表达内容驱动的纵向区域、代表性的双列表状态及未分配透明空间；其他有效状态由 Home 契约定义。
- [Home 编辑模式](wireframes/home-edit-mode.txt) — 完整画布，表达编辑区域、列表控件、新增操作、临时容器和独立滚动关系。
- [Drawer](wireframes/drawer.txt) — 代表性 Content 状态画布，表达固定顶部区域、普通与多选语义、列表锚点、Settings 分组和字母索引。
- [应用操作面板](wireframes/app-action-sheet.txt) — 模态状态画布，表达被阻断背景、遮罩和内容顺序。
- [Settings](wireframes/settings.txt) — 完整画布，表达导航、Launcher 状态和产品信息区域。

## 权威与更新规则

线框图是可视化辅助，不是独立产品决定。与规范性文字不一致时，操作结果以适用的行为契约为准，精确视觉数值以适用的表现规范为准。

已经确认的空间层级、区域关系或主要内容顺序发生变化时，应在同一次文档变更中更新线框图。仅改变行为或表现 token 时，如果画布结构不会因此产生误导，则不要求修改线框图。
