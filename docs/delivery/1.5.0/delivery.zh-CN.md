# Avenor Launcher 1.5.0 交付

> 英文语义源：[delivery.md](delivery.md)。
>
> 本记录规划从产品契约基线 `48d5bd470c84d222b6e89e128f438da1f25e595b` 中选择的行为。状态和证据不授权实施、版本变更、提交、推送、tag、产物移动、发布或发行。

## 版本意图

`1.5.0` 在另行规划的 `1.4.0` Home 模块交付之后改进普通 Drawer。所选结果使作者可以按展示名称在本地查找应用、通过修订后的普通导航进入设置，并选择持久且可读的 Drawer 展示，同时不改变应用排序或 Home 模块模型。

## 交付级别

按照[发布治理](../../release.zh-CN.md)定义，本版本为`作者日常使用基线`。

计划应用标识保持 `com.avenor.launcher`，`versionName` 为 `1.5.0`。候选 `versionCode` 是暂定值，只能在生成可追溯 APK 时从下一个未使用值中选择；规划不预留值，也不覆盖期间发生的产物分配。

## 产品引用

- [产品概览](../../../overview.zh-CN.md)
- [产品基础](../../requirements/product-foundation.zh-CN.md)
- [Drawer 行为](../../product/surfaces/drawer.zh-CN.md)和[Drawer 展示](../../product/presentation/drawer.zh-CN.md)
- [应用操作面板](../../product/surfaces/app-action-sheet.zh-CN.md)
- [导航](../../product/navigation.zh-CN.md)
- [隐私与数据处理](../../product/features/privacy.zh-CN.md)
- [验证指南](../../validation.zh-CN.md)

## 纳入范围和用户旅程

在普通 Drawer 中，作者可以按展示的应用名称搜索可靠本地清单，从最终设置行打开设置，并配置应用尺寸、排列、分区锚点以及透明或毛玻璃背景。已接受的 Home 模块与收藏状态保持不变，Drawer 应用操作面板保持其来源特定边界。

## 排除范围

- Home 模块创建、样式、排序、移动或采用，它们属于另行规划的 `1.4.0` 交付。
- Drawer 快捷操作排名、包名或拼音搜索、模糊匹配或基于相关性的第二套应用顺序。
- Drawer 之外的主题定制、壁纸采样或用户自定义视觉值。
- 在独立清单和验收条件满足前展示第三方 License。
- 正式发布产物、公开分发、tag、里程碑或 GitHub Release。

## 技术路径和风险

实现细节由开发负责。交付必须保持可靠清单身份、既有顺序、仅本地处理、版本化原子持久化、备份排除、当前权限边界和已接受 Home 状态。任何有后果的持久化、渲染、清单或导航决定都需要作者审阅，并在适用时建立 ADR。

主要风险是依赖 locale 的搜索行为、过时结果身份、返回键/IME 歧义、展示设置保存竞态、几何变化后丢失可见位置、平台模糊降级，以及 Home–Drawer–设置导航回归。

## 纳入的迭代

| 迭代 | 状态 | 更新日期 | 依据 |
| --- | --- | --- | --- |
| [迭代 26：Drawer 搜索与普通导航](iteration-26-drawer-search-and-ordinary-navigation.zh-CN.md) | `Planned` | 2026-09-01 | 本地应用名称搜索与修订后的普通 Drawer 导航构成一个可独立观察的发现增量。 |
| [迭代 27：Drawer 展示设置](iteration-27-drawer-display-settings.zh-CN.md) | `Planned` | 2026-09-01 | 持久布局、锚点和背景控制依赖修订后的普通 Drawer 表面。 |
| [迭代 28：升级、回归与版本收尾](iteration-28-upgrade-regression-and-version-closure.zh-CN.md) | `Planned` | 2026-09-01 | 所选 Drawer 闭环完成后，版本完成需要集成的 Drawer、升级、兼容性、产物和作者验收证据。 |

## 迭代证据和结果

### 迭代 26

[契约](iteration-26-drawer-search-and-ordinary-navigation.zh-CN.md)。当前没有实施或验证证据。状态保持 `Planned`；缺少证据不代表通过。

### 迭代 27

[契约](iteration-27-drawer-display-settings.zh-CN.md)。当前没有实施或验证证据。状态保持 `Planned`；缺少证据不代表通过。

### 迭代 28

[契约](iteration-28-upgrade-regression-and-version-closure.zh-CN.md)。当前没有实施或验证证据。状态保持 `Planned`；缺少证据不代表通过。

## 依赖和顺序

迭代 26 建立普通 Drawer 顶部应用栏、搜索、最终设置行和来源特定应用操作面板行为，迭代 27 随后添加展示设置。迭代 28 依赖迭代 26–27 的已接受结果和已接受的前一版本基线。这些依赖不把工作绑定到分支、终端、贡献者、预测日期或永久任务线。

## 验证

强制版本环境是一台作者指定的主物理设备。完成要求包括：一个可追溯、可安装的候选包；从已接受的前一版本基线直接升级；保留可读 Home 和无关配置；作者接受完整的所选 Drawer 旅程；没有已知纳入路径崩溃、ANR、破坏性配置错误、错误身份启动或导航死路。

聚焦自动化检查，以及额外 API 31、API 36/37、OEM、资料、克隆、locale、字体缩放、导航模式、模糊可用性、进程重建、清单变化和持久化失败场景为建议项，除非被明确提升。已执行检查在纳入行为上的失败必须解决或处置；未执行检查保持 `Not run`、`Unknown` 或 `Unavailable`。

## 产物和发布要求

已接受 APK 必须保持 `com.avenor.launcher`，使用已接受的 `1.5.0` 标识，可追溯到一个源码 commit 和签名类别，并支持要求的升级旅程。为保持原地更新连续性，仍要求作者本地私有签名身份。APK 保留、tag、发布和分发仍需单独授权。

## 已知限制和遗留问题

- 更广泛设备、API、OEM、资料、克隆、无障碍、模糊和性能覆盖在执行前仍为未知。
- 平台跨窗口模糊不可用时，毛玻璃可使用契约规定的更不透明降级效果。
- 最低可接受性能、功耗、内存和启动阈值仍未决定，除非明确提升，否则不是版本门禁。
- 搜索保持在现有 Drawer 顺序中对展示名称做连续匹配；更广泛发现不属于本版本。

## 完成标准

- 迭代 26–28 均为 `Completed`，并分别记录证据和作者验收。
- 所选范围的产品契约、实现、测试和交付证据不存在未解决的重大不一致。
- 强制物理设备升级和完整的所选 Drawer 旅程已接受，且不丢失已接受 Home 状态。
- 最终标识、已分配 `versionCode`、源码 commit、签名类别、APK 身份、已知缺口和 tag 处置均准确记录。

## 完成结果

当前没有完成结果。`1.5.0` 和迭代 26–28 保持 `Planned`；本文件不授权生产实现。
