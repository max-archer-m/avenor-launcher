# Settings Presentation Specification

> Public semantic source: English. Chinese counterpart: [settings.zh-CN.md](settings.zh-CN.md).

## Responsibility

This document owns exact Settings typography and row geometry. [Settings behavior](../surfaces/settings.md) owns page content, navigation, and action results; [design foundations](../design-foundations.md) owns shared color and text-size tokens.

## Top app bar

- The fixed top app bar is at least `56dp` high and uses `16dp` horizontal padding for its title and trailing content.
- Its visible Back artwork uses the shared `24dp` functional-icon token centered in a standard `48dp x 48dp` icon-button target that starts `12dp` from the safe start edge, so the artwork's start edge sits `24dp` from the safe start edge, matching the [Drawer top app bar](drawer.md). The Back control provides a localized accessibility name.
- The title uses Material 3 `titleLarge` and shared `primaryTextColor`. A full-width `1dp` divider using shared `secondaryTextColor` separates the app bar from the scrolling item list, using the shared top-app-bar divider treatment defined by [design foundations](../design-foundations.md).

## Primary items

- Primary titles use the shared `primaryTextFontSize` and line height, equivalent to Material 3 `titleMedium`, with medium weight and `primaryTextColor`.
- Supporting text uses the shared `secondaryTextFontSize` and line height, equivalent to Material 3 `bodyMedium`, with normal weight and `secondaryTextColor`.
- A two-line item is at least `72dp` high. A one-line item is at least `56dp` high and vertically centers its title.
- Primary items use `16dp` horizontal content padding and a trailing `24dp` Android or Material arrow where the behavior contract includes one.

## Secondary items

- Secondary items use the shared `secondaryTextFontSize` and line height, equivalent to centered Material 3 `titleSmall`, with medium weight and `secondaryTextColor`.
- Each secondary item is at least `40dp` high and centers its content horizontally and vertically. A selectable secondary item uses that complete full-width row as its interaction target; its ordinary minimum target is `40dp` and does not expand invisibly to `48dp`, although the row may grow when system font scaling requires more height. This author-accepted target-size exception applies only to Settings secondary items and does not alter primary items, the Back control, or other ordinary icon controls. Version information keeps the same visual row geometry but is not interactive.

## Quick action settings page and selection dialog

- The `Quick action settings` page reuses the Settings top app bar and the primary-item row geometry defined above. Its title is `Quick action settings` and each row carries a title plus supporting text.
- Every row on the page carries the standard trailing arrow of the primary-item treatment, matching the Settings page. The two slot rows carry it as well even though they open a local selection popup rather than another destination.
- The selection popup is one anchored dark popup presenting a single vertical sequence of option rows. The specification describes the observable result only and does not prescribe a container type.
- The popup uses the shared `darkSurfaceBaseColor` background with exactly `12dp` corners. Each option row is `48dp` high with `16dp` horizontal padding and contains one leading `20dp` radio single-selection indicator followed by the action label with `12dp` between them. The radio and the label both use `primaryTextColor`, and the label uses the shared `primaryTextFontSize`. The current binding is marked in its radio.
- Each option row provides the shared Material ripple, clipped so no ripple extends beyond the popup's rounded corners; the top and bottom rows therefore follow the popup's `12dp` corners.
- The popup presents no scrim and does not dim the page. It is anchored to the tapped row rather than centered on the screen: its top edge aligns with the vertical midpoint of that row, and its trailing edge sits `16dp` from the safe trailing screen edge. Because the option list is short, no bottom-edge alignment or bottom clamping is defined.
- The popup width wraps its content. Together with its fixed trailing `16dp` inset, the popup remains inside the safe screen area and never forces its own horizontal scrolling.

## Informational Bottom Sheets

- The Screen-lock explanation, Privacy, Launcher4Max License, and any later author-accepted Third-party License reuse the exterior shell geometry defined by the [application action sheet presentation specification](app-action-sheet.md): complete available portrait-phone width, `12dp` top corner radii, natural height up to the status-bar safe boundary, `0dp` global content padding, and the shared `32dp x 4dp` handle with `12dp` vertical padding above and below.
- These informational sheets do not inherit the application identity row, shortcut rows, five Launcher-action slots, or badge. They use one fixed title row at least `48dp` high with `16dp` horizontal content insets and one body region with `16dp` content padding. Only the body scrolls when content exceeds the available height; the title and drag handle remain visible.
- The accessibility prominent disclosure remains a separate Material 3 Dialog with its own affirmative and cancellation actions. It does not inherit Bottom Sheet geometry.
- Settings exposes no visible `About` group heading. `About` in the behavior document is organizational text only; secondary-item order provides the visible grouping.
