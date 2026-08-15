# Iteration 4: Application Action Sheet and Favorite Creation

> Semantic source: English. Chinese counterpart: [iteration-4-application-action-sheet-and-favorite-creation.zh-CN.md](iteration-4-application-action-sheet-and-favorite-creation.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](delivery.md). This iteration contract defines one product increment and its evidence. Its status records the current lifecycle result; it does not independently authorize new implementation or approve candidate architecture.

## Status

- Value: `Completed`
- Updated: 2026-08-12
- Basis: The author accepted the action-sheet and favorite-creation result, and implementation commit `1afb30c` and the corresponding iteration documentation are committed in the shared `origin/main` history. Outstanding validation remains recorded below and is not treated as passed.

## Objective

Let the author long-press an application, use the included modal application actions, and add a Drawer entry to Home as one persistent, non-duplicated favorite.

## Product and version references

- [1.0.0 delivery contract](delivery.md)
- [1.0.0 product scope](product-scope.md)
- [1.0.0 technical assessment](technical-assessment.md)
- [Home interaction](../../product/surfaces/home.md)
- [Drawer interaction](../../product/surfaces/drawer.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Product design foundations](../../product/design-foundations.md)

## Observable outcome

The author can long-press a Drawer entry, open the modal application action sheet, open system application information, add the entry as a favorite, and observe that favorite on Home after ordinary process recreation without creating a duplicate.

## Included work

- Open the application action sheet from a Drawer long-press without conflicting with list, transition, or index gestures.
- Implement the included modal presentation, scrim, drag handle, input blocking, Back/scrim/drag dismissal, identity presentation, and platform-provided badge treatment.
- Open system application information defensively and preserve the originating Drawer position after return or failure as specified.
- Present the applicable add-favorite action for a non-favorite Drawer entry.
- Establish ordered favorite identity persistence, schema versioning, atomic mutation, backup exclusion, and process restoration.
- Append a newly favorited identity to Home and prevent duplicate addition of the same launchable identity.
- Keep the application in Drawer after adding it to Home.
- Distinguish a successful empty persistence read from a failed read and preserve the no-destructive-write invariants required by the technical assessment.
- Localize the included action labels and failure feedback.

## Excluded work

- Remove-favorite actions and complete favorite lifecycle assigned to Iteration 5.
- Favorite reordering, reorder mode, platform application shortcuts, uninstall, disable, clone removal, Settings, or a user-visible third-party license surface.
- The complete favorite-corruption recovery UI excluded from `1.0.0`.
- Full-version compatibility, measured quality, signing, formal APK, completed-version recording, tag, or distribution actions.

## Technical change areas

- Modal sheet state, selected-entry identity, pointer/input blocking, dismissal, and platform information action.
- Favorite domain model, ordered identity schema, persistence boundary, atomic add/deduplication, read state, and process restoration.
- Home favorite rendering from persisted identities and Drawer/Home shared inventory state.
- Backup/data-extraction exclusions and dependency/license evaluation for the selected persistence implementation.

Proto DataStore is a candidate, not a preapproved result. The iteration selects the smallest proven persistence approach that preserves ordered typed identity, explicit migration, read-failure distinction, no destructive overwrite, and acceptable license obligations. A consequential persistence choice is documented through architecture or an ADR when required.

## Dependencies and sequence

- Iteration 3 is `Completed`; its selected-entry identity, Drawer position, and long-press input behavior form the accepted foundation for this iteration.
- The selected persistence stack must have enough known dependency and license information to avoid a known blocker on the included daily-use path; unresolved formal-review evidence is recorded for follow-up.
- The project author explicitly authorizes this iteration and any product change if a required notice surface conflicts with the approved exclusion.
- The project author may authorize Iteration 5 when the observable favorite-creation and persistence foundation is acceptable for adding removal and reconciliation and known gaps are recorded.

## Migration and compatibility impact

