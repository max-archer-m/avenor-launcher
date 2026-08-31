# Drawer Presentation Specification

> Public semantic source: English. Chinese counterpart: [drawer.zh-CN.md](drawer.zh-CN.md).

## Responsibility

This document owns exact Drawer layout, typography, component geometry, and visual-state values. [Drawer behavior](../surfaces/drawer.md) owns inventory, sorting, state, selection, and action results; [navigation](../navigation.md) owns transition thresholds; [design foundations](../design-foundations.md) owns shared color and text-size tokens.

## Surface and application content

- Drawer uses a `56dp`-high top app bar below the status-bar safe inset. The bar remains visually transparent over the selected Drawer background rather than creating its own surface. Its Back arrow uses `24dp` artwork in a `48dp` interaction target.
- At the Medium application size, an ordinary right-side-name row is at least `56dp` high and uses a visible `40dp x 40dp` application icon. The complete item remains the selection and long-press target.
- In right-side-name arrangement, each item uses `8dp` leading inset, a `40dp` icon, `16dp` icon-to-name gap, and `8dp` trailing inset.
- In below-icon-name arrangement, the `40dp` icon and name are centered as a unit with an `8dp` gap and at least `8dp` horizontal text insets.
- Columns use `0dp` separate spacing and divide the available application-content width by the selected items-per-row count. Large, Medium, and Small names respectively use normal-weight shared `largeAppNameFontSize`, `primaryTextFontSize`, and `secondaryTextFontSize`, each with its shared line height and `primaryTextColor`. Every name uses one static line and end ellipsis.
- At every application size, right-side-name arrangement supports one or two equal-width columns and below-icon-name arrangement supports one through four equal-width columns. The stepper's decrement and increment targets retain their ordinary geometry at a boundary but use the shared disabled presentation and expose disabled state rather than actionable Ripple. Their enabled or disabled presentation updates with the effective count in the same frame as a label-placement change; switching to Below at two enables increment, while switching to Right from three or four presents two with increment disabled.
- The exact Large and Small application-row heights, arrangement insets, and below-icon-name item height are `To be decided`. Their icon sizes follow the confirmed `48dp`, `40dp`, and `32dp` Large-Medium-Small token sequence; unconfirmed values must not be derived by proportion.

## Background modes and contrast

- `Transparent` leaves the wallpaper clear across the complete Drawer and adds no full-surface tint, scrim, glass layer, or blur. Text and monochrome foreground artwork drawn over the wallpaper use `primaryTextColor` and one fixed dark glyph- or artwork-following shadow. Exact shadow color, opacity, offset, and blur radius are `To be decided` through author-device wallpaper calibration.
- `Frosted glass` uses one fixed full-surface platform background blur when cross-window blur is available, combined with a low-opacity neutral glass tint. It does not sample the wallpaper or change blur, tint, or contrast by list position, pointer position, search result, or local luminance. Exact blur radius, tint role, and tint opacity are `To be decided` through Samsung Galaxy S23 Ultra and Google Pixel 8 calibration.
- When platform blur is unavailable, the selected `Frosted glass` mode replaces blur with a more-opaque fixed neutral glass surface. Exact fallback role and opacity are `To be decided`; the fallback must preserve content contrast without presenting itself as an Error, warning, disabled setting, or automatic switch to `Transparent`.
- Background mode changes only the Drawer background and applicable contrast treatment. Top-app-bar, application, anchor, search, AlphabetIndex, Settings, multi-selection, modal, and interaction geometry remain unchanged.

## Search field and matching emphasis

- The top app bar centers one `40dp`-high search field independently of the unequal side controls. It remains centered when ordinary Back and display-settings controls are replaced by the search-mode empty-left reservation and right-side `Cancel` action. Side reservations and the field may adapt to the safe width, but they must not overlap or shift the field away from the screen center.
- The field uses a transparent interior and `20dp` corner radius. Its leading magnifying-glass and conditional trailing X use visible `20dp` artwork. Internal start and end insets are `12dp`, and the icon-to-text gap is `8dp`. The X is centered in an interaction target of at least `48dp x 48dp` that remains inside the complete `56dp` top-app-bar interaction region even though the visible field is shorter.
- Hint and query text use normal-weight shared `primaryTextFontSize` and its line height. The English hint is `Search apps`; the Simplified Chinese hint is `搜索应用`. Hint color uses `secondaryTextColor`; entered text uses `primaryTextColor`.
- Outside search mode, the field uses a `1dp` Material `outline` boundary. Search mode animates that boundary to `2dp` using `primaryTextColor` and the shared short-duration color/property animation token. Empty query, hidden keyboard, and temporary loss of text focus do not remove the active boundary while search mode remains active.
- The physical-right `Cancel` text uses medium-weight shared `secondaryTextFontSize`, its line height, and `primaryTextColor`, with one interaction target of at least `48dp` high and wide enough for the complete localized label. The corresponding physical-left reservation matches the right reservation width so the field remains centered, but exposes no visible control, interaction, focus, or accessibility node.
- A matched application-name span uses `primaryTextColor` and medium weight. Unmatched text retains the applicable normal style. Emphasis does not add a background, underline, independent padding, separate semantic node, or geometry change.
- The no-match message is centered in the available application region and uses normal-weight shared `primaryTextFontSize`, its line height, and `primaryTextColor`, without an error icon, progress indicator, or Retry presentation.

## Section anchors and alphabet index

- Section anchors use bold shared `primaryTextFontSize`, its line height, and `primaryTextColor`. Inline anchors occupy a `32dp`-high row and use a `56dp` start inset so their text aligns with the application-name column; left-side anchors remain beside section entries. Both scroll with list content.
- Alphabet index labels use `11sp` medium-weight text in fixed `20dp` slots. Its Settings gear uses `11dp` artwork inside one complete `20dp` slot.
- The maximum index model contains 28 fixed slots. The Drawer behavior contract owns the resulting available-height threshold and scrolling decision.
- The active-token bubble uses at least `64dp x 64dp` and displays `32sp` medium-weight text or the corresponding Settings gear.
- A non-interactive Drawer error icon uses the shared `40dp` status-icon token.

## Favorite multi-selection

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
- The Drawer-background row presents `Transparent` and `Frosted glass` in one two-option single-selection control. Simplified Chinese uses `透明` and `磨砂玻璃`. The complete localized labels remain visible without wrapping or abbreviation and retain separate targets of at least `48dp x 48dp`; exact control width and internal geometry are `To be decided` rather than forced into the narrower existing two-option selector frame.
