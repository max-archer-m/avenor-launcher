# Avenor Launcher 1.0.0 Delivery Contract

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This version-level contract integrates the approved [1.0.0 product scope](product-scope.md) and the evidence-limited [technical assessment](technical-assessment.md) under the project's [version, artifact, and release governance](../../release.md). It links the included iteration contracts rather than duplicating their delivery details.
>
> This document does not authorize implementation, architecture decisions, signing, artifact movement, a Git tag, a GitHub Release, or public distribution. Each iteration requires its own contract and explicit project-author authorization.

## Version outcome

`1.0.0` delivers the first installable, verified, minimum usable Avenor Launcher APK for the project author's daily use.

The complete version outcome is the offline journey selected by the [product scope](product-scope.md): use Avenor as an Android Home application, move between Home and Drawer, locate and launch a platform-exposed application, add it as a persistent favorite, launch it from Home, and remove the favorite.

- Android `versionName`: `1.0.0`
- Android `versionCode`: `1`
- Android `applicationId`: `com.avenor.launcher`

Documentation, an intermediate iteration, or a local build does not create the formal version. The formal version exists only after all included product increments and version-level evidence requirements are complete.

## Delivery level

`1.0.0` uses the `Author daily-use baseline` level defined by [release governance](../../release.md). It is not a `Formal release artifact`.

The version therefore closes on accepted daily-use evidence from the author's designated primary physical device and accurate source/APK traceability. API 31 and Pixel validation, the complete automated matrix, performance distributions and absolute thresholds, formal release signing and keystore backups, release-wide specialist license conclusions, release-level digest evidence, tags, publication, and public distribution remain recommended or deferred work rather than `1.0.0` completion gates.

## Included and excluded scope

The [1.0.0 product scope](product-scope.md) is authoritative for included behavior, exclusions, product acceptance intent, and detailed current-product references. This contract selects that scope without redefining it.

The product-scope exclusions remain excluded from `1.0.0`, including deferred Settings behavior, favorite reordering, platform application shortcuts, uninstall and clone-removal actions, manual language selection, complete favorite-corruption recovery behavior, broader device adaptation, network or account capability, analytics, later capability layers, and public distribution.

If implementation or validation evidence conflicts with included behavior, the affected work stops at the contract mismatch. The technical role records the evidence and impact; the product manager and project author decide any product-scope or acceptance change. An iteration may not silently convert a failed requirement into an implementation limitation.

## Technical feasibility boundary

The [technical assessment](technical-assessment.md) concludes that the selected scope is feasible, subject to repository and device evidence. Its candidate directions are not approved architecture merely because they appear in the assessment or this contract.

Confirmed version constraints include:

- `minSdk` 31 and `targetSdk` 36, unless an authorized product-contract update changes them.
- The current approved Android application identity is `com.avenor.launcher`. A later change is allowed only by explicit project-author decision and after recording its installation, upgrade, signing, data-continuity, distribution, and migration consequences.
- The Android project is created at the product-repository root when implementation is authorized.
- Core behavior remains local and offline, without account, server, cloud synchronization, analytics, or network dependency.
- The approved baseline excludes `QUERY_ALL_PACKAGES`, `ACCESS_HIDDEN_PROFILES`, `INTERNET`, cloud backup, and device-to-device transfer.
- User-facing strings, colors, and reusable dimensions are resource-backed, with English default and Simplified Chinese resources.

Candidate technical directions such as stable API 37 `compileSdk`, the exact JDK/Gradle/AGP/Kotlin/Compose combination, single-activity Compose, a project-owned `LauncherApps` boundary, profile-plus-component identity, Proto DataStore, coroutines, and dependency injection must be validated just in time by the first product iteration that depends on them. Consequential proven choices belong in architecture documentation or ADRs; ordinary implementation details remain in code and tests.

## Included product-delivery iterations

The completed delivery contains six cumulative product increments:

