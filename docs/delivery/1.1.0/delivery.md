# Avenor Launcher 1.1.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This delivery selects behavior from the current product specifications. It does not authorize implementation, a product or architecture change, signing, artifact movement, a commit, push, tag, GitHub Release, or distribution.

## Version-contract boundary

This delivery record preserves the scope selected for `1.1.0` at its authorization boundary. The current product contract may continue to change while this version is planned or completed. Later product-contract changes do not retroactively expand this version's selected scope or alter its completion result.

The completion result, when recorded, applies only to the selected `1.1.0` scope, its acceptance criteria, the `Author daily-use baseline`, and the evidence retained by this version record.

## Version intent

`1.1.0` extends the accepted `1.0.0` daily-use baseline with four user-controlled utility increments: full-width primary-favorite editing and reordering, platform application shortcuts from both application contexts, the basic Settings surface, and optional double-tap lock with its authorization and disclosure loop. A final closure increment removes the unnecessary application-name marquee and validates the selected version journey. Companion favorites and the complete two-group edit capability are not selected for this version. This boundary statement does not determine their future product status.

The version remains within V1 fixed presentation. Iteration records state only real delivery dependencies and do not bind their scope to a terminal, contributor, branch, forecast date, or permanent development line. The current execution plan may still use one active task line without changing these contracts.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

- `versionName`: `1.1.0`
- `versionCode`: `2`
- Application identity remains `com.avenor.launcher`.
- Direct upgrade from `1.0.0` must preserve existing favorites and their append order until the user changes that order.

## Product references

- [1.1.0 technical assessment](technical-assessment.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Home](../../product/surfaces/home.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Settings](../../product/surfaces/settings.md)
- [Privacy and data handling](../../product/features/privacy.md)
- [Navigation](../../product/navigation.md)
- [Design foundations](../../product/design-foundations.md)

## Included scope and user journey

The author can edit and reorder at least two full-width primary Home favorites and retain the chosen primary order; invoke platform-provided application shortcuts from the shared action sheet opened on Home or Drawer; open Settings from Drawer to inspect or use the default-home state, Avenor License, project repository, and version information; review the confirmed local Privacy and prominent disclosure content; and optionally authorize double-tap lock through Android system accessibility settings. Application names remain static and use end ellipsis when needed.

All accepted `1.0.0` Home, Drawer, launch, action-sheet, and favorite behavior remains in scope as the compatibility baseline.

## Exclusions

- Companion-favorite presentation, assignment, persistence, and editing, including cross-group movement and swapping.
- Privacy content beyond the confirmed local statement required for the current product and double-tap-lock disclosure boundary.
- Third-party License presentation, pending an authoritative dependency inventory and applicable review.
- Manual language selection, diagnostics, update checks, backup, cloud behavior, or broader Settings capability.
- A shortcut overflow interaction is not selected for `1.1.0`; this record does not determine its future product status.
- Public distribution, tag, milestone, or GitHub Release.
- Multi-branch, parallel task-line, or multi-contributor workflow changes.

## Technical approach and risks

Implementation details remain owned by development. Before relying on them, the applicable iteration must verify platform shortcut discovery and invocation for the selected launchable identity and profile, persistence compatibility for reorder writes, the system destination used for default-home settings, and the minimum accessibility-service declaration, state detection, disclosure, and lock-action behavior. The author-approved Privacy and prominent-disclosure text is defined in the linked current product contract; enabled acceptance still requires implementation to match it and satisfy the remaining technical and device gates.

The technical assessment and Iteration 11 closure inspected the Android backup configuration, including the manifest, `android:allowBackup`, and `android:dataExtractionRules`. The checked project and merged manifests establish that Avenor-owned favorite persistence files are excluded from Android cloud backup and device-transfer backup. Actual Android backup transport was not run and is not a blocker for this author daily-use baseline.

Consequential findings that change identity, persistence compatibility, permissions, architecture, or product acceptance require author direction and an ADR when appropriate. Iteration 10 may perform focused technical validation before its AccessibilityService ADR exists, but the resulting service must not be integrated into the version mainline until supported evidence has established the boundary and the required ADR records it. Ordinary UI state, platform adapters, and test seams remain code-level decisions.

## Included iterations

