# Iteration 1: Home Minimum Usable Surface

> Semantic source: English. Chinese counterpart: [iteration-1-home-minimum-usable-surface.zh-CN.md](iteration-1-home-minimum-usable-surface.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md). This iteration contract defines one product increment and its required evidence. It does not authorize implementation, approve candidate architecture, or declare the iteration complete. The project author must explicitly authorize implementation.

## Objective

Deliver an installable Avenor build that Android can recognize as a Home application and that presents the minimum localized Home information surface without depending on later Drawer or favorite behavior.

## Product and version references

- [1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md)
- [1.0.0 product scope](../versions/1.0.0/product-scope.md)
- [1.0.0 technical assessment](../versions/1.0.0/technical-assessment.md)
- [Product navigation](../product/navigation.md)
- [Home interaction](../product/home.md)
- [Product design foundations](../product/design-foundations.md)

The linked product documents remain authoritative for detailed behavior. This iteration selects only the Home behavior listed below.

## Observable outcome

The author can install and directly open Avenor, Android recognizes it as an available Home application, and Home displays the localized current time, date, and weekday. Selecting time opens the resolved system Clock application's main surface rather than only its alarms page when that surface is exposed; selecting date safely invokes the applicable system-owned Calendar destination. Returning Home and recreating the process returns to Home without a crash or an invalid task stack.

## Included work

- Create the minimum Android project at the product-repository root.
- Configure `com.avenor.launcher` as the approved `applicationId` and initial namespace unless a later explicit project-author decision replaces it.
- Establish a reproducible debug build, selected automated-test foundation, installation, and focused validation commands.
- Centralize plugin and dependency repositories with a selectable Mainland China profile using the approved Alibaba Cloud and Tencent Cloud mirrors and an official-upstream profile using Google, Maven Central, and Gradle Plugin Portal.
- Qualify Avenor as an Android Home application while keeping it directly launchable when not selected as the default Home.
- Present Home time, date, and weekday according to system clock, format, and locale behavior.
- Provide default English and Simplified Chinese resources, with English fallback for unsupported locales.
- Use resource-backed user-facing strings, colors, and reusable dimensions.
- Keep the Home application surface, status-bar region, and navigation-bar region fully transparent so the system background remains visible. Do not draw an Avenor gradient, scrim, blur, or opaque background, and retain platform-default contrast protection.
- Implement the selected Home inset, dark-theme foreground, typography, and touch-target baseline required by the included content. Iteration 1 does not implement the Home-content opacity animation assigned to the complete Home/Drawer transition in Iteration 3.
- Open the resolved system Clock application's main surface without hard-coding a vendor package, falling back to its system alarm destination when no main surface is exposed; invoke Calendar through a defensive system-owned action; and provide localized failure behavior.
- Start and restore at Home after ordinary process recreation.
- Verify the merged manifest and the absence of unapproved declarations introduced by the initial toolchain or dependencies.

## Excluded work

- Drawer content, Home-to-Drawer interaction, application inventory, application launching, alphabet index, and live application updates.
- Favorite display, creation, persistence, launch, removal, reconciliation, reorder, or recovery behavior.
- Application action sheet and Settings.
- Full-version compatibility, performance thresholds, release signing, formal APK creation, artifact movement, archive, tag, or release actions.
- Any user-visible behavior outside the approved `1.0.0` product scope.

## Technical change areas

- Root Android project and reproducible toolchain.
- Application identifiers, SDK values, build variants, resources, and test foundations.
- Home/Launcher manifest qualification, activity/task behavior, direct launch, and process restoration.
- Time/date formatting, locale resource selection, system bars, insets, and defensive platform intents.

The iteration validates the candidate JDK, Gradle, Android Gradle Plugin, Kotlin, Compose, and SDK combination. A consequential proven choice is documented in architecture or an ADR when required; ordinary implementation details remain in code and tests.

## Dependencies and sequence

