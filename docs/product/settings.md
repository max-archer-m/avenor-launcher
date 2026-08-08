# Settings Interaction Specification

> Public semantic source: English. Chinese counterpart: [settings.zh-CN.md](settings.zh-CN.md).

## Entry and return

- Settings opens only from the fixed gear destination below the Drawer alphabet index.
- Back returns to Drawer and preserves its prior list position during the same process.
- Settings uses an opaque standard Material 3 dark color scheme. Unlike Home and Drawer, it paints its Material surface background rather than exposing the system background beneath the application.

## Current contract

### Launcher settings

- **Default home application:** Displays current default-Launcher state and opens the system default-home application settings.
- The title is `Default home application`. The supporting text is `Avenor is the default launcher` or `Avenor is not the default launcher` according to current system state.
- Returning from the system destination refreshes the supporting text immediately.

### Language behavior

- The current product provides English and Simplified Chinese resources for every user-visible string.
- Avenor automatically resolves its resources from the current system locale. Simplified Chinese uses the Simplified Chinese resources; English uses the English resources; unsupported locales fall back to English.
- Settings does not contain an application-language item or manual language selector.
- A system-language change updates Avenor to the corresponding supported resource set without requiring an Avenor-specific selection.
- Manual application-language selection is an additive future capability and is outside the current contract.

### About

- **Privacy:** Opens the local Privacy Bottom Sheet.
- **Avenor License:** Opens the local Avenor License Bottom Sheet; the English label uses `License`.
- **Third-party License:** Appears as a separate entry after actual dependencies exist and can be inventoried, and opens its own local scrollable presentation.
- **Project repository:** Opens the configured repository URL through an implicit system browser action.
- **Version information:** Displays `v<version-name>(<version-code>)`, for example `v1.22.1(34)`. It is not interactive and cannot be copied.

### Support and diagnostics

Complex logs, update checks, backup, cloud synchronization, diagnostic export, and copying version or device information are outside the current product contract.

## Privacy presentation

- Selecting Privacy opens a dark modal Bottom Sheet containing a local, readable privacy statement. A dedicated Privacy page is not part of the current scope.
- The statement must remain available offline.
- The sheet uses the same scrim, top drag handle, drag-to-dismiss, scrim-tap dismissal, and Back dismissal behavior as the application action sheet.
- Privacy content scrolls vertically when it exceeds the available sheet height. Closing returns to the same Settings position.
- The actual privacy text remains to be authored and professionally reviewed when the product's data and distribution conditions require it.

## License presentation

- Avenor License and, when present, Third-party License are separate Settings entries.
- Each opens a dark, local, offline-readable presentation. Long content scrolls vertically.
- Their modal dismissal and Settings-position restoration match the Privacy Bottom Sheet.

## Settings item presentation

### Primary settings items

- A primary item contains a title, optional supporting text, and a `24dp` Android or Material trailing arrow icon.
- The title uses Material 3 `titleMedium`: `16sp/24sp`, medium weight, and `onSurface`.
- Supporting text uses `bodySmall`: `12sp/16sp`, normal weight, and `onSurfaceVariant`.
- The trailing arrow uses `onSurfaceVariant`.
- A two-line item is at least `72dp` high. A one-line item is at least `56dp` high and vertically centers its title.
- Primary items use `16dp` horizontal content padding.

### Secondary information items

- Privacy, Avenor License, Third-party License, Project repository, and Version information use the secondary presentation.
- Secondary items use centered Material 3 `titleSmall` text: `14sp/20sp`, medium weight, and `onSurfaceVariant`.
- They do not show a trailing arrow. Clickable entries remain clickable despite the intentionally secondary presentation; Version information is not clickable.
- A secondary item is at least `40dp` high and centers its content horizontally and vertically.

## Project repository

- Avenor does not preflight network connectivity. The system browser owns offline or network-error presentation after it opens.
- If no system handler can open the repository URL or the implicit action fails, show the short localized Toast `Unable to open project link` and retain the current Settings position.
- The repository URL has no copy action in the current scope.

## State refresh

- Returning from a system settings destination refreshes affected Launcher state.
- Settings does not restore a previously open modal sheet after leaving for a system surface.
