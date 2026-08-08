# Product Design Foundations

> Public semantic source: English. Chinese counterpart: [design-foundations.zh-CN.md](design-foundations.zh-CN.md).

## Current theme

- All Avenor surfaces use the dark theme regardless of whether the surface itself paints an opaque background. Text, icons, controls, and semantic color roles use their dark-theme presentation.
- Home and Drawer paint no visible application background. Their surface, status-bar region, and navigation-bar region request full transparency so the system background beneath Avenor remains visible.
- Avenor does not add a gradient, fixed scrim, blur, glass effect, or other contrast-protection layer to Home or Drawer in the current product contract. A future Drawer glass or blur treatment is an additive capability and is not defined now.
- Settings uses an opaque standard Material 3 dark color scheme. Use Material semantic roles such as `surface`, `onSurface`, and related container roles instead of inventing page-specific dark hex colors. Final resource values remain part of the design-token work.
- Modal sheets remain dark. Their presentation must preserve light status-bar icons.
- Avenor requests transparent system bars on Home and Drawer and leaves platform or device contrast enforcement at its default behavior. It does not disable, replace, or duplicate system contrast protection.
- The system navigation mode and navigation controls remain visible according to the user's system configuration; Avenor does not enter immersive mode or hide system navigation.
- Theme customization is an additive future capability and is outside the current contract.

## Layout

- Product spacing values are semantic design tokens rather than arbitrary per-screen literals.
- Home currently uses 16dp horizontal content padding and a provisional 32dp vertical content padding.
- Application-action-sheet dividers use `16dp` horizontal inset.
- Current application rows are single-column, top aligned, and not vertically distributed to fill unused space.

## Typography

- Typography follows system font scaling.
- Time uses a `57sp/64sp` bold display role with tabular numerals where supported.
- Date, weekday, application names, and ordinary information use the Material 3 `bodyLarge` reference of `16sp/24sp` unless a component specification defines another semantic role.
- Application names remain one line. A fitting name is static. An overflowing name waits `800ms`, moves from right to left until its end is visible, waits `800ms`, and then returns to its start.
- Home and Drawer use the same marquee speed. The exact velocity is an implementation token to validate on a physical device rather than a page-specific constant.
- At most one visible application name may animate at a time. Priority belongs to the actively pressed or focused overflowing entry; otherwise, after list motion stops, the overflowing entry nearest the visual center is eligible.
- Marquee animation pauses while its list is scrolling, Home and Drawer are transitioning, an application action sheet is open, or a reorder drag is active. A newly eligible entry restarts from its initial `800ms` pause.
- Font scaling beyond the current personal-use layout is not separately optimized. Text remains clipped to its one-line component boundary if extreme system scaling exceeds that boundary.
- Settings primary titles use `titleMedium` (`16sp/24sp`) with `onSurface`; supporting text uses `bodySmall` (`12sp/16sp`) with `onSurfaceVariant`; centered secondary information items use `titleSmall` (`14sp/20sp`) with `onSurfaceVariant`.

## Icons and application identity

- Application icons preserve the platform-provided adaptive shape, such as device-specific circular or squircle presentation.
- Clone or profile badges use platform-provided data and remain consistent across Home, Drawer, and related application UI.
- Home and Drawer use a `40dp × 40dp` visible application icon inside an application row at least `56dp` high.
- The icon and application name are vertically centered in the row. The horizontal gap from the visible icon boundary to the application-name region is `16dp`.
- The complete row remains the selection and long-press target; the 40dp icon is a visual size, not a restriction on the row touch target.
- If an application's icon cannot be loaded, use Android's platform-default generic application icon in the same 40dp visual region. Do not leave the region empty or substitute an unrelated Avenor icon.
- Current target devices are expected to provide clone or profile badges. Avenor does not add its own fallback badge or secondary identity label when the platform provides none. Such fallback identity treatment is an additive future capability.

## Interaction and accessibility

- Interactive controls should provide a focusable touch target of at least 48dp by 48dp, following Android accessibility guidance, even when their visible content is smaller.
- The Home time row is at least 64dp high. The date row remains visually 40dp high while permitting the input layer to expand its focusable target to at least 48dp.
- Pressed, focused, selected, and disabled states must not rely on color alone.
- Haptic feedback respects system availability and user settings. Current semantic feedback types are long-press confirmation and short index or reorder steps; exact platform constants require implementation validation.
- Small-screen-specific layout and TalkBack-specific alphabet-index behavior are outside the current personal-use scope. The index instead uses fixed 20dp slots and becomes scrollable when its maximum 28-slot model does not fit within 560dp of available height.

## Resource-backed values

User-facing strings, colors, dimensions, and other reusable presentation values must be resource-backed and localizable or themeable as applicable. They must not be scattered as hard-coded literals in product UI code. The exact Android resource and Compose access structure belongs to the future technical architecture.

## Official references

- [Material 3 in Compose](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [Android accessibility: make apps more accessible](https://developer.android.com/guide/topics/ui/accessibility/apps.html)
