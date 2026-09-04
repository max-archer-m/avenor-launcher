# Avenor Launcher 1.4.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This record selects behavior from initial product-contract baseline `7cae837dafb188896dd24bd43aae58022c81fe11`, with the authorized Home refinements and closure alignment at `78d2aab18066c2d9b57b56581e0ab8c17402d104` recorded in Iterations 24-25. Status and evidence do not authorize implementation, a version change, commit, push, tag, artifact movement, publication, or release.

## Version intent

`1.4.0` replaces the completed `1.3.0` fixed vertical-list and favorite-bar composition with the current ordered favorite-module model. The selected result is a complete Home editing loop: the author can build, style, order, and maintain one direct sequence of vertical modules and ribbons without requiring the separately planned Drawer search or display-setting work.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

The configured application identity is `com.avenor.launcher` with `versionName` `1.4.0` and candidate `versionCode` `5`. Code `5` is not recorded as allocated until a traceable APK uses it under release governance; configuration alone does not establish an artifact or version completion.

## Product references

- [Product overview](../../../overview.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Home behavior](../../product/surfaces/home.md) and [Home presentation](../../product/presentation/home.md)
- [Drawer behavior](../../product/surfaces/drawer.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Navigation](../../product/navigation.md)
- [Privacy and data handling](../../product/features/privacy.md)
- [Validation guide](../../validation.md)

## Included scope and user journey

After adopting the new Home model, the author can create, use, style, and order any number of full-width vertical favorite modules and horizontal ribbons in one vertically scrolling main list. The author can remove and move applications within or across modules with failure-safe persistence and can add applications through destination-bound Drawer multi-selection.

Existing application inventory, launch, profile identity, Drawer, Settings, Privacy, double-tap lock, offline, and least-privilege behavior remain compatible. Drawer search and configurable Drawer presentation remain valid product-contract directions but are deliberately deferred from this version.

## Exclusions

- Migration of `1.3.0` favorites into the new Home model; the accepted product contract selects a one-time favorite reset while retaining unrelated configuration.
- Ordinary Drawer search, revised ordinary navigation, Drawer display settings, or unrelated Drawer changes.
- Folder-like grouping, Home paging, themes, widgets, weather, automatic ordering, recommendations, or another capability layer.
- Third-party License presentation until its separate inventory and acceptance conditions are satisfied.
- Formal release artifact, public distribution, tag, milestone, or GitHub Release.

## Technical approach and risks

Development owns implementation details. Delivery must preserve stable launchable identity, versioned atomic persistence, unreadable-data fail-closed behavior, backup exclusion, and current permission boundaries. The Home adoption requires an explicit new persisted model or compatible replacement boundary; any consequential persistence or state-ownership choice requires author review and an ADR when applicable.

Primary risks are destructive reset scope, stale module or selection identity, gesture arbitration across the main list and ribbons, rollback that overwrites newer inventory facts, and regression of the accepted Home–Drawer–Settings journey.

## Included iterations

| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [Iteration 22: Ordered Favorite Module Foundation](iteration-22-ordered-favorite-module-foundation.md) | `Completed` | 2026-09-01 | The implementation, focused tests, static checks, author-reported compilation, and functional acceptance satisfy the iteration boundary. |
| [Iteration 23: Module Style and Ordering](iteration-23-module-style-and-ordering.md) | `Completed` | 2026-09-02 | The implementation, focused persistence-test sources, static checks, and author-reported functional acceptance satisfy the iteration boundary. |
| [Iteration 24: Application Editing and Cross-Module Movement](iteration-24-application-editing-and-cross-module-movement.md) | `Completed` | 2026-09-03 | The author accepted the implementation through the amended exit-transition slice and explicitly confirmed iteration completion after the engineering closeout review. Recommended unperformed checks remain recorded below. |
| [Iteration 25: Upgrade, Regression, and Version Closure](iteration-25-upgrade-regression-and-version-closure.md) | `In Progress` | 2026-09-04 | Baseline/test preparation and candidate configuration are complete. The primary device and installed baseline are confirmed; a newly packaged `1.4.0(5)` APK is still required before the mandatory upgrade. |

## Iteration evidence and results

### Iteration 22

