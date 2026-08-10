# Iteration 2: Drawer Application Discovery and Launch

> Semantic source: English. Chinese counterpart: [iteration-2-drawer-application-discovery-and-launch.zh-CN.md](iteration-2-drawer-application-discovery-and-launch.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](../versions/1.0.0/README.md). This iteration contract defines one product increment and its required evidence. It does not authorize implementation, approve candidate architecture, or declare the iteration complete. The project author must explicitly authorize implementation.

## Objective

Extend the accepted Home increment so the author can enter Drawer, see every launchable entry Android exposes within Avenor's least-privilege boundary, recover from an inventory failure, and launch the intended entry.

## Product and version references

- [1.0.0 delivery contract](../versions/1.0.0/README.md)
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
- Present platform labels, icons, and badges when available, with safe generic-icon fallback.
- Present a stable, locale-aware single-column application list sufficient for core discovery.
- Implement Drawer loading, empty-or-failed, Retry, successful recovery, and localized failure behavior.
- Implement the core reversible Home/Drawer entry and Back path without accidental application activation.
- Launch the selected entry defensively and suppress duplicate rapid activation.
- Validate primary, cloned, and normally exposed profile candidates on the recorded physical devices.
- Keep Private Space and hidden-profile access outside the inventory boundary.

## Excluded work

- Final alphabet index interaction, complete section-anchor behavior, live-update position preservation, and every advanced gesture edge case assigned to Iteration 3.
- Application action sheet, application information, favorites, persistence, reconciliation, and Home favorite presentation.
- Settings, reorder, shortcuts, uninstall, clone removal, or any other excluded `1.0.0` action.
- Full-version performance, signing, formal APK, archive, tag, or distribution actions.

## Technical change areas

- Project-owned inventory models, identity candidates, repository boundary, callbacks, and immutable UI state.
- `LauncherApps` or another evidence-supported launcher-aware platform adapter.
- Core Drawer list rendering, loading/error state, Retry, exact-entry launch, and duplicate-activation suppression.
- Core Home/Drawer transition state and Back behavior needed for the observable path.

The device evidence must determine whether the candidate profile-plus-`ComponentName` identity is viable. The iteration does not approve identity or architecture by assertion; consequential proven choices are documented separately.

## Dependencies and sequence

- Iteration 1 is closed with reproducible commands and accepted Home behavior.
- The project author approves this iteration and any required technical decision arising from the platform spike.
- Completion unlocks Iteration 3 only when the core list and launch path are correct enough that advanced navigation can be added without replacing the inventory boundary.

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
- Android callbacks may not distinguish temporary unavailability from permanent disappearance.
- A stable identity suitable for later persistence is not proven until physical-device evidence exists.
- The complete gesture controller remains unfinished until Iteration 3; the core transition must not establish an incompatible state model.
- An inability to meet the included inventory boundary is a product contract mismatch, not permission to add broad visibility.

## Validation plan

- Unit-test identity comparison, duplicate prevention, stable ordering, state reduction, Retry transitions, and activation throttling.
- Instrument the platform adapter where practical for inventory and exact-entry launch.
- Verify loading, failure, Retry, recovery, Back, and core transition behavior through UI tests.
- Validate list contents and launch behavior on the API 31 emulator and both physical devices.
- Record primary, clone/profile, badge, and launch observations separately for Samsung and Pixel.
- Inspect package visibility, merged manifest, dependency graph, and absence of unapproved declarations.
- Record actual commands, build identity, source commit, environment, procedure, and results.

## Acceptance evidence

Before closure, record:

- the exact successful automated and manual validation commands;
- inventory and exact-entry launch results for every required environment;
- Samsung and Pixel clone/profile identity observations;
- merged-manifest and least-privilege evidence;
- loading, failure, Retry, and recovery results; and
- every unresolved OEM limitation or contract mismatch transferred for author decision.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Identity or inventory ADR: add only if the proven decision is consequential and durable.
- Implementation commits: to be recorded from actual work.
- Tags: none authorized or required by this iteration.

## Final result

The iteration closes only when the author can demonstrate the Home-to-Drawer discovery and exact-entry launch path, the inventory boundary remains least-privilege, required environment evidence is recorded, and no unresolved core mismatch is hidden. Before then, no completion is claimed.

## Remaining issues and handoff

The handoff to [Iteration 3](iteration-3-drawer-navigation-and-live-state-completeness.md) records the accepted inventory and identity behavior, current Drawer state model, transition baseline, OEM observations, and advanced navigation work still required. It does not transfer an omitted or incorrectly launched included entry as normal polish.
