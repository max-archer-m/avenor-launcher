# Iteration 6: Author Daily-Use Baseline Closure

> Semantic source: English. Chinese counterpart: [iteration-6-compatibility-quality-and-formal-apk-closure.zh-CN.md](iteration-6-compatibility-quality-and-formal-apk-closure.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](delivery.md). This iteration closes the `Author daily-use baseline`; it does not create a `Formal release artifact` or authorize signing, artifact movement, a tag, a release, or public distribution.

## Status

- Value: `Completed`
- Updated: 2026-08-13
- Basis: The project author accepted the `053b6b7` implementation baseline and its complete primary-device daily-use journey. The implementation is synchronized to `origin/main`; the commit containing this status transition synchronizes the corresponding closure documentation.

## Objective

Establish one installable and traceable `1.0.0` APK that the project author accepts for ongoing daily use after completing the selected offline journey on the author-designated primary physical device.

## Product and version references

- [1.0.0 delivery contract](delivery.md)
- [1.0.0 product scope](product-scope.md)
- [1.0.0 technical assessment](technical-assessment.md)
- [Version, artifact, and release governance](../../release.md)
- [Iteration record format](../../iterations/iteration-record-format.md)

## Observable outcome

One `1.0.0` APK installs on the author-designated primary physical device, completes the selected offline Home, Drawer, application launch, action-sheet, and favorite journey, and is traceable to its application identifiers and source commit. Known gaps and signing or reinstall limitations are recorded.

## Included work

- Resolve defects that block the selected daily-use journey on the author-designated primary physical device.
- Install the `1.0.0` APK and execute the complete selected offline journey on that device.
- Record the device, OS/API, application identifiers, source commit, available APK/build identity, procedure, result, and known limitations.
- Record every unperformed automated, API 31, Pixel, performance, manifest, dependency, license, security, privacy, and release-artifact check as an explicit gap rather than a pass.
- Record and disposition OEM limitations and contract mismatches through the applicable authority.
- Record development-signing or author-controlled signing implications for update and reinstall behavior.
- Prepare the factual completed version record for the selected delivery level only after all completion evidence exists.

## Excluded work

- New product capability, deferred `1.0.0` behavior, Settings, reorder, shortcuts, uninstall, broader device adaptation, network capability, later capability layers, or commercial features.
- Silent relaxation of an included acceptance requirement.
- Creation of a tag, milestone declaration, GitHub Release, remote upload, store action, or public distribution without separate explicit authorization.
- Storage of an APK or signing secret in the product repository.
- Formal release signing and backup, release-level digest evidence, a complete compatibility matrix, performance thresholds, and specialist release conclusions unless separately authorized as additional work.

## Technical change areas

- Primary-device defect correction and focused regression evidence for the selected journey.
- APK identifiers, source traceability, installation evidence, and signing/reinstall limitations.
- Stable completed-version record preparation.

This iteration does not introduce speculative architecture. A defect fix that reveals a consequential architecture choice follows the ADR process; a product behavior change returns to the product manager and project author.

## Dependencies and sequence

- Iterations 1 through 5 must be `Completed`, or the project author must explicitly change the dependency sequence, before this iteration changes from `Planned` to `In Progress`. Completion of their recommended scenarios is not required unless the version contract makes a scenario a formal-version gate.
- Available build and install identity, the source commit, and the primary-device validation procedure are recorded as daily-use closing evidence. Missing recommended commands remain explicit gaps.
- The project author separately authorizes optional signing identity creation and external artifact movement when an action becomes ready.
- Version completion follows only after every gate in the version contract is satisfied.

## Migration and compatibility impact

- Validate the selected journey on the author-designated primary physical device. API 31 and Pixel validation remain recommended follow-up.
- Validate the exact `versionName` `1.0.0` and `versionCode` `1` in the installed baseline APK.
- Verify process/device restoration and the final persisted schema produced by Iteration 5.
- No downgrade, public distribution, store migration, or pre-1.0 production-data migration is included.

## Security, privacy, permission, and licensing impact

