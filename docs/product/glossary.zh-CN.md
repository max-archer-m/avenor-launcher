# 产品字典

> 英文语义源：[glossary.md](glossary.md)。

| 规范英文术语 | 中文工作术语 | 含义 |
| --- | --- | --- |
| Home | 主页面 | 包含时间、日期、星期和收藏应用的 Avenor Launcher 主界面；未加限定的 `Home` 永远不表示 Android 平台导航操作 |
| Android system Home action | Android 系统 Home 操作 | 请求显示当前默认 Launcher 的平台操作，无论它来自手势导航、导航按钮、物理按键还是其他系统入口 |
| Drawer | 应用列表 | 平台暴露的全部可启动应用条目的带索引列表 |
| Settings | 设置 | Avenor 配置与产品信息界面 |
| Launchable entry | 可启动应用条目 | Avenor 可以启动的一个平台目标；主应用与分身是不同条目 |
| Favorite | 收藏应用 | 只保存到一个 Home 收藏目标的可启动应用条目；同一稳定条目身份不得跨列表或织带重复 |
| Favorite-list area | 收藏列表区 | 由最多两个地位相同纵向收藏列表组成、按物理位置从左到右排列的 Home 模块；一个列表使用全宽，两个列表平均分配可用宽度 |
| Favorite size | 收藏尺寸 | 统一作用于某个纵向收藏列表全部条目的“大／中／小”列表级呈现；当前产品以“中”新建和迁移所有列表，且不提供尺寸选择交互 |
| Secondary favorites area | 次级收藏区 | 收藏列表区下方、最多包含五条由用户定义收藏条目横向织带的条件性 Home 模块 |
| Favorite container | 收藏容器 | 某个纵向收藏列表或某条次级织带；同容器应用拖动即时交换，跨容器拖动在松手时交换或插入 |
| Vertical-list controls | 纵向列表控件 | 持久化纵向列表的固定编辑模式顶部栏：物理左侧删除、中间隐藏的尺寸预留槽，以及存在两个列表时物理右侧的排序操作 |
| Ribbon controls | 织带控件 | 位于织带滚动应用视口之外的固定编辑模式控件：逻辑 start 删除整条织带，逻辑 end 调整织带顺序 |
| Application shortcut | 应用快捷操作 | 应用通过平台暴露的操作 |
| Launcher action | 启动器操作 | Avenor 提供的操作，例如收藏、编辑或卸载 |
| Application action sheet | 应用操作面板 | 包含应用身份、应用快捷操作和启动器操作的模态 Bottom Sheet |
| Section anchor | 分组锚点 | Drawer 中的分组标题，例如 A 或 `#`；它是索引跳转目标并随列表滚动，不保持吸顶 |
| Alphabet index | 字母索引 | Drawer 右侧用于跳转到锚点的固定索引 |
| Edit mode | 编辑模式 | 从收藏操作或基础信息区符合条件的空白位置长按进入，在 Home 中显示模块边界、收藏移动能力和面向明确目标的新增控件的状态 |
| Favorite multi-selection | 收藏多选 | Drawer 的临时模式，为一个已记录的 Home 收藏目标收集一组有顺序且此前未收藏的应用 |
| Double-tap lock | 双击锁屏 | 通过 Avenor 用途受限的无障碍服务请求一次 Android 系统锁屏操作的可选 Home 手势 |
| Privacy statement | 隐私声明 | Settings 中可离线阅读的内容，用于说明 Avenor 当前的数据处理、存储、删除、权限和外部链接边界 |
| Prominent disclosure | 显著披露 | 以启用为目的跳转无障碍设置前单独展示的应用内说明与明确选择；Privacy 正文不能替代它 |
| Badge | 标记 | 平台提供的分身或资料身份视觉标记 |
| Application information | 应用信息 | 系统负责的应用信息与管理界面 |
| Private Space | 私密空间 | 当前产品契约之外的 Android 隐藏资料能力；Avenor 不申请 `ACCESS_HIDDEN_PROFILES` 访问该能力 |

产品文档统一使用上述术语。只有实现确实需要额外区分且形成文档时，技术命名才可以不同。
