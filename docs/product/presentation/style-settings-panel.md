# Style Settings Panel Presentation Specification

> Public semantic source: English. Chinese counterpart: [style-settings-panel.zh-CN.md](style-settings-panel.zh-CN.md).

## Responsibility

This document is the single presentation source for the visual surface, setting rows, selectors, steppers, and application-size controls shared by the Drawer and Home style settings panels. [Drawer presentation](drawer.md#display-settings) and [Home presentation](home.md#edit-dock-and-style-settings-panel) own only their host-specific placement, modality, height, content, and surrounding layout. Their behavior specifications continue to own state and action results.

This shared product contract requires one observably identical panel treatment; it does not prescribe a source-code class, framework abstraction, or implementation component boundary.

## Shared panel surface

- The style settings panel uses one resource-backed `darkSurfaceBaseColor` surface with exactly `12dp` corners and one clear edge-shadow treatment. Drawer and Home use the same surface color, corner radius, horizontal outer margin, and shadow without host-specific visual substitutions.
- The panel surface has `0dp` general container padding. Every setting block consists of one non-interactive title line and one content line. A title line is exactly `32dp` high. A content line is exactly `48dp` high, except the application-size content line, which is exactly `56dp` high because its options include larger application-icon previews. Lines occupy the complete inner panel width, use `12dp` start and end content insets, and vertically center their contents. A read-only informational row (for example the selection prompt or the fixed-style message) is a single `48dp` row.
- Because the application-size block has the tallest content line, hosts place it last among their setting blocks.
- The shared panel surface does not itself define a Scrim, modal input blocking, outside-click behavior, bottom margin, maximum height, internal scrolling, or attachment to another control. Each host owns those rules.

## Shared selectors and controls

- Related controls that belong to one semantic setting appear together on one content line; for example, the application-arrangement block combines the name-placement selector and the items-per-row stepper on one content line. A content line may scroll horizontally only when its controls overflow the available line width; the title line never scrolls. The contracted acceptance is that content lines do not overflow on the author's designated baseline device (currently the author-designated primary physical device; the Pixel 8 is not currently considered).
- Interaction targets in the panel are at least `40dp x 40dp`. This is an author-accepted exception to the shared `48dp` minimum target, because the panel is not a frequent-touch region and its compact `48dp` content lines otherwise leave no target margin. Visible control size does not define hit geometry.
- A compact two-option selector uses a `140dp x 40dp` dark rounded frame with a light boundary and `2dp` internal padding. It contains two equal `68dp x 36dp` light-filled, borderless rounded thumbs. Centered option labels use medium-weight shared `secondaryTextFontSize` and its line height. Each option retains a target at least `40dp` high and full thumb width.
- Selector thumb and text-color transitions use the same platform or theme short-duration property-animation token, with approximately `200ms` as the reference duration rather than a hard-coded duration.
- Decrement and increment use localized text on approximately `28dp x 28dp` visible rounded backgrounds with `4dp` corners. The items-per-row value uses an approximately `32dp x 28dp` visible background. Each of the three controls retains its own interaction target of at least `40dp x 40dp`; visible background size does not define hit geometry.
- The application-size selector occupies the `56dp` application-size content line. Each Large, Medium, or Small option contains one selection indicator, generic application-icon preview, and localized label. Preview sizes are `48dp`, `40dp`, and `32dp`; indicator-to-icon gap is `4dp`, icon-to-label gap is `8dp`. Options add no independent `16dp` inter-option gap and each retains a separate target at least `40dp` high and full option width.
- Enabled, selected, disabled, focus, Ripple, text, and icon treatment follows [Design foundations](../design-foundations.md). Temporary operation locks, including saving and selection animations, preserve control colors and opacity while rejecting activation and exposing disabled semantics; they do not grey out selectors, values, or otherwise available stepper actions. Stepper actions at a count boundary retain the shared disabled opacity. A host may omit an inapplicable setting block but may not restyle a shared line or render a disabled placeholder unless its behavior contract requires one.
