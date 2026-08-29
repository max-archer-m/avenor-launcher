# Home Interaction Specification

> Public semantic source: English. Chinese counterpart: [home.zh-CN.md](home.zh-CN.md). Shared navigation is defined in [navigation.md](../navigation.md); exact visual values are defined in the [Home presentation specification](../presentation/home.md).

## Purpose and structure

Home is one fixed, non-pageable, non-collapsible Launcher surface. A fixed basic-information region shows time, date, and weekday. Below it, one full-width favorite main list scrolls vertically.

The main list is one ordered, heterogeneous sequence of peer modules. A module is either a vertical favorite module or a horizontal favorite ribbon. Both occupy the complete Home content width and may be added, repeated, and ordered without a product-defined module-count limit. Home has no primary, secondary, side-by-side, folder, or page-based favorite regions.

Only the favorite main list scrolls vertically. A vertical module expands naturally and has no independent vertical scroll position; a ribbon has one row and may scroll horizontally. Remaining space is transparent Home background, not another module.

## Time, date, and weekday

- Time follows the system 12-hour or 24-hour preference and does not show seconds. Date and weekday follow the active locale.
- Selecting time opens an exposed system Clock main surface, then falls back to the system alarm destination. Selecting date invokes an implicit Calendar destination. An unavailable destination produces localized non-blocking feedback.
- Eligible blank space in the full-width information region supports the optional [double-tap lock](../features/double-tap-lock.md). Interactive targets remain excluded.

## Favorite identity and module lifecycle

- A new installation or the one-time adoption of this Home model starts with an empty favorite main list. The former vertical-list and favorite-bar model is not migrated, retained, or run in parallel. This author-accepted favorite reset does not clear Drawer display settings or unrelated local configuration.
- Every favorite belongs to exactly one persisted module. The same stable launchable identity cannot appear more than once across the main list. Primary, cloned, and work-profile entries remain distinct identities.
- Every persisted module contains at least one favorite. A provisional empty module is persisted only after its first valid application is added and is otherwise discarded. Losing the final favorite deletes a persisted module.
- There is no artificial total or per-type module limit. Available unique launchable identities form the natural capacity boundary. When all are favorited, no add flow may claim another module can be completed.
- Names, icons, badges, and launchability resolve from the latest reliable Android inventory; they are not per-favorite visual overrides.

## Vertical favorite modules

- One module-level application size, name placement, and items-per-row value applies to every entry. Individual entries cannot override the module style.
- A new module defaults to medium applications, names on the right, and one item per row.
- The style vocabulary follows the applicable Drawer application-item subset but does not import search, anchors, AlphabetIndex, background, or Settings structure.
- Large, medium, and small use the same arrangement bounds as Drawer: right-side names allow one or two items per row; below-icon names allow one through four. The decrement action is disabled at one and increment is disabled at the applicable maximum. Available width does not increase these bounds.
- Selection launches the entry. Long press produces the platform-standard long-press haptic and opens the application action sheet.

## Horizontal favorite ribbons

- A ribbon is one full-width module with one horizontal row of content-measured entries. It remains stationary when content fits and scrolls horizontally without looping on overflow.
- Its style is fixed: medium application presentation, name on the right, and the presentation-defined content width, maximum, ellipsis, background, boundary, and radius.
- It exposes no row-count, items-per-row, name-position, application-size, border, or width-mode setting. Only members and module order are editable.

## Loading, inventory changes, and failure

- Loading, unreadable, and valid-empty states apply once to the complete favorite main list.
- An unreadable persisted state preserves the unreadable data, disables favorite mutation, offers Retry, and remains distinct from valid empty data without blocking Drawer.
- A favorite is removed automatically only after a successful refresh confirms permanent disappearance. A disabled entry remains stored and provides localized unavailable feedback. An uncertain or failed read does not delete or hide favorites.
- A failed mutation restores the last reliably persisted complete favorite state, does not overwrite newer reliable inventory facts, and provides non-blocking feedback.

## Home edit mode

Edit mode may be entered from the existing favorite action or an eligible blank-space long press. The information region remains unchanged. The main-list viewport shrinks to reserve a fixed, extensible edit dock at the bottom of safe content; modules retain ordinary dimensions.

### Collapsed edit dock and module movement

- The collapsed dock currently exposes only a broad upward affordance. Future dock actions require separate product approval.
- Every module exposes a top-right overlay reorder handle that does not affect layout measurement.
- With the style panel collapsed or expanded, long-pressing the handle produces the platform-standard long-press haptic and begins whole-module movement. Movement before recognition remains main-list scrolling.
- Recognition lifts a complete non-interactive module preview that follows the touch point and immediately removes the source module from list layout without retaining a placeholder. Remaining modules close the vacated space.
- Module sorting is insertion-only and never exchanges two modules. The touch point controls the candidate boundary: entering the first half of a module shows its start-side insertion line; entering the second half shows its end-side insertion line. In an inter-module gap, the nearest adjacent boundary applies.
- Dragging at the main-list start or end edge uses the existing application-drag edge region, delay, and speed behavior to scroll the list. Release performs one atomic save of the final inserted order; cancellation restores the saved order.
- Save failure restores the last successfully saved complete module list, retains Home edit mode, and shows the localized short Toast `Unable to save module order`. The user may retry by dragging again; no Dialog or separate Retry action is added.

### Expanded style panel

- The upward affordance expands an inline bottom style panel inside Home layout and further shortens the main-list viewport. The panel does not cover the list, use a Scrim, or become modal. The main list remains vertically scrollable.
- It opens without requiring or automatically making a selection. Empty Home may open it without a Toast.
- Every module receives a complete selection layer. Whole-module selection, main-list scrolling, module-handle long-press movement, and panel controls remain available. Application launch, application long press, application removal, application dragging, and ribbon horizontal scrolling are blocked.
- With no selection, the panel shows a selection prompt and actions to add a vertical module or ribbon. These actions remain visible and enabled without pre-counting unfavorited inventory; Drawer naturally presents no selectable entries when every available identity is already assigned.
- A selected vertical module exposes a read-only module-type first row, application size, name placement, items per row, module deletion, and both add actions.
- A selected ribbon exposes a read-only module-type first row, module deletion, and both add actions. It has no style controls.
- Current product capability does not provide module naming or renaming.
- Style changes preview immediately and save as one complete module-style state. Failure restores the last saved style and provides non-blocking feedback.
- The down affordance or system Back collapses the panel while retaining edit mode. Exiting edit mode clears selection. Deleting the selected module returns to no selection.

### Adding, removing, and moving applications

- Add actions open Drawer favorite multi-selection with the exact persisted or provisional destination captured. Already-favorited identities are unavailable. The first valid selection completes a provisional module.
- Application-level editing remains available while the style panel is collapsed. Every application exposes separate remove and reorder targets without changing its stored ordinary presentation.
- Application reorder uses long press plus movement and one platform-standard activation haptic. Pre-recognition movement remains ordinary main-list or ribbon scrolling; recognition suppresses application selection and long press.
- Within one module, movement presents a source placeholder and candidate exchange or insertion feedback. A valid release saves the resulting application order atomically. Across modules, a valid release moves the identity to the explicit destination and applies that module's presentation; it never duplicates or silently redirects the identity.
- The main list and an active ribbon may edge-scroll only along their own axes while application movement is active. Candidate destinations, edge zones, and feedback must remain distinguishable from the module-level reorder handle.
- Cancellation restores the last saved application arrangement. Save failure restores the last reliable complete favorite state without overwriting newer reliable inventory facts and provides non-blocking feedback.
- Losing the final application deletes the module. Removal of a complete module requires explicit confirmation; automatic deletion after its last entry disappears does not show a second confirmation.
- The most recent eligible removal may expose one transient Undo action; it is not durable history.

### Inventory changes during edit mode

- Installing or removing an unfavorited application does not end edit mode. A favorite name, icon, or badge change updates in place.
- Confirmed removal of a favorite identity, disabling of a favorite, inability to confirm its identity, or full inventory failure ends edit mode, cancels unfinished movement, preserves already saved changes, and follows the loading and failure rules above.
- Confirmed disappearance removes only that identity; a disabled identity remains saved. An uncertain identity or failed inventory read remains saved.

## Home–Drawer gesture arbitration

- In normal mode, an upward gesture may begin on any Avenor-managed Home element. Vertical movement gives the favorite main list first opportunity to scroll and suppresses the originating click or long press.
- At the main-list end, remaining displacement from the same continuous upward gesture transfers to the Home-to-Drawer transition without a second gesture or discontinuity.
- Ribbon horizontal scrolling wins only after horizontal intent is established. Edit mode does not open Drawer through this gesture.

## Acceptance intent

- Home presents one fixed information region and one full-width ordered main list containing any interleaving of vertical modules and ribbons.
- Modules have no artificial count limit, persisted empty modules do not exist, and every stable identity appears in at most one module.
- Only the main list scrolls vertically; ribbons alone scroll horizontally. One continuous upward gesture can proceed into Drawer at the main-list end.
- Vertical style is uniform per module; ribbon style is fixed. Right-side names allow at most two items per row and below-icon names at most four.
- The non-modal edit panel preserves list visibility and scrolling. Module reorder remains available collapsed or expanded; expanded selection blocks application-level actions.
- Collapsed-panel application editing preserves atomic same-module and cross-module movement, cancellation, failure recovery, and identity uniqueness.
- The new model starts empty without migrating former Home favorites while retaining unrelated local settings.
