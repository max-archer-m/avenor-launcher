# 双击锁屏交互规格

> 英文语义源：[double-tap-lock.md](double-tap-lock.md)。当前 Privacy 与显著披露正文见 [privacy.zh-CN.md](privacy.zh-CN.md)。

## 目的与范围

双击锁屏是作者明确需要的日用 Home 能力。在 Home 基础信息区符合条件的空白位置连续点击两次后，Avenor 请求 Android 系统锁屏操作。

该能力属于可选能力；只有用户在系统设置中明确启用 Avenor 无障碍服务后才生效。它默认关闭，且不得阻断 Home、Drawer、应用启动、Settings 或其他核心路径。Avenor 不在首次启动时请求该能力。

## 触发区域与手势

- 基础信息模块在当前内容和交互目标之外保留具有实际可操作性的全宽空白，供本手势使用；两次点击都必须在该有效空白内开始并结束。
- 未来在该模块中展示天气等信息时，其内容和交互目标会从有效区域中排除，但不得占满所有具有实际可操作性的空白。
- 可见时间行、完整日期星期行、两者的可聚焦触控目标、系统 inset 以及其他所有交互元素均不属于触发区域。
- 点击时间仍然立即打开时钟；点击日期星期仍然立即打开日历。两种操作都不得为了等待可能出现的第二次点击而延迟。
- 双击识别采用平台的双击时间和移动容差，不定义产品专用硬编码阈值。
- 移动超过平台点击容差、Home→Drawer 上滑、长按、手势取消，或任一次点击进入排除目标时，取消双击识别。
- 锁屏请求成功时不显示 Toast、不震动、不增加动画或二次确认；系统屏幕变化就是操作结果。
- 服务已经启用，但锁屏操作当前不可用或执行失败时，Home 保持可用，并显示短 Toast `无法锁定屏幕`；Avenor 不自动重试。
- 服务未启用时，该手势不产生产品行为，也不意外打开系统设置。

## Settings 与授权

Settings 包含一个标题为 `双击锁屏` 的主要设置项。

- 所需 Avenor 无障碍服务已经启用并连接时，副标题显示 `已开启`；其他情况显示 `已关闭`。
- 点击该设置项打开本地说明界面，展示当前状态、用途、隐私边界和 `打开无障碍设置` 操作。
- 每次以启用服务为目的跳转系统无障碍设置前，Avenor 单独展示 [privacy.zh-CN.md](privacy.zh-CN.md#双击锁屏显著披露) 定义的显著披露，并提供 `取消` 与 `同意并继续`。同意并继续只确认本次跳转并打开系统界面；Avenor 不保留披露确认历史，该操作也不表示 Android 已经启用服务。
- 从系统设置返回后立即刷新真实服务状态。Android 状态是唯一权威来源；Avenor 不提供可能与系统状态不一致的独立开关。
- 服务已经启用时，说明界面继续提供同一系统设置入口，供用户检查或关闭服务。
- 系统无障碍设置无法打开时，显示短 Toast `无法打开无障碍设置`，并保留当前 Settings 位置。

## 无障碍服务边界

当前产品仅授权 Android 无障碍服务执行这一由用户明确触发的锁屏操作。

- Avenor 不是无障碍工具，也不得把自身描述成无障碍工具。
- 服务不请求读取窗口内容，不检查其他应用界面内容，不为分析收集无障碍事件，不推断行为，也不根据后台条件自动执行操作。
- 除本能力所需的系统锁屏操作外，不执行其他全局操作。
- 不使用设备管理员作为回退方案。
- 用户关闭或撤销服务后，只移除双击锁屏能力，不降低任何独立 Launcher 行为。
- 进程终止、服务断开、系统操作不可用或授权变化时必须以关闭能力的方式失败；只有当前服务连接能够执行用户明确请求时，才发出锁屏操作。
- 未来扩展服务用途必须由作者重新决定，并重新审查产品、隐私、安全、平台政策和验证范围。

## 隐私与分发

本地 Privacy 展示和独立显著披露采用 [privacy.zh-CN.md](privacy.zh-CN.md) 中当前产品正文。Privacy 正文、服务说明、应用商店说明或外部页面均不得替代显著披露。

当前仅通过 GitHub 分发并不免除上述披露义务。未来进入应用商店前，必须重新评估适用的无障碍服务声明、显著披露、同意、商店说明和审核要求。

## 验收意图

- Given 服务已经启用，when 两次点击都发生在符合条件的空白区域且没有其他手势取得所有权，then Avenor 请求一次系统锁屏操作。
- Given 任一次点击发生在时间、日期星期、收藏、编辑表面或其他交互目标上，then 不触发双击锁屏。
- Given 服务未启用或已经撤销，when 用户使用所有独立 Launcher 路径，then 这些路径保持完整可用，且 Avenor 不执行锁屏操作。
- Given 服务已经启用但操作失败，when 双击手势被识别，then Avenor 保持在 Home、展示一次本地化失败提示，且不重试。
- Given 用户从无障碍设置返回，when Settings 恢复，then 显示状态与 Android 当前服务状态一致。

## 平台参考

- [Android AccessibilityService API](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [Google Play AccessibilityService 政策](https://support.google.com/googleplay/android-developer/answer/10964491)
- [Google Play 显著披露与同意指南](https://support.google.com/googleplay/android-developer/answer/11150561)
- [Android DevicePolicyManager API](https://developer.android.com/reference/android/app/admin/DevicePolicyManager)
