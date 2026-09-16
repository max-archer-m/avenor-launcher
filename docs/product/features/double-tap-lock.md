# Screen Locking Interaction Specification

> Public semantic source: English. Chinese counterpart: [double-tap-lock.zh-CN.md](double-tap-lock.zh-CN.md). Current Privacy and prominent-disclosure copy is defined in [privacy.md](privacy.md). This capability was historically introduced as double-tap lock; the user-configurable trigger slots and bindings are defined in [quick-actions.md](quick-actions.md).

## Purpose and scope

Screen locking is an optional Home capability. The Home quick-action slot that the user binds to `Screen lock` requests the Android system lock-screen action through Avenor's purpose-limited accessibility service.

The capability is optional and inert until the user both binds a quick-action slot to `Screen lock` in [quick-actions.md](quick-actions.md) and explicitly enables Avenor's accessibility service in system settings. It never gates Home, Drawer, application launching, Settings, or another core path. Avenor does not request this capability during initial startup.

## Trigger and result

- The bound gesture operates on the eligible blank space of the basic-information region defined by the [Home interaction specification](../surfaces/home.md) and is recognized per [quick-actions.md](quick-actions.md). The visible time line, the complete date-and-weekday row, their focusable targets, system insets, and every other interactive element are excluded.
- Future information displayed in this region, including weather, reduces the eligible area by its own content and targets but must not consume all practical blank space.
- A tap on time continues to open Clock immediately. A tap on date and weekday continues to open Calendar immediately; neither action waits for a possible second tap.
- Recognition uses the platform double-tap timing and movement tolerance, or the platform long-press threshold for the long-press slot, rather than product-specific hard-coded thresholds.
- A drag beyond the platform tap tolerance, an upward Home-to-Drawer gesture, a long press on the double-tap slot, cancellation, or a gesture crossing into an excluded target cancels recognition.
- A successful lock request produces no Toast, haptic response, animation, or additional confirmation because the system screen transition is the result.
- When the service is enabled but the lock action is currently unavailable or fails, Home remains available and shows the short localized Toast `Unable to lock screen`. Avenor does not retry automatically.
- When the service is disabled, the gesture has no product action and does not open system settings unexpectedly.

## Settings and authorization

The lock action's authorization entry is the screen-lock service state row on the quick-action settings page defined by [quick-actions.md](quick-actions.md). Settings itself displays no separate screen-lock item.

- That row is always present in the quick-action settings page and its supporting text is `On` while the required Avenor accessibility service is enabled and connected, and `Off` otherwise. It is the standing status and authorization channel for this capability, independent of the current slot bindings.
- Selecting the row opens a local explanation surface showing the current state, purpose, privacy boundary, and an `Open accessibility settings` action.
- The same flow is entered when the user binds a slot to `Screen lock` while the service is off; that binding stays saved regardless of the flow's result, per [quick-actions.md](quick-actions.md).
- The explanation surface uses the informational Bottom Sheet geometry defined by the [Settings presentation specification](../presentation/settings.md#informational-bottom-sheets). Its title remains fixed while an overflowing body scrolls.
- Before a handoff intended to enable the service, Avenor presents the separate prominent disclosure defined in [privacy.md](privacy.md#screen-locking-prominent-disclosure), with `Cancel` and `Agree and continue`. Agree and continue confirms only the current handoff and opens the system destination; Avenor retains no disclosure-acknowledgement history, and continuing does not imply that Android enabled the service.
- Returning from system settings refreshes the actual service state immediately. Android's state is authoritative; Avenor does not display an independent toggle that can become inconsistent with it.
- When enabled, the explanation surface offers the same system-settings handoff so the user can review or disable the service.
- Failure to open the system destination shows the short localized Toast `Unable to open accessibility settings` and preserves the current Settings position.

## Accessibility-service boundary

The current product authorizes an Android accessibility service only for this explicit user-triggered lock action.

- Avenor is not an accessibility tool and must not present itself as one.
- The service does not request window-content retrieval, inspect other applications' interface content, collect accessibility events for analytics, infer behavior, or automate actions from background conditions.
- It performs no global action other than the lock-screen action required by this capability.
- It does not use Device Administrator as a fallback.
- Disabling or revoking the service removes screen locking without degrading any independent Launcher behavior.
- Process death, service disconnection, an unavailable system action, or authorization changes must fail closed: no lock request is issued unless the current service connection can perform the explicit action.
- Any future expansion of the service purpose requires a new author decision plus renewed product, privacy, security, platform-policy, and validation review.

## Privacy and distribution

The local Privacy presentation and separate prominent disclosure use the current product copy in [privacy.md](privacy.md). The disclosure is not replaced by the Privacy statement, service description, application listing, or external page.

Current GitHub-only distribution does not remove this disclosure obligation. Any future store distribution must re-evaluate the applicable accessibility-service declaration, prominent-disclosure, consent, listing, and review requirements before publication.

## Acceptance intent

- Given a slot is bound to `Screen lock` and the service is enabled, when the bound gesture is recognized in eligible blank space without another gesture taking ownership, then Avenor requests one system lock action.
- Given the recognized gesture occurs on time, date and weekday, a favorite, an editing surface, or another interactive target, then screen locking does not trigger.
- Given the service is disabled or revoked, when the user uses all independent Launcher paths, then those paths remain fully available and Avenor performs no lock action.
- Given the service is enabled but the action fails, when the bound gesture is recognized, then Avenor stays on Home, reports one localized failure, and does not retry.
- Given the user returns from accessibility settings, when Settings resumes, then the displayed state matches Android's current service state.

## Platform references

- [Android AccessibilityService API](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [Google Play AccessibilityService policy](https://support.google.com/googleplay/android-developer/answer/10964491)
- [Google Play prominent disclosure and consent guidance](https://support.google.com/googleplay/android-developer/answer/11150561)
- [Android DevicePolicyManager API](https://developer.android.com/reference/android/app/admin/DevicePolicyManager)
