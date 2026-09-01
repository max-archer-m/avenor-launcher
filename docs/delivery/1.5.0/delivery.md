# Avenor Launcher 1.5.0 Delivery

> Semantic source: English. Chinese counterpart: [delivery.zh-CN.md](delivery.zh-CN.md).
>
> This record plans behavior selected from product-contract baseline `48d5bd470c84d222b6e89e128f438da1f25e595b`. Status and evidence do not authorize implementation, a version change, commit, push, tag, artifact movement, publication, or release.

## Version intent

`1.5.0` improves the ordinary Drawer after the separately planned `1.4.0` Home-module delivery. The selected result lets the author find applications locally by displayed name, reach Settings through the revised ordinary navigation, and choose a durable readable Drawer presentation without changing application ordering or the Home module model.

## Delivery level

`Author daily-use baseline`, as defined by [release governance](../../release.md).

The planned application identity remains `com.avenor.launcher` with `versionName` `1.5.0`. A candidate `versionCode` is provisional and must be selected from the next unused value only when a traceable APK is produced; planning does not reserve a value or override intervening artifact allocations.

## Product references

- [Product overview](../../../overview.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Drawer behavior](../../product/surfaces/drawer.md) and [Drawer presentation](../../product/presentation/drawer.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Navigation](../../product/navigation.md)
- [Privacy and data handling](../../product/features/privacy.md)
- [Validation guide](../../validation.md)

## Included scope and user journey

In ordinary Drawer, the author can search the reliable local inventory by displayed application name, open Settings from the final Settings row, and configure application size, arrangement, section anchors, and Transparent or Frosted-glass background. The accepted Home module and favorite state remain intact and Drawer action sheets preserve their source-specific boundary.

## Exclusions

- Home module creation, styling, ordering, movement, or adoption, which belong to the separately planned `1.4.0` delivery.
- Drawer shortcut ranking, package-name or pinyin search, fuzzy matching, or a relevance-based second application order.
- Theme customization outside Drawer, wallpaper sampling, or user-authored visual values.
- Third-party License presentation until its separate inventory and acceptance conditions are satisfied.
- Formal release artifact, public distribution, tag, milestone, or GitHub Release.

## Technical approach and risks

Development owns implementation details. Delivery must preserve reliable inventory identity, existing order, local-only processing, versioned atomic persistence, backup exclusion, current permission boundaries, and the accepted Home state. Any consequential persistence, rendering, inventory, or navigation decision requires author review and an ADR when applicable.

Primary risks are locale-dependent search behavior, stale result identity, Back/IME ambiguity, display-setting save races, loss of visible position after geometry changes, platform blur fallback, and regression of Home–Drawer–Settings navigation.

## Included iterations

| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [Iteration 26: Drawer Search and Ordinary Navigation](iteration-26-drawer-search-and-ordinary-navigation.md) | `Planned` | 2026-09-01 | Local application-name search and the revised ordinary Drawer navigation form one independently observable discovery increment. |
| [Iteration 27: Drawer Display Settings](iteration-27-drawer-display-settings.md) | `Planned` | 2026-09-01 | Durable layout, anchor, and background controls depend on the revised ordinary Drawer surface. |
| [Iteration 28: Upgrade, Regression, and Version Closure](iteration-28-upgrade-regression-and-version-closure.md) | `Planned` | 2026-09-01 | Version completion requires integrated Drawer, upgrade, compatibility, artifact, and author-acceptance evidence after the selected Drawer loop is complete. |

## Iteration evidence and results

### Iteration 26

[Contract](iteration-26-drawer-search-and-ordinary-navigation.md). No implementation or validation evidence exists. Status remains `Planned`; absent evidence is not a pass.

### Iteration 27

[Contract](iteration-27-drawer-display-settings.md). No implementation or validation evidence exists. Status remains `Planned`; absent evidence is not a pass.

### Iteration 28

[Contract](iteration-28-upgrade-regression-and-version-closure.md). No implementation or validation evidence exists. Status remains `Planned`; absent evidence is not a pass.

## Dependencies and sequence

Iteration 26 establishes the ordinary Drawer top app bar, search, final Settings row, and source-specific action-sheet behavior required before Iteration 27 adds display settings. Iteration 28 depends on accepted results from Iterations 26–27 and an accepted prior-version baseline. These dependencies do not bind work to a branch, terminal, contributor, forecast date, or permanent task line.

## Validation

The mandatory version environment is one author-designated primary physical device. Completion requires a traceable installable candidate, direct upgrade from the accepted prior-version baseline, preservation of readable Home and unrelated configuration, author acceptance of the complete selected Drawer journey, and no known included-path crash, ANR, destructive configuration error, wrong identity launch, or navigation dead end.

Focused automated checks and additional API 31, API 36/37, OEM, profile, clone, locale, font-scale, navigation-mode, blur-availability, process-recreation, inventory-change, and persistence-failure scenarios are recommended unless explicitly promoted. Executed failures on included behavior must be resolved or dispositioned; unperformed checks remain `Not run`, `Unknown`, or `Unavailable`.

## Artifact and release requirements

The accepted APK must retain `com.avenor.launcher`, use accepted `1.5.0` identifiers, be traceable to one source commit and signing category, and support the required upgrade journey. The author-local private signing identity remains required for in-place update continuity. APK retention, tag, publication, and distribution remain separately authorized.

## Known limitations and legacy issues

- Broader device, API, OEM, profile, clone, accessibility, blur, and performance coverage remains unknown until performed.
- Frosted glass may use its contracted more-opaque fallback when platform cross-window blur is unavailable.
- Minimum acceptable performance, power, memory, and startup thresholds remain undecided and are not version gates unless explicitly promoted.
- Search remains displayed-name contiguous matching in existing Drawer order; broader discovery is outside this version.

## Completion criteria

- Iterations 26–28 are `Completed` with separately recorded evidence and author acceptance.
- Product contracts, implementation, tests, and delivery evidence have no unresolved material mismatch in the selected scope.
- The mandatory physical-device upgrade and complete selected Drawer journey are accepted without loss of accepted Home state.
- Final identifiers, allocated `versionCode`, source commit, signing category, APK identity, known gaps, and tag disposition are recorded accurately.

## Completion result

No completion result exists. `1.5.0` and Iterations 26–28 remain `Planned`; this document does not authorize production implementation.