- The project author approves the `1.0.0` prospective delivery boundary and explicitly authorizes this iteration.
- API 37 is evaluated first as stable `compileSdk`; using the documented API 36 fallback requires the reserved project-author decision.
- Iteration 2 may begin after the project author accepts the delivered implementation and observed Home behavior as sufficient to continue, the existing project foundation is suitable for extension without replacement, and every incomplete Iteration 1 validation item is explicitly recorded and assigned. This continuation gate does not declare Iteration 1 formally closed or waive any version-level exit gate.

## Migration and compatibility impact

- This is the first Android implementation, so no production data or application-version migration exists.
- `minSdk` remains 31 and `targetSdk` remains 36.
- The approved initial `applicationId` is `com.avenor.launcher`. A later author-approved change remains possible, but after a formal artifact exists it creates a distinct Android application identity and requires explicit installation, upgrade, signing, data-continuity, distribution, and migration treatment.
- Intermediate debug installation is not the formal `1.0.0` artifact.

## Security, privacy, permission, and licensing impact

- No network, account, analytics, broad package visibility, hidden-profile access, cloud backup, or device-to-device transfer is introduced.
- The source manifest declares `com.android.alarm.permission.SET_ALARM` for the confirmed Clock main-surface and alarm-fallback behavior. The project author has accepted that declaration as part of the current requirement; it is not a pending-removal item.
- The merged manifest is inspected for dependency-contributed permissions, components, and backup behavior.
- Direct and transitive dependencies, versions, maturity, licenses, and manifest contributions are recorded from the resolved graph.
- No signing secret is created or stored without separate project-author authorization.

## Risks and unresolved decisions

- The project author reports successful editor build, installation, execution, and visible Home output for the implementation represented by commit `2e492109482a185f33670e87e86ce562b0279ebf`. The report does not identify the editor and version, host environment, build variant, device model, Android/API version, exact procedure, or retained output, so those evidence fields remain unverified.
- Reproducible CLI build, automated tests, release lint, dependency resolution, merged-manifest inspection, emulator validation, and required physical-device validation remain outstanding.
- The approved mirrors may be stale or incomplete relative to official upstream sources; exact endpoints, ordering, and resolved artifacts require evidence.
- Some Windows environments may report an Android Gradle Plugin path error when a checkout path contains non-ASCII characters. The repository currently enables `android.overridePathCheck=true` because the project author's checkout requires that workaround. This is an environment-specific accommodation rather than a validation result or a requirement that every contributor reproduce the same path; it only bypasses the AGP guard and does not guarantee that every downstream tool supports the path.
- API 37 `compileSdk` reproducibility remains evidence-dependent; API 36 fallback is author-reserved.
- Home role, task-stack, repeated Home invocation, and direct-launch behavior may vary by environment.
- Clock or Calendar destinations may be absent or behave differently across devices.
- A consequential project/module/UI choice may require an ADR after the initial evidence exists.

## Validation plan

- Build from the repository wrapper with the selected JDK on a cleanly described environment.
- Resolve the same locked dependency graph through the Mainland China and official profiles where available, recording mirror gaps, fallback behavior, repository order, and artifact provenance.
- If a path-related build failure still occurs on Windows, inspect and record the actual message. An affected environment may use an ASCII-only checkout path or another evidence-backed local resolution; do not turn the author's current workaround into a universal pass/fail procedure.
- Run the selected initial automated tests and release lint when available.
- Inspect the merged manifest and resolved dependency graph.
- Install and directly launch the build on the API 31 emulator and both recorded physical devices.
- Verify Home qualification, system-owned Home selection, repeated Home invocation, Back behavior, and process recreation.
- Verify that the stationary Home content remains fully visible over the system background; the application surface and system-bar regions request full transparency; platform contrast protection remains enabled; and no Avenor gradient, scrim, blur, or opaque background is drawn.
- Verify time, date, weekday, 12/24-hour behavior, English, Simplified Chinese, and English fallback resources.
- Verify that time selection opens the resolved Clock application's main surface rather than only its alarm tab when that surface is exposed, falls back safely when it is not, and reports an unavailable destination without a crash; also verify Calendar success and failure behavior.
- Record actual commands, environment identities, build identity, source commit, procedure, and result.

