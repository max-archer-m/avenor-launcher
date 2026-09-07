# Avenor Launcher 1.5.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This record plans behavior selected from product-contract baseline `48d5bd470c84d222b6e89e128f438da1f25e595b`. Status and evidence do not authorize implementation, a version change, commit, push, tag, artifact movement, publication, or release.

## Version intent

`1.5.0` improves the ordinary Drawer after the separately planned `1.4.0` Home-module delivery. The selected result lets the author find applications locally by displayed name, reach Settings through the revised ordinary navigation, and choose a durable readable Drawer presentation without changing application ordering or the Home module model.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

The planned application identity remains `com.avenor.launcher` with `versionName` `1.5.0`. A candidate `versionCode` is provisional and must be selected from the next unused value only when a traceable APK is produced; planning does not reserve a value or override intervening artifact allocations.

## Product references

- [Product overview](../../../overview.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Drawer behavior](../../product/surfaces/drawer.md) and [Drawer presentation](../../product/presentation/drawer.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Navigation](../../product/navigation.md)
- [Privacy and data handling](../../product/features/privacy.md)
- [Validation guide](../../validation.md)

## Included scope and user journey

In ordinary Drawer, the author can search the reliable local inventory by displayed application name, open Settings from the final Settings row, and configure application size, arrangement, section anchors, and Transparent or Frosted-glass background through the revised stacked display-settings panel (title line plus content line, application-size block last). The accepted Home module and favorite state remain intact and Drawer action sheets preserve their source-specific boundary.

## Exclusions

- Home module creation, styling, ordering, movement, or adoption, which belong to the separately planned `1.4.0` delivery.
- Drawer shortcut ranking, package-name or pinyin search, fuzzy matching, or a relevance-based second application order.
- Theme customization outside Drawer, wallpaper sampling, or user-authored visual values.
- Third-party License presentation until its separate inventory and acceptance conditions are satisfied.
- Formal release artifact, public distribution, tag, milestone, or GitHub Release.

## Technical approach and risks

Development owns implementation details. Delivery must preserve reliable inventory identity, existing order, local-only processing, versioned atomic persistence, backup exclusion, current permission boundaries, and the accepted Home state. Any consequential persistence, rendering, inventory, or navigation decision requires author review and an ADR when applicable.

Primary risks are locale-dependent search behavior, stale result identity, Back/IME ambiguity, display-setting save races, loss of visible position after geometry changes, platform blur fallback, and regression of Home–Drawer–Settings navigation.

## Included iterations

| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [Iteration 26: Drawer Search and Ordinary Navigation](iteration-26-drawer-search-and-ordinary-navigation.md) | `Completed` | 2026-09-04 | The author reported basic acceptance of the complete implemented scope after incremental device validation and final gap review. |
| [Iteration 27: Drawer Display Settings](iteration-27-drawer-display-settings.md) | `Completed` | 2026-09-06 | The author explicitly accepted the complete iteration result and directed local completion; recorded validation gaps and background optimization remain non-blocking follow-up. |
| [Iteration 28: Upgrade, Regression, and Version Closure](iteration-28-upgrade-regression-and-version-closure.md) | `Completed` | 2026-09-07 | The author accepted the revised style settings panel layout after build/package and primary-device upgrade acceptance and directed local completion without a tag or candidate artifact; the contract records the 2026-09-06 layout amendment and the 2026-09-07 1.5.0 scope boundary. |

## Iteration evidence and results

### Iteration 26

[Contract](iteration-26-drawer-search-and-ordinary-navigation.md). Production implementation was authorized on 2026-09-04 from integrated mainline commit `9bdd79c69013379dc3a7f964388bd012deaf2907`, which contains the accepted product-contract baseline. Status is `Completed` after the author reported basic acceptance of the complete implemented iteration scope.

The completed implementation establishes the fixed ordinary top app bar and transient search mode without exposing the Iteration 27 display-settings entry. Displayed-name filtering preserves the existing section and application order, uses the ordering-compatible Latin-to-ASCII normalization without Han-to-Latin or pinyin matching, emphasizes every non-overlapping displayed match, hides Settings during search, and supplies clear, cancel, Back, no-result, live-inventory reprojection, and ordinary-position restoration paths including entry from the Settings section. Focused test sources cover normalization, match ranges, search lifecycle, latest-inventory projection, non-blocking launch-failure refresh, and ordinary restoration. The author reported the resulting search and ordinary-navigation behavior basically accepted after the final review corrections.

