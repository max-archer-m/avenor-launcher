# Double-Tap Lock Interaction Specification

> Public semantic source: English. Chinese counterpart: [double-tap-lock.zh-CN.md](double-tap-lock.zh-CN.md). Current Privacy and prominent-disclosure copy is defined in [privacy.md](privacy.md).

## Purpose and scope

Double-tap lock is an author-required daily-use Home capability. Two taps on eligible blank space in Home's basic-information region request the Android system lock-screen action.

The capability is optional, disabled until the user explicitly enables Avenor's accessibility service in system settings, and never gates Home, Drawer, application launching, Settings, or another core path. Avenor does not request this capability during initial startup.

## Trigger region and gesture

- The basic-information module keeps practical full-width blank space outside its current content and interactive targets for this gesture. Both taps must begin and end inside that eligible blank space.
- Future information displayed in this module, including weather, reduces the eligible area by its own content and targets but must not consume all practical blank space.
- The visible time line, the complete date-and-weekday row, their focusable targets, system insets, and every other interactive element are excluded.
- A tap on time continues to open Clock immediately. A tap on date and weekday continues to open Calendar immediately; neither action waits for a possible second tap.
- Recognition uses the platform double-tap timing and movement tolerance rather than product-specific hard-coded thresholds.
- A drag beyond the platform tap tolerance, an upward Home-to-Drawer gesture, a long press, cancellation, or a tap crossing into an excluded target cancels double-tap recognition.
- A successful lock request produces no Toast, haptic response, animation, or additional confirmation because the system screen transition is the result.
- When the service is enabled but the lock action is currently unavailable or fails, Home remains available and shows the short localized Toast `Unable to lock screen`. Avenor does not retry automatically.
- When the service is disabled, the gesture has no product action and does not open system settings unexpectedly.

## Settings and authorization

Settings contains one primary item titled `Double-tap to lock`.

- Supporting text is `On` when the required Avenor accessibility service is enabled and connected, and `Off` otherwise.
- Selecting the item opens a local explanation surface showing the current state, purpose, privacy boundary, and an `Open accessibility settings` action.
- Before a handoff intended to enable the service, Avenor presents the separate prominent disclosure defined in [privacy.md](privacy.md#double-tap-lock-prominent-disclosure), with `Cancel` and `Agree and continue`. Agree and continue confirms only the current handoff and opens the system destination; Avenor retains no disclosure-acknowledgement history, and continuing does not imply that Android enabled the service.
- Returning from system settings refreshes the actual service state immediately. Android's state is authoritative; Avenor does not display an independent toggle that can become inconsistent with it.
- When enabled, the explanation surface offers the same system-settings handoff so the user can review or disable the service.
- Failure to open the system destination shows the short localized Toast `Unable to open accessibility settings` and preserves the current Settings position.

## Accessibility-service boundary

The current product authorizes an Android accessibility service only for this explicit user-triggered lock action.

- Avenor is not an accessibility tool and must not present itself as one.
- The service does not request window-content retrieval, inspect other applications' interface content, collect accessibility events for analytics, infer behavior, or automate actions from background conditions.
- It performs no global action other than the lock-screen action required by this capability.
- It does not use Device Administrator as a fallback.
- Disabling or revoking the service removes double-tap lock without degrading any independent Launcher behavior.
- Process death, service disconnection, an unavailable system action, or authorization changes must fail closed: no lock request is issued unless the current service connection can perform the explicit action.
- Any future expansion of the service purpose requires a new author decision plus renewed product, privacy, security, platform-policy, and validation review.

## Privacy and distribution

The local Privacy presentation and separate prominent disclosure use the current product copy in [privacy.md](privacy.md). The disclosure is not replaced by the Privacy statement, service description, application listing, or external page.

Current GitHub-only distribution does not remove this disclosure obligation. Any future store distribution must re-evaluate the applicable accessibility-service declaration, prominent-disclosure, consent, listing, and review requirements before publication.

## Acceptance intent

- Given the service is enabled, when both taps occur in eligible blank space without another gesture taking ownership, then Avenor requests one system lock action.
- Given either tap occurs on time, date and weekday, a favorite, an editing surface, or another interactive target, then double-tap lock does not trigger.
- Given the service is disabled or revoked, when the user uses all independent Launcher paths, then those paths remain fully available and Avenor performs no lock action.
- Given the service is enabled but the action fails, when the gesture is recognized, then Avenor stays on Home, reports one localized failure, and does not retry.
- Given the user returns from accessibility settings, when Settings resumes, then the displayed state matches Android's current service state.

## Platform references

- [Android AccessibilityService API](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [Google Play AccessibilityService policy](https://support.google.com/googleplay/android-developer/answer/10964491)
- [Google Play prominent disclosure and consent guidance](https://support.google.com/googleplay/android-developer/answer/11150561)
- [Android DevicePolicyManager API](https://developer.android.com/reference/android/app/admin/DevicePolicyManager)
