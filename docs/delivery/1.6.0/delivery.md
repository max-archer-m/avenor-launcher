# Avenor Launcher 1.6.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This record plans behavior selected from the current product contract. It is incomplete: implementation is not authorized by this record, and its status and evidence do not authorize a version change, commit, push, tag, artifact movement, publication, or release.

## Version intent

`1.6.0` delivers the Settings local backup and restore contract that the author confirmed on 2026-09-07 as the first change of the next version, after it was deferred from the `1.5.0` scope boundary. The selected result lets the author export the complete Home favorite state and Drawer display settings to one user-managed local JSON file and later replace the current state with a backup's contents after explicit confirmation. The version also delivers the Drawer drag-to-favorite journey the author decided on the same day: long-pressing and dragging an eligible Drawer application adds it to favorites through Home edit-mode destinations in one atomic operation. A third iteration, designated by the author on 2026-09-08 and amended on 2026-09-08 and 2026-09-09, delivers the accepted presentation and behavior reworks: the Home edit dock moved to the top of safe content with the animated panel slot swap, the Drawer background reworked into a continuous background-opacity percentage slider with its percentage readout, the shared style settings panel edge border, and the favorite multi-selection presentation redefined with unified overlay badges. Per the author's direction for this version, its content remains feature addition and optimization; UI-animation and experience-polish work beyond the contracted behaviors is not part of this version.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

The application identity is `com.avenor.launcher` with `versionName` `1.6.0`. The `versionCode` `7` is allocated as the next unused value for the accepted baseline and recorded at the identifier-bump commit `a1d43dba72f591862fcec9d2c355fe632144bdaa`; the accepted baseline APK built from that commit remains the author's packaging step.

## Product references

- [Product overview](../../../overview.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Settings behavior](../../product/surfaces/settings.md), including the Data section backup and restore contract
- [Settings presentation](../../product/presentation/settings.md)
- [Drawer behavior](../../product/surfaces/drawer.md), including the Drag-to-favorite section
- [Home behavior](../../product/surfaces/home.md), including the Drag-to-favorite journeys section
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Drawer presentation](../../product/presentation/drawer.md)
- [Home presentation](../../product/presentation/home.md)
- [Style settings panel presentation](../../product/presentation/style-settings-panel.md)
- [Design foundations](../../product/design-foundations.md)
- [Navigation](../../product/navigation.md)
- [Privacy and data handling](../../product/features/privacy.md)
- [Validation guide](../../validation.md)

## Included scope and user journey

In Settings, the author can select Back up favorites and settings to write one JSON file — containing the complete current Home favorite state and Drawer display settings — to a location chosen through the system document picker under a versioned, timestamped suggested name, and select Restore from backup to choose a backup file and, after a detailed confirmation dialog, replace the complete current state as one atomic operation. Success and failure use localized short messages; picker cancellation and in-flight behavior are defined; schema compatibility is directional; partially structured backups have defined validity.

In ordinary Drawer and search results, the author can long-press an eligible application and drag it through the programmatic Drawer-to-Home transition into Home edit mode, releasing it on an edit-mode destination to add it to favorites in one atomic operation; ineligible rows show their contracted toasts, and cancellation paths leave normal Home without returning to Drawer.

In Home edit mode, the author reaches the fixed edit dock at the top of safe content and sees the style settings panel expand and collapse as an animated slot swap that never covers the list. In Drawer display settings, the author selects the background opacity on a continuous percentage slider with live preview beside its percentage readout, while the style settings panels on both surfaces render the accepted edge border. In favorite multi-selection, selected rows present their overlaid order badges on scaled, outlined cells without changing the selection model. The accepted Home module model, Drawer behavior, and privacy boundaries remain intact.

## Exclusions

- Automatic backup, scheduled backup, cloud synchronization, cloud backup, device-to-device transfer, and any upload of the backup file.
- Backup or restore of any state beyond the Home favorite state and Drawer display settings; the backup file is not encrypted.
- A vertical favorites list, which the author confirmed on 2026-09-07 does not enter this version.
- The third favorite-module type (column favorites), which the author confirmed on 2026-09-07 stays out of this version pending a demonstrated layout need.
- Experience-polish work beyond the contracted behaviors and the contracted edit-dock panel animation, per author direction.
- Third-party License presentation until its separate inventory and acceptance conditions are satisfied.
- Formal release artifact, public distribution, tag, milestone, or GitHub Release.