| Iteration | Product increment | Primary completion evidence |
| --- | --- | --- |
| [Iteration 1](iteration-1-home-minimum-usable-surface.md) | Home minimum usable surface | Installable Home candidate with localized time/date behavior on the required foundation environments |
| [Iteration 2](iteration-2-drawer-application-discovery-and-launch.md) | Drawer application discovery and launch | Home-to-Drawer entry, platform-exposed application list, recovery, and exact-entry launch |
| [Iteration 3](iteration-3-drawer-navigation-and-live-state-completeness.md) | Drawer navigation and live-state completeness | Product-complete grouping, index, transition gestures, updates, and stable state |
| [Iteration 4](iteration-4-application-action-sheet-and-favorite-creation.md) | Application action sheet and favorite creation | Modal application information and persistent add-to-Home behavior |
| [Iteration 5](iteration-5-favorite-lifecycle-and-resilience.md) | Favorite lifecycle and resilience | Complete add, launch, restart, remove, reconciliation, and offline favorite loop |
| [Iteration 6](iteration-6-compatibility-quality-and-formal-apk-closure.md) | Daily-use baseline closure | Primary-device core-journey acceptance, source/APK traceability, known-gap recording, and completed version evidence |

Each linked record defines one observable product increment, its exclusions, technical change areas, dependencies, and validation plan. The sequence is cumulative: a later iteration preserves all accepted behavior from earlier iterations unless an authorized contract change says otherwise.

## Version-level dependencies and decisions

Before an iteration begins mutation work:

1. the project author must accept the preceding implementation foundation as sufficient to continue or explicitly change the dependency sequence; completion of every preceding recommended validation scenario is not required for entry;
2. the iteration contract must be reviewed against the current product scope and implementation evidence;
3. unresolved product mismatches must be transferred to the product manager and project author;
4. consequential technical decisions must identify whether an ADR is required; and
5. the project author must explicitly authorize that iteration's implementation.

Because this is currently an author-maintained personal project, iteration validation scenarios are recommended evidence rather than default iteration entry or completion gates. An iteration may become `Completed` with incomplete recommended validation when it satisfies the iteration status rules and records every known gap. Missing checks remain unknown, not passed. This flexibility does not relax the `1.0.0` author daily-use baseline gates below.

Project-author decisions reserved across the version include:

- approval of this prospective delivery boundary and sequence;
- authorization and acceptance of each iteration;
- any `minSdk`, product-scope, or acceptance-intent change;
- API 36 `compileSdk` fallback after evidence that the stable API 37 candidate is not reproducible;
- acceptance of an OEM limitation;
- the author-designated primary physical device and accepted daily-use result;
- optional release-keystore creation, custody, backup, and signing when separately pursued;
- external APK retention when the author chooses to retain the baseline artifact; and
- any tag, milestone declaration, GitHub Release, artifact upload, or public distribution.

Approval of this prospective delivery plan does not approve candidate architecture or declare evidence-dependent gates satisfied.

## Validation and exit gates

### Required environment

| Environment | Required purpose |
| --- | --- |
| Author-designated primary physical device; currently Samsung Galaxy S23 Ultra on Android 16/API 36 | Installation and the complete selected offline Home, Drawer, launch, action-sheet, and favorite journey for daily use |

Record the device, OS/API, application identifiers, source commit, APK or build identity when available, procedure, result, and known limitations. Android 12/API 31 emulator and Google Pixel 8/API 37 evidence remain recommended compatibility work and do not block this delivery level.

### Deterministic gates

- One installable APK has `applicationId` `com.avenor.launcher`, `versionName` `1.0.0`, and `versionCode` `1`, and is traceable to the recorded source commit.
- The complete selected journey passes offline on the author-designated primary physical device.
- No accepted primary-device run contains an Avenor-caused crash, ANR, accidental launch, duplicate favorite, silent favorite deletion, or data overwrite in the included journey.
- Inventory, identity, launch, persistence, restoration, localization, navigation, gesture, and failure behavior needed by the selected daily-use journey match the linked product contract on that device.
- Every known unperformed check, limitation, unresolved defect, permission or dependency concern, and compatibility gap is recorded without being represented as passed.
- The implementation and delivery documentation are committed and synchronized to the author-designated shared Git history.

