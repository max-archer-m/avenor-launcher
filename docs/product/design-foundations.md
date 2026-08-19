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
- Layout and reachability decisions primarily optimize for right-hand holding with right-thumb input and left-hand holding with right-hand tapping. Other postures are secondary considerations rather than equal optimization targets.
- Home uses `16dp` horizontal and vertical content padding. Adjacent visible Home modules use `16dp` spacing.
- Application-action-sheet dividers use `16dp` horizontal inset.
- Home is a fixed, non-pageable surface. Primary and companion favorites remain in their two visible regions without folders or another reveal surface; each region scrolls independently only when its content exceeds its viewport.
- Home uses content-driven vertical sizing. The basic-information module wraps its required content and interaction targets; the favorite composition grows up to the remaining safe height and then handles overflow inside its groups. A conditional bottom secondary module has zero height until user-created content for that later capability exists. Home itself does not scroll.
- Home's middle favorite composition uses `8dp` internal padding on each side and a `16dp` inter-group gap, then allocates approximately the left 55% of the remaining horizontal space to primary favorites and the right 45% to companion favorites. The two areas form one asymmetric composition, share one viewport height, keep independent scroll state, do not borrow space from one another, and the ratio is not a user-adjustable control.
- Primary favorites carry greater visual prominence through `40dp` application icons, `8dp` item padding on every side, default `56dp`-high interaction targets, and `16sp/24sp` normal-weight names. Companion favorites use `32dp` application icons, the same item padding, default `48dp`-high interaction targets, and `14sp/20sp` normal-weight names. Both use a `16dp` icon-to-name gap, follow system font scaling, remain directly accessible, and display static one-line names with end ellipsis when needed. Normal mode and edit mode preserve these item heights; drag handles are contained within the existing rows.
- In Home edit mode, each visible module receives the same subtle translucent light-gray editing surface with a small corner radius. The current basic-information and favorite-composition modules receive surfaces; a zero-height conditional module does not. The surface sits behind content rather than tinting it and does not imply that a non-favorite module is editable. Exact shared color, opacity, and radius tokens remain subject to visual calibration.

## Typography

- Typography follows system font scaling.
- Time uses a `57sp/64sp` bold display role with tabular numerals where supported.
- Date, weekday, application names, and ordinary information use the Material 3 `bodyLarge` reference of `16sp/24sp` unless a component specification defines another semantic role.
- Application names remain one line and static. A name that does not fit uses end ellipsis; Avenor does not use marquee motion for application names.
- Font scaling beyond the current personal-use layout is not separately optimized. Text remains clipped to its one-line component boundary if extreme system scaling exceeds that boundary.
- Settings primary titles use `titleMedium` (`16sp/24sp`) with `onSurface`; supporting text uses `bodySmall` (`12sp/16sp`) with `onSurfaceVariant`; centered secondary information items use `titleSmall` (`14sp/20sp`) with `onSurfaceVariant`.

## Icons and application identity

