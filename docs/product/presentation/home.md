# Home Presentation Specification

> Public semantic source: English. Chinese counterpart: [home.zh-CN.md](home.zh-CN.md).

## Responsibility

This document owns exact Home layout, typography, component geometry, and visual states. [Home behavior](../surfaces/home.md) owns state and action results; [design foundations](../design-foundations.md) owns shared principles.

## Surface and basic information

- Home preserves wallpaper without a full-screen dim, tint, Scrim, glass surface, or blur. Foreground text and monochrome artwork may use one fixed glyph-following dark shadow. Exact shadow values are `To be decided` through author-device calibration.
- Home applies uniform `8dp` content padding inside `safeDrawing`; adjacent visible modules use `8dp` spacing.
- The basic-information module adds no padding. Its time and date rows use `8dp` start and end margins inside the padded boundary, producing `16dp` row boundaries from safe edges. The time row adds `8dp` top margin for a final `16dp` top distance. Date text adds another `8dp` start inset and begins `24dp` from the safe start edge while its complete row target remains at `16dp`.
- The time row is at least `64dp` high and uses `57sp/64sp` bold display text. The date row is `48dp` high and uses `16sp/24sp` normal text.
- The favorite main list and its modules add no outer padding beyond Home content padding.

## Vertical favorite modules

- Modules use the complete padded content width. Their large, medium, and small item geometry and right/below name arrangements use the applicable Drawer application-item presentation values.
- One-item-per-row with a right-side name follows the established Home list geometry: large uses a `48dp` icon and `64dp` item height; medium uses `40dp` and `56dp`; small uses `32dp` and `48dp`. Names use the corresponding Drawer typography and ellipsis rules.
- Every size permits one or two items per row with right-side names and one through four with below-icon names. Exact multi-column cell geometry reuses the applicable Drawer presentation values. Step controls use the established enabled and disabled presentation at one and the applicable maximum.
- Normal-mode items use no module-level title bar. Application-level editing geometry remains the existing shared remove and drag treatment while the style panel is collapsed.

## Horizontal favorite ribbons

- A ribbon is full width, one row, `56dp` high, and has no container padding. Multiple ribbons or adjacent modules use the shared `8dp` module spacing.
- Each entry uses an `8dp` start inset, `40dp` icon, `8dp` icon-to-name gap, one `16sp/24sp` line measured up to `64dp`, and `8dp` trailing inset. Total width is content-measured up to `128dp`; longer names use end ellipsis. Entry spacing is `8dp`.
- Each entry uses the light foreground role at `6%` for background, `12%` for a `1dp` boundary, and `12dp` radius. Press Ripple clips to that outline.

## Edit dock and style panel

- The edit dock is fixed to the bottom of safe content. Its collapsed height, expanded height, upward/downward affordance geometry, and reserved spacing are `To be decided`.
- The expanded panel is an inline Home layout region that follows the established Drawer settings-panel visual hierarchy while remaining non-modal and outside the favorite main-list viewport. It has no Scrim.
- Every module uses a top-right overlay reorder handle. Its target, artwork, offset, lifted-preview appearance, insertion-line appearance, and selected-module outline or overlay values are `To be decided`; none may change ordinary module measurement.
- The expanded selection layer must make selection visible while preserving readable module identity. It may not expose application-level press or drag feedback.
- The read-only module-type row precedes applicable controls. No visual space is reserved for a custom module name.
- Application remove controls retain a white X on a solid error-red `20dp x 20dp` background. Exact module-level deletion geometry in the panel follows the panel control system and remains `To be decided`.

## Drag and overflow feedback

- Ribbon overflow may use the existing inward `48dp` light-foreground gradient from transparent to at most `8%` opacity. It changes no geometry or input ownership.
- Module movement uses a complete lifted preview following the touch point, no source placeholder, and one insertion line at the candidate boundary. Remaining modules close the source gap without changing widths. The insertion line and edge feedback use resource-backed values still `To be decided`; main-list edge scrolling reuses the existing application-drag edge region, delay, and speed.
- Application drag artwork keeps its source presentation until a valid cross-module release applies destination presentation.
