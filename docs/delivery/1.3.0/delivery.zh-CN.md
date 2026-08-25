# Avenor Launcher 1.3.0 交付

> 英文语义源：[delivery.md](delivery.md)。
>
> 本记录跟踪 `1.3.0` 所选择的行为。状态和证据不授权版本号修改、commit、push、tag、发布或 Release。

## 版本意图

`1.3.0` 以当前统一收藏模型替代受保护的 `1.2.0` 固定主收藏／伴收藏构图：最多两个地位相同的纵向列表、最多五条收藏栏、一个 Home 编辑会话、目标化 Drawer 新增，以及收藏容器之间一致的移动和恢复行为。

已关闭的 `1.2.0` 记录保持不变。其迭代 14 实现边界之后的产品契约变化，只能通过下列迭代契约进入本版本。

## 交付等级

采用[发布治理](../../release.zh-CN.md)定义的 `作者日用基线`。

预期 `versionName` 为 `1.3.0`。其 `versionCode`、源提交、签名类别、制品身份、完成决定和任何 tag 决定，均留待各自后续门禁。本版本可能在验收后成为候选 tag，但这只是作者届时的独立决定，并非承诺结果。

## 产品引用

- [产品总览](../../../overview.zh-CN.md)
- [产品基础](../../requirements/product-foundation.zh-CN.md)
- [导航](../../product/navigation.zh-CN.md)
- [Home](../../product/surfaces/home.zh-CN.md)
- [Drawer](../../product/surfaces/drawer.zh-CN.md)
- [设计基础](../../product/design-foundations.zh-CN.md)
- [隐私与数据处理](../../product/features/privacy.zh-CN.md)
- [验证指南](../../validation.zh-CN.md)
- [发布治理](../../release.zh-CN.md)

## 纳入范围与用户旅程

所选旅程将可读的既有收藏迁移到统一目标模型；呈现一个全宽或两个等宽纵向列表及各自列表级尺寸；允许作者进入统一 Home 编辑会话管理列表和收藏；通过绑定目标的 Drawer 多选按序新增应用；呈现并管理最多五条收藏栏；并支持契约规定的容器内与跨容器移动、目标反馈、两轴边缘自动滚动、失败恢复、清单协调和状态恢复。

版本收尾覆盖从已接受 `1.2.0` 基线的原位升级、进程重建、应用清单变化、持久化失败以及完整纳入旅程。

## 排除项

- Drawer 搜索；收藏栏标题、命名或重命名；用户配置收藏栏上限；收藏栏尺寸选择；新主题设置；文件夹；自动排序；导出；修复；重置；云备份；恢复或同步。
- Android 私密空间或 `ACCESS_HIDDEN_PROFILES`。
- 双击锁屏新增的构建、权限、隐私或分发范围。
- 当前最新一次删除快照以及既定取消或失败恢复行为之外的通用撤销历史。
- 正式发布制品、GitHub Release、商店上架、公开分发或保证创建 tag。

## 技术路径与风险

实现细节由开发角色负责。已接受的技术评估未发现已知可行性阻塞，并建议采用下列顺序。实现必须保留稳定可启动身份、版本化原子持久化、不可读数据失败关闭、备份禁用以及当前导航和清单边界。

对持久化架构、状态所有权、权限、隐私、后台行为、兼容性或产品范围的重大改变，需要作者决定，并在适用时建立 ADR 或进行专业审查。主要风险包括破坏性迁移、竞争状态源、过期临时目标、多滚动轴手势仲裁、失败回滚覆盖更新后的清单事实，以及破坏已接受的 `1.2.0` Home–Drawer 过渡。

## 纳入迭代

