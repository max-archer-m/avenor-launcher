# Product Glossary

> Public semantic source: English. Chinese counterpart: [glossary.zh-CN.md](glossary.zh-CN.md).

| Canonical English term | Chinese working term | Meaning |
| --- | --- | --- |
| Home | 主页面 | The Launcher primary surface containing time, date, weekday, and favorites |
| Drawer | 应用列表 | The full indexed list of platform-exposed launchable entries |
| Settings | 设置 | Avenor configuration and product-information surface |
| Launchable entry | 可启动应用条目 | One platform-exposed target that Avenor can launch; a primary app and clone are separate entries |
| Favorite | 收藏应用 | A launchable entry saved to Home |
| Application shortcut | 应用快捷操作 | An action exposed by the application through the platform |
| Launcher action | 启动器操作 | An action supplied by Avenor, such as favorite, edit, or uninstall |
| Application action sheet | 应用操作面板 | The modal Bottom Sheet containing application identity, application shortcuts, and Launcher actions |
| Section anchor | 分组锚点 | A pinned Drawer section heading such as A or `#` |
| Alphabet index | 字母索引 | The fixed right-side Drawer index used to jump between anchors |
| Edit mode | 编辑模式 | The Home state that exposes module boundaries and allows favorites to be reordered or moved between groups |
| Double-tap lock | 双击锁屏 | The optional Home gesture that requests one Android system lock action through Avenor's narrowly scoped accessibility service |
| Badge | 标记 | Platform-provided visual identity for a clone or profile context |
| Application information | 应用信息 | The system-owned information and management surface for an application |
| Private Space | 私密空间 | Android hidden-profile capability outside the current product contract; Avenor does not request `ACCESS_HIDDEN_PROFILES` to access it |

Use these terms consistently in product documents. Technical names may differ only when an implementation distinction is necessary and documented.
