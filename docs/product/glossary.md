# Product Glossary

> Public semantic source: English. Chinese counterpart: [glossary.zh-CN.md](glossary.zh-CN.md).

| Canonical English term | Chinese working term | Meaning |
| --- | --- | --- |
| Home | 主页面 | The Avenor Launcher primary surface containing time, date, weekday, and favorites; unqualified `Home` never means the Android platform navigation action |
| Android system Home action | Android 系统 Home 操作 | The platform action that requests display of the selected default Launcher, regardless of whether it originates from gesture navigation, a navigation button, a physical key, or another system entry |
| Drawer | 应用列表 | The full indexed list of platform-exposed launchable entries |
| Settings | 设置 | Avenor configuration and product-information surface |
| Launchable entry | 可启动应用条目 | One platform-exposed target that Avenor can launch; a primary app and clone are separate entries |
| Favorite | 收藏应用 | A launchable entry saved to exactly one Home favorite destination; one stable entry identity cannot be duplicated across lists or ribbons |
| Favorite-list area | 收藏列表区 | The Home module containing a physical left-to-right horizontal sequence of at most two equal-status vertical favorite lists; one list uses full width and two divide available width equally |
| Favorite size | 收藏尺寸 | A persisted large, medium, or small list-level presentation applied uniformly to every entry in one vertical favorite list; every list starts at medium and can be changed independently in edit mode |
| Secondary favorites area | 次级收藏区 | The conditional Home module below the favorite-list area, containing up to five untitled horizontal ribbons of fixed-medium user-defined favorite entries |
| Favorite container | 收藏容器 | One vertical favorite list or one secondary ribbon; same-container application drags exchange immediately, while cross-container drags use release-time exchange or insertion |
| Vertical-list controls | 纵向列表控件 | The fixed edit-mode top bar for a persisted vertical list: physical-left remove, center current-size selection, and physical-right reorder when two lists exist |
| Ribbon controls | 织带控件 | Fixed edit-mode rails outside a persisted ribbon's scrolling application viewport: confirm complete-ribbon removal at logical start and, when at least two persisted ribbons exist, reorder at logical end |
| Application shortcut | 应用快捷操作 | An action exposed by the application through the platform |
| Launcher action | 启动器操作 | An action supplied by Avenor, such as favorite, edit, or uninstall |
| Application action sheet | 应用操作面板 | The modal Bottom Sheet containing application identity, application shortcuts, and Launcher actions |
| Section anchor | 分组锚点 | A Drawer section heading such as A or `#`; it is an index destination and scrolls with the list rather than remaining pinned |
| Alphabet index | 字母索引 | The fixed right-side Drawer index used to jump between anchors |
| Edit mode | 编辑模式 | The Home state entered from a favorite action or eligible basic-information blank-space long-press, exposing module boundaries, favorite movement, and destination-targeted add controls |
| Favorite multi-selection | 收藏多选 | The temporary Drawer mode that collects an ordered set of previously unfavorited applications for one captured Home favorite destination |
| Double-tap lock | 双击锁屏 | The optional Home gesture that requests one Android system lock action through Avenor's narrowly scoped accessibility service |
| Privacy statement | 隐私声明 | The offline Settings presentation describing Avenor's current data handling, storage, deletion, permission, and external-link boundaries |
| Prominent disclosure | 显著披露 | The separate in-app explanation and affirmative choice shown immediately before an enable-oriented accessibility-settings handoff; it is not replaced by the Privacy statement |
| Badge | 标记 | Platform-provided visual identity for a clone or profile context |
| Application information | 应用信息 | The system-owned information and management surface for an application |
| Private Space | 私密空间 | Android hidden-profile capability outside the current product contract; Avenor does not request `ACCESS_HIDDEN_PROFILES` to access it |

Use these terms consistently in product documents. Technical names may differ only when an implementation distinction is necessary and documented.
