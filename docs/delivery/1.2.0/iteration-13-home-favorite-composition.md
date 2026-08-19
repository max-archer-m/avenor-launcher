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

The date text uses the contracted logical start inset while retaining its complete touch target. Home displays visually distinct primary and companion groups in their contracted composition; both groups launch and scroll independently. Edit mode supports immediate persistent in-group reorder, cross-group move, and occupied-position swap without losing, duplicating, or overwriting favorites.

## Included work

- Apply the contracted `8dp` logical start inset to visible date-and-weekday text without shrinking its complete `48dp` focusable touch target.
- Present primary favorites in approximately 55% and companion favorites in approximately 45% of the shared composition width.
- Apply each group's contracted icon, target, typography, spacing, empty-space, and independent-scroll behavior.
- Assign new Drawer favorites to primary by default without opening Home or edit mode.
- Enter edit mode through the selected favorite's Launcher action and retain the contracted editing surfaces and Back-only exit.
- Support in-group reorder, cross-group insertion, occupied-position swap, target-group preview, valid-drop feedback, invalid-drop restoration, and target-group edge auto-scroll.
- Persist each completed reorder, move, or swap immediately.
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

- Cross-group drag, independent nested scrolling, and edge auto-scroll can conflict unless pointer ownership is explicit.
- A persistence-format change can cause destructive loss or incorrect identity mapping if migration is not atomic and profile-aware.
- The current product contract selects the observable 55/45 composition but does not prescribe a specific Compose layout architecture.
- Consequential persistence or gesture architecture changes require author direction and an ADR when applicable.

## Acceptance criteria

- Date-and-weekday visible text has the contracted `8dp` logical start inset in both layout directions, and its complete row remains the `48dp` focusable touch target.
- Home presents primary and companion regions with the contracted relative width, visual hierarchy, independent scroll positions, and preserved unused space.
- Both groups launch entries and expose applicable long-press actions.
- Drawer additions append to primary and never silently assign to companion.
- Edit mode supports in-group reorder, cross-group insertion, occupied-position swap, valid target preview, invalid-drop restoration, and edge auto-scroll for overflowing groups.
- Every completed mutation is saved once and survives the applicable reopen, process-recreation, or restart scenario.
- Upgrade from readable existing favorite state preserves identity and order without loss or duplication; unreadable state remains preserved and mutation-disabled.
- Inventory changes and disabled or temporarily unavailable favorites follow the current Home contract.

## Validation requirements

Recommended focused scenarios cover zero favorites; only primary; only companion; both groups; each group independently overflowing; in-group reorder; move in both directions; occupied-position swap; invalid drop; edge auto-scroll; Back exit; process recreation; device restart; readable upgrade; unreadable persistence; install/remove/disable/rename/clone/profile changes; English, Chinese, fallback locale, RTL, and relevant font scaling.

Relevant automated checks, an installable debug build, and author observation on the designated primary device are recommended. Actual results belong in `delivery.md`; no check is recorded as run by this contract.

## Related decisions and technical assessments

- [ADR-0002: Use versioned atomic file for favorites](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)
- [ADR-0003: Model profile completeness for favorite reconciliation](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)

Create additional assessment or decision records only if implementation evidence exposes a consequential migration, identity, persistence, or gesture-architecture choice.
