# Iteration 21: Upgrade, Regression, and Version Closure

> Applicable version: [Avenor Launcher 1.3.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation, version change, Git, artifact, tag, publication, or release action.

## Objective

Produce truthful end-to-end evidence that the selected `1.3.0` journey upgrades safely from the accepted `1.2.0` baseline, remains compatible with established Launcher behavior, and is ready for the author's separate version-completion decision.

## Product and version references

- [1.3.0 delivery](delivery.md)
- [Validation guide](../../validation.md)
- [Release governance](../../release.md)
- [1.2.0 delivery](../1.2.0/delivery.md)
- [Navigation](../../product/navigation.md)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Privacy](../../product/features/privacy.md)

## Observable outcome

One traceable installable candidate can upgrade the accepted `1.2.0` baseline on the author-designated primary device, preserve readable favorites under the selected migration, complete the Iterations 15-20 journey, and record every result and gap without implying tag or public-release approval.

## Included work

- Reconcile product contracts, implementation, tests, identifiers, and evidence for Iterations 15-20; resolve or record every material mismatch.
- Validate representative `1.2.0` primary/companion states through an in-place upgrade into the unified model; a clean installation may provide supplemental evidence but cannot replace this journey.
- Validate normal Home, edit session, targeted additions, favorite bars, long-press drag activation and scroll arbitration, local and cross-container movement, auto-scroll, navigation, launch, inventory changes, persistence failure, same-process return, and process recreation.
- Check current Android backup configuration and confirm Avenor-owned favorite files remain excluded from cloud and device-transfer backup without assuming existing compliance.
- Produce or identify an installable artifact with accepted identifiers, source commit, signing category, digest, and upgrade limitation.
- Record mandatory and recommended checks as Passed, Failed, Not run, Unknown, or Unavailable with environment and ownership.
- Update `delivery.md` with separate iteration evidence, known limitations, author acceptance, and version result when authorized and supported.

## Excluded work

- Adding product functionality or hiding unfinished work inside closure.
- Repair, restore, cloud backup, Android Private Space, new double-tap-lock scope, formal release artifact, GitHub Release, store publication, public distribution, or automatic tag creation.
- Retrospective modification of the protected `1.2.0` result.

## Technical change areas

Upgrade fixtures and journeys, regression tests, backup-configuration inspection, build/artifact traceability, physical-device acceptance, evidence recording, and focused defect correction only when separately in scope.

## Dependencies and sequence

Depends on accepted Iterations 15-20. A discovered implementation gap returns to its owning iteration or a new author-approved scope; closure does not silently absorb it.

## Migration and compatibility impact

This iteration validates rather than redefines migration. In-place upgrade must preserve readable identity, destination mapping, order, and applicable settings. Unreadable state remains preserved and mutation-disabled. Downgrade is unsupported.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, external service, dependency, or license impact is selected. Backup exclusion is a required compatibility/privacy check. Any mismatch or expanded boundary requires correction or author review before completion.

## Risks and unresolved decisions

The precise `versionCode`, source commit, signing category, artifact, device environment, and tag disposition do not exist at planning time. A build pass alone does not establish migration or daily-use acceptance. A recommended test that is run and finds an included-path failure cannot be ignored because it was optional before execution.

## Acceptance criteria

- Iterations 15-20 have accepted results and no unresolved material contract mismatch.
- An accepted candidate retains `com.avenor.launcher`, uses approved `1.3.0` identifiers, and is traceable to source and signing category.
- The mandatory author-designated physical device upgrades the candidate in place from the accepted `1.2.0` baseline and completes the full selected journey without unintended loss, duplication, wrong destination/order, destructive overwrite, crash, ANR, wrong launch, or navigation dead end.
- Process recreation, inventory changes, persistence failure, Undo, navigation restoration, long-press drag activation, pre-recognition scrolling, and cross-container behavior meet the selected contracts.
- Manifest, `allowBackup`, `dataExtractionRules`, and applicable equivalent configuration are checked; favorite persistence does not enter Android cloud or device-transfer backup.
- Every skipped, unknown, failed, or unavailable check is recorded accurately.
- The author separately decides version completion and whether the completed baseline merits a tag; GitHub Release remains optional and separately authorized.

## Validation requirements

Primary-device in-place upgrade from the accepted `1.2.0` baseline and the complete author journey are mandatory version evidence. That journey includes each drag-handle type, one activation haptic after long-press recognition, and ordinary horizontal or vertical scrolling before recognition without a preview. A clean installation is recommended supplemental evidence and cannot replace the upgrade. Focused automated tests and additional API 31, API 36/37, OEM, profile, clone, locale, font-scale, navigation-mode, accessibility, and performance coverage are recommended unless promoted. Actual commands, environments, devices, artifacts, and outcomes belong in `delivery.md`.

## Related decisions and technical assessments

Use current release, validation, privacy, persistence, and identity decisions. Do not create a closure ADR; create or amend a durable decision only when implementation evidence establishes a consequential boundary.
