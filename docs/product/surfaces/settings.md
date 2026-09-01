# Settings Interaction Specification

> Public semantic source: English. Chinese counterpart: [settings.zh-CN.md](settings.zh-CN.md). Exact visual values are defined in the [Settings presentation specification](../presentation/settings.md); the spatial sketch is the shared [Settings wireframe](../wireframes/settings.txt), with reading rules in the [wireframe index](../low-fidelity-wireframes.md).

## Entry and return

- The page title is `Settings`.
- A fixed top app bar displays the page title and one visible Back control. Selecting that control has the same result as system Back; neither path opens another destination or resets Drawer position.
- Settings opens only when the user selects the fixed Settings row at the end of the Drawer list. Selecting the gear anchor in the AlphabetIndex only navigates to that row's section.
- Back returns to Drawer and preserves its prior list position during the same process.
- Settings uses an opaque standard Material 3 dark color scheme. Unlike Home and Drawer, it paints its Material surface background rather than exposing the system background beneath the application.

## Current contract

### Launcher settings

- **Default home application:** Displays current default-Launcher state and opens the system default-home application settings.
- The title is `Default home application`. The supporting text is `Avenor is the default launcher` or `Avenor is not the default launcher` according to current system state.
- Returning from the system destination refreshes the supporting text immediately.
- **Double-tap to lock:** Displays `On` only while the Avenor accessibility service required by [double-tap-lock.md](../features/double-tap-lock.md) is enabled and connected; otherwise it displays `Off`.
- Selecting Double-tap to lock opens its local explanation and disclosure flow rather than behaving as a direct toggle. Android system state is authoritative, and returning from accessibility settings refreshes the supporting text immediately.

### Language behavior

- The current product provides English and Simplified Chinese resources for every user-visible string.
- Avenor automatically resolves its resources from the current system locale. Simplified Chinese uses the Simplified Chinese resources; English uses the English resources; unsupported locales fall back to English.
- Settings does not contain an application-language item or manual language selector.
- A system-language change updates Avenor to the corresponding supported resource set without requiring an Avenor-specific selection.
- Manual application-language selection is an additive future capability and is outside the current contract.

### About

This heading organizes the behavior contract and is not a visible Settings group heading.

- **Privacy:** Opens the local Privacy Bottom Sheet defined by [privacy.md](../features/privacy.md).
- **Avenor License:** Opens the local Avenor License Bottom Sheet; the English label uses `License`.
- **Third-party License:** Its applicability to the current dependency set is `To be decided` pending a complete dependency-and-license inventory and any qualified review that inventory requires. The entry remains absent until the project author accepts both the inventory result and the exact local offline-readable notice content. Absence of the entry is not a claim that no third-party obligation exists.
- **Project repository:** Opens the configured repository URL through an implicit system browser action.
- **Version information:** Displays `v<version-name>(<version-code>)`, for example `v1.22.1(34)`. It is not interactive and cannot be copied.

### Support and diagnostics

Complex logs, update checks, backup, cloud synchronization, diagnostic export, and copying version or device information are outside the current product contract.

## Privacy presentation

- Selecting Privacy opens a dark modal Bottom Sheet containing a local, readable privacy statement. A dedicated Privacy page is not part of the current scope.
- Its exterior shell, fixed title, and scrolling body follow the [Settings presentation specification](../presentation/settings.md#informational-bottom-sheets).
- The statement must remain available offline.
- The sheet uses the same scrim, top drag handle, drag-to-dismiss, scrim-tap dismissal, and Back dismissal behavior as the application action sheet.
- Privacy content scrolls vertically when it exceeds the available sheet height. Closing returns to the same Settings position.
- The displayed text is the current user-visible Privacy statement in [privacy.md](../features/privacy.md). It includes the current local data, backup, deletion, external-link, permission, and double-tap-lock boundaries.
- The GitHub Issues contact address is selectable and uses the implicit browser and localized failure behavior defined by that Privacy contract.

## License presentation

- Avenor License and an author-accepted Third-party License, when applicable, are separate Settings entries.
- Each opens a dark, local, offline-readable presentation. Long content scrolls vertically.
- Their exterior shell, fixed title, and scrolling body follow the [Settings presentation specification](../presentation/settings.md#informational-bottom-sheets).
- Their modal dismissal and Settings-position restoration match the Privacy Bottom Sheet.

## Settings item roles

### Primary settings items

- A primary item contains a title, optional supporting text, and a trailing Android or Material arrow when it opens another destination.
- Primary item typography, color roles, row geometry, padding, and arrow size belong to the [Settings presentation specification](../presentation/settings.md).

### Secondary information items

- Privacy, Avenor License, Third-party License, Project repository, and Version information use the secondary presentation.
- They do not show a trailing arrow. Clickable entries remain clickable despite the intentionally secondary presentation; Version information is not clickable.
- Secondary item typography, color roles, row geometry, and alignment belong to the Settings presentation specification.

## Project repository

- Avenor does not preflight network connectivity. The system browser owns offline or network-error presentation after it opens.
- If no system handler can open the repository URL or the implicit action fails, show the short localized Toast `Unable to open project link` and retain the current Settings position.
- The repository URL has no copy action in the current scope.

## State refresh

- Returning from a system settings destination refreshes affected Launcher state.
- Settings does not restore a previously open modal sheet after leaving for a system surface.
