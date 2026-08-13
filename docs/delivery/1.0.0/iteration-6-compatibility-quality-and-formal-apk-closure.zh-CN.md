# 迭代 6：作者日常使用基线收尾

> 英文语义源：[iteration-6-compatibility-quality-and-formal-apk-closure.md](iteration-6-compatibility-quality-and-formal-apk-closure.md)。
>
> 本迭代适用于 [Avenor Launcher 1.0.0 交付契约](delivery.zh-CN.md)，关闭 `Author daily-use baseline`；它不创建 `Formal release artifact`，也不授权签名、产物移动、tag、发布或公开分发。

## 状态

- Value: `Completed`
- Updated: 2026-08-13
- Basis: 项目作者已验收 `053b6b7` 实现基线及其完整主要设备日常使用路径。实现已同步到 `origin/main`；包含本状态转换的 commit 同步对应收尾文档。

## 目标

建立一个可安装、可追踪的 `1.0.0` APK；它在作者指定的主要物理设备上完成所选离线路径，并被项目作者接受用于持续日常使用。

## 产品和版本引用

- [1.0.0 交付契约](delivery.zh-CN.md)
- [1.0.0 产品范围](product-scope.zh-CN.md)
- [1.0.0 技术评估](technical-assessment.zh-CN.md)
- [版本、产物与发布治理](../../release.zh-CN.md)
- [迭代记录格式](../../iterations/iteration-record-format.zh-CN.md)

## 可观察结果

一个 `1.0.0` APK 在作者指定的主要物理设备上安装，完成所选离线 Home、Drawer、应用启动、操作面板和收藏路径，并可追踪到其应用标识与源码 commit。已知缺口和签名或重新安装限制得到记录。

## 纳入工作

- 解决阻塞作者指定主要物理设备上所选日常使用路径的缺陷。
- 安装 `1.0.0` APK，并在该设备上执行完整所选离线路径。
- 记录设备、OS/API、应用标识、源码 commit、可用 APK/构建身份、步骤、结果和已知限制。
- 将每项未执行的自动化、API 31、Pixel、性能、Manifest、依赖、许可证、安全、隐私和发布制品检查记录为明确缺口，而不是通过。
- 通过适用决定权记录和处置 OEM 限制与契约不匹配。
- 记录开发签名或作者控制签名对更新和重新安装行为的影响。
- 只有全部完成证据存在后，才按所选交付级别准备事实性的已完成版本记录。

## 排除工作

- 新产品能力、延期的 `1.0.0` 行为、Settings、排序、快捷操作、卸载、更广设备适配、网络能力、后续能力层或商业功能。
- 静默放宽纳入的验收要求。
- 未经单独明确授权创建 tag、声明里程碑、创建 GitHub Release、远程上传、执行应用商店操作或公开分发。
- 在产品仓库中存储 APK 或签名秘密。
- 正式发布签名及备份、发布级摘要证据、完整兼容性矩阵、性能阈值和专业发布结论，除非另行授权为附加工作。

## 技术影响面

- 所选路径的主要设备缺陷修复和聚焦回归证据。
- APK 标识、源码可追踪性、安装证据和签名/重新安装限制。
- 稳定的已完成版本记录准备。

本迭代不引入推测性架构。暴露重大架构选择的缺陷修复遵循 ADR 流程；产品行为变更返回产品经理和项目作者处理。

## 依赖和序列

- 迭代 1 至 5 必须为 `Completed`，或由项目作者明确改变依赖顺序，本迭代才能从 `Planned` 变为 `In Progress`。除非版本契约将某一场景规定为正式版本门禁，否则不要求先完成这些迭代的全部建议场景。
- 将可用构建和安装身份、源码 commit 与主要设备验证步骤记录为日常使用收尾证据。缺失的建议命令继续作为明确缺口。
- 可选签名身份创建和外部产物移动分别就绪时，项目作者分别作出授权。
- 只有满足版本契约中的每个门禁后，才完成版本。

## 迁移和兼容性影响

- 在作者指定的主要物理设备上验证所选路径。API 31 和 Pixel 验证保留为建议后续工作。
- 验证已安装基线 APK 中的准确 `versionName` `1.0.0` 和 `versionCode` `1`。
- 验证进程/设备恢复，以及迭代 5 产出的最终持久化 schema。
- 不纳入降级、公开分发、应用商店迁移或 1.0 前生产数据迁移。