## Acceptance evidence

Evidence available as of 2026-08-11:

- Commit `75dfdfd221a981db1677a9e5f2873a6e84fab398` aligned the approved application identity, Clock launch behavior, repository profiles, path workaround, transparency boundary, and `48dp` Home date-row contract before implementation.
- Commit `2e492109482a185f33670e87e86ce562b0279ebf` added the root Android project, single-activity Compose Home implementation, Home/Launcher manifest entry, API and application identifiers, time/date resources, default English and Simplified Chinese resources, transparent theme, platform-destination handling, and initial test sources.
- Repository inspection confirms that the source manifest contains the Home and direct-launch entry points and the accepted `com.android.alarm.permission.SET_ALARM` declaration. This is source evidence only; the merged manifest has not been recorded or accepted.
- Repository inspection confirms that the date-and-weekday row uses the contract-defined `48dp` resource and vertically centered content. No `40dp`/`48dp` contract mismatch is identified in the current source.
- The project author reports that the current project was successfully built, installed, and run from an editor and that Home displayed normally. The author accepts that observed implementation result as sufficient to continue delivery toward Iteration 2.
- The supplied evidence does not identify the editor and version, host environment, build variant, device model, Android/API version, exact procedure, build output, APK identity, or retained evidence location. These fields are unverified and must not be inferred.

Outstanding before formal Iteration 1 and `1.0.0` closure:

- reproducible CLI build commands and results, selected automated-test execution, release lint, dependency and repository-profile resolution, and merged-manifest evidence;
- API 31 emulator, Samsung API 36, and Pixel API 37 validation with exact environment and build identities;
- focused evidence for Home qualification, direct launch, repeated Home invocation, Back, process recreation, transparency, localization, 12/24-hour behavior, and Clock/Calendar success and failure paths; and
- exact records for every failure, unavailable check, limitation, and author disposition.

The available evidence supports continuation, not formal iteration closure or final-version acceptance.

## Related decisions, commits, and tags

- Delivery-alignment commit: `75dfdfd221a981db1677a9e5f2873a6e84fab398` (`docs: align initial Home delivery contracts`).
- Implementation commit: `2e492109482a185f33670e87e86ce562b0279ebf` (`feat(home): implement minimum launcher surface`).
- Architecture and ADR links: none recorded for this iteration. Add one only if a consequential proven choice requires it.
- Tags: none authorized or required by this iteration.

## Final result

The project author has accepted the delivered implementation and observed Home display as sufficient to continue product delivery on the existing foundation. Iteration 1 is not formally closed because the reproducible CLI, automated, lint, merged-manifest, emulator, physical-device, and focused behavior evidence listed above remains incomplete. Continuation acceptance does not mark any missing check as passed and does not reduce the final `1.0.0` gates.

## Remaining issues and handoff

The reusable foundation for [Iteration 2](iteration-2-drawer-application-discovery-and-launch.md) is the root single-activity Compose project, `compileSdk` 37 / `minSdk` 31 / `targetSdk` 36 configuration, `com.avenor.launcher` application identity, Home and direct-launch manifest entries, localized Home time/date implementation, default English and Simplified Chinese resources, transparent theme, and defensive Clock/Calendar destination foundation. The project author has confirmed that this structure does not need replacement before Drawer work.

The missing CLI, automated-test, release-lint, dependency-resolution, merged-manifest, API 31 emulator, Samsung API 36, Pixel API 37, and focused Home behavior evidence remains an explicit Iteration 1 closure obligation. Execute it in the next applicable validation run and close it no later than Iteration 6 and the `1.0.0` version gate. Iteration 2 must not represent this carried evidence as completed or use it to hide a discovered Home contract failure. Iteration 2 implementation still requires separate explicit project-author authorization.
