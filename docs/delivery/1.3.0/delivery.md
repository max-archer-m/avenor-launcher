# Avenor Launcher 1.3.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This record tracks the behavior selected for `1.3.0`. Status and evidence do not authorize a version change, commit, push, tag, publication, or release.

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
| [Iteration 17: Home Edit Session and Vertical-List Management](iteration-17-home-edit-session-and-vertical-list-management.md) | `Completed` | 2026-08-25 | Implementation is complete; the author reported that compilation, installation, and the currently verifiable behavior were basically accepted. |
| [Iteration 18: Drawer Targeted Multi-Selection and Vertical-List Addition](iteration-18-drawer-targeted-multiselection-and-list-creation.md) | `Completed` | 2026-08-25 | The implementation is complete; the author reported that code-level acceptance and the basic device acceptance passed with no current functional issue. |
| [Iteration 19: Favorite-Bar Presentation, Creation, and Management](iteration-19-favorite-bar-presentation-creation-and-management.md) | `Completed` | 2026-08-26 | Implementation is complete; the author reported that packaging and compilation operated normally and the current functional acceptance basically passed. |
| [Iteration 20: Cross-Container Application Drag and Two-Axis Auto-Scroll](iteration-20-cross-container-drag-and-two-axis-auto-scroll.md) | `Planned` | 2026-08-23 | Cross-container movement requires all destination types and their local management behavior. |
| [Iteration 21: Upgrade, Regression, and Version Closure](iteration-21-upgrade-regression-and-version-closure.md) | `Planned` | 2026-08-23 | Final closure follows acceptance of Iterations 15-20 and the required version evidence. |

## Iteration evidence and results

### Iteration 15

[Contract](iteration-15-unified-favorite-aggregation-and-migration.md). The unified aggregate, schema-1/schema-2 migration, container and identity invariants, AtomicFile persistence, aggregate reconciliation, compatibility projections, and focused test coverage are implemented. `git diff --check` and the changed implementation line-width sweep are `Passed`. Agent-run Gradle and instrumented tests are `Not run`; the author reported that Gradle compilation, the `1.2.0` upgrade, favorite data, and the remaining acceptance checks passed with no material issue. The retained `1.2.0` 45:55 composition and asymmetric visible item sizes are presentation behavior outside this iteration and remain assigned to Iteration 16. No push, tag, publication, or release occurred. Status is `Completed`.

### Iteration 16

[Contract](iteration-16-vertical-list-normal-mode-composition.md). The normal Home composition now renders zero, one, or two persisted vertical lists with per-list size, equal-status layout, independent list state, and shared loading/error/empty handling. P1 fixes preserve vertical-container IDs and list sizes during drag persistence, retain favorite bars, bind asynchronous completion to the active drag generation, and apply the contracted internal item padding. The author reported that the currently verifiable behavior was basically accepted. Agent-run Gradle and instrumented tests are `Not run`; `git diff --check` and the changed implementation line-width sweep are `Passed`. The author-directed contract amendment retains edit-session drag compatibility as compatibility work without expanding list controls, new-list creation, favorite-bar management, or new drag semantics. Status is `Completed`; no commit, push, tag, publication, or release is claimed.

### Iteration 17

[Contract](iteration-17-home-edit-session-and-vertical-list-management.md). The Home edit session, list-size and removal controls, same-list application exchange, complete-list drag reorder, latest-removal Undo, drag ownership, edge auto-scroll, frozen previews, source placeholders, target feedback, persistence, localized feedback, and baseline UI-test coverage are implemented. The author reported that compilation, installation, and the currently verifiable behavior were basically accepted. Agent-run Gradle and instrumented tests are `Not run`; Kotlin delimiter inspection, affected XML parsing, residual production-log and marker searches, `git diff --check`, and `git diff --cached --check` were `Passed`. No push, tag, publication, or release is claimed. Status is `Completed` by author decision on 2026-08-25.

### Iteration 18

[Contract](iteration-18-drawer-targeted-multiselection-and-list-creation.md). The implementation presents persisted and provisional vertical-list add controls, captures the exact target, opens the Drawer selection mode, preserves ordered selection with circular numeric indicators, disables already-favorited identities, freezes applicable interaction during save, revalidates selected identities against the current inventory, protects provisional creation from zero valid identities, restores the Home target position, and supports atomic aggregate append or medium provisional-list creation. The Home edit surface also includes the accepted list-level drag exchange behavior. The author reported that code-level acceptance and basic device acceptance passed with no current functional issue; a minor exchange-time pause remains a performance observation rather than a functional failure.

The following local checks are `Passed`: `git diff --check`, staged-diff whitespace check, affected XML parsing, and the implementation line-width sweep. Agent-run Gradle and instrumented tests are `Not run`; broader API, OEM, profile, clone, process-recreation, accessibility, and performance scenarios remain `Not run` or `Unknown`. These are recorded evidence gaps, not reported failures for the author-accepted iteration scope. Status is `Completed` by author decision on 2026-08-25; no commit, push, tag, publication, artifact, or release is claimed.

