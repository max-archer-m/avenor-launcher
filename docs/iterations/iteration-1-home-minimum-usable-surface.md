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
- Completion unlocks Iteration 2 only when the resulting project, commands, Home qualification, and manifest evidence are stable enough to extend without replacement.

## Migration and compatibility impact

- This is the first Android implementation, so no production data or application-version migration exists.
- `minSdk` remains 31 and `targetSdk` remains 36.
- The approved initial `applicationId` is `com.avenor.launcher`. A later author-approved change remains possible, but after a formal artifact exists it creates a distinct Android application identity and requires explicit installation, upgrade, signing, data-continuity, distribution, and migration treatment.
- Intermediate debug installation is not the formal `1.0.0` artifact.

## Security, privacy, permission, and licensing impact

- No network, account, analytics, broad package visibility, hidden-profile access, cloud backup, or device-to-device transfer is introduced.
- The merged manifest is inspected for dependency-contributed permissions, components, and backup behavior.
- Direct and transitive dependencies, versions, maturity, licenses, and manifest contributions are recorded from the resolved graph.
- No signing secret is created or stored without separate project-author authorization.

## Risks and unresolved decisions

- The selected toolchain has preliminary local build evidence, but the complete build, test, lint, dependency-resolution, emulator, and device validation set remains outstanding.
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

Before closure, replace this prospective statement with links or records for:

- the exact successful build, test, lint, install, and focused validation commands;
- the resolved toolchain, dependency, and merged-manifest evidence;
- Home qualification and behavior on the API 31 emulator, Samsung API 36 device, and Pixel API 37 device;
- localized resource and platform-destination results; and
- every failure, unavailable check, limitation, or author decision.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Architecture and ADR links: required only after consequential choices are proven and recorded.
- Implementation commits: to be recorded from actual work.
- Tags: none authorized or required by this iteration.

## Final result

The iteration closes only when the observable outcome, included behavior, focused environment checks, reproducible commands, manifest review, and required evidence are accepted by the project author. Before then, no completion is claimed.

## Remaining issues and handoff

The closure handoff records the proven toolchain and commands, Home behavior, unresolved limitations, relevant architecture decisions, and the exact inputs available to [Iteration 2](iteration-2-drawer-application-discovery-and-launch.md). It must not transfer a known Home contract failure as ordinary Drawer work.
