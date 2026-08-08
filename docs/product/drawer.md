# Drawer Interaction Specification

> Public semantic source: English. Chinese counterpart: [drawer.zh-CN.md](drawer.zh-CN.md). Shared navigation is defined in [navigation.md](navigation.md).

## Purpose and layout

Drawer presents every launchable application entry exposed to the Launcher by the platform, including cloned entries. System and user-installed applications are treated alike.

- The application inventory is a single-column list; a future grid is an additive capability and is outside the current contract.
- Each section anchor occupies its own row and remains pinned while its section is current.
- Each application row is at least `56dp` high and displays a `40dp` platform icon, platform-provided badge when present, and application name, with `16dp` between icon and name.
- An application name occupies exactly one line. A name that fits remains static; an overflowing name uses the shared marquee behavior defined in [design-foundations.md](design-foundations.md).
- Selecting an application immediately launches it and suppresses duplicate rapid activation.
- Long-pressing an application produces long-press haptic feedback and opens the application action sheet.

## Grouping and sorting

- Sorting uses Android's locale-aware collation for the current system locale, compares the complete displayed application name, and ignores case. It does not use ASCII ordering or a Launcher-defined character-priority table.
- In a Chinese locale, system locale collation determines pinyin, polyphonic-character, and mixed-script ordering. Avenor does not embed a separate pronunciation dictionary or pinyin library as a product requirement.
- Section assignment follows the complete-name result produced by the same locale-aware ordering rather than inspecting only the first raw Unicode character.
- Entries that the locale-aware sectioning cannot place under Latin A–Z are grouped under `#`; their internal order uses the same complete-name locale-aware comparator. Avenor does not separately rank numbers, punctuation, Emoji, or other scripts inside `#`.
- Every application participates in the same ordering regardless of whether it is a system, downloaded, primary, or cloned application.
- When displayed names collate equally, identity precedence is primary application, cloned application, then work-profile application.
- When both displayed-name collation and identity category are equal, use a stable launchable-entry identity as the final tie-breaker. The observable order must remain stable across refreshes; the product contract does not prescribe the implementation field or API.

## Alphabet index

- A fixed right-side index is visible whenever Drawer is visible.
- It contains `#`, each non-empty A–Z section, and a fixed Settings gear below Z. Empty alphabetical sections are omitted.
- Index labels use `11sp` medium-weight text in fixed `20dp` slots. The Settings gear occupies one slot with the same height; non-empty entries are not stretched to redistribute unused height.
- The maximum index model contains 28 slots: `#`, A–Z, and Settings. Its minimum complete available height is therefore `560dp`, calculated as `28 × 20dp` after excluding status-bar, navigation-bar, display-cutout, Drawer-padding, and system-gesture insets.
- At `560dp` or more of available height, the index does not scroll. Below `560dp`, the index becomes an independently scrollable vertical region while the application list remains separately scrollable.
- Selecting an index entry jumps immediately to its anchor and produces index-step haptic feedback.
- Sliding across a different available entry changes the active anchor and produces one haptic response for that change; remaining on the same entry does not repeat it.
- A magnified bubble displays only the active character using `32sp` medium-weight text inside a region at least `64dp × 64dp`. It remains visible while the pointer is held and disappears immediately on release or cancellation.
- While the index owns the pointer, the application list does not scroll.
- The Settings gear is a fixed index destination rather than a separate Drawer button. It opens Settings.
- Returning from Settings preserves the Drawer list position during the same process.
- TalkBack semantics and an accessibility-specific alternate index interaction are outside the current personal-use scope.

## Inventory changes and states

- The inventory updates while Drawer is active when applications or cloned entries are added, removed, enabled, disabled, or renamed.
- A routine live update preserves the current section anchor and the relative scroll position within that anchor whenever the anchor still exists.
- If the current anchor disappears, move to the next existing application anchor in sort order and pin its heading at the top.
- If no later application anchor exists, remain at the bottom of the application list. The fixed Settings gear is not an application anchor and is never opened automatically by an inventory update.
- An update never automatically launches an application or opens an application action sheet or Settings.

### Loading

- While no usable inventory result is available and a read is in progress, the application region shows a progress indicator and the localized message `Loading applications…`.
- Loading is an in-progress state, not an empty or error result. The error icon and Retry action are not shown concurrently with the progress indicator.

### Empty or failed result

- A successful read that returns no launchable entries is treated as an error rather than a valid empty Drawer.
- A failed inventory read uses the same error state.
- The application region hides the progress indicator, shows an error icon, displays the localized message `Unable to load applications`, and provides a `Retry` action.
- The error state does not instruct the user to restart Avenor.
- Selecting Retry clears the visible error state, returns to the Loading state, and starts a new inventory read.
- A successful retry displays the application list. An empty or failed retry returns to the same error state.

## Acceptance intent

- Every platform-exposed launchable entry appears once. Primary and cloned entries remain distinguishable when the platform supplies a badge; Avenor-specific fallback distinction is outside the current scope.
- Index navigation lands on the intended pinned anchor.
- Index gestures do not simultaneously scroll the list.
- Live inventory updates do not leave a removed application launchable from stale UI.
- Repeated refreshes with unchanged input preserve application ordering and the current anchor-relative position.