[Contract](iteration-22-ordered-favorite-module-foundation.md). The implementation establishes the ordered-module store, one-time readable-legacy reset, initial vertical-module creation through Drawer, ordered Home rendering and launch, and focused store/UI tests. The final backup-source and retry-loading fixes preserve the required failure states, and the runtime Privacy text now reflects the adopted storage boundary. The author reported successful compilation and basic functional acceptance, including acceptance after the final fixes. Agent static checks passed for `git diff --check` and resource XML parsing; Gradle and instrumentation were not run by the agent. Status is `Completed`. The consequential persistence direction remains subject to the repository rule that an `Active` ADR is created only after its accepted implementation is committed and synchronized.

### Iteration 23

[Contract](iteration-23-module-style-and-ordering.md). The implementation on accepted product-contract baseline `7cae837dafb188896dd24bd43aae58022c81fe11` now provides position-resolved add-favorite entries for both module types and existing modules; the shared inline style panel with content-driven bounded height; complete-module selection; durable vertical-module style changes with serialization and rollback; and insertion-only whole-module movement with a frozen preview, source removal, boundary feedback, edge auto-scroll, atomic order persistence, cancellation, interruption cleanup, and localized failure recovery. Focused ordered-store test sources cover exact module-order persistence and invalid order rejection. The author reported that the current functional behavior was basically accepted after the final gesture-lifecycle fix. Agent static checks passed for `git diff --check` and affected resource XML parsing. Gradle and instrumentation were not run by the agent; broader recommended device, interruption, failure-injection, font-scale, process-recreation, and compatibility scenarios remain `Not run` or `Unknown` and are not promoted iteration blockers. Status is `Completed` by author decision on 2026-09-02.

### Iteration 24

[Contract](iteration-24-application-editing-and-cross-module-movement.md). Implementation began under author authorization on 2026-09-03 against product-contract baseline `7cae837dafb188896dd24bd43aae58022c81fe11`. The author-authorized amendment of the same date selects baseline `78d2aab18066c2d9b57b56581e0ab8c17402d104` for movement feedback, content transitions, and add-entry surface refinements; the contract records the previous boundary and affected acceptance requirements. Status is `Completed` by author decision on 2026-09-03 following basic acceptance of the implemented slices and the engineering closeout review.

#### Pre-amendment implementation and evidence

The following records describe implementation through `b42e02b88aafa3158e4d00d60a6eb5c554126b4a` under the previous product baseline. They retain their reported evidence scope and do not establish implementation or acceptance of the amended requirements.

- Automatic empty-module cleanup now omits an emptied module before constructing a non-empty module value and saves the complete result through the existing serialized atomic store. The author reports compilation and basic acceptance of this change. It adds no module-level deletion control.
- Application removal now has the contracted separate control in collapsed-panel editing for vertical modules and ribbons. Normal Home action-sheet removal shares the transient latest-removal Undo lifecycle. Restoration preserves the original module position, type, and style without resurrecting unrelated identities removed by reliable inventory cleanup. Favorite writes are guarded against concurrent user mutations; exit, interruption, other successful mutations, and confirmed disappearance invalidate eligible Undo state. No persistence schema or migration changes are introduced. The author reports compilation and basic acceptance of these additions.
- Same-module application movement now uses whole-item long-press recognition outside the remove control, one source placeholder, a source-style preview, and a single insertion boundary for single-column, multi-column, and ribbon arrangements. It neither exchanges items nor performs a provisional full-list reorder. Invalid and no-change releases do not save. Changed releases revalidate the source order under the existing store mutex, preserve other modules, and use the App-owned exclusive save lifecycle with localized failure feedback. Back, edit-mode exit, and invalidated source state cancel unpublished movement. The author reports compilation and basic acceptance of these additions.
- Cross-module movement now resolves each destination using its own cell geometry and resolves bounded inter-module gaps to one boundary. One atomic save validates both source and destination orders, moves the exact identity, and omits an emptied source without changing unrelated modules or restoring stale inventory. The preview retains source presentation until release; viewport-driven source disposal does not transfer pointer ownership. Application edge auto-scroll uses the shared band, residence-delay, speed, and directional-feedback tokens; only one eligible axis runs, and owner changes restart the delay. The author reports basic acceptance of these additions.
- Drop-to-create now recognizes only the two main-list add-favorite entries. Hovering outlines the selected entry without an insertion line or layout change. Release creates the selected module type with default presentation as the final module and removes the source identity, including an emptied source, in one existing atomic transaction. Hover, cancellation, invalid destinations, stale source state, and failed saves do not publish a provisional module. The author reports basic functional acceptance of the current implementation, including these additions; this does not establish execution evidence for the full interaction and recovery matrix.
- Focused instrumentation test sources cover storage cleanup, exact profile identity, persisted reloads, removal replacement and failure, restoration after inventory cleanup, Undo invalidation and failure, interruption during saving, remove-control geometry and interaction separation, and normal Home action-sheet removal with Undo. Agent diff, resource XML, and affected local Markdown-link checks passed. Agent Gradle and instrumentation execution remain `Not run`; test sources are not execution evidence.
- Additional test sources cover same-module geometry, wrapped-row ties, partial rows, trailing-entry exclusion, no-change boundaries, exclusive pointer feedback, persistence, failed saves, and stale-source rejection after inventory changes. They have not been executed by the agent.
- Further test sources cover all four source/destination module-type combinations, empty-source deletion, failed cross-module saves, destination inventory changes, source virtualization, inter-module gap ties, edge ownership, band capping, and proximity-independent residence keys. Agent execution remains `Not run`.
- Drop-to-create test sources cover both source and destination types, final-application movement, default style, persisted reload, failed saves and Undo eligibility, stale source rejection, colliding IDs, hover/insertion exclusivity, and disposed targets. Agent execution remains `Not run`.
- Technical decision assessment: drop-to-create adds a destination to the existing App-owned favorite editor and ordered-store transaction, not a second persistence owner or schema. No additional ADR is selected specifically for this route. The final iteration-wide interruption, recovery, regression, and decision review remains incomplete.