### Recommended evidence that does not block `1.0.0`

API 31 and Pixel compatibility, complete automated tests and release lint, merged-manifest and resolved-dependency review, repeatable performance/memory/power distributions, absolute quality thresholds, baseline-profile evaluation, and qualified license conclusions remain valuable follow-up evidence. Missing results remain unknown. A discovered included-path failure on the designated primary device must still be resolved or explicitly change the product contract before completion.

## Artifact, signing, and completed-record requirements

- The daily-use baseline is one installable APK with `applicationId` `com.avenor.launcher`, `versionName` `1.0.0`, and `versionCode` `1`.
- Changing `applicationId` before the daily-use baseline is accepted replaces the planned identity only after author approval. Changing it after the baseline exists creates a distinct Android application identity and must not be represented as an ordinary in-place upgrade.
- Record the exact APK or build identity available from the accepted installation and the corresponding source commit; do not invent unavailable evidence.
- Development signing or another author-controlled signing identity is acceptable for this level. Record any resulting update or reinstall limitation. Stable release signing, certificate fingerprinting, secure custody, and two encrypted backups are deferred until a formal release artifact is pursued.
- If the author retains the APK, keep it outside the product repository and record its logical location. External retention, SHA-256 calculation, and copying remain separately authorized actions rather than completion requirements.
- This contract does not authorize copying, committing, uploading, or distributing the APK.

The integrated delivery record, supporting version inputs, and original iteration records remain together in the stable `docs/delivery/1.0.0/` directory. This completed record contains the delivery level, identifiers, source commit, included iterations, important changes, primary-device evidence, limitations, and available APK/build identity. No retained artifact location, release digest, or certificate fingerprint was reported or required for this level.

`1.0.0` does not require a tag, milestone declaration, GitHub Release, remote upload, store action, or public distribution.

## Known limitations and legacy issues

The completion record distinguishes product-scope exclusions from observed limitations, unresolved defects, toolchain or measurement constraints, dependency obligations, technical debt, migration work, and later-version follow-up.

An included acceptance failure cannot be relabeled as a known limitation to close the version. A limitation that changes product behavior or acceptance intent requires product-manager review, project-author approval, and an update to the applicable product contract.

Each remaining item records its affected environment and behavior, user impact, evidence, disposition, follow-up destination, and supported workaround. If no item remains, the completion record states that evidence-backed conclusion.

## Completion result

`1.0.0` is complete at the `Author daily-use baseline` level.

- All six included iterations are `Completed` with their evidence retained in this stable version directory.
- The accepted APK uses `applicationId` `com.avenor.launcher`, `versionName` `1.0.0`, and `versionCode` `1`, and represents source commit `053b6b7da58a27a9c237d98c2e49f7a94e5b1d3e`.
- The project author accepted the complete selected offline journey on a Samsung Galaxy S23 Ultra running Android 16/API 36 and reported no known core-path blocker.
- The APK uses an author-local private signing identity. A later in-place update must use the same identity; update continuity cannot be assumed if that identity becomes unavailable.
- The exact Gradle and installation commands, APK filename, digest, retained artifact location, and command output were not reported.
- API 31 and Pixel compatibility, the complete automated matrix, performance distributions, merged-manifest and resolved-dependency review, qualified license conclusions, formal security and privacy review, release-signing custody and backup, and formal-release-artifact evidence remain unknown or unperformed follow-up rather than passed results.
- The version boundary was declared because the complete minimum offline Home, Drawer, application launch, action-sheet, and persistent favorite journey was accepted for the author's ongoing daily use.
- No tag, milestone, GitHub Release, upload, publication, or public distribution was created or required.

The implementation and Iteration 6 closure record are committed in the single development history. The commit containing this completed delivery update synchronizes the version record; Git history is authoritative for that identifier.
