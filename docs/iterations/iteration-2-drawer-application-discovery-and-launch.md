# Iteration 2: Drawer Application Discovery and Launch

> Semantic source: English. Chinese counterpart: [iteration-2-drawer-application-discovery-and-launch.zh-CN.md](iteration-2-drawer-application-discovery-and-launch.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md). This iteration contract defines one product increment and its required evidence. It does not authorize implementation, approve candidate architecture, or declare the iteration complete. The project author must explicitly authorize implementation.

## Objective

Extend the accepted Home increment so the author can enter Drawer, see the launchable entries successfully read within Avenor's least-privilege boundary, continue using available entries when a non-current profile read fails, recover when no usable inventory is available, and launch the intended entry.

## Product and version references

- [1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md)
- [1.0.0 product scope](../versions/1.0.0/product-scope.md)
- [1.0.0 technical assessment](../versions/1.0.0/technical-assessment.md)
- [Product navigation](../product/navigation.md)
- [Drawer interaction](../product/drawer.md)
- [Product design foundations](../product/design-foundations.md)

The linked product documents remain authoritative. This iteration establishes the core discovery and launch path; Iteration 3 completes the advanced navigation and live-state behavior.

## Observable outcome

Starting from the accepted Home build, the author can use the approved upward interaction to enter Drawer, observe a single list of platform-exposed launchable entries, recover from loading failure with Retry, select an entry, and launch that exact application. Back returns to Home.

## Included work

- Establish the project-owned launchable inventory boundary and immutable snapshot used by UI state.
- Use Android's launcher-aware platform boundary for inventory and exact-entry launch.
- Treat each platform-exposed launchable activity as an independent entry and avoid package-name-only deduplication.
- Present platform labels, icons, and badges when available, with safe generic-icon fallback. Route icon presentation through the replaceable boundary defined by [ADR-0001](../decisions/0001-establish-replaceable-launcher-icon-rendering.md): preserve native adaptive behavior, normalize legacy artwork inside the device mask without cropping its identifying content, and apply profile or clone badging after normalization.
- Present a stable single-column application list sufficient for core discovery, using the product-defined platform transliteration and locale-aware mixed ordering as the foundation completed by Iteration 3.
- Implement distinct Loading, Content, and Error states. Treat an empty complete read or a failure that leaves no usable inventory as Error and provide localized manual Retry. An isolated non-current-profile failure may omit that profile while preserving available Content without a required warning.
- Implement a basic binary state transition that enters Drawer after the upward drag reaches the contract-aligned `120dp` release gate and returns through Back without accidental application activation. This interim entry path does not implement or satisfy the complete direct-manipulation transition assigned to Iteration 3.
- Launch the selected entry defensively and suppress duplicate rapid activation.
- Validate primary, cloned, and normally exposed profile candidates on the recorded physical devices.
- Keep Private Space and hidden-profile access outside the inventory boundary.

## Excluded work

- Continuous drag progress, the complete release decision including fling targeting, rebound, list-boundary transfer, pointer arbitration, final alphabet-index interaction, complete section-anchor behavior, and live-update position preservation assigned to Iteration 3.
- Application action sheet, application information, favorites, persistence, reconciliation, and Home favorite presentation.
- Settings, reorder, shortcuts, uninstall, clone removal, or any other excluded `1.0.0` action.
- Full-version performance, signing, formal APK, archive, tag, or distribution actions.

## Technical change areas

- Project-owned inventory models, identity candidates, repository boundary, callbacks, immutable UI state, and the normalized complete-name comparison foundation.
- `LauncherApps` or another evidence-supported launcher-aware platform adapter.
- The replaceable icon-rendering boundary and current `SystemAdaptive` policy accepted by ADR-0001. Rendered icons are derived presentation rather than launchable identity or persisted truth; algorithm constants and image-analysis heuristics remain in code and tests.
- Core Drawer list rendering, Loading/Content/Error state reduction, manual Retry, exact-entry launch, and duplicate-activation suppression.
- Extensible Home/Drawer route state and Back behavior needed for the observable path; this is not acceptance of the complete product gesture contract.

