# Drawer Presentation Specification

> Public semantic source: English. Chinese counterpart: [drawer.zh-CN.md](drawer.zh-CN.md).

## Responsibility

This document owns exact Drawer layout, typography, component geometry, and visual-state values. [Drawer behavior](../surfaces/drawer.md) owns inventory, sorting, state, selection, and action results; [navigation](../navigation.md) owns transition thresholds; [design foundations](../design-foundations.md) owns shared color and text-size tokens; the [style settings panel presentation](style-settings-panel.md) owns the panel surface and controls shared with Home.

## Surface and application content

- Drawer uses a `56dp`-high top app bar below the status-bar safe inset. The bar remains visually transparent over the selected Drawer background rather than creating its own surface. Its Back arrow uses `24dp` artwork in a `48dp` interaction target.
- Large, Medium, and Small right-side-name rows are exactly `68dp`, `60dp`, and `52dp` high and use visible `48dp`, `40dp`, and `32dp` application icons, respectively. Each icon is placed `8dp` from the cell's top and `12dp` from its bottom. The complete item remains the selection and long-press target, and the press Ripple covers the complete cell without being reduced by cell padding.
- In right-side-name arrangement, every size uses a `12dp` icon-to-name gap. Large, Medium, and Small names respectively use normal-weight shared `largeAppNameFontSize`, `primaryTextFontSize`, and `secondaryTextFontSize`, each with its shared line height and `primaryTextColor`.
- In below-icon-name arrangement, each item uses `8dp` above its icon, a `12dp` icon-to-name gap, and `12dp` below the one-line name. The resulting exact Large, Medium, and Small item heights are `108dp`, `96dp`, and `84dp`, respectively. The name occupies one text box spanning the complete cell content width with exactly `8dp` visible-content inset on each side, and the text is horizontally centered within that box; the icon is horizontally centered in the cell. Adjacent cells therefore keep their name text boxes exactly `16dp` apart without a separate column gap. In hidden-name arrangement, no name is rendered: the icon is placed `8dp` from the cell's top and `12dp` from its bottom and horizontally centered in the cell, using the same Large, Medium, and Small row heights as right-side names (`68dp`, `60dp`, and `52dp`).
- The application grid boundary begins `8dp` from the safe start edge and ends `8dp` before the safe-end AlphabetIndex reservation. Every cell adds its own `8dp` start and end visible-content inset. The resulting first and last visible-content boundaries are `16dp` from their applicable outer boundary, and adjacent cells produce `16dp` between visible content without a separate column gap. Implementations must not add another container and item inset that changes these observable totals.
- Columns use `0dp` separate spacing and divide the available application-grid width by the selected items-per-row count. Every rendered name uses one static line and end ellipsis.
- At every application size, right-side-name arrangement supports one or two equal-width columns, below-icon-name arrangement supports one through four equal-width columns, and hidden-name arrangement supports one through six equal-width columns. The stepper's decrement and increment targets retain their ordinary geometry at a boundary but use the shared disabled presentation and expose disabled state rather than actionable Ripple. Their enabled or disabled presentation updates with the effective count in the same frame as a label-placement change; switching to `Below` at two enables increment, switching to `Right` from three or four presents two with increment disabled, switching to `Hidden` preserves the count and enables increment up to six, and switching from `Hidden` to `Right` presents two or from `Hidden` to `Below` presents four when the count exceeds that boundary, with increment disabled.

## Background modes and contrast

