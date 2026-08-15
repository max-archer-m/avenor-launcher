# Avenor Launcher 1.1.0 Technical Assessment

> Semantic source: English. Chinese counterpart: [technical-assessment.zh-CN.md](technical-assessment.zh-CN.md).
>
> This assessment evaluates the feasibility and delivery constraints of the planned [1.1.0 delivery](delivery.md). It does not change product scope, authorize implementation or release activity, or establish a durable architecture decision. The project author retains decision authority.

## Assessment conclusion

The selected `1.1.0` scope is technically feasible on the current API 31 baseline and can be delivered without changing application identity, raising `minSdk`, replacing the existing persistence format, or adding a third-party runtime dependency. Iterations 7-9 and 11 are incremental extensions of the accepted implementation. Iteration 10 is feasible but is the version's principal architecture, privacy, platform-policy, and device-validation risk.

No broad refactor or module split is justified. The implementation should add narrow platform adapters and screen-level state holders around the current composition root, while preserving the established exact launchable identity and project-owned `AtomicFile` boundaries.

## Inputs and current baseline

This assessment uses the current product surfaces, navigation, Privacy and data-handling contract, and design foundations; the development, validation and release guides; Iterations 7-11; and ADR-0002 and ADR-0003. The checked-in implementation currently has:

- `versionCode` 1 and `versionName` `1.0.0`;
- a single Compose application module with `minSdk` 31;
- ordered schema-1 favorites stored through a project-owned atomic file;
- profile-aware launchable identities and inventory reconciliation; and
- shared Home and Drawer application-name marquee behavior that Iteration 11 removes.

The product contract remains authoritative where this assessment and product wording differ. Android documentation establishes API contracts, not OEM behavior; all settings destinations, shortcut exposure, accessibility state, and lock behavior still require evidence on the designated physical device.

## Cross-cutting technical boundary

Keep the application single-module for `1.1.0`. Extract only cohesive boundaries required by the selected work: favorite reorder operations, shortcut discovery/invocation, Settings state, and accessibility lock execution. The top-level application composable may continue to coordinate navigation, but new platform calls and durable writes should not be embedded directly in UI composables.

No new dependency is presently necessary. Compose pointer input, Android framework APIs, coroutines already in use, and the existing persistence layer cover the planned behavior. A reorder or navigation framework would add lifecycle and gesture risk without solving a product requirement.

All new user-facing strings, semantic colors, and reusable dimensions remain resource- or theme-backed. Platform failures must be contained and localized without converting missing handlers, locked profiles, or unavailable services into application crashes.

## Iteration 7: primary-favorite reorder

Schema 1 already stores favorite identities in order. Within the `1.1.0` subset, membership in that list implicitly means membership in the primary group, so the Privacy statement's stored group-and-order description does not require a new field or migration. Reordering only the selected full-width primary list must not pre-model excluded companion-favorite state; a later companion-group implementation will need a separately reviewed schema evolution.

Add one serialized whole-order operation to `FavoriteStore`, such as `replaceOrder`, with these invariants:

- the new list contains the same unique identities as the accepted current list;
- the write remains under the store mutex and uses the existing atomic replacement path;
- read failure is not interpreted as an empty list and cannot be overwritten by a reorder; and
- persistence occurs for a completed valid move, not for every pointer frame.

The drag session, target index, elevation, auto-scroll, and cancellation state are transient UI state. Gesture ownership should be explicit: edit-mode drag consumes its pointer stream, ordinary Home navigation does not begin from the same stream, and Back cancels or exits according to the product contract. Compose's built-in pointer and scroll APIs are sufficient; edge auto-scroll and haptic behavior need focused device checks.

## Iteration 8: platform application shortcuts