#### Amended-scope follow-up

The implementation line contains amended baseline `78d2aab18066c2d9b57b56581e0ab8c17402d104`. The first amendment slice implements ribbon gap-centered insertion feedback and the revised add-entry surfaces. Equivalent ribbon hit regions now resolve to one gap axis, including the final application-to-add-entry gap; feedback retains actual neighbor geometry and clips the complete stroke to the visible region. Vertical trailing entries add no fill or border, ribbons retain their existing entry treatment, and main-list creation entries use one shared edit-region background layer. Add-entry components, item-dimension mappings, and pure insertion geometry have been separated from the screen and movement coordinator without changing persistence ownership.

New test sources cover unequal-width neighbors, equivalent hit regions, first/last boundaries, changing visible geometry, missing neighbors, viewport clipping, inter-module gap resolution, and the ordinary/disabled entry surfaces. Agent diff and affected local Markdown-link checks passed; Gradle and instrumentation remain `Not run`. Author compilation and device acceptance of this slice remain unreported.

The next bounded slice removes the application-source placeholder. Vertical and ribbon content now omit the active identity through the same transient order projection used by insertion geometry. Remaining entries close the gap immediately; a singleton source retains only its existing add entry and exposes no synthetic insertion boundary. The saved module remains non-empty and unchanged until a valid atomic mutation. Same-module releases insert into the reduced order exactly once and compare against the original saved order to detect no-change completion. Cancelling restores the ordinary layout without a favorite removal or Undo snapshot. Focused test sources cover reduced boundaries, row wrapping, singleton sources, cancellation and no-change restoration, and existing cross-module/drop-to-create journeys; persistence tests use the same reduced-boundary convention. Agent diff and local Markdown-link checks passed; agent compilation and instrumentation were not run. The author reports basic acceptance of source omission.

Pending-save preview handoff is now implemented as a separate completion phase. Release freezes the request and preview, clears candidate feedback, and stops edge auto-scroll. The App-owned save acknowledges completion after its reliable store state reaches Home's presentation input; the preview and source omission then clear together. A completed source module may disappear without cancelling the waiting preview. Leaving the edit journey clears transient feedback without cancelling the App-owned write, and request-identity checks keep late completions from clearing a newer gesture. Focused controller and UI test sources cover frozen release geometry, pending feedback, same-module and cross-module handoff, module creation, failure restoration, navigation, and late completion. Agent diff and local Markdown-link checks passed; Gradle and instrumentation remain `Not run`. The author reports basic acceptance of this handoff slice.