- `Transparent` leaves the wallpaper clear across the complete Drawer and adds no full-surface tint, scrim, glass layer, or blur. Text and monochrome foreground artwork drawn over the wallpaper use `primaryTextColor` and one fixed dark glyph- or artwork-following shadow using the accepted parameters below.
- The frosted-glass mode uses one fixed full-surface platform background blur at the user-selected intensity when cross-window blur is available. It adds no translucent tint layer, does not sample the wallpaper, and does not change blur or contrast by list position, pointer position, search result, or local luminance. The fixed text and monochrome-artwork shadow defined for `Transparent` remain in effect in this mode.
- When platform blur is unavailable, the frosted-glass mode renders as the `Transparent` presentation—clear wallpaper with the fixed shadows—without presenting itself as an Error, warning, or disabled setting. The selected intensity is retained and the blur returns when it becomes available. Because the frosted-glass mode adds no tint layer, its readability over bright wallpapers depends on the blurred wallpaper luminance; this is a recorded known limitation judged by author-device acceptance.
- Background mode changes only the Drawer background and applicable contrast treatment. Top-app-bar, application, anchor, search, AlphabetIndex, Settings, multi-selection, modal, and interaction geometry remain unchanged.

### Accepted background parameters

The author accepted the following current delivery values on 2026-09-06, amended on 2026-09-07 by the pure-blur decision that removed the tint and fallback treatments. Further optimization remains follow-up work and does not block Iteration 27:

| Treatment | Accepted value |
| --- | --- |
| Foreground shadow (both modes) | Black at `65%` opacity (`#A6000000`), `0dp` horizontal offset, `1dp` vertical offset, `2dp` blur radius |
| Frosted-glass blur radius | `15dp` per selected intensity level; levels `1` through `10`, maximum `150dp` |

Follow-up calibration should compare Samsung Galaxy S23 Ultra and Google Pixel 8 across representative bright, dark, and visually complex wallpapers. That matrix has not been established as passed; its absence no longer blocks acceptance of the current parameters or Iteration 27. Future parameter changes require author acceptance and an update to this specification.

## Search field and matching emphasis

- The top app bar centers one `40dp`-high search field independently of the unequal side controls. It remains centered when ordinary Back and display-settings controls are replaced by the search-mode empty-left reservation and right-side `Cancel` action. Side reservations and the field may adapt to the safe width, but they must not overlap or shift the field away from the screen center.
- The field uses a transparent interior and `20dp` corner radius. Its leading magnifying-glass and conditional trailing X use visible `20dp` artwork. Internal start and end insets are `12dp`, and the icon-to-text gap is `8dp`. The X is centered in an interaction target of at least `48dp x 48dp` that remains inside the complete `56dp` top-app-bar interaction region even though the visible field is shorter.
- Hint and query text use normal-weight shared `primaryTextFontSize` and its line height. The English hint is `Search apps`; the Simplified Chinese hint is `搜索应用`. Hint color uses `secondaryTextColor`; entered text uses `primaryTextColor`.
- Outside search mode, the field uses a `1dp` Material `outline` boundary. Search mode animates that boundary to `2dp` using `primaryTextColor` and the shared short-duration color/property animation token. Empty query, hidden keyboard, and temporary loss of text focus do not remove the active boundary while search mode remains active.
- The physical-right `Cancel` text uses medium-weight shared `secondaryTextFontSize`, its line height, and `primaryTextColor`, with one interaction target of at least `48dp` high and wide enough for the complete localized label. The corresponding physical-left reservation matches the right reservation width so the field remains centered, but exposes no visible control, interaction, focus, or accessibility node.
- A matched application-name span uses `primaryTextColor` and medium weight. Unmatched text retains the applicable normal style. Emphasis does not add a background, underline, independent padding, separate semantic node, or geometry change.
- The no-match message is centered in the available application region and uses normal-weight shared `primaryTextFontSize`, its line height, and `primaryTextColor`, without an error icon, progress indicator, or Retry presentation.

## Section anchors and alphabet index

