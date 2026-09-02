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
| [Iteration 23: Module Style and Ordering](iteration-23-module-style-and-ordering.md) | `In progress` | 2026-09-01 | Authorized implementation is being realigned to accepted product-contract baseline `7cae837dafb188896dd24bd43aae58022c81fe11`. |
| [Iteration 24: Application Editing and Cross-Module Movement](iteration-24-application-editing-and-cross-module-movement.md) | `Planned` | 2026-09-01 | The accepted product contract defines collapsed-dock removal, Undo, insertion movement, and the drop-to-create destination after module types, destinations, and add-favorite entries exist. |
| [Iteration 25: Upgrade, Regression, and Version Closure](iteration-25-upgrade-regression-and-version-closure.md) | `Planned` | 2026-09-01 | Version completion requires integrated Home adoption, compatibility, artifact, and author-acceptance evidence after the selected Home loop is complete. |

## Iteration evidence and results

### Iteration 22

[Contract](iteration-22-ordered-favorite-module-foundation.md). The implementation establishes the ordered-module store, one-time readable-legacy reset, initial vertical-module creation through Drawer, ordered Home rendering and launch, and focused store/UI tests. The final backup-source and retry-loading fixes preserve the required failure states, and the runtime Privacy text now reflects the adopted storage boundary. The author reported successful compilation and basic functional acceptance, including acceptance after the final fixes. Agent static checks passed for `git diff --check` and resource XML parsing; Gradle and instrumentation were not run by the agent. Status is `Completed`. The consequential persistence direction remains subject to the repository rule that an `Active` ADR is created only after its accepted implementation is committed and synchronized.

### Iteration 23

[Contract](iteration-23-module-style-and-ordering.md). Implementation is in progress on accepted product-contract baseline `7cae837dafb188896dd24bd43aae58022c81fe11`. The persisted style schema, backward-readable defaults, module selection, edit dock, initial inline panel, and immediate durable vertical-style preview exist locally; the author reported that the persistence foundation compiled without errors. The implementation is being realigned so add-favorite entries belong to the main list and module tails rather than the panel, and the shared panel and whole-module movement contracts remain incomplete. Current unvalidated work remains `Not run` or `Unknown`. Status is `In progress`.

### Iteration 24

[Contract](iteration-24-application-editing-and-cross-module-movement.md). No implementation or validation evidence exists. The contract is rebaselined to accepted product-contract commit `7cae837dafb188896dd24bd43aae58022c81fe11`, including the drop-to-create destination when an application is released on a main-list add-favorite entry. Status remains `Planned`; absent evidence is not a pass.

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

No version completion result exists. Iteration 22 is `Completed`; Iteration 23 is `In progress`; Iterations 24–25 remain `Planned`. This document does not authorize push, release, or publication.