- Record any known permission, manifest, dependency, backup, security, privacy, or license concern affecting the selected daily-use journey. Missing release-wide review remains an explicit follow-up gap.
- Do not store a keystore, key, password, signing-property file, or other secret in project records.
- Record the signing identity category and resulting update or reinstall limitation without requiring release-certificate fingerprinting or release-keystore backups at this level.

## Risks and unresolved decisions

- OEM limitations may require author acceptance or a product-contract decision.
- API 31, Pixel, complete automated checks, performance measurement, merged-manifest/dependency review, and qualified license conclusions remain recommended follow-up evidence.
- External APK retention and formal signing remain author-reserved optional actions.
- A failure in accepted core behavior is not quality debt that can be waived silently.

## Validation plan

The following primary-device validation is required for the `1.0.0` author daily-use baseline. Higher-level evidence is recommended and recorded when available but does not block completion.

- Record the build or APK identity and source commit used for installation.
- Complete the full offline user journey on the author-designated primary physical device.
- Exercise Home qualification, direct launch, time/date, Drawer, grouping/index, transitions, live updates, exact-entry launch, action sheet, favorite creation/lifecycle, process recreation, and device restart.
- Record crashes, ANRs, accidental activation, duplicates, data loss, overwrite, and unavailable behavior.
- Verify the application identifiers and record signing/update/reinstall limitations that can be established from the accepted installation.
- Compare documentation, implementation, tests, and validation evidence; resolve every material contract mismatch explicitly.

## Acceptance evidence

The project author reported and accepted the following daily-use evidence:

- Accepted source commit: `053b6b7da58a27a9c237d98c2e49f7a94e5b1d3e` (`perf(drawer): reduce application list rendering cost`).
- Accepted application identity: `applicationId` `com.avenor.launcher`, `versionName` `1.0.0`, and `versionCode` `1`.
- Primary environment: Samsung Galaxy S23 Ultra on Android 16/API 36.
- Result: the complete selected offline daily-use journey passed, and the author reported no known core-path blocker.
- Signing category: author-local private signing identity. A later in-place update must use the same signing identity; if that identity becomes unavailable, update continuity cannot be assumed.
- Iterations 1 through 5 are `Completed` and retain their linked evidence.

The exact Gradle build command, installation command, APK filename, digest, retained artifact location, and command output were not reported. API 31 and Pixel compatibility, the complete automated matrix, performance distributions, merged-manifest and resolved-dependency review, qualified license conclusions, formal security and privacy review, release-signing custody and backup, and formal-release-artifact evidence remain unknown or unperformed recommended follow-up. None is represented as passed.

## Related decisions, commits, and tags

- Applicable architecture decisions: [ADR-0001](../../decisions/0001-establish-replaceable-launcher-icon-rendering.md), [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md), and [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md). ADR status and exact scope remain authoritative in each linked record.
- Accepted implementation baseline: `053b6b7da58a27a9c237d98c2e49f7a94e5b1d3e` (`perf(drawer): reduce application list rendering cost`).
- Signing configuration commit: `97a38e6 build(signing): configure local release credentials`; private signing material remains outside Git.
- Closure documentation: synchronized by the commit containing this status transition; Git history is authoritative for its identifier.
- Tag: not required for `1.0.0`; create only through separate explicit project-author authorization.
- GitHub Release and public distribution: not required or authorized by this iteration.

## Final result

The project author accepted the `053b6b7` implementation baseline, the identified `1.0.0` APK, and the reported Samsung Galaxy S23 Ultra primary-device journey for ongoing daily use, with no known core-path blocker. The implementation is synchronized to `origin/main`, and the commit containing this status transition synchronizes the corresponding closure documentation. Iteration 6 is `Completed` at the `Author daily-use baseline` level. Formal-release-artifact evidence remains outside this iteration unless separately authorized.

## Remaining issues and handoff

The completed version record remains with this iteration and the other `1.0.0` delivery inputs under the stable `docs/delivery/1.0.0/` path. The next project-wide iteration number remains `7`, and all remaining issues stay recorded for later authorized delivery. A tag, milestone, GitHub Release, or public distribution remains a separate decision.
