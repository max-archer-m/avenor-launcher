# Avenor Launcher 1.3.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This record plans the behavior selected for `1.3.0`. `Planned` does not authorize production implementation, a version change, build, commit, push, tag, publication, or release.

## Version intent

`1.3.0` replaces the protected `1.2.0` fixed primary/companion favorite composition with the current unified favorite model: up to two equal-status vertical lists, up to five favorite bars, one Home edit session, destination-targeted Drawer addition, and consistent movement and recovery across favorite containers.

The closed `1.2.0` record remains unchanged. Product-contract changes after its Iteration 14 implementation boundary enter this version only through the iteration contracts listed below.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

The intended `versionName` is `1.3.0`. Its `versionCode`, source commit, signing category, artifact identity, completion decision, and any tag decision remain unallocated or unperformed until their applicable later gates. A tag is a possible post-acceptance author decision, not a promised result.

## Product references

- [Product overview](../../../overview.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Navigation](../../product/navigation.md)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Design foundations](../../product/design-foundations.md)
- [Privacy and data handling](../../product/features/privacy.md)
- [Validation guide](../../validation.md)
- [Release governance](../../release.md)

## Included scope and user journey

The selected journey migrates readable existing favorites into one unified destination model; presents one full-width or two equal-width vertical lists with independent list-level sizes; lets the author enter one Home edit session to manage lists and favorites; adds ordered applications from a destination-bound Drawer multi-selection flow; presents and manages up to five favorite bars; and supports contracted same-container and cross-container movement, target feedback, two-axis edge auto-scroll, failure recovery, inventory reconciliation, and state restoration.

Version closure covers an in-place upgrade from the accepted `1.2.0` baseline, process recreation, application-inventory change, persistence failure, and the complete included author journey.

## Exclusions

- Drawer search; favorite-bar titles, naming, or renaming; user-configurable favorite-bar limits; favorite-bar size selection; new theme settings; folders; automatic sorting; export; repair; reset; cloud backup; restore; or synchronization.
- Android Private Space or `ACCESS_HIDDEN_PROFILES`.
- New build, permission, privacy, or distribution scope for double-tap lock.
- A general undo history beyond the current latest-removal snapshot and defined cancellation or failure-recovery behavior.
- Formal release artifact, GitHub Release, store publication, public distribution, or a promised tag.

## Technical approach and risks

Development owns implementation details. The accepted technical assessment found no known feasibility blocker and recommended the sequence below. Implementation must preserve stable launchable identity, versioned atomic persistence, unreadable-data fail-closed behavior, backup exclusion, and the current navigation and inventory boundaries.

Material changes to persistence architecture, state ownership, permissions, privacy, background behavior, compatibility, or product scope require author direction and an ADR or specialist review when applicable. The main risks are destructive migration, competing state owners, stale provisional destinations, gesture arbitration among multiple scroll axes, failure rollback that overwrites newer inventory facts, and regressions to the accepted `1.2.0` Home–Drawer transition.

## Included iterations

| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [Iteration 15: Unified Favorite Aggregation and Compatible Migration](iteration-15-unified-favorite-aggregation-and-migration.md) | `Completed` | 2026-08-23 | Implementation is complete; the author reported that compilation, upgrade, favorite-data, and remaining acceptance passed with no material issue. |
| [Iteration 16: Vertical-List Normal-Mode Composition](iteration-16-vertical-list-normal-mode-composition.md) | `Completed` | 2026-08-24 | Normal Home composition and P1 aggregate-persistence fixes are implemented; the author reported that the currently verifiable behavior was basically accepted. |
| [Iteration 17: Home Edit Session and Vertical-List Management](iteration-17-home-edit-session-and-vertical-list-management.md) | `Planned` | 2026-08-23 | Editing and list management depend on accepted normal-mode list composition. |
| [Iteration 18: Drawer Targeted Multi-Selection and Vertical-List Addition](iteration-18-drawer-targeted-multiselection-and-list-creation.md) | `Planned` | 2026-08-23 | Technical assessment recommended completing the reusable destination-targeted addition flow before favorite bars. |
| [Iteration 19: Favorite-Bar Presentation, Creation, and Management](iteration-19-favorite-bar-presentation-creation-and-management.md) | `Planned` | 2026-08-23 | Favorite bars reuse the accepted edit session and targeted Drawer addition flow. |
| [Iteration 20: Cross-Container Application Drag and Two-Axis Auto-Scroll](iteration-20-cross-container-drag-and-two-axis-auto-scroll.md) | `Planned` | 2026-08-23 | Cross-container movement requires all destination types and their local management behavior. |
| [Iteration 21: Upgrade, Regression, and Version Closure](iteration-21-upgrade-regression-and-version-closure.md) | `Planned` | 2026-08-23 | Final closure follows acceptance of Iterations 15-20 and the required version evidence. |

## Iteration evidence and results

### Iteration 15

