# Privacy and Data Handling

> Public semantic source: English. Chinese counterpart: [privacy.zh-CN.md](privacy.zh-CN.md). Settings presentation is defined in [settings.md](../surfaces/settings.md); the optional accessibility capability is defined in [double-tap-lock.md](double-tap-lock.md).

## Purpose and authority

This document defines Avenor Launcher's current user-visible Privacy statement and the separate prominent disclosure required before an enable-oriented handoff for double-tap lock. It describes the current product contract rather than making a legal, store-approval, or implementation-completeness claim.

The Privacy statement and prominent disclosure are separate presentations. Showing or reading Privacy does not count as consent to accessibility access. The prominent disclosure appears in the normal double-tap-lock enablement flow immediately before Avenor offers to open Android accessibility settings.

Both presentations are bundled with the application, remain available offline, and use the English or Simplified Chinese resource selected from the system locale, with English fallback. The application must not fetch either text from GitHub or another network source.

## User-visible Privacy statement

### Privacy

Avenor Launcher is designed to keep its current product data and processing on your device.

### Information used on your device

Avenor processes the application entries that Android exposes to it under the current Launcher role and permission boundary. This can include application names, icons, launchable components, Android profile identities, platform-provided badges, and application shortcuts. Avenor uses this information to show applications, distinguish launchable identities, launch the entry you select, and present applicable shortcuts.

When you add a Home favorite, Avenor stores that launchable identity, its favorite-list or ribbon destination, its order, applicable list-level presentation setting, and the order of its containing list or ribbon where applicable locally on the device. A temporary edit-mode Undo snapshot may retain the most recently removed favorite, complete vertical list, or complete ribbon only until its Snackbar is replaced, dismissed, or invalidated by the documented navigation and lifecycle boundaries; it is not retained as undo history. Application inventory and shortcut information that is not part of a saved favorite is processed as needed and is not retained as Avenor-owned application-usage history.

Avenor also reads current system information needed for its interface and controls, including time, date, system language, default-Launcher state, and whether the optional Avenor accessibility service is enabled and connected. Time and date are not retained as history, and these system states are not used for behavioral analytics.

### Storage, backup, and deletion

Favorites remain on the device until you remove them or Android clears Avenor's application data. Avenor does not currently provide an in-app clear-all action, export, restoration, cloud synchronization, cloud backup, or device-to-device transfer of its application data. Android cloud backup and device-transfer backup must be disabled for Avenor-owned favorite data under the current product contract.

If stored favorite data cannot be read reliably, Avenor preserves the original unreadable data and disables favorite changes rather than silently replacing it with an empty list. You can retry the read. Clearing Avenor's application data through Android system settings or uninstalling Avenor removes its locally stored application data; the current product does not provide recovery afterward.

### Data collection, sharing, and network use

Avenor does not provide an account, advertising, analytics, crash-report upload, cloud service, or Avenor-operated server. It does not transmit, sell, or share the locally processed information described above. Core Home, Drawer, application launching, and Settings behavior remains available without a network connection.

Selecting a GitHub link asks Android to open the fixed URL with a system browser or another compatible application. Avenor sends only that URL to the selected handler. The external application and any network service it uses operate under their own privacy practices.

### Double-tap lock and accessibility access

Double-tap lock is optional and remains inactive unless you enable Avenor's purpose-limited accessibility service in Android system settings. Avenor uses that service only to request one system lock action after you double-tap eligible blank space on Home.

The service does not retrieve screen or window content, inspect other applications' interfaces, collect accessibility events for analytics, observe application usage, automate background actions, or send or share data. It performs no global action other than the explicit lock request. Avenor does not use Device Administrator as a fallback. You can disable the service at any time in Android accessibility settings without losing independent Launcher functionality.

### Permissions and profile boundary

Avenor uses only Android capabilities required by its current product contract. It does not request `ACCESS_HIDDEN_PROFILES`, access or display Android Private Space entries that require that permission, or provide Private Space management. Ordinary, work-profile, and cloned launchable entries may appear when Android exposes them within Avenor's current role and least-privilege boundary.

### Contact

For privacy questions, open the Avenor Launcher GitHub Issues page:

`https://github.com/max-archer-m/avenor-launcher/issues`

Selecting this address uses an implicit system browser action. If no compatible handler can open it, Avenor shows the localized short message `Unable to open privacy contact` and keeps the Privacy presentation open.

## Double-tap-lock prominent disclosure

The following disclosure is displayed separately immediately before an enable-oriented handoff to Android accessibility settings. It is not merged into the Privacy Bottom Sheet or another unrelated disclosure.

### Title

`Accessibility access for double-tap lock`

### Body

`Avenor uses Android AccessibilityService only to lock the screen when you double-tap eligible blank space on Home. The service does not access or collect personal or sensitive data. It does not read screen or window content, observe activity in other applications, collect accessibility events, or send or share data. Double-tap lock is optional, and you can disable the service at any time in Android settings without affecting other Launcher features.`

### Actions

- `Cancel` closes the disclosure and stays in Avenor without opening system settings.
- `Agree and continue` records no retained acknowledgement and only confirms the current handoff to Android accessibility settings. Android remains authoritative for whether the service is enabled.
- Dismissing the disclosure through Back, a scrim action, or another cancellation path is not consent and does not open system settings.

## Change boundary

The Privacy statement and prominent disclosure must be reviewed and updated before Avenor adds or changes any network-backed feature, account, advertising, analytics, crash reporting, monitoring SDK, cloud or device-transfer behavior, data category, external recipient, permission, accessibility purpose, Private Space support, distribution channel, or other behavior that would make the current text incomplete.

Future public-store distribution requires a renewed privacy, security, platform-policy, listing, consent, and specialist review. The current text does not claim Google Play or another distributor has approved the application or its accessibility-service use.
