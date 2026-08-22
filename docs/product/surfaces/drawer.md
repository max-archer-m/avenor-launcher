# Drawer Interaction Specification

> Public semantic source: English. Chinese counterpart: [drawer.zh-CN.md](drawer.zh-CN.md). Shared navigation is defined in [navigation.md](../navigation.md); the spatial sketch is the shared [Drawer wireframe](../wireframes/drawer.txt), with reading rules in the [wireframe index](../low-fidelity-wireframes.md).

## Purpose and layout

Drawer presents every launchable application entry successfully read from the sources exposed to the Launcher by the platform, including cloned entries. System and user-installed applications are treated alike.

“Every launchable application entry” is bounded by Avenor's current Android role and least-privilege permissions. It does not mean every installed package, hidden profile, or entry that could become visible only after adding another sensitive permission.

- The application inventory is a single-column list; a future grid is an additive capability and is outside the current contract.
- Each section anchor occupies its own `32dp`-high row and scrolls with the application list; section anchors never remain pinned over later content. Its `14sp/20sp` medium-weight label uses a `56dp` start inset from the application-row content boundary so it aligns with the application-name column after the `40dp` icon and `16dp` icon-to-name gap. The inset applies only to the anchor label and does not add permanent margin to application rows.
- Each application row is at least `56dp` high and displays a `40dp` platform icon, platform-provided badge when present, and application name, with `16dp` between icon and name.
- An application name occupies exactly one static line. A name that does not fit uses end ellipsis as defined in [design-foundations.md](../design-foundations.md).
- Selecting an application immediately launches it and suppresses duplicate rapid activation.
- Long-pressing an application produces long-press haptic feedback and opens the application action sheet.

## Top app bar

- Drawer always reserves one fixed `56dp`-high top app bar below the status-bar safe inset. It remains fixed while the application list scrolls, uses the transparent Drawer surface, and does not introduce a visible bar background.
- In ordinary mode, the left side contains a standard `24dp` Back arrow in a `48dp` interaction target. Selecting it completes the same downward Drawer-to-Home transition as system Back. The center is an empty, non-interactive slot reserved for a future search field; no title, search affordance, placeholder, or unavailable control is currently displayed. The right side is visually empty and has no interaction target.
- The application list and AlphabetIndex begin below the top app bar and remain clear of its controls. The fixed bar height participates in the available-height calculation for the index.

## Favorite multi-selection mode

- Selecting an edit-mode add control on Home opens the existing Drawer surface in favorite multi-selection mode and captures that control's persisted-list, provisional-list, existing-ribbon, or provisional-ribbon target. The Drawer completes its upward transition programmatically rather than requiring another user drag.
- The same inventory, locale-aware ordering, application rows, section anchors, and AlphabetIndex are reused. The fixed Settings section and Settings index token are hidden because Settings is not a selectable application target.
- The top app bar replaces its ordinary contents with `Cancel` on the left, a localized description of the captured destination in the center, and `Confirm` on the right. Confirm is disabled while nothing is selected.
- Selecting an available application toggles its selection instead of launching it. Long-press actions and the application action sheet are disabled in this mode. Every row reserves the same leading selection-order region to the left of the application icon so row content does not shift; a selected row displays its one-based selection number there.
- Selection order is the order in which entries were selected. Deselecting an entry immediately closes the numbering gap, and confirming appends the remaining entries to the captured destination in the displayed number order.
- An identity already assigned to any vertical favorite list or secondary ribbon remains visible but unavailable for selection. Primary, cloned, and work-profile entries continue to use their distinct stable identities when this state is evaluated.
- Selecting Confirm saves the complete current selection once, then completes the downward transition and returns to the same Home edit mode. Selecting Cancel, system Back, or a valid downward Drawer dismissal discards the complete unconfirmed selection, completes the downward transition, and returns to Home edit mode.
- The Android system Home action also discards the complete unconfirmed selection but exits Home edit mode and resolves directly to normal Home. No cancellation path persists a partial selection, and the downward animation itself never implies confirmation.

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
- Index labels use `11sp` medium-weight text in fixed `20dp` slots. The Settings gear graphic is `11dp` inside one complete `20dp` index slot; its slot and index interaction range must not be reduced to the graphic bounds. Non-empty entries are not stretched to redistribute unused height.
- The maximum index model contains 28 slots: `#`, A–Z, and Settings. Its minimum complete available height is therefore `560dp`, calculated as `28 × 20dp` after excluding status-bar, navigation-bar, display-cutout, Drawer-padding, and system-gesture insets.
- At `560dp` or more of available height, the index does not scroll. Below `560dp`, the index becomes an independently scrollable vertical region while the application list remains separately scrollable.
- On initial pointer down over an available index entry, the application list jumps immediately and without animation to that entry's anchor, with the anchor heading positioned at the top of the visible list. This direct positioning does not wait for pointer release and produces index-step haptic feedback.
- After the initial pointer down, entering a different available index entry starts a smooth scroll to that entry's anchor. Moving within the same index slot does not change the list position. A newly selected anchor cancels and replaces an unfinished smooth-scroll target; targets are never queued.
- The final position is the last selected anchor position. On pointer release or cancellation, the current smooth scroll finishes at that anchor; the list does not derive a percentage position from the pointer, snap to another anchor, or play a completion animation.
- A magnified bubble displays only the active character, or the Settings gear when that token is active, using `32sp` medium-weight text inside a region at least `64dp × 64dp`. It remains visible while the pointer is held and disappears immediately on release or cancellation.
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
- The application region hides the progress indicator, shows a non-interactive `40dp` error icon, displays the localized message `Unable to load applications`, and provides a separate `Retry` action.
- The error state does not instruct the user to restart Avenor.
- The application list, fixed Settings section, and AlphabetIndex remain absent while this full-surface error is shown.
- Selecting Retry clears the visible error state, returns to the Loading state, and starts a new inventory read.
- While the user remains on the same Error presentation, activity resume, network changes, package callbacks, and other ordinary lifecycle events do not automatically retry or clear the error. Leaving Drawer and later entering it again starts a new initial read.
- A successful retry displays the application list. An empty or failed retry returns to the same error state.

## Acceptance intent

- Ordinary Drawer keeps the transparent top app bar fixed while content scrolls; its Back arrow returns through the normal downward transition, and its empty center and right regions expose no inactive controls.
- Favorite multi-selection preserves selection order visibly, never launches or long-presses an application, never exposes Settings, and saves only the complete selection through Confirm. Every cancellation path discards all unconfirmed entries.
- Every launchable entry returned by a successfully read source within Avenor's current role and least-privilege boundary appears once. Entries from an isolated failed non-current profile may be absent without blocking available Content. Primary, work-profile, and cloned entries follow the applicable platform identity treatment; Private Space entries requiring `ACCESS_HIDDEN_PROFILES` are intentionally absent. Primary and cloned entries remain distinguishable when the platform supplies a badge; Avenor-specific fallback distinction is outside the current scope.
- Initial index contact lands immediately on the intended anchor with its heading positioned at the top of the visible list. Each later available-token change smooth-scrolls to that token's anchor without queuing targets.
- Releasing or cancelling an index gesture leaves the application list at the last selected anchor; it does not preserve an arbitrary pointer-percentage position or snap elsewhere. The heading then scrolls normally with the list.
- Index gestures do not independently scroll the list or trigger application-row actions.
- Live inventory updates do not leave a removed application launchable from stale UI.
- Repeated refreshes with unchanged input preserve application ordering and the current anchor-relative position.
