# Iteration 19: Favorite-Bar Presentation, Creation, and Management

> Applicable version: [Avenor Launcher 1.3.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Deliver the complete favorite-bar lifecycle below the vertical lists, including presentation, targeted creation, local management, restoration, and failure recovery, without yet adding cross-container application movement.

## Product and version references

- [1.3.0 delivery](delivery.md)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Navigation](../../product/navigation.md)
- [Design foundations](../../product/design-foundations.md)

## Observable outcome

Normal Home conditionally presents up to five untitled fixed-medium horizontal favorite bars. In edit mode the author can create a bar through the targeted Drawer flow, add to an existing bar, remove applications or complete bars, reorder complete bars, exchange applications within one bar, scroll overflow, and use contracted Undo.

## Included work

- Present zero to five `56dp`-high favorite bars below the favorite-list area with the shared Home geometry: `8dp` adjacent-module spacing and no uniform Home outer padding, with no padding in the favorite-bar containers or their items. Item internals define their own placement, including a `16dp` icon start margin and the application handle's physical-right-edge `40dp`-wide, full-item-height target without an end margin. Normal and edit modes preserve those boundaries; editing surfaces, outlines, and fixed rails add no extra outer inset. Preserve the remaining contracted fixed-medium item, overflow-fade, launch, long-press, identity, badge, and accessibility behavior.
- Preserve each bar's meaningful same-process horizontal position and reset it after process recreation; apply captured restoration and target-only reveal after Drawer multi-selection.
- Add existing and provisional favorite-bar targets to the Iteration 18 multi-selection mechanism; create a bar only from at least one successfully saved valid identity.
- Present fixed edit rails, remove action, conditional complete-bar reorder action, add control, and provisional bar when capacity permits.
- Remove applications, exchange applications within one bar, remove complete bars, reorder complete bars, and delete a bar when its final entry disappears.
- Apply latest-only Undo and capacity reservation to removed applications and complete bars, including invalidation by a successful conflicting addition.
- Preserve the five-bar limit and suppress provisional creation while full or while Undo reserves the only place.

## Excluded work

- Favorite-bar titles, naming, renaming, selectable sizes, or configurable maximum count.
- Cross-container application insertion/exchange and application-drag automatic scrolling.
- Drawer search or general undo history.

## Technical change areas

Secondary-favorites layout, horizontal scroll state, favorite-bar controls, provisional creation, targeted Drawer reuse, same-bar item exchange, complete-bar reorder, capacity/Undo state, accessibility, and tests.

## Dependencies and sequence

Depends on Iteration 18 targeted multi-selection, Iteration 17 edit-session behavior, and Iteration 15 aggregate support. It establishes every container type required by Iteration 20.

## Migration and compatibility impact

No additional migration is selected. Favorite bars are new unified destinations. Their meaningful positions are transient UI state; membership and order are durable. Empty bars are never persisted.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, external service, dependency, or license impact is selected.

## Risks and unresolved decisions

Five independent horizontal states can conflict with Home edit and Drawer restoration. Fixed rails must not change persisted application geometry. Undo reservation, provisional creation, automatic empty cleanup, and inventory reconciliation require one capacity owner.

## Acceptance criteria

- No bar reserves normal-mode height; one to five bars use the contracted `8dp` adjacent-module spacing and no uniform Home outer padding, `56dp` bar height, zero container/item padding, application-handle target geometry, item geometry, overflow, launch, and long-press behavior without acquiring an additional edit-mode inset.
- Same-process, edit-exit, system Home, and external-return positions follow navigation rules; process recreation resets to start.
- Existing and provisional targets use the Iteration 18 flow without duplicating its state machine.
- A bar is created atomically only with valid confirmed entries; no persisted empty bar exists.
- Removal, same-bar exchange, complete-bar reorder, final-entry cleanup, failure recovery, and latest Undo preserve identity, membership, and order.
- One bar hides its reorder interaction; two or more expose it without changing rail geometry.
- Capacity never exceeds five, including an active Undo reservation.

## Validation requirements

Recommended scenarios cover zero through five bars, fit/overflow, horizontal restoration, existing/provisional additions, all cancellation and failure paths, one/multiple bar controls, same-bar exchange, removal/Undo, inventory reconciliation, font scale, locale direction, accessibility, and process recreation. Results belong in `delivery.md`.

## Related decisions and technical assessments

No new decision is selected. Escalate consequential state, persistence, layout, gesture, or accessibility architecture evidence.
