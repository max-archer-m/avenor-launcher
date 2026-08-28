# Drawer Interaction Specification

> Public semantic source: English. Chinese counterpart: [drawer.zh-CN.md](drawer.zh-CN.md). Shared navigation is defined in [navigation.md](../navigation.md); exact Drawer visual values are defined in the [Drawer presentation specification](../presentation/drawer.md); the spatial sketch is the shared [Drawer wireframe](../wireframes/drawer.txt), with reading rules in the [wireframe index](../low-fidelity-wireframes.md).

## Purpose and layout

Drawer presents every launchable application entry successfully read from the sources exposed to the Launcher by the platform, including cloned entries. System and user-installed applications are treated alike.

“Every launchable application entry” is bounded by Avenor's current Android role and least-privilege permissions. It does not mean every installed package, hidden profile, or entry that could become visible only after adding another sensitive permission.

- The application inventory supports a finite selected items-per-row count and is not restricted to one item per row. The current arrangement contract includes only right-side-name and below-icon-name label placement; it does not imply another named layout mode.
- With inline section anchors, each anchor occupies its own row and scrolls with the application list; it never remains pinned over later content. With left-side anchors, each anchor remains beside its section's entries and scrolls with that content. Left-side anchors are not fixed to the viewport, do not create a separate fixed-width panel, and remain non-interactive. Multiple anchors may appear simultaneously at their respective section positions without replacing or merging. Inline anchors align with the application-name column without adding permanent margin to application rows.
- Right-side-name and below-icon-name arrangements use the exact item geometry defined in the [Drawer presentation specification](../presentation/drawer.md#surface-and-application-content). The selected items-per-row count divides the available application-content width. Names occupy one static line and use end ellipsis.
- The application-size setting is a horizontal large-medium-small single-selection list. Each complete option combines one selection indicator, bundled Android-default generic application-icon preview, and localized label in one interaction target. Exact row, preview, spacing, and target geometry belongs to the Drawer presentation specification. Labels remain within their targets without wrapping or reducing preview sizes.
- Selecting an application immediately launches it and suppresses duplicate rapid activation.
- Long-pressing an application produces long-press haptic feedback and opens the application action sheet.

## Top app bar

- Drawer always reserves one fixed top app bar below the status-bar safe inset. It remains fixed while the application list scrolls, uses the transparent Drawer surface, and does not introduce a visible bar background. Exact geometry belongs to the Drawer presentation specification.
- In ordinary mode, the left side contains the standard Back control. Selecting it completes the same downward Drawer-to-Home transition
  as system Back. The center is an empty, non-interactive slot reserved for a future
  search field; no title, search affordance, placeholder, or unavailable control is
  currently displayed. The right side contains one overflow-style display-settings
  entry in its own interaction target.
- The application list and AlphabetIndex begin below the top app bar and remain clear of
  its controls. The fixed bar height participates in the available-height calculation
  for the index.

## Display settings

- Selecting the ordinary-mode display-settings entry opens one modal Drawer display-settings panel. The modal layer blocks the underlying Drawer; system Back or selecting outside the panel closes it without invoking a separate confirmation action.
- The panel owns three independent semantic settings: application size, application arrangement, and section-anchor presentation. Application size has exactly `Large`, `Medium`, and `Small`; the current default is `Medium`.
- Application arrangement combines a two-option label-placement selector, `Right` or `Below`, with an items-per-row stepper. The current default is `Right` with one item per row, matching the representative default Drawer wireframe. The minimum items-per-row count is one. The supported maximum count, its interaction with each application size, and the disabled boundary behavior of the stepper are `To be decided`; implementation must not infer those product values from available width alone.
- Section-anchor presentation has exactly `Inline` and `Left side`; the current default is `Inline`. Both outcomes retain the scrolling and non-interactive anchor behavior defined above.
- Whether a changed value applies immediately or only when the panel closes, whether it persists across process or device restart, and how a write failure is reported are `To be decided`. Closing the panel never acts as an otherwise undocumented Confirm operation. Until these points are decided, display-setting changes are not an implementation-ready persistence contract and must not be added to the durable-data claims in the Privacy contract.
- Each selector is one accessibility control that exposes its label, complete option names, current selected state, and enabled state. The items-per-row control exposes the current numeric value and separate localized decrement and increment actions; a disabled boundary action is reported as unavailable and does not change the value.
- A selector change is atomic. While its selection animation is active, the display-settings panel ignores additional click activations so two visible selections cannot compete; system Back and outside-panel dismissal remain available.
- Exact panel, setting-row, selector, stepper, animation, and target geometry belongs to the [Drawer presentation specification](../presentation/drawer.md#display-settings).

## Favorite multi-selection mode

- Selecting an edit-mode add control on Home opens the existing Drawer surface in favorite multi-selection mode and captures that control's persisted-list, provisional-list, existing-favorite-bar, or provisional-favorite-bar target. The Drawer completes its upward transition programmatically rather than requiring another user drag.
- The same inventory, locale-aware ordering, application rows, section anchors, and AlphabetIndex are reused. The fixed Settings section and Settings index token are hidden because Settings is not a selectable application target.
- The top app bar replaces its ordinary contents with `Cancel` on the left, a localized description of the captured destination in the center, and `Confirm` on the right. The center text is `Add to list` for a persisted vertical list, `Create list` for a provisional vertical list, `Add to favorite bar` for an existing favorite bar, and `Create favorite bar` for a provisional favorite bar. These labels describe the current action without assigning a persistent title or ordinal to the destination. Confirm is disabled while nothing is selected.
- Selecting an available application toggles its selection instead of launching it. Long-press actions and the application action sheet are disabled in this mode. Every row reserves the presentation-defined leading indicator region so row content does not shift. An available unselected row shows an empty outlined circle; a selected row fills it, displays its one-based plain-text order, and receives the selected-row treatment defined by the Drawer presentation specification. The indicator is not independently interactive; selecting the complete row again deselects it, removes selected treatments, and closes later numbering gaps.
- Selection order is the order in which entries were selected. Deselecting an entry immediately closes the numbering gap, and confirming appends the remaining entries to the captured destination in the displayed number order.
- An identity already assigned to any vertical favorite list or favorite bar remains visible but unavailable for selection. Its empty indicator, icon, and name use the disabled semantic opacity; selecting it has no effect and produces no Toast or other feedback. Primary, cloned, and work-profile entries continue to use their distinct stable identities when this state is evaluated.
- Selecting Confirm saves the complete current selection once, then completes the downward transition and returns to the same Home edit mode under the target-only minimum reveal rule defined by Home. Selecting Cancel, system Back, or a valid downward Drawer dismissal discards the complete unconfirmed selection, completes the downward transition, and returns to Home edit mode with every captured Home container at its exact pre-entry scroll position.
- The Android system Home action also discards the complete unconfirmed selection but exits Home edit mode and resolves directly to normal Home, except that it does not cancel an atomic save already in progress. No cancellation path persists a partial selection, and the downward animation itself never implies confirmation.
- Inventory changes reconcile against stable entry identity without interrupting multi-selection. A newly installed entry appears unselected. A selected entry whose name or icon changes updates in place while retaining its selection number. Temporary profile unavailability or a partial inventory-read failure retains the affected entry and selection rather than treating it as permanently absent. These non-destructive changes show no additional feedback.
- When a refresh confirms that a selected application was uninstalled, a clone was removed, or another launchable identity permanently disappeared, Drawer silently removes that row from the current selection and closes later numbering gaps. An unselected identity confirmed absent simply disappears. Neither case shows a Toast, Snackbar, Dialog, or other explicit notice.
- Confirm revalidates the selection against the latest reliable inventory and silently excludes identities confirmed permanently absent. Remaining valid identities are saved atomically in their displayed order. If none remain, Confirm performs no write and follows the ordinary confirmed downward return to Home edit mode without additional feedback; this race does not introduce a separate validation or disabled-button state. The existing rule that Confirm is disabled while the visible selection is already empty remains unchanged.
- The first valid Confirm activation starts one exclusive atomic save and suppresses duplicate activation. Until it resolves, the visible selection remains in place, Confirm is disabled, and Cancel, row selection, AlphabetIndex input, valid downward dismissal, and system Back do not act. The surface shows no full-screen Loading state, progress indicator, or cleared selection. Inventory changes received during this interval are queued for reconciliation after the save resolves rather than changing the submitted snapshot.
- Save success performs the downward transition exactly once and returns to the originating Home edit mode when it remains active. Save failure retains the current still-valid identities and order and follows the existing retry behavior below: all multi-selection interactions return when reliable inventory remains available; if a full inventory failure is still active, Drawer instead presents the multi-selection Error state with only its defined recovery and cancellation paths. The Android system Home action may background Avenor but does not cancel an in-progress save: success remains persisted, while failure never claims completion and the next Avenor entry presents the last successfully persisted favorite state.
- The complete application row exposes one toggle-selection accessibility node named by the application. Its state reports `Not selected` or `Selected, item N`; renumbering updates that state. An already-favorited row reports `Already in favorites, unavailable for selection` and disabled state. The circular indicator exposes no second focus target or duplicate description. Silently removing a permanently absent selection does not add a long announcement; platform focus recovery may move to the nearest remaining valid row.
- If saving fails, Drawer stays in multi-selection mode, retains the current valid identities and their displayed order, and shows the favorite-save failure Toast defined by the [Home favorite-mutation feedback contract](home.md#favorite-mutation-failure-feedback). When reliable inventory remains available, the user may retry Confirm or use any ordinary cancellation path. When reliable inventory is unavailable, the multi-selection Error state disables Confirm until its Retry restores Content; ordinary cancellation remains available throughout. No partial selection is persisted and Drawer does not return to Home as if the addition succeeded.
- If the captured destination reference becomes invalid before the mutation is evaluated, Drawer discards the complete unconfirmed selection and never redirects it to the leftmost list, another favorite bar, or a newly created substitute destination. A persisted-list or existing-favorite-bar reference becomes invalid when that destination has been removed. A provisional-list or provisional-favorite-bar reference remains a valid creation intent while its originating multi-selection session and Home edit context remain active and the applicable list or favorite bar limit still permits creation; it is not invalid merely because no persisted container exists yet. Its reference becomes invalid if that session is replaced, its Home edit context has ended, or creation is no longer permitted. If the originating Home edit context remains active, Drawer returns to it and shows the favorite-save failure Toast defined by Home. If that context has ended or been replaced, Avenor follows the already-resolved exit or replacement state and does not recreate the old edit mode or disturb a newer selection session.

### Availability states during multi-selection

- Entering favorite multi-selection while inventory is Loading still opens the captured multi-selection context. The top app bar keeps `Cancel` and the destination description, disables Confirm, and the application region uses the existing Loading presentation. Application rows, Settings, and AlphabetIndex remain absent. Cancellation returns to the captured Home edit viewport without creating a destination or changing favorites.
- A full inventory failure after one or more selections preserves those identities and their order only in the current in-memory selection, hides the unreliable application list, disables Confirm, and uses the existing Error presentation and Retry action. Cancel, system Back, and valid downward dismissal remain available and discard the selection normally. The failure neither submits nor clears the selection and does not infer that its identities permanently disappeared.
- Retry performs one read and first returns the application region to Loading. Success restores Content, application anchors, and AlphabetIndex from the latest reliable inventory. Still-valid selections retain their order; confirmed permanently absent identities are silently removed and later numbers close their gaps; temporarily unavailable identities remain selected. Confirm then follows the resulting selection state naturally. Drawer restores the pre-failure anchor and relative position when valid; otherwise it uses the nearest valid position. Recovery adds no success Toast or Snackbar.
- Retry failure returns to the same Error presentation, preserves the in-memory selection, and adds no repeated Toast, Snackbar, Dialog, or stacked error layer. Retry and ordinary cancellation paths remain available. Every Retry is read-only with respect to favorites and starts only one inventory read.
- A reliable inventory in which every launchable identity is already favorited remains ordinary Content rather than Empty or Error. The complete sorted and anchored application list and AlphabetIndex remain available, every row uses the existing already-favorited disabled presentation, and Confirm remains disabled. No dedicated explanatory message is added.
- A reliable inventory with no launchable application entry reuses the existing Error presentation rather than introducing a multi-selection Empty state or dedicated copy. Settings and AlphabetIndex remain absent, Confirm is disabled, and Cancel, the destination description, Retry, system Back, and valid downward dismissal retain their existing behavior.
- An inventory failure received after an exclusive atomic save starts is queued like any other inventory change and does not replace the frozen save surface, cancel the submitted snapshot, or cause another validation pass. Save success completes the normal return and then reconciles the latest inventory. Save failure restores the selection; if reliable inventory is still unavailable, it then enters the multi-selection Error state above. No additional feedback is layered over the existing save-failure or inventory-error feedback.

## Profile and Private Space boundary

- Ordinary, work-profile, and cloned launchable entries follow the existing inventory, identity, sorting, launch, favorite, and refresh rules when Android normally exposes them to Avenor without hidden-profile access.
- The current product does not support Android Private Space and does not declare `ACCESS_HIDDEN_PROFILES`.
- Avenor does not actively query, access, display, deduplicate, sort, launch, favorite, or restore state for Private Space entries that require hidden-profile access.
- Avenor does not provide a separate Private Space container or controls to show, hide, lock, unlock, favorite, or restore Private Space state.
- Absence of Private Space entries under this boundary is expected product behavior, not an incomplete Drawer inventory or loading error.
- Platform exposure of ordinary, work-profile, or cloned entries does not classify those entries as Private Space and does not remove them from the current contract.
- Future Private Space support is a separate product capability. It requires a new project-author decision and renewed review of product interaction, permissions, privacy, compatibility, and validation before it can enter the current contract.

## Grouping and sorting

- Sorting derives a normalized sort form from the complete displayed application name before applying Android's locale-aware collation for the current system locale. The displayed name itself is not changed.
- The normalized sort form uses platform-provided Han-to-Latin transliteration, removes Latin diacritic differences, and ignores case. Han names and names already written with Latin characters therefore participate in one mixed ordering rather than separate script blocks.
- Platform transliteration determines pinyin and polyphonic-character results. Avenor does not ship or maintain an independent pronunciation dictionary, pinyin library, or Launcher-defined character-priority table.
- Section assignment follows the same normalized complete-name sort form rather than inspecting only the first raw Unicode character of the displayed name.
- Entries that the normalized locale-aware sectioning cannot place under Latin A-Z are grouped under `#`; their internal order uses the same complete-name comparator. Avenor does not separately rank numbers, punctuation, Emoji, or other scripts inside `#`.
- Every application participates in the same ordering regardless of whether it is a system, downloaded, primary, or cloned application.
- When displayed names collate equally, identity precedence is primary application, cloned application, then work-profile application.
- When both displayed-name collation and identity category are equal, use a stable launchable-entry identity as the final tie-breaker. The observable order must remain stable across refreshes; the product contract does not prescribe the implementation field or API.
- After every application section, the Drawer list ends with a fixed independent section headed `Settings`. This heading is an anchor in the same list, but it is not an application section and does not participate in application sorting.
- The fixed Settings section contains one clickable row with a Settings gear icon and the name `Settings`. Only selecting this row opens Settings.

## Alphabet index

- A fixed right-side index is visible whenever Drawer presents navigable Content. Full-surface Loading and Error states hide the entire index because those states do not present any list anchors; the index is not partially displayed.
- It contains `#`, each non-empty A–Z section, and a fixed Settings gear below Z. Empty alphabetical sections are omitted.
- Index labels, slots, and Settings gear use the exact values defined by the [Drawer presentation specification](../presentation/drawer.md#section-anchors-and-alphabet-index). The complete slot and index interaction range must not be reduced to the gear artwork, and non-empty entries are not stretched to redistribute unused height.
- The maximum index model contains 28 slots: `#`, A–Z, and Settings. Its minimum complete available height is therefore `560dp` after excluding status-bar, navigation-bar, display-cutout, Drawer-padding, and system-gesture insets.
- At `560dp` or more of available height, the index does not scroll. Below `560dp`, the index becomes an independently scrollable vertical region while the application list remains separately scrollable.
- On initial pointer down over an available index entry, the application list jumps immediately and without animation to that entry's anchor, with the anchor heading positioned at the top of the visible list. This direct positioning does not wait for pointer release and produces index-step haptic feedback.
- After the initial pointer down, entering a different available index entry starts a smooth scroll to that entry's anchor. Moving within the same index slot does not change the list position. A newly selected anchor cancels and replaces an unfinished smooth-scroll target; targets are never queued.
- The final position is the last selected anchor position. On pointer release or cancellation, the current smooth scroll finishes at that anchor; the list does not derive a percentage position from the pointer, snap to another anchor, or play a completion animation.
- A magnified bubble displays only the active character or Settings gear using the Drawer presentation specification. It remains visible while the pointer is held and disappears immediately on release or cancellation.
- While the index owns the pointer, the application list does not independently consume the gesture; index movement may drive the list only through the discrete anchor smooth-scroll behavior above.
- The Settings gear is an index anchor, not a control that opens Settings. Selecting it by initial pointer down jumps immediately to the fixed Settings section; entering it during index movement smooth-scrolls to that section and produces the same index-step haptic feedback as any other anchor change.
- The Settings row in that section is the only Drawer control that opens Settings.
- Returning from Settings preserves the Drawer list position during the same process.
- TalkBack semantics and an accessibility-specific alternate index interaction are outside the current personal-use scope.

## Inventory changes and states

- The inventory updates while Drawer is active when applications or cloned entries are added, removed, enabled, disabled, or renamed.
- Inventory changes observed while an ordinary external application is active are reconciled without presenting a new Loading state on return to Home. When Drawer is next presented, it uses the reconciled inventory and applies the current anchor and position rules.
- If a non-current ordinary, work-profile, or cloned profile read fails while another profile provides usable entries, Drawer presents those available entries as Content rather than blocking the entire surface. Entries from the failed profile may be absent, and the current product does not require a partial-read warning.
- A partial profile failure must not crash Avenor, prevent available entries from launching, or be treated as confirmation that an unavailable entry was permanently removed. A failure that leaves no usable inventory follows the Error state below.
- The intentional absence of Private Space entries under the current product boundary is not an inventory failure.
- A routine live update preserves the current section anchor and the relative scroll position within that anchor whenever the anchor still exists.
- If the current anchor disappears, move to the next existing application anchor in sort order and position its heading at the top of the visible list without pinning it during later scrolling.
- If no later application anchor exists, remain at the bottom of the application list without automatically moving into the Settings section. The Settings anchor is not an application anchor, and an inventory update never opens Settings.
- An update never automatically launches an application or opens an application action sheet or Settings.

### Loading

- While no usable inventory result is available and a read is in progress, the application region shows a progress indicator and the localized message `Loading applications…`.
- Loading is an in-progress state, not an empty or error result. The error icon and Retry action are not shown concurrently with the progress indicator.
- The application list, fixed Settings section, and AlphabetIndex are absent for the full duration of this state.

### Empty or failed result

- A successful read that returns no launchable entries is treated as an error rather than a valid empty Drawer.
- A failed inventory read that leaves no usable launchable entries uses the same error state.
- The application region hides the progress indicator, shows the shared non-interactive status icon, displays the localized message `Unable to load applications`, and provides a separate `Retry` action.
- The error state does not instruct the user to restart Avenor.
- The application list, fixed Settings section, and AlphabetIndex remain absent while this full-surface error is shown.
- Selecting Retry clears the visible error state, returns to the Loading state, and starts a new inventory read.
- While the user remains on the same Error presentation, activity resume, network changes, package callbacks, and other ordinary lifecycle events do not automatically retry or clear the error. Leaving Drawer and later entering it again starts a new initial read.
- A successful retry displays the application list. An empty or failed retry returns to the same error state.

## Acceptance intent

- Ordinary Drawer keeps the transparent top app bar fixed while content scrolls; its Back
  arrow returns through the normal downward transition, its center remains a reserved
  non-interactive search slot, and its right-side display-settings entry opens the
  display-settings dialog.
- Drawer display settings expose only the three defined setting groups and their defined options. Confirmed defaults are Medium application size, Right-side names, one item per row, and Inline section anchors; unresolved limits, application timing, persistence, and failure behavior remain visibly `To be decided` rather than being inferred by implementation.
- Favorite multi-selection preserves selection order visibly, never launches or long-presses an application, never exposes Settings, and saves only the complete selection through Confirm. Every cancellation path discards all unconfirmed entries.
- Every launchable entry returned by a successfully read source within Avenor's current role and least-privilege boundary appears once. Entries from an isolated failed non-current profile may be absent without blocking available Content. Primary, work-profile, and cloned entries follow the applicable platform identity treatment; Private Space entries requiring `ACCESS_HIDDEN_PROFILES` are intentionally absent. Primary and cloned entries remain distinguishable when the platform supplies a badge; Avenor-specific fallback distinction is outside the current scope.
- Initial index contact lands immediately on the intended anchor with its heading positioned at the top of the visible list. Each later available-token change smooth-scrolls to that token's anchor without queuing targets.
- Releasing or cancelling an index gesture leaves the application list at the last selected anchor; it does not preserve an arbitrary pointer-percentage position or snap elsewhere. The heading then scrolls normally with the list.
- Index gestures do not independently scroll the list or trigger application-row actions.
- Live inventory updates do not leave a removed application launchable from stale UI.
- Repeated refreshes with unchanged input preserve application ordering and the current anchor-relative position.