## Technical approach and risks

Development owns implementation details. Delivery must preserve the versioned atomic favorite-file persistence and its backup exclusion, current permission boundaries (no new permission), local-only processing, and the accepted Home and Drawer state. The backup file's schema versioning follows the directional compatibility rule in the Settings contract: higher-than-current schema versions fail, equal-or-lower versions are interpreted under the current schema.

Primary risks are OEM document-picker behavior differences, suggested-name and timestamp presentation across locales, partial-file and field-gap interpretation, races between restore and other in-flight settings persistence, restoring identities whose applications are absent, and regression of ordinary Settings navigation and state refresh.

## Included iterations

| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [Iteration 29: Settings Backup and Restore](iteration-29-settings-backup-restore.md) | `Completed` | 2026-09-08 | The author confirmed the backup and restore feature as the next version's next iteration on 2026-09-07; implemented and accepted at the author daily-use baseline on 2026-09-08. |
| [Iteration 30: Drawer Drag-to-Favorite](iteration-30-drawer-drag-to-favorite.md) | `Completed` | 2026-09-08 | The author designated drag-to-favorite as the following iteration on 2026-09-07; implemented and accepted at the author daily-use baseline on 2026-09-08. |
| [Iteration 31: Edit Dock, Drawer Background, Panel Shadow, and Multi-Selection Presentation Rework](iteration-31-edit-dock-drawer-background-shadow.md) | `Completed` | 2026-09-10 | The author designated the edit-dock, background, and shadow reworks as the next iteration on 2026-09-08, amended the iteration the same day to add the multi-selection presentation rework, and accepted the continuous background-opacity contract revision on 2026-09-09; the author authorized production implementation on 2026-09-09 and confirmed the delivered, integrated result accepted at the author daily-use baseline on 2026-09-10. |

## Iteration evidence and results

### Iteration 29

[Contract](iteration-29-settings-backup-restore.md). `Completed` on 2026-09-08. The implementation delivers the contracted backup and restore journey, including the 2026-09-08 scope amendments (backup contents limited to the display-settings fields present in the current application model; the in-app Privacy statement copy update). The agent's local checks (`git diff --check`, line-length sweep) passed; the agent ran no Gradle build or test command (`Not run`). The author performed the build, installation, and device checks on the primary physical device and accepted the mandatory backup → restore round trip and uninstall/reinstall state recovery, together with the recommended cancel, Back, in-flight, interruption, and partially structured backup scenarios, at the author daily-use baseline. Acceptance evidence is author-reported. The contract's product-contract baseline is `1d98b078b1d8ac087cf8f4893aa2390b8bcf6ba3`, recorded under the baseline-change rule because the 2026-09-08 Drawer-background revision changed the contracted backup contents.

### Iteration 30

[Contract](iteration-30-drawer-drag-to-favorite.md). `Completed` on 2026-09-08. The implementation delivers the contracted gesture split, eligibility and toasts, the non-interactive Drawer-row preview, the programmatic Drawer-to-Home transition into collapsed-panel edit mode, destination resolution through the existing edit-mode rules (module insertion boundaries and both main-list add-favorite entries), the one atomic favorite mutation with save-failure feedback, and the cancellation paths including Back and pointer-stream cancellation during the journey. Focused instrumentation test sources cover eligibility resolution, the external-journey session lifecycle and its mutual exclusion with Home-originated drag sessions, external-favorite insertion and creation commits with their rejection paths, and the row gesture discrimination; the author executed these tests and reports them passing, and performed the build, installation, and device checks on the primary physical device, accepting the drag-to-favorite journey at the author daily-use baseline. Test and acceptance results are author-reported; the agent ran no Gradle command (`Not run`). The contract's product-contract baseline is `c0a1a44e38cf38919fbb3e9cf10dc75970081764`.

### Iteration 31