The subsequent placement-transition slice uses the shared short-property duration for ribbon cells, ordered modules, and their trailing creation entries. Vertical applications now retain keyed composition across rows in a module-local lookahead grid, with animated cell bounds and container height; partial rows retain their column widths. Geometry continues to come from the real placed content, not cached destination coordinates. Placement and size transitions do not retain removed application nodes or animate the finger preview, and do not gate persistence or Undo. New focused UI test sources cover cross-row identity retention, intermediate hit coordinates, interrupted retargeting, partial rows, initial/return layout, parent translation, and disabled system animations. Agent Gradle and instrumentation execution remain `Not run`. The author reports basic acceptance of this placement slice, with occasional visible frame drops.

An implementation review identified unnecessary high-frequency state observation: the drag-phase getter read the changing full session, and preview composition read pointer/candidate geometry. The optimization separates stable drag ownership and frozen preview content from moving coordinates, reads preview position during placement and feedback geometry during drawing, and filters unchanged pointer values for edge-scroll evaluation. It preserves live hit geometry, animation duration, save ownership, and recovery. Additional test sources cover phase-reader invalidation, live candidate updates, preview lifetime, and preview movement without phase-reader recomposition. Static diff and affected local Markdown-link checks passed; agent Gradle, instrumentation, and device frame-time profiling remain `Not run`. The author reports basic acceptance of the optimization; no quantified frame-time improvement is established.

An entry-transition slice now distinguishes durable membership changes from layout appearance. Adding or restoring applications in an existing vertical module or ribbon fades only those applications; creating or restoring an entire module fades its parent once. Existing identities moved or reordered, including drop-to-create, receive no entry fade. Entry eligibility expires after the change's initial layout opportunity, and off-viewport or later-composed entries do not replay it on scroll or return. Fades use the shared short-property duration, preserve an existing fade across unrelated mutations, suppress a second child fade while a parent is entering, and remove their opacity layer on completion. Saving and Undo eligibility are unchanged. Six policy and four UI test sources cover membership classification, claim lifetime, parent/child exclusion, visible entry opacity, return/scroll, and disabled animations. Static diff and local Markdown-link checks passed; agent Gradle and instrumentation remain `Not run`. The author reports basic acceptance of this entry-transition slice.

The exit-transition slice now keeps visual-only native display-list remnants, without copying bitmaps or retaining outgoing application layout, input, or semantic nodes. Durable membership changes select either the removed application or the disappearing parent module; movement and transient drag-source omission do not trigger exit fades. Real content leaves layout immediately while placement transitions close the gap. Remnants use the shared short-property duration and are clipped to the main-list viewport. Quick Undo replaces the remnant and provides its current opacity to the returning entry; a subsequent whole-module removal replaces earlier child remnants. Capture resources are allocated on visible drawing and retained through parent/child exit lifetimes, then released after disposal and transition completion. Unreadable state or owner disposal clears remnants; disabled system animations bypass exit retention. One additional policy test and six UI test sources cover removal classification, non-retained application nodes, whole-module removal, consecutive removal, quick Undo, moved identities, and disabled animations. Static diff and affected local Markdown-link checks passed; agent Gradle, instrumentation, and frame-time profiling remain `Not run`. The author reports basic acceptance of this exit slice; the exact build command and device identity are unreported, and no measured performance claim is made.

#### Engineering closeout review

The final source review compared the amended contract with removal/Undo, same-module and cross-module movement, drop-to-create, current-geometry feedback, source omission, pending-save handoff, content transitions, and the corresponding test sources. No additional production-code change was identified. Three further UI test sources cover clearing an unfinished exit on unreadable state, releasing its graphics resources on owner disposal, and accepting another addition while the previous removal still fades. These are regression specifications, not executed results.

Technical decision assessment: drop-to-create still passes through `HomeFavoriteEditor` and the existing mutex-serialized ordered aggregate transaction; it creates no second persistence owner, schema, or migration. Movement and transition coordinators own only transient presentation and cannot write favorites or extend Undo. The display-list retention and component extractions are local rendering/maintenance choices with no new dependency, permission, or compatibility boundary. No additional ADR is required for Iteration 24. This assessment does not close the separate persistence-decision follow-up recorded for Iteration 22.