- Section anchors use bold shared `largeAppNameFontSize`, its line height, and `primaryTextColor`. An Inline anchor occupies a full-width `40dp` row, vertically centers its text, and places the text start exactly `16dp` from the safe start edge; it scrolls without pinning. A Left-side anchor occupies a `40dp`-wide column with `0dp` gap before the remaining application grid. Its text is horizontally centered and begins `8dp` below the owning section's top until section-bounded pinning places that same geometry below the top app bar. The Settings left-side anchor replaces text with `16dp x 16dp` `primaryTextColor` gear artwork, horizontally centered with the same `8dp` top offset. Neither anchor presentation changes layout while pinning or exposes an interaction target.
- The AlphabetIndex occupies a fixed `32dp` width at the safe end edge and is vertically centered in the available application region. Labels use `11sp` medium-weight text in fixed `20dp` slots. Its Settings gear uses `11dp` artwork inside one complete `20dp` slot.
- The maximum index model contains 28 fixed slots. The Drawer behavior contract owns the resulting available-height threshold and scrolling decision.
- The active-token bubble is exactly `64dp x 64dp`, remains vertically centered in the available application region, and sits immediately before the AlphabetIndex with a `16dp` gap. It displays `32sp` medium-weight text or a `32dp` Settings gear and does not follow the active slot vertically.
- Drawer Loading uses the shared `48dp x 48dp` progress-indicator size, and Error uses the shared `48dp x 48dp` non-interactive status-icon size. Their message uses normal-weight shared `primaryTextFontSize`, its line height, and `primaryTextColor`. Indicator or icon to message spacing is `16dp`; Error message to Retry spacing is also `16dp`.

## Settings fixed row

- The fixed Settings row is at least `56dp` high. It uses a `40dp` `primaryTextColor` gear, a `16dp` icon-to-name gap, and a normal-weight shared `primaryTextFontSize` name with its line height and `primaryTextColor`.
- The row begins `16dp` from the safe start edge. Its content ends before the AlphabetIndex reservation, which consists of `16dp` safe-end content inset plus the fixed `32dp` index width. The complete row is the interaction target; the gear exposes no duplicate target or description.

## Favorite multi-selection

- The multi-selection top app bar remains `56dp` high and uses start, center, and end content regions. The start `Cancel` and end `Confirm` controls each occupy a vertically centered `48dp`-high target flush with their applicable safe edge, with `12dp` start and end content padding inside that target. The text itself retains its shared font size and natural line height rather than being assigned a `48dp` text height.
- The two side reservations use the wider measured width of the complete localized `Cancel` and `Confirm` targets, so they remain symmetric and the destination description stays centered on the physical screen. The center description uses the remaining width, remains one line with end ellipsis, and cannot overlap either action. All three regions are vertically centered.
- Multi-selection reserves a `40dp` leading region before every application icon. A centered `24dp` circle uses a `1dp` Material `outline` border while available and unselected.
- Selection fills the circle with `primaryTextColor` and shows its one-based order in medium-weight shared `secondaryTextFontSize` and line height using `darkSurfaceBaseColor`. The selected row uses `primaryTextColor` at `8%` opacity.
- Disabled already-favorited rows retain the empty indicator and use the shared disabled semantic opacity for indicator, icon, and name.

## Display settings

- Drawer hosts the shared [style settings panel](style-settings-panel.md) as a compact custom surface that may enter from the bottom. It uses the shared horizontal outer margin and adds a Drawer-specific bottom margin. A transparent modal layer blocks underlying input without dimming the exposed Drawer or system background; the shared edge shadow must preserve visible separation from both exposed layers.
- Drawer presents four setting blocks through the shared panel: application arrangement, section anchors, Drawer background, and application size. The application-size block is last because its content line is the tallest. The section-anchor control uses the shared compact two-option selector; the name-placement (`Right` / `Below` / `Hidden`) control uses the shared compact selector; application size and items per row use the shared application-size selector and stepper. Drawer retains its own setting names, option labels, availability, and save behavior.
- The Drawer-background block presents one on-off switch and one blur-intensity slider on its content line using the shared background-row geometry defined by the [style settings panel presentation](style-settings-panel.md): a `48dp x 48dp` switch target at the physical left, a `16dp` gap, and a snapping ten-stop slider filling the remaining width. The switch's selected track and the slider's active track and thumb use shared `primaryTextColor`; the slider displays no current-level number and uses the shared `38%` disabled opacity while the switch is off.
