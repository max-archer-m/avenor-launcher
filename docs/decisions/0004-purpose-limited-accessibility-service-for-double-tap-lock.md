# ADR-0004: Purpose-Limited AccessibilityService Boundary for Double-Tap Lock

## Status

- Value: `Active`
- Updated: 2026-08-16
- Basis: The author accepted the physical-device validation of the debug-only implementation path and the boundary recorded below. This ADR and the supporting iteration evidence establish the constraint for mainline integration.

## Date

2026-08-16

## Context

Iteration 10 delivers an optional double-tap lock capability that allows the author to lock the Android screen by double-tapping eligible blank space on Home. The implementation uses Android's `AccessibilityService` API to request one `GLOBAL_ACTION_LOCK_SCREEN` action after the user explicitly enables the service in system settings and reviews the local Privacy and prominent disclosure.

An accessibility service is a privileged platform capability with significant privacy, security, and platform-policy implications. Stores and platform reviews impose strict constraints on when, how, and why such a service may be used. The current product contract explicitly limits the service to a single purpose and boundary.

## Decision

Avenor's accessibility service exists only to support double-tap lock. Its boundary is:

- **Sole purpose**: Perform one `GLOBAL_ACTION_LOCK_SCREEN` action after an explicit user double-tap on eligible Home basic-information blank space.
- **Manifest declaration**: The service uses the minimum `accessibilityService` XML configuration required to request global actions. It does not declare `flagRequestFilterKeyEvents`, `flagIncludeNotImportantViews`, `flagReportViewIds`, `flagRetrieveInteractiveWindows`, or any capability that provides window-content retrieval.
- **Permissions**: The service requests no Android permissions beyond those required for the minimum `AccessibilityService` declaration. It does not request `BIND_ACCESSIBILITY_SERVICE`, `SYSTEM_ALERT_WINDOW`, or any device-administration permission.
- **Data access**: The service does not read or retain any window content, screen content, accessibility events, or data from other applications. The `AccessibilityLockProbeService` debug implementation confirms it ignores `onAccessibilityEvent`, `onInterrupt`, and all other event callbacks except `onServiceConnected`, `onUnbind`, and `onDestroy`.
- **Global actions**: The service requests only `GLOBAL_ACTION_LOCK_SCREEN` when the user double-taps. It performs no other global action, background automation, or continuous monitoring.
- **Fail-closed behavior**: If the service is disabled, revoked, disconnected, or the lock action is unavailable or rejected, the capability is silently unavailable. All independent Launcher paths (Home, Drawer, application launching, Settings) remain unaffected.
- **User control**: The service is disabled until the user explicitly enables it in Android accessibility settings. The user can disable it at any time without losing independent Launcher functionality. No in-app toggle exists; control is through the platform's settings path.
- **Disclosure**: Before the user enables the service, Settings presents the current local Privacy description and a separate prominent disclosure that explains the service's sole purpose, data-access boundary, and disable path. The disclosure uses `Cancel` and `Agree and continue` choices and is not retained as acknowledged history.
- **Home gesture boundary**: Double-tap recognition is enabled only in eligible basic-information blank space when `editMode` is `false`. Time, date, favorites, edit surfaces, and all other interactive targets are excluded from double-tap lock detection.
- **Connection model**: `AccessibilityLockConnection` is a debug-only application-to-service seam. It owns no `Context`, event, or window data and does not persist state outside the application process. The debug `AccessibilityLockProbeService` calls `AccessibilityLockConnection.connected(this)` on `onServiceConnected` and `disconnected(this)` on `onUnbind`/`onDestroy`.
- **Production integration**: When this boundary is satisfied and the service is integrated into the mainline, the implementation must preserve the same restrictions and the same debug-only connection model must not be exposed to release builds.

## Rationale

This boundary ensures the accessibility service is a narrow, user-controlled utility that performs exactly one action on demand. It keeps the service's purpose, permissions, and data-access behavior transparent and reviewable. The fail-closed design guarantees that any service-state change cannot break core Launcher functionality.

Using the platform's accessibility settings and disclosure system keeps user authorization explicit and platform-native. The current product Privacy statement and prominent disclosure text provide the contract for what the service does and does not do.

The connection model keeps the service stateless and does not introduce a persistent background bridge or shared data store that would require broader architectural review.

