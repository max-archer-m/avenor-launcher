# 迭代 9：基础 Settings 闭环

> 适用于 [Avenor Launcher 1.1.0](delivery.zh-CN.md)。本记录规划一个产品增量，不授权实现或 Git/远程操作。

## 状态

- 值：`Planned`
- 更新日期：2026-08-13
- 依据：作者已为 `1.1.0` 选择基础 Settings 能力；尚未授权生产实现。

## 目标

交付用于默认 Launcher 管理和必要产品信息的连贯离线 Settings 闭环，不扩展到诊断、隐私声明或更广自定义。

## 产品与版本引用

- [1.1.0 交付](delivery.zh-CN.md)
- [Settings](../../product/surfaces/settings.zh-CN.md)
- [Drawer](../../product/surfaces/drawer.zh-CN.md#字母索引)
- [导航](../../product/navigation.zh-CN.md)
- [设计基础](../../product/design-foundations.zh-CN.md)
- [Avenor License](../../../LICENSE)

## 可观察结果

作者从固定 Drawer 齿轮打开 Settings，查看当前默认主屏幕状态，打开系统默认主屏幕设置并在返回后观察刷新状态，阅读本地 Avenor License，通过系统浏览器打开项目仓库，查看准确应用版本，并返回保留位置的 Drawer。

## 纳入工作

- 固定 Settings 齿轮目标和 Settings 导航/恢复。
- 不透明 Material 3 深色 Settings 界面和契约规定的条目展示。
- 默认主屏幕状态、系统设置跳转及返回刷新。
- 本地离线 Avenor License 展示。
- 项目仓库隐式浏览器操作和无处理器反馈。
- 使用实际构建身份展示不可交互的 `v<version-name>(<version-code>)`。
- 纳入 UI 的英文、简体中文和英文回退资源。

## 排除工作

- Privacy 条目和内容；它们使用现已确认的产品正文，并随迭代 10 授权闭环交付，而不属于本次 Settings 基础增量。
- Third-party License 条目、清单和展示。
- 手动语言选择、日志、诊断、更新检查、备份、重置、云同步或信息复制操作。
- 双击锁屏及任何相关设置项。

## 技术变更面

Drawer 索引目标、导航状态、Settings UI/资源、默认主屏幕平台 intent 与状态查询、本地许可证资源展示、仓库 URI 调用和构建版本展示。准确导航与资源加载结构仍是实现选择。

## 依赖和顺序

依赖已验收的 Drawer 索引、导航、系统栏行为和构建身份。其完成后的 Settings 导航与状态刷新基础是迭代 10 的准入依赖。

## 迁移和兼容性影响

不计划用户数据迁移。现有 Home、Drawer 位置恢复、收藏数据和应用操作必须保持兼容。获授权的版本实现与收尾使用 `versionName` `1.1.0`，已完成 APK 在制品获接受边界使用下一个未分配的 `versionCode`。

## 安全、隐私、权限和许可证影响

核心 Settings 保持离线。仓库条目把 URL 处理交给系统浏览器，不预检网络状态。Avenor License 展示必须忠实使用仓库 `LICENSE`；它不形成第三方许可证结论。不展示签名秘密或私有数据。

## 风险和未决决定

- 默认主屏幕目标和状态报告可能因 Android/OEM 不同，需要防御性处理。
- 仓库 URL 必须匹配配置的公开项目位置。
- 较长本地许可证内容必须保持可读、可关闭，且不丢失 Settings 位置。
- 不得用这一缩减版本范围暗示 Privacy 和第三方许可证工作已完成。

## 验证计划

建议场景包括齿轮入口；Back 和位置恢复；默认/非默认状态；系统目标返回和取消；目标不可用；英文、简体中文和回退 locale；离线许可证阅读；长内容滚动；仓库浏览器成功/失败；准确 `1.1.0(2)` 展示；进程重建；Home/Drawer/操作面板回归。除非提升，否则不是自动迭代门禁。

## 验收证据

尚无实现证据。执行时记录执行者、源码/构建身份、设备/API/OEM、初始默认主屏幕状态、系统跳转结果、显示版本、本地内容行为、浏览器结果、回归和跳过场景。

## 相关决定、提交和 tag

- 本迭代尚无新 ADR、实现 commit 或 tag。

## 最终结果

迭代处于 `Planned` 时尚无最终结果。

## 剩余问题和交接

开发应在固定实现行为前验证 OEM 系统目标。作者验收后，版本仍需满足 [delivery.zh-CN.md](delivery.zh-CN.md) 的完成证据；仅完成迭代不会自动完成 `1.1.0`。