### Iteration 19

[Contract](iteration-19-favorite-bar-presentation-creation-and-management.md). Typed existing and provisional favorite-bar Drawer targets, atomic creation or append, conditional normal and edit-mode Home presentation, fixed-medium application items, launch and long-press behavior, the five-bar limit, localized add actions, stable horizontal viewports, overflow fades, retained same-process positions, inventory reconciliation, and target-only reveal are implemented. Application and complete-bar removal, latest-only Undo with capacity reservation, automatic empty-bar deletion and restoration, same-bar application exchange, complete-bar reorder, fixed edit rails, compact vertical-list controls, source placeholders, persistence rollback, and successful-addition invalidation of stale Undo are also implemented.

The author reported that the packaging and compilation workflows operated normally and that the current functional acceptance basically passed. The author also clarified that the accepted vertical-list start geometry is the composed `8dp` Home content padding plus `8dp` list-item icon inset, yielding `16dp` from the applicable safe edge; a later product-document wording clarification does not represent an implementation failure or block this iteration. `git diff --check`, affected XML parsing, and the changed implementation line-width sweep are `Passed`. Agent-run Gradle and instrumented tests are `Not run`; broader device, accessibility, locale, process-recreation, and failure-injection scenarios remain `Not run` or `Unknown`. These are recorded evidence gaps rather than reported failures for the author-accepted scope. Status is `Completed` by author decision on 2026-08-26; no commit, push, tag, publication, or release is claimed.

### Iteration 20

[Contract](iteration-20-cross-container-drag-and-two-axis-auto-scroll.md). Status is `Planned`; no implementation, command, evidence, artifact, result, commit, or tag is recorded.

### Iteration 21

[Contract](iteration-21-upgrade-regression-and-version-closure.md). Status is `Planned`; no implementation, command, evidence, artifact, result, commit, or tag is recorded.

## Dependencies and sequence

The current delivery dependency is `15 → 16 → 17 → 18 → 19 → 20 → 21`. Iteration 19 is complete and establishes the container types and local-management behavior required by Iteration 20. The sequence does not bind an iteration to a contributor, terminal, branch, forecast date, or permanent single-line execution policy.

## Validation

The mandatory version environment is one author-designated primary physical device. Completion requires an installable artifact with accepted identifiers; a successful in-place upgrade from the accepted `1.2.0` baseline without unintended favorite loss, duplication, order change, destination change, or unreadable-data overwrite; author acceptance of the complete selected journey; and no known included-path crash, ANR, destructive favorite-state error, wrong launch, or navigation dead end. A clean installation may provide additional evidence but cannot replace the required upgrade journey.

Focused automated checks and additional API, OEM, locale, profile, clone, process-recreation, font-scale, navigation-mode, inventory-change, gesture-interruption, and persistence-failure coverage are recommended evidence unless the author later promotes a check. An executed recommended check that exposes an included-path failure must be resolved or explicitly dispositioned; an unperformed check is `Not run`, `Unknown`, or `Unavailable`, never passed.

## Artifact and release requirements

The eventual artifact must retain `applicationId` `com.avenor.launcher`, use the accepted `1.3.0` identifiers, be traceable to one source commit, and record signing category and applicable upgrade limitations. This delivery level does not require a formal release artifact. Version completion, tag approval, GitHub Release, upload, publication, and distribution remain separate decisions.

## Known limitations and legacy issues

- Exact build, automated-test, and artifact evidence is not yet available for Iteration 18.
- Iteration 18 has author-reported device acceptance for the currently implemented flow; this does not replace the unrun Gradle and instrumented-test evidence.
- Broader device, API, OEM, profile, clone, accessibility, and performance coverage remains unknown until performed.
- `1.2.0` closed with its exact-identifier installation and in-place-upgrade checks `Not run`; `1.3.0` must create its own truthful upgrade evidence rather than revising that history.

## Completion criteria

- Iterations 15-21 are `Completed` with separately recorded evidence and author acceptance.
- The mandatory primary-device in-place upgrade from the accepted `1.2.0` baseline and complete selected journey are accepted.
- Migration, persistence, inventory reconciliation, process recreation, navigation, edit, addition, favorite-bar, drag, auto-scroll, and failure-recovery behavior meet the selected contracts.
- Product documents, implementation, tests, and recorded evidence have no unresolved material mismatch within the selected scope.
- Identifiers, source commit, signing category, artifact traceability, known gaps, author decision, and any separate tag disposition are recorded accurately.

## Completion result

No version completion result exists. Iterations 15-19 are `Completed`, and Iterations 20-21
remain `Planned`, so `1.3.0` remains incomplete. No version
artifact, tag, publication, or release is claimed.
