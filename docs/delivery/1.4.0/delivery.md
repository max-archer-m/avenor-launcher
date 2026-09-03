# Avenor Launcher 1.4.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This record plans behavior selected from product-contract baseline `7cae837dafb188896dd24bd43aae58022c81fe11`. Status and evidence do not authorize implementation, a version change, commit, push, tag, artifact movement, publication, or release.

## Version intent

`1.4.0` replaces the completed `1.3.0` fixed vertical-list and favorite-bar composition with the current ordered favorite-module model. The selected result is a complete Home editing loop: the author can build, style, order, and maintain one direct sequence of vertical modules and ribbons without requiring the separately planned Drawer search or display-setting work.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

The planned application identity remains `com.avenor.launcher` with `versionName` `1.4.0`. `versionCode` `5` is the candidate next value and is not allocated until a traceable APK uses it under release governance.

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
| [Iteration 24: Application Editing and Cross-Module Movement](iteration-24-application-editing-and-cross-module-movement.md) | `In Progress` | 2026-09-03 | The author reports basic functional acceptance through drop-to-create. Interaction refinement and final integrated validation remain incomplete; this is not iteration completion. |
| [Iteration 25: Upgrade, Regression, and Version Closure](iteration-25-upgrade-regression-and-version-closure.md) | `Planned` | 2026-09-01 | Version completion requires integrated Home adoption, compatibility, artifact, and author-acceptance evidence after the selected Home loop is complete. |

## Iteration evidence and results

### Iteration 22

[Contract](iteration-22-ordered-favorite-module-foundation.md). The implementation establishes the ordered-module store, one-time readable-legacy reset, initial vertical-module creation through Drawer, ordered Home rendering and launch, and focused store/UI tests. The final backup-source and retry-loading fixes preserve the required failure states, and the runtime Privacy text now reflects the adopted storage boundary. The author reported successful compilation and basic functional acceptance, including acceptance after the final fixes. Agent static checks passed for `git diff --check` and resource XML parsing; Gradle and instrumentation were not run by the agent. Status is `Completed`. The consequential persistence direction remains subject to the repository rule that an `Active` ADR is created only after its accepted implementation is committed and synchronized.

### Iteration 23

[Contract](iteration-23-module-style-and-ordering.md). The implementation on accepted product-contract baseline `7cae837dafb188896dd24bd43aae58022c81fe11` now provides position-resolved add-favorite entries for both module types and existing modules; the shared inline style panel with content-driven bounded height; complete-module selection; durable vertical-module style changes with serialization and rollback; and insertion-only whole-module movement with a frozen preview, source removal, boundary feedback, edge auto-scroll, atomic order persistence, cancellation, interruption cleanup, and localized failure recovery. Focused ordered-store test sources cover exact module-order persistence and invalid order rejection. The author reported that the current functional behavior was basically accepted after the final gesture-lifecycle fix. Agent static checks passed for `git diff --check` and affected resource XML parsing. Gradle and instrumentation were not run by the agent; broader recommended device, interruption, failure-injection, font-scale, process-recreation, and compatibility scenarios remain `Not run` or `Unknown` and are not promoted iteration blockers. Status is `Completed` by author decision on 2026-09-02.

### Iteration 24

[Contract](iteration-24-application-editing-and-cross-module-movement.md). Implementation began under author authorization on 2026-09-03 against product-contract baseline `7cae837dafb188896dd24bd43aae58022c81fe11`. The author-authorized amendment of the same date selects baseline `78d2aab18066c2d9b57b56581e0ab8c17402d104` for movement feedback, content transitions, and add-entry surface refinements; the contract records the previous boundary and affected acceptance requirements. Status remains `In Progress`.

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

The source placeholder described above remains an implementation mismatch with the amended contract and must be removed under the selected scope. Ribbon gap-centered insertion feedback, current-visible-geometry resolution, content transitions and preview handoff, and revised add-entry surfaces still require implementation alignment and applicable validation. Resolving the documentation merge does not complete those changes or extend the earlier author acceptance to them. Final interaction, recovery, regression, and iteration-wide decision review remain incomplete.

### Iteration 25

[Contract](iteration-25-upgrade-regression-and-version-closure.md). No implementation or validation evidence exists. Status remains `Planned`; absent evidence is not a pass.

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

No version completion result exists. Iterations 22-23 are `Completed`; Iteration 24 is `In Progress`; Iteration 25 remains `Planned`. This document does not authorize push, release, or publication.
