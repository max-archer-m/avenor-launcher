# Iteration 31: Edit Dock, Drawer Background, Panel Shadow, and Multi-Selection Presentation Rework

> Applicable version contract: [1.6.0 delivery](delivery.md). This contract defines the authorized delivery boundary only. It does not own execution state, evidence, commits, or results, and it does not by itself authorize production implementation, a commit, a push, a tag, or any release action.

## Objective

Deliver four author-accepted reworks exactly as defined by the accepted product contracts: the Home edit dock moved to the top of safe content with the purely presentational panel slot-swap animation, the Drawer background reworked into one continuous background-opacity percentage slider with a percentage readout and no switch, blur, or fallback, the shared style settings panel edge border, and the favorite multi-selection presentation redefined with unified overlay badges.

## Product and version references

- Product-contract baseline: `4f596a5fe9de30785dd0e06c2f66a727e481bb9a` — the commit that integrates the accepted 2026-09-09 edge-border and Drawer-panel-animation contract revisions, which supersede the panel-edge shadow portions of `32d7d25a0fed9e1784518c85796b5f38d18960fc`; the edit-dock, Drawer-background, and favorite-multi-selection revisions of the earlier baselines remain included.
- Applicable product documents:
  - [Home behavior](../../product/surfaces/home.md), edit mode, collapsed dock, and expanded panel sections
  - [Home presentation](../../product/presentation/home.md), including the panel animation duration and easing values
  - [Drawer behavior](../../product/surfaces/drawer.md), display-settings and favorite multi-selection sections
  - [Drawer presentation](../../product/presentation/drawer.md), including the multi-selection presentation section
  - [Style settings panel presentation](../../product/presentation/style-settings-panel.md), including the readout-and-slider row and edge border values
  - [Design foundations](../../product/design-foundations.md), shared placement and toggle hot-zone principles
- Applicable version contract: `delivery.md`

## Observable outcome

On the primary device, Home edit mode shows the fixed dock row at the top of safe content in both panel states; expanding and collapsing the style settings panel animates the information region and panel as one slot swap without covering the list. The Drawer display-settings background group offers one continuous percentage slider with its fixed-width percentage readout and the contracted defaults, live preview, commit-on-release, and interruption-revert behavior. The Home and Drawer style settings panels both render the contracted edge border. Favorite multi-selection presents no leading indicator, and a selected row shows the scaled, outlined cell with its overlaid order badge while unselected and disabled rows stay plain.

## Included work

- The fixed top edit dock row above the basic-information region and main list, with the dock and its toggle affordance at one unchanged position and interaction target across collapsed and expanded states, changing only directional artwork and instruction text, and toggling that never scrolls the main list or changes its logical scroll position.
- The `48dp` dock height and `48dp x 48dp` affordance target, removing the prior dock-specific height exception.
- The animated panel slot swap: expanding collapses the information region to zero while the panel grows to its content height and the main list absorbs the difference continuously; the panel stays non-covering, non-modal, and scrim-free, with the purely presentational semantics of immediate state commitment, interruption resolution to the final state, re-toggle retargeting, reduced-motion skipping, and current-bounds hit testing during the animation.
- The Drawer background group as one continuous background-opacity slider with its fixed-width percentage readout: the selected percentage composites `darkSurfaceBaseColor` over the wallpaper at that opacity, `0` presents the wallpaper clear, `100` presents the solid surface color, and a fresh configuration defaults to `50`.
- The continuous `0`–`100` slider model: whole-percentage granularity (tentatively accepted), the platform-standard continuous progress treatment without tick marks, the active track and thumb in `primaryTextColor` with the inactive track in `secondaryTextColor`, the `100%`-sized readout reservation, and accessibility percentage announcements with range actions stepping by `1`.
- Removal of the platform cross-window blur, its availability detection, and the Transparent-presentation fallback: the background is the single opacity treatment on every device, and the presentation-defined text and artwork shadows remain in effect at every percentage.
- The slider drag behavior: continuous preview without persistence, exactly one display-setting save committed at pointer release, immediate reversion to the persisted state on interruption before release, and one short tick haptic per whole-percentage value change during the drag, under the unchanged single-unresolved-save rule.
- Persistence of the background-opacity percentage with versioned migration: absent fields resolve per the contract default rule, the prior transparent/frosted-glass mode choice is not mapped, and the percentage enters the backup file contents and the local data enumeration.
- The shared readout-and-slider row geometry with the `primaryTextColor` accent and the contracted control gap.
- The style settings panel edge border on the Home and Drawer panels: one `1dp` border in `#6E6E73` drawn along the panel corner radius, using the author-accepted delivery values.
- The Drawer style settings panel opens and closes with one height property animation — growing from `0` to its content height and retracting back — on the shared short-duration property-animation token.
- The favorite multi-selection presentation rework: multi-selection rows reusing the ordinary inventory's items-per-row grid and arrangements without layout change, no selection indicator on available unselected rows, and selected cells treated with the approximately `90%` visual scale transform with unchanged layout bounds, the complete `2dp` `primaryTextColor` outline with `12dp` corners, and one non-interactive overlaid top-left order badge styled per the presentation specification.
- The disabled already-favorited rows stripped of badge, outline, and scaling, with the preserved selection toggling, order numbering and renumbering, confirm and save semantics, and accessibility semantics of the selection model.
- Localized English and Simplified Chinese strings for any new or changed user-visible text, including the background-opacity slider label and its percentage format.
- Focused test sources covering dock placement and hot-zone stability, animation interruption and retargeting, slider mapping and release-commit behavior, interruption reversion, migration of prior background values, and border realization.

