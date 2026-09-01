# 产品字典

> 英文语义源：[glossary.md](glossary.md)。

| 规范英文术语 | 中文工作术语 | 含义 |
| --- | --- | --- |
| Home | 主页面 | 包含时间、日期、星期和收藏应用的 Avenor Launcher 主界面；未加限定的 `Home` 永远不表示 Android 平台导航操作 |
| Android system Home action | Android 系统 Home 操作 | 请求显示当前默认 Launcher 的平台操作，无论它来自手势导航、导航按钮、物理按键还是其他系统入口 |
| Drawer | 应用列表 | 平台暴露的全部可启动应用条目的带索引列表 |
| Settings | 设置 | Avenor 配置与产品信息界面 |
| Launchable entry | 可启动应用条目 | Avenor 可以启动的一个平台目标；主应用与分身是不同条目 |
| Favorite | 收藏应用 | 只保存到一个 Home 收藏模块的可启动应用条目；同一稳定身份不得跨模块重复 |
| Favorite main list | 收藏主列表 | Home 基础信息下方，由同级纵向收藏模块和横向收藏织带组成的一个全宽、可纵向滚动有序序列 |
| Favorite module | 收藏模块 | 至少包含一个收藏的持久纵向收藏模块或横向收藏织带 |
| Vertical favorite module | 纵向收藏模块 | 全宽模块，其自然展开的全部条目共享一组模块级尺寸、名称位置和每行数量样式 |
| Horizontal favorite ribbon | 横向收藏织带 | 全宽单行模块，包含固定样式、按内容测量的收藏条目，仅在溢出时横向滚动 |
| Add-favorite entry | 新增收藏入口 | Home 编辑模式中位于收藏主列表或模块内部的非持久条目；仅由其位置决定它创建新模块还是加入既有模块 |
| Edit dock | 编辑坞 | Home 编辑模式底部固定区域，在面板收起与展开时都保持可见，并包含展开或收起非模态 Home 样式设置面板的入口 |
| Style settings panel | 样式设置面板 | Drawer 显示设置与可展开 Home 模块样式编辑器共同使用的圆角设置面板表现；各宿主分别定义自身内容、模态性、位置和行为 |
| Application shortcut | 应用快捷操作 | 应用通过平台暴露的操作 |
| Launcher action | 启动器操作 | Avenor 提供的操作，例如收藏、编辑或卸载 |
| Application action sheet | 应用操作面板 | 包含应用身份、应用快捷操作和启动器操作的模态 Bottom Sheet |
| Section anchor | 分组锚点 | Drawer 中的分组标题，例如 A 或 `#`；内嵌展示位于分组上方并随列表滚动，左侧展示占据所属分组的起始侧列，并只在该分组经过视口期间吸附于固定顶部应用栏下方 |
| Alphabet index | 字母索引 | Drawer 右侧用于跳转到锚点的固定索引 |
| Edit mode | 编辑模式 | 从收藏操作或合格空白区域长按进入，在 Home 中显示编辑坞、模块移动、应用编辑和新增收藏入口的状态 |
| Favorite multi-selection | 收藏多选 | Drawer 的临时模式，为一个已记录的 Home 收藏目标收集一组有顺序且此前未收藏的应用 |
| Double-tap lock | 双击锁屏 | 通过 Avenor 用途受限的无障碍服务请求一次 Android 系统锁屏操作的可选 Home 手势 |
| Privacy statement | 隐私声明 | Settings 中可离线阅读的内容，用于说明 Avenor 当前的数据处理、存储、删除、权限和外部链接边界 |
| Prominent disclosure | 显著披露 | 以启用为目的跳转无障碍设置前单独展示的应用内说明与明确选择；Privacy 正文不能替代它 |
| Badge | 标记 | 平台提供的分身或资料身份视觉标记 |
| Application information | 应用信息 | 系统负责的应用信息与管理界面 |
| Private Space | 私密空间 | 当前产品契约之外的 Android 隐藏资料能力；Avenor 不申请 `ACCESS_HIDDEN_PROFILES` 访问该能力 |

产品文档统一使用上述术语。只有实现确实需要额外区分且形成文档时，技术命名才可以不同。

## 界面文案映射

规格术语与面向用户的界面文案是两个独立维度。界面文案不替代上表的规范术语，文案调整也不重命名规格术语。

| 规格术语 | 简体中文界面文案 | 英文界面文案 |
| --- | --- | --- |
| 纵向收藏模块 | 收藏列表 | Favorite list |
| 横向收藏织带 | 收藏织带 | Favorite ribbon |

`收藏主列表` 是 favorite main list 的规范中文工作术语。`home 收藏区` 是同一区域的口语同义说法，不是独立术语。
