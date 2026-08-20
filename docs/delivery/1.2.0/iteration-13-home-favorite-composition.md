# Iteration 13: Home Information and Editable Favorite Composition

> Applicable version: [Avenor Launcher 1.2.0 Delivery](delivery.md).
>
> This stable contract defines selected delivery scope. Iteration status, actual evidence, commits, artifacts, and results belong only in the sibling version delivery record. This document does not authorize production implementation or any Git or release action.

## Objective

Deliver the contracted Home basic-information alignment and a complete, directly accessible primary/companion favorite composition with independent scrolling, persistent ordering, and in-group and cross-group editing.

## Product and version references

- [1.2.0 delivery](delivery.md)
- [Home](../../product/surfaces/home.md)
- [Navigation](../../product/navigation.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Design foundations](../../product/design-foundations.md)
- [Validation guide](../../validation.md)

## Observable outcome

The date text uses the contracted logical start inset while retaining its complete touch target. Home displays visually distinct primary and companion groups in their contracted composition; both groups launch and scroll independently. Primary items use `40dp` icons in `56dp` rows, companion items use `32dp` icons in `48dp` rows, and the groups use a `16dp` inter-group gap. Edit mode preserves those item heights and supports persistent in-group exchange, cross-group insertion, and cross-group exchange without losing, duplicating, or overwriting favorites.

## Included work

- Apply the contracted `8dp` logical start inset to visible date-and-weekday text without shrinking its complete `48dp` focusable touch target.
- Present primary favorites in approximately 55% and companion favorites in approximately 45% of the shared composition width after the fixed `8dp` side padding and `16dp` inter-group gap are removed.
- Keep normal-mode and edit-mode item heights identical. Use the same `24dp` drag-handle graphic for both groups; each handle has a `48dp`-wide hit region made from the graphic plus `12dp` horizontal padding on each side, and its hit-region height follows the current item height. The graphic has no additional right margin; visible spacing comes from the existing item padding. Handles must remain inside the existing rows and must not stretch or compress either list.
- Apply each group's contracted icon, target, typography, spacing, empty-space, and independent-scroll behavior.
- Assign new Drawer favorites to primary by default without opening Home or edit mode.
- Enter edit mode through the selected favorite's Launcher action and retain the contracted editing surfaces and Back-only exit.
- Support an independent `50%`-opacity full-safe-area follow-pointer preview with a fixed grab offset, stable source position, source-row-height preservation, target feedback, invalid-drop restoration, and target-group edge auto-scroll. The preview may pass through all Home safe content, but only primary and companion favorite regions accept drops. The dragged favorite does not participate in list-position animation, and no target change or other-item animation may move, jump, or rebound it.
- Define in-group dragging as exchange-only: an occupied favorite body is the only valid target, its favorite moves to the source position, and its former position becomes the dragged favorite's release position. Same-group gaps never show an insertion line and never accept insertion. Touching the dragged favorite, the source position, or an invalid area produces no new exchange.
- Define cross-group dragging as two mutually exclusive operations. An occupied target body performs real-time visible cross-group exchange: the target and source favorite exchange positions and group assignments as the touch point enters the body, and both group counts remain unchanged. A target gap performs cross-group insertion: an insertion line identifies the target boundary, no list insertion occurs before release, the source favorite is removed on release, and the target group's relative order is preserved. First, between-item, last, and empty-group boundaries are valid insertion targets.
- Classify all targets from the finger touch point, not the preview center or edge. Switching between an occupied body and a gap switches exchange feedback and insertion-line feedback without modifying saved data. Recalculate feedback during edge auto-scroll against current target-group geometry, without unexplained target jumps.
- Do not persist the current favorite operation during dragging. In-group and cross-group exchange update the visible target positions as the touch point enters eligible bodies; cross-group insertion remains feedback-only. Finalize the current exchange or insertion once on release over the corresponding valid target. The stable source position is not a second interactive copy. An occupied target may use a synchronized confirmation animation, but the independent preview does not join that animation.
- Persist each completed in-group exchange, cross-group insertion, or cross-group exchange immediately. If persistence fails, restore the last successfully saved group assignments and order only for still-valid favorite identities, retain newer confirmed inventory reconciliation, do not revive removed or invalid identities, do not present the operation as completed, and show the existing favorite-update failure feedback.
- Preserve identity, order, disabled/unavailable behavior, inventory reconciliation, and the shared loading/error/retry composition.
- Add or update proportionate tests and validation support for the selected behavior.

## Excluded work

- Home–Drawer transition tuning, continuous nested-scroll handoff, release thresholds, settle behavior, and Drawer index-anchor interaction.
- A product-defined favorite capacity, automatic companion assignment, automatic sorting, folders, undo, export, repair, reset, backup, or restore.
- Bottom secondary-region content.
- A change to double-tap-lock authorization or build integration.

## Technical change areas

- Home layout, semantics, touch targets, and edit-mode presentation.
- Favorite domain state, group assignment, ordering, persistence serialization, and migration.
- Independent group scroll state and drag/drop gesture ownership.
- Drawer add-favorite path and inventory-driven favorite reconciliation.
- Compose/UI tests, persistence tests or seams, and focused device journeys.

## Dependencies and sequence

