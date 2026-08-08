# Drawer Interaction Specification

> Public semantic source: English. Chinese counterpart: [drawer.zh-CN.md](drawer.zh-CN.md). Shared navigation is defined in [navigation.md](navigation.md).

## Purpose and layout

Drawer presents every launchable application entry exposed to the Launcher by the platform, including cloned entries. System and user-installed applications are treated alike.

- The application inventory is a single-column list; a future grid is an additive capability and is outside the current contract.
- Each section anchor occupies its own row and remains pinned while its section is current.
- Each application row displays platform icon, platform-provided badge when present, and application name.
- An application name occupies exactly one line. A name that fits remains static; an overflowing name uses the shared marquee behavior defined in [design-foundations.md](design-foundations.md).
- Selecting an application immediately launches it and suppresses duplicate rapid activation.
- Long-pressing an application produces long-press haptic feedback and opens the application action sheet.

## Grouping and sorting

- Sorting ignores case and uses stable natural character ordering.
- In a Chinese locale, Chinese names are grouped and sorted by pinyin.
- Entries that cannot be grouped under pinyin or Latin A–Z are grouped under `#`; their internal order remains natural.
- Every application participates in the same ordering regardless of whether it is a system, downloaded, primary, or cloned application.
- Exact handling of homophones, identical names, mixed-script names, numbers, and profile ties remains to be specified.

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
- During initial loading, the application region shows a progress indicator and localized loading message.
- If the resulting list is unexpectedly empty or cannot be loaded, the same region shows an error icon, an explanatory message, and guidance to restart Avenor.
- Refresh behavior, scroll-position stability during live changes, and distinction between a true empty inventory and an inventory error require validation.

## Acceptance intent

- Every platform-exposed launchable entry appears once and remains distinguishable from its clone.
- Index navigation lands on the intended pinned anchor.
- Index gestures do not simultaneously scroll the list.
- Live inventory updates do not leave a removed application launchable from stale UI.