The device evidence must determine whether the candidate profile-plus-`ComponentName` identity is viable. The iteration does not approve identity or architecture by assertion; consequential proven choices are documented separately.

## Dependencies and sequence

- The project author has accepted the Iteration 1 implementation and observed Home behavior as sufficient to continue delivery on the existing single-activity Compose foundation. This continuation decision does not claim that Iteration 1 is formally closed.
- Remaining Iteration 1 CLI, automated-test, release-lint, dependency, merged-manifest, emulator, physical-device, and focused Home validation gaps are explicitly recorded in its handoff. They are recommended follow-up evidence and do not block this iteration's entry or exit; any item required by the final `1.0.0` contract remains due before formal version completion.
- The project author separately authorizes this iteration and any required technical decision arising from the platform spike.
- The project author may authorize Iteration 3 when the observable core list and launch foundation are acceptable for extension and known gaps are recorded; completing every recommended validation scenario is not required for progression.

## Migration and compatibility impact

- No production persistence schema is introduced by this iteration.
- The inventory and identity model must preserve a migration path to later favorite persistence.
- API 31 through API 37 remain the product range; emulator evidence cannot substitute for Samsung or Pixel clone/profile behavior.

## Security, privacy, permission, and licensing impact

- Do not declare `QUERY_ALL_PACKAGES`, `ACCESS_HIDDEN_PROFILES`, `INTERNET`, usage access, notification access, contacts, location, file, photo, or accessibility-service capabilities.
- Package visibility and any manifest query must be necessary and traceable to the selected path.
- External launch operations tolerate absence, revocation, disabled state, and `SecurityException` without exposing raw exception text.
- Inventory metadata remains local and is not logged in full in release builds.
- Reinspect the merged manifest and dependency graph after inventory dependencies are resolved.

## Risks and unresolved decisions

- Samsung and Pixel may expose clone/profile entries, badges, and launch behavior differently.
- Platform transliteration and locale collation results may differ across supported API levels or locale data revisions; the implementation must follow the current product contract and record observed compatibility differences rather than introduce a separate pronunciation dictionary.
- ADR-0001 records accepted Samsung evidence for the current device mask, normalized legacy-icon recognizability, and badge presentation, while Pixel behavior remains recommended unverified evidence. Home has no icon consumer in this iteration; cross-surface consistency should be included in the recommended verification when the first Home icon consumer is delivered. Exact parity with proprietary OEM effects is not required.
- The current source isolates `IllegalStateException` or `SecurityException` from a non-current profile by omitting that profile's activities while preserving available Content. This matches the current non-blocking product direction. Device evidence must still record which entries may be absent and confirm that partial failure does not crash Avenor, block available launch, or cause destructive favorite reconciliation.
- The current source uses a `120dp` release gate followed by a direct binary surface switch. Matching the final distance does not constitute acceptance of the continuous position, opacity, fling, rebound, cancellation, or gesture-transfer contract assigned to Iteration 3.
- Android callbacks may not distinguish temporary unavailability from permanent disappearance.
- A stable identity suitable for later persistence is not proven until physical-device evidence exists.
- The complete gesture controller remains unfinished until Iteration 3; the core transition must not establish an incompatible state model.
- An inability to read any usable inventory remains an Error and is not permission to add broad visibility.

## Validation plan

The following scenarios are recommended to reduce delivery risk and improve evidence. Unless the project author explicitly promotes a scenario to a gate, incomplete or unavailable results do not block this iteration's entry, exit, or progression. Missing results remain unknown and must be recorded rather than treated as passed.