## Excluded work

- Wallpaper sampling, blur in any form, position- or pointer-dependent background variation, or user-authored visual values.
- Any change to the edge treatment's structure or rules: device comparison may calibrate values only, never the border structure or mapping relationships.
- Behavior changes outside the Home edit-mode layout, the Drawer display-settings background group, the style settings panel presentation, and the favorite multi-selection presentation.
- Any change to favorite multi-selection interaction semantics: selection toggling, order numbering and renumbering, Confirm and save behavior, and the selection model's accessibility semantics remain unchanged.
- The Iteration 29 backup and restore scope and the Iteration 30 drag-to-favorite scope, beyond the shared display-settings persistence that carries the intensity level.
- The `1.6.0` version identifier update (`versionName`/`versionCode`), which remains a version-level closure concern unless a later authorized amendment assigns it here.
- The third favorite-module type, a vertical favorites list, and third-party License presentation.

## Technical change areas

- Home edit-mode layout: dock relocation to the top of safe content, the animated slot swap, viewport height computation across both panel states, and logical scroll-position preservation.
- Drawer background rendering: the full-surface `darkSurfaceBaseColor` layer at the persisted opacity percentage; removal of platform blur, availability detection, and every fallback path.
- Display-settings store: the background field reworked into the opacity percentage with versioned migration, and the drag-preview path that commits its save at pointer release.
- Style settings panel UI: the switch-and-slider row and the edge border treatment shared by Home and Drawer panels.
- Drawer multi-selection UI: row composition on the ordinary items-per-row grid, the selected-cell scale and outline treatment, and the non-interactive order-badge overlay, without changes to the selection model.
- Backup and restore serialization: the background-opacity percentage within the display-settings section.
- Resources: new or changed English and Simplified Chinese strings with complete name parity.
- Tests: focused local and instrumentation sources for the behaviors above.

## Dependencies and sequence

This iteration depends on the edit-dock, panel-shadow, and favorite-multi-selection contract revisions integrated in `7af7adba15b018886dd613c587dfedd9299136b4`, the accepted 2026-09-09 background-opacity contract revision, and the current integrated mainline. Its code areas overlap Iteration 29 in the display-settings store, backup serialization, and style panel, and Iteration 30 in the Home edit-mode layout and Drawer application-row UI, so the three iterations must not run concurrently on one line; their relative order is otherwise free, with the implementation line of each later iteration containing the integrated results of the earlier ones it depends on.

## Migration and compatibility impact