Implementation follows accepted Iteration 12 loading and return semantics. This iteration must complete before Iteration 14 finalizes Home gesture arbitration over both favorite groups. Date alignment may be implemented as a focused commit within this iteration, but it remains part of the same iteration acceptance result.

## Migration and compatibility impact

Readable favorites from `1.0.0` and `1.1.0` must retain launchable identity and existing order. Existing favorites without a companion-group assignment enter primary in their existing order unless implementation evidence requires a different author-approved migration. Migration must be versioned and atomic where applicable. Unreadable or damaged state is not empty state and must not be migrated, repaired, cleared, or overwritten.

Downgrade behavior is not added. The accepted `1.2.0` artifact must support in-place upgrade under release governance and the existing signing identity.

## Security, privacy, permission, and licensing impact

No new permission, network access, external service, dependency, user-data category, or license impact is selected. Favorite group and order remain local Avenor-owned state under the current privacy and backup-exclusion boundary.

## Risks and unresolved decisions

- Cross-group drag, independent nested scrolling, and edge auto-scroll can conflict unless pointer ownership is explicit. A full-safe-area preview must remain separate from the narrower drop-target regions.
- A persistence-format change can cause destructive loss or incorrect identity mapping if migration is not atomic and profile-aware.
- The current product contract selects the observable 55/45 composition but does not prescribe a specific Compose layout architecture.
- Consequential persistence or gesture architecture changes require author direction and an ADR when applicable.

## Acceptance criteria

- Date-and-weekday visible text has the contracted `8dp` logical start inset in both layout directions, and its complete row remains the `48dp` focusable touch target.
- Home presents primary and companion regions with the contracted relative width after spacing subtraction, shared viewport height, independent scroll positions, preserved unused space, and the contracted normal/edit item heights.
- Both groups launch entries and expose applicable long-press actions.
- Drawer additions append to primary and never silently assign to companion.
- Edit mode supports source-stable independent preview behavior, real-time visible in-group exchange, real-time visible cross-group exchange, release-committed cross-group insertion, mutually exclusive touch-point target feedback, first/between/last/empty-group insertion boundaries, invalid-drop restoration, and edge auto-scroll for overflowing groups. The preview does not change row or viewport height, and release finalizes and saves the current operation.
- Back during an active drag cancels that drag, restores the last saved visible state, removes all drag feedback, and prevents the gesture's later release from saving.
- Every completed mutation is saved once and survives the applicable reopen, process-recreation, or restart scenario.
- A failed favorite save restores only still-valid favorite identities to their last saved order and retains any newer confirmed inventory reconciliation; failure recovery never revives an application, clone, or launchable identity confirmed as removed or invalid.
- Upgrade from readable existing favorite state preserves identity and order without loss or duplication; unreadable state remains preserved and mutation-disabled.
- Inventory changes and disabled or temporarily unavailable favorites follow the current Home contract.

## Validation requirements

Focused drag/drop validation includes:

1. Start a handle drag and verify the preview follows continuously with the initial grab offset.
2. Verify the source favorite remains stable, preserves its row height, does not create a second interactive copy, and does not cause list shrink or viewport change.
3. Within one group, drag onto the first, middle, and last favorite and verify the target exchanges visibly with the source as the touch point enters; drag through gaps and verify no insertion line or insertion operation appears.
4. Within one group, touch the dragged favorite, the source position, and invalid Home space and verify no new exchange is selected.
5. Exchange across groups using the target group's first, middle, and last favorite, verifying that the target exchanges visibly with the source as the touch point enters, while the example-style group counts and source/target positions remain correct.
6. Insert across groups before the first item, between items, and after the last item; verify the insertion line and preserved target relative order.
7. Insert into an empty target group and verify the empty-group boundary feedback.
8. Move from an exchange body to an insertion gap and back; verify the visible exchange updates only over bodies, insertion remains feedback-only over gaps, feedback is mutually exclusive, and persistence occurs only on release.
9. Move through the full route: source-group exchange -> inter-group space -> target-group insertion -> target-body exchange -> return to the source group.
10. Trigger top or bottom edge auto-scroll and verify feedback follows the touch point and current target geometry without an unexplained jump.
11. Release over an invalid area and verify the original group/order return with persisted data unchanged.
12. Start from the upper, middle, and lower part of the handle and verify the preview offset changes while touch-point target and release result remain consistent.
13. Press Back during an active exchange and during an active insertion target; verify the preview and feedback disappear, the last saved state returns, and the gesture's later release cannot save.
14. Cause favorite persistence to fail while a newer inventory result confirms a favorite identity as removed or invalid; verify rollback preserves that inventory result and does not revive the identity.

Additional focused scenarios cover zero favorites; only primary; only companion; both groups; equal normal/edit row heights; each group independently overflowing; full-safe-area preview; Back exit; process recreation; device restart; readable upgrade; unreadable persistence; install/remove/disable/rename/clone/profile changes; English, Chinese, fallback locale, RTL, and relevant font scaling.

Relevant automated checks, an installable debug build, and author observation on the designated primary device are recommended. Actual results belong in `delivery.md`; no check is recorded as run by this contract.

## Related decisions and technical assessments

- [ADR-0002: Use versioned atomic file for favorites](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)
- [ADR-0003: Model profile completeness for favorite reconciliation](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)

Create additional assessment or decision records only if implementation evidence exposes a consequential migration, identity, persistence, or gesture-architecture choice.