[Contract](iteration-15-unified-favorite-aggregation-and-migration.md). The unified aggregate, schema-1/schema-2 migration, container and identity invariants, AtomicFile persistence, aggregate reconciliation, compatibility projections, and focused test coverage are implemented. `git diff --check` and the changed implementation line-width sweep are `Passed`. Agent-run Gradle and instrumented tests are `Not run`; the author reported that Gradle compilation, the `1.2.0` upgrade, favorite data, and the remaining acceptance checks passed with no material issue. The retained `1.2.0` 45:55 composition and asymmetric visible item sizes are presentation behavior outside this iteration and remain assigned to Iteration 16. No push, tag, publication, or release occurred. Status is `Completed`.

### Iteration 16

[Contract](iteration-16-vertical-list-normal-mode-composition.md). The normal Home composition now renders zero, one, or two persisted vertical lists with per-list size, equal-status layout, independent list state, and shared loading/error/empty handling. P1 fixes preserve vertical-container IDs and list sizes during drag persistence, retain favorite bars, bind asynchronous completion to the active drag generation, and apply the contracted internal item padding. The author reported that the currently verifiable behavior was basically accepted. Agent-run Gradle and instrumented tests are `Not run`; `git diff --check` and the changed implementation line-width sweep are `Passed`. The author-directed contract amendment retains edit-session drag compatibility as compatibility work without expanding list controls, new-list creation, favorite-bar management, or new drag semantics. Status is `Completed`; no commit, push, tag, publication, or release is claimed.

### Iteration 17

[Contract](iteration-17-home-edit-session-and-vertical-list-management.md). Status is `Planned`; no implementation, command, evidence, artifact, result, commit, or tag is recorded.

### Iteration 18

[Contract](iteration-18-drawer-targeted-multiselection-and-list-creation.md). Status is `Planned`; no implementation, command, evidence, artifact, result, commit, or tag is recorded.

### Iteration 19

[Contract](iteration-19-favorite-bar-presentation-creation-and-management.md). Status is `Planned`; no implementation, command, evidence, artifact, result, commit, or tag is recorded.

### Iteration 20

[Contract](iteration-20-cross-container-drag-and-two-axis-auto-scroll.md). Status is `Planned`; no implementation, command, evidence, artifact, result, commit, or tag is recorded.

### Iteration 21

[Contract](iteration-21-upgrade-regression-and-version-closure.md). Status is `Planned`; no implementation, command, evidence, artifact, result, commit, or tag is recorded.

## Dependencies and sequence

The current delivery dependency is `15 → 16 → 17 → 18 → 19 → 20 → 21`. It reflects model, presentation, editing, target-addition, container, movement, and closure dependencies. It does not bind an iteration to a contributor, terminal, branch, forecast date, or permanent single-line execution policy.

## Validation

The mandatory version environment is one author-designated primary physical device. Completion requires an installable artifact with accepted identifiers; a successful in-place upgrade from the accepted `1.2.0` baseline without unintended favorite loss, duplication, order change, destination change, or unreadable-data overwrite; author acceptance of the complete selected journey; and no known included-path crash, ANR, destructive favorite-state error, wrong launch, or navigation dead end. A clean installation may provide additional evidence but cannot replace the required upgrade journey.

Focused automated checks and additional API, OEM, locale, profile, clone, process-recreation, font-scale, navigation-mode, inventory-change, gesture-interruption, and persistence-failure coverage are recommended evidence unless the author later promotes a check. An executed recommended check that exposes an included-path failure must be resolved or explicitly dispositioned; an unperformed check is `Not run`, `Unknown`, or `Unavailable`, never passed.

## Artifact and release requirements

The eventual artifact must retain `applicationId` `com.avenor.launcher`, use the accepted `1.3.0` identifiers, be traceable to one source commit, and record signing category and applicable upgrade limitations. This delivery level does not require a formal release artifact. Version completion, tag approval, GitHub Release, upload, publication, and distribution remain separate decisions.

## Known limitations and legacy issues

- Exact implementation, migration, build, automated-test, device, and artifact evidence is not yet available.
- Broader device, API, OEM, profile, clone, accessibility, and performance coverage remains unknown until performed.
- `1.2.0` closed with its exact-identifier installation and in-place-upgrade checks `Not run`; `1.3.0` must create its own truthful upgrade evidence rather than revising that history.

## Completion criteria

- Iterations 15-21 are `Completed` with separately recorded evidence and author acceptance.
- The mandatory primary-device in-place upgrade from the accepted `1.2.0` baseline and complete selected journey are accepted.
- Migration, persistence, inventory reconciliation, process recreation, navigation, edit, addition, favorite-bar, drag, auto-scroll, and failure-recovery behavior meet the selected contracts.
- Product documents, implementation, tests, and recorded evidence have no unresolved material mismatch within the selected scope.
- Identifiers, source commit, signing category, artifact traceability, known gaps, author decision, and any separate tag disposition are recorded accurately.

## Completion result

No version completion result exists. Iteration 15 is `Completed`; Iterations 16-21 remain
`Planned`, so `1.3.0` remains incomplete. No version artifact, tag, publication, or release is
claimed.