- Unit-test identity comparison, duplicate prevention, normalized complete-name comparison, mixed Han and Latin ordering, case and Latin-diacritic handling, stable ordering, state reduction, empty-result classification, Retry transitions, and activation throttling.
- Instrument the platform adapter where practical for inventory and exact-entry launch.
- Force a complete empty result, current-profile read failure, non-current-profile read failure, failed Retry, and successful Retry. Verify that Loading, Error, and Content remain distinguishable, a non-current-profile failure preserves usable Content without a crash or full-surface Error, and a failure with no usable inventory enters Error.
- Verify loading, failure, manual Retry, Back, the interim `120dp` entry gate, and core binary transition behavior through UI tests without claiming complete navigation-transition acceptance.
- Validate adaptive, legacy, fallback, primary, cloned, and work-profile icon cases in Drawer. Confirm that the device mask follows the platform, legacy artwork remains recognizable inside its safe region, badging is applied after normalization, and the renderer remains reusable by the later Home icon consumer without changing identity or persistence.
- Validate list contents and launch behavior on the API 31 emulator and both physical devices.
- Record primary, clone/profile, badge, and launch observations separately for Samsung and Pixel.
- Inspect package visibility, merged manifest, dependency graph, and absence of unapproved declarations.
- Record actual commands, build identity, source commit, environment, procedure, and results.

## Acceptance evidence

Current narrow evidence:

- Accepted ADR-0001 records Samsung physical-device acceptance of the current device-mask shape, normalized legacy-icon recognizability, and profile/clone badge presentation, plus successful debug Kotlin and Android-test Kotlin compilation for that boundary.
- Current repository source contains Loading/Content/Error reduction, localized error presentation, manual Retry, exact-entry launch, platform-transliteration-based comparison, icon normalization, the interim `120dp` entry gate, and regression test sources including a mixed Han and Latin ordering case. Their presence is source evidence, not proof that every test or required environment has passed.

When performed, record the following recommended evidence. Missing recommended evidence does not by itself block author acceptance or progression:

- the exact successful automated and manual validation commands;
- inventory and exact-entry launch results for every required environment;
- normalized mixed-ordering results for applicable locales and supported API levels, including observed platform-transliteration differences;
- Samsung and Pixel clone/profile identity observations;
- adaptive, legacy, fallback, profile/clone-badge, Samsung, and Pixel Drawer results linked to ADR-0001, plus an explicit handoff for later Home/Drawer consistency validation when Home gains an icon consumer;
- merged-manifest and least-privilege evidence;
- the exact environment and failure class for each observed inventory-load failure, whether it affected the current or another profile, and the resulting Loading/Error/Content state;
- empty-result, non-blocking partial-profile failure, manual Retry, failed-retry, and successful-retry results; and
- every unresolved OEM limitation or contract mismatch transferred for author decision.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Icon rendering ADR: [ADR-0001: Establish a Replaceable Launcher Icon Rendering Boundary](../decisions/0001-establish-replaceable-launcher-icon-rendering.md).
- Identity or inventory ADR: add only if another proven decision is consequential and durable.
- Validation-governance commit: `2db2e12` (`docs(project): clarify iteration validation policy`).
- Product-contract commit: `4d40b6c` (`docs(product): refine Drawer inventory and presentation contracts`).
- Implementation commit: `2aca840` (`feat(drawer): add application discovery and launch`).
- Tags: none authorized or required by this iteration.

## Final result

The project author may close this iteration or continue when the observable Home-to-Drawer discovery and exact-entry launch foundation is acceptable and known gaps are recorded. Completing every recommended scenario is not required. Unperformed checks are not passed, unresolved core mismatches remain explicit, and the formal `1.0.0` gates are unchanged.

## Remaining issues and handoff

The handoff to [Iteration 3](iteration-3-drawer-navigation-and-live-state-completeness.md) records the accepted inventory and identity behavior, non-blocking partial-profile behavior, current Drawer state model, transition baseline, ADR-0001 icon boundary, remaining Pixel evidence, recommended later Home/Drawer consistency verification, inventory-failure classification and recovery behavior, OEM observations, and advanced navigation work still required. It does not transfer an omitted entry from a successfully read source, an incorrectly launched available entry, an unbounded retry loop, or destructive icon-boundary replacement as normal polish.
