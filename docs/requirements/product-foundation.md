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

Ergonomic decisions primarily optimize for right-hand holding with right-thumb input and left-hand holding with right-hand tapping. Other postures are secondary rather than equal optimization targets.

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

- Home: one non-pageable, non-collapsible Android home surface displaying system time, date, the primary favorites area, the secondary favorites area, and a Drawer entry. The page does not scroll vertically. The primary favorites area contains primary and companion favorites, whose groups scroll independently only when their content exceeds their visible regions. The full-width secondary favorites area sits below it with `16dp` vertical spacing, has zero height without content, and contains at most five independently horizontal-scrolling ribbons.
- Favorites: the author can maintain the primary and companion application entries shown on Home according to the Home and application-action-sheet specifications. Both groups provide the same direct application access and editing availability; primary favorites represent relatively higher-frequency use and companion favorites represent relatively lower-frequency use.
- Double-tap lock: eligible blank space in Home's basic-information region can request the system lock action after the user explicitly enables Avenor's purpose-limited accessibility service. The service is optional and does not gate independent Launcher paths.
- Drawer: a single-list presentation of every launchable entry successfully read from the sources Android exposes, including cloned application entries when the platform exposes and Avenor successfully reads them. An isolated non-current-profile read failure does not block entries already available from other profiles.
- Settings: current default-Launcher status, an entry to Android system settings for the default home application, and secondary product-information entries. Language follows supported system-locale resources automatically; manual language selection is outside the current scope.
- Complete offline availability for core tasks.
- Local storage of core product data without a self-hosted server, account, or cloud synchronization.
- Only system permissions that are necessary for, and traceable to, core functionality.

### Out of scope

- Visual refinement whose primary goal is aesthetic quality.
- Widgets, folder-like grouping, Home paging, themes, and extensive or unrestricted layout and visual customization.
- One-action clearing or restoration of all local configuration.
- Network-backed information such as weather.
- Accounts, cloud synchronization, or a self-hosted server.
- Behavioral analytics, automatic ordering, recommendations, AI assistance, or agent integration.
- Android Private Space access, presentation, management, favorites, and state restoration, including declaration of `ACCESS_HIDDEN_PROFILES`.
- Business-model validation, formal store release, or mass-market adaptation.

“Out of scope” means excluded from the current product contract, not permanently rejected. Advertising, recommendation feeds, and engagement-maximizing design remain constrained by the long-term boundaries in the project overview.

## Additive requirements

An additive requirement may be added to the current product contract when useful without defining or blocking movement between capability layers. Current candidates include landscape support, foldable and tablet adaptation, themes and colors, weather information, and widgets. Folder-like grouping is excluded because it conflicts with the current direct-access principle.

An additive requirement does not become current scope merely because it can be added independently, and it does not bypass scope, privacy, security, validation, or maintenance review.

## Functional requirements