Static checks passed for staged and unstaged diffs, whitespace in the new transition test sources, and affected local Markdown links. The amended product baseline remains in the implementation ancestry. Agent Gradle, instrumentation, failure injection, and device profiling remain `Not run`; the complete multi-pointer, external-interruption, process-recreation, animation-time drag/release, and broader launch/navigation regression matrix remains `Unknown` beyond the author's reported basic acceptance. These recommended checks have not been promoted to mandatory iteration gates. No new known blocker was identified. On 2026-09-03, the author explicitly directed that Iteration 24 be marked `Completed` and its changes committed. Completion retains the stated evidence gaps and does not imply version completion or authorize push, integration, or Iteration 25 work.

### Iteration 25

[Contract](iteration-25-upgrade-regression-and-version-closure.md). Preparation began under author authorization on 2026-09-03 from integrated source `d8ada79f9b8a92a826edba17a340d7e0971274fc`. The contract now selects the accepted Home refinements at `78d2aab18066c2d9b57b56581e0ab8c17402d104`, already present in that ancestry. This changes neither the protected Iterations 22-24 contracts nor the upgrade/reset acceptance intent.

#### Baseline and test preparation

- Existing ordered-store tests cover readable schema-3 adoption, invalid legacy and ordered data, recoverable backups, persisted module/style/order round trips, and serialized mutations. The schema-3 fixture format was compared with the accepted `1.3.0` serializer.
- Two new `OrderedFavoriteUpgradeTest` cases specify that resetting mixed legacy list/bar favorites happens only once and that unreadable new-model data never falls back to readable legacy data or permits a write. They use isolated temporary files and do not clear application data. Their execution is `Not run`; they do not establish APK upgrade or Android settings-preservation evidence.
- Source inspection confirms `allowBackup=false` and whole-domain exclusions for cloud backup and device transfer. Packaged backup behavior remains to be checked against the candidate; the existing `BackupConfigurationTest` provides the automated entry point.
- Static diff, new-test whitespace, affected Markdown-link, and pinned-reference target checks passed. No production behavior, configured version identifier, signing material, installed package, or device data changed. Gradle and instrumentation were not run.

#### Candidate and validation readiness

