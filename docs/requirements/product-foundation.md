# Requirements Brief: Product Foundation and Current Scope

> Public semantic source: English. Chinese authoring counterpart: [product-foundation.zh-CN.md](product-foundation.zh-CN.md). Keep both versions materially aligned.
>
## Problem

The author uses an Android phone as their primary device and commonly uses Samsung and Google phones. The Launcher is used whenever the author enters the home screen or needs to find an application or obtain information from it. There is no reliable usage-duration or frequency measurement yet.

The author prefers a custom Home page combined with an application Drawer, but current alternatives do not satisfy all of the following needs:

- Stock launchers and competing products commonly use lists or free-form grids for applications and widgets.
- Highly flexible products allow extensive customization but provide too few constraints to sustain an attractive, comfortable, and minimal result.
- Minimal products may reduce visual noise without matching the author's preferred Home-and-Drawer combination or personal layout needs.

The author therefore wants an Android Launcher with deliberate design constraints that first meets their own long-term daily needs.

## Evidence boundary

### Known facts and author experience

- The author has long-term Android experience and uses Android as their primary phone platform.
- The author has used stock home-screen applications and multiple third-party Launchers as alternatives.
- Current problem evidence consists of the author's sustained usage experience and understanding of their own needs.
- No usage counts, task-duration data, interviews, external user feedback, or market research are currently available.

### Hypotheses to validate

- Limited and intentional layout rules may make an attractive, comfortable, and minimal daily home screen easier to sustain than unrestricted customization.
- Whether Android users other than the author share the same need is unverified.
- Whether the product should later be published and maintained through GitHub or application stores is unverified.

## Project motivation

- Product motivation: create a personally owned home-screen tool suitable for the author's long-term daily use.
- Learning motivation: use a real project to learn agent systems and build experience toward agent-engineering or prompt-engineering work.

The learning motivation may shape the project process and retrospectives, but it must not replace user value, product acceptance, or quality standards.

## Target user and scenarios

### Primary user

The author. Current product trade-offs are evaluated first against whether the author can use the product reliably every day.

### Secondary users

There are no identified secondary users. Possible future publication on GitHub or in application stores does not demonstrate broader demand or expand the current target audience.

### Primary scenarios

- Enter the Android default home screen.
- Move from Home to the Drawer, find an installed application, and launch it.
- Open Settings and configure what is necessary to preserve the Launcher's basic utility.
- Continue completing the preceding core tasks without a network connection.

Usage frequency, task duration, and the exact types of information obtained from the home screen have not been measured. The current Settings items are defined in the current product scope.

## Product goal

Provide the author with an Android Launcher suitable for long-term daily use that is fully local and follows least-privilege principles, using Home, Drawer, and Settings to complete the most basic home-screen utility tasks.

The current product scope covers utility, not visual refinement, extensive customization, network information, or mass-market suitability.

The author ultimately wants personalization that fits their own needs and capabilities that some existing alternatives either lack or place behind payment. This is an author expectation and hypothesis, not completed market evidence or a current acceptance criterion.

## Platform and delivery boundary

- Minimum supported version: Android 12 (API 31).
- Current physical validation devices: Samsung Galaxy S23 Ultra on Android 16 and Google Pixel 8 on Android 17.
- Minimum-version validation environment: an Android 12/API 31 emulator unless a representative physical device becomes available.
- Device scope: ordinary Android phones in portrait orientation only.
- Device exclusions: landscape, foldable, tablet, desktop-mode, and external-display adaptation.
- Distribution boundary: maintain the GitHub project for the author's daily use, with no application-store submission, GitHub Release APK, website APK, or other public distribution.
- Store target-API, review, and data-disclosure requirements do not apply to the current distribution boundary; review them if public distribution becomes part of the current scope.

