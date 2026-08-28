# Requirements Brief: Product Foundation and Current Scope

> Public semantic source: English. Chinese authoring counterpart: [product-foundation.zh-CN.md](product-foundation.zh-CN.md). Keep both versions materially aligned.

## Responsibility

This document defines the product problem, evidence boundary, target user, current capability scope, cross-feature requirements, data boundary, and product-level acceptance guardrails. It does not repeat page interaction flows or exact presentation values. Those facts belong to the linked product contracts under [Contract routing](#contract-routing).

## Problem

The author uses an Android phone as their primary device and commonly uses Samsung and Google phones. The Launcher is used whenever the author enters the home screen or needs to find an application. There is no reliable usage-duration or frequency measurement yet.

Existing products do not jointly satisfy the author's preference for a Home-and-Drawer structure, deliberate layout constraints, low visual noise, and personally useful customization. The author therefore wants an Android Launcher that first serves their own long-term daily use.

## Evidence boundary

### Known facts and author experience

- The author has long-term Android experience and has used stock and third-party Launchers.
- Current problem evidence consists of the author's sustained experience and understanding of their own needs.
- No usage counts, task-duration data, interviews, external-user feedback, or market research are available.

### Hypotheses to validate

- Limited and intentional layout rules may make an attractive, comfortable, and minimal daily Home easier to sustain than unrestricted customization.
- Whether other Android users share the same need is unverified.
- Whether the product should later be published through GitHub or application stores is unverified.

## Project motivation

- Product motivation: create a personally owned home-screen tool suitable for the author's long-term daily use.
- Learning motivation: use a real project to learn agent systems and build agent-engineering experience.

Learning goals may shape process and retrospectives, but they do not replace user value, product acceptance, or quality standards.

## Target user and scenarios

The primary user is the author. Product trade-offs are evaluated first against reliable daily use by that person. Ergonomic decisions primarily optimize for right-hand holding with right-thumb input and left-hand holding with right-hand tapping; other postures are secondary.

There are no identified secondary users. Possible future publication does not demonstrate broader demand or expand the current audience.

Primary scenarios are:

- Enter the Android default Home.
- Move from Home to Drawer, find an application, and launch it.
- Open Settings and configure capabilities needed for basic Launcher utility.
- Complete these core tasks without a network connection.

## Product goal

Provide the author with an Android Launcher suitable for long-term daily use that is fully local and follows least-privilege principles, using Home, Drawer, and Settings to complete the basic home-screen utility loop.

The current scope covers utility, not mass-market suitability, extensive customization, or network information. The author's desire for capabilities unavailable or paid elsewhere is an expectation, not market evidence or a current acceptance criterion.

## Platform and delivery boundary

- Minimum supported version: Android 12 (API 31).
- Current physical validation devices: Samsung Galaxy S23 Ultra on Android 16 and Google Pixel 8 on Android 17.
- Minimum-version validation environment: an Android 12/API 31 emulator unless a representative physical device becomes available.
- Device scope: ordinary Android phones in portrait orientation only.
- Device exclusions: landscape, foldable, tablet, desktop-mode, and external-display adaptation.
- Distribution boundary: maintain the GitHub project for the author's daily use, with no application-store submission, GitHub Release APK, website APK, or other public distribution.
- Store target-API, review, and data-disclosure requirements are not current delivery gates; review them if public distribution enters scope.

The product compatibility boundary requires `minSdk` 31. Current configured `compileSdk`, `targetSdk`, and version values are maintained in the [development guide](../development.md); public-distribution target-API gates are maintained in [release governance](../release.md). This current product requirement does not preserve a historical version's SDK-selection instructions. Reviewed: 2026-08-28.

## Current product scope

### In scope

- Home: one non-pageable, non-collapsible, non-vertically-scrolling default Home containing system time, date, favorite content, and Drawer entry.
- Favorites: up to two equal-status vertical favorite lists and up to five horizontal favorite bars. A launchable identity can appear in only one destination. Vertical lists persist one large, medium, or small list-level presentation value; new and migrated lists start at medium.
- Favorite editing: explicit add, remove, move, reorder, resize, confirmation, cancellation, and latest-removal Undo behavior defined by the Home and Drawer contracts.
- Double-tap lock: an optional, purpose-limited accessibility-service capability for eligible blank Home space. It does not gate independent Launcher paths.
- Drawer: every launchable entry successfully read from Android-exposed sources, including cloned and work-profile entries when available, with ordinary-mode local application-name search and a user-selected Transparent or Frosted-glass background. A fresh configuration defaults to Frosted glass. An isolated non-current-profile read failure does not block usable entries from other profiles.
- Application actions: application information, applicable Launcher favorite actions, edit entry, platform shortcuts, and uninstall only when the platform can safely address the selected identity.
- Settings: default-Launcher state and system destination, double-tap-lock disclosure, Privacy, licenses, repository link, and version information as applicable.
- English and Simplified Chinese resources selected from system locale, with English fallback.
- Offline availability for core tasks, local core-data storage, and only permissions traceable to necessary core capabilities.

### Out of scope

- Visual polish whose primary purpose is aesthetic refinement.
- Widgets, folder-like grouping, Home paging, themes, and unrestricted layout or visual customization.
- One-action clearing, export, cloud deletion, or restoration of all local configuration.
- Network-backed information such as weather.
- Accounts, cloud synchronization, a self-hosted server, or cross-device backup.
- Behavioral analytics, automatic ordering, recommendations, AI assistance, or agent integration.
- Android Private Space access, presentation, management, favorites, or restoration, including declaration of `ACCESS_HIDDEN_PROFILES`.
- Business-model validation, formal store release, or mass-market adaptation.

Out-of-scope capabilities are excluded from the current contract, not permanently rejected. Advertising, recommendation feeds, and engagement-maximizing design remain constrained by the project overview.

## Additive requirements

Landscape support, foldable and tablet adaptation, themes, weather information, and widgets may be evaluated as future additive capabilities. Folder-like grouping conflicts with the current direct-access principle and is excluded.

An additive capability does not enter current scope merely because it can be built independently, and it does not bypass product, privacy, security, validation, or maintenance review.

## Contract routing

| Product question | Authoritative contract |
| --- | --- |
| Home content, favorite lifecycle, editing, dragging, and failure behavior | [Home interaction specification](../product/surfaces/home.md) |
| Home exact layout, typography, geometry, and visual states | [Home presentation specification](../product/presentation/home.md) |
| Drawer inventory, identity, search, sorting, selection, index behavior, and states | [Drawer interaction specification](../product/surfaces/drawer.md) |
| Drawer exact layout, typography, geometry, and visual states | [Drawer presentation specification](../product/presentation/drawer.md) |
| Application action availability, order, dismissal, and results | [Application action sheet](../product/surfaces/app-action-sheet.md) |
| Application action sheet exact geometry | [Application action sheet presentation](../product/presentation/app-action-sheet.md) |
| Settings content, navigation, modal behavior, and state refresh | [Settings interaction specification](../product/surfaces/settings.md) |
| Settings exact typography and row geometry | [Settings presentation specification](../product/presentation/settings.md) |
| Cross-surface gestures, transitions, and system-return behavior | [Navigation](../product/navigation.md) |
| Double-tap-lock disclosure, permission boundary, and denial behavior | [Double-tap lock](../product/features/double-tap-lock.md) |
| User-visible privacy and data-handling statement | [Privacy and data handling](../product/features/privacy.md) |
| Spatial hierarchy and major content-order sketches | [Low-fidelity wireframes](../product/low-fidelity-wireframes.md) |

## Functional requirements

- Avenor can serve as the Android Home entry point and exposes the Home–Drawer–Settings utility loop.
- Home displays system time and date, locally saved favorites, and launches the selected identity.
- Favorite identities distinguish primary, cloned, and work-profile entries and are not deduplicated solely by package name.
- Favorite mutations are explicit, locally persisted, and preserve a valid destination and order without duplication or silent redirection.
- Drawer presents, locally searches, and launches the reliable inventory available within Avenor's Android role and least-privilege boundary.
- Inventory refresh, partial-source failure, permanent disappearance, Loading, and Error behavior preserve usable paths and do not convert uncertain data into destructive conclusions.
- Application actions and Settings open only destinations valid for the selected identity and report defined local failures without corrupting favorite state.
- Double-tap lock requests the system lock action only after the user enables the applicable accessibility service and performs the defined gesture in an eligible area.
- English and Simplified Chinese are complete supported resource sets; unsupported locales fall back to English without a manual in-app language selector.
- Core Home, Drawer, application launch, and Settings tasks remain usable offline.

Detailed interaction outcomes and exact presentation values are acceptance requirements through the authoritative contracts in the routing table; they are not duplicated here.

## Non-functional requirements

- Core paths must not crash, become unresponsive, or corrupt local configuration because of Avenor.
- Least privilege is mandatory. Permission denial may degrade only the dependent capability.
- User-visible strings are localizable, and system font scaling remains applicable.
- Current data remains local; no account, analytics, server, or cloud synchronization is introduced.
- Validation claims must identify actual evidence and follow [the validation guide](../validation.md).

Minimum acceptable performance, power, memory, and startup-response thresholds are `To be decided`. No separate quality-baseline document currently establishes numeric gates, so these thresholds are not product-level completion conditions unless an applicable delivery explicitly selects and evidences them.

## Product-level acceptance criteria

- The author can complete the Home–Drawer–Settings loop on the supported device boundary without a network connection.
- Selecting a visible launchable identity launches the intended application.
- Drawer does not omit, duplicate, or misidentify entries returned by successfully read Android sources; isolated failed sources may be absent without blocking available Content.
- Ordinary Drawer search filters the reliable local application inventory by displayed application name without network access, changing the selected arrangement, or introducing a second application order.
- Private Space identities requiring hidden-profile access remain outside inventory and permission declarations.
- Favorite identity, destination, order, list size, and container order survive applicable recreation without unexpected loss, duplication, or reassignment.
- Failed or uncertain reads remain distinguishable from valid empty data and do not overwrite the last reliable favorite state.
- Permission denial or revocation affects only the dependent capability and leaves independent Launcher paths available.
- English, Simplified Chinese, and English fallback resolve according to system locale.
- Applicable surface behavior and presentation contracts are satisfied for the delivery's explicitly selected scope and evidence baseline.
- Results intended as daily-use acceptance are observed on the author's applicable physical devices; implementation completion alone is insufficient.

## Success assessment and guardrails

Core success means that Home, Drawer, and Settings form a reliable minimum utility loop for the author's ongoing daily use. Success is judged by the author's observed experience; uncollected external-user opinions are not evidence. “Attractive, comfortable, and minimal” has not yet been converted into observable standards and is not a current acceptance target.

## User control

The product lets users maintain Home favorites and use individual Settings entries. It does not provide one-action clearing, export, cloud deletion, or restoration of all configuration. Users can clear application data through Android system settings. Favorite changes result from explicit user actions, and language follows system locale rather than observed behavior.

## Local data boundary

- Durable user-content data consists of stable favorite identity, destination, position, vertical-list size, and favorite-container order. A stable identity has one destination and cannot be duplicated across lists or bars.
- Durable local configuration also includes the selected Drawer application size, name placement, items-per-row count, section-anchor presentation, and background mode. Each valid change is saved as one complete display-setting state.
- Primary, cloned, and work-profile identities must remain distinguishable and must not be stored or deduplicated solely by package name.
- The latest edit-mode removal snapshot is transient Undo state, not durable undo history.
- Time and date come from the device system and are not retained historically.
- Avenor does not collect or store accessibility window content or events, notifications, contacts, location, clipboard content, files, photos, stable device identifiers, application-usage history, or analytics events.
- Avenor has no account, cloud synchronization, server, cloud backup, or cross-device backup. Android cloud backup and device-to-device transfer backup remain disabled for Avenor-owned favorite and display-setting data until an author-approved restoration contract exists.

## Dependencies and risks

- The Android Home role, application-enumeration approach, cloned-entry visibility, and related declarations or permissions require continuing technical and privacy review.
- Future Private Space support requires a separate author-approved capability and renewed interaction, permission, privacy, compatibility, and validation review.
- Current evidence represents only the author and cannot support a mass-market demand conclusion.
- Learning agent systems may encourage process or technical complexity beyond product needs; control this through explicit scope changes.