| 迭代 | 状态 | 更新日期 | 依据 |
| --- | --- | --- | --- |
| [迭代 15：统一收藏聚合与兼容迁移](iteration-15-unified-favorite-aggregation-and-migration.zh-CN.md) | `Completed` | 2026-08-23 | 实现已完成；作者报告编译、升级、收藏数据及剩余验收均通过，未发现重大问题。 |
| [迭代 16：纵向列表普通模式构图](iteration-16-vertical-list-normal-mode-composition.zh-CN.md) | `Completed` | 2026-08-24 | 普通 Home 构图及 P1 聚合持久化修复已实现；作者报告当前可验证行为基本验收通过。 |
| [迭代 17：Home 编辑会话与纵向列表管理](iteration-17-home-edit-session-and-vertical-list-management.zh-CN.md) | `Completed` | 2026-08-25 | 实现已完成；作者报告编译、安装及当前可验证行为基本验收通过。 |
| [迭代 18：Drawer 目标化收藏多选与纵向新增](iteration-18-drawer-targeted-multiselection-and-list-creation.zh-CN.md) | `Completed` | 2026-08-25 | 实现已完成；作者报告代码层面验收和基本设备验收通过，当前无功能问题。 |
| [迭代 19：收藏栏呈现、创建和管理](iteration-19-favorite-bar-presentation-creation-and-management.zh-CN.md) | `Planned` | 2026-08-23 | 收藏栏复用已接受的编辑会话和目标化 Drawer 新增流程。 |
| [迭代 20：跨容器应用拖动与两轴自动滚动](iteration-20-cross-container-drag-and-two-axis-auto-scroll.zh-CN.md) | `Planned` | 2026-08-23 | 跨容器移动依赖所有目标类型及其本地管理行为。 |
| [迭代 21：升级、回归与版本闭环](iteration-21-upgrade-regression-and-version-closure.zh-CN.md) | `Planned` | 2026-08-23 | 最终收尾依赖迭代 15–20 的接受和版本所需证据。 |

## 迭代证据与结果

### 迭代 15

[契约](iteration-15-unified-favorite-aggregation-and-migration.zh-CN.md)。统一聚合、schema 1/schema 2 迁移、容器与身份不变量、AtomicFile 持久化、聚合清单协调、兼容投影和聚焦测试覆盖均已实现。`git diff --check` 及变更实现代码行宽扫描为 `Passed`。Agent 执行的 Gradle 与 instrumented tests 为 `Not run`；作者报告 Gradle 编译、从 `1.2.0` 升级、收藏数据及剩余验收均通过，未发现重大问题。继续保留的 `1.2.0` 45:55 构图和可见条目大小不对称属于本迭代范围外的呈现行为，仍归迭代 16 处理。未发生 push、tag、发布或 Release。状态为 `Completed`。

### 迭代 16

[契约](iteration-16-vertical-list-normal-mode-composition.zh-CN.md)。普通 Home 现在根据持久化聚合呈现零、一个或两个纵向列表，使用各自列表级尺寸、等地位布局、独立列表状态以及统一 Loading/Error/空状态处理。P1 修复保证拖动持久化保留纵向容器 ID 和列表尺寸、保留收藏栏、将异步完成结果绑定到当前拖动代次，并应用契约要求的列表内部条目间距。作者报告当前可验证行为基本验收通过。Agent 执行的 Gradle 与 instrumented tests 为 `Not run`；`git diff --check` 和变更实现代码行宽扫描为 `Passed`。根据作者指示，保留编辑会话拖动兼容行为，但不扩展列表控件、新列表创建、收藏栏管理或新的拖动语义。状态为 `Completed`；不声明 commit、push、tag、发布或 Release。

### 迭代 17

[契约](iteration-17-home-edit-session-and-vertical-list-management.zh-CN.md)。Home 编辑会话、列表尺寸与删除控件、同列表应用交换、完整列表拖动排序、最新删除 Undo、拖动所有权、边缘自动滚动、冻结预览、源位置占位、目标反馈、持久化、本地化反馈及基础 UI 测试覆盖均已实现。作者报告编译、安装及当前可验证行为基本验收通过。Agent 执行的 Gradle 与 instrumented tests 为 `Not run`；Kotlin 分隔符检查、受影响 XML 解析、生产日志与残留标记搜索、`git diff --check` 和 `git diff --cached --check` 均为 `Passed`。不声明 push、tag、发布或 Release。根据作者 2026-08-25 的决定，状态为 `Completed`。

### 迭代 18

[契约](iteration-18-drawer-targeted-multiselection-and-list-creation.zh-CN.md)。当前已实现持久化列表和临时纵向列表新增控件、精确目标捕获、Drawer 多选模式、按序选择与圆形数字指示器、已收藏身份禁用、保存期间适用交互冻结、按当前清单重验选择、零有效身份时的临时列表保护、Home 目标位置恢复，以及统一聚合的原子追加或中等尺寸临时列表创建。Home 编辑面同时包含已验收的列表级拖动交换行为。作者报告代码层面验收和基本设备验收通过，当前无功能问题；交换瞬间的轻微停顿被记录为性能观察，不作为功能失败。