[Contract](iteration-31-edit-dock-drawer-background-shadow.md). `Completed` on 2026-09-10. The implementation delivers the four contracted reworks with their 2026-09-09 amendments: the top edit dock with the animated panel slot swap, the continuous background-opacity slider with its percentage readout, preview-commit-revert drag behavior, and schema-version-2 migration, the shared panel edge border, the Drawer panel height animation, and the multi-selection outline-and-badge presentation without press ripple. Focused instrumentation test sources cover dock placement and hot-zone stability, slot-swap interruption and retargeting, slider mapping, release commit and drag revert, store migration and out-of-range failure, backup field parity, border realization, and multi-selection row presentation; the author executed these tests and reports them passing, and performed the build, installation, and device checks on the primary physical device across the four group deliveries, accepting them at the author daily-use baseline. Three subsequent no-behavior-change refactors (the shared foreground-shadow construction, the parameterized opacity-slider test tag, and the consolidated panel cleanup paths) are integrated on `main` and included in the accepted result. Test and acceptance results are author-reported; the agent ran no Gradle command (`Not run`). The contract's product-contract baseline is `68d8a9afeb81e43648caacb43bfdd05c0a754596`.

## Dependencies and sequence

Iteration 29 depends on the accepted backup and restore product contract. Iteration 30 depends on the accepted drag-to-favorite contract revision, integrated in `c0a1a44e38cf38919fbb3e9cf10dc75970081764`. Iteration 31 depends on the accepted edit-dock, Drawer-background, panel-edge, and favorite-multi-selection contract revisions, integrated from `7af7adba15b018886dd613c587dfedd9299136b4` through `68d8a9afeb81e43648caacb43bfdd05c0a754596`. The three iterations overlap in shared state stores, strings resources, the style settings panel, the Home edit-mode layout, and Drawer application-row UI, so they must not run concurrently on one line; their relative order is otherwise free, with each later implementation line containing the integrated results of the earlier ones it depends on. These dependencies do not bind work to a branch, terminal, contributor, forecast date, or permanent task line.

## Validation

The mandatory version environment is one author-designated primary physical device. Completion requires a full on-device backup → restore round trip, acceptance of the confirmation and cancel paths, failure-path behavior for unreadable, malformed, and newer-schema files, preservation of the accepted Home and Drawer state outside the restored data, and no known included-path crash, ANR, destructive configuration error, wrong identity launch, or navigation dead end.

Focused automated checks and additional OEM picker, locale, font-scale, interrupted-process, empty-backup, partially-structured-backup, and older-schema scenarios are recommended unless explicitly promoted. Executed failures on included behavior must be resolved or dispositioned; unperformed checks remain `Not run`, `Unknown`, or `Unavailable`.

## Artifact and release requirements

The accepted APK must retain `com.avenor.launcher`, use accepted `1.6.0` identifiers, be traceable to one source commit and signing category, and support the required upgrade journey. The author-local private signing identity remains required for in-place update continuity. APK retention, tag, publication, and distribution remain separately authorized.

## Known limitations and legacy issues

- Document-picker presentation and suggested-name handling vary across OEMs; the primary-device journey was exercised and accepted, and per-OEM variance remains accepted presentation variance.
- The backup file is not encrypted; its content is user-managed outside Avenor's private storage by contract.
- Older-schema backups are accepted and interpreted under the current schema; no cross-version migration guarantees beyond that rule are committed.
- Broader device, API, OEM, and locale coverage remains unknown until performed.

## Completion criteria

- All included iterations are `Completed` with separately recorded evidence and author acceptance.
- Product contracts, implementation, tests, and delivery evidence have no unresolved material mismatch in the selected scope.
- The mandatory physical-device backup → restore round trip and the complete selected Settings journey are accepted without loss of accepted state outside the restored data.
- Final identifiers, allocated `versionCode`, source commit, signing category, APK identity, known gaps, and tag disposition are recorded accurately.

## Completion result

This version is not yet declared complete. All three included iterations are completed and accepted at the author daily-use baseline. The application identifiers are set to `versionName` `1.6.0` and `versionCode` `7` at `a1d43dba72f591862fcec9d2c355fe632144bdaa`. Per the author's 2026-09-10 direction, this version receives no Git tag and no retained release artifact. Remaining before the version result is declared: the author's packaging and in-place upgrade verification of the `versionCode`-7 baseline on the primary device, and the recording of the final accepted APK's source-commit identity.
