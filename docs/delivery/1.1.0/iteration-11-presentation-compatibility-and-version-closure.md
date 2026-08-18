# Iteration 11: Presentation, compatibility, and version closure

> Applies to [Avenor Launcher 1.1.0](delivery.md). This record plans the bounded implementation cleanup and evidence work required to close the version. It does not authorize implementation, signing, artifact movement, commit, push, tag, GitHub Release, or distribution.

## Status

- Value: `Completed`
- Updated: 2026-08-18
- Basis: The application-name presentation cleanup and Home-to-Drawer gesture ownership correction are complete, and the author completed basic acceptance and `1.0.0` upgrade validation on the designated daily-use device.

## Objective

Align application-name presentation and Home-to-Drawer gesture coverage with their current product contracts, remove obsolete marquee behavior, and establish evidence that the complete selected `1.1.0` journey is an upgrade-safe author daily-use baseline.

## Product and version references

- [1.1.0 delivery](delivery.md)
- [Design foundations](../../product/design-foundations.md#typography)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Validation guide](../../validation.md)
- [Release governance](../../release.md)

## Observable outcome

Application names on Home and Drawer remain static, one line, and end-ellipsized when necessary, with no marquee start, pause, priority, or restart behavior. In normal Home mode, an upward gesture can begin across every Avenor-managed Home interaction region defined by the navigation contract, rather than only in blank space, while preserving the existing arbitration for favorite scrolling, selection, long-press, double-tap lock, edit mode, and system-reserved insets. The accepted `1.1.0` APK preserves readable `1.0.0` favorite data and completes the selected Iterations 7-10 journey on the required baseline with exact source, build, version, signing-category, device, and known-gap evidence.

## Included work

- Remove Drawer application-name marquee motion and obsolete shared timing, active-entry priority, pause, restart, transition, and test state.
- Confirm static one-line end-ellipsis presentation for Home primary favorites and Drawer application names across included locales and representative font scaling.
- Correct Home-to-Drawer gesture ownership so an upward gesture may begin from every Avenor-managed Home interaction region allowed by the existing navigation contract, including time, date, favorite entries, blank space, Loading, Error, and Retry presentation.
- Preserve the contracted gesture arbitration: a scrollable favorite group consumes movement until its end and transfers only remaining displacement from the same continuous gesture; an upward drag that takes transition ownership suppresses selection, long-press, Retry, and double-tap-lock activation; Home edit mode disables the transition; system-reserved insets remain governed by Android.
- Run or collect the version-level validation required by [delivery.md](delivery.md), including upgrade, regression, offline, localization, gesture, identity/profile, Settings, double-tap lock, and failure-path evidence.
- Inspect the current merged manifest and applicable Android backup configuration, including `android:allowBackup`, `android:dataExtractionRules`, and legacy or equivalent rules for the supported API range; record evidence that Avenor-owned favorite persistence files are excluded from Android cloud backup and device-transfer backup.
- Confirm exact `applicationId`, `versionName`, next-unused `versionCode`, source commit, build stage, signing category, device/API/OEM observations, and available APK identity.
- Record every failed, skipped, unknown, unavailable, or out-of-scope check and assign remaining issues to an explicit follow-up destination.
- Complete Iterations 7-11 and the version completion result only from observed evidence and author acceptance.

## Excluded work

- A replacement marquee, multi-line application names, user-configurable text motion, or another presentation capability outside the current contract.
- Companion favorites, cross-group editing, uninstall actions, manual language selection, broader Settings, monitoring-platform integration, public distribution, tag, milestone, or GitHub Release.
- Claiming unperformed compatibility, policy, security, privacy, license, performance, or automated checks as passed.

## Technical change areas

Drawer and shared application-name presentation state, obsolete marquee cleanup, Home gesture ownership and nested-scroll handoff, touch-action arbitration, focused UI/state tests, regression validation, upgrade installation, build/version identity, signing-category observation, APK traceability, and delivery evidence. Implementation structure remains a technical choice and must preserve the navigation contract's observable behavior. A material issue found here returns to its owning iteration or to an explicitly authorized corrective task rather than being hidden inside closure.

## Dependencies and sequence

Depends on completed and author-accepted Iterations 7-10 and their available evidence. It is the version closure iteration because its cleanup and validation span the selected product journey; it does not create a dependency among Iterations 7-9 that does not otherwise exist.

## Migration and compatibility impact

Direct upgrade from accepted `1.0.0` must preserve every readable favorite and its order until the author changes that order. Removing marquee state must not change application identity, ordering, list position, selection, scrolling, Home/Drawer transition, or accessibility semantics. Correcting gesture coverage must not make an upward drag also activate the touched Home element, bypass favorite scrolling, enable the transition during edit mode, or claim Android-reserved system gesture areas. Downgrade is not supported.

## Security, privacy, permission, and licensing impact

Closure verifies the recorded permission, service, manifest, dependency, signing-category, and privacy boundaries required by the selected author daily-use baseline. It does not independently authorize a new dependency, monitoring service, release signing identity, upload, or specialist conclusion.

## Risks and unresolved decisions

- A failed included-path check must be corrected or returned for explicit scope/acceptance direction; it cannot be relabeled as a harmless limitation to close the version.
- The mandatory primary-device evidence remains a version-completion gate until the author or an explicitly authorized agent performs it. Recommended API 31, additional API 36/API 37 device, and OEM/profile evidence may remain unavailable when recorded under the version rules.
- The Android backup configuration has been checked at the project-configuration and merged-manifest levels; actual Android backup transport was not run and does not block the author's primary-device completion result.
- The author confirmed that the current `applicationId`, `versionName`, and `versionCode` are set correctly; APK artifact traceability is not a blocker for this author's basic acceptance.
- Broadening the gesture start region can expose conflicts among click, long-press, double-tap lock, favorite scrolling, Retry, and Home-to-Drawer recognition. A passing result requires the existing navigation ownership and continuous handoff rules, not merely recognition from more coordinates.

## Validation plan

On the mandatory author-designated primary physical device, the version exit covers: upgrade from accepted `1.0.0`; zero/one/two/many primary favorites; edit visibility, surfaces, reorder, persistence, restart, and failures; application shortcuts from Home and Drawer for the exact identity/profile; Settings navigation, default-home state refresh, local content, repository, and version display; double-tap-lock disclosure, authorization, gesture exclusions, revocation, and failure; static application names during scrolling, updates, and Home/Drawer transitions; English, Simplified Chinese, and fallback resources; offline core paths; and regression of accepted `1.0.0` paths. Home gesture coverage must be observed from time, date, each favorite group, blank space, Loading, Error, and Retry regions. It must also show that a transition-owning drag suppresses the touched element's action, a scrollable favorite group transfers only post-boundary displacement without requiring a lift, a non-overflowing or already-ended group enters the transition directly, edit mode keeps the transition disabled, and system-reserved insets remain unaffected. The exit also requires project evidence that the Android backup configuration excludes Avenor-owned favorite persistence files from cloud backup and device transfer.

API 31, one additional API 36 or API 37 physical device, and additional OEM/profile coverage are recommended evidence. When not performed they must be recorded as `Unknown`, `Not run`, or `Unavailable`, and their absence alone does not block `1.1.0`. When performed, a failure on an included path must be resolved or returned for author disposition and cannot be ignored because the environment was recommended. Applicable commands, environments, procedures, and results must be recorded accurately.

## Acceptance evidence

The author reported the following closure evidence:

- Executor: the project author.
- Build: `debug`.
- Signing: the author's local keystore file.
- Device: Samsung Galaxy S23 Ultra.
- System environment: Android 16, One UI 8.5.
- Profile: personal profile; the device also contains some app-clone instances.
- Fresh installation: `1.1.0` was directly installed and passed basic acceptance.
- Upgrade validation: `1.0.0` was upgraded in place to `1.1.0`; multiple favorites and their order were preserved.
- Upgrade baseline source: `1.0.0` used commit `96a9e68a21c7c55844deb06b2e4ca7284788d091`.
- Version identity: the author confirmed that the current version settings are correct.
- Result: the presentation cleanup, Home-to-Drawer gesture correction, and upgrade compatibility were basically accepted.

Additional API/OEM coverage, actual Android backup transport, and other non-primary-device checks were not performed and are not treated as blockers for this iteration. The author did not require APK filename, hash, or more detailed device fields in this record.

## Related decisions, commits, and tags

- [Focused backup and AccessibilityService technical validation](focused-technical-validation.md) records the current backup-configuration evidence and unperformed transport/device checks.
- Historical `1.0.0` records retain the fact that a Drawer marquee foundation was implemented then; this iteration records its intentional removal for `1.1.0` rather than rewriting that history.
- The `1.0.0` upgrade baseline is commit `96a9e68a21c7c55844deb06b2e4ca7284788d091`. This record does not claim a tag, GitHub Release, upload, or distribution for `1.1.0`.

## Final result

Iteration 11 completed the in-scope presentation cleanup and Home-to-Drawer gesture ownership correction. The author performed basic acceptance on a Samsung Galaxy S23 Ultra running Android 16 and One UI 8.5, using the personal profile, and confirmed that upgrading from `1.0.0` to `1.1.0` preserved multiple favorites and their order. The `1.0.0` upgrade baseline was commit `96a9e68a21c7c55844deb06b2e4ca7284788d091`; the current version settings are correct, and the `debug` build used the author's local keystore file.

## Remaining issues and handoff

This record now reflects the author's reported basic acceptance and upgrade result. Commit, synchronization, tag, Release, upload, and distribution remain governed by their separate authorizations.
