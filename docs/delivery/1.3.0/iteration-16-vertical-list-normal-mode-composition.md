# Iteration 16: Vertical-List Normal-Mode Composition

> Applicable version: [Avenor Launcher 1.3.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Present the unified vertical favorite lists in normal Home as one full-width or two equal-width, independently sized and independently scrolling lists without exposing edit behavior.

## Product and version references

- [1.3.0 delivery](delivery.md)
- [Home](../../product/surfaces/home.md)
- [Navigation](../../product/navigation.md)
- [Design foundations](../../product/design-foundations.md)
- [Validation guide](../../validation.md)

## Observable outcome

Normal Home accurately renders zero, one, or two persisted vertical lists from the unified aggregate. Each populated list uses its stored large, medium, or small presentation, launches its entries, and scrolls only when its own content overflows.

## Included work

- Replace fixed primary/companion presentation with the contracted equal-status composition.
- Use full width for one list and equal widths with contracted padding and gap for two.
- Apply each list's stored large, medium, or small item geometry and accessibility semantics.
- Preserve independent list extents and meaningful same-process vertical positions; reset to content start after process recreation.
- Present shared Loading, unreadable Error/Retry, and valid empty states once across the favorite composition.
- Preserve launch, long-press action-sheet, disabled identity, badge, inventory reconciliation, and Home–Drawer normal-mode behavior.

## Excluded work

- Edit mode, list controls, provisional lists, targeted Drawer addition, favorite bars, or new drag behavior.
- Changes to current navigation thresholds or Drawer inventory behavior.

## Technical change areas

Normal Home composition, list layout and scroll state, item presentation, accessibility, loading/error/empty integration, and focused UI tests.

## Dependencies and sequence

Depends on accepted Iteration 15 unified persisted state. It establishes the normal-mode surface used by Iteration 17.

## Migration and compatibility impact

No additional format migration is selected. The iteration must render migrated and newly written aggregate state consistently and preserve the `1.2.0` navigation and launch baseline unless this contract selects a change.

## Security, privacy, permission, and licensing impact

No new permission, data category, network access, dependency, or license impact is selected.

## Risks and unresolved decisions

Independent list scrolling can regress the accepted Home–Drawer boundary handoff. Size and font-scale combinations can create clipping or invalid touch targets. Implementation architecture remains owned by development.

## Acceptance criteria

- Zero lists show the contracted empty presentation; one list is full width; two lists divide width equally.
- Each list uses its own persisted size and scrolls independently only when overflowing.
- Entries retain required launch, long-press, badge, disabled, identity, typography, and touch-target behavior.
- Same-process Home round trips preserve meaningful vertical positions; process recreation resets them to start.
- Loading, unreadable state, Retry, and inventory changes do not duplicate the composition or destroy saved data.
- Normal-mode Home–Drawer gestures remain continuous at applicable list boundaries.

## Validation requirements

Recommended scenarios cover zero/one/two lists, all size combinations, overflow and fit, locale directions, font scaling, same-process return, process recreation, inventory changes, Loading/Error/Retry, launch, and navigation handoff. Results belong in `delivery.md`.

## Related decisions and technical assessments

No new decision is selected. Escalate only if evidence requires a consequential layout, navigation, or state-ownership change.
