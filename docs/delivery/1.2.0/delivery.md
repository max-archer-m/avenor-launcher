# Avenor Launcher 1.2.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This delivery record defines and closes the behavior selected for `1.2.0`. It does not authorize a later product or architecture change, signing-secret movement, a tag, GitHub Release, upload, publication, or distribution.

## Version intent

`1.2.0` strengthens the V1 fixed-presentation daily-use baseline through three delivered increments: stable application-data loading and Home return behavior; the contracted Home basic-information alignment and editable primary/companion favorite composition; and continuous Home–Drawer navigation with stable transition and Drawer index-anchor behavior.

Iterations 12-14 are the final `1.2.0` iteration set. Product-contract changes committed after the Iteration 14 implementation boundary are not retroactively included in this version and require selection by a later delivery contract.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

- `versionName`: `1.2.0`.
- `versionCode`: `3`.
- Application identity remains `com.avenor.launcher`.
- Source commit `b119aadbdff04284f02c106fd93a59121641dbea` applies these identifiers and produced the traceable local debug APK recorded below.

## Product references

- [Product overview](../../../overview.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Navigation](../../product/navigation.md)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Design foundations](../../product/design-foundations.md)
- [Privacy and data handling](../../product/features/privacy.md)
- [Validation guide](../../validation.md)
- [Release governance](../../release.md)

## Included scope and user journey

The current `1.2.0` plan lets the author return reliably to an already usable Home without unnecessary blocking reloads; use the contracted Home date alignment and directly access, scroll, launch, reorder, move, and swap favorites across the primary and companion groups; and move continuously between Home and Drawer while retaining the specified release thresholds, position/opacity path, scroll-to-transition handoff, and discrete Drawer index-anchor navigation.

The accepted `1.1.0` Home, Drawer, application launch, action-sheet, Settings, and local-data behavior remains the compatibility baseline unless a listed iteration contract explicitly selects a current product-contract change.

## Exclusions

- Network, account, cloud sync, recommendation, analytics, AI, automatic favorite sorting, folders, backup, restore, or broader customization.
- Android Private Space support or `ACCESS_HIDDEN_PROFILES`.
- Moving the optional double-tap-lock accessibility service from its current build boundary into a formal mainline or release configuration.
- A new Drawer grid, a new Settings capability, or a product behavior not selected by an included iteration contract.
- Formal release artifact, public distribution, tag, milestone, GitHub Release, or store publication.
- Favorite-list, secondary-ribbon, and Drawer multi-selection behavior defined after the Iteration 14 implementation boundary; those product-contract changes belong to a later delivery selection.

## Technical approach and risks

Implementation details remain owned by development. Iteration 12 must distinguish process-start loading, same-process return, process recreation, explicit retry, and real inventory-change reconciliation without suppressing required refreshes. Iteration 13 must preserve readable `1.0.0` and `1.1.0` favorite identity and order while introducing group assignment, and must fail closed when stored state is unreadable. Iteration 14 must coordinate nested scrolling, pointer ownership, transition progress, opacity, endpoint settling, and index-driven list movement without accidental application actions or discontinuities.

Consequential changes to persistence format, state ownership, navigation architecture, permissions, privacy, signing, or acceptance intent require author direction and an ADR or specialist review when applicable. Performance work must be supported by observable behavior or measured evidence and must not introduce speculative infrastructure.

## Included iterations

| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [Iteration 12: Loading and Home return foundation](iteration-12-loading-and-home-return.md) | `Completed` | 2026-08-18 | Implementation is complete; the author reported that device acceptance passed. |
| [Iteration 13: Home information and editable favorite composition](iteration-13-home-favorite-composition.md) | `Completed` | 2026-08-20 | Implementation is included in the amended local delivery commit; the author reported that device acceptance passed. |
| [Iteration 14: Continuous Drawer navigation and anchors](iteration-14-continuous-drawer-navigation.md) | `Completed` | 2026-08-21 | Implementation is present in source commit `5e86d6dcc2f7b3cca5a6f669ed5b591f1249e365`; the author reported that packaging and device acceptance passed. |

These are the final iterations selected for `1.2.0`. Later product-contract changes do not alter this protected delivery boundary.

## Iteration evidence and results

### Iteration 12

[Contract](iteration-12-loading-and-home-return.md). Implementation is complete. The author reported that device acceptance passed. Gradle build/test execution by development is `Not run`; detailed device and artifact evidence remains outside this iteration closure. Status is `Completed`.

### Iteration 13

