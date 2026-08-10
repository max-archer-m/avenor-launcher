# Avenor Launcher 1.0.0 Technical Assessment

> Semantic source: English. Chinese counterpart: [technical-assessment.zh-CN.md](technical-assessment.zh-CN.md).
>
> This assessment evaluates the feasibility and delivery constraints of the approved [1.0.0 product scope](product-scope.md). It does not change product scope, authorize implementation or release activity, or turn a proposed technical choice into an approved durable architecture decision. The project author retains decision authority.

## Assessment question

Can the selected `1.0.0` journey be delivered as a maintainable Android Home application on the documented API 31 through API 37 device range, using a least-privilege, local-first architecture that can evolve for the next three to five years without importing deferred product behavior into the first version?

The assessment concludes that the scope is technically feasible. Feasibility remains evidence-limited until the platform-specific launcher inventory and identity behavior is validated on the two recorded physical devices, the selected build-tool combination is proven in the repository, and measured performance gates are established before version closure.

## Inputs and evidence

### Product and project inputs

- [Product foundation requirements](../../requirements/product-foundation.md)
- [Product navigation](../../product/navigation.md)
- [Home interaction](../../product/home.md)
- [Drawer interaction](../../product/drawer.md)
- [Application action sheet](../../product/app-action-sheet.md)
- [Product design foundations](../../product/design-foundations.md)
- [Version and release governance](../../release.md)
- [1.0.0 product scope](product-scope.md)

The product contract, not this assessment, remains authoritative for user-visible behavior. In particular, `minSdk` 31 is a current product contract rather than a permanent compatibility promise; raising it requires separate project-author approval.

### Platform and tool evidence

The recommendations are based on current Android platform and AndroidX documentation, including:

- Android Home role and [`RoleManager`](https://developer.android.com/reference/android/app/role/RoleManager)
- Launcher inventory and launch operations in [`LauncherApps`](https://developer.android.com/reference/android/content/pm/LauncherApps)
- [Package visibility](https://developer.android.com/training/package-visibility)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) and its [release history](https://developer.android.com/jetpack/androidx/releases/datastore)
- [Android backup rules](https://developer.android.com/identity/data/autobackup)
- [Android Gradle Plugin 9.2 release notes](https://developer.android.com/build/releases/agp-9-2-0-release-notes)
- [Compose Bill of Materials guidance](https://developer.android.com/develop/ui/compose/bom)
- [Macrobenchmark guidance](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview) and [captured metrics](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-metrics)

Documentation establishes API contracts but does not prove OEM behavior. Samsung clone behavior, profile exposure, badge availability, and exact launch behavior therefore remain device-validation evidence rather than assumed platform facts.

## Platform and compatibility findings

### SDK range

- `minSdk` is 31.
- `targetSdk` is 36 for `1.0.0` unless the product contract is changed separately.
- The first build-tool spike must evaluate stable API 37 as `compileSdk`.
- API 36 remains the fallback only if API 37 requires preview or release-candidate tooling, or the selected stable dependency set cannot be reproduced with it.
- No implementation may raise `minSdk` without separate project-author approval.

The minimum-version emulator provides deterministic API 31 compatibility coverage. It is not evidence for OEM clone behavior or authoritative performance measurement.

### Android Home capability

The main activity must be exported and qualify as a Home destination through an `ACTION_MAIN` intent filter containing `CATEGORY_HOME` and `CATEGORY_DEFAULT`. A separate `CATEGORY_LAUNCHER` entry keeps Avenor directly launchable while it is not the selected Home application.

Home selection is system-owned. The implementation may invoke the system Home-role request or applicable system settings path only where required by the selected product journey. It must not create a custom default-Launcher confirmation surface or add the deferred Settings entry.

Activity and task behavior must be validated for all of the following:

- direct application launch while Avenor is not the default Home;
- system Home invocation while Avenor is the default;
- repeated Home invocation without duplicate activities or an invalid task stack;
- process recreation returning to Home rather than restoring Drawer, a sheet, or an in-progress gesture;
- ordinary Back behavior when Avenor is not the default Home; and
- no visible Back effect on Home when Avenor is the default.

The exact activity `launchMode`, task flags, and intent handling remain implementation-level choices until the spike demonstrates the required behavior on all recorded environments.

### Launchable-entry inventory

`LauncherApps` is the preferred platform boundary for inventory, labels, icons, badges, callbacks, and launching. The inventory source is launchable activities exposed by Android for an applicable user/profile, not a list of every installed package.

The implementation must not declare `QUERY_ALL_PACKAGES`. It must not declare `ACCESS_HIDDEN_PROFILES`, query Private Space, or interpret the intentional absence of Private Space entries as an inventory error.

Ordinary, work-profile, and cloned entries are included only when Android exposes them within this least-privilege boundary. Each returned activity is an independent launchable entry. Package name alone is not an adequate identity or deduplication key.

Inventory reads and platform callbacks must feed one repository-owned snapshot. UI code must not independently query `PackageManager` or `LauncherApps`, because independent reads would make ordering, disappearance confirmation, retry behavior, and favorite reconciliation inconsistent.

### Platform destinations

Clock, Calendar, application information, and application launch are external platform operations. Each must be resolved or attempted defensively and report the product-defined localized failure without crashing.

Clock and Calendar use implicit intents and must not target a vendor package. Application information must address the selected package while retaining the selected launchable identity for the refresh performed after return. Application launch should use the launcher-aware platform operation for the selected user/profile where available.

## Proposed system boundaries

### Project and module shape

Start with one production Android application module at the repository root. Do not create feature modules, a domain module, or a generic platform abstraction solely for hypothetical scale.

A separate benchmark or baseline-profile test module may be added after the critical journey exists and only if it is needed to produce version-exit evidence. This exception keeps test instrumentation out of the production artifact without turning the product into a premature multi-module system.

### Runtime boundaries

The proposed runtime boundaries are:

1. **UI and interaction** — Compose rendering, accessibility semantics, action sheet, list/index interaction, and gesture arbitration.
2. **Presentation state** — screen state, loading/error states, transition state, duplicate-activation suppression, and lifecycle collection.
3. **Application inventory** — the only owner of `LauncherApps` and related package/profile callbacks; produces stable immutable snapshots.
4. **Favorites** — ordered favorite identities, add/remove rules, persistence, read failure, and inventory reconciliation.
5. **Platform actions** — Home-role request, Clock, Calendar, application information, and application launch.

Dependencies point inward from Android adapters to small project-owned models and interfaces. Product rules such as duplicate prevention, stable ordering, and confirmed-disappearance removal remain testable without a device.

### UI architecture

Jetpack Compose is the preferred UI toolkit. One activity owns a single Avenor surface state rather than separate Home and Drawer activities. Home and Drawer should be composable states within one transition container so the continuous drag, opacity, list-boundary transfer, cancellation, and reverse-direction contract share one source of truth.

The transition requires a project-owned controller using Compose state and animation primitives. A stock navigation animation or two unrelated scroll handlers cannot express the documented continuous handoff. The controller must arbitrate pointer ownership among:

- Home favorite-list scrolling;
- Home-to-Drawer dragging;
- Drawer list scrolling;
- Drawer-to-Home dragging after the list reaches its top boundary; and
- the alphabet index, which exclusively owns its active pointer sequence.

The controller is a narrow product-specific component, not a reusable gesture framework. Deferred favorite reordering must not be implemented in `1.0.0`, but the pointer-arbitration boundary must avoid making later reorder support require replacement of the entire surface model.

### Concurrency and state

Use Kotlin coroutines and `Flow`/`StateFlow` for asynchronous platform and persistence work. UI observes immutable state with lifecycle-aware collection. Inventory refresh and favorite mutation require explicit serialization so callback storms, Retry, and user actions cannot publish out-of-order state.

No network client, account layer, background synchronization service, analytics SDK, or server interface belongs in `1.0.0`.

## Data, identity, persistence, and migration

### Launchable identity

The candidate persisted identity is:

- a stable representation of the Android user/profile, preferably the `UserManager` serial number rather than an in-process `UserHandle` hash;
- the exact `ComponentName` of the launchable activity; and
- an explicit schema version at the persisted document level.

Display name, grouping key, icon, badge, enabled state, and availability are derived inventory data and must not be persisted as favorite truth.

The device spike must verify that the candidate distinguishes primary, cloned, and ordinarily exposed profile entries on the recorded Samsung and Pixel devices. If Android does not expose a durable serial/component combination for a device-specific clone, the technical assessment must be amended rather than silently falling back to package-name identity.

### Favorite persistence

Proto DataStore is the preferred persistence candidate because it provides an explicit typed schema, ordered repeated entries, atomic updates, and a migration path. Stable DataStore releases are preferred; alpha dependencies are excluded from the production baseline.

The initial schema should contain only data needed to restore identity and order. It must not store application usage, timestamps, cached icons, labels, analytics, or historical inventory.

`1.0.0` defers the complete corruption-recovery product behavior. The implementation nevertheless must preserve these architectural invariants:

- a successful empty read is distinguishable from a read failure;
- a failed read is never converted into an empty write;
- Retry is read-only;
- mutations require a successfully loaded state; and
- the original unreadable file is not automatically replaced or cleared.

If the selected DataStore integration cannot preserve those invariants without implementing excluded recovery UI, Preferences DataStore or a project-owned serializer must be reconsidered before implementation.

### Reconciliation

A favorite remains stored during loading, inventory failure, and transient launch failure. Automatic removal requires a successful inventory refresh that confirms the exact persisted identity has permanently disappeared.

Reconciliation must be deterministic and independently unit-tested. It must not infer permanent disappearance from a single failed launch, missing icon, missing label, callback ordering, locked profile, or failed inventory read.

### Storage and backup

Favorites use credential-encrypted application-private storage. Avenor is not `directBootAware`; no current user journey requires favorites before first unlock.

The product excludes cloud synchronization and cross-device backup. The manifest and Android 12+ data-extraction rules must therefore explicitly prevent favorites from entering cloud backup or device-to-device transfer. This is a product privacy boundary, not merely a default configuration choice.

No migration from a pre-1.0 production schema exists. Schema evolution support is still required so later versions can migrate the `1.0.0` file without destructive replacement.

## Permissions, security, privacy, and licensing impact

### Manifest and permission baseline

The expected baseline contains:

- exported Home/launcher activity declarations required for platform entry;
- package-visibility queries only for the included implicit Clock and Calendar destinations if platform resolution requires them;
- no `INTERNET` permission;
- no `QUERY_ALL_PACKAGES` permission;
- no `ACCESS_HIDDEN_PROFILES` permission;
- no usage-access, notification-listener, contacts, location, files, photos, or accessibility-service capability; and
- explicit backup exclusion.

The final merged manifest must be inspected because dependencies may contribute declarations that are not visible in the source manifest.

### Security and privacy

All external intents and launcher operations cross a platform boundary and must tolerate absence, revocation, disabled entries, and `SecurityException`. Failure handling must not expose raw exception text to users or persist diagnostic history.

Favorites are local user-content data even though they contain no message or file content. They must remain in application-private storage and must not be logged in full in release builds. Profile identifiers and component names should be treated as application-inventory metadata and kept within the same local boundary.

### Dependency licenses

The initial dependency inventory is expected to contain AndroidX/Compose, Kotlin, DataStore, protocol-buffer runtime if Proto DataStore is selected, and potentially Hilt/Dagger and KSP. Exact artifacts and transitive runtime contents must be generated from the resolved release dependency graph.

The product currently excludes the Settings surface and its Third-party License entry. Therefore dependency selection must satisfy one of these conditions before `1.0.0` integration:

1. all required notices may legally be included in repository and packaged artifact metadata without a user-visible in-app entry; or
2. the project author approves a product-scope change that introduces a suitable notice surface.

The technical role cannot decide that legal question alone. A resolved dependency inventory and qualified license review are required before accepting the affected implementation and closing the version.

## Dependencies and alternatives

### Recommended baseline

- JDK 17.
- Gradle 9.4.1 with Android Gradle Plugin 9.2, subject to the toolchain spike.
- Kotlin through AGP's supported built-in Kotlin path where compatible.
- Stable Jetpack Compose libraries managed through the stable Compose BOM.
- Stable Activity Compose, Lifecycle, Core, and DataStore releases compatible with the selected SDK/toolchain.
- Kotlin coroutines.
- Proto DataStore with protobuf-lite, subject to persistence-invariant and license review.
- Hilt with KSP only if the exact stable combination builds and tests cleanly.

Versions must be locked in a version catalog and Gradle wrapper when the project is created. “Latest” is a research policy, not a reproducible build declaration.

### Alternatives retained

- **Manual dependency injection instead of Hilt:** preferred if Hilt/KSP adds disproportionate toolchain risk for the small initial runtime graph. Constructor injection and one application composition root preserve a later Hilt migration path.
- **Preferences DataStore instead of Proto DataStore:** acceptable only if a documented encoding preserves ordered typed identities, distinguishes read failure from empty state, and provides explicit migration behavior.
- **API 36 compile SDK instead of API 37:** allowed only under the fallback already defined by product scope.
- **Views instead of Compose:** not recommended. It would reduce immediate Compose-specific toolchain coupling but increases the cost of the continuous transition, gesture coordination, and future UI iteration without producing a product benefit.

No dependency is approved merely because it appears in this assessment. The resolved graph, license, minimum SDK, manifest contribution, release maturity, and replacement cost must be recorded during the build-foundation iteration.

## Build and validation approach

### Build foundation

The first implementation iteration must establish a reproducible project at the repository root with:

- Gradle wrapper and version catalog;
- one application module;
- explicit `minSdk`, `targetSdk`, and `compileSdk`;
- release and debug build types;
- resource-backed user-facing strings, colors, and reusable dimensions;
- English default resources and Simplified Chinese resources;
- unit-test and instrumented-test foundations;
- dependency locking or equivalent resolved-version evidence; and
- commands documented only after they run successfully in the actual project.

The local workstation currently has no project wrapper or authoritative build command. Tool availability and Android SDK packages must therefore be established by the build-foundation iteration rather than assumed by this document.

### Test layers

1. **Local unit tests:** identity encoding, locale grouping and tie-breaking, favorite add/remove/deduplication, reconciliation, state reduction, activation throttling, and transition release decisions.
2. **Compose/UI tests:** Home, Drawer, loading/error/Retry, action sheet, localized resources, Back behavior within Avenor, pointer cancellation, and accidental-activation prevention.
3. **Instrumented platform tests:** manifest entry, persistence across recreation, external intent failure, inventory callback integration, and merged-manifest assertions where practical.
4. **Manual device validation:** Home selection, actual Home behavior, cloned/profile identities, platform badge treatment, Clock/Calendar/application information, system bars, haptics, restart behavior, and OEM-specific failure cases.
5. **Macrobenchmark on physical devices:** cold startup, time to full display, Drawer transition, Drawer scrolling, alphabet-index movement, and return to Home. Emulator benchmark numbers are diagnostic only.

### Required environments

- Android 12/API 31 emulator for minimum-SDK functional compatibility.
- Samsung Galaxy S23 Ultra on Android 16/API 36 for the recorded Samsung and clone behavior.
- Google Pixel 8 on Android 17/API 37 for current platform behavior.

Validation evidence must record device identifier, OS/API level, build identity, source commit, APK digest, test procedure, and result. A passing emulator does not substitute for either physical device.

## Quality-gate proposals

### Deterministic gates

The following are required and do not depend on a future performance baseline:

- the project builds reproducibly with the documented wrapper and JDK;
- release lint and all selected automated tests pass;
- the merged release manifest contains no unapproved permission or component;
- the complete included journey passes offline in every required environment;
- no observed crash, application-not-responding event, accidental application activation, duplicate favorite, silent favorite deletion, or data overwrite occurs in the recorded acceptance runs;
- English, Simplified Chinese, and English fallback resources cover every included user-visible string;
- process recreation and device restart preserve valid favorite identity and order; and
- a failed inventory read or launch does not delete a favorite.

### Measured performance gates

Absolute performance numbers cannot be responsibly fixed before an installable implementation is measured on the target physical devices. The validation iteration must produce repeatable distributions for:

- cold-start time to initial display and time to full display;
- frame-overrun percentiles for Home-to-Drawer, Drawer scrolling, index sliding, and Drawer-to-Home;
- memory after cold Home start and after completing the full journey; and
- idle power behavior sufficient to demonstrate that Avenor performs no polling or background network work.

Macrobenchmark results must include multiple iterations and retain the generated JSON and trace evidence. The project author must approve the resulting absolute exit thresholds before `1.0.0` can close. Until then, performance feasibility is provisional rather than failed.

A baseline profile is added only if measured release-build evidence shows a material improvement to the critical journey and its generation can be reproduced. It is not a substitute for fixing avoidable startup work or recomposition/jank problems.

## Delivery risks and unresolved decisions

### Blocking validation risks

- Samsung may expose cloned entries, badges, or user/profile identities differently from AOSP assumptions.
- A platform callback may not by itself distinguish temporary unavailability from permanent disappearance; reconciliation may require a successful full snapshot.
- The exact AGP 9.2, built-in Kotlin, Compose, KSP, Hilt, and protobuf-plugin combination has not yet been built in this repository.
- Proto DataStore license obligations may require a notice mechanism even though Settings is excluded from `1.0.0`.
- Gesture arbitration is the highest custom-UI risk and needs an early vertical slice on real touch hardware.
- Absolute performance, memory, and power thresholds require implementation measurements and author approval.

### Decisions reserved for the project author

- any increase to `minSdk`;
- any change to the approved `1.0.0` product scope;
- acceptance of API 36 fallback if the API 37 candidate fails the toolchain spike;
- introduction of a user-visible license surface if qualified review requires it;
- final measured performance thresholds; and
- acceptance of known OEM limitations discovered by physical-device validation.

Hilt versus manual dependency injection is an implementation decision unless it materially changes delivery risk, dependency/license obligations, or the approved schedule. The technical role should select the smaller proven option after the build spike and record the result.

## Iteration recommendations

1. **Home minimum usable surface** — create the reproducible project, prove SDK/tool versions and Home qualification, and deliver the localized Home information surface and safe platform destinations.
2. **Drawer application discovery and launch** — prove the launcher inventory and identity boundary, deliver the core Drawer list and Retry states, and launch exact platform-exposed entries.
3. **Drawer navigation and live-state completeness** — complete grouping, alphabet-index behavior, live updates, position preservation, and real-touch Home/Drawer gesture arbitration.
4. **Application action sheet and favorite creation** — deliver the modal application actions and prove ordered, non-duplicated favorite creation and persistence.
5. **Favorite lifecycle and resilience** — complete favorite launch, removal, restart persistence, reconciliation, transient-failure handling, and non-destructive read-failure behavior.
6. **Compatibility, quality, and formal APK closure** — execute the full matrix, measure physical-device performance, resolve licenses, decide whether a baseline profile is justified, and prepare the signed formal APK and version-exit evidence under separate authorization.

Each iteration requires its own current contract and explicit implementation authorization. The sequence minimizes the chance that OEM identity behavior or gesture feasibility is discovered only after the entire UI is built.

## Product-scope impact proposals

No product-scope change is proposed by this assessment.

The following findings may later require a separately approved proposal, but they do not modify `1.0.0` now:

- a qualified license conclusion requiring an in-app Third-party License surface;
- inability to identify a cloned entry durably without a product-visible limitation;
- an API 37 toolchain failure requiring the already permitted API 36 fallback; or
- a device limitation that prevents an included behavior from being met as written.

Private Space remains outside the current product contract. Corruption detection, source-data preservation UI, read-only recovery, and related disabled-action states remain explicitly deferred from `1.0.0` even though the persistence architecture must avoid destructive overwrite.

## Assessment conclusion

Avenor Launcher `1.0.0` is technically feasible with a modern, maintainable Android architecture and without broad package visibility, hidden-profile access, network capability, or premature modularization.

The recommended direction is a single-activity Compose application, a project-owned `LauncherApps` inventory repository, stable profile-plus-component identity, ordered local DataStore persistence, explicit backup exclusion, and layered automated plus physical-device validation.

This conclusion authorizes neither implementation nor integration. The current delivery contract and iteration sequence remain prospective: version closure still requires repository build evidence, the resolved dependency and license inventory, physical-device clone/profile validation, and author-approved measured performance gates.
