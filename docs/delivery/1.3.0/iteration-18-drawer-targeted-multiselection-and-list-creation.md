# Iteration 18: Drawer Targeted Multi-Selection and Vertical-List Addition

> Applicable version: [Avenor Launcher 1.3.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Complete destination-bound Drawer favorite multi-selection so the author can append an ordered set to a persisted vertical list or atomically create a provisional vertical list.

## Product and version references

- [1.3.0 delivery](delivery.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Home](../../product/surfaces/home.md)
- [Navigation](../../product/navigation.md)
- [Design foundations](../../product/design-foundations.md)

## Observable outcome

Selecting a vertical-list add control opens Drawer with that exact destination captured. The author can select available applications in order, confirm once, or cancel without mutation; success returns to the same Home edit session and reveals only the required target content.

## Included work

- Capture persisted-list and provisional-list targets and programmatically complete the Drawer transition.
- Present Cancel, target description, Confirm, ordered selection indicators, disabled already-favorited rows, and applicable accessibility semantics.
- Reconcile installed, removed, renamed, cloned, profile-scoped, temporarily unavailable, and partially unread inventory by stable identity.
- Implement multi-selection Loading, Error, Retry, all-already-favorited Content, and no-launchable-entry behavior.
- Revalidate and atomically append selected identities in displayed order; create a provisional list at medium size only when at least one valid identity is saved.
- Suppress duplicate confirmation and freeze applicable interactions during one save.
- Retain selection for retry after save failure, cancel without partial persistence, and never redirect an invalid target.
- Restore captured Home positions and apply target-only minimum reveal after success.

## Excluded work

- Favorite-bar presentation or creation, except that the reusable target mechanism must not preclude its later use.
- Moving already-favorited identities, Drawer search, application launch, action sheets, or ordinary Settings access during multi-selection.
- Cross-container drag behavior.

## Technical change areas

Drawer mode/state ownership, destination references, ordered selection, inventory reconciliation, atomic append/create mutation, save exclusivity, navigation return, Home position restoration, accessibility, and tests.

## Dependencies and sequence

Depends on Iteration 17 edit-session/add-control seams and Iteration 15 aggregate invariants. It precedes Iteration 19, which reuses the targeted flow for favorite bars.

## Migration and compatibility impact

No format migration is selected. New writes use the unified aggregate. Provisional intent is transient and must not survive an ended or replaced edit context or process recreation.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, external service, dependency, or license impact is selected. Ordinary Private Space and backup exclusions remain unchanged.

## Risks and unresolved decisions

Stale target references, concurrent inventory changes, duplicate Confirm, and save-result/navigation races can misdirect or partially persist selection. Temporary profile failure must not be treated as permanent removal.

## Acceptance criteria

- Persisted and provisional vertical-list targets remain exact and are never silently redirected.
- Selection order, deselection renumbering, already-favorited disabled state, and Confirm enablement match the Drawer contract.
- Cancel, Back, valid downward dismissal, and Android system Home discard unconfirmed selection with their contracted Home results.
- Loading/Error/Retry and inventory changes retain, remove, or update selections only under the defined identity rules.
- One Confirm saves once; failure retains valid ordered selection for retry or cancellation; no partial selection persists.
- Successful provisional confirmation creates one medium list atomically; zero valid identities create nothing.
- Return restores non-target positions and minimally reveals the first still-valid appended target identity.

## Validation requirements

Recommended scenarios cover both list target types, ordered selection, cancellation paths, duplicate Confirm, Loading/Error/Retry, full inventory failure, profile and clone changes, target invalidation, save failure, process/background transitions, position restoration, and accessibility. Results belong in `delivery.md`.

## Related decisions and technical assessments

No new decision is selected. Escalate evidence requiring a consequential state, navigation, persistence, permission, or privacy change.
