# Iteration 5: Favorite Lifecycle and Resilience

> Semantic source: English. Chinese counterpart: [iteration-5-favorite-lifecycle-and-resilience.zh-CN.md](iteration-5-favorite-lifecycle-and-resilience.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](delivery.md). This iteration contract defines one product increment and its evidence. Its status records the current lifecycle result; it does not independently authorize new implementation or approve candidate architecture.

## Status

- Value: `Completed`
- Updated: 2026-08-13
- Basis: The implementation and observable result have been accepted, and implementation commit `00bdb98` with the corresponding iteration evidence is synchronized to the shared `origin/main` history.

## Objective

Complete the `1.0.0` favorite lifecycle so the author can add, persist, launch, survive ordinary recreation and restart, reconcile, and remove favorites without data loss or unintended Drawer changes.

## Product and version references

- [1.0.0 delivery contract](delivery.md)
- [1.0.0 product scope](product-scope.md)
- [Product navigation](../../product/navigation.md)
- [Home interaction](../../product/surfaces/home.md)
- [Drawer interaction](../../product/surfaces/drawer.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)

## Observable outcome

The author can find and launch an exposed application, add it to Home, launch it from Home, restart the process or device without losing it, remove it from either applicable action-sheet context, and confirm that removal changes only Home while Drawer remains correct. Transient inventory or launch failure does not delete the favorite.

## Included work

- Open the application action sheet from Home and Drawer with the correct favorite action for current state.
- Launch an available favorite from Home and suppress duplicate rapid activation.
- Remove a favorite from Home or Drawer without removing its launchable entry from Drawer.
- Preserve Drawer position and Home state as required after actions and platform returns.
- Persist valid identity and order through process recreation and device restart.
- Reconcile favorites against successful inventory snapshots using exact identity.
- Retain favorites during loading, inventory failure, transient launch failure, disabled state, locked-profile state, or unconfirmed identity.
- Remove a favorite automatically only after a successful refresh confirms permanent disappearance of that exact identity.
- Handle disabled and transiently unavailable favorites with localized feedback and without destructive state change.
- Complete the selected offline favorite journey for primary and platform-exposed clone/profile entries.
- Ensure favorite mutations remain disabled when persistence state cannot be reliably read, without implementing excluded recovery UI.

## Excluded work

- Favorite reorder mode, drag handles, position swapping, reorder haptics, and reorder auto-scroll.
- Complete favorite-corruption repair, reset, export, backup, restore, or user-visible read-only recovery UI.
- Platform application shortcuts, uninstall, disable, clone removal, Settings, manual language selection, or other excluded actions.
- Full-version performance thresholds, release signing, formal APK, completed-version recording, tag, or distribution actions.

## Technical change areas

- Home/Drawer shared favorite state and action-sheet action selection.
- Launch throttling, unavailable-state refresh, deterministic reconciliation, confirmed disappearance, and callback/mutation serialization.
- Persistence across process and device restart, read failure, mutation gating, and schema evolution.
- Focused end-to-end state and UI validation across Home, Drawer, action sheet, platform launch, and local storage.

Implementation details remain in code and tests. A newly consequential state-ownership, reconciliation, or persistence decision is recorded in architecture or an ADR rather than duplicated here.

## Dependencies and sequence

- Iteration 4 is `Completed`; its favorite identity, schema, creation, Home presentation, and action-sheet input behavior form the accepted foundation for this iteration.
- Inventory and live-update behavior from Iterations 2 and 3 remains stable under favorite reconciliation.
- The project author explicitly authorizes this iteration.
- The project author may authorize Iteration 6 when the observable favorite lifecycle foundation is acceptable for compatibility and quality work and known gaps are recorded; unresolved core behavior must remain explicit rather than being relabeled as generic quality work.

## Migration and compatibility impact

- The Iteration 4 schema remains authoritative unless evidence requires an approved migration decision.
- Any schema change is tested for non-destructive migration from data produced by the prior iteration.
- No cloud or cross-device migration is introduced.
- Primary and clone/profile favorites remain independent when Android exposes distinct launchable identities.