Use [`LauncherApps`](https://developer.android.com/reference/android/content/pm/LauncherApps) as a narrow platform adapter. Shortcut discovery must first verify shortcut-host permission, query the exact activity/package and user profile represented by the selected launchable identity, and retain package, shortcut ID, rank/order, label, icon, and user only for the lifetime of the sheet. Shortcut data must not become project-owned persistence.

Invocation must use the exact package, shortcut ID, and user through `startShortcut`. Security, locked-profile, missing-shortcut, and unavailable-target failures must dismiss or recover safely while preserving the originating Home or Drawer state. The shared action sheet should receive a display model and callbacks rather than framework shortcut objects.

The initial implementation should preserve the stable order exposed by the platform query and remain within the existing non-overflow product contract. Device evidence needs at least one dynamic or manifest shortcut from each originating surface, plus no-shortcut, revoked/unavailable, and profile-specific cases where the device exposes them.

## Iteration 9: basic Settings

Settings should be an opaque screen in the existing in-app navigation state, with license presentation as a local child destination or modal. Returning to Drawer must retain the established Drawer state.

Use [`RoleManager`](https://developer.android.com/reference/android/app/role/RoleManager) to determine whether Avenor holds the Home role, and use the system Home settings destination for management. Re-query on foreground resume; do not cache system role state as durable application data. OEM destination behavior remains a physical-device finding.

Bundle the root `LICENSE` content locally and add a validation check that prevents the displayed copy from drifting. Open `https://github.com/max-archer-m/avenor-launcher` with an implicit HTTPS view intent; no `INTERNET` permission is required, and a missing handler must be handled locally. Read the installed package's version name and long version code so the display describes the running artifact. The planned `1.1.0` identifiers must be applied before Iteration 9 acceptance, with the final version code still following the next-unused rule.

## Iteration 10: double-tap lock

Implement a dedicated, purpose-limited [`AccessibilityService`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService.html) declared with `android.permission.BIND_ACCESSIBILITY_SERVICE` and minimal metadata. It must not request window-content retrieval, gesture performance, broad event observation, or unrelated global actions. Its event callback should perform no product work. Enabled state should be derived from the system's enabled-service list for the exact component and refreshed on resume; enabled and currently connected are distinct states.

The application-to-service boundary should expose only a lock request. A live service instance must confirm that the lock-screen global action is available, then call `performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)` and report false or unavailable results without crashing. Absence, revocation, disconnection, or OEM refusal must fail closed.

Attach double-tap recognition only to the product-defined blank basic-information area. Do not place a parent detector above time, date, favorites, edit controls, or navigation regions, because doing so can delay or steal their gestures. Edit mode disables the lock gesture, and a navigation drag that crosses its threshold takes precedence. Use platform-configured double-tap timing rather than custom timing constants.

The exact local Privacy statement and prominent disclosure are now author-approved in the current [Privacy product contract](../../product/features/privacy.md). They must be bundled as English and Simplified Chinese resources with English fallback, remain offline, and be rendered from one maintained source per locale so the Settings Privacy sheet, explanation flow, and disclosure cannot drift into conflicting copies. Privacy viewing and disclosure continuation remain separate states; neither is persisted as consent, and only `Agree and continue` may initiate the enable-oriented system handoff.

The Privacy contact uses the same narrow implicit HTTPS adapter as the repository entry but has its own fixed Issues URL, localized failure text, and sheet-preservation behavior. This adds no `INTERNET` permission and must not preflight network state.

Android's accessibility guidance frames these services as disability assistance, so local technical feasibility does not establish eligibility for public-store distribution. Any later store release requires renewed platform-policy and, where appropriate, specialist review. Iteration 10 may first perform focused technical validation of the minimum service declaration, exact enabled-state detection, lock-screen operation, fail-closed behavior, and data boundary. After those boundaries are supported by evidence and before the service is integrated into the version mainline, an ADR must record the service's sole purpose, permission and manifest boundary, explicitly prohibited behavior, fail-closed rules, relationship to Privacy and prominent disclosure, and the requirement to re-review any future expansion or store distribution. Do not create an empty ADR before the evidence exists. If the evidence requires broader permission, data processing, background behavior, or product scope, stop and obtain author direction instead of creating or integrating that expanded design.

## Iteration 11: presentation and upgrade closure

Replace the shared marquee implementation with ordinary one-line `Text` using end ellipsis and no soft wrapping. Remove the associated selection, measurement, animation, and orchestration state from Home, Drawer, and the application root rather than leaving a dormant marquee path.

Upgrade validation must start from an accepted `1.0.0` installation signed by the same author-local identity, populate schema-1 favorites, install the traceable `1.1.0` candidate in place, and verify preserved identities and order before exercising reorder. The closure evidence must identify the actual version code, source commit, device and skipped checks; it must not expose signing material.

Before version closure, development must inspect the current merged manifest and backup resources, including `android:allowBackup`, `android:dataExtractionRules`, and any legacy or equivalent configuration applicable to the supported API range. The resulting evidence must establish that the project-owned favorite persistence file is excluded from Android cloud backup and device-transfer backup. The current state is not assessed as passing here, and the exact configuration remains a development decision based on the actual project.

## Validation strategy

Implementation should add focused automated checks for favorite permutation validation and write failure, shortcut mapping and invocation parameters, Home-role state mapping, accessibility component-state mapping, and lock-command failure paths. Compose UI checks are most valuable for edit-mode gesture ownership, action-sheet shortcut presence, Settings navigation, separate Privacy and disclosure flows, cancellation paths, locale fallback, and static ellipsis semantics. A focused content check should compare the bundled Privacy/disclosure resources with the approved product copy without assuming that rendered typography or line wrapping is identical.

One author-designated primary physical device remains the required acceptance environment. Record its evidence for process recreation, upgrade preservation, edge auto-scroll, haptics, shortcut invocation from both surfaces, system-settings return, service enable/revoke/reconnect, eligible and excluded double-tap regions, and unsuccessful lock execution. API 31, one additional API 36 or API 37 physical device, and additional OEM/profile coverage are recommended evidence. If not performed, record them as `Unknown`, `Not run`, or `Unavailable`; their absence alone does not block this author daily-use baseline. If performed, an included-path failure must be resolved or returned for author disposition and cannot be ignored because the check was recommended.

No build or device result is claimed by this assessment.

## Risks, gates, and decisions

| Item | Assessment | Required handling |
| --- | --- | --- |
| Favorite reorder corruption | Low if schema 1 and atomic whole-list replacement are retained | Validate permutations and preserve read-failure gates |
| Shortcut profile mismatch | Medium | Carry exact activity/package/user identity through query and invocation |
| Settings OEM handoff | Medium | Re-query on resume and obtain primary-device evidence |
| Accessibility capability and policy | High | Minimal service, fail-closed adapter, approved disclosure, device evidence, later distribution review |
| Gesture conflict | Medium | Scope the detector to eligible blank space and test edit/navigation precedence |
| Upgrade/signing continuity | Medium | Use the same private author-local signing identity and perform in-place upgrade evidence |
| Android backup exclusion | Unknown until inspected | Inspect manifest and applicable backup rules; prove favorite persistence is excluded from cloud backup and device transfer |
| Architecture growth in the root composable | Medium | Add narrow state holders/adapters; do not perform a broad rewrite |

### Remaining gates and author-direction boundary

The author has approved the exact local Privacy statement and prominent disclosure text referenced by the product documents. No further product-copy confirmation currently blocks Iteration 10. Enabled acceptance still requires technical confirmation of the minimum service declaration, exact resource and presentation alignment, supported system action and state detection, fail-closed and data-boundary behavior, and physical-device behavior. Focused validation may precede the ADR; mainline integration may not. The evidence-supported ADR described above is an integration gate, not an unresolved product-copy decision.

Android backup exclusion remains an unperformed Iteration 11 and version-closure gate owned by development. No other unresolved item currently blocks technical implementation of Iterations 7-9 or 11. Raising `minSdk`, changing identity or persistence format, broadening accessibility capability, changing the declared data handling, or changing the selected product acceptance boundary would require separate author direction and a Privacy review where applicable.

## Recommended delivery order

The documented numeric order remains technically sound. Iteration 7 proves persistence mutation before other additions; Iteration 8 isolates launcher-host platform behavior; Iteration 9 establishes Settings and system-state refresh; Iteration 10 builds on that surface for authorization; and Iteration 11 removes obsolete presentation machinery and closes upgrade evidence. Each iteration should remain independently reviewable, with Iteration 10 held at its remaining technical and implementation-alignment gates.