- Functional icon sizes describe the visible icon artwork, not its container or interaction boundary. A standard functional icon is `24dp × 24dp`. This token applies to ordinary icon buttons, action icons, application-shortcut icons, Settings trailing arrows, and other current controls unless a component specification explicitly defines another size. If an applicable page contract later introduces a visible Back arrow on Drawer or Settings, or Pixel-style leading icons for Settings items, those icons also use this standard token.
- A standard functional icon that is independently interactive remains inside a focusable touch target of at least `48dp × 48dp`. When an icon and label form one action, the complete combined item is one interaction and accessibility target; the icon does not create a second target.
- A non-interactive status icon is `40dp × 40dp`. This token applies to standalone loading-result, empty-state, or error illustration icons when the applicable component defines one. The status icon does not receive its own touch target or ripple; any adjacent action remains a separate control.
- Functional and status icons use the applicable semantic content color and consistent optical weight. A `24dp` or `40dp` viewport must not be filled with disproportionately small artwork merely to satisfy the nominal token.
- An icon-only control has a localized accessibility name. A decorative icon, or an icon whose adjacent label already names the same combined action, does not expose a duplicate accessibility description.
- Defining an icon-size token does not add the corresponding control to a page. Drawer and Settings Back arrows and Settings leading-item icons appear only when the applicable page or navigation contract explicitly includes them; their mention here establishes visual sizing only.
- Native adaptive application icons follow the current device mask, such as a device-specific circular or squircle presentation.
- A legacy icon without adaptive layers is normalized inside the current device mask while keeping its complete identifying artwork recognizable within a safe region.
- Clone or profile badges use platform-provided data, are applied after shape normalization so the mask does not crop them, and remain consistent across Home, Drawer, and related application UI.
- Drawer uses a `40dp × 40dp` visible application icon inside an application row at least `56dp` high. Home uses the primary and companion icon and target dimensions defined above.
- In Drawer, the icon and application name are vertically centered in the row, with `16dp` between the visible icon boundary and the application-name region.
- The complete Drawer row remains the selection and long-press target; its 40dp icon is a visual size, not a restriction on the row touch target. Home target geometry remains subject to its later layout decision.
- If an application's icon cannot be loaded, use Android's platform-default generic application icon in the applicable Home or Drawer visual region and apply the same shape-normalization and badge rules. Do not leave the region empty or substitute an unrelated Avenor icon.
- Current target devices are expected to provide clone or profile badges. Avenor does not add its own fallback badge or secondary identity label when the platform provides none. Such fallback identity treatment is an additive future capability.
- Exact parity with proprietary OEM shadows, icon packs, theme services, or other Launcher-specific effects is not required by the current product contract.

## Interaction and accessibility

- Interactive controls should provide a focusable touch target of at least 48dp by 48dp, following Android accessibility guidance, even when their visible content is smaller.
- The Home time row is at least 64dp high. The date row is 48dp high and directly provides its focusable touch target.
- Pressed, focused, selected, and disabled states must not rely on color alone.
- Every enabled target that performs a click or long-press action provides a bounded Material ripple from the initial press. The ripple is clipped to the target's actual interaction boundary and follows its applicable shape; it must not spill across adjacent items or imply that a larger region is interactive.
- Under the current dark-theme contract, the ripple uses a subtle low-opacity near-white color derived from the light foreground-content role. Its exact color and opacity are shared semantic design tokens rather than page-specific values.
- Ripple indicates that input is being pressed; it does not confirm that an action succeeded. A long-press target shows the same press ripple before the long-press threshold and provides its separately defined haptic feedback only when the long press is recognized.
- If input becomes a scroll, drag, surface-transition gesture, cancellation, or other non-click interaction, the press indication is cancelled without triggering the click action. Disabled targets do not present an actionable ripple.
- A future light theme must use a ripple derived from its foreground-content role with visible contrast against the affected surface. It is not required to retain the current near-white ripple. This cross-theme rule does not add light-theme support to the current product contract.
- Haptic feedback respects system availability and user settings. Current semantic feedback types are long-press confirmation and short index or edit-movement steps; exact platform constants require implementation validation.
- Small-screen-specific layout and TalkBack-specific alphabet-index behavior are outside the current personal-use scope. The index instead uses fixed 20dp slots and becomes scrollable when its maximum 28-slot model does not fit within 560dp of available height.

## Resource-backed values

User-facing strings, colors, dimensions, and other reusable presentation values must be resource-backed and localizable or themeable as applicable. They must not be scattered as hard-coded literals in product UI code. The exact Android resource and Compose access structure belongs to the future technical architecture.

## Official references

- [Material 3 in Compose](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [Material 3 ripple API](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ripple(androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Shape,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean,kotlin.Boolean))
- [Android accessibility: make apps more accessible](https://developer.android.com/guide/topics/ui/accessibility/apps.html)