| Area | Preparation and remaining evidence |
| --- | --- |
| Old baseline | Accepted `1.3.0(4)` source is `0bdb54a49530fa6a84e9c57a054447d3ba093525`; APK and debug-certificate digests are recorded in [1.3.0 delivery](../1.3.0/delivery.md#iteration-21). Original APK availability is unconfirmed. Any reconstruction must be identified separately and accepted as a limitation, not represented as the original APK. |
| Candidate identity | Configuration and packaged-identity test expectations now use `1.4.0(5)` under author authorization. Repository delivery records identify `4` as the latest allocated code and contain no allocation for `5`; no candidate APK has been reported or verified for this change. Record exact source, build stage, package, version, signing certificate, and available digest when built. |
| Signing and device | The author designated Samsung S23 Ultra as the primary physical device; its exact model identifier and Android/API version remain unreported for this iteration. Match the candidate certificate to the accepted baseline before installation. Do not assume release and debug signing are interchangeable. Reinstalling an old baseline or clearing current development data requires separate direction; do not use downgrade as the upgrade setup. |
| Mandatory upgrade | Start from actual old-model `1.3.0` state with representative lists/bars and noted unrelated settings/system authorization. Perform an in-place candidate update, verify the one-time empty Home reset, preserve unrelated configuration, then create new modules and restart to verify they are not reset again. A current development install already using ordered modules does not substitute for this journey. |
| Selected regression | Check module creation/addition, style/order, removal/Undo, every movement type, drop-to-create, auto-scroll, transitions and single-preview handoff, then existing Drawer, Settings, launch, double-tap-lock, Privacy, offline and profile paths. Record actual observations and included-path failures; do not add deferred Drawer features. |
| Supplemental checks | Clean install, failed reads/writes, process recreation, multi-pointer and lifecycle interruption, disabled animations, broader OEM/profile/API coverage, and profiling remain recommended. Historical Home instrumentation failures remain unresolved until rerun or explicitly dispositioned; they are not recorded as passing. |
| Closure | Record the exact candidate and accepted device journey, known gaps, and the author's version-completion and tag disposition. Required source/evidence synchronization and any commit, push, artifact movement, tag or publication remain separately controlled. |

#### Candidate configuration

The author authorized file changes and retained IDE compilation responsibility. The application configuration, `CandidateConfigurationTest`, and both development-guide counterparts now agree on `1.4.0(5)`; application ID, SDK levels, signing configuration, and reset behavior are unchanged. This change does not erase current ordered-module data: reset applies only to first adoption from readable legacy favorites, not to every version change.

The author selected Samsung S23 Ultra and requested reduced emphasis on compatibility testing. Broader compatibility coverage remains recommended and unperformed, not a new gate or an implied pass. The selected in-place upgrade/reset and unrelated-configuration preservation requirement is unchanged. Static diff and affected local Markdown-link checks passed; agent Gradle, APK generation, signing, installation, and device testing were not performed. Author IDE compilation and device results remain unreported.

The author subsequently reported basic acceptance of this configuration slice; the exact IDE command or task was not reported. Read-only preparation identified the connected primary device as Samsung `SM-S9180`, Android 16 / API 36. Package inspection showed that user 0 currently has `com.avenor.launcher` `1.3.0(4)` installed, with first-install time `2026-09-01 15:11:21` and last-update time `2026-09-03 11:32:44`; another reported user 96 installation is absent. This is a suitable installed-version starting identity, but its application state and pre-upgrade acceptance observations still need to be recorded before mutation.

The only locally found application APK was `app/build/intermediates/apk/debug/app-debug.apk`. Static artifact inspection identified it as stale `com.avenor.launcher` `1.3.0(4)`, SHA-256 `51725D8105081E80668FC51436C4362E73DE9CC36D8247A09B285367715ADCEE`, signed by debug certificate SHA-256 `E6786FC1914AAD390436C4F24661D81A2781492F45741CC77F96D0AD8B8C4E77`. Its certificate matches the accepted `1.3.0` debug identity, but its version proves it is not the configured `1.4.0(5)` candidate. It was not installed or treated as new evidence. A fresh IDE `Build APK(s)` result, or an equivalent explicitly reported package task, is required before candidate identity and in-place installation can be checked.

#### Development-candidate installation

The author reported completing the IDE packaging step. The generated debug APK at `app/build/outputs/apk/debug/app-debug.apk` was inspected as `com.avenor.launcher` `1.4.0(5)`, target SDK 36, SHA-256 `20735A68144E416562F54F06C587E4A37CE6A2602DBDBCF0634CD6D3DD54A1B2`, and debug certificate SHA-256 `E6786FC1914AAD390436C4F24661D81A2781492F45741CC77F96D0AD8B8C4E77`. The certificate matches the accepted `1.3.0` debug identity. The APK represents the current uncommitted working tree based on `d8ada79f9b8a92a826edba17a340d7e0971274fc`; it is a development-validation candidate, not yet a final source-commit-traceable candidate.

Agent-executed `adb install -r -t app/build/outputs/apk/debug/app-debug.apk` on the connected `SM-S9180` returned `Success`. Package inspection changed the installed identity from `1.3.0(4)` to `1.4.0(5)`, retained first-install time `2026-09-01 15:11:21`, and recorded last-update time `2026-09-04 10:02:31`. User 0 remains installed. Android still resolves the default Home activity to `com.avenor.launcher/.MainActivity`.

This was not the contracted legacy-model adoption journey. After installation, application-private inspection showed an existing `ordered_favorite_modules.bin` last modified `2026-09-03 20:30`, with SHA-256 `617FE2DAA121B84580DB3A55D19A433360988EEF887B0A424E9FD7A3276B2D6B`; no legacy `favorites.bin` exists. Therefore the installed `1.3.0(4)` starting package already used the ordered-module model, and its module data remained available across the version update as expected. This evidence supports signature continuity, in-place package update, default-Home continuity, and new-model data preservation only. It neither exercises nor passes the mandatory one-time reset from an accepted old-model `1.3.0` state. No uninstall, downgrade, or data clearing occurred.

The author reported basic acceptance of the requested installed-candidate journey: Home modules/style/order remained usable, application add/remove/Undo/movement was usable, Drawer/Settings/application launch operated, and restarting Avenor retained the resulting state. The exact actions, module combinations, offline/profile conditions, and interruption cases were not reported, so broader regression remains `Unknown` rather than passed.

Post-acceptance read-only inspection confirmed the packaged manifest retains `allowBackup=false` and the configured data-extraction rules, Avenor remains the default Home, and the candidate process is running. Its latest exit record is the expected `PACKAGE UPDATED` stop at installation; no candidate-period crash or ANR appears in the inspected exit history. Installed permissions retain `SET_ALARM`, the application-private dynamic-receiver permission, and the author-granted Samsung application-list permission for user 0.

Samsung automatically registered the updated package for Dual App user 96 during installation even though that user was not installed immediately beforehand. User 96 is now reported installed but stopped, with the Samsung application-list permission not granted. This unintended secondary installation has not been removed because removal is a separate destructive device action; it remains an open cleanup and acceptance item.

The author then attempted to remove the secondary installation manually. Read-only package checks established that this removed `com.avenor.launcher` globally rather than only for user 96: the package was absent for user 0, no package path existed, and Android resolved Home to the system resolver. The author authorized recovery. Agent-executed `adb install --user 0 -t app/build/outputs/apk/debug/app-debug.apk` returned `Success` and installed `1.4.0(5)` only for user 0 at `2026-09-04 10:12:19`; user 96 is now reported not installed. This was a clean reinstall, not another in-place upgrade. The previous Avenor application-private data and default-Home selection must be treated as lost or reset by the global uninstall; no claim of their preservation continues beyond that point. The author then selected Avenor as default Home and reported that clean empty favorites, creation of one module, persistence after restarting Avenor, Drawer, Settings, and application launch all operated normally. This supplies the recommended clean-install evidence at the reported scope; it does not restore the earlier private data or broaden compatibility coverage.

The author separately accepted the old-model APK upgrade result, reporting that the manual journey was performed during the initial development of this adoption behavior and is not considered an unresolved product issue. The exact APK digest, source revision, device state, time, operations, and retained output for that earlier check were not reported. This author acceptance satisfies the selected daily-use baseline's legacy-reset disposition while retaining those traceability limitations; it does not convert the new automated test sources to executed evidence or establish broader compatibility.

A final accepted candidate must be rebuilt from a committed source revision and retain the recorded identifier and certificate continuity. The clean reinstall requires selection of Avenor as default Home and a final basic clean-state journey before device acceptance can be restored. Status remains `In Progress`.

## Dependencies and sequence

Iterations 22 → 23 → 24 form the Home dependency chain. Iteration 25 depends on accepted results from Iterations 22–24. These dependencies do not bind work to a branch, terminal, contributor, forecast date, or permanent task line.

## Validation

The mandatory version environment is one author-designated primary physical device. Completion requires a traceable installable candidate, direct upgrade from the accepted `1.3.0` baseline, the contracted one-time favorite reset without loss of unrelated readable configuration, author acceptance of the complete selected Home journey, and no known included-path crash, ANR, destructive configuration error, wrong identity launch, or navigation dead end.

Focused automated checks and additional API 31, API 36/37, OEM, profile, clone, locale, font-scale, navigation-mode, process-recreation, gesture-interruption, and persistence-failure scenarios are recommended unless explicitly promoted. Executed failures on included behavior must be resolved or dispositioned; unperformed checks remain `Not run`, `Unknown`, or `Unavailable`.

## Artifact and release requirements

The accepted APK must retain `com.avenor.launcher`, use accepted `1.4.0` identifiers, be traceable to one source commit and signing category, and support the required upgrade journey. The author-local private signing identity remains required for in-place update continuity. APK retention, tag, publication, and distribution remain separately authorized.

## Known limitations and legacy issues

- The complete Home instrumentation suite was unresolved at the `1.3.0` boundary and must not be represented as passed without new evidence.
- Broader device, API, OEM, profile, clone, accessibility, and performance coverage remains unknown until performed.
- Minimum acceptable performance, power, memory, and startup thresholds remain undecided and are not version gates unless explicitly promoted.
- Drawer search and display settings remain outside `1.4.0`; their absence is planned scope, not a failed `1.4.0` result.

## Completion criteria

- Iterations 22–25 are `Completed` with separately recorded evidence and author acceptance.
- Product contracts, implementation, tests, and delivery evidence have no unresolved material mismatch in the selected scope.
- The mandatory physical-device upgrade and complete selected Home journey are accepted.
- Final identifiers, allocated `versionCode`, source commit, signing category, APK identity, known gaps, and tag disposition are recorded accurately.

## Completion result

No version completion result exists. Iterations 22-24 are `Completed`; Iteration 25 is `In Progress`. This document does not authorize push, release, or publication.
