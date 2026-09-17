# 锁屏交互规格

> 英文语义源：[double-tap-lock.md](double-tap-lock.md)。当前 Privacy 与显著披露正文见 [privacy.zh-CN.md](privacy.zh-CN.md)。本能力最初以"双击锁屏"引入；用户可配置的触发槽位与绑定见 [quick-actions.zh-CN.md](quick-actions.zh-CN.md)。

## 目的与范围

锁屏是可选的 Home 能力。用户在快捷操作中绑定为 `锁屏` 的 Home 槽位，通过 Launcher4Max 用途受限的无障碍服务请求 Android 系统锁屏操作。

该能力在用户同时完成两件事之前保持无效：在 [quick-actions.zh-CN.md](quick-actions.zh-CN.md) 中把某个快捷操作槽位绑定为 `锁屏`，并在系统设置中明确启用 Launcher4Max 无障碍服务。它不得阻断 Home、Drawer、应用启动、Settings 或其他核心路径。Launcher4Max 不在首次启动时请求该能力。

## 触发与结果

- 绑定手势作用于 [Home 交互规格](../surfaces/home.zh-CN.md) 定义的基础信息区合格空白，并按 [quick-actions.zh-CN.md](quick-actions.zh-CN.md) 识别。可见时间行、完整日期星期行、两者的可聚焦触控目标、系统 inset 以及其他所有交互元素均不属于触发区域。
- 未来在该区域展示天气等信息时，其内容和交互目标会从有效区域中排除，但不得占满所有具有实际可操作性的空白。
- 点击时间仍然立即打开时钟；点击日期星期仍然立即打开日历。两种操作都不得为了等待可能出现的第二次点击而延迟。
- 双击槽位采用平台的双击时间和移动容差，长按槽位采用平台长按阈值，不定义产品专用硬编码阈值。
- 移动超过平台点击容差、Home→Drawer 上滑、双击槽位识别期间的长按、手势取消，或手势进入排除目标时，取消识别。
- 锁屏请求成功时不显示 Toast、不震动、不增加动画或二次确认；系统屏幕变化就是操作结果。
- 服务已经启用，但锁屏操作当前不可用或执行失败时，Home 保持可用，并显示短 Toast `无法锁定屏幕`；Launcher4Max 不自动重试。
- 服务未启用时，该手势不产生产品行为，也不意外打开系统设置。

## Settings 与授权

锁屏动作的授权入口是 [quick-actions.zh-CN.md](quick-actions.zh-CN.md) 定义的快捷操作设置页中的锁屏服务状态行。Settings 本身不再单独显示锁屏项。

- 该行在快捷操作设置页中始终存在；所需 Launcher4Max 无障碍服务已经启用并连接时副标题显示 `已开启`，其他情况显示 `已关闭`。它是本能力的常设状态与授权通道，与当前槽位绑定无关。
- 点击该行打开本地说明界面，展示当前状态、用途、隐私边界和 `打开无障碍设置` 操作。
- 服务关闭时把某槽位绑定为 `锁屏` 也会进入同一流程；按 [quick-actions.zh-CN.md](quick-actions.zh-CN.md)，无论该流程结果如何，该绑定都保持已保存。
- 说明界面使用 [Settings 表现规范](../presentation/settings.zh-CN.md#信息-bottom-sheet)定义的信息 Bottom Sheet 几何；正文超出可用高度时滚动，标题保持固定。
- 每次以启用服务为目的跳转系统无障碍设置前，Launcher4Max 单独展示 [privacy.zh-CN.md](privacy.zh-CN.md#锁屏显著披露) 定义的显著披露，并提供 `取消` 与 `同意并继续`。同意并继续只确认本次跳转并打开系统界面；Launcher4Max 不保留披露确认历史，该操作也不表示 Android 已经启用服务。
- 从系统设置返回后立即刷新真实服务状态。Android 状态是唯一权威来源；Launcher4Max 不提供可能与系统状态不一致的独立开关。
- 服务已经启用时，说明界面继续提供同一系统设置入口，供用户检查或关闭服务。
- 系统无障碍设置无法打开时，显示短 Toast `无法打开无障碍设置`，并保留当前 Settings 位置。

## 无障碍服务边界

当前产品仅授权 Android 无障碍服务执行这一由用户明确触发的锁屏操作。

- Launcher4Max 不是无障碍工具，也不得把自身描述成无障碍工具。
- 服务不请求读取窗口内容，不检查其他应用界面内容，不为分析收集无障碍事件，不推断行为，也不根据后台条件自动执行操作。
- 除本能力所需的系统锁屏操作外，不执行其他全局操作。
- 不使用设备管理员作为回退方案。
- 用户关闭或撤销服务后，只移除锁屏能力，不降低任何独立 Launcher 行为。
- 进程终止、服务断开、系统操作不可用或授权变化时必须以关闭能力的方式失败；只有当前服务连接能够执行用户明确请求时，才发出锁屏操作。
- 未来扩展服务用途必须由作者重新决定，并重新审查产品、隐私、安全、平台政策和验证范围。

## 隐私与分发

本地 Privacy 展示和独立显著披露采用 [privacy.zh-CN.md](privacy.zh-CN.md) 中当前产品正文。Privacy 正文、服务说明、应用商店说明或外部页面均不得替代显著披露。

当前仅通过 GitHub 分发并不免除上述披露义务。未来进入应用商店前，必须重新评估适用的无障碍服务声明、显著披露、同意、商店说明和审核要求。

## 验收意图

- Given 某槽位绑定为 `锁屏` 且服务已经启用，when 绑定手势在符合条件的空白区域被识别且没有其他手势取得所有权，then Launcher4Max 请求一次系统锁屏操作。
- Given 被识别的手势发生在时间、日期星期、收藏、编辑表面或其他交互目标上，then 不触发锁屏。
- Given 服务未启用或已经撤销，when 用户使用所有独立 Launcher 路径，then 这些路径保持完整可用，且 Launcher4Max 不执行锁屏操作。
- Given 服务已经启用但操作失败，when 绑定手势被识别，then Launcher4Max 保持在 Home、展示一次本地化失败提示，且不重试。
- Given 用户从无障碍设置返回，when Settings 恢复，then 显示状态与 Android 当前服务状态一致。

## 平台参考

- [Android AccessibilityService API](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [Google Play AccessibilityService 政策](https://support.google.com/googleplay/android-developer/answer/10964491)
- [Google Play 显著披露与同意指南](https://support.google.com/googleplay/android-developer/answer/11150561)
- [Android DevicePolicyManager API](https://developer.android.com/reference/android/app/admin/DevicePolicyManager)
