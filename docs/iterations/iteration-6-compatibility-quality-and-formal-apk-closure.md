# Iteration 6: Compatibility, Quality, and Formal APK Closure

> Semantic source: English. Chinese counterpart: [iteration-6-compatibility-quality-and-formal-apk-closure.zh-CN.md](iteration-6-compatibility-quality-and-formal-apk-closure.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md). This iteration contract defines version-closing product and evidence outcomes. It does not authorize signing, artifact movement, a tag, a release, or public distribution. Each mutation requires the applicable explicit project-author authorization.

## Objective

Demonstrate that the complete `1.0.0` product journey is compatible, reliable, measurable, traceable, and represented by one formally identified and signed APK without adding unfinished product scope.

## Product and version references

- [1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md)
- [1.0.0 product scope](../versions/1.0.0/product-scope.md)
- [1.0.0 technical assessment](../versions/1.0.0/technical-assessment.md)
- [Version, artifact, and release governance](../release.md)
- [Iteration record format](iteration-record-format.md)

## Observable outcome

The same release-signed `1.0.0` APK completes the selected offline Home, Drawer, application launch, action-sheet, and favorite journey on the API 31 emulator, Samsung API 36 device, and Pixel API 37 device. Its source, identifiers, signing certificate, digest, validation, limitations, and external location are traceable.

## Included work

- Resolve defects required for the accepted Iterations 1 through 5 behavior to pass the complete environment matrix.
- Execute the complete deterministic automated and manual validation plan in every required environment.
- Measure repeatable cold start, time to full display, critical interaction frame behavior, memory, idle power, and stability on physical devices.
- Retain measurement procedures, iterations, build mode, device state, JSON, traces, and observed distributions.
- Obtain project-author approval for absolute evidence-based exit thresholds.
- Complete release dependency, license, merged-manifest, permission, backup, security, and privacy reviews applicable to the actual implementation.
- Decide from evidence whether a baseline profile materially improves the critical journey.
- Record and disposition OEM limitations and contract mismatches through the applicable authority.
- Under separate authorization, establish release-signing custody and backups, build and verify the formal APK, calculate and reverify SHA-256, and place it in the approved external location.
- Prepare factual version archive records and update links only after all completion evidence exists.

## Excluded work

- New product capability, deferred `1.0.0` behavior, Settings, reorder, shortcuts, uninstall, broader device adaptation, network capability, later capability layers, or commercial features.
- Silent relaxation of an included acceptance requirement.
- Creation of a tag, milestone declaration, GitHub Release, remote upload, store action, or public distribution without separate explicit authorization.
- Storage of an APK or signing secret in the product repository.

## Technical change areas

- Cross-environment defect correction and regression coverage for the complete accepted journey.
- Release build, lint, tests, benchmarks, traces, manifest/dependency evidence, and reproducible commands.
- Signing integration, certificate verification, APK identity/digest, source traceability, and external artifact record.
- Version archive preparation and link migration.

This iteration does not introduce speculative architecture. A defect fix that reveals a consequential architecture choice follows the ADR process; a product behavior change returns to the product manager and project author.

## Dependencies and sequence

- The project author accepts the observable foundations from Iterations 1 through 5 as sufficient to begin compatibility and quality closure. Formal closure of those iterations and completion of all recommended scenarios are not required for entry.
- Authoritative build, test, lint, install, and focused validation commands are established as formal-version closing evidence; their absence does not block entry into this iteration.
- The project author separately authorizes signing identity creation, signing, external artifact movement, and archive mutation when each action becomes ready.
- Version completion follows only after every gate in the version contract is satisfied.

## Migration and compatibility impact

- Validate Android 12/API 31 through Android 17/API 37 within the approved portrait-phone boundary.
- Validate the exact `versionName` `1.0.0` and `versionCode` `1` in the final artifact.
- Verify process/device restoration and the final persisted schema produced by Iteration 5.
- No downgrade, public distribution, store migration, or pre-1.0 production-data migration is included.

## Security, privacy, permission, and licensing impact

- Inspect the final merged release manifest for every permission, exported component, query, backup rule, and dependency contribution.
- Confirm the absence of unapproved network, broad visibility, hidden-profile, analytics, data collection, cloud backup, and device-transfer behavior.
- Record the resolved runtime dependency graph and qualified license conclusion.
- Store only the release-certificate SHA-256 fingerprint in project records; never store a keystore, key, password, signing-property file, or other secret.
- Require author-controlled secure storage and at least two independent encrypted signing backups before formal completion.

## Risks and unresolved decisions

- Absolute performance, memory, power, and stability thresholds remain undecided until repeatable measurements exist.
- OEM limitations may require author acceptance or a product-contract decision.
- Qualified license review may expose a conflict with the excluded user-visible notice surface.
- Exact external APK directory, retention, synchronization, backup, and Git-tracking policy remain author-reserved.
- Signing parameters and executable commands do not exist before the actual environment is established.
- A failure in accepted core behavior is not quality debt that can be waived silently.

## Validation plan

Iteration 6 may begin before the following work is complete. These validations are required for formal `1.0.0` completion where the version contract says so; they are not gates for entering Iteration 6.

- Run all documented build, unit, UI, instrumented, lint, manifest, dependency, install, and focused validation commands.
- Complete the full offline user journey in every required environment.
- Exercise Home qualification, direct launch, time/date, Drawer, grouping/index, transitions, live updates, exact-entry launch, action sheet, favorite creation/lifecycle, process recreation, and device restart.
- Record crashes, ANRs, accidental activation, duplicates, data loss, overwrite, and unavailable behavior.
- Run repeatable physical-device benchmarks and retain generated evidence.
- Build the final authorized release variant, verify identifiers and signature, calculate SHA-256, copy only under authorization, and reverify the external file.
- Compare documentation, implementation, tests, and validation evidence; resolve every material contract mismatch explicitly.

## Acceptance evidence

Before closure, record:

- exact successful build, test, lint, install, benchmark, signing, digest, and validation commands;
- complete environment identities, procedures, results, build identity, source commit, and APK digest;
- measured distributions and author-approved thresholds;
- dependency, license, manifest, permission, backup, security, and privacy conclusions;
- signed APK filename, `versionName`, `versionCode`, source commit, SHA-256, signing-certificate fingerprint, build time/environment, and external logical location;
- every known limitation, unresolved defect, technical debt, migration issue, workaround, and author disposition; and
- links to accepted Iterations 1 through 5 and their evidence.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Architecture, ADR, product-decision, and release-governance links: record every applicable decision.
- Implementation and closure commits: record the exact source history represented by the APK.
- Tag: not required for `1.0.0`; create only through separate explicit project-author authorization.
- GitHub Release and public distribution: not required or authorized by this iteration.

## Final result

The iteration and formal version close only when the project author accepts the complete factual evidence set and the same signed APK is traceable across identifiers, source commit, signature, digest, required environments, external storage, limitations, and archive records. Before then, no completion is claimed.

## Remaining issues and handoff

On completion, move the version contract, supporting version inputs, and original included iteration records into `docs/archives/v1.0.0/`, update links, preserve the next project-wide iteration number as `7`, and record all remaining issues for later authorized delivery. A tag, milestone, GitHub Release, or public distribution remains a separate decision.