## Security, privacy, permission, and licensing impact

- Favorite data remains local, application-private, minimized, excluded from cloud/device transfer, and absent from analytics.
- No new sensitive permission or service is required.
- Platform launch and application-information actions handle failures without exposing raw exceptions or deleting data.
- Dependency and merged-manifest review continues for any newly resolved library or test tool.

## Risks and unresolved decisions

- OEM callbacks may make permanent disappearance difficult to prove from a single event.
- A locked or temporarily unavailable profile may resemble removal.
- Concurrent Retry, refresh, launch, and favorite mutation can publish stale state if not serialized.
- Device restart evidence may reveal identity instability for a clone/profile entry.
- An included behavior that cannot be met on a required device requires a contract mismatch decision, not silent deletion or package-name fallback.

## Validation plan

The following scenarios are recommended to reduce delivery risk and improve evidence. Unless the project author explicitly promotes a scenario to a gate, incomplete or unavailable results do not block this iteration's entry, exit, or progression. Missing results remain unknown and must be recorded rather than treated as passed.

- Unit-test add/remove/deduplication, launch throttling, reconciliation, permanent disappearance, transient failures, serialization, and migration.
- UI-test Home and Drawer action-sheet states, Home launch, removal from both contexts, position preservation, localized feedback, and mutation disabling.
- Instrument process recreation, persistence reload, failed reads, and successful retry foundations where applicable to the selected scope.
- Manually validate the complete offline favorite journey on both physical devices and focused compatibility on API 31.
- Restart the process and devices, validate primary and exposed clone/profile favorites, and exercise install/remove/disable/refresh cases.
- Record exact commands, build identity, source commit, APK digest where available, environment, procedure, and results.

## Acceptance evidence

When performed, record the following recommended evidence. Missing recommended evidence does not by itself block author acceptance or progression:

- the complete find, launch, add, Home launch, restart, remove, and resulting-state journey;
- primary and applicable clone/profile identity persistence results;
- process recreation and device restart evidence;
- transient failure, disabled state, successful disappearance, and non-destructive read-failure results;
- successful automated and manual validation commands; and
- all remaining defects or contract mismatches transferred to Iteration 6 or author decision.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Implementation commit: `00bdb98 feat(favorites): complete lifecycle and resilient reconciliation` delivered Home exact-entry launch with rapid-activation suppression, Home and Drawer remove-favorite actions, mutation gating, shared inventory state, explicit favorite availability, profile-complete exact-identity reconciliation, retained disabled or unknown favorites, localized feedback, and shared Home/Drawer marquee coordination.
- Active [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md) records the implemented persistence boundary inherited from Iteration 4 and the author's acceptance of its material technical trade-offs.
- Active [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md) records the implemented profile-level snapshot completeness and conservative exact-identity reconciliation direction and the author's acceptance of its material technical trade-offs.
- Focused test source covers availability presentation, exact identity/profile independence, non-destructive partial reads, favorite-store mutations and damaged-file protection, action-sheet mutation states, and marquee priority. These tests have not been run by an agent in this change.
- The project author reported a successful Gradle build for the implementation under review and considers its functional result acceptable for continuing delivery. The exact Gradle command, build variant, environment, procedure, and retained output were not reported.
- Product decision: required for any behavior or acceptance change.
- Tags: none authorized or required by this iteration.

## Final result

The implementation and observable favorite-lifecycle result have been accepted, and the implementation and corresponding iteration evidence are synchronized to the author-designated shared Git history. Iteration 5 is `Completed`. Unperformed checks are not passed, unresolved core behavior remains explicit, and the formal `1.0.0` gates are unchanged.

## Remaining issues and handoff

The project author's reported successful Gradle build and functional acceptance are sufficient to continue delivery toward [Iteration 6](iteration-6-compatibility-quality-and-formal-apk-closure.md). Exact Gradle command and environment details, API 31 and Pixel behavior, locked/disabled profile behavior, process/device restart, damaged-file injection, and write-failure behavior remain unknown recommended evidence. Iteration 6 must not be used to hide a subsequently identified unfinished core favorite path.
