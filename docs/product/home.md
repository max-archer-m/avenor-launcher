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
- Entries are a vertical single-column list containing the platform-provided application icon, its clone or profile badge when present, and the application name.
- An application name occupies exactly one line. A name that fits remains static; an overflowing name uses the shared marquee behavior defined in [design-foundations.md](design-foundations.md).
- Icon shape and badge use the platform-provided representation consistently across Home, Drawer, and related application UI.
- Selecting an entry immediately launches it. Ordinary selection does not produce haptic feedback. Duplicate rapid activation must be suppressed.
- Long-pressing an entry produces long-press haptic feedback and opens the application action sheet defined in [app-action-sheet.md](app-action-sheet.md).
- A missing or permanently removed launchable entry is removed from favorites when the application inventory confirms that change. Temporary unavailability and recovery behavior remain to be defined.

## Reorder mode

- Reorder mode is available only for Home favorites and is entered from the selected favorite's Launcher actions.
- Every favorite displays a three-horizontal-line drag handle on its right, visually comparable to common media-list reorder handles.
- Dragging an item across another stored position swaps the positions and produces one short haptic response for the completed position change.
- The changed order is saved immediately.
- System Back is the only current exit from reorder mode. Exiting does not undo completed moves.

## Acceptance intent

- Empty, short, and scrollable favorite lists preserve the same top-aligned structure.
- Primary and cloned entries remain visibly distinguishable through the platform badge.
- Returning to Home during the same process preserves its meaningful list state; process recreation follows [navigation.md](navigation.md).
