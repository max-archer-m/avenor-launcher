# Home Presentation Specification

> Public semantic source: English. Chinese counterpart: [home.zh-CN.md](home.zh-CN.md).

## Responsibility

This document owns exact Home layout, typography, component geometry, and visual states. [Home behavior](../surfaces/home.md) owns state and action results; [design foundations](../design-foundations.md) owns shared principles and reusable tokens.

## Surface and basic information

- Home preserves wallpaper without a full-screen dim, tint, Scrim, glass surface, or blur. Foreground text and monochrome artwork use shared `primaryTextColor` and may use one fixed glyph-following dark shadow. Exact shadow values are `To be decided` through author-device calibration.
- Home applies uniform `8dp` content padding inside `safeDrawing`; adjacent visible modules use `8dp` spacing.
- The basic-information module adds no padding. Its time and date rows use `8dp` start and end margins inside the padded boundary, producing `16dp` row boundaries from safe edges. The time row adds `8dp` top margin for a final `16dp` top distance. Date text adds another `8dp` start inset and begins `24dp` from the safe start edge while its complete row target remains at `16dp`.
- The time row is at least `64dp` high and uses `57sp/64sp` bold display text. The date row is `48dp` high and uses normal-weight shared `primaryTextFontSize`, its line height, and `primaryTextColor`.
- The favorite main list and its modules add no outer padding beyond Home content padding.

## Vertical favorite modules

- Modules use the complete padded content width. Their large, medium, and small item geometry and right/below name arrangements use the applicable Drawer application-item presentation values.
- One-item-per-row with a right-side name follows the established Home list geometry: large uses a `48dp` icon and `64dp` item height; medium uses `40dp` and `56dp`; small uses `32dp` and `48dp`. Large, medium, and small names respectively use normal-weight shared `largeAppNameFontSize`, `primaryTextFontSize`, and `secondaryTextFontSize`, each with its shared line height and `primaryTextColor`; all retain the Drawer single-line ellipsis rule.
- Every size permits one or two items per row with right-side names and one through four with below-icon names. Exact multi-column cell geometry reuses the applicable Drawer presentation values. Step controls use the established enabled and disabled presentation at one and the applicable maximum. They update in the same frame as a name-placement change: right-to-below at two enables increment, while below-to-right from three or four displays two with increment disabled.
- Normal-mode items use no module-level title bar. While the style panel is collapsed, application-level editing adds one remove control but no application-reorder handle or reserved handle space; the non-remove portion of the complete item remains the long-press movement target.

## Horizontal favorite ribbons

- A ribbon is full width, one row, `56dp` high, and has no container padding. Multiple ribbons or adjacent modules use the shared `8dp` module spacing.
- Each entry uses an `8dp` start inset, `40dp` icon, `8dp` icon-to-name gap, one normal-weight `primaryTextFontSize` line measured up to `64dp`, and `8dp` trailing inset. The name uses its shared line height and `primaryTextColor`. Total width is content-measured up to `128dp`; longer names use end ellipsis. Entry spacing is `8dp`.
- Each entry uses `primaryTextColor` at `6%` for background, `12%` for a `1dp` boundary, and `12dp` radius. Press Ripple clips to that outline.

## Edit dock and style panel

- The edit dock is fixed inside `safeDrawing` at the bottom of safe content and remains exactly `32dp` high whether the style panel is collapsed or expanded. The system bottom safe inset is outside that height. With the panel collapsed, the favorite main-list viewport ends directly at the dock with `0dp` intervening spacing.
- The dock has no general horizontal padding. Its non-interactive instruction occupies the physical-left region remaining after the affordance target and uses `8dp` start and end padding within that region. The text is vertically centered, remains complete on one line without ellipsis, and uses shared `secondaryTextFontSize`, its line height, and `primaryTextColor`. Instruction weight remains `To be decided`.
- The physical-right upward or downward affordance occupies a `48dp x 32dp` target flush with the dock's top, end, and bottom edges. Its visible directional artwork uses the shared `24dp x 24dp` functional-icon size and `primaryTextColor`; the exact drawable asset is selected during implementation. The author accepts this dock-specific target as an exception to the shared `48dp` minimum target height.
- The expanded panel is an inline Home layout region that follows the established Drawer settings-panel visual hierarchy while remaining non-modal and outside the favorite main-list viewport. It sits directly above the still-visible edit dock with `0dp` spacing and has no Scrim. While it is expanded, the basic-information region is not laid out. Exact panel height, internal padding, row geometry, control spacing, and scrolling remain `To be decided`.
- Modules expose no separate module-reorder handle or reserved handle space. When the style panel is expanded, each module instead receives four short, rounded corner edit marks as a non-interactive overlay. The marks indicate that the complete module surface supports selection and long-press movement; they do not define a smaller touch target or change ordinary module measurement. Exact mark length, stroke width, inset, radius, color, and state values remain `To be decided`.
- The expanded selection layer must make selection visible while preserving readable module identity. Selection uses a complete outline or overlay distinct from the four-corner edit marks, so edit availability, current selection, press recognition, lifted movement, and disabled state are not represented by the same visual. It may not expose application-level press or drag feedback. Exact selected and disabled values remain `To be decided`.
- The read-only module-type row precedes applicable controls. No visual space is reserved for a custom module name.
- Application remove controls use a white X on a solid error-red `20dp x 20dp` circle. The visible circle is also the author-accepted `20dp x 20dp` interaction target and must remain inside its owning item. With a right-side name, the icon begins `8dp` from the item's physical left and top, while the remove circle touches those item edges; the resulting overlap at the icon's top-left is `12dp x 12dp`. A ribbon uses the same geometry. With a below-icon name, the icon-and-name unit retains its centered arrangement and the remove circle is positioned `8dp` left and `8dp` above that icon's top-left, again producing a `12dp x 12dp` overlap. Exact module-level deletion geometry in the panel follows the panel control system and remains `To be decided`.

## Drag and overflow feedback

- Ribbon overflow may use the existing inward `48dp` `primaryTextColor` gradient from transparent to at most `8%` opacity. It changes no geometry or input ownership.
- Module movement uses a complete lifted preview that preserves the initial pointer offset, no source placeholder, and one insertion line at the candidate boundary. Remaining modules close the source gap without changing widths. Exact lifted-preview, insertion-line, and any visible edge-feedback values remain `To be decided`.
- Module and application movement use the same resource-backed edge auto-scroll values. Each applicable start/end or top/bottom edge band is `48dp`, capped at half of the viewport's corresponding dimension when that dimension is less than `96dp`. Continuous residence in one eligible band for `120ms` activates scrolling. Speed then increases linearly from `0dp/s` at the band's inner boundary to exactly `180dp/s` at the viewport's outer edge.
- Application movement retains one source placeholder at the source item's measured dimensions. Its lifted preview keeps source presentation until a valid cross-module release saves successfully. The destination presents one insertion indicator without a provisional full-list reorder; exact placeholder, preview, candidate-module emphasis, and insertion-indicator values remain `To be decided` under the Home edit-feedback presentation work.
