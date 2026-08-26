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

Normal Home conditionally presents up to five untitled fixed-medium horizontal favorite bars whose entries contract with shorter labels up to the contracted maximum width. In edit mode the author sees destination-specific plain-text add actions and the compact shared vertical-list control bar, can create a bar through the targeted Drawer flow, add to an existing destination, remove applications or complete bars, reorder complete bars, exchange applications within one bar, scroll overflow, and use contracted Undo.

## Included work

- Present zero to five `56dp`-high favorite bars below the favorite-list area with the shared Home geometry: uniform `8dp` content padding inside `safeDrawing`, `8dp` adjacent-module spacing, and no additional padding in favorite-bar containers or their items. The favorite-list and secondary-favorites areas use the complete padded Home content width rather than the raw safe width. Each fixed-medium-height item is content-measured up to `128dp` from an `8dp` start inset, `40dp` icon, `8dp` icon-to-name gap, label measured up to `64dp`, and `8dp` trailing inset. Short labels shrink the item and longer labels use end ellipsis. The application handle retains its physical-right-edge `40dp`-wide, full-item-height target without an end margin. Normal and edit modes preserve calculated item widths and those boundaries; editing surfaces, outlines, and fixed rails add no extra outer inset, while edit controls may cause earlier ellipsis. Preserve the remaining contracted overflow-fade, launch, long-press, identity, badge, and accessibility behavior.
- Refine the shared vertical-list edit control bar to the contracted compact presentation: a `40dp` bar, internal `36dp` whole-bar surface, following `4dp` content gap, physical left/center/right `36dp × 40dp` targets, solid error-red `32dp` remove treatment with a `20dp` white X, and `20dp` reorder artwork. Do not add speculative control positions.
- Preserve each bar's meaningful same-process horizontal position and reset it after process recreation; apply captured restoration and target-only reveal after Drawer multi-selection.
- Add existing and provisional favorite-bar targets to the Iteration 18 multi-selection mechanism; create a bar only from at least one successfully saved valid identity.
- Present fixed edit rails, remove action, conditional complete-bar reorder action, and the applicable plain-text add actions. Existing lists and favorite bars use `＋ Add apps`; provisional list and favorite-bar targets name the container they create. The leading plus is text rather than a separate icon asset, and each complete localized label remains one target at least `48dp` high. Present the provisional bar when capacity permits.
- Remove applications, exchange applications within one bar, remove complete bars, reorder complete bars, and delete a bar when its final entry disappears.
- Apply latest-only Undo and capacity reservation to removed applications and complete bars, including invalidation by a successful conflicting addition.
- Preserve the five-bar limit and suppress provisional creation while full or while Undo reserves the only place.

## Excluded work

- Favorite-bar titles, naming, renaming, selectable sizes, or configurable maximum count.
- Cross-container application insertion/exchange and application-drag automatic scrolling.
- Drawer search or general undo history.

## Technical change areas

Secondary-favorites layout and item measurement, horizontal scroll state, favorite-bar controls, shared add-action presentation, vertical-list control-bar presentation, provisional creation, targeted Drawer reuse, same-bar item exchange, complete-bar reorder, capacity/Undo state, accessibility, and tests.

## Dependencies and sequence

Depends on Iteration 18 targeted multi-selection, Iteration 17 edit-session behavior, and Iteration 15 aggregate support. It establishes every container type required by Iteration 20.

## Migration and compatibility impact

No additional migration is selected. Favorite bars are new unified destinations. Their meaningful positions are transient UI state; membership and order are durable. Empty bars are never persisted.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, external service, dependency, or license impact is selected.

## Risks and unresolved decisions

Five independent horizontal states can conflict with Home edit and Drawer restoration. Fixed rails must not change persisted application geometry. Undo reservation, provisional creation, automatic empty cleanup, and inventory reconciliation require one capacity owner.

## Acceptance criteria

- No bar reserves normal-mode height; one to five bars use uniform `8dp` Home content padding inside `safeDrawing`, the complete resulting padded content width, contracted `8dp` adjacent-module spacing, `56dp` bar height, zero additional container/item padding, application-handle target geometry, item geometry, overflow, launch, and long-press behavior without acquiring another edit-mode inset.
- The basic-information geometry preserves its prior final safe-edge relationship through explicit composition: Home's `8dp` content padding plus each information row's `8dp` top/start/end margin yields `16dp` from the applicable safe edges; the date's additional `8dp` text inset places its visible text `24dp` from the safe start edge while the complete row target remains at `16dp`.
- Every favorite-bar application item is content-measured from the contracted `8dp + 40dp + 8dp + label + 8dp` composition, caps rendered label width at `64dp` and total item width at `128dp`, shrinks for shorter labels, uses end ellipsis for longer labels, retains `8dp` between adjacent items, and does not expand in edit mode.
- Persisted lists and existing favorite bars visibly use the localized `＋ Add apps` action, while provisional list and favorite-bar targets visibly name the container they create. The leading plus uses text rather than a separate icon asset; each complete label remains one localized interaction and accessibility target at least `48dp` high, and selecting it captures the correct destination without changing creation semantics.
- Every persisted vertical list uses the contracted `40dp` control bar, internal `36dp` whole-bar surface, following `4dp` content gap, and physical left/center/right `36dp × 40dp` targets. Remove uses a solid error-red `32dp` rounded square with a `20dp` white X, reorder uses `20dp` artwork, size and reorder have no persistent individual background, one list hides reorder while preserving its position, and the complete `44dp` reservation changes only the edit-mode application viewport rather than the favorite area's external height or persisted state.
- Same-process, edit-exit, system Home, and external-return positions follow navigation rules; process recreation resets to start.
- Existing and provisional targets use the Iteration 18 flow without duplicating its state machine.
- A bar is created atomically only with valid confirmed entries; no persisted empty bar exists.
- Removal, same-bar exchange, complete-bar reorder, final-entry cleanup, failure recovery, and latest Undo preserve identity, membership, and order.
- One bar hides its reorder interaction; two or more expose it without changing rail geometry.
- Capacity never exceeds five, including an active Undo reservation.

## Validation requirements

Recommended scenarios cover zero through five bars, short and ellipsized entry labels, fit/overflow, horizontal restoration, all four localized add-action contexts, compact vertical-list controls, one-list hidden reorder, existing/provisional additions, all cancellation and failure paths, one/multiple bar controls, same-bar exchange, removal/Undo, inventory reconciliation, font scale, locale direction, accessibility, and process recreation. Results belong in `delivery.md`.

## Related decisions and technical assessments

No new decision is selected. Escalate consequential state, persistence, layout, gesture, or accessibility architecture evidence.
