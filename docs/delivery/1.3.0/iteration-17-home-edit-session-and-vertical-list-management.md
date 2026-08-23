# Iteration 17: Home Edit Session and Vertical-List Management

> Applicable version: [Avenor Launcher 1.3.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Deliver one coherent Home edit session in which the author can manage persisted vertical lists and their existing favorites, while preparing the internal target seam for the later Drawer-backed new-list flow without exposing an incomplete user action.

## Product and version references

- [1.3.0 delivery](delivery.md)
- [Home](../../product/surfaces/home.md)
- [Navigation](../../product/navigation.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Design foundations](../../product/design-foundations.md)

## Observable outcome

The author can enter and exit edit mode through every contracted path, change a list's size, remove or reorder complete lists, remove or reorder applications within one list, and use the latest-removal Undo behavior without corrupting saved or reconciled state.

## Included work

- Enter edit mode from the action-sheet Edit action and eligible basic-information blank-space long press; apply contracted editing surfaces and gesture exclusion.
- Implement Back, Android system Home, inventory-event, process-recreation, and ordinary edit-exit behavior with meaningful vertical-position preservation.
- Present list-level remove, size, and conditional reorder controls with their Dialog, menu, press, haptic, preview, and accessibility behavior.
- Remove existing favorites and exchange applications within the same vertical list, persisting at the contracted points.
- Reorder complete vertical lists and update the leftmost default Drawer destination.
- Apply latest-only removal Undo, including exact restoration, capacity reservation, replacement, dismissal, invalidation by a successful conflicting addition, and failure recovery.
- Prepare the internal destination and session seam required by the later provisional-list flow without presenting its provisional list or add control in the accepted Iteration 17 surface. Iteration 18 owns their first user-visible availability together with the complete Drawer selection and list-creation result.

## Excluded work

- Presenting or activating a provisional-list add control, completing Drawer multi-selection, or persisting a new provisional list.
- Favorite-bar presentation or management.
- Cross-container application insertion/exchange or two-axis automatic scrolling.

## Technical change areas

Home edit-session state, list controls, same-list application movement, complete-list movement, Dialog/menu/Snackbar state, Undo snapshot, capacity accounting, scroll restoration, accessibility, and tests.

## Dependencies and sequence

Depends on Iteration 16 normal composition and Iteration 15 persistence. Iteration 18 completes the add-control handoff; Iteration 19 later extends the same edit session to favorite bars.

## Migration and compatibility impact

No new migration is selected. Every successful mutation must use the unified aggregate and retain the latest reliable inventory facts. Process recreation discards edit mode and transient Undo.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, external service, dependency, or license impact is selected.

## Risks and unresolved decisions

Undo capacity reservation and provisional entries can exceed limits if owned by separate state sources. Immediate same-list exchanges must not be undone by invalid release. Modal controls and drag ownership can leak actions if session arbitration is incomplete.

## Acceptance criteria

- Every contracted entry and exit path produces the correct normal/edit state and position lifecycle.
- Size changes, list removal, complete-list reorder, item removal, and same-list exchange persist once and recover to the last successful state on failure.
- One list hides its complete-list reorder action; two lists expose it and reorder physical positions.
- Latest Undo restores exact identity, destination, position, list size, membership, and container order where applicable.
- A successful conflicting addition invalidates Undo; failed or cancelled addition does not.
- An Undo snapshot that would recreate a list reserves the applicable capacity and suppresses a conflicting provisional list.
- No provisional-list add control or other unavailable creation action is exposed before Iteration 18 completes its user journey.

## Validation requirements

Recommended scenarios cover every entry/exit path, zero/one/two lists, size menu, removal Dialog, list and same-list movement, save failure, latest-only Undo lifecycle, reserved capacity, inventory interruption, accessibility, and scroll restoration. Results belong in `delivery.md`.

## Related decisions and technical assessments

No new decision is selected. Escalate consequential state-ownership, persistence, or gesture architecture evidence.
