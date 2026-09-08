# Iteration 29: Settings Backup and Restore

> Applicable version contract: [1.6.0 delivery](delivery.md). This contract defines the authorized delivery boundary only. It does not own execution state, evidence, commits, or results, and it does not by itself authorize production implementation, a commit, a push, a tag, or any release action.

## Objective

Deliver the Settings Data backup and restore journey exactly as defined by the accepted product contract: the author can back up the complete Home favorite state and Drawer display settings to one user-managed local JSON file through the system document picker, and restore a chosen backup file over the current state as one atomic operation after explicit confirmation, with the contracted feedback, cancellation, in-flight, schema-compatibility, and validity behavior.

## Product and version references

- Product-contract baseline: `1d98b078b1d8ac087cf8f4893aa2390b8bcf6ba3` — the commit that integrates the accepted 2026-09-07 backup and restore contract optimization and the later 2026-09-08 Drawer-background revision that changed the contracted backup contents. The originally integrating commit `b35ccd3` predates that background revision, so under the baseline-change rule the baseline is recorded at the later commit, which contains every product rule the selected scope depends on. Production implementation must be based on or otherwise include this baseline.
- Applicable product documents:
  - [Settings behavior](../../product/surfaces/settings.md), Data section
  - [Settings presentation](../../product/presentation/settings.md)
  - [Privacy and data handling](../../product/features/privacy.md)
  - [Navigation](../../product/navigation.md)
- Applicable version contract: `delivery.md`

## Observable outcome

On the primary device, from Settings: selecting Back up favorites and settings produces one JSON file at a user-chosen location under the suggested versioned, timestamped name; selecting Restore from backup and confirming replaces the current favorites and display settings with the backup contents in one atomic operation; Home and Drawer then present the restored state. Success, failure, cancel, and in-flight behavior match the Data section contract, including the directional schema-compatibility rule and the defined validity of empty and partially structured backups.

## Included work

- The two Settings Data entries with their order, titles, trailing-arrow primary presentation, and entry into the system document picker.
- Backup serialization of the complete current Home favorite state (ordered modules with type, order, stable identities, style, and per-module application order) and the Drawer display-settings fields present in the current application model (application size, name placement, items per row, section-anchor presentation, and background mode) into one schema-versioned JSON file with the suggested display name `avenor-backup-<version-name>-<yyyyMMddHHmm>`. The blur-intensity level is not serialized: the intensity setting does not exist in the application model until its implementing iteration delivers it.
- Restore selection, the detailed confirmation dialog (replacement warning including irreversibility, `Restore` and `Cancel` actions, Cancel/dismiss/Back no-ops), and the atomic replacement of favorites and display settings.
- Localized short messages for backup success, backup failure, restore success, and restore failure, in English and Simplified Chinese.
- The in-app Privacy statement copy update, in English and Simplified Chinese, that aligns the Storage, backup, and deletion paragraph with the manual backup and restore disclosure already recorded by the Privacy contract.
- Directional schema-version compatibility (higher than current fails; equal or lower accepted and interpreted under the current schema) and the defined validity of a missing favorites section, a missing display-settings section, a missing field inside a present section, and an empty backup.
- Non-interactive Data entries while a backup write or restore apply is in progress, with no partial-state outcome on interruption.
- Focused test sources covering serialization round trip, schema compatibility, partial-file validity, dialog and cancel paths, and failure paths that leave current state untouched.

## Excluded work

- Automatic, scheduled, cloud, or uploaded backup of any kind; encryption of the backup file.
- Backup or restore of state beyond the Home favorite state and Drawer display settings.
- Serializing or restoring a blur-intensity level before the intensity setting exists in the application model; the backup schema gains that field through the directional schema-compatibility rule when the setting ships.
- The `1.6.0` version identifier update (`versionName`/`versionCode`), which remains a version-level closure concern unless a later authorized amendment assigns it here.
- Style-edit dialog shadow and frosted-glass presentation changes; UI-animation and experience-polish work.
- Third-party License presentation.

## Technical change areas

- Settings UI: the two Data entries, the confirmation dialog, message presentation, and entry enabled/disabled state during in-flight operations.
- Backup and restore serialization: schema version, JSON structure for favorites and display settings, suggested file name construction, and directional compatibility interpretation.
- State stores: atomic replacement path for favorites and display settings consistent with existing versioned atomic persistence; backup exclusion and app-private-storage boundaries remain unchanged.
- Resources: English and Simplified Chinese strings for the two entry titles, dialog text and actions, and the four short messages, plus the updated Privacy statement copy in both languages.
- Manifest and permissions: none; no new permission and no manifest change is expected.
- Tests: focused local and instrumentation sources for the behaviors above.