- The Drawer background setting changes shape: absent background-opacity fields resolve under the contract default rule to the default `50`, and prior Transparent and Frosted-glass mode values are intentionally unmapped; the versioned atomic persistence and its migration approach remain the implementation responsibility within that rule.
- Backups written before this change are interpreted under the directional schema-compatibility rule of the Settings contract; a missing background field resolves to the contracted default.
- No change to favorites persistence, the one-time adoption reset, or the Android cloud-backup boundary.

## Security, privacy, permission, and licensing impact

- No new permission, networking, account, or external service is introduced; opacity compositing and shadow drawing are local presentation behavior.
- The background-opacity percentage enters the backup file, which remains an unencrypted, user-managed local file within the disclosed Privacy boundary; the Privacy wording is aligned in the same contract change. If implementation reveals a material divergence from the Privacy statement, stop and obtain author direction.

## Risks and unresolved decisions

- Low background percentages trade readability for wallpaper visibility by user choice; the fixed foreground shadows must remain contracted at every percentage, and the selected value is judged by author-device acceptance.
- Migrating prior enum-valued background configurations must not lose unrelated display settings or trigger the failure path, while the prior mode choice itself is intentionally unmapped.
- The slot-swap animation must keep hit testing, scroll position, and drag-to-favorite destination geometry consistent with the committed state, including during interruptions and retargeting.
- The slider's release-committed save must respect the single-unresolved-save rule and cannot begin a second change before that save resolves.
- Border values are author-accepted 2026-09-09 delivery values, chosen through device comparison over the earlier shadow-ring treatment; device comparison may still amend the values, never the treatment's structure.
- The selected-cell scale must remain a purely visual transform with unchanged layout bounds and hit targets, and the order badge must stay a non-interactive overlay that occupies no layout space and exposes no second focus target.
- Reusing the ordinary items-per-row grid in multi-selection must not alter ordinary Content layout or launch behavior.

## Amendment record

- 2026-09-08: Author-directed material amendment. The author accepted the favorite multi-selection presentation rework on 2026-09-08 (contract integrated in `7af7adba15b018886dd613c587dfedd9299136b4`) and directed that it join this iteration as a fourth presentation-only rework. Previous boundary: the edit-dock, Drawer-background, and panel-shadow reworks under baseline `5997845de3c099f20b5f1d6184538a1ee8f24936`. New boundary: the favorite multi-selection presentation rework is added as included work, with interaction semantics explicitly excluded; the baseline moves to `7af7adba15b018886dd613c587dfedd9299136b4`. Affected obligations: multi-selection acceptance criteria and validation scenarios are added; the iteration identifier and file slug remain unchanged.
- 2026-09-09: Author-directed material amendment. Device research established that the author's primary device (Samsung Galaxy S23 Ultra) does not support real-time cross-window blur, leaving the contracted frosted-glass mode unobservable on the primary device. The author accepted reworking the Drawer background into one continuous background-opacity percentage (`0`–`100` whole percentages, default `50`) compositing `darkSurfaceBaseColor` over the wallpaper, with a fixed-width percentage readout, no switch, no blur, and no fallback; slider drags preview without persisting and commit exactly one save at pointer release, reverting immediately to the persisted state on pre-release interruption; and one short tick haptic per whole-percentage change during touch drags. This decision supersedes the 2026-09-07 pure-blur decision and its no-tint exclusion, the no-displayed-number and ten-stop clauses, and the platform-blur fallback clauses. Previous boundary: the Drawer background as one frosted-glass switch with a ten-level blur-intensity slider under baseline `7af7adba15b018886dd613c587dfedd9299136b4`. New boundary: the Drawer-background rework above; the baseline moves to `32d7d25a0fed9e1784518c85796b5f38d18960fc`, the commit integrating this contract revision. Affected obligations: the Drawer-background included work, observable outcome, acceptance criteria, validation scenarios, and the recorded baseline; the iteration identifier and file slug remain unchanged.
- 2026-09-09: Author-directed material amendment. After comparing the contracted soft dark ring with a `1dp` border on the primary device, the author ruled that the shared style settings panel edge treatment is one `1dp` border in `#6E6E73` drawn along the panel corner radius, superseding the 2026-09-07 accepted shadow-ring treatment and its 2026-09-09 calibration values. Previous boundary: the soft dark ring behind the panel surface under baseline `32d7d25a0fed9e1784518c85796b5f38d18960fc`. New boundary: the edge border defined by the style settings panel presentation specification; the baseline moves to `4f596a5fe9de30785dd0e06c2f66a727e481bb9a`, the commit integrating that presentation revision. Affected obligations: the edge-treatment included work, observable outcome, acceptance criteria, validation scenarios, and the recorded baseline; the iteration identifier and file slug remain unchanged.

