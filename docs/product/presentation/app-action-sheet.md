# Application Action Sheet Presentation Specification

> Public semantic source: English. Chinese counterpart: [app-action-sheet.zh-CN.md](app-action-sheet.zh-CN.md).

## Responsibility

This document owns exact application-action-sheet presentation values. [Application action sheet behavior](../surfaces/app-action-sheet.md) owns content availability, order semantics, and action results.

## Geometry

- The application-information and Launcher-action graphics use the shared `24dp` functional-icon token.
- Light dividers use `16dp` horizontal inset.
- Each optional application shortcut uses a `24dp` icon plus name; the region ends with the same inset divider.
- Launcher actions place their `24dp` icon above the label. The complete icon-and-label item is one interaction target.
- The clone or profile badge uses a `12dp x 12dp` visual region aligned to the Bottom Sheet's bottom-right corner without outward offset.
