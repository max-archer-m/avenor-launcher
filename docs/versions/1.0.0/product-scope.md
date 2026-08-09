# Avenor Launcher 1.0.0 Product Scope

> Semantic source: English. Chinese counterpart: [product-scope.zh-CN.md](product-scope.zh-CN.md).
>
> This document is the product-scope input for version `1.0.0`. It selects a minimum usable subset of the current product contract; it does not replace or narrow that contract.
>
> Detailed behavior remains authoritative in the linked current product documents. Technical feasibility, architecture, implementation sequencing, and validation design belong to the version technical assessment and iteration contracts.

## Version intent

`1.0.0` establishes the first installable, verified, minimum usable Avenor Launcher for the project author's daily use.

The version proves one primary product outcome:

> The author can use Avenor as an Android Home application, move from Home to Drawer, find and launch a platform-exposed application, add it to Home as a persistent favorite, launch it again from Home, and remove the favorite, with the complete core path available offline.

This delivery scope targets the formal application version `1.0.0` under the [version, artifact, and release governance](../../release.md). The formal version exists only after its required completion evidence is satisfied. It does not require a Git tag, GitHub Release, application-store release, public APK distribution, or implementation of the complete current product contract.

## Authoritative product references

The selected behavior is governed by:

- [Product foundation requirements](../../requirements/product-foundation.md)
- [Product navigation](../../product/navigation.md)
- [Home interaction](../../product/home.md)
- [Drawer interaction](../../product/drawer.md)
- [Application action sheet](../../product/app-action-sheet.md)
- [Product design foundations](../../product/design-foundations.md)
- [Product glossary](../../product/glossary.md)

Where this version excludes behavior defined by those documents, the exclusion applies only to `1.0.0`. The behavior remains part of the current product contract and may be selected by a later delivery contract.

## Included user journey

1. The author installs and opens Avenor on a supported Android phone.
2. Android recognizes Avenor as an available Home application, and the author can select it through a system-owned Home-selection path.
3. Home displays the current system time, date, weekday, and the author's saved favorites.
4. The author performs the defined upward gesture to enter Drawer.
5. Drawer presents the launchable entries exposed to Avenor by Android in the defined grouped and locale-aware order.
6. The author locates an entry through list scrolling or the alphabet index and launches the intended application.
7. The author long-presses an entry, opens the application action sheet, and adds that entry to favorites.
8. The favorite appears at the end of Home's favorite list and remains available after process recreation and device restart while the launchable identity remains valid.
9. The author launches the intended application from Home.
10. The author removes the favorite from the application action sheet without removing the application from Drawer.

## Included product scope

### Android Home capability

- Avenor is an installable Android application that the platform can recognize and select as a Home application.
- Avenor remains directly launchable when it is not the current default Home application.
- When selected as the default Home application, the system Home action opens Avenor Home.
- The user completes default-Home selection through an Android system-owned path. `1.0.0` does not provide an Avenor Settings entry for that path.
- Process recreation starts at Home. Transient Drawer position, an open action sheet, and in-progress gestures are not restored after process recreation.

### Home

- Home displays system time without seconds and follows the system 12-hour or 24-hour preference.
- Home displays the locale-formatted system date and weekday.
- Selecting the time invokes an implicit system Clock destination; selecting the date and weekday invokes an implicit system Calendar destination.
- Home displays saved favorites in persistent user-defined order.
- A new installation begins with no favorites and shows the defined invitation to add applications from Drawer.
- Selecting an available favorite launches the intended application and suppresses duplicate rapid activation.
- Long-pressing a favorite opens the included application action sheet behavior.
- A disabled or transiently unavailable favorite remains stored and follows the unavailable and refresh behavior defined by the Home specification.
- A favorite is removed automatically only after a successful inventory refresh confirms that its launchable identity permanently disappeared.
- Inventory or launch failures do not silently delete saved favorites.

### Home and Drawer transition

- An upward drag on Home is the `1.0.0` entry into Drawer.
- A downward drag after the Drawer list reaches its top boundary returns to Home.
- System Back from Drawer returns to Home.
- System Back on Home follows the default-Launcher and non-default-Launcher behavior defined by the navigation specification.
- Gesture progress, completion threshold, fling completion, rebound, position, opacity, list-boundary transfer, cancellation, and pointer-safety behavior follow the current navigation specification.
- Transition gestures must not accidentally launch, long-press, or scroll an application entry.

