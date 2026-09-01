# Style Settings Panel Presentation Specification

> Public semantic source: English. Chinese counterpart: [style-settings-panel.zh-CN.md](style-settings-panel.zh-CN.md).

## Responsibility

This document is the single presentation source for the visual surface, setting rows, selectors, steppers, and application-size controls shared by the Drawer and Home style settings panels. [Drawer presentation](drawer.md#display-settings) and [Home presentation](home.md#edit-dock-and-style-settings-panel) own only their host-specific placement, modality, height, content, and surrounding layout. Their behavior specifications continue to own state and action results.

This shared product contract requires one observably identical panel treatment; it does not prescribe a source-code class, framework abstraction, or implementation component boundary.

## Shared panel surface

- The style settings panel uses one resource-backed `darkSurfaceBaseColor` surface with exactly `12dp` corners and one clear edge-shadow treatment. Drawer and Home use the same surface color, corner radius, horizontal outer margin, and shadow without host-specific visual substitutions.
- The panel surface has `0dp` general container padding. Every read-only or setting row occupies the complete inner panel width, is exactly `56dp` high, uses `12dp` start and end content insets, and vertically centers its contents.
- The shared panel surface does not itself define a Scrim, modal input blocking, outside-click behavior, bottom margin, maximum height, internal scrolling, or attachment to another control. Each host owns those rules.

## Shared selectors and controls

- A compact two-option selector uses a `148dp x 44dp` dark rounded frame with a light boundary and `2dp` internal padding. It contains two equal `72dp x 40dp` light-filled, borderless rounded thumbs. Centered option labels use medium-weight shared `secondaryTextFontSize` and its line height.
- Selector thumb and text-color transitions use the same platform or theme short-duration property-animation token, with approximately `200ms` as the reference duration rather than a hard-coded duration.
- A setting title remains fixed. Only its complete trailing control region scrolls horizontally when that region overflows.
- Decrement and increment use localized text on approximately `32dp x 32dp` visible rounded backgrounds with `4dp` corners. The items-per-row value uses an approximately `36dp x 32dp` visible background. Each of the three controls retains its own interaction target of at least `48dp x 48dp`; visible background size does not define hit geometry.
- The application-size selector occupies one shared `56dp` setting row. Each Large, Medium, or Small option contains one selection indicator, generic application-icon preview, and localized label. Preview sizes are `48dp`, `40dp`, and `32dp`; indicator-to-icon gap is `4dp`, icon-to-label gap is `8dp`, and title-to-control-region gap is `16dp`. Options add no independent `16dp` inter-option gap and retain separate targets of at least `48dp x 48dp`.
- Enabled, selected, disabled, focus, Ripple, text, and icon treatment follows [Design foundations](../design-foundations.md). A host may omit an inapplicable setting row but may not restyle a shared row or render a disabled placeholder unless its behavior contract requires one.
