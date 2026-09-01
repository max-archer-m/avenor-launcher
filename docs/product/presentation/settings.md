# Settings Presentation Specification

> Public semantic source: English. Chinese counterpart: [settings.zh-CN.md](settings.zh-CN.md).

## Responsibility

This document owns exact Settings typography and row geometry. [Settings behavior](../surfaces/settings.md) owns page content, navigation, and action results; [design foundations](../design-foundations.md) owns shared color and text-size tokens.

## Top app bar

- The fixed top app bar is at least `56dp` high and uses `16dp` horizontal padding.
- Its visible Back artwork uses the shared `24dp` functional-icon token in a standard `48dp x 48dp` icon-button target with a localized accessibility name.
- The title uses Material 3 `titleLarge` and shared `primaryTextColor`. A full-width divider using shared `secondaryTextColor` separates the app bar from the scrolling item list.

## Primary items

- Primary titles use the shared `primaryTextFontSize` and line height, equivalent to Material 3 `titleMedium`, with medium weight and `primaryTextColor`.
- Supporting text uses the shared `secondaryTextFontSize` and line height, equivalent to Material 3 `bodyMedium`, with normal weight and `secondaryTextColor`.
- A two-line item is at least `72dp` high. A one-line item is at least `56dp` high and vertically centers its title.
- Primary items use `16dp` horizontal content padding and a trailing `24dp` Android or Material arrow where the behavior contract includes one.

## Secondary items

- Secondary items use the shared `secondaryTextFontSize` and line height, equivalent to centered Material 3 `titleSmall`, with medium weight and `secondaryTextColor`.
- Each secondary item is at least `40dp` high and centers its content horizontally and vertically. A selectable secondary item uses that complete full-width row as its interaction target; its ordinary minimum target is `40dp` and does not expand invisibly to `48dp`, although the row may grow when system font scaling requires more height. This author-accepted target-size exception applies only to Settings secondary items and does not alter primary rows, the Back control, or other ordinary icon controls. Version information keeps the same visual row geometry but is not interactive.

## Informational Bottom Sheets

- The Double-tap-to-lock explanation, Privacy, Avenor License, and any later author-accepted Third-party License reuse the exterior shell geometry defined by the [application action sheet presentation specification](app-action-sheet.md): complete available portrait-phone width, `12dp` top corner radii, natural height up to the status-bar safe boundary, `0dp` global content padding, and the shared `32dp x 4dp` handle with `12dp` vertical padding above and below.
- These informational sheets do not inherit the application identity row, shortcut rows, five Launcher-action slots, or badge. They use one fixed title row at least `48dp` high with `16dp` horizontal content insets and one body region with `16dp` content padding. Only the body scrolls when content exceeds the available height; the title and drag handle remain visible.
- The accessibility prominent disclosure remains a separate Material 3 Dialog with its own affirmative and cancellation actions. It does not inherit Bottom Sheet geometry.
- Settings exposes no visible `About` group heading. `About` in the behavior document is organizational text only; secondary-item order provides the visible grouping.