### Drawer inventory and application launch

- Drawer presents every launchable entry exposed to Avenor by Android, without distinguishing system-installed and user-installed applications as separate product categories.
- When Android exposes primary, cloned, or profile launchable entries, Avenor treats each exposed identity as an independent entry and does not deduplicate solely by package name.
- Entries use the platform-provided application name, icon, and badge when available.
- Drawer uses the locale-aware grouping, stable ordering, section-anchor, and tie-breaking behavior defined by the Drawer specification.
- Drawer includes the `#` anchor and every non-empty A–Z anchor.
- The right-side alphabet index supports direct selection, continuous index sliding, active-character presentation, and the defined haptic response.
- Selecting an entry launches the intended application and suppresses duplicate rapid activation.
- Long-pressing an entry opens the included application action sheet behavior.
- Drawer implements the defined initial loading, empty-or-failed result, Retry, and successful recovery states.
- Inventory updates do not leave a confirmed removed entry launchable from stale UI and preserve stable ordering and position where the applicable anchor remains available.
- Returning to Drawer during the same process preserves its meaningful list position where required by the current specifications.

### Favorites

- `1.0.0` includes adding, removing, storing, restoring, and launching favorites.
- Adding a favorite from Drawer appends it to Home and does not remove it from Drawer.
- Removing a favorite removes only its Home reference and leaves the launchable entry in Drawer.
- The same launchable identity cannot be favorited more than once.
- A primary application and a cloned or profile entry may each be favorited when Android exposes them as distinct launchable identities.
- Every favorite change is caused by an explicit user action except removal after confirmed permanent disappearance of that specific launchable identity.
- Favorite identity and order survive ordinary process recreation and device restart.
- `1.0.0` does not provide user-controlled favorite reordering. Favorites therefore remain in their append order unless an entry is removed.

### Application action sheet

- Long-pressing an application on Home or Drawer opens the modal application action sheet.
- The sheet includes the application identity presentation, application-information action, and the applicable add-favorite or remove-favorite Launcher action.
- Application information opens the system-owned application-information surface and follows the defined failure and return behavior.
- The sheet follows the current dark presentation, scrim, drag handle, modal input blocking, dismissal, action completion, position preservation, and failure-feedback behavior.
- A cloned or profile badge follows the current platform-provided identity treatment when available.
- The sheet does not expose platform application shortcuts, favorite reordering, uninstall, disable, or clone-removal actions in `1.0.0`.
- Excluded actions leave no empty interactive control or placeholder.

### Language and presentation baseline

- Every `1.0.0` user-visible string has English and Simplified Chinese resources.
- Avenor follows the current system locale automatically and falls back to English for unsupported locales.
- Home, Drawer, and the application action sheet use the current dark-theme foreground treatment and transparent Home/Drawer surface and system-bar requests.
- The included surfaces follow the current row, icon, typography, touch-target, inset, system-bar, and resource-backed-value rules.
- Loading, error, retry, unavailable, and failure feedback used by included behavior is localized.
- The `1.0.0` alphabet index contains at most 27 slots: `#` plus A–Z. Because Settings is excluded, it contains no Settings gear and uses no inactive Settings placeholder.
- The included index requires `540dp` of complete available height, calculated as `27 × 20dp` after excluding the insets defined by the Drawer specification. Below that height, the index becomes independently scrollable.

### Local and offline boundary

- Home, Drawer, application launch, favorite management, and included action-sheet behavior do not depend on a network connection, account, cloud synchronization, or self-hosted server.
- Core persisted user content is limited to the launchable-entry identity and order required by favorites.
- `1.0.0` does not collect or store analytics, application-usage history, notifications, contacts, location, clipboard content, files, photos, or historical time and date values.
- Any required Android capability, manifest declaration, permission, or package-visibility rule must be necessary for and traceable to an included capability.
- Denial or absence of a dependent platform capability must not block unrelated included core behavior.

## Explicitly excluded from 1.0.0

The following current-contract behavior is deferred from this version:

