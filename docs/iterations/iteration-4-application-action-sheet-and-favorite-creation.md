# Iteration 4: Application Action Sheet and Favorite Creation

> Semantic source: English. Chinese counterpart: [iteration-4-application-action-sheet-and-favorite-creation.zh-CN.md](iteration-4-application-action-sheet-and-favorite-creation.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md). This iteration contract defines one product increment and its required evidence. It does not authorize implementation, approve candidate architecture, or declare the iteration complete. The project author must explicitly authorize implementation.

## Objective

Let the author long-press an application, use the included modal application actions, and add a Drawer entry to Home as one persistent, non-duplicated favorite.

## Product and version references

- [1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md)
- [1.0.0 product scope](../versions/1.0.0/product-scope.md)
- [1.0.0 technical assessment](../versions/1.0.0/technical-assessment.md)
- [Home interaction](../product/home.md)
- [Drawer interaction](../product/drawer.md)
- [Application action sheet](../product/app-action-sheet.md)
- [Product design foundations](../product/design-foundations.md)

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
- Full-version compatibility, measured quality, signing, formal APK, archive, tag, or distribution actions.

## Technical change areas

- Modal sheet state, selected-entry identity, pointer/input blocking, dismissal, and platform information action.
- Favorite domain model, ordered identity schema, persistence boundary, atomic add/deduplication, read state, and process restoration.
- Home favorite rendering from persisted identities and Drawer/Home shared inventory state.
- Backup/data-extraction exclusions and dependency/license evaluation for the selected persistence implementation.

Proto DataStore is a candidate, not a preapproved result. The iteration selects the smallest proven persistence approach that preserves ordered typed identity, explicit migration, read-failure distinction, no destructive overwrite, and acceptable license obligations. A consequential persistence choice is documented through architecture or an ADR when required.

## Dependencies and sequence

- Iteration 3 is closed with stable selected-entry identity, Drawer position, and long-press input boundaries.
- Qualified dependency/license review is sufficient to determine whether the selected persistence stack can remain within `1.0.0` scope.
- The project author explicitly authorizes this iteration and any product change if a required notice surface conflicts with the approved exclusion.
- Completion unlocks Iteration 5 only when persisted creation is stable enough to add removal and reconciliation without replacing the schema.

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
- The resolved persistence dependency graph and notice obligations require documented review before closure.

## Risks and unresolved decisions

- Candidate identity may not remain durable for a device-specific clone.
- Persistence integration may not preserve failure invariants without an alternative serializer or DataStore form.
- Qualified license review may require a product-visible notice surface currently excluded from `1.0.0`.
- Sheet gestures may conflict with underlying transition or list input.
- Showing a newly added favorite requires correct reconciliation with current inventory without persisting labels, icons, or availability as favorite truth.

## Validation plan

- Unit-test schema encoding, identity equality, append order, duplicate prevention, atomic mutation, read failure, and process restoration.
- UI-test long-press, modal blocking, dismissal, application information, add favorite, localized failures, and Home presentation.
- Instrument persistence recreation and backup/data-extraction configuration where practical.
- Validate add-to-Home behavior for primary and exposed clone/profile entries on both physical devices.
- Verify process recreation retains the exact favorite once and does not write during a failed read.
- Inspect the resolved dependency graph, license evidence, and merged manifest.

## Acceptance evidence

Before closure, record:

- action-sheet interaction and platform-information results;
- exact add, deduplication, append-order, Home presentation, and process-recreation results;
- identity results for applicable primary and clone/profile entries;
- persistence schema, failure-invariant, backup-exclusion, dependency, and license evidence;
- successful automated and manual commands; and
- every product or technical mismatch and its disposition.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Persistence or identity ADR: add only if the proven decision is consequential and durable.
- Product decision: required before adding a user-visible notice surface or changing approved behavior.
- Implementation commits: to be recorded from actual work.
- Tags: none authorized or required by this iteration.

## Final result

The iteration closes only when the author can add one exact Drawer entry as a durable, non-duplicated Home favorite through the included action sheet, and persistence and license evidence satisfy the contract. Before then, no completion is claimed.

## Remaining issues and handoff

The handoff to [Iteration 5](iteration-5-favorite-lifecycle-and-resilience.md) records the accepted schema, identity, add behavior, read states, Home rendering, platform action behavior, and all unresolved lifecycle cases. It must not transfer destructive persistence behavior or duplicate creation as normal follow-up.
