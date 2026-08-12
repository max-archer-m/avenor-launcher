# Iteration 6: Author Daily-Use Baseline Closure

> Semantic source: English. Chinese counterpart: [iteration-6-compatibility-quality-and-formal-apk-closure.zh-CN.md](iteration-6-compatibility-quality-and-formal-apk-closure.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md). This iteration closes the `Author daily-use baseline`; it does not create a `Formal release artifact` or authorize signing, artifact movement, a tag, a release, or public distribution.

## Status

- Value: `Planned`
- Updated: 2026-08-10
- Basis: The primary-device daily-use closure scope is defined, but its implementation, validation, and version-closing execution have not been authorized by this record.

## Objective

Establish one installable and traceable `1.0.0` APK that the project author accepts for ongoing daily use after completing the selected offline journey on the author-designated primary physical device.

## Product and version references

- [1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md)
- [1.0.0 product scope](../versions/1.0.0/product-scope.md)
- [1.0.0 technical assessment](../versions/1.0.0/technical-assessment.md)
- [Version, artifact, and release governance](../release.md)
- [Iteration record format](iteration-record-format.md)

## Observable outcome

One `1.0.0` APK installs on the author-designated primary physical device, completes the selected offline Home, Drawer, application launch, action-sheet, and favorite journey, and is traceable to its application identifiers and source commit. Known gaps and signing or reinstall limitations are recorded.

## Included work

- Resolve defects that block the selected daily-use journey on the author-designated primary physical device.
- Install the `1.0.0` APK and execute the complete selected offline journey on that device.
- Record the device, OS/API, application identifiers, source commit, available APK/build identity, procedure, result, and known limitations.
- Record every unperformed automated, API 31, Pixel, performance, manifest, dependency, license, security, privacy, and release-artifact check as an explicit gap rather than a pass.
- Record and disposition OEM limitations and contract mismatches through the applicable authority.
- Record development-signing or author-controlled signing implications for update and reinstall behavior.
- Prepare factual version archive records for the selected delivery level and update links only after all completion evidence exists.

## Excluded work

- New product capability, deferred `1.0.0` behavior, Settings, reorder, shortcuts, uninstall, broader device adaptation, network capability, later capability layers, or commercial features.
- Silent relaxation of an included acceptance requirement.
- Creation of a tag, milestone declaration, GitHub Release, remote upload, store action, or public distribution without separate explicit authorization.
- Storage of an APK or signing secret in the product repository.
- Formal release signing and backup, release-level digest evidence, a complete compatibility matrix, performance thresholds, and specialist release conclusions unless separately authorized as additional work.

## Technical change areas

- Primary-device defect correction and focused regression evidence for the selected journey.
- APK identifiers, source traceability, installation evidence, and signing/reinstall limitations.
- Version archive preparation and link migration.

This iteration does not introduce speculative architecture. A defect fix that reveals a consequential architecture choice follows the ADR process; a product behavior change returns to the product manager and project author.

## Dependencies and sequence

- Iterations 1 through 5 must be `Completed`, or the project author must explicitly change the dependency sequence, before this iteration changes from `Planned` to `In Progress`. Completion of their recommended scenarios is not required unless the version contract makes a scenario a formal-version gate.
- Available build and install identity, the source commit, and the primary-device validation procedure are recorded as daily-use closing evidence. Missing recommended commands remain explicit gaps.
- The project author separately authorizes optional signing identity creation, external artifact movement, and archive mutation when an action becomes ready.
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

Before completion, record:

- available build/install procedure, primary-device identity, result, APK/build identity, and source commit;
- `applicationId`, `versionName`, and `versionCode` from the accepted installation;
- the complete selected journey result and any observed blocker;
- signing identity category and known update or reinstall limitation;
- every missing compatibility, automated, performance, dependency, license, manifest, permission, backup, security, privacy, digest, and external-retention result;
- every known limitation, unresolved defect, technical debt, migration issue, workaround, and author disposition; and
- links to accepted Iterations 1 through 5 and their evidence.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Architecture, ADR, product-decision, and release-governance links: record every applicable decision.
- Implementation and closure commits: record the exact source history represented by the APK.
- Tag: not required for `1.0.0`; create only through separate explicit project-author authorization.
- GitHub Release and public distribution: not required or authorized by this iteration.

## Final result

No final result exists while this iteration is `Planned`. It becomes `Completed` when the project author accepts one traceable APK and its observed primary-device journey for ongoing daily use, known gaps are recorded, and the implementation and documentation are committed and synchronized. Formal-release-artifact evidence remains outside this iteration unless separately authorized.

## Remaining issues and handoff

On completion, move the version contract, supporting version inputs, and original included iteration records into `docs/archives/v1.0.0/`, update links, preserve the next project-wide iteration number as `7`, and record all remaining issues for later authorized delivery. A tag, milestone, GitHub Release, or public distribution remains a separate decision.