Android 17 corresponds to API 37 according to the [Android 17 SDK setup guide](https://developer.android.com/about/versions/17/setup-sdk). Starting August 31, 2026, Google Play requires new applications and updates to target at least Android 16 (API 36), according to the [Google Play target API requirement](https://developer.android.com/google/play/requirements/target-sdk); this constrains `targetSdk`, not the project's `minSdk`. Because this project does not currently publish to Google Play, its target-API requirement is a future compatibility reference rather than a current release gate. The product compatibility boundary requires `minSdk` 31. For `1.0.0`, the technical assessment must validate `targetSdk` 36 and use the latest stable compatible `compileSdk`; API 37 is the current candidate, with API 36 retained if API 37 still requires preview or release-candidate tooling. The selected values must be implemented and verified in the build configuration when the Android project is created. Reviewed: 2026-08-09.

## Current product scope

### In scope

- Home: the Android home entry displaying system time, date, favorite applications, and a Drawer entry.
- Favorites: the author can maintain the application entries shown on Home according to the Home and application-action-sheet specifications.
- Drawer: a single-list presentation of every launchable entry exposed by Android, including cloned application entries when the platform exposes them.
- Settings: current default-Launcher status, an entry to Android system settings for the default home application, and secondary product-information entries. Language follows supported system-locale resources automatically; manual language selection is outside the current scope.
- Complete offline availability for core tasks.
- Local storage of core product data without a self-hosted server, account, or cloud synchronization.
- Only system permissions that are necessary for, and traceable to, core functionality.

### Out of scope

- Visual refinement whose primary goal is aesthetic quality.
- Widgets, folders, themes, and extensive or unrestricted layout and visual customization.
- One-action clearing or restoration of all local configuration.
- Network-backed information such as weather.
- Accounts, cloud synchronization, or a self-hosted server.
- Behavioral analytics, automatic ordering, recommendations, AI assistance, or agent integration.
- Business-model validation, formal store release, or mass-market adaptation.

“Out of scope” means excluded from the current product contract, not permanently rejected. Advertising, recommendation feeds, and engagement-maximizing design remain constrained by the long-term boundaries in the project overview.

## Additive requirements

An additive requirement may be added to the current product contract when useful without defining or blocking movement between capability layers. Current candidates include landscape support, foldable and tablet adaptation, themes and colors, weather information, widgets, and folders.

An additive requirement does not become current scope merely because it can be added independently, and it does not bypass scope, privacy, security, validation, or maintenance review.

## Functional requirements

- The user can launch Avenor Launcher as the Android home-screen entry point.
- Home displays time and date supplied by the device system.
- Home displays locally saved favorite application entries and allows the selected favorite to be launched.
- The user can access the Drawer from Home.
- Whether or not Avenor Launcher is selected as the default home application, the Drawer presents a single list of every application entry that should be visible and launchable under Android platform rules and allows the selected entry to be launched.
- When Android exposes a cloned application entry to the Launcher, the Drawer presents it as an independent launchable entry and does not deduplicate solely by package name.
- The Launcher provides English and Simplified Chinese user-visible resources, selects them from the system locale automatically, and falls back to English for unsupported locales.
- The user can open Android system settings for the default home application from Settings.
- The current product does not provide one-action clearing or restoration of all local configuration; users edit configuration through individual settings.
- The core Home, Drawer, and Settings paths do not depend on a network, account, cloud synchronization, or a self-hosted server.
- If a network-backed capability such as weather is added later, its unavailable, unauthorized, or offline state does not block core paths.

## Non-functional requirements

- **Local first:** Current product state and core data remain on the device.
- **Offline capable:** Home, Drawer, application launching, and core Settings remain available without a network connection.
- **Least privilege:** Every permission maps to a current core capability and records its purpose, trigger, denial behavior, and distribution-policy impact.
- **Minimum visibility:** The current product requires only discovery and launching of application entries exposed to a Launcher by Android; unrestricted access to all installed-package data is outside the current scope. Determine concrete APIs, manifest declarations, and permissions through technical and privacy review.
- **Reliability:** The current product must support the author's ongoing daily use on their actual primary devices; measurable stability thresholds remain unresolved.
- **Compatibility:** Support portrait use on ordinary phones from Android 12/API 31 through Android 17/API 37. Validate the minimum boundary on an API 31 emulator and validate primary daily-use behavior on at least the two recorded physical devices running API 36 and API 37.

## Acceptance criteria

- Given Avenor Launcher is selected as the home application on a supported Android device, when the user performs the system action to return home, then the system displays Avenor Launcher Home.
- Given the user enters Home, when core content finishes loading, then Home displays system time, date, saved favorite application entries, and a Drawer entry.
- Given the user is on Home, when the user performs the defined Drawer-entry action, then the Drawer presents a single list of application entries exposed by Android.
- Given Avenor Launcher is not selected as the default home application, when the user launches Avenor Launcher directly and enters the Drawer, then the Drawer still displays every application entry that should be visible and launchable under Android platform rules.
- Given Android exposes cloned entries for an application, when the Drawer presents the application list, then every cloned entry appears independently and can be launched.
- Given a launchable application appears in the Drawer, when the user selects it, then the system launches that application.
- Given the system locale is English or Simplified Chinese, when Avenor presents or refreshes its interface, then it uses the corresponding resource set; given another locale, it uses the English fallback.
- Given the user opens Settings, when the user selects the default-home settings entry, then the system opens the corresponding Android settings page.
- Given the device has no network connection, when the user accesses Home, Drawer, application launching, or core Settings, then the core task remains completable.
- Given a non-core network capability is unavailable or unauthorized, when the user performs a core task, then that capability does not block Home, Drawer, application launching, or core Settings.
- Given a system permission is considered for the current product, when requirements and distribution review occur, then the permission is traceable to a necessary core capability and its denial behavior.

## Success assessment and guardrails

### Confirmed direction

- Core product success means that Home, Drawer, and Settings form a minimum utility loop suitable for the author's ongoing daily use.
- Development completion alone is not sufficient evidence of success; results must be observed on the author's real primary devices.
- Success is judged by the author's own daily experience; uncollected external-user opinions are not acceptance evidence.
- Core paths must not crash or become unresponsive because of Avenor Launcher.
- The Drawer must not omit, duplicate, or incorrectly display applications that should be visible and launchable under Android platform rules.
- Selecting an application must launch the intended application.
- Local configuration must not be unexpectedly lost or corrupted.
- Permission denial may degrade only the dependent capability and must not block other core tasks.
- Home, Drawer, application launching, and core Settings must remain available while the device is offline.

Minimum acceptable performance, power, memory, and startup-response levels will be defined in the quality baseline and are not completion conditions for this product-foundation baseline.

## User control

The current product lets users maintain Home favorites and use individual Settings entries, but it does not provide one-action clearing, export, cloud deletion, or restoration of all configuration. Users can clear application data through Android system settings. Favorite changes must result from explicit user actions. Language presentation follows the system locale rather than adapting from observed behavior.

## Local data boundary

- The only user-content data is the identifier of each launchable application entry saved as a Home favorite. Identifiers must distinguish cloned entries exposed by the platform and must not store or deduplicate solely by package name.
- System-locale-derived language presentation and other interface settings in the current product contract are local behavior, not behavioral analytics data.
- Time and date come directly from the device system and are not retained as historical data.
- The current product does not collect or store notifications, contacts, location, clipboard content, files, photos, stable device identifiers, application-usage history, or analytics events.
- The current product has no account, cloud synchronization, server, or cross-device backup.

## Dependencies and risks

- The Android home role, application-enumeration approach, cloned-entry visibility, and related manifest declarations or permissions still require technical and privacy review.
- Settings includes current default-Launcher status, an entry to system default-home settings, and the product-information entries defined in the Settings interaction specification. Manual application-language selection is outside the current scope.
- Current evidence represents only the author and cannot support a mass-market demand conclusion.
- “Attractive, comfortable, and minimal” has not been converted into observable standards and is not a current acceptance target.
- Using the project to learn agent systems may encourage process or technical complexity beyond product needs; control this through explicit scope changes.