- The complete Settings surface and its Drawer gear destination
- Default-Launcher status presentation and the in-application link to system default-Home settings
- Privacy, Avenor License, Third-party License, Project repository, and visible version-information entries
- Favorite reorder mode, drag handles, position swapping, reorder haptics, and reorder auto-scroll
- Platform-provided application shortcuts in the application action sheet
- Uninstall, disable, and cloned-application removal actions
- Manual application-language selection

The following capabilities remain outside the current product contract and are also excluded:

- Widgets, folders, themes, unrestricted visual customization, Drawer grid, blur, and glass effects
- Landscape, foldable, tablet, desktop-mode, and external-display adaptation
- Network-backed information, accounts, cloud synchronization, server development, and cross-device backup
- Behavioral analytics, automatic ordering, recommendations, AI assistance, and agent integration
- Logs, diagnostic export, update checks, payment, advertising, subscriptions, and commercial features
- Public distribution, store submission, website APK distribution, GitHub Release, and mass-market validation

## Product acceptance intent

The product scope is satisfied only when all of the following are observable on the supported validation devices:

- Android can recognize Avenor as a Home application and the author can complete a system-owned selection path.
- Returning Home while Avenor is the default opens Avenor Home without blocking or crashing.
- Home displays the correct system time, date, weekday, empty state, and saved favorites in the applicable locale.
- The defined gesture reliably opens Drawer and the defined reverse paths return to Home without accidental application activation.
- Drawer presents the launchable entries Android exposes to Avenor, including distinct cloned or profile entries when exposed, without product-caused omission, duplication, or unstable ordering.
- List scrolling and the alphabet index allow the author to locate an intended entry, and selecting it launches that exact entry.
- Loading failure is distinguishable from loading, Retry can start another read, and a failed read does not corrupt favorites.
- The author can add a Drawer entry to Home, launch it from Home, remove it, and observe the expected Drawer and Home state after each operation.
- Favorite state survives process recreation and device restart and is not deleted by a transient inventory failure.
- Included core behavior remains completable without network connectivity.
- English, Simplified Chinese, and English fallback behavior apply consistently to every included user-visible string.

Detailed measurable performance, power, memory, startup-response, and stability gates are not invented by this product-scope document. The technical assessment and later validation contract must propose evidence-based thresholds sufficient for the included journey to support the author's daily use.

## Technical assessment inputs

The version technical assessment must determine whether and how the selected scope can be delivered, including at least:

- Android Home-role declarations and the system-owned default-Home selection path
- The minimum application-query, launch, package-visibility, and permission surface
- Platform behavior for primary, cloned, and profile launchable entries on the two target devices
- A stable launchable-entry identity suitable for favorite persistence and reconciliation
- Inventory loading, refresh, package-change, disabled-entry, and stale-entry behavior
- Gesture and scroll arbitration capable of satisfying the observable navigation contract
- Local persistence and process/device restoration behavior
- Resource, locale, theme, system-bar, icon, badge, and haptic implementation constraints
- Safe handling of unavailable Clock, Calendar, application-information, and application-launch destinations
- Required third-party dependencies and any license-notice obligations that would require product-scope reconsideration
- Validation methods and measurable quality gates for both recorded physical devices
- Likely migration cost for later Settings, reorder, shortcut, uninstall, and broader current-contract delivery

A technical constraint may produce a documented product-scope proposal, but it does not silently change this version or the current product contract. Material changes require project-author direction and updates to the applicable authoritative documents.

## Version and release boundary

- `1.0.0` is `versionName` `1.0.0` with `versionCode` `1`.
- A formal `1.0.0` exists only after its included iterations are completed and the exact APK is installable and verified under [release governance](../../release.md).
- The completed version requires the applicable archive, artifact digest, signing fingerprint, source-commit identity, validation evidence, known limitations, and external artifact record required by release governance.
- `1.0.0` does not require or authorize a Git tag.
- `1.0.0` does not require or authorize a GitHub Release, remote artifact upload, store submission, or other public distribution.
- This product-scope document does not authorize implementation, signing-key creation, artifact movement, committing, tagging, pushing, or release actions. Each action remains subject to the project's explicit authorization rules.

## Completion handoff

This document supplies product input to the version technical assessment. After technical assessment is complete, the version `README.md` may integrate the confirmed product scope, technical feasibility, iteration plan, risks, and exit evidence into the complete `1.0.0` delivery contract without duplicating the detailed current product specifications.