以下本地检查为 `Passed`：`git diff --check`、暂存区差异空白检查、受影响 XML 解析和实现代码行宽扫描。Agent 执行的 Gradle 与 instrumented tests 为 `Not run`；更广泛的 API、OEM、profile、clone、进程重建、无障碍和性能场景仍为 `Not run` 或 `Unknown`。这些属于证据缺口，不代表作者已接受的迭代范围存在失败。根据作者 2026-08-25 的决定，状态为 `Completed`；不声明 commit、push、tag、发布、制品或 Release。

### 迭代 19

[契约](iteration-19-favorite-bar-presentation-creation-and-management.zh-CN.md)。状态为 `Planned`；未记录实现、命令、证据、制品、结果、commit 或 tag。

### 迭代 20

[契约](iteration-20-cross-container-drag-and-two-axis-auto-scroll.zh-CN.md)。状态为 `Planned`；未记录实现、命令、证据、制品、结果、commit 或 tag。

### 迭代 21

[契约](iteration-21-upgrade-regression-and-version-closure.zh-CN.md)。状态为 `Planned`；未记录实现、命令、证据、制品、结果、commit 或 tag。

## 依赖与顺序

当前交付依赖为 `15 → 16 → 17 → 18 → 19 → 20 → 21`，体现模型、呈现、编辑、目标化新增、容器、移动和收尾依赖。迭代 18 已完成，其目标化新增机制可由迭代 19 复用。该顺序不把迭代永久绑定到贡献者、终端、分支、预计日期或单线执行政策。

## 验证

版本强制环境是一台作者指定的主要物理设备。完成要求：具有已接受标识的可安装制品；从已接受 `1.2.0` 基线成功原位升级，且没有非预期收藏丢失、重复、顺序变化、目标变化或不可读数据覆盖；作者接受完整所选旅程；并且不存在已知纳入路径崩溃、ANR、破坏性收藏状态错误、错误启动或导航死路。全新安装可以提供补充证据，但不能替代强制升级旅程。

聚焦自动化检查及额外 API、OEM、语言、profile、clone、进程重建、字体缩放、导航模式、清单变化、手势中断和持久化失败覆盖属于建议证据，除非作者后续将某项提升为强制。已执行的建议检查若发现纳入路径失败，必须解决或明确处置；未执行检查只能记为 `Not run`、`Unknown` 或 `Unavailable`，不得记为通过。

## 制品与发布要求

最终制品必须保留 `applicationId` `com.avenor.launcher`，使用已接受的 `1.3.0` 标识，可追溯到一个源提交，并记录签名类别及适用升级限制。本交付等级不要求正式发布制品。版本完成、tag 批准、GitHub Release、上传、发布和分发仍是独立决定。

## 已知限制与遗留问题

- 迭代 18 尚无精确构建、自动化测试和制品证据。
- 迭代 18 已有作者报告的当前流程设备验收；这不能替代尚未运行的 Gradle 和 instrumented-test 证据。
- 更广泛设备、API、OEM、profile、clone、无障碍和性能覆盖在执行前保持未知。
- `1.2.0` 关闭时，其精确标识安装和原位升级检查为 `Not run`；`1.3.0` 必须建立自身真实升级证据，而不是修改该历史。

## 完成条件

- 迭代 15–21 均为 `Completed`，分别记录证据并得到作者接受。
- 强制主要设备从已接受 `1.2.0` 基线原位升级及完整所选旅程得到接受。
- 迁移、持久化、清单协调、进程重建、导航、编辑、新增、收藏栏、拖动、自动滚动和失败恢复符合所选契约。
- 产品文档、实现、测试和记录证据在所选范围内不存在未解决的重大不一致。
- 准确记录标识、源提交、签名类别、制品追踪、已知缺口、作者决定及任何独立 tag 处置。

## 完成结果

目前不存在版本完成结果。迭代 15–18 为 `Completed`；迭代 19–21 仍为 `Planned`，因此
`1.3.0` 尚未完成。不声明任何版本制品、tag、发布或 Release。
