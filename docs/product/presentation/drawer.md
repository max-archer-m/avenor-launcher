# Drawer Presentation Specification

> Public semantic source: English. Chinese counterpart: [drawer.zh-CN.md](drawer.zh-CN.md).

## Responsibility

This document owns exact Drawer layout, typography, component geometry, and visual-state values. [Drawer behavior](../surfaces/drawer.md) owns inventory, sorting, state, selection, and action results; [navigation](../navigation.md) owns transition thresholds; [design foundations](../design-foundations.md) owns shared color and text-size tokens.

## Surface and application content

- Drawer uses a `56dp`-high top app bar below the status-bar safe inset. The bar remains visually transparent over the selected Drawer background rather than creating its own surface. Its Back arrow uses `24dp` artwork in a `48dp` interaction target.
- Large, Medium, and Small right-side-name rows are exactly `64dp`, `56dp`, and `48dp` high and use visible `48dp`, `40dp`, and `32dp` application icons, respectively. Each icon is vertically centered, leaving `8dp` above and below it. The complete item remains the selection and long-press target.
- In right-side-name arrangement, every size uses a `12dp` icon-to-name gap. Large, Medium, and Small names respectively use normal-weight shared `largeAppNameFontSize`, `primaryTextFontSize`, and `secondaryTextFontSize`, each with its shared line height and `primaryTextColor`.
- In below-icon-name arrangement, each item uses `8dp` above its icon, a `12dp` icon-to-name gap, and `8dp` below the one-line name. The resulting exact Large, Medium, and Small item heights are `104dp`, `92dp`, and `80dp`, respectively. The icon-and-name unit is horizontally centered and retains at least `8dp` content inset on each side.
- The application grid boundary begins `8dp` from the safe start edge and ends `8dp` before the safe-end AlphabetIndex reservation. Every cell adds its own `8dp` start and end visible-content inset. The resulting first and last visible-content boundaries are `16dp` from their applicable outer boundary, and adjacent cells produce `16dp` between visible content without a separate column gap. Implementations must not add another container and item inset that changes these observable totals.
- Columns use `0dp` separate spacing and divide the available application-grid width by the selected items-per-row count. Every name uses one static line and end ellipsis.
- At every application size, right-side-name arrangement supports one or two equal-width columns and below-icon-name arrangement supports one through four equal-width columns. The stepper's decrement and increment targets retain their ordinary geometry at a boundary but use the shared disabled presentation and expose disabled state rather than actionable Ripple. Their enabled or disabled presentation updates with the effective count in the same frame as a label-placement change; switching to Below at two enables increment, while switching to Right from three or four presents two with increment disabled.

## Background modes and contrast

- `Transparent` leaves the wallpaper clear across the complete Drawer and adds no full-surface tint, scrim, glass layer, or blur. Text and monochrome foreground artwork drawn over the wallpaper use `primaryTextColor` and one fixed dark glyph- or artwork-following shadow. Exact shadow color, opacity, offset, and blur radius are `To be decided` through author-device wallpaper calibration.
- `Frosted glass` uses one fixed full-surface platform background blur when cross-window blur is available, combined with a low-opacity neutral glass tint. It does not sample the wallpaper or change blur, tint, or contrast by list position, pointer position, search result, or local luminance. Exact blur radius, tint role, and tint opacity are `To be decided` through Samsung Galaxy S23 Ultra and Google Pixel 8 calibration.
- When platform blur is unavailable, the selected `Frosted glass` mode replaces blur with a more-opaque fixed neutral glass surface. Exact fallback role and opacity are `To be decided`; the fallback must preserve content contrast without presenting itself as an Error, warning, disabled setting, or automatic switch to `Transparent`.
- Background mode changes only the Drawer background and applicable contrast treatment. Top-app-bar, application, anchor, search, AlphabetIndex, Settings, multi-selection, modal, and interaction geometry remain unchanged.

### Device-calibration candidates

The following experimental values are approved inputs for author-device comparison, not accepted final presentation values:

| Treatment | Calibration candidate |
| --- | --- |
| Transparent foreground shadow | Black at `65%` opacity (`#A6000000`), `0dp` horizontal offset, `1dp` vertical offset, `2dp` blur radius |
| Frosted-glass blur | Fixed `32dp` background-blur radius |
| Frosted-glass tint | `darkSurfaceBaseColor` at `32%` opacity |
| Blur-unavailable fallback | `darkSurfaceBaseColor` at `88%` opacity |

Calibration must compare both Samsung Galaxy S23 Ultra and Google Pixel 8 across representative bright, dark, and visually complex wallpapers. Acceptance may replace any candidate independently. Until that evidence is recorded, the preceding `To be decided` values remain unresolved and an implementation must label results as experimental rather than claiming final visual acceptance.

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

- The panel may enter from the bottom as a compact custom surface. It uses a dark rounded surface, explicit side and bottom margins, and a clear edge shadow instead of dimming the exposed Drawer or system background. A transparent modal layer still blocks underlying input, and the shadow must preserve visible separation from both exposed layers.
- At the Medium application-size sample, each compact title-and-control setting row is at least `56dp` high.
- The section-anchor selector uses a `148dp x 44dp` dark rounded frame with a light border and `2dp` internal padding. It contains two equal `72dp x 40dp` light-filled borderless rounded thumbs. Centered option labels use medium-weight shared `secondaryTextFontSize` and line height.
- Selector thumb and text-color transitions use the same platform or theme short-duration property-animation token, with approximately `200ms` as the reference duration rather than a hard-coded duration.
- The arrangement title remains fixed. Only its complete trailing control region scrolls horizontally when that region overflows.
- The Right-or-Below selector uses the same `148dp x 44dp` frame, `2dp` padding, and equal `72dp x 40dp` thumbs as the section-anchor selector.
- Decrement and increment use localized text on approximately `32dp x 32dp` visible rounded backgrounds with `4dp` corners. The items-per-row value uses an approximately `36dp x 32dp` visible background. Each of the three controls retains its own interaction target of at least `48dp x 48dp`; visible background size does not define hit geometry.
- The application-size selector occupies a `56dp` setting row. Each large, medium, or small option contains one selection indicator, generic icon preview, and localized label.
- Preview sizes are `48dp`, `40dp`, and `32dp`. Indicator-to-icon gap is `4dp`, icon-to-label gap is `8dp`, and title-to-control-region gap is `16dp`. Options add no independent `16dp` inter-option gap and retain separate targets of at least `48dp x 48dp`.
- The Drawer-background row presents `Transparent` and `Frosted glass` in one `224dp x 44dp` two-option single-selection frame with `2dp` internal padding and two equal `110dp x 40dp` visible thumbs. Simplified Chinese uses `透明` and `磨砂玻璃`. The complete localized labels remain visible without wrapping or abbreviation. Each option retains a distinct interaction target at least `48dp` high through the owning setting row; the visible thumb does not reduce that target.