## Dependencies and sequence

This iteration depends on the accepted product contract and the current integrated mainline; no other in-version dependency is planned. It must not run concurrently with another line that mutates the favorites or display-settings stores, Settings UI, or shared strings resources.

## Migration and compatibility impact

- Restoring replaces current state by contract; no data migration beyond the directional schema rule is introduced.
- The backup file lives outside app-private storage by user choice and is excluded from any automatic platform backup by the existing privacy boundary; this iteration must not change that boundary.
- Android cloud backup and device-transfer behavior for Avenor-owned data remains governed by the Privacy contract and is unchanged.

## Security, privacy, permission, and licensing impact

- No new permission, networking, account, or external service is introduced.
- The backup file is unencrypted and user-managed outside Avenor's private storage, as the Privacy contract already discloses; this iteration must keep the file free of any data beyond the contracted favorites and display-settings state.
- The in-app Privacy statement copy is updated within this iteration to match the manual backup-and-restore disclosure in the Privacy contract; no other Privacy statement change is authorized. If implementation reveals any further material divergence from the Privacy statement, stop and obtain author direction.

## Risks and unresolved decisions

- OEM document-picker behavior may ignore or alter the suggested file name; the contract assigns final naming to the system picker, so per-OEM variance is accepted presentation variance, not a defect by itself.
- Timestamp construction across locales and 12/24-hour settings must always produce the `yyyyMMddHHmm` form; a mistake silently changes suggested names.
- Races between restore and other in-flight settings persistence must not produce a mixed state; the atomic-replacement path must cover both stores together.
- The partial-file rule (missing display-settings section restores Avenor defaults) was confirmed by the author on 2026-09-07 as part of accepting partially structured backups as valid; if device use shows this default-reset reading to be surprising, reopening it is a product decision, not an implementation choice.
- Backup-contents scope (2026-09-08 author decision): this iteration's backup serializes only the display-settings fields that exist in the current application model. The Settings behavior contract names the blur-intensity level among the backup contents; that phrase becomes fully implementable when the intensity setting ships, and the author owns any future alignment of that product wording.

## Acceptance criteria

- The two Data entries appear in the contracted order with the primary settings-item presentation and trailing arrows, and open the system document picker without new permissions.
- Backup writes one schema-versioned JSON file containing the complete favorites state and the display-settings fields present in the current application model under the suggested name; cancel writes nothing and shows no message; success and failure show the contracted localized messages.
- The in-app Privacy statement, in both languages, discloses the manual local backup and restore and no longer claims that export or restoration is not provided.
- Restore shows the detailed confirmation dialog; Cancel, dismissal, and Back change nothing; confirming atomically replaces both stores and shows the contracted success message; Home and Drawer present the restored state on next view.
- Unreadable, malformed, and higher-schema files fail without overwriting current state and show the contracted failure message; equal-or-lower schema backups, missing-section backups, missing-field cases, and the empty backup behave per the validity rule.
- During an in-flight backup or restore apply, both Data entries are non-interactive, and interruption leaves no partial state.
- All new user-visible strings exist in English and Simplified Chinese with complete name parity.
- No regression of ordinary Settings navigation, state refresh, or the accepted Home and Drawer behavior outside the restored data.

## Validation requirements

Recommended scenarios unless explicitly promoted:

- Device round trip on the primary physical device: back up, modify state, restore, and verify the restored favorites and display settings in Home and Drawer.
- Cancel paths for both pickers and the confirmation dialog; Back behavior on the dialog.
- Failure paths: unreadable file, malformed JSON, truncated file, and a file whose schema version is higher than current.
- Compatibility paths: a backup from the current schema version, a manually constructed lower-schema backup, a backup missing the display-settings section, a backup missing the favorites section, and the empty backup.
- In-flight behavior: repeated entry taps during write/apply; process interruption during backup and during restore apply.
- String parity between English and Simplified Chinese resources.
- Regression of ordinary Settings navigation and state refresh after restore.

## Related decisions and technical assessments

- [ADR-0002: versioned atomic file for favorites](../../decisions/0002-use-versioned-atomic-file-for-favorites.md) — the persistence model the restore path must preserve.
- [Privacy and data handling](../../product/features/privacy.md) — the disclosed backup boundary this iteration implements.
- [Product decision and scope-change governance](../../product-decisions.md) — the deferral of this contract from the 1.5.0 scope boundary and the author's 2026-09-07 confirmation of this iteration.