The author reported basic acceptance of the source-specific action-sheet split, system-application-information round trip, revised `40dp` section-anchor presentation, and Home-only uninstall journey after its platform Intent correction. Drawer omits the complete Home Launcher-action region and terminal shortcut divider; Home retains its applicable actions, while ordinary current-user non-system applications may hand uninstall to the system confirmation surface. Work-profile, cloned, system, and updated-system identities remain conservatively excluded when exact removal cannot be guaranteed. The manifest now declares the platform-required `REQUEST_DELETE_PACKAGES` capability; no runtime permission prompt or direct package deletion is introduced. Final review removed Drawer action-sheet dependence on favorite-storage readability, restored the Settings position across search, and aligned Latin normalization with ordering; the author then reported these corrections basically accepted.

Agent `git diff --check` and affected manifest/resource XML parsing passed after the completed changes. Gradle and instrumentation were not run by the agent; author-reported observations are limited to the accepted scope stated above and do not imply unreported commands or broader compatibility coverage. No known included-scope crash, wrong-identity launch, destructive reconciliation, or navigation dead end remains open at completion.

### Iteration 27

[Contract](iteration-27-drawer-display-settings.md). Status is `Completed` following explicit author acceptance on 2026-09-06. Commit `7bc9ae4764d87edf579dd0a30b14349e0bf0de8b` established persistent display geometry. Subsequent implementation adds section-anchor controls and position restoration, experimental background rendering and capability fallback, selection-animation isolation, accessibility improvements, and publication of committed settings before the cancellable IO return boundary.

The author reported basic acceptance of incremental panel, anchor, and background changes, including the corrected IconButton import. These reports do not identify a complete device matrix or validate the later storage and lifecycle corrections. Final local review added file-scoped serialization across recreated store instances, prevented a completed save from restoring an obsolete scroll position, cleared ordinary pending restoration when entering search, and hardened blur-listener cleanup. The search normalizer now explicitly unwraps its initialized thread-local value. Tests use isolated files rather than deleting real settings.

Test sources cover settings interaction, anchors, backgrounds, cancellation at commit, failed-write retry, and concurrent access during recreation. Five integration scenarios mount `AvenorApp` with the real settings store and controlled file-write failures/delays: outside dismissal with success or failure, Back during saving, failure while the panel stays open, and Activity recreation during a pending write. They check full-state persistence, disabled controls, rollback/retry, failure-notification dispatch count, and recreation with a fresh store. Notification dispatch assertions do not establish actual system Toast presentation. A regression scenario checks that successful save completion does not undo subsequent scrolling. Full-source checking also corrected invalid test imports and a test variable-scope error.

Agent validation on 2026-09-05:

| Check | Result and boundary |
| --- | --- |
| Direct Kotlin/Compose source compilation | `Passed`: all 50 main/debug Kotlin sources and all 33 instrumentation Kotlin sources compiled with cached Kotlin `2.3.10`, its Compose compiler plugin, Android `37.0` SDK classes, cached dependencies, and existing generated resource/BuildConfig classes. The final main-source pass had no warnings; test sources retain existing Compose test-rule deprecation advisories. This is not a Gradle build, resource regeneration, APK, lint, or device-test result. |
| Gradle compile/lint entry | `Unavailable`: `gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin :app:lintDebug --offline --console=plain` exited before task execution with `Unable to establish loopback connection` on Windows PowerShell/JDK 17. Temporary single-use-daemon, JBR, and socket-setting attempts did not resolve the host failure; project configuration was not changed. |
| Static resource and document checks | `Passed`: diff whitespace, affected resource/manifest XML parsing, resource-name uniqueness, complete English/Chinese string-name parity, and affected local Markdown links. |
| Instrumentation and device journeys | `Not run` by the agent. Source compilation does not establish execution of the new failure, Back, or Activity-recreation scenarios. Process recreation/restart, system Toast presentation, and complete device regression remain unverified beyond the author's earlier basic observations. |
| Background parameter acceptance | The author accepted the current parameters on 2026-09-06 and explicitly made further calibration/optimization non-blocking. The dual-device matrix remains `Unknown`, not passed; the iteration contract records the amended acceptance boundary. |

On 2026-09-06, the existing pure geometry test `DrawerSectionAnchorTest.anchorFollowsItsSectionPinsAndLeavesBeforeTheNextSection` ran directly through JUnit on the host JVM: one test, zero failures. This covers section following, pinning, and push-out arithmetic only, not Compose layout or device rendering. `adb devices -l` listed no connected devices, and no emulator executable or configured AVD was available; device execution could not proceed in this environment.

The author explicitly accepted the complete iteration result on 2026-09-06 and directed that completion be recorded and amended into the current local commit. The recorded unperformed checks remain gaps, not passes; accepted background optimization is non-blocking, and the below-name multi-selection layout limitation remains deferred by author direction. This author-directed local completion precedes shared-history synchronization: no push is included in this authorization, and remote synchronization remains pending.