- The user can launch Avenor Launcher as the Android home-screen entry point.
- Home displays time and date supplied by the device system.
- Home displays locally saved favorite application entries and allows the selected favorite to be launched.
- Home uses content-driven vertical sizing within `16dp` horizontal and vertical outer padding and `16dp` spacing between adjacent visible modules; Home itself does not scroll vertically. Basic information wraps its required content and interaction targets. When the natural primary and populated-secondary heights fit the remaining safe height, both retain natural height and the primary groups remain stationary. When their combined height overflows, the secondary area retains its natural ribbon height and the primary area receives the remaining viewport above it, where only overflowing primary or companion groups scroll vertically. The full-width secondary area has zero height without content.
- Secondary favorites content is user-defined rather than divided by product-defined types. The area contains no more than five `56dp`-high horizontal ribbons under the current fixed limit, with `8dp` vertical spacing between ribbons. Each ribbon contains a finite user-defined application sequence, stays stationary while its entries fit, scrolls horizontally when they overflow, and never wraps cyclically. Each `128dp`-wide entry shows a `40dp` application icon and one static `16sp/24sp` normal-weight application-name line with a `16dp` gap. Entries are left-aligned with `8dp` spacing and use the confirmed `6%` light-foreground background, `1dp` boundary at `12%`, `12dp` corner radius, and current light ripple. Selecting an entry launches its application; long-pressing it opens the existing application action sheet. Ribbon titles remain `To be decided`.
- Each saved launchable-entry identity has exactly one assignment among primary, companion, and secondary favorites and appears at most once across all three groups. Primary, cloned, and work-profile entries remain distinct identities and must not be deduplicated solely by package name. Secondary-favorite addition, already-favorited treatment, and movement between the secondary and other groups remain outside the currently defined interaction boundary.
- The middle composition uses `8dp` internal padding on each side and a `16dp` inter-group gap. Primary favorites use approximately the left 55% and companion favorites the right 45% of the remaining horizontal space. The ratio expresses product hierarchy rather than a user-adjustable split, and neither group borrows unused capacity from the other. Both groups share one viewport height and scroll independently. Every item uses `8dp` internal padding on all sides. Primary favorites use 40dp icons, default 56dp-high interaction targets, and `16sp/24sp` names; companion favorites use 32dp icons, default 48dp-high targets, and `14sp/20sp` names. Both use normal weight, a `16dp` icon-to-name gap, system font scaling, and static one-line names with end ellipsis when needed. Normal and edit modes preserve these item heights. Both groups use the same `24dp` drag-handle graphic in a `48dp`-wide hit region with `12dp` horizontal padding on each side; the hit-region height follows the current item height, and the graphic has no additional right margin beyond the item's existing padding.
- Drawer additions append to primary favorites by default without navigating to Home or entering edit mode. A full visible viewport does not reject an addition: each group stays stationary while its content fits and scrolls independently after overflow. Edit mode adds drag handles and shared editing surfaces around the visible Home modules. A source-stable independent opaque preview supports real-time visible in-group exchange, while cross-group occupied-entry exchange and cross-group gap insertion remain feedback-only until release. Same-group gaps never insert; exchange and insertion feedback are mutually exclusive and use the touch point. The current cross-group operation is finalized and saved on release. Back exits edit mode.
- Double-tapping eligible blank space in Home's basic-information region requests one system lock action only while the user-authorized Avenor accessibility service is enabled. Time, date-and-weekday, system insets, favorites, and all other interactive targets are excluded; authorization absence or revocation affects only this capability.
- An unreadable or damaged favorite persistence result is presented as a recoverable loading failure rather than an empty favorite list. Avenor preserves the original data, disables favorite mutations, and keeps independent Drawer and application-launch paths available while a read-only Retry is offered.
- The user can access the Drawer from Home.
- Whether or not Avenor Launcher is selected as the default home application, the Drawer presents a single list of every application entry successfully read from the visible and launchable sources Android normally exposes within Avenor's current role and least-privilege boundary and allows the selected entry to be launched.
- If a non-current profile cannot be read while another profile produces usable entries, Drawer continues to present and launch the available entries without crashing or replacing the whole surface with Error. The current product does not require a partial-read warning.
- When Android exposes a cloned application entry to the Launcher, the Drawer presents it as an independent launchable entry and does not deduplicate solely by package name.
- Ordinary, work-profile, and cloned launchable entries remain in scope when Android exposes them without hidden-profile access. Private Space entries that require `ACCESS_HIDDEN_PROFILES` are outside the current product scope.
- The Launcher provides English and Simplified Chinese user-visible resources, selects them from the system locale automatically, and falls back to English for unsupported locales.
- The user can open Android system settings for the default home application from Settings.
- The current product does not provide one-action clearing or restoration of all local configuration; users edit configuration through individual settings.
- The core Home, Drawer, and Settings paths do not depend on a network, account, cloud synchronization, or a self-hosted server.
- If a network-backed capability such as weather is added later, its unavailable, unauthorized, or offline state does not block core paths.

## Non-functional requirements

- **Local first:** Current product state and core data remain on the device.
- **Offline capable:** Home, Drawer, application launching, and core Settings remain available without a network connection.
- **Least privilege:** Every permission maps to a current core capability and records its purpose, trigger, denial behavior, and distribution-policy impact.
- **Accessibility-service minimization:** The optional double-tap-lock service is not an accessibility tool, does not request window-content retrieval, does not observe other-application UI for product behavior or analytics, and performs no global action other than the explicit user-triggered system lock request. Device Administrator is not a fallback.
- **Minimum visibility:** The current product requires only discovery and launching of application entries normally exposed to Avenor under its current role and least-privilege boundary; unrestricted access to all installed-package data is outside the current scope. Avenor does not declare `ACCESS_HIDDEN_PROFILES` or expand visibility to Android Private Space. Determine concrete APIs and other necessary manifest declarations or permissions through technical and privacy review.
- **Reliability:** The current product must support the author's ongoing daily use on their actual primary devices; measurable stability thresholds remain unresolved.
- **Compatibility:** Support portrait use on ordinary phones from Android 12/API 31 through Android 17/API 37. Validate the minimum boundary on an API 31 emulator and validate primary daily-use behavior on at least the two recorded physical devices running API 36 and API 37.

## Acceptance criteria

