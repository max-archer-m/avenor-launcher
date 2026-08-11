# Iteration 3: Drawer Navigation and Live-State Completeness

> Semantic source: English. Chinese counterpart: [iteration-3-drawer-navigation-and-live-state-completeness.zh-CN.md](iteration-3-drawer-navigation-and-live-state-completeness.zh-CN.md).
>
> Applies to the [Avenor Launcher 1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md). This iteration contract defines one product increment and its required evidence. It does not authorize implementation, approve candidate architecture, or declare the iteration complete. The project author must explicitly authorize implementation.

## Objective

Complete the `1.0.0` Drawer product behavior so application discovery remains fast, stable, and safe under locale-aware navigation, live inventory changes, and real-touch Home/Drawer gesture arbitration.

## Product and version references

- [1.0.0 delivery contract](../versions/1.0.0/delivery-contract.md)
- [1.0.0 product scope](../versions/1.0.0/product-scope.md)
- [Product navigation](../product/navigation.md)
- [Drawer interaction](../product/drawer.md)
- [Product design foundations](../product/design-foundations.md)

## Observable outcome

The author can find an intended launchable entry through stable locale-aware grouping, list scrolling, or the alphabet index; navigate continuously between Home and Drawer; and observe correct Drawer state when applications change, without accidental activation or unstable ordering.

## Included work

- Complete the Drawer contract's platform-transliteration-based, locale-aware grouping and mixed ordering, including section anchors and tie-breaking.
- Complete the shared one-line application-name marquee behavior, including eligibility, timing, single-active-entry priority, and pause or restart behavior during list motion and Home/Drawer transitions.
- Complete the `#` and non-empty A-Z alphabet index, active-character bubble, direct selection, continuous sliding, haptic steps, and independent index scrolling when required.
- Preserve or deterministically adjust the current anchor and relative position during live inventory updates.
- Handle application add, remove, enable, disable, rename, icon, badge, and exposed clone/profile changes.
- Complete the reversible Home/Drawer transition, progress, opacity, release threshold, target-directed fling, rebound, cancellation, and Back behavior.
- Complete list-boundary gesture transfer, pointer cancellation, additional-pointer safety, and exclusive alphabet-index ownership.
- Prevent transition and index gestures from accidentally launching, long-pressing, or scrolling an application entry.
- Preserve meaningful Drawer position within the same process where the product contract requires it.

## Excluded work

- Application action sheet and application-information action.
- Favorite creation, persistence, Home favorite launch, removal, reconciliation, or recovery.
- Settings gear behavior, because Settings is excluded from `1.0.0`; no placeholder is added.
- TalkBack-specific alternate alphabet-index interaction and broader device adaptations excluded by the product scope.
- Full-version measured quality, release signing, formal APK, archive, tag, or distribution actions.

## Technical change areas

- Platform transliteration, locale collation, normalized section assignment, tie-breaking, stable identity, list-position mapping, and shared marquee coordination.
- Alphabet-index input ownership, scrolling, haptics, and accessibility semantics within the selected scope.
- Project-owned transition controller and arbitration among Home scrolling, Drawer scrolling, surface transition, and index input.
- Inventory callback serialization, state updates, stale-entry prevention, and position preservation.

The product contract defines observable gesture and ordering behavior. Exact animation primitives, state types, and implementation algorithms remain code-level choices unless they create a consequential architecture decision.

## Dependencies and sequence

- The project author accepts the Iteration 2 inventory, identity, list, Retry, and exact-entry launch foundation as sufficient to extend; formal closure and completion of every recommended validation scenario are not required for entry.
- The selected transition state model can be extended without replacing accepted Home or inventory boundaries.
- The project author explicitly authorizes this iteration.
- The project author may authorize Iteration 4 when the observable navigation and pointer-ownership foundation is acceptable for adding long-press and known gaps are recorded.

## Migration and compatibility impact

- No production persistence schema is added.
- Stable ordering and position behavior must remain compatible with the identity model selected in Iteration 2.
- Gesture behavior requires physical touch evidence; emulator input is functional evidence only.
- Small-screen and broader form-factor adaptation remain outside `1.0.0`.

## Security, privacy, permission, and licensing impact

- No new sensitive permission, network behavior, data collection, or external service is required.
- Live inventory data remains within the existing local least-privilege boundary.
- Haptic and UI dependencies are reviewed through the resolved graph and merged manifest.
- A proposed dependency that expands permission, data, or license obligations requires review before integration.

## Risks and unresolved decisions

- Gesture arbitration is the highest custom-UI risk and may expose a product/implementation mismatch on real hardware.
- Platform transliteration and locale collation can differ across API levels, locale data revisions, and language configurations.
- OEM inventory callbacks can arrive in bursts or incomplete transient states.
- A platform-provided clone/profile badge may be absent even when identity remains distinct; Avenor-specific fallback treatment is outside scope.
- Any OEM limitation that prevents included behavior requires project-author disposition rather than silent acceptance.

## Validation plan

The following scenarios are recommended to reduce delivery risk and improve evidence. Unless the project author explicitly promotes a scenario to a gate, incomplete or unavailable results do not block this iteration's entry, exit, or progression. Missing results remain unknown and must be recorded rather than treated as passed.

- Unit-test platform transliteration, mixed Han and Latin ordering, case and Latin-diacritic handling, normalized section assignment, tie-breaking, anchor fallback, state reduction, and deterministic marquee eligibility and priority rules.
- UI-test index selection/sliding, loading/error recovery, application-name marquee timing and pause/restart behavior, Back, transition thresholds, fling decisions, cancellation, and accidental-activation prevention.
- Exercise live add/remove/disable/rename and unchanged-refresh behavior where automation is practical.
- Validate real-touch transitions, list-boundary transfer, index ownership, haptics, and position continuity on both physical devices.
- Run API 31 functional compatibility checks for grouping, navigation, list/index layout, and Back.
- Record commands, locale, environment, build identity, source commit, procedure, traces when useful, and results.

## Acceptance evidence

When performed, record the following recommended evidence. Missing recommended evidence does not by itself block author acceptance or progression:

- stable normalized grouping, mixed ordering, index, anchor, marquee, and live-update results across the required locales and API levels;
- gesture displacement, threshold, fling, rebound, cancellation, and pointer-safety evidence;
- physical-device observations for Samsung and Pixel;
- API 31 compatibility results;
- automated commands and retained focused evidence; and
- every unresolved OEM or product mismatch and its author disposition.

No acceptance evidence exists merely because this contract is present.

## Related decisions, commits, and tags

- Gesture or state-ownership ADR: add only if the proven decision is consequential and durable.
- Implementation commits: to be recorded from actual work.
- Tags: none authorized or required by this iteration.

## Final result

The project author may close this iteration or continue when the observable Drawer navigation, live-state, and Home/Drawer interaction foundation is acceptable and known gaps are recorded. Completing every recommended scenario is not required. Unperformed checks are not passed, product mismatches remain explicit, and the formal `1.0.0` gates are unchanged.

## Remaining issues and handoff

The handoff to [Iteration 4](iteration-4-application-action-sheet-and-favorite-creation.md) records accepted pointer ownership, position preservation, selected-entry identity, application launch behavior, and the safe long-press integration boundary. It must not transfer unfinished core Drawer behavior as action-sheet work.
