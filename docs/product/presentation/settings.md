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
- Each secondary item is at least `40dp` high and centers its content horizontally and vertically.
