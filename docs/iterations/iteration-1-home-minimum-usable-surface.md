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

The author can install and directly open Avenor, Android recognizes it as an available Home application, and Home displays the localized current time, date, and weekday. Selecting time or date safely invokes the applicable system-owned destination. Returning Home and recreating the process returns to Home without a crash or an invalid task stack.

## Included work

- Create the minimum Android project at the product-repository root.
- Establish a reproducible debug build, selected automated-test foundation, installation, and focused validation commands.
- Qualify Avenor as an Android Home application while keeping it directly launchable when not selected as the default Home.
- Present Home time, date, and weekday according to system clock, format, and locale behavior.
- Provide default English and Simplified Chinese resources, with English fallback for unsupported locales.
- Use resource-backed user-facing strings, colors, and reusable dimensions.
- Implement the selected Home system-bar, inset, theme, typography, and touch-target baseline required by the included content.
- Invoke Clock and Calendar through defensive system-owned actions with localized failure behavior.
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
- The initial application identifier and signing relationship affect later installation continuity and must be treated as durable once a formal version is produced.
- Intermediate debug installation is not the formal `1.0.0` artifact.

## Security, privacy, permission, and licensing impact

- No network, account, analytics, broad package visibility, hidden-profile access, cloud backup, or device-to-device transfer is introduced.
- The merged manifest is inspected for dependency-contributed permissions, components, and backup behavior.
- Direct and transitive dependencies, versions, maturity, licenses, and manifest contributions are recorded from the resolved graph.
- No signing secret is created or stored without separate project-author authorization.

## Risks and unresolved decisions

- The exact stable toolchain has not been built in this repository.
- API 37 `compileSdk` reproducibility remains evidence-dependent; API 36 fallback is author-reserved.
- Home role, task-stack, repeated Home invocation, and direct-launch behavior may vary by environment.
- Clock or Calendar destinations may be absent or behave differently across devices.
- A consequential project/module/UI choice may require an ADR after the initial evidence exists.

## Validation plan

- Build from the repository wrapper with the selected JDK on a cleanly described environment.
- Run the selected initial automated tests and release lint when available.
- Inspect the merged manifest and resolved dependency graph.
- Install and directly launch the build on the API 31 emulator and both recorded physical devices.
- Verify Home qualification, system-owned Home selection, repeated Home invocation, Back behavior, and process recreation.
- Verify time, date, weekday, 12/24-hour behavior, English, Simplified Chinese, and English fallback resources.
- Verify Clock/Calendar success and unavailable-destination failure behavior without a crash.
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
