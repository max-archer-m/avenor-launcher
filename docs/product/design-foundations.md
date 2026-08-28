# Product Design Foundations

> Public semantic source: English. Chinese counterpart: [design-foundations.zh-CN.md](design-foundations.zh-CN.md).

## Responsibility

This document defines shared theme, layout, typography, icon, interaction, accessibility, and resource principles. Exact component and surface values belong to the applicable presentation specification:

- [Home presentation](presentation/home.md)
- [Drawer presentation](presentation/drawer.md)
- [Application action sheet presentation](presentation/app-action-sheet.md)
- [Settings presentation](presentation/settings.md)

The applicable navigation, surface, or feature specification owns product state and action results. A presentation specification does not add a control or behavior that the behavior contract does not include.

## Current theme

- All Avenor surfaces use the dark theme. Text, icons, controls, and semantic color roles use their dark-theme presentation.
- Home paints no visible application background, preserving the system wallpaper without a persistent backdrop gradient, fixed scrim, blur, glass effect, or other full-surface contrast-protection layer. Its presentation specification owns the permitted foreground contrast treatment.
- Drawer's application background follows the user-selected mode defined by the [Drawer behavior](surfaces/drawer.md#display-settings) and [Drawer presentation](presentation/drawer.md#background-modes-and-contrast) specifications. This bounded surface choice does not change the shared dark-theme semantic roles.
- Home and Drawer request transparent system-bar regions and draw edge to edge so their applicable surface treatment remains visible beneath the system bars. A transient local interaction cue remains permitted where its component specification defines one.
- Settings uses an opaque standard Material 3 dark color scheme. Use Material semantic roles instead of page-specific dark hex colors.
- Modal sheets remain dark and preserve light status-bar icons.
- Platform or device contrast enforcement remains at its default behavior. Avenor does not enter immersive mode or hide system navigation.
- Theme customization is an additive future capability outside the current contract.

## Shared layout and typography

- Spacing, typography, color, shape, and visible-size values are semantic design tokens rather than arbitrary per-screen literals.
- Layout and reachability primarily optimize for right-hand holding with right-thumb input and left-hand holding with right-hand tapping. Other postures remain secondary considerations.
- Typography follows system font scaling. Application names remain static, single-line, and end-ellipsized rather than using marquee motion.
- Font scaling beyond the current personal-use layout is not separately optimized. Text remains clipped to its one-line component boundary if extreme scaling exceeds that boundary.

## Shared icons and application identity

- A standard functional icon is `24dp x 24dp`. This value describes visible artwork, not its complete interaction boundary.
- An independently interactive standard functional icon remains inside a focusable target of at least `48dp x 48dp`. When an icon and label form one action, the combined item is one interaction and accessibility target.
- A standalone non-interactive status icon is `40dp x 40dp`. It has no separate touch target or ripple.
- Functional and status icons use the applicable semantic content color and consistent optical weight. An icon-only control has a localized accessibility name; decorative or already-labelled icons do not expose duplicate descriptions.
- Defining a shared icon token does not add the corresponding control to a surface.
- Native adaptive application icons follow the current device mask. Legacy icons are normalized within that mask while preserving recognizable artwork. Platform clone or profile badges are applied after normalization and remain consistent across application surfaces.
- If application artwork cannot be loaded, use Android's platform-default generic application icon with the same normalization and badge rules. Do not substitute an unrelated Avenor icon.
- Current target devices are expected to provide clone or profile badges. Avenor does not add a fallback badge or secondary identity label when the platform provides none.
- Exact parity with proprietary OEM shadows, icon packs, theme services, or other Launcher-specific effects is not required.

## Shared interaction and accessibility

- Interactive controls should provide a focusable target of at least `48dp x 48dp`. A smaller component-specific target requires an author-accepted reason and focused device evidence; dense layouts should first separate visible size from hit geometry.
- Pressed, focused, selected, and disabled states must not rely on color alone.
- The exact shared disabled-content opacity is `To be decided`. Disabled content must meanwhile retain an explicit disabled accessibility state and suppress actionable ripple or activation; a component-specific contract may define an additional non-color indicator but must not invent an independent opacity as a durable product value.
- Every enabled click or long-press target provides a bounded Material ripple from the initial press unless its component contract defines another visible press state. The indication is clipped to the actual target and cancelled when input becomes scrolling, dragging, a surface transition, cancellation, or another non-click interaction.
- The current dark-theme ripple derives from the light foreground-content role. A future light theme would derive it from that theme's foreground role; this does not add light-theme support now.
- Ripple communicates a press, not successful completion. Disabled targets do not present an actionable ripple.
- Haptic feedback respects system availability and user settings. Exact platform constants require implementation validation.

## Resource-backed values

User-facing strings, colors, dimensions, and other reusable presentation values must be resource-backed and localizable or themeable as applicable. They must not be scattered as hard-coded literals in product UI code. Exact Android resource and Compose access structure remains an implementation concern.

## Official references

- [Material 3 in Compose](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [Material 3 ripple API](https://developer.android.com/reference/kotlin/androidx.compose.material3/package-summary#ripple(androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Shape,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean))
- [Android accessibility: make apps more accessible](https://developer.android.com/guide/topics/ui/accessibility/apps.html)
