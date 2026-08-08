# Home Interaction Specification

> Public semantic source: English. Chinese counterpart: [home.zh-CN.md](home.zh-CN.md). Shared navigation is defined in [navigation.md](navigation.md); shared visual rules are defined in [design-foundations.md](design-foundations.md).

## Purpose and structure

Home is the Launcher primary surface. Its content is one top-to-bottom column:

1. Basic information region: time, then date and weekday.
2. Favorite application list: every saved launchable entry in user-defined order.

The basic information region normally occupies approximately 20%–30% of available vertical space. Content fit and accessibility take priority over enforcing that proportion. The favorite list occupies the remaining space, starts at the top of its region, and scrolls only when necessary. A scrollbar appears only during scrolling.

Home content uses 16dp horizontal inset and a provisional 32dp vertical inset. These are container paddings rather than external margins because they define the safe internal distance between Home content and its available bounds.

## Time, date, and weekday

- Time is left-aligned above the date and uses a `57sp` font size, `64sp` line height, and bold weight. It uses tabular numerals where the selected system font supports them so minute changes do not shift the clock width.
- Time follows the system 12-hour or 24-hour preference and does not show seconds.
- Date and weekday are left-aligned below time, use a `16sp` font size with `24sp` line height and normal weight, and follow the active locale, for example `2月1日 星期二` in Simplified Chinese.
- Selecting the visible time text invokes an implicit system Clock destination without targeting a package.
- Selecting the visible date-and-weekday text invokes an implicit system Calendar destination without targeting a package.
- The time line is at least `64dp` high and does not require a separately enlarged target beyond its rendered line region.
- The date-and-weekday row is visually `40dp` high and vertically centers its text. The touch-input layer may expand its focusable target to at least `48dp` without drawing a larger background or changing the visible 40dp layout.

## Favorite list

- A new installation starts with no favorites and shows a concise empty-state invitation to add applications from Drawer.
- Adding a favorite from Drawer appends it to the end of the list.
- The same launchable entry cannot appear more than once. A primary application and its clone are distinct entries and may each be favorited.
- Entries are a vertical single-column list. Each row is at least `56dp` high and contains a `40dp` platform-provided application icon, its clone or profile badge when present, and the application name, with `16dp` between icon and name.
- An application name occupies exactly one line. A name that fits remains static; an overflowing name uses the shared marquee behavior defined in [design-foundations.md](design-foundations.md).
- Icon shape and badge use the platform-provided representation consistently across Home, Drawer, and related application UI.
- Selecting an entry immediately launches it. Ordinary selection does not produce haptic feedback. Duplicate rapid activation must be suppressed.
- Long-pressing an entry produces long-press haptic feedback and opens the application action sheet defined in [app-action-sheet.md](app-action-sheet.md).
- A favorite is removed automatically only after a successful inventory refresh confirms that its application was uninstalled, its clone was removed, or that specific launchable identity permanently disappeared.
- An inventory loading failure never deletes or hides saved favorites.
- An application that still exists but is disabled remains in its stored position as a visibly disabled favorite. Selecting it does not attempt a normal launch and provides a short localized unavailable Toast. Long-press remains available for applicable information or management actions.
- A transient launch failure retains the favorite, provides a short localized error Toast, and refreshes that entry's state.
- A later successful refresh restores an available entry in place without changing its favorite order.

## Reorder mode

- Reorder mode is available only when Home contains at least two favorites and is entered from the selected favorite's Launcher actions. With zero or one favorite, the reorder action is hidden.
- Every favorite displays a three-horizontal-line drag handle on its right, visually comparable to common media-list reorder handles.
- Dragging an item across another stored position swaps the positions and produces one short haptic response for the completed position change.
- The changed order is saved immediately.
- System Back is the only current exit from reorder mode. Exiting does not undo completed moves.
- The visible drag handles are the current indication that reorder mode is active. No additional title, banner, completion button, or exit action is defined.

### Automatic scrolling

- The top and bottom `48dp` of the visible favorite-list viewport are reorder auto-scroll zones.
- While a dragged item remains inside an auto-scroll zone, the list scrolls in that direction. Speed increases as the pointer approaches the outer edge; the exact speed curve is a shared implementation token validated on a physical device.
- Auto-scroll stops immediately when the pointer leaves both zones or when the list reaches its corresponding boundary.
- Crossing a stored item position during auto-scroll performs the same swap, immediate save, and one short haptic response as an ordinary reorder move. Remaining in an edge zone or scrolling without crossing a stored position does not produce haptic feedback.
- Releasing the item keeps and immediately saves its current position.
- Reorder mode exclusively owns vertical favorite-list dragging. Home-to-Drawer dragging is unavailable until reorder mode ends, so bottom-edge auto-scroll cannot begin a Drawer transition.

### Inventory changes during reorder

| Inventory event | Current behavior |
| --- | --- |
| A new application is installed | Keep reorder mode; favorites are unchanged |
| A non-favorite application is removed | Keep reorder mode; favorites are unchanged |
| A favorite's name changes | Update the row in place and keep reorder mode |
| A favorite's icon or platform badge changes | Update the row in place and keep reorder mode |
| A favorite application is uninstalled | End reorder mode, remove the favorite, preserve already saved moves, and notify the user |
| A favorite clone is removed | End reorder mode, remove that clone favorite, preserve already saved moves, and notify the user |
| A favorite application is disabled | End reorder mode, retain it as a disabled favorite in its saved position, preserve already saved moves, and notify the user |
| Favorite identity can no longer be confirmed | End reorder mode, retain the saved entry and order pending refresh, and notify the user |
| The full application inventory fails to load | End reorder mode, retain all favorites and their saved order, and notify the user |
| The favorite count falls below two | End reorder mode after applying the relevant event behavior |

- Use one localized message for a confirmed favorite change: `Favorites changed. Reorder ended.`
- Use one localized message for an inventory or identity refresh failure: `Unable to update application status. Reorder ended.`
- Present both messages as short Toasts.

## Acceptance intent

- Empty, short, and scrollable favorite lists preserve the same top-aligned structure.
- Primary and cloned entries remain visibly distinguishable when the platform supplies a badge; Avenor-specific fallback distinction is outside the current scope.
- Returning to Home during the same process preserves its meaningful list state; process recreation follows [navigation.md](navigation.md).
