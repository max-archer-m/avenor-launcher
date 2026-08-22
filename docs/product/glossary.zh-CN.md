# 产品字典

> 英文语义源：[glossary.md](glossary.md)。

| 规范英文术语 | 中文工作术语 | 含义 |
| --- | --- | --- |
| Home | 主页面 | 包含时间、日期、星期和收藏应用的 Launcher 主界面 |
| Drawer | 应用列表 | 平台暴露的全部可启动应用条目的带索引列表 |
| Settings | 设置 | Avenor 配置与产品信息界面 |
| Launchable entry | 可启动应用条目 | Avenor 可以启动的一个平台目标；主应用与分身是不同条目 |
| Favorite | 收藏应用 | 只保存到一个 Home 收藏分组的可启动应用条目；同一稳定条目身份不得跨分组重复 |
| Primary favorites area | 主要收藏区 | 由主收藏和伴收藏共同组成的 Home 模块 |
| Secondary favorites area | 次级收藏区 | 位于主要收藏区下方、最多包含五条由用户定义收藏条目横向织带的条件性 Home 模块；它不同于伴收藏 |
| Application shortcut | 应用快捷操作 | 应用通过平台暴露的操作 |
| Launcher action | 启动器操作 | Avenor 提供的操作，例如收藏、编辑或卸载 |
| Application action sheet | 应用操作面板 | 包含应用身份、应用快捷操作和启动器操作的模态 Bottom Sheet |
| Section anchor | 分组锚点 | Drawer 中的分组标题，例如 A 或 `#`；它是索引跳转目标并随列表滚动，不保持吸顶 |
| Alphabet index | 字母索引 | Drawer 右侧用于跳转到锚点的固定索引 |
| Edit mode | 编辑模式 | Home 中显示模块边界，并允许收藏排序或跨组移动的状态 |
| Double-tap lock | 双击锁屏 | 通过 Avenor 用途受限的无障碍服务请求一次 Android 系统锁屏操作的可选 Home 手势 |
| Privacy statement | 隐私声明 | Settings 中可离线阅读的内容，用于说明 Avenor 当前的数据处理、存储、删除、权限和外部链接边界 |
| Prominent disclosure | 显著披露 | 以启用为目的跳转无障碍设置前单独展示的应用内说明与明确选择；Privacy 正文不能替代它 |
| Badge | 标记 | 平台提供的分身或资料身份视觉标记 |
| Application information | 应用信息 | 系统负责的应用信息与管理界面 |
| Private Space | 私密空间 | 当前产品契约之外的 Android 隐藏资料能力；Avenor 不申请 `ACCESS_HIDDEN_PROFILES` 访问该能力 |

产品文档统一使用上述术语。只有实现确实需要额外区分且形成文档时，技术命名才可以不同。