The author subsequently reported basic build/package acceptance on 2026-09-06. No exact command, artifact identity, device matrix, or instrumentation results were supplied, so this remains attributed author evidence rather than an agent-run build result. The author also clarified that temporary style-panel locks must preserve normal colors; the shared controls and bilingual contracts now distinguish temporary activation locks from count-boundary dimming. The earlier direct compilation predates that final visual adjustment. The author directed that the cramped leading selection region in below-name multi-column mode be left unchanged for now; this is a known layout limitation awaiting a separate product decision, not a resolved defect or a claim of full combination coverage.

### Iteration 28

[Contract](iteration-28-upgrade-regression-and-version-closure.md). On 2026-09-06 the author directed that the revised shared style settings panel layout be delivered and validated within this iteration; the contract records the amendment. On 2026-09-07 the author confirmed the 1.5.0 scope boundary, deferring the local backup and restore contract and the hidden-name and vertical-list-geometry presentation contract to the next version; the contract records that amendment.

The author authorized production implementation on 2026-09-07. The implementation restructures the shared style settings panel into title-line and content-line blocks (32dp title lines, 48dp content lines, a 56dp application-size content line placed last), adopts 40dp interaction targets with the compact 140dp x 40dp two-option selector and 28dp stepper controls, and adds 16dp end spacing after each application-size option label. The implementation, the 1.5.0 identifier update (`versionName` `1.5.0`, `versionCode` 6), and this delivery record are included in the same delivery commit.

Author-reported evidence on 2026-09-07: the style settings panel changes package successfully and received basic acceptance; the 1.5.0 identifiers are in place; and the direct upgrade from the accepted prior version basically passed on the author-designated primary device. The author directed local completion without producing a tag or a formal candidate APK artifact; the traceable-candidate requirement is therefore recorded as author-directed out of scope for this closure, and version completion and tag disposition remain the author's separate decision. Source-level contract reconciliation for Iterations 26–27 found no unresolved material mismatch in the selected scope. Unperformed checks, including the broader device/API/OEM matrix and journey regression beyond the reported upgrade acceptance, remain recorded gaps, not passes.

## Dependencies and sequence

Iteration 26 establishes the ordinary Drawer top app bar, search, final Settings row, and source-specific action-sheet behavior required before Iteration 27 adds display settings. Iteration 28 depends on accepted results from Iterations 26–27 and an accepted prior-version baseline. These dependencies do not bind work to a branch, terminal, contributor, forecast date, or permanent task line.

## Validation

The mandatory version environment is one author-designated primary physical device. Completion requires a traceable installable candidate, direct upgrade from the accepted prior-version baseline, preservation of readable Home and unrelated configuration, author acceptance of the complete selected Drawer journey, and no known included-path crash, ANR, destructive configuration error, wrong identity launch, or navigation dead end.

Focused automated checks and additional API 31, API 36/37, OEM, profile, clone, locale, font-scale, navigation-mode, blur-availability, process-recreation, inventory-change, and persistence-failure scenarios are recommended unless explicitly promoted. Executed failures on included behavior must be resolved or dispositioned; unperformed checks remain `Not run`, `Unknown`, or `Unavailable`.

## Artifact and release requirements

The accepted APK must retain `com.avenor.launcher`, use accepted `1.5.0` identifiers, be traceable to one source commit and signing category, and support the required upgrade journey. The author-local private signing identity remains required for in-place update continuity. APK retention, tag, publication, and distribution remain separately authorized.

## Known limitations and legacy issues

- Broader device, API, OEM, profile, clone, accessibility, blur, and performance coverage remains unknown until performed.
- Frosted glass may use its contracted more-opaque fallback when platform cross-window blur is unavailable.
- Minimum acceptable performance, power, memory, and startup thresholds remain undecided and are not version gates unless explicitly promoted.
- Search remains displayed-name contiguous matching in existing Drawer order; broader discovery is outside this version.

## Completion criteria

- Iterations 26–28 are `Completed` with separately recorded evidence and author acceptance.
- Product contracts, implementation, tests, and delivery evidence have no unresolved material mismatch in the selected scope.
- The mandatory physical-device upgrade and complete selected Drawer journey are accepted without loss of accepted Home state.
- Final identifiers, allocated `versionCode`, source commit, signing category, APK identity, known gaps, and tag disposition are recorded accurately.

## Completion result

Iterations 26–28 are `Completed` with the implementation and author-reported acceptance recorded above. The author accepted the revised style settings panel layout and the 1.5.0 identifier update, reported basic upgrade acceptance on the primary device, and directed local completion without a tag or candidate artifact. This record does not authorize push, artifact movement, publication, or release; those actions remain separately authorized.