- Given Avenor Launcher is selected as the home application on a supported Android device, when the user performs the system action to return home, then the system displays Avenor Launcher Home.
- Given the user enters Home, when core content finishes loading, then Home displays system time, date, saved favorite application entries, and a Drawer entry.
- Given the secondary favorites area has content, when Home displays it, then it shows no more than five `56dp`-high horizontal ribbons separated by `8dp`, containing user-defined `128dp`-wide launchable application entries without product-defined type grouping. Each entry shows a `40dp` icon and one `16sp/24sp` name line within the confirmed subtle boundary, entries are left-aligned with `8dp` spacing, selecting launches the application, long-pressing opens the existing application action sheet, and each ribbon's horizontal scrolling stops at its explicit first and last entries without wrapping.
- Given a stable launchable-entry identity is already assigned to any favorite group, when favorite state is presented, then that identity does not appear again in the same or another favorite group; a primary, cloned, or work-profile entry with a different stable identity remains independently assignable.
- Given the user adds an application from Drawer, including when the visible primary viewport is already full, then Avenor appends and saves it as a primary favorite, closes the action sheet, preserves the Drawer position, and does not navigate to Home or enter edit mode.
- Given a favorite group's content fits its viewport, when the user drags within that group, then the group does not scroll and an upward drag can enter the Drawer transition; given the content overflows, then the group scrolls first and transfers only same-gesture displacement remaining after its end boundary to the Drawer transition.
- Given the user drags within the source group during edit mode, when the touch point enters another favorite body, then that favorite exchanges visible positions with the source; same-group gaps do not accept insertion, and release over the current valid body finalizes and saves that exchange once.
- Given the user drags into the other group, when the touch point enters an occupied favorite body, then Avenor shows a mutually exclusive cross-group exchange and release finalizes and saves both favorites' group assignments and positions without removing or overwriting either entry.
- Given the user drags into the other group, when the touch point enters a valid gap before the first item, between items, after the last item, or in an empty group, then Avenor shows only an insertion line; release performs and saves that insertion once while preserving the target group's relative order.
- Given an active drag exits through Back or releases over an invalid Home area, then Avenor removes its preview and target feedback, restores the last saved group and order, and does not save that unfinished operation.
- Given the accessibility service is enabled, when the user completes a valid double tap entirely within eligible basic-information blank space, then Avenor requests one system lock action; given the service is disabled or revoked, then the gesture issues no action and every independent Launcher path remains available.
- Given favorite persistence cannot be reliably read, when Home presents the favorite region, then it distinguishes the failure from an empty list, preserves the original data, disables favorite writes, offers a read-only Retry, and keeps Drawer discovery and application launching available; when Retry fails, then the same inline error returns without an additional Toast or data overwrite.
- Given the user is on Home, when the user performs the defined Drawer-entry action, then the Drawer presents a single list of application entries successfully read from the sources Android exposes.
- Given Avenor Launcher is not selected as the default home application, when the user launches Avenor Launcher directly and enters the Drawer, then the Drawer still displays every application entry successfully read from the sources that should be visible and launchable under Android platform rules.
- Given a non-current profile read fails while another profile provides usable entries, when Drawer presents the inventory, then the available entries remain visible and launchable without a crash or full-surface Error; entries from the failed profile may be absent and no partial-read warning is required.
- Given Android exposes cloned entries for an application, when the Drawer presents the application list, then every cloned entry appears independently and can be launched.
- Given an entry requires Android Private Space access through `ACCESS_HIDDEN_PROFILES`, when Avenor builds the Drawer inventory, then Avenor does not request that permission, access or display the entry, create a Private Space container, or provide Private Space visibility, lock, unlock, favorite, or restoration behavior.
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
- The Drawer must not omit, duplicate, or incorrectly display entries returned by successfully read Android sources. Entries from an isolated failed non-current profile may be absent without blocking available entries.
- Selecting an application must launch the intended application.
- Local configuration must not be unexpectedly lost or corrupted.
- Permission denial may degrade only the dependent capability and must not block other core tasks.
- Home, Drawer, application launching, and core Settings must remain available while the device is offline.

Minimum acceptable performance, power, memory, and startup-response levels will be defined in the quality baseline and are not completion conditions for this product-foundation baseline.

## User control

The current product lets users maintain Home favorites and use individual Settings entries, but it does not provide one-action clearing, export, cloud deletion, or restoration of all configuration. Users can clear application data through Android system settings. Favorite changes must result from explicit user actions. Language presentation follows the system locale rather than adapting from observed behavior.

## Local data boundary

- The only user-content data is each launchable application entry saved as a Home favorite together with its primary, companion, or secondary assignment and user-defined position. A secondary favorite additionally belongs to one ribbon and has one position within that ribbon. Each stable entry identity has one favorite assignment and must not be duplicated across groups or ribbons. Entry identifiers must distinguish primary, cloned, and work-profile entries exposed by the platform and must not store or deduplicate solely by package name.
- System-locale-derived language presentation and other interface settings in the current product contract are local behavior, not behavioral analytics data.
- Time and date come directly from the device system and are not retained as historical data.
- The current product does not collect or store accessibility window content or events, notifications, contacts, location, clipboard content, files, photos, stable device identifiers, application-usage history, or analytics events.
- The current product has no account, cloud synchronization, server, cloud backup, or cross-device backup. Android cloud backup and device-to-device transfer backup are disabled for Avenor-owned favorite data until a later author-approved restoration contract defines otherwise.

## Dependencies and risks

- The Android home role, application-enumeration approach, cloned-entry visibility, and related manifest declarations or permissions still require technical and privacy review.
- Future support for Android Private Space requires a separate author-approved product capability and renewed interaction, permission, privacy, compatibility, and validation review; implementation discovery does not place it into the current scope.
- Settings includes current default-Launcher status, an entry to system default-home settings, and the product-information entries defined in the Settings interaction specification. Manual application-language selection is outside the current scope.
- Current evidence represents only the author and cannot support a mass-market demand conclusion.
- “Attractive, comfortable, and minimal” has not been converted into observable standards and is not a current acceptance target.
- Using the project to learn agent systems may encourage process or technical complexity beyond product needs; control this through explicit scope changes.