## Considered Options

### Use Device Administrator API

- Benefits: `DevicePolicyManager.lockNow()` is the established Android mechanism for programmatic screen locking.
- Trade-offs: Requires `DEVICE_ADMIN` permission, device-administration enrollment, and revocation through a specialized system path. Platform policy restricts its use, and stores may scrutinize or reject non-enterprise uses. The current product does not need its broader administrative capabilities.

### Add a Home-surface overlay and manage touches manually

- Benefits: Would avoid the accessibility-service permission and platform-policy boundary entirely.
- Trade-offs: Requires a system overlay permission, complex gesture handling, and would not be available while other overlays are active. It also risks interfering with Android's native gesture system and does not provide a cleaner authorization story.

### Use a purpose-limited AccessibilityService (selected)

- Benefits: Uses the platform's explicit authorization and settings path, requires one minimal permission, and performs only the needed action without persistent monitoring. The current product boundary and Privacy text are written around this approach.
- Trade-offs: Platform policy and stores may review the usage, and the service must be explicitly enabled by the user. The boundary must be preserved in code, disclosure, and validation to remain within acceptable platform constraints.

## Consequences

- No implementation may expand the accessibility service to read window content, observe other applications, collect accessibility events, perform background automation, or add any other global action without a new active ADR.
- Store distribution requires a fresh platform-policy and disclosure review even if the GitHub-distributed debug build is accepted.
- The service must remain optional and must not be required for any independent Launcher path.
- The `android:accessibilityService` XML must not be expanded to request capabilities beyond what the current boundary requires.
- Future platform compatibility testing must verify that the service continues to request only `GLOBAL_ACTION_LOCK_SCREEN` and that fail-closed behavior is preserved across API 31–37.
- When integrated to mainline, the service must use the same `AccessibilityLockProbeService` boundary but the package and class resolution must be moved from debug to main with the same manifest constraints.

## Validation Evidence and Gaps

- Physical-device validation on the author's primary device confirmed that a valid double tap on eligible Home blank space requests one lock action when the service is enabled, and that revocation or disabling the service leaves independent Launcher paths unaffected.
- Debug implementation confirms that `onAccessibilityEvent` and `onInterrupt` are no-ops, that only `GLOBAL_ACTION_LOCK_SCREEN` is requested, and that the connection model owns no persistent data.
- Settings UI, Privacy presentation, and prominent disclosure were implemented and observed to display the current local text.
- The current manifest and `accessibilityService` XML for the debug implementation confirm no window-content, key-event, or extended-view capabilities are declared.
- API 31 and one additional API 36 or API 37 physical-device coverage remain recommended compatibility evidence. Unperformed OEM and Private Space scenarios do not by themselves invalidate this decision but must be recorded as `Unknown`, `Not run`, or `Unavailable` in the version record.

## Implementation Notes

- The debug implementation uses `AccessibilityLockProbeService` in `src/debug` to validate the boundary. When moving to mainline, the same service class and manifest declaration must be preserved with identical `accessibilityService` XML constraints.
- `AccessibilityLockConnection` is a debug-only singleton that provides a stateless `LockRequestPort` adapter. It does not persist across process restarts and must not be converted into a background service or persistent bridge.
- The double-tap gesture uses `detectTapGestures(onDoubleTap = ...)` with `pointerInput` scoped to the eligible Home blank space and only when `!editMode`. The gesture must not be moved to a broader Home-surface modifier.

## Implementation and Validation Evidence

- Iteration 10 record: `docs/delivery/1.1.0/iteration-10-double-tap-lock.md`
- Physical-device validation: author-reported successful double-tap lock behavior, Settings state refresh, disclosure flow, and revocation/fail-closed behavior on the primary device
- Debug implementation files: `app/src/debug/java/com/avenor/launcher/AccessibilityLockProbeService.kt`, `app/src/main/java/com/avenor/launcher/AccessibilityLock.kt`
- Settings integration: `SettingsScreen.kt`, `SettingsPlatform.kt`, and related Privacy and disclosure resources
- Home gesture integration: `HomeScreen.kt` double-tap detection in eligible blank space

## Replaces

None

## Inactivation

- Date: Not applicable while `Active`
- Reason: Not applicable while `Active`
- Replaced by: None
- Consequences: None