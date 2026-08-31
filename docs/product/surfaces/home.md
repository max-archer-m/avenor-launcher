# Home Interaction Specification

> Public semantic source: English. Chinese counterpart: [home.zh-CN.md](home.zh-CN.md). Shared navigation is defined in [navigation.md](../navigation.md); exact visual values are defined in the [Home presentation specification](../presentation/home.md).

## Purpose and structure

Home is one fixed, non-pageable, non-collapsible Launcher surface. In normal Home and collapsed-panel editing, a fixed basic-information region shows time, date, and weekday; expanding the inline style panel temporarily removes only that region as defined below. One full-width favorite main list occupies the remaining content area and scrolls vertically.

The main list is one ordered, heterogeneous sequence of peer modules. A module is either a vertical favorite module or a horizontal favorite ribbon. Both occupy the complete Home content width and may be added, repeated, and ordered without a product-defined module-count limit. Home has no primary, secondary, side-by-side, folder, or page-based favorite regions.

Only the favorite main list scrolls vertically. A vertical module expands naturally and has no independent vertical scroll position; a ribbon has one row and may scroll horizontally. Remaining space is transparent Home background, not another module.

## Time, date, and weekday

- Time follows the system 12-hour or 24-hour preference and does not show seconds. Date and weekday follow the active locale.
- Selecting time opens an exposed system Clock main surface, then falls back to the system alarm destination. Selecting date invokes an implicit Calendar destination. An unavailable destination produces localized non-blocking feedback.
- Eligible blank space in the full-width information region supports the optional [double-tap lock](../features/double-tap-lock.md). Interactive targets remain excluded.

## Favorite identity and module lifecycle

- A new installation or the one-time adoption of this Home model starts with an empty favorite main list. The former vertical-list and favorite-bar model is not migrated, retained, or run in parallel. This author-accepted favorite reset does not clear Drawer display settings or unrelated local configuration.
- Every favorite belongs to exactly one persisted module. The same stable launchable identity cannot appear more than once across the main list. Primary, cloned, and work-profile entries remain distinct identities.
- Every module persists one stable one-dimensional application order. A vertical module wraps that order from start to end and then top to bottom according to its current items-per-row value; a ribbon presents the same order from start to end. Changing application size, name placement, or items per row changes presentation only and never rewrites the persisted application order.
- Every persisted module contains at least one favorite. A provisional empty module is persisted only after its first valid application is added and is otherwise discarded. Losing the final favorite deletes a persisted module.
- There is no artificial total or per-type module limit. Available unique launchable identities form the natural capacity boundary. When all are favorited, no add flow may claim another module can be completed.
- Names, icons, badges, and launchability resolve from the latest reliable Android inventory; they are not per-favorite visual overrides.
- In normal Home mode, selecting an application in either module type launches it. Long-pressing it produces the platform-standard long-press haptic and opens the application action sheet.

## Vertical favorite modules

- One module-level application size, name placement, and items-per-row value applies to every entry. Individual entries cannot override the module style.
- A new module defaults to medium applications, names on the right, and one item per row.
- The style vocabulary follows the applicable Drawer application-item subset but does not import search, anchors, AlphabetIndex, background, or Settings structure.
- Large, medium, and small use the same arrangement and transition rules as Drawer. Right-side names allow one or two items per row; below-icon names allow one through four. Decrement is disabled at one; increment is disabled at two for right-side names and four for below-icon names, and neither action crosses its current name-placement boundary. Count changes never change name placement. Switching from right-side to below-icon names preserves one or two and enables increment when the count is two. Switching from below-icon to right-side names preserves one or two but atomically clamps three or four to two and disables increment. The placement change and required clamp preview and save as one complete module-style change. Available width does not increase these bounds.

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

Edit mode may be entered from the existing favorite action or an eligible blank-space long press. While the style panel is collapsed, the information region remains visible and the main-list viewport shrinks to reserve a fixed, extensible edit dock at the bottom of safe content; modules retain ordinary dimensions.

### Collapsed edit dock

- The collapsed dock presents one non-interactive single-line instruction at the physical left and one upward affordance at the physical right. With at least one favorite, the instruction is `Hold and drag apps`; empty Home instead shows `Add favorites`. The instruction has no click action or independent accessibility action. Selecting the upward affordance expands the style panel. No edit-entry Toast or continuous item-wiggle animation is used as a reorder hint.
- While the style panel is collapsed, Home exposes application-level editing, main-list scrolling, and the panel affordance only. Whole-module selection and movement are unavailable, and modules expose no separate module-reorder handle or reserved handle space.

### Expanded style panel and module movement

- The upward affordance atomically removes the basic-information region from layout and expands an inline bottom style panel between the main-list viewport and the still-visible edit dock. The recovered information-region height is returned to the available Home layout before the panel receives its space. The panel does not cover the list, use a Scrim, or become modal, and the main list remains vertically scrollable.
- Expanding or collapsing the panel preserves the main list's logical scroll position, the current module selection, and module order. It neither resets nor automatically scrolls, centers, or reveals another module. Collapsing the panel restores the basic-information region in the same state change as panel removal.
- The expanded panel's dock row changes the physical-left instruction to `Tap to select · hold to move` and replaces the physical-right upward affordance with the downward affordance. Empty Home continues to show `Add favorites`. The instruction remains non-interactive.
- It opens without requiring or automatically making a selection. Empty Home may open it without a Toast.
- Every module receives a complete selection layer and the presentation-defined four-corner edit marker. That layer blocks every application target and gesture within the module. A release after an unconsumed tap selects the complete module; no application launches, opens an action sheet, is removed, or begins application movement. Long-pressing anywhere in the module to the platform threshold produces one platform-standard long-press haptic and begins whole-module movement; prior selection is not required. Before recognition, vertical movement remains main-list scrolling, and any movement that cancels platform long-press recognition does not begin module movement. Ribbon horizontal scrolling is blocked.
- Recognition lifts a complete non-interactive module preview that preserves the pointer's original offset within the module and immediately removes the source module from list layout without retaining a placeholder. Remaining modules close the vacated space.
- Module sorting is insertion-only and never exchanges two modules. The touch point controls the candidate boundary: entering the first half of a module shows its start-side insertion line; entering the second half shows its end-side insertion line. In an inter-module gap, the nearest adjacent boundary applies.
- Module movement uses the shared edge auto-scroll rules below and may scroll only the Home main-list viewport vertically. Release performs one atomic save of the final inserted order; cancellation restores the saved order.
- Save failure restores the last successfully saved complete module list, retains Home edit mode, and shows the localized short Toast `Unable to save module order`. The user may retry by long-pressing a module again; no Dialog or separate Retry action is added.
- With no selection, the panel shows a selection prompt and actions to add a vertical module or ribbon. These actions remain visible and enabled without pre-counting unfavorited inventory; Drawer naturally presents no selectable entries when every available identity is already assigned.
- A selected vertical module exposes a read-only module-type first row, application size, name placement, items per row, module deletion, and both add actions.
- A selected ribbon exposes a read-only module-type first row, module deletion, and both add actions. It has no style controls.
- Current product capability does not provide module naming or renaming.
- Style changes preview immediately and save as one complete module-style state. Failure restores the last saved style and provides non-blocking feedback.
- The downward affordance or system Back collapses the panel, restores the basic-information region, and retains edit mode. Exiting edit mode clears selection. Deleting the selected module returns to no selection.

### Adding, removing, and moving applications

- Add actions open Drawer favorite multi-selection with the exact persisted or provisional destination captured. Already-favorited identities are unavailable. The first valid selection completes a provisional module.
- Application-level editing is available only while the style panel is collapsed. Selecting an application item performs no action. Each item exposes one separate remove target; the rest of its complete item surface is the application-movement target, with no visible reorder handle or reserved handle space.
- Selecting the remove target removes that favorite through the existing atomic mutation and shows the removal Snackbar. The target consumes its complete pointer sequence and cannot launch the application or begin movement. The Snackbar's text, Undo eligibility, replacement, dismissal, and invalidation follow the separately unresolved removal-and-Undo lifecycle.
- Application reorder is insertion-only; it never switches to a separate exchange operation. One movement means removing the source identity from its saved order and inserting it at one target-order boundary, so intervening identities shift naturally.
- Long-pressing the non-remove portion of the complete application item to the platform threshold produces one platform-standard long-press haptic and gives that active pointer exclusive ownership of application movement. Prior to recognition, vertical intent remains main-list scrolling and horizontal intent within a ribbon remains ribbon scrolling; movement that cancels platform long-press recognition does not begin application movement. After recognition, the same sequence cannot select the application, open its action sheet, open Drawer, or start module movement. Candidate-boundary changes produce no repeated haptic.
- A one-item-per-row vertical module divides a candidate item's bounds into top and bottom halves: the top half selects the boundary before the item and the bottom half selects the boundary after it. A multi-item row divides the candidate item's bounds into start-side and end-side halves: the start half selects before and the end half selects after. Current English and Simplified Chinese layouts resolve start to physical left and use left-to-right, then top-to-bottom reading order.
- In a partially filled final row, space after the last real item selects the boundary after that item; it never creates a candidate empty identity or artificial slot. In a gap between two rows, the nearer preceding-row end or following-row start boundary applies, with an exact tie resolving to the following-row start.
- A ribbon uses the same start-half-before and end-half-after rule. Space within the ribbon's valid content region before its first item or after its last item selects the module start or end boundary respectively.
- Entering another module resolves a candidate through that destination module's own rules and visibly identifies both the candidate module and its single insertion boundary. In a gap between modules, the nearest preceding-module end or following-module start boundary applies, with an exact tie resolving to the following-module start. The basic-information region, edit dock, expanded style panel, system-protected area, and every region without candidate feedback are invalid application destinations.
- Recognition retains one source placeholder with the source item's measured size, preventing its source layout from collapsing. The lifted application preview keeps its source presentation. The destination shows only one candidate insertion indicator and does not perform a provisional data mutation or full live reorder. Destination presentation applies only after a valid cross-module release is saved successfully.
- A valid release that changes the semantic order performs one exclusive atomic save. Movement within one module rewrites only that module's application order. Cross-module movement removes the identity from the source and inserts it into the explicit destination in the same mutation; it never duplicates, redirects, or exposes an intermediate persisted state. If the move removes the source module's final application, deletion of that source module belongs to the same atomic mutation.
- Releasing at the semantic source position is a no-change completion: it clears movement without saving, feedback, or another haptic. Releasing in an invalid region or receiving pointer cancellation restores the pre-movement saved arrangement without presenting a save failure.
- While the atomic save is unresolved, another application or module movement cannot begin. Save failure restores the last reliable complete favorite state, including a source module provisionally emptied by the failed move, without overwriting newer reliable inventory facts; Home stays in edit mode and provides non-blocking feedback.
- System Back during active movement cancels that movement and is consumed without also exiting edit mode. The Android system Home action, application backgrounding, lifecycle interruption, or process loss cancels the unpublished movement and then follows the existing navigation or restoration rule from the last saved state.
- Additional pointers never take ownership, change the candidate, or move the preview. Release or cancellation of the owning pointer alone completes or cancels the movement.
- Inventory changes do not turn an uncertain identity into a deletion while movement is active. If a reliable refresh confirms that the moving source identity permanently disappeared, Home cancels movement and then applies the existing confirmed-disappearance reconciliation rule.
- Application movement uses the shared edge auto-scroll rules below. The Home main-list viewport may scroll only vertically, and the active ribbon viewport may scroll only horizontally. Candidate destinations, edge feedback, and application feedback must remain distinguishable from expanded-panel module selection and movement feedback.
- Losing the final application deletes the module. Removal of a complete module requires explicit confirmation; automatic deletion after its last entry disappears does not show a second confirmation.
- The most recent eligible removal may expose one transient Undo action; it is not durable history.

### Edge auto-scroll during movement

- Module and application movement share the edge-band size, activation delay, maximum speed, and linear speed curve defined by the [Home presentation specification](../presentation/home.md). No platform default or earlier implementation value substitutes for that contract.
- The main-list viewport owns vertical edge scrolling. A ribbon owns horizontal edge scrolling only while it is the active application-movement container. Module movement therefore uses only main-list vertical scrolling; application movement uses the axis of its current active main-list or ribbon container. At most one container and one axis may auto-scroll at a time.
- Auto-scroll begins only after the owning pointer remains continuously inside the same container, axis, and direction's edge band for the full activation delay. Moving within that band adjusts speed from pointer proximity without restarting the delay. Leaving the band, changing the active container, axis, or direction, or becoming ineligible stops the current request; a newly eligible request must satisfy the complete delay again.
- While active, speed increases linearly from zero at the edge band's inner boundary to the presentation-defined maximum at the viewport's outer edge. Edge scrolling changes only viewport position; the insertion candidate is recomputed against the resulting geometry and is never committed by scrolling alone.
- Auto-scroll stops immediately when the owning pointer is released or cancelled, movement ends or is interrupted, the relevant content boundary is reached, or the active viewport cannot consume further displacement. Edge scrolling adds no haptic beyond the one long-press haptic that began movement.

### Inventory changes during edit mode

- Installing or removing an unfavorited application does not end edit mode. A favorite name, icon, or badge change updates in place.
- Confirmed removal of a favorite identity, disabling of a favorite, inability to confirm its identity, or full inventory failure ends edit mode, cancels unfinished movement, preserves already saved changes, and follows the loading and failure rules above.
- Confirmed disappearance removes only that identity; a disabled identity remains saved. An uncertain identity or failed inventory read remains saved.

## Home–Drawer gesture arbitration

- In normal mode, an upward gesture may begin on any Avenor-managed Home element. Vertical movement gives the favorite main list first opportunity to scroll and suppresses the originating click or long press.
- At the main-list end, remaining displacement from the same continuous upward gesture transfers to the Home-to-Drawer transition without a second gesture or discontinuity.
- Ribbon horizontal scrolling wins only after horizontal intent is established. Edit mode does not open Drawer through this gesture.

## Acceptance intent

- Home presents one fixed information region and one full-width ordered main list containing any interleaving of vertical modules and ribbons. The information region remains visible in normal Home and collapsed-panel application-level editing, leaves layout only while the inline style panel is expanded, and returns when that panel collapses.
- Modules have no artificial count limit, persisted empty modules do not exist, and every stable identity appears in at most one module.
- Only the main list scrolls vertically; ribbons alone scroll horizontally. One continuous upward gesture can proceed into Drawer at the main-list end.
- Vertical style is uniform per module; ribbon style is fixed. Right-side names allow one or two items per row and below-icon names allow one through four. Count changes never switch placement; changing placement preserves an in-range count and clamps three or four to two only when entering right-side names, with stepper availability updating to the resulting boundary.
- The non-modal edit panel preserves list visibility and scrolling. Its collapsed state exposes only application-level editing; its expanded state blocks application-level actions and allows whole-module selection and movement by tapping or long-pressing the complete module surface without a separate reorder handle.
- Collapsed-panel application editing makes item selection inert, uses one separate remove target, and starts insertion movement by long-pressing the remainder of the complete item without a reorder handle. The same stable insertion-order model applies across single-column, multi-column, ribbon, and cross-module movement; it preserves one source placeholder and one candidate boundary, commits only a changed valid release, and preserves atomicity, cancellation, interruption recovery, failure recovery, and identity uniqueness.
- Module and application movement share deterministic edge scrolling: only the active viewport's own axis moves, activation requires continuous residence through the defined delay, speed follows pointer proximity, and every exit, ownership change, boundary, completion, or interruption stops the current request without another haptic.
- The new model starts empty without migrating former Home favorites while retaining unrelated local settings.
