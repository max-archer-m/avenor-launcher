# Iteration 20: Cross-Container Application Drag and Two-Axis Auto-Scroll

> Applicable version: [Avenor Launcher 1.3.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Complete unified application movement across vertical lists and favorite bars with long-press drag activation, mutually exclusive exchange/insertion targets, vertical or horizontal edge auto-scroll, and failure-safe persistence.

## Product and version references

- [1.3.0 delivery](delivery.md)
- [Home](../../product/surfaces/home.md)
- [Navigation](../../product/navigation.md)
- [Design foundations](../../product/design-foundations.md)
- [Validation guide](../../validation.md)

## Observable outcome

While Home editing is active, every application, complete-list, and complete-favorite-bar drag requires platform-standard long-press recognition on its handle before a preview can appear. Ordinary horizontal or vertical movement before recognition remains scrolling when available. After recognition, an application can move from any persisted favorite container to another valid persisted or provisional container. The preview remains source-stable, feedback follows the touch point, the active target scrolls on its own axis near an edge, and valid release persists exactly one cross-container exchange or insertion.

## Included work

- Unify drag activation for application handles, complete-list handles, and complete-favorite-bar handles: require the platform-standard long-press threshold, emit exactly one standard long-press semantic haptic feedback when recognized, and create or move the preview only afterward. Release before recognition does nothing; movement before recognition cancels the drag candidate and remains ordinary viewport scrolling when available.
- Use the independent follow-pointer preview, stable source slot, grab offset, source presentation, target presentation after success, and contracted interruption behavior.
- Classify cross-container application bodies as exchange targets and valid boundaries as insertion targets; never show both feedback types.
- Support all vertical-list/favorite-bar source and target combinations, including valid provisional-empty targets.
- Add the contracted two-axis auto-scroll behavior to application dragging both before and after crossing a container boundary. Only the valid container under the touch point may auto-scroll: the source while it remains current, then only the entered target.
- Apply `48dp` trigger zones, delayed start, proximity-based speed, boundary stop, and local edge cues on the target's vertical or horizontal axis.
- Persist one release-time exchange or insertion atomically, preserve unaffected relative order, enforce unique destination, and delete a source container that becomes empty.
- On failure, restore the last successfully persisted favorite state for still-valid identities while retaining newer reliable inventory reconciliation.
- Coordinate Back, Android system Home, invalid release, cancellation, multi-pointer input, inventory interruption, pre-recognition scroll handoff, list/bar scrolling, and existing Home–Drawer gesture boundaries.

## Excluded work

- Same-container exchange semantics, complete-list reorder results, and complete-bar reorder results, which belong to Iterations 17 and 19. This exclusion does not remove Iteration 20 ownership of their shared long-press activation and gesture arbitration, or of auto-scroll integration for same-container application drags.
- New destination types, Drawer drag targets, search, general undo, or changes to the Home–Drawer transition constants.
- Version closure or release work.

## Technical change areas

Application drag coordinator, long-press recognition and semantic haptic feedback, pre-recognition scroll handoff, target geometry, cross-container aggregate transaction, vertical/horizontal auto-scroll, edge cues, gesture ownership, interruption and reconciliation, accessibility implications, performance evidence, and tests.

## Dependencies and sequence

Depends on accepted Iterations 15-19 so every target type, edit session, local container behavior, and persistence invariant exists. Iteration 21 validates the integrated result.

## Migration and compatibility impact

No new migration is selected. Cross-container mutations use the unified aggregate and must not change stable identity or revive confirmed-absent entries.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, external service, dependency, or license impact is selected.

## Risks and unresolved decisions

Long-press arbitration can delay or suppress ordinary scrolling if ownership is taken too early. Two-axis auto-scroll and multiple independent viewports can produce stale target geometry or simultaneous scrolling. Release-time transactions can conflict with inventory updates. Gesture regressions can break the accepted Iteration 14 navigation path. Consequential coordinator or architecture changes require author review and an ADR when applicable.

## Acceptance criteria

- Every application, complete-list, and complete-favorite-bar drag begins only after its handle recognizes the platform-standard long press and produces exactly one standard long-press semantic haptic feedback. Release before recognition does nothing; movement before recognition creates no preview and preserves ordinary scrolling when available.
- A horizontal swipe beginning on either an application handle or the fixed favorite-bar reorder handle scrolls an overflowing favorite bar without starting a drag when long-press recognition has not occurred. Home–Drawer navigation and non-edit-mode scrolling retain their existing activation rules.
- Every list/bar source-target combination supports valid body exchange and first/between/last/provisional-empty insertion without loss or duplication.
- Feedback is mutually exclusive, touch-point based, and recalculated during auto-scroll; the preview never jumps or resizes with target changes.
- During same-container and cross-container application dragging, only the valid container currently under the touch point auto-scrolls on its own axis; leaving the zone or reaching its boundary stops scrolling and the cue.
- Valid release saves one atomic operation; invalid release and interruption remove unsaved feedback without undoing earlier contracted same-container saves.
- Empty source containers are removed atomically and capacity/layout update correctly.
- Save failure retains newer inventory facts and restores only still-valid identities to the last successful favorite state.
- Existing list/bar scrolling and Home–Drawer behavior remain usable without accidental launch, selection, or dead zones.

## Validation requirements

Recommended evidence covers every handle type; release and movement before long-press recognition; exactly one activation haptic; horizontal and vertical scroll handoff; the complete source-target matrix; exchange and every insertion boundary; overflowing/non-overflowing axes; trigger delay, speed, direction changes, target changes, and boundaries; invalid release; Back, Home, cancellation, multi-pointer, inventory and save failure; empty-source cleanup; and regression of Iteration 14 navigation. Results belong in `delivery.md`.

## Related decisions and technical assessments

No new decision is selected. Create an ADR only when implementation evidence establishes a consequential, durable gesture or state architecture choice.