| Iteration | Increment | Entry dependency | Evidence to continue |
| --- | --- | --- | --- |
| [Iteration 7](iteration-7-favorite-reorder-loop.md) | Primary-favorite reorder loop | Accepted `1.0.0` favorite persistence | Author can reorder primary favorites and observe the saved order after reopening or restart evidence available to the iteration |
| [Iteration 8](iteration-8-application-shortcuts.md) | Application shortcuts on Home and Drawer | Stable shared action sheet and application identity | At least one exposed shortcut can be invoked from each originating surface without disturbing its state |
| [Iteration 9](iteration-9-basic-settings.md) | Basic Settings loop | Stable Drawer index and navigation | Settings entry, default-home handoff, local license, repository link, and version display behave as contracted |
| [Iteration 10](iteration-10-double-tap-lock.md) | Double-tap lock authorization loop | Accepted Iteration 9 Settings foundation and the current author-approved Privacy and disclosure text | Service state, disclosure, system handoff, eligible Home gesture, revocation, and failure behavior match the contract |
| [Iteration 11](iteration-11-presentation-compatibility-and-version-closure.md) | Static-name cleanup and version closure | Accepted Iterations 7-10 and their available evidence | Marquee is removed and the complete selected version journey satisfies the version exit evidence |

Iteration 10 depends on Iteration 9, and Iteration 11 depends on accepted Iterations 7-10. No other dependency or task-line binding is created by iteration numbering. The current execution plan is one active task line in iteration-number order, but that reversible coordination choice may change without changing iteration scope or status. Recommended scenarios are evidence guidance rather than automatic iteration gates unless promoted by the author or this version contract.

## Validation

The required version environment is one author-designated primary physical device. Version completion requires:

- an installable APK with the selected identifiers;
- successful upgrade or installation without unintended loss of readable `1.0.0` favorites;
- evidence from the current Android backup configuration that Avenor-owned favorite persistence files are excluded from Android cloud backup and device-transfer backup;
- retained full-width primary-favorite presentation with no companion region, placeholder, or reserved width;
- static one-line application names with no remaining marquee behavior;
- confirmed local Privacy and prominent disclosure content before double-tap lock is enabled for acceptance;
- author acceptance of the complete included journey on that device;
- no known included-path crash, ANR, destructive favorite-state error, wrong-profile shortcut invocation, or unusable Settings handoff; and
- author disposition of skipped, unknown, failed, or unavailable checks that affect the selected daily-use baseline.

The designated primary physical device is the only mandatory device environment for this delivery level. Focused automated checks, API 31, one additional API 36 or API 37 physical device, additional OEM/profile coverage, shortcut variations, process restart, locale switching, and unavailable-handler paths are recommended evidence. An unperformed recommended check must be recorded as `Unknown`, `Not run`, or `Unavailable` and does not by itself block `1.1.0` completion. If a recommended check is performed and exposes a failure on an included path, that result must be resolved or explicitly returned for author disposition; its recommended origin does not permit the failure to be ignored.

## Artifact and release requirements

Use the source, signing, artifact, and synchronization rules for an `Author daily-use baseline`. The author-local private signing identity must remain available for an in-place update from the accepted `1.0.0` installation. Do not record signing secrets. APK retention, digest, tag, upload, and publication remain separately authorized and are not planned by this document.

## Known limitations and legacy issues

- Platform shortcut availability and presentation depend on what Android exposes for the selected application and profile.
- Double-tap lock remains disabled until the user explicitly enables the purpose-limited service; OEM settings and service behavior require device evidence, and future store distribution requires renewed policy review.
- No dedicated shortcut-overflow interaction is defined.
- Privacy content beyond the confirmed local `1.1.0` statement and third-party-license presentation remain outside this version as stated above.
- Broader compatibility and formal-release evidence not required by this delivery level remain unknown until performed.

## Completion criteria

- Iterations 7-11 are `Completed` and accepted by the author.
- The version validation and upgrade conditions above are met on the designated primary device.
- The completion result records the selected identifiers, signing category, observed primary-device environment, upgrade result, and material known gaps. Commit, synchronization, APK retention, digest, tag, Release, upload, and distribution remain separately authorized actions.

## Completion result

`1.1.0` completed its selected `Author daily-use baseline` scope. Iterations 7-11 are `Completed`. The accepted build uses `applicationId` `com.avenor.launcher`, `versionName` `1.1.0`, and `versionCode` `2`.

The project author performed basic acceptance with a `debug` build signed by the author's local keystore file on a Samsung Galaxy S23 Ultra running Android 16 and One UI 8.5, using the personal profile on a device that also contains some app-clone instances. The accepted journey includes primary-favorite editing and reordering, application shortcuts, Settings, optional double-tap lock, static application-name presentation, and the corrected Home-to-Drawer gesture path.

Upgrade validation used the accepted `1.0.0` baseline at commit `96a9e68a21c7c55844deb06b2e4ca7284788d091`. An in-place upgrade to `1.1.0` completed normally and preserved multiple favorites and their order. Project-configuration and merged-manifest evidence confirms the selected backup-exclusion boundary. Additional device/API/OEM coverage and actual Android backup transport were not performed and do not block this author-accepted daily-use baseline.

No push, tag, GitHub Release, upload, or distribution is claimed by this completion result; each remains separately authorized.
