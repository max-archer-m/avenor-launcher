# Avenor Launcher 1.0.0 Delivery Contract

> Semantic source: English. Chinese counterpart: [README.zh-CN.md](README.zh-CN.md).
>
> This version-level contract integrates the approved [1.0.0 product scope](product-scope.md) and the evidence-limited [technical assessment](technical-assessment.md) under the project's [version, artifact, and release governance](../../release.md). It links the included iteration contracts rather than duplicating their delivery details.
>
> This document does not authorize implementation, architecture decisions, signing, artifact movement, a Git tag, a GitHub Release, or public distribution. Each iteration requires its own contract and explicit project-author authorization.

## Version outcome

`1.0.0` delivers the first installable, verified, minimum usable Avenor Launcher APK for the project author's daily use.

The complete version outcome is the offline journey selected by the [product scope](product-scope.md): use Avenor as an Android Home application, move between Home and Drawer, locate and launch a platform-exposed application, add it as a persistent favorite, launch it from Home, and remove the favorite.

- Android `versionName`: `1.0.0`
- Android `versionCode`: `1`

Documentation, an intermediate iteration, or a local build does not create the formal version. The formal version exists only after all included product increments and version-level evidence requirements are complete.

## Included and excluded scope

The [1.0.0 product scope](product-scope.md) is authoritative for included behavior, exclusions, product acceptance intent, and detailed current-product references. This contract selects that scope without redefining it.

The product-scope exclusions remain excluded from `1.0.0`, including deferred Settings behavior, favorite reordering, platform application shortcuts, uninstall and clone-removal actions, manual language selection, complete favorite-corruption recovery behavior, broader device adaptation, network or account capability, analytics, later capability layers, and public distribution.

If implementation or validation evidence conflicts with included behavior, the affected work stops at the contract mismatch. The technical role records the evidence and impact; the product manager and project author decide any product-scope or acceptance change. An iteration may not silently convert a failed requirement into an implementation limitation.

## Technical feasibility boundary

The [technical assessment](technical-assessment.md) concludes that the selected scope is feasible, subject to repository and device evidence. Its candidate directions are not approved architecture merely because they appear in the assessment or this contract.

Confirmed version constraints include:

- `minSdk` 31 and `targetSdk` 36, unless an authorized product-contract update changes them.
- The Android project is created at the product-repository root when implementation is authorized.
- Core behavior remains local and offline, without account, server, cloud synchronization, analytics, or network dependency.
- The approved baseline excludes `QUERY_ALL_PACKAGES`, `ACCESS_HIDDEN_PROFILES`, `INTERNET`, cloud backup, and device-to-device transfer.
- User-facing strings, colors, and reusable dimensions are resource-backed, with English default and Simplified Chinese resources.

Candidate technical directions such as stable API 37 `compileSdk`, the exact JDK/Gradle/AGP/Kotlin/Compose combination, single-activity Compose, a project-owned `LauncherApps` boundary, profile-plus-component identity, Proto DataStore, coroutines, and dependency injection must be validated just in time by the first product iteration that depends on them. Consequential proven choices belong in architecture documentation or ADRs; ordinary implementation details remain in code and tests.

## Included product-delivery iterations

The preliminary delivery sequence contains six cumulative product increments:

| Iteration | Product increment | Primary completion evidence |
| --- | --- | --- |
| [Iteration 1](../../iterations/iteration-1-home-minimum-usable-surface.md) | Home minimum usable surface | Installable Home candidate with localized time/date behavior on the required foundation environments |
| [Iteration 2](../../iterations/iteration-2-drawer-application-discovery-and-launch.md) | Drawer application discovery and launch | Home-to-Drawer entry, platform-exposed application list, recovery, and exact-entry launch |
| [Iteration 3](../../iterations/iteration-3-drawer-navigation-and-live-state-completeness.md) | Drawer navigation and live-state completeness | Product-complete grouping, index, transition gestures, updates, and stable state |
| [Iteration 4](../../iterations/iteration-4-application-action-sheet-and-favorite-creation.md) | Application action sheet and favorite creation | Modal application information and persistent add-to-Home behavior |
| [Iteration 5](../../iterations/iteration-5-favorite-lifecycle-and-resilience.md) | Favorite lifecycle and resilience | Complete add, launch, restart, remove, reconciliation, and offline favorite loop |
| [Iteration 6](../../iterations/iteration-6-compatibility-quality-and-formal-apk-closure.md) | Compatibility, quality, and formal APK closure | Full environment matrix, measured quality, signed APK, traceability, and archive evidence |

Each linked record defines one observable product increment, its exclusions, technical change areas, dependencies, and validation plan. The sequence is cumulative: a later iteration preserves all accepted behavior from earlier iterations unless an authorized contract change says otherwise.

## Version-level dependencies and decisions

Before an iteration begins mutation work:

1. the preceding iteration gate must be satisfied or an explicit author decision must record why a dependency is changed;
2. the iteration contract must be reviewed against the current product scope and implementation evidence;
3. unresolved product mismatches must be transferred to the product manager and project author;
4. consequential technical decisions must identify whether an ADR is required; and
5. the project author must explicitly authorize that iteration's implementation.

