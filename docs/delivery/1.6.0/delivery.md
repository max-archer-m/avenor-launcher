# Avenor Launcher 1.6.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This record plans behavior selected from the current product contract. It is incomplete: implementation is not authorized by this record, and its status and evidence do not authorize a version change, commit, push, tag, artifact movement, publication, or release.

## Version intent

`1.6.0` delivers the Settings local backup and restore contract that the author confirmed on 2026-09-07 as the first change of the next version, after it was deferred from the `1.5.0` scope boundary. The selected result lets the author export the complete Home favorite state and Drawer display settings to one user-managed local JSON file and later replace the current state with a backup's contents after explicit confirmation. Per the author's direction for this version, its content remains feature addition and optimization; UI-animation and experience-polish work is not part of this version.

Author-confirmed next-version directions that are not yet assigned to an iteration — the more visible shadow around the style-edit dialog, and the frosted-glass darkness/blur calibration (with an optional adjustable-strength control still undecided) — remain candidate scope for later iterations of this version or a later version. They are not included scope until an iteration contract selects them.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

The planned application identity remains `com.avenor.launcher` with `versionName` `1.6.0`. A candidate `versionCode` of `7` is provisional and must be selected from the next unused value only when a traceable APK is produced; planning does not reserve a value or override intervening artifact allocations.

## Product references

- [Product overview](../../../overview.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Settings behavior](../../product/surfaces/settings.md), including the Data section backup and restore contract
- [Settings presentation](../../product/presentation/settings.md)
- [Navigation](../../product/navigation.md)
- [Privacy and data handling](../../product/features/privacy.md)
- [Validation guide](../../validation.md)

## Included scope and user journey

In Settings, the author can select Back up favorites and settings to write one JSON file — containing the complete current Home favorite state and Drawer display settings — to a location chosen through the system document picker under a versioned, timestamped suggested name, and select Restore from backup to choose a backup file and, after a detailed confirmation dialog, replace the complete current state as one atomic operation. Success and failure use localized short messages; picker cancellation and in-flight behavior are defined; schema compatibility is directional; partially structured backups have defined validity. The accepted Home module model, Drawer behavior, and privacy boundaries remain intact.

## Exclusions

- Automatic backup, scheduled backup, cloud synchronization, cloud backup, device-to-device transfer, and any upload of the backup file.
- Backup or restore of any state beyond the Home favorite state and Drawer display settings; the backup file is not encrypted.
- Drawer long-press drag-to-favorite, which remains a to-be-refined proposal in the author's private working notes.
- A vertical favorites list, which the author confirmed on 2026-09-07 does not enter this version.
- Style-edit dialog shadow and frosted-glass presentation changes, which are unassigned candidate directions, not selected scope.
- UI-animation and experience-polish work for this version, per author direction.
- Third-party License presentation until its separate inventory and acceptance conditions are satisfied.
- Formal release artifact, public distribution, tag, milestone, or GitHub Release.

## Technical approach and risks

Development owns implementation details. Delivery must preserve the versioned atomic favorite-file persistence and its backup exclusion, current permission boundaries (no new permission), local-only processing, and the accepted Home and Drawer state. The backup file's schema versioning follows the directional compatibility rule in the Settings contract: higher-than-current schema versions fail, equal-or-lower versions are interpreted under the current schema.

Primary risks are OEM document-picker behavior differences, suggested-name and timestamp presentation across locales, partial-file and field-gap interpretation, races between restore and other in-flight settings persistence, restoring identities whose applications are absent, and regression of ordinary Settings navigation and state refresh.

## Included iterations

| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [Iteration 29: Settings Backup and Restore](iteration-29-settings-backup-restore.md) | `Planned` | 2026-09-07 | The author confirmed the backup and restore feature as the next version's next iteration on 2026-09-07; production implementation is not yet authorized. |

## Iteration evidence and results

### Iteration 29

[Contract](iteration-29-settings-backup-restore.md). Planned only; no implementation, evidence, or result is recorded yet. The contract's product-contract baseline records the full commit ID of the commit that integrates the accepted 2026-09-07 contract optimization.

## Dependencies and sequence

Iteration 29 depends on the accepted backup and restore product contract and the current integrated mainline; it has no other planned in-version dependencies. Later author-confirmed directions (dialog shadow, frosted-glass calibration) would be additional iterations with independent reviewable results if the author assigns them. These dependencies do not bind work to a branch, terminal, contributor, forecast date, or permanent task line.

## Validation

The mandatory version environment is one author-designated primary physical device. Completion requires a full on-device backup → restore round trip, acceptance of the confirmation and cancel paths, failure-path behavior for unreadable, malformed, and newer-schema files, preservation of the accepted Home and Drawer state outside the restored data, and no known included-path crash, ANR, destructive configuration error, wrong identity launch, or navigation dead end.

Focused automated checks and additional OEM picker, locale, font-scale, interrupted-process, empty-backup, partially-structured-backup, and older-schema scenarios are recommended unless explicitly promoted. Executed failures on included behavior must be resolved or dispositioned; unperformed checks remain `Not run`, `Unknown`, or `Unavailable`.

## Artifact and release requirements

The accepted APK must retain `com.avenor.launcher`, use accepted `1.6.0` identifiers, be traceable to one source commit and signing category, and support the required upgrade journey. The author-local private signing identity remains required for in-place update continuity. APK retention, tag, publication, and distribution remain separately authorized.

## Known limitations and legacy issues

- Document-picker presentation and suggested-name handling vary across OEMs and remain unknown until exercised on the primary device.
- The backup file is not encrypted; its content is user-managed outside Avenor's private storage by contract.
- Older-schema backups are accepted and interpreted under the current schema; no cross-version migration guarantees beyond that rule are committed.
- Broader device, API, OEM, and locale coverage remains unknown until performed.

## Completion criteria

- All included iterations are `Completed` with separately recorded evidence and author acceptance.
- Product contracts, implementation, tests, and delivery evidence have no unresolved material mismatch in the selected scope.
- The mandatory physical-device backup → restore round trip and the complete selected Settings journey are accepted without loss of accepted state outside the restored data.
- Final identifiers, allocated `versionCode`, source commit, signing category, APK identity, known gaps, and tag disposition are recorded accurately.

## Completion result

This version is incomplete. The record plans the selected scope and its first iteration; implementation, identifiers, evidence, and completion remain unauthorized and unrecorded.
