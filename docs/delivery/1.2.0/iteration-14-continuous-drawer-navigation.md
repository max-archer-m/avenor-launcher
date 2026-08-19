# Iteration 14: Continuous Drawer Navigation and Anchors

> Applicable version: [Avenor Launcher 1.2.0 Delivery](delivery.md).
>
> This stable contract defines selected delivery scope. Iteration status, actual evidence, commits, artifacts, and results belong only in the sibling version delivery record. This document does not authorize production implementation or any Git or release action.

## Objective

Deliver continuous, predictable direct manipulation between Home and Drawer and stable discrete Drawer AlphabetIndex anchor navigation, without gesture discontinuities, accidental application actions, or conflicts with list and favorite scrolling.

## Product and version references

- [1.2.0 delivery](delivery.md)
- [Navigation](../../product/navigation.md)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Validation guide](../../validation.md)

## Observable outcome

Home–Drawer and Drawer–Home gestures follow one continuous position and opacity path through dragging, direction reversal, nested-scroll handoff, release, completion, rebound, Back, cancellation, and interruption. Drawer index contact selects only available discrete anchors with the contracted immediate-first and smooth-subsequent behavior, stable final target, haptic feedback, and active-token bubble.

## Included work

- Apply the contracted 200dp gesture distance, 1:1.5 interactive Drawer displacement, 120dp/60% release threshold, target-directed fling, reverse-fling, cancellation, and settle behavior.
- Derive Home and Drawer opacity continuously from gesture progress and preserve continuity through reversal, transfer, release, rebound, and completion.
- Permit Home upward-drag entry over every contracted Avenor-managed normal-mode area, including information, favorites, empty, Loading, Error, and Retry regions.
- Transfer remaining displacement from an overflowing touched favorite group to Home–Drawer after that group reaches its end, without lift or replay; arbitrate the two groups independently.
- Transfer remaining downward displacement from the Drawer list at its top boundary into Drawer–Home without lift or replay.
- Preserve edit-mode gesture exclusion, pointer ownership, multi-pointer safety, system-inset boundaries, and suppression of accidental selection, long-press, or haptic actions.
- Apply discrete Drawer AlphabetIndex anchors for `#`, non-empty A–Z sections, and Settings, including immediate initial jump, later replaceable smooth-scroll target, final-anchor stability, index-step haptic feedback, active-token bubble, and independently scrollable index when required.
- Preserve the distinction between the Settings index anchor and the only Settings-opening row.
- Add or update proportionate tests and validation support for the selected behavior.

## Excluded work

- Changing the current gesture distance, displacement ratio, opacity curve, release threshold, or Drawer index product model.
- Percentage-based index scrolling, queued anchor targets, direct Settings opening from the index gear, or pinned section headings.
- Primary/companion favorite composition or persistence changes beyond integration needed to honor the accepted Iteration 13 interaction regions.
- TalkBack-specific alternate index interaction, a Drawer grid, or new navigation surfaces.

## Technical change areas

- Shared Home–Drawer transition state and endpoint settling.
- Nested-scroll coordination for both favorite groups and the Drawer application list.
- Pointer recognition, action suppression, multi-pointer handling, cancellation, and system interruption.
- Drawer list state, section-anchor lookup, AlphabetIndex pointer ownership, smooth-scroll replacement, bubble, and haptics.
- Compose/UI tests, gesture test seams, and focused performance or frame-continuity evidence.

## Dependencies and sequence

Implementation follows accepted Iteration 12 loading/return behavior and accepted Iteration 13 primary/companion Home interaction regions. Loading, Error, Retry, and both favorite groups must be present in the final gesture-arbitration validation. Drawer index anchors can be developed independently inside this iteration only when they do not obscure the required end-to-end navigation result.

## Migration and compatibility impact

No stored favorite or application data migration is selected. Existing meaningful Drawer anchor-relative position preservation and same-process Home favorite-group positions remain governed by the current contracts. Process recreation continues to begin on Home rather than restoring an in-progress transition.

## Security, privacy, permission, and licensing impact

No new permission, network access, external service, dependency, user-data category, or license impact is selected. Android system gestures and reserved insets remain platform-governed.

## Risks and unresolved decisions

- Nested-scroll ownership can create jumps, lost displacement, duplicate actions, or dead zones if transition and list state are not coordinated.
- Animation tuning can hide a contract mismatch without fixing the underlying position/progress model.
- Index smooth-scroll replacement can queue stale targets or leave the wrong final anchor if cancellation semantics are incorrect.
- Performance targets require evidence on the designated primary device; this contract does not prescribe an implementation framework or an unsupported numeric frame-time gate.
- A consequential navigation or gesture architecture change requires author direction and an ADR when applicable.

## Acceptance criteria

- A 100dp target-directed drag produces 50% gesture progress and 150dp interactive Drawer displacement before settling.
- Releasing at 119dp without a qualifying target fling returns to the origin; releasing at 120dp or beyond completes to the target.
- Position and opacity remain continuous through drag, reversal, list-to-transition transfer, release, completion, rebound, cancellation, and Back.
- In normal Home mode, upward drag can begin over every contracted Avenor-managed area without triggering its selection or long-press action; edit mode continues to disable Home–Drawer dragging.
- An overflowing touched favorite group and the Drawer application list each transfer only their unconsumed remaining displacement at the applicable boundary in the same pointer sequence.
- Additional pointers do not cause a jump, reverse the transition, activate an application, or start another transition.
- Initial contact on an available index token jumps immediately to its anchor; entering another token selects one replaceable smooth-scroll target and produces one index-step haptic response.
- Release or cancellation finishes at the last selected anchor, the active-token bubble disappears, and the index does not derive a percentage position or open Settings from its gear.
- Loading and Error states hide the index while retaining the contracted Home-to-Drawer gesture entry behavior.

## Validation requirements

Recommended focused scenarios cover gesture start over time, date, each favorite group, empty, Loading, Error, and Retry; non-overflowing and overflowing groups; Drawer list at nonzero offset and top-boundary transfer; 119dp and 120dp release; target and reverse fling; direction reversal; Back; cancellation; system interruption; multiple pointers; edit mode; rapid application interaction; every available index token; moving across tokens; target replacement; index cancellation; short-height index scrolling; Settings anchor; inventory anchor insertion/removal; and repeated unchanged refresh.

Relevant automated checks, an installable debug build, and author observation of continuity and responsiveness on the designated primary device are recommended. Actual results belong in `delivery.md`; no check is recorded as run by this contract.

## Related decisions and technical assessments

No additional decision or technical assessment is currently required. Add one only if implementation evidence exposes a consequential navigation-state, nested-scroll, animation, accessibility, or architecture decision.