Project-author decisions reserved across the version include:

- approval of this prospective delivery boundary and sequence;
- authorization and acceptance of each iteration;
- any `minSdk`, product-scope, or acceptance-intent change;
- API 36 `compileSdk` fallback after evidence that the stable API 37 candidate is not reproducible;
- any product-visible license surface required by qualified review;
- acceptance of an OEM limitation;
- absolute measured performance, memory, power, and stability thresholds;
- release-keystore creation, custody, backup, and signing;
- external APK storage and retention policy; and
- any tag, milestone declaration, GitHub Release, artifact upload, or public distribution.

Approval of this prospective delivery plan does not approve candidate architecture or declare evidence-dependent gates satisfied.

## Validation and exit gates

### Required environments

| Environment | Required purpose |
| --- | --- |
| Android 12/API 31 emulator | Minimum-SDK functional compatibility; not OEM or authoritative performance evidence |
| Samsung Galaxy S23 Ultra on Android 16/API 36 | Samsung Home, clone/profile, badge, launch, gesture, persistence, and complete-journey evidence |
| Google Pixel 8 on Android 17/API 37 | Current platform Home, profile, launch, gesture, persistence, performance, and complete-journey evidence |

An emulator does not substitute for either physical device. Validation records identify the environment, OS/API, build identity, source commit, APK SHA-256, procedure, result, and retained evidence location.

### Deterministic gates

- The project builds reproducibly through documented commands that have succeeded in the repository.
- Release lint and all selected automated tests pass.
- The merged release manifest contains no unapproved permission, component, backup behavior, or dependency-contributed declaration.
- The complete selected journey passes offline in every required environment.
- No acceptance run contains an Avenor-caused crash, ANR, accidental launch, duplicate favorite, silent favorite deletion, or data overwrite.
- Inventory, identity, launch, persistence, restoration, localization, accessibility, navigation, gesture, and failure behavior match the linked product contract.
- The resolved release dependency graph and license obligations are recorded and resolved without an unapproved product-scope change.

### Evidence-dependent gates

Iteration 6 records repeatable physical-device distributions for cold start, time to full display, frame behavior on critical interactions, memory, idle power, and stability. It retains the measurement procedure and generated evidence. The project author approves absolute exit thresholds only after those measurements exist.

A baseline profile is included only when reproducible release-build evidence shows material benefit. It is not an automatic version requirement.

## Artifact, signing, and archive requirements

- The formal artifact is one installable, verified release APK with `versionName` `1.0.0` and `versionCode` `1`.
- The exact APK filename is recorded from the final build rather than invented before build configuration exists.
- The APK corresponds exactly to the recorded source commit and final validation build identity.
- The externally archived APK has a computed and reverified SHA-256 digest.
- The formal APK uses the stable release-signing identity established for `1.0.0`; signing actions require separate author authorization.
- The archive records only the release certificate SHA-256 fingerprint, never a keystore, key, password, signing-property file, or other secret.
- Before formal completion, the author controls secure signing storage and at least two independent encrypted backups of the keystore and recovery information.
- The APK remains outside the product repository beneath `../max-dev-context` using an author-approved logical directory and retention policy.
- This contract does not authorize copying, committing, uploading, or distributing the APK.

After completion, move the integrated contract, supporting version inputs, and original iteration records into `docs/archives/v1.0.0/`, update affected links, and record the identifiers, source commit, included iterations, important changes, migrations, environment evidence, limitations, APK filename and logical location, APK SHA-256, release-certificate fingerprint, and any separately approved tag or GitHub Release.

`1.0.0` does not require a tag, milestone declaration, GitHub Release, remote upload, store action, or public distribution.

## Known limitations and legacy issues

The completion record distinguishes product-scope exclusions from observed limitations, unresolved defects, toolchain or measurement constraints, dependency obligations, technical debt, migration work, and later-version follow-up.

An included acceptance failure cannot be relabeled as a known limitation to close the version. A limitation that changes product behavior or acceptance intent requires product-manager review, project-author approval, and an update to the applicable product contract.

Each remaining item records its affected environment and behavior, user impact, evidence, disposition, follow-up destination, and supported workaround. If no item remains, the completion record states that evidence-backed conclusion.

## Completion result

`1.0.0` may be declared complete only when:

1. all six included iteration contracts are closed with traceable acceptance evidence;
2. the complete selected product journey and deterministic gates pass in every required environment;
3. measured quality distributions satisfy author-approved thresholds;
4. dependency, license, manifest, security, privacy, compatibility, and OEM findings are resolved or explicitly dispositioned without an unapproved product change;
5. the signed release APK, version identifiers, source commit, APK SHA-256, signing-certificate fingerprint, build identity, external location, and validation evidence refer to the same artifact;
6. limitations and legacy issues do not conceal a failed included requirement;
7. the project author accepts the factual completion evidence; and
8. the completed version and iteration records are moved into `docs/archives/v1.0.0/` with links updated.

Until those facts exist, this section defines closure evidence and does not claim that `1.0.0` has been built, validated, signed, archived, or completed.
