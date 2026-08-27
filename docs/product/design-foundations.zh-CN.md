# 产品设计基础

> 英文公共语义源：[design-foundations.md](design-foundations.md)。

## 职责

本文档定义共享主题、布局、字体、图标、交互、无障碍和资源原则。精确组件与界面数值属于适用表现规范：

- [Home 表现](presentation/home.zh-CN.md)
- [Drawer 表现](presentation/drawer.zh-CN.md)
- [应用操作面板表现](presentation/app-action-sheet.zh-CN.md)
- [Settings 表现](presentation/settings.zh-CN.md)

适用导航、界面或功能规范负责产品状态和操作结果。表现规范不得增加行为契约未包含的控件或行为。

## 当前主题

- Avenor 所有界面均使用深色主题。文字、图标、控件和语义颜色角色使用其深色主题表现。
- Home 和 Drawer 不绘制可见应用背景，并请求透明界面与系统栏区域，使系统背景保持可见。
- Avenor 不为 Home 或 Drawer 添加持久背景渐变、固定遮罩、模糊、玻璃效果或其他对比度保护层。组件规范定义的瞬时局部交互提示仍可使用。
- Settings 使用不透明的标准 Material 3 深色配色方案。使用 Material 语义角色，不创建页面特定的深色十六进制颜色。
- 模态面板保持深色，并保留浅色状态栏图标。
- 平台或设备对比度强制保持默认行为。Avenor 不进入沉浸模式，也不隐藏系统导航。
- 主题自定义属于当前契约外的未来增量能力。

## 共享布局与字体

- 间距、字体、颜色、形状和可见尺寸使用语义设计 token，不使用任意页面局部字面量。
- 布局和可达性主要针对右手持机、右拇指操作，以及左手持机、右手点击进行优化。其他姿势属于次要考虑。
- 字体遵循系统字体缩放。应用名称保持静态单行并使用尾部省略，不使用跑马灯。
- 当前个人使用布局不单独优化极端字体缩放。如果极端缩放超过单行组件边界，文字仍裁剪在该边界内。

## 共享图标与应用身份

- 标准功能图标为 `24dp x 24dp`。该数值描述可见图形，不表示完整交互边界。
- 可独立交互的标准功能图标置于至少 `48dp x 48dp` 的可聚焦目标内。图标与标签共同形成一个操作时，完整组合项是一个交互与无障碍目标。
- 独立的非交互状态图标为 `40dp x 40dp`，没有独立触控目标或 ripple。
- 功能图标与状态图标使用适用语义内容颜色和一致的视觉粗细。纯图标控件具有本地化无障碍名称；装饰图标或已由相邻标签命名的图标不暴露重复说明。
- 定义共享图标 token 不会向界面增加对应控件。
- 原生自适应应用图标遵循当前设备蒙版。旧式图标在该蒙版内规范化，同时保留可识别图形。平台 clone 或 profile badge 在规范化后应用，并在各应用界面保持一致。
- 无法加载应用图形时，使用 Android 平台默认通用应用图标，并应用相同规范化和 badge 规则。不得替换为无关的 Avenor 图标。
- 当前目标设备预计提供 clone 或 profile badge；平台未提供时，Avenor 不添加后备 badge 或次级身份标签。
- 当前不要求与 OEM 专有阴影、图标包、主题服务或其他 Launcher 特效完全一致。

## 共享交互与无障碍

- 交互控件应提供至少 `48dp x 48dp` 的可聚焦目标。更小的组件特定目标需要作者接受的理由和聚焦真机证据；密集布局应先分离可见尺寸与命中区域。
- 按下、聚焦、选中和禁用状态不得仅依赖颜色。
- 除非组件契约定义其他可见按下状态，每个可用的点击或长按目标从首次按下开始提供有界 Material ripple。提示裁剪在实际目标内；当输入转为滚动、拖动、界面转换、取消或其他非点击交互时取消。
- 当前深色主题 ripple 从浅色前景内容角色派生。未来浅色主题应从对应前景角色派生；该规则不会增加当前浅色主题支持。
- Ripple 表示按下，不表示操作成功。禁用目标不显示可操作 ripple。
- 触觉反馈遵循系统能力和用户设置。准确平台常量需要实现验证。

## 资源化数值

面向用户的字符串、颜色、尺寸及其他可复用表现数值必须使用资源，并按需要支持本地化或主题化。不得在产品 UI 代码中散布硬编码字面量。准确 Android 资源与 Compose 访问结构仍属于实现关注点。

## 官方参考

- [Material 3 in Compose](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [Material 3 ripple API](https://developer.android.com/reference/kotlin/androidx.compose.material3/package-summary#ripple(androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Shape,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean))
- [Android 无障碍：让应用更易于使用](https://developer.android.com/guide/topics/ui/accessibility/apps.html)