- 2026-09-09: Author-directed amendment. The author directed one optimization within the style settings panel scope: the Drawer display-settings panel now opens and closes with one height property animation (`0` to its content height, retracting back) on the shared short-duration property-animation token, matching the Home style panel. Previous boundary: the Drawer panel appeared and closed without an animation. New boundary: the Drawer presentation specification records the animated entry and exit. Affected obligations: one included-work line and one validation scenario are added; the iteration identifier and file slug remain unchanged.

## Acceptance criteria

- The dock row sits at the top of safe content in both panel states with an unchanged toggle hot zone; toggling never scrolls the main list; the dock height and affordance target match the presentation specification.
- Expanding and collapsing the panel animates the contracted slot swap without covering the list, commits state immediately, resolves interruptions to the final state, retargets re-toggles, and skips to the final state under reduced motion.
- The background group presents the percentage readout and slider with the contracted defaults; the `0`–`100` whole-percentage model, progress colors, readout geometry, and accessibility announcements match the contract, and the default opacity is `50`.
- `0` presents the wallpaper clear and `100` presents the solid `darkSurfaceBaseColor` surface; text and artwork shadows remain in effect at every percentage, and no percentage renders blur.
- A slider drag previews continuously without persisting and commits exactly one display-setting save at pointer release; an interruption before release reverts the background and readout immediately to the persisted state with no save; the single-unresolved-save rule holds.
- Existing configurations and absent fields migrate per the contract default rule without mapping the prior mode choice; the background-opacity percentage persists, survives recreation and restart, and appears in backup contents.
- The Home and Drawer style settings panels render the contracted edge border with the accepted values.
- Favorite multi-selection reuses the ordinary grid without layout change; available unselected rows show no indicator; selected cells show the scaled outline treatment with the top-left order badge; deselecting closes numbering gaps; disabled already-favorited rows show no badge, outline, or scaling; ordinary selection toggling, Confirm, save, and accessibility semantics are unchanged.
- All new or changed user-visible strings exist in English and Simplified Chinese with complete name parity.
- No regression of ordinary Home editing, Drawer launching, search, or the remaining display-setting groups.

## Validation requirements

Recommended scenarios unless explicitly promoted:

- Device journeys on the primary physical device: edit-mode dock placement, panel expand/collapse animation, interruption and re-toggle during the animation, and reduced-motion behavior.
- Background journeys: default state, boundary percentages `0` and `100`, representative intermediate percentages, persistence across recreation and restart, and migration from a prior enum-valued configuration.
- Slider save behavior: a continuous drag ending in release; interrupted drags (pointer cancellation and panel closure) reverting immediately with no save; one save per gesture; no second change during the unresolved save.
- Border rendering on both panels against the accepted values.
- The Drawer panel open and close animation against the shared token duration.
- Multi-selection presentation journeys: select and deselect across the three arrangements with renumbering, badge presentation and its non-interactivity, disabled already-favorited rows, and parity of multi-selection row geometry with ordinary Content.
- Backup round trip including the background-opacity percentage, coordinated with the Iteration 29 implementation state.
- String parity between English and Simplified Chinese resources.
- Regression of ordinary Home editing, Drawer search, launching, and the other display-setting groups.

## Related decisions and technical assessments

- [Design foundations](../../product/design-foundations.md) — the shared placement and toggle hot-zone principles this iteration implements.
- [Style settings panel presentation](../../product/presentation/style-settings-panel.md) — the readout-and-slider row and edge border values.
- [Product decision and scope-change governance](../../product-decisions.md) — the author's 2026-09-08 contract revisions and the value-only calibration direction.