- This iteration creates the first persisted favorite schema; no pre-1.0 production migration exists.
- The schema includes only stable launchable identity, order, and schema version needed by the approved product path.
- It must permit future migration without destructive replacement.
- Cloud backup and device-to-device transfer remain excluded.

## Security, privacy, permission, and licensing impact

- Favorites are local user-content data stored in credential-encrypted application-private storage.
- Favorite identities are not logged in full in release builds and are not used for analytics or usage history.
- No network, account, cloud, broad package visibility, or hidden-profile capability is introduced.
- External application-information actions handle absence and `SecurityException` without exposing raw exception text.
- Any unresolved persistence dependency graph or notice obligation is recorded before iteration completion and requires qualified review before a future formal release artifact.

## Risks and unresolved decisions

- Candidate identity may not remain durable for a device-specific clone.
- Persistence integration may not preserve failure invariants without an alternative serializer or DataStore form.
- Future qualified license review may require a product-visible notice surface currently excluded from `1.0.0`; that impact must remain recorded until resolved.
- Sheet gestures may conflict with underlying transition or list input.
- Showing a newly added favorite requires correct reconciliation with current inventory without persisting labels, icons, or availability as favorite truth.

## Validation plan

The following scenarios are recommended to reduce delivery risk and improve evidence. Unless the project author explicitly promotes a scenario to a gate, incomplete or unavailable results do not block this iteration's entry, exit, or progression. Missing results remain unknown and must be recorded rather than treated as passed.

- Unit-test schema encoding, identity equality, append order, duplicate prevention, atomic mutation, read failure, and process restoration.
- UI-test long-press, modal blocking, dismissal, application information, add favorite, localized failures, and Home presentation.
- Instrument persistence recreation and backup/data-extraction configuration where practical.
- Validate add-to-Home behavior for primary and exposed clone/profile entries on both physical devices.
- Verify process recreation retains the exact favorite once and does not write during a failed read.
- Inspect the resolved dependency graph, license evidence, and merged manifest.

## Acceptance evidence

When performed, record the following recommended evidence. Missing recommended evidence does not by itself block author acceptance or progression:

- action-sheet interaction and platform-information results;
- exact add, deduplication, append-order, Home presentation, and process-recreation results;
- identity results for applicable primary and clone/profile entries;
- persistence schema, failure-invariant, backup-exclusion, dependency, and license evidence;
- successful automated and manual commands; and
- every product or technical mismatch and its disposition.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Implementation commit: `1afb30c feat(favorites): add persistent favorite creation` delivered the modal action sheet, exact ordered identity schema, atomic add and deduplication, read states, Home presentation, and process reload foundation.
- Active [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md) records the implemented versioned `AtomicFile` persistence direction and the author's acceptance of its material technical trade-offs.
- The clone/profile badge contract now uses a `12dp × 12dp` region aligned to the sheet bottom-right without an outward offset or active clipping.
- The project author reported a successful Gradle build, correct persistence in tested daily scenarios, correct Samsung clone presentation, and correct long-press interaction. The exact Gradle command, build variant, environment, retained output, damaged-file injection, API 31, Pixel, merged-manifest, and full dependency/license evidence remain unknown.
- Product decision: required before adding a user-visible notice surface or changing approved behavior.
- Tags: none authorized or required by this iteration.

## Final result

The project author accepted the observable action-sheet and favorite-creation result, and the implementation and iteration documentation were committed and synchronized; Iteration 4 is `Completed`. Unperformed checks are not passed; any unresolved persistence, licensing, or product issue remains explicit, and the formal `1.0.0` gates are unchanged.

## Remaining issues and handoff

The completed schema, identity, add behavior, read states, Home rendering, platform information action, Samsung clone presentation, persistence observations, and long-press boundary support [Iteration 5](iteration-5-favorite-lifecycle-and-resilience.md). Unexecuted checks remain explicit evidence gaps and are not treated as passed.
