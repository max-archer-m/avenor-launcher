# Drawer Presentation Specification

> Public semantic source: English. Chinese counterpart: [drawer.zh-CN.md](drawer.zh-CN.md).

## Responsibility

This document owns exact Drawer layout, typography, component geometry, and visual-state values. [Drawer behavior](../surfaces/drawer.md) owns inventory, sorting, state, selection, and action results; [navigation](../navigation.md) owns transition thresholds.

## Surface and application content

- Drawer uses a transparent `56dp`-high top app bar below the status-bar safe inset. Its Back arrow uses `24dp` artwork in a `48dp` interaction target.
- At the Medium application size, an ordinary right-side-name row is at least `56dp` high and uses a visible `40dp x 40dp` application icon. The complete item remains the selection and long-press target.
- In right-side-name arrangement, each item uses `8dp` leading inset, a `40dp` icon, `16dp` icon-to-name gap, and `8dp` trailing inset.
- In below-icon-name arrangement, the `40dp` icon and name are centered as a unit with an `8dp` gap and at least `8dp` horizontal text insets.
- Columns use `0dp` separate spacing and divide the available application-content width by the selected items-per-row count. Medium names use normal-weight `16sp/24sp`, one static line, and end ellipsis.
- The exact Large and Small application-row heights, name typography, arrangement insets, and below-icon-name item height are `To be decided`. Their icon sizes follow the confirmed `48dp`, `40dp`, and `32dp` Large-Medium-Small token sequence; unconfirmed values must not be derived by proportion.

## Section anchors and alphabet index

- Section anchors use bold `16sp/24sp` text. Inline anchors occupy a `32dp`-high row and use a `56dp` start inset so their text aligns with the application-name column; left-side anchors remain beside section entries. Both scroll with list content.
- Alphabet index labels use `11sp` medium-weight text in fixed `20dp` slots. Its Settings gear uses `11dp` artwork inside one complete `20dp` slot.
- The maximum index model contains 28 fixed slots. The Drawer behavior contract owns the resulting available-height threshold and scrolling decision.
- The active-token bubble uses at least `64dp x 64dp` and displays `32sp` medium-weight text or the corresponding Settings gear.
- A non-interactive Drawer error icon uses the shared `40dp` status-icon token.

## Favorite multi-selection

- Multi-selection reserves a `40dp` leading region before every application icon. A centered `24dp` circle uses a `1dp` Material `outline` border while available and unselected.
- Selection fills the circle with the light foreground role and shows its one-based order in `14sp/20sp` medium-weight text using the dark `surface` role. The selected row uses the light foreground role at `8%` opacity.
- Disabled already-favorited rows retain the empty indicator and use the shared disabled semantic opacity for indicator, icon, and name.

## Display settings

- The panel may enter from the bottom as a compact custom surface. It uses a dark rounded surface, explicit side and bottom margins, and a clear edge shadow instead of dimming the exposed Drawer or system background. A transparent modal layer still blocks underlying input, and the shadow must preserve visible separation from both exposed layers.
- At the Medium application-size sample, each compact title-and-control setting row is at least `56dp` high.
- The section-anchor selector uses a `148dp x 44dp` dark rounded frame with a light border and `2dp` internal padding. It contains two equal `72dp x 40dp` light-filled borderless rounded thumbs. Centered option labels use `14sp/20sp` medium-weight text.
- Selector thumb and text-color transitions use the same platform or theme short-duration property-animation token, with approximately `200ms` as the reference duration rather than a hard-coded duration.
- The arrangement title remains fixed. Only its complete trailing control region scrolls horizontally when that region overflows.
- The Right-or-Below selector uses the same `148dp x 44dp` frame, `2dp` padding, and equal `72dp x 40dp` thumbs as the section-anchor selector.
- Decrement and increment use localized text on approximately `32dp x 32dp` visible rounded backgrounds with `4dp` corners. The items-per-row value uses an approximately `36dp x 32dp` visible background. Each of the three controls retains its own interaction target of at least `48dp x 48dp`; visible background size does not define hit geometry.
- The application-size selector occupies a `56dp` setting row. Each large, medium, or small option contains one selection indicator, generic icon preview, and localized label.
- Preview sizes are `48dp`, `40dp`, and `32dp`. Indicator-to-icon gap is `4dp`, icon-to-label gap is `8dp`, and title-to-control-region gap is `16dp`. Options add no independent `16dp` inter-option gap and retain separate targets of at least `48dp x 48dp`.
