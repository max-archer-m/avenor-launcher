# Product Glossary

> Public semantic source: English. Chinese counterpart: [glossary.zh-CN.md](glossary.zh-CN.md).

| Canonical English term | Chinese working term | Meaning |
| --- | --- | --- |
| Home | 主页面 | The Avenor Launcher primary surface containing time, date, weekday, and favorites; unqualified `Home` never means the Android platform navigation action |
| Android system Home action | Android 系统 Home 操作 | The platform action that requests display of the selected default Launcher, regardless of whether it originates from gesture navigation, a navigation button, a physical key, or another system entry |
| Drawer | 应用列表 | The full indexed list of platform-exposed launchable entries |
| Settings | 设置 | Avenor configuration and product-information surface |
| Launchable entry | 可启动应用条目 | One platform-exposed target that Avenor can launch; a primary app and clone are separate entries |
| Favorite | 收藏应用 | A launchable entry saved to exactly one Home favorite module; one stable identity cannot be duplicated across modules |
| Favorite main list | 收藏主列表 | The one full-width, vertically scrolling ordered sequence of peer vertical favorite modules and horizontal favorite ribbons below Home basic information |
| Favorite module | 收藏模块 | One persisted vertical favorite module or horizontal favorite ribbon containing at least one favorite |
| Vertical favorite module | 纵向收藏模块 | A full-width module whose naturally expanded entries share one module-level size, name placement, and items-per-row style |
| Horizontal favorite ribbon | 横向收藏织带 | A full-width, single-row module of fixed-style, content-measured favorite entries that scrolls horizontally only on overflow |
| Edit dock | 编辑坞 | The fixed bottom Home edit region whose collapsed affordance opens the non-modal module style panel |
| Style panel | 样式面板 | The expandable Home edit panel used to select modules, change applicable module style, add modules, or delete the selected module |
| Application shortcut | 应用快捷操作 | An action exposed by the application through the platform |
| Launcher action | 启动器操作 | An action supplied by Avenor, such as favorite, edit, or uninstall |
| Application action sheet | 应用操作面板 | The modal Bottom Sheet containing application identity, application shortcuts, and Launcher actions |
| Section anchor | 分组锚点 | A Drawer section heading such as A or `#`; inline presentation places it above its section and left-side presentation places it beside its section, with both presentations scrolling with the application list rather than remaining pinned |
| Alphabet index | 字母索引 | The fixed right-side Drawer index used to jump between anchors |
| Edit mode | 编辑模式 | The Home state entered from a favorite action or eligible blank-space long press, exposing the edit dock, module movement, application editing, and destination-targeted add controls |
| Favorite multi-selection | 收藏多选 | The temporary Drawer mode that collects an ordered set of previously unfavorited applications for one captured Home favorite destination |
| Double-tap lock | 双击锁屏 | The optional Home gesture that requests one Android system lock action through Avenor's narrowly scoped accessibility service |
| Privacy statement | 隐私声明 | The offline Settings presentation describing Avenor's current data handling, storage, deletion, permission, and external-link boundaries |
| Prominent disclosure | 显著披露 | The separate in-app explanation and affirmative choice shown immediately before an enable-oriented accessibility-settings handoff; it is not replaced by the Privacy statement |
| Badge | 标记 | Platform-provided visual identity for a clone or profile context |
| Application information | 应用信息 | The system-owned information and management surface for an application |
| Private Space | 私密空间 | Android hidden-profile capability outside the current product contract; Avenor does not request `ACCESS_HIDDEN_PROFILES` to access it |

Use these terms consistently in product documents. Technical names may differ only when an implementation distinction is necessary and documented.