[Contract](iteration-13-home-favorite-composition.md). Implementation and focused review are complete. `git diff --check` and the changed implementation line-width sweep are `Passed`; Gradle build and instrumented tests are `Not run`. The author reported that device acceptance passed. No APK, release artifact, tag, push, or publication was created. Status is `Completed`.

### Iteration 14

[Contract](iteration-14-continuous-drawer-navigation.md). Implementation and focused review are complete. `git diff --check` and the changed implementation line-width sweep are `Passed`. Under author authorization, `assembleDebug` and `assembleDebugAndroidTest` are `Passed`. One instrumented-test run executed 52 tests with 3 failures, all of them defects in test code rather than product behavior: two iteration-14 test fixtures were corrected, and one pre-existing non-void `FavoriteStoreTest` declaration set was corrected outside iteration-14 scope. The verification re-run after those corrections is `Not run` because the author interrupted it. The author reported that packaging and device acceptance passed. A local debug APK was produced by the authorized build; no release artifact, tag, push, or publication was created. Status is `Completed`.

## Dependencies and sequence

The delivered dependency order was Iteration 12, then Iteration 13, then Iteration 14.

- Iteration 12 establishes the loading, return, and state-reuse behavior needed by later Home and Drawer work.
- Iteration 13 follows Iteration 12 because the two-group favorite composition must use stable loading and restoration semantics.
- Iteration 14 follows Iterations 12 and 13 because its gesture arbitration must cover loading/error controls and the final primary/companion scroll regions.

This dependency order does not bind an iteration to a branch, terminal, contributor, start date, or forecast completion date. A later added iteration must declare where it depends on or can proceed independently from the current sequence.

## Validation

The required version environment is one author-designated primary physical device. Version completion requires:

- an installable APK with the accepted `1.2.0` identifiers;
- successful installation or in-place upgrade without unintended loss, duplication, reordering, or wrong-group assignment of readable existing favorites;
- author acceptance of the complete included journey for every iteration listed at version-completion time;
- same-process Home return without restoring an inappropriate transient surface or replacing usable Home content with blocking Loading;
- complete primary/companion presentation and editing behavior, including persistence across the applicable restart scenarios;
- continuous Home–Drawer position, opacity, nested-scroll handoff, release, rebound, and index-anchor behavior;
- no known included-path crash, ANR, destructive favorite-state error, wrong application launch, or navigation dead end; and
- accurate disposition of every skipped, unknown, failed, or unavailable check affecting the selected daily-use baseline.

Focused automated checks and additional API, OEM, locale, profile, cloned-application, process-recreation, font-scale, and navigation-mode coverage are recommended evidence. They become mandatory only when the author or a later version-contract amendment explicitly promotes them.

## Artifact and release requirements

The local debug APK built from source commit `b119aadbdff04284f02c106fd93a59121641dbea` uses `applicationId` `com.avenor.launcher`, `versionName` `1.2.0`, and allocated `versionCode` `3`. Its SHA-256 is `6E9B607A84A874C0EE4888945308647FCF82455A1F958ED22B9A7ABC62C8C70F`, and its signing category is debug. The APK is not designated a formal release artifact and no tag, upload, publication, or distribution is authorized by this record.

## Known limitations and legacy issues

- Performance evidence for Iterations 12-14 is limited to author device observation; no measured data is recorded.
- Product-contract changes made after the Iteration 14 implementation boundary are intentionally deferred to a later version selection.
- Broader device, API, OEM, profile, clone, and accessibility coverage remains unknown until performed.
- Formal-release evidence and public-distribution readiness are outside the selected delivery level.

## Completion criteria

- Iterations 12-14 are `Completed` and accepted by the author.
- The version validation and upgrade conditions above are satisfied on the designated primary device.
- Product documents, implementation, tests, and recorded evidence have no unresolved material contract mismatch within the selected scope.
- The completion result records the accepted identifiers, source commit, signing category, device environment, upgrade result, included-journey result, material known gaps, and author decision.

## Completion result

`1.2.0` is closed as the author-directed historical boundary for Iterations 12-14. The author previously reported packaging and designated-device acceptance for their included journeys. Source commit `b119aadbdff04284f02c106fd93a59121641dbea` sets `versionName` `1.2.0` and `versionCode` `3`; after one failed attempt caused by locked generated output, a clean `assembleDebug --no-daemon` run on 2026-08-22 passed and produced the debug APK identified above. Installing that exact identifier build and performing an in-place upgrade from the accepted `1.1.0` baseline are `Not run`; the author explicitly directed version closure with those gaps visible rather than treating them as passed. No tag, GitHub Release, upload, publication, or distribution occurred.
