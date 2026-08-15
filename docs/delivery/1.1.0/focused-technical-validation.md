# 1.1.0 Focused Technical Validation

> Semantic source: English. Chinese counterpart: [focused-technical-validation.zh-CN.md](focused-technical-validation.zh-CN.md).
>
> This is a non-authoritative implementation evidence record for the Android backup gate and the pre-ADR AccessibilityService probe requested on 2026-08-15. It does not authorize production integration, create an ADR, complete an iteration or version, or claim device behavior that was not observed.

## Scope and environment

- Repository: `avenor-launcher`
- Host: Windows PowerShell
- Project baseline: one Android application module, `minSdk` 31, `targetSdk` 36, `compileSdk` 37
- Reviewed variants: current source Manifest, freshly generated debug merged Manifest, and freshly generated release main merged Manifest
- Physical device: not used
- Remote, signing, artifact publication, and Git operations: not performed

Official platform interpretation used [Android Auto Backup guidance](https://developer.android.com/identity/data/autobackup), the [Android 12 backup behavior changes](https://developer.android.com/about/versions/12/behavior-changes-12), [`AccessibilityService`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService.html), [`AccessibilityServiceInfo`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo), and [`AccessibilityManager`](https://developer.android.com/reference/android/view/accessibility/AccessibilityManager).

## Android backup confirmation

### Observed persistence and configuration

- `AtomicFileFavoriteStore(Context)` resolves its base file to `context.filesDir/favorites.bin`.
- The store writes a versioned binary document through `AtomicFile.startWrite`, `finishWrite`, and `failWrite`. AtomicFile companion files remain in the same internal files domain.
- The source Manifest explicitly sets `android:allowBackup="false"` and `android:dataExtractionRules="@xml/data_extraction_rules"`.
- `data_extraction_rules.xml` excludes `root`, `file`, `database`, `sharedpref`, and `external`, each at path `.`, independently under both `cloud-backup` and `device-transfer`.
- The fresh debug and release main merged Manifests retain `allowBackup=false` and the `dataExtractionRules` reference.
- No `android:fullBackupContent` rule exists. That is acceptable for the current supported runtime range because `minSdk` 31 excludes Android 11 and lower; API 31 and higher use `data-extraction-rules` for an app targeting API 31 or higher.

### Assessment

The current checked-in configuration satisfies the documented Android API 31-37 configuration boundary for excluding `filesDir/favorites.bin` and its same-directory atomic companion files from Android cloud backup and Android device-to-device transfer. `allowBackup=false` is retained as defense in depth for cloud backup, but it is not treated as sufficient for D2D because Android documents OEM variation for apps targeting API 31 or higher. The explicit `device-transfer` exclusions are the controlling D2D evidence.

No persistence-format, data-boundary, `minSdk`, or application-identity change is required. No source configuration change was made to the existing backup rules.

This is configuration and merged-Manifest evidence, not a transport-level device observation. An actual backup/restore or setup-wizard transfer was not run, so OEM adherence and transport behavior remain `Not run`. Cross-platform transfer is not configured and no corresponding non-Android application identity exists; no cross-platform transfer result is claimed.

### Added regression checks

- `BackupConfigurationTest` checks the packaged application's `FLAG_ALLOW_BACKUP` state and verifies file-domain exclusions under both cloud and device-transfer sections.
- `FavoriteStoreTest.contextStoreWritesFavoritesInsideTheExcludedFilesDirectory` verifies the Context constructor writes `favorites.bin` directly under `filesDir`.

These instrumentation checks were authored but did not compile or run in this session because the existing `HomeScreenTest.kt` has an unrelated unresolved `assertDoesNotExist` reference that stops the complete androidTest source set.

## AccessibilityService focused probe

### Probe isolation

The probe exists only under `app/src/debug`. A fresh `assembleDebug` packaged it, while a fresh `processReleaseMainManifest` completed successfully and the release merged Manifest contained zero matches for the probe service, `BIND_ACCESSIBILITY_SERVICE`, or the accessibility-service intent. The probe is therefore not integrated into the release/main variant.

### Evidence-supported boundary

- The debug service is exported solely for Android system binding and is protected by `android.permission.BIND_ACCESSIBILITY_SERVICE` on the service component.
- Its intent filter contains only `android.accessibilityservice.AccessibilityService`, and its metadata references one accessibility-service XML resource.
- Metadata explicitly disables window-content retrieval, gesture performance, key filtering, fingerprint gestures, and touch exploration. It declares generic feedback only so enabled-service enumeration can include it and declares no accessibility event types, package filters, flags, screenshots, or other capability.
- `onAccessibilityEvent` and `onInterrupt` perform no product work and retain no event data.
- Enabled state is queried from `AccessibilityManager.getEnabledAccessibilityServiceList(FEEDBACK_ALL_MASK)` and matched against the exact package/class `ComponentName`.
- Connection state is separately owned by a volatile in-process connection seam and is cleared on unbind or destruction. System-enabled and currently connected therefore remain distinct facts.
- The application-facing port exposes only `requestLock()`.
- A disconnected service returns `ServiceDisconnected` without issuing an action.
- A connected service checks `getSystemActions()` for `GLOBAL_ACTION_LOCK_SCREEN` before calling `performGlobalAction` exactly once. Missing action and a false platform return become `ActionUnavailable` and `ActionRejected`; there is no retry or fallback.
- The probe adds no network service, analytics, monitoring, Device Administrator, persistence, disclosure acknowledgement, event collection, or background-triggered action.

### Unknown runtime behavior

The following remain `Not run` because no physical device was used:

- appearance and enablement in OEM accessibility settings;
- exact enabled-state query before and after enablement or revocation;
- service connection, disconnection, process death, and reconnect timing;
- actual `getSystemActions()` contents;
- successful or rejected `GLOBAL_ACTION_LOCK_SCREEN` execution;
- OEM refusal and user-visible fail-closed behavior; and
- gesture, disclosure, Settings, Privacy, and localization integration.

The probe contains no production UI trigger and intentionally does not test the complete Iteration 10 user journey.

## ADR input

Evidence supports the following proposed ADR input, but not creation of an Active ADR:

- **Sole purpose:** request one Android lock-screen global action after the contracted explicit Home double tap.
- **Manifest boundary:** one service with the accessibility-service intent, exact metadata, `exported=true` for system binding, and component-level `BIND_ACCESSIBILITY_SERVICE` protection.
- **Prohibited behavior:** window/content retrieval, event processing or retention, package observation, gestures, key/fingerprint/touch-exploration capabilities, screenshots, analytics, network processing, background triggers, unrelated global actions, and Device Administrator fallback.
- **State model:** exact component enabled state comes from Android; current connection is separate in-process state; product On requires both.
- **Application interface:** one lock request returning requested, disconnected, unavailable, or rejected; no general global-action interface.
- **Failure model:** fail closed on absent authorization, disconnection, unavailable action, false platform result, process death, or OEM refusal; never retry automatically or degrade independent Launcher paths.
- **Data boundary:** no accessibility content or event data is accessed, retained, transmitted, or used for analytics; no acknowledgement history is stored.
- **Disclosure relationship:** Privacy, explanation, and prominent disclosure remain separate local presentations; only the contracted continue action may precede an enable-oriented system handoff.
- **Compatibility:** the lock action exists below `minSdk` 31, but current availability and OEM behavior require device evidence.
- **Re-review triggers:** any new accessibility purpose, capability, permission, event processing, background action, data handling, fallback, distribution channel, or store publication.

An Active ADR is premature. Mainline integration, device behavior, acceptance, commit, and synchronization do not yet exist. After runtime evidence supports the remaining boundary, the author must accept the implemented trade-offs and the project must follow its ADR activation rules.

## Validation results

| Check | Result | Evidence |
| --- | --- | --- |
| Source backup configuration inspection | `Passed` | Manifest, data extraction rules, and favorite store path inspected |
| Fresh debug merged Manifest and package | `Passed` | `assembleDebug` completed; probe and backup attributes present |
| Fresh release main merged Manifest | `Passed` | `processReleaseMainManifest` completed; backup attributes present and debug probe absent |
| Debug instrumentation test APK compilation | `Failed` | Existing `HomeScreenTest.kt` unresolved `assertDoesNotExist` stops androidTest compilation |
| Added instrumentation tests execution | `Not run` | Test APK did not compile and no device was used |
| Android lint | `Failed` | Existing `OldTargetApi` and `NewerVersionAvailable` findings are errors under current warnings-as-errors policy |
| Connected/device validation | `Not run` | No author-designated physical device was used |
| API 31 recommended evidence | `Not run` | No API 31 environment used |
| Additional API 36/37 physical device | `Not run` | No additional device used |
| Additional OEM/profile evidence | `Not run` | No additional OEM/profile environment used |

The first combined Gradle attempt was also interrupted by locked OneDrive-generated resource directories. Only verified `app/build` outputs were removed; a clean retry then reached the results above.

## Blocking and follow-up findings

1. The English Privacy semantic source and Simplified Chinese counterpart were compared for the contact and prominent-disclosure boundary; no material omission was found. The approved copy was not modified.
2. The existing androidTest compilation error prevents the new backup and probe checks from compiling or running. It should be handled by its owning implementation task rather than hidden as validation success.
3. No permission, data-processing, background-behavior, persistence, `minSdk`, or product-scope expansion was found necessary. There is therefore no new consequential scope decision to return to the author at this stage.