## 安全、隐私、权限和许可证影响

- 记录影响所选日常使用路径的任何已知权限、Manifest、依赖、备份、安全、隐私或许可证关注点。缺失的发布级完整审查继续作为明确后续缺口。
- 项目记录不得存储 keystore、密钥、密码、签名属性文件或其他秘密。
- 记录签名身份类别及其更新或重新安装限制；本级别不要求 release 证书指纹或 release keystore 备份。

## 风险和未解决决定

- OEM 限制可能需要作者接受或产品契约决定。
- API 31、Pixel、完整自动化检查、性能测量、merged manifest/依赖审查和合格许可证结论继续作为建议后续证据。
- 外部 APK 保留和正式签名继续作为作者保留的可选操作。
- 已验收核心行为失败不属于可静默豁免的质量债。

## 验证计划

以下主要设备验证是 `1.0.0` 作者日常使用基线的必需条件。更高级别证据在可用时记录，但不阻塞完成。

- 记录用于安装的构建或 APK 身份及源码 commit。
- 在作者指定的主要物理设备上完成完整离线用户路径。
- 验证 Home 资格、直接启动、时间/日期、Drawer、分组/索引、过渡、实时更新、准确条目启动、操作面板、收藏创建/生命周期、进程重建和设备重启。
- 记录崩溃、ANR、意外激活、重复、数据丢失、覆盖和不可用行为。
- 验证应用标识，并记录可从已接受安装确认的签名、更新和重新安装限制。
- 比较文档、实现、测试和验证证据；明确解决每个实质契约不匹配。

## 验收证据

项目作者报告并验收以下日常使用证据：

- 已验收源码 commit：`053b6b7da58a27a9c237d98c2e49f7a94e5b1d3e`（`perf(drawer): reduce application list rendering cost`）。
- 已验收应用身份：`applicationId` `com.avenor.launcher`、`versionName` `1.0.0` 和 `versionCode` `1`。
- 主要环境：Android 16/API 36 的 Samsung Galaxy S23 Ultra。
- 结果：完整所选离线日常使用路径通过，作者报告不存在已知核心路径阻塞。
- 签名类别：作者本地私有签名身份。后续原位更新必须继续使用同一签名身份；如果该身份不可用，则不能假定更新连续性。
- 迭代 1 至 5 均为 `Completed`，并保留各自链接的证据。

准确的 Gradle 构建命令、安装命令、APK 文件名、摘要、保留位置和命令输出未报告。API 31 与 Pixel 兼容性、完整自动化矩阵、性能分布、merged manifest 与解析依赖审查、合格许可证结论、正式安全和隐私审查、release 签名保管与备份，以及正式发布制品证据继续属于未知或未执行的建议后续工作；其中任何一项均未表述为通过。

## 相关决定、commit 和 tag

- 适用架构决定：[ADR-0001](../../decisions/0001-establish-replaceable-launcher-icon-rendering.md)、[ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)和 [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)。各链接记录中的 ADR 状态与准确范围保持权威。
- 已验收实现基线：`053b6b7da58a27a9c237d98c2e49f7a94e5b1d3e`（`perf(drawer): reduce application list rendering cost`）。
- 签名配置 commit：`97a38e6 build(signing): configure local release credentials`；私有签名材料保持在 Git 之外。
- 收尾文档：由包含本状态转换的 commit 同步；其标识以 Git 历史为准。
- Tag：`1.0.0` 不要求 tag；只有在项目作者单独明确授权后才创建。
- GitHub Release 和公开分发：本迭代不要求也不授权。

## 最终结果

项目作者已验收 `053b6b7` 实现基线、具有既定身份的 `1.0.0` APK，以及报告的 Samsung Galaxy S23 Ultra 主要设备路径，用于持续日常使用，且不存在已知核心路径阻塞。实现已同步到 `origin/main`，包含本状态转换的 commit 同步对应收尾文档。迭代 6 以 `Author daily-use baseline` 级别达到 `Completed`。除非另行授权，正式发布制品证据不属于本迭代范围。

## 剩余问题和交接

已完成版本记录与本迭代及其他 `1.0.0` 交付输入继续保留在稳定的 `docs/delivery/1.0.0/` 路径下。下一个项目级迭代编号保持为 `7`，所有剩余问题继续记录以供后续获授权交付处理。Tag、里程碑、GitHub Release 或公开分发仍是独立决定。
