# Iteration 31: Edit Dock, Drawer Background, and Panel Shadow Rework

> Applicable version contract: [1.6.0 delivery](delivery.md). This contract defines the authorized delivery boundary only. It does not own execution state, evidence, commits, or results, and it does not by itself authorize production implementation, a commit, a push, a tag, or any release action.

## Objective

Deliver three author-accepted reworks exactly as defined by the accepted product contracts: the Home edit dock moved to the top of safe content with the purely presentational panel slot-swap animation, the Drawer background reworked into one frosted-glass switch with a ten-level blur-intensity slider and no tint or opaque fallback, and the shared style settings panel edge shadow.

## Product and version references

- Product-contract baseline: `5997845de3c099f20b5f1d6184538a1ee8f24936` — the commit that integrates the accepted edit-dock, Drawer-background, and panel-shadow contract revisions.
- Applicable product documents:
  - [Home behavior](../../product/surfaces/home.md), edit mode, collapsed dock, and expanded panel sections
  - [Home presentation](../../product/presentation/home.md), including the panel animation duration and easing values
  - [Drawer behavior](../../product/surfaces/drawer.md), display settings section
  - [Drawer presentation](../../product/presentation/drawer.md)
  - [Style settings panel presentation](../../product/presentation/style-settings-panel.md), including the switch-and-slider row and edge shadow values
  - [Design foundations](../../product/design-foundations.md), shared placement and toggle hot-zone principles
- Applicable version contract: `delivery.md`

## Observable outcome

On the primary device, Home edit mode shows the fixed dock row at the top of safe content in both panel states; expanding and collapsing the style settings panel animates the information region and panel as one slot swap without covering the list. The Drawer display-settings background group offers one switch and one ten-level slider with the contracted defaults, preview, save, and fallback behavior. The Home and Drawer style settings panels both render the contracted soft edge shadow ring.

## Included work

- The fixed top edit dock row above the basic-information region and main list, with the dock and its toggle affordance at one unchanged position and interaction target across collapsed and expanded states, changing only directional artwork and instruction text, and toggling that never scrolls the main list or changes its logical scroll position.
- The `48dp` dock height and `48dp x 48dp` affordance target, removing the prior dock-specific height exception.
- The animated panel slot swap: expanding collapses the information region to zero while the panel grows to its content height and the main list absorbs the difference continuously; the panel stays non-covering, non-modal, and scrim-free, with the purely presentational semantics of immediate state commitment, interruption resolution to the final state, re-toggle retargeting, reduced-motion skipping, and current-bounds hit testing during the animation.
- The Drawer background group as one on-off switch plus one blur-intensity slider: switch-off presents Transparent; switch-on presents the frosted-glass blur at the selected level; a fresh configuration defaults to on at level `5`.
- The ten-level slider mapping, where level `n` uses a `15dp x n` background-blur radius up to the `150dp` maximum, with ten-stop snapping, no displayed level number, accessibility level announcements, and the shared disabled presentation while the switch is off.
- Removal of the translucent tint layer and the more-opaque fallback: with platform blur unavailable or disabled, the frosted-glass mode presents the Transparent presentation while retaining the switch and intensity values, and the presentation-defined text and artwork shadows remain in effect in both modes.
- The slider drag behavior: continuous blur preview with the whole gesture coalesced into one display-setting save at pointer release or drag pause, under the unchanged single-unresolved-save rule.
- Persistence of the new background state with versioned migration: existing configurations and absent fields resolve per the contract default rule, and the blur-intensity level enters the backup file contents and the local data enumeration.
- The shared switch-and-slider row geometry with the primaryTextColor accent and the contracted control gap.
- The style settings panel edge shadow as the contracted soft dark ring on the Home and Drawer panels, realized as the contracted layered or equivalent gradient treatment with per-layer radii, using the author-accepted delivery values.
- Localized English and Simplified Chinese strings for any new or changed user-visible text, including the background switch and slider labels.
- Focused test sources covering dock placement and hot-zone stability, animation interruption and retargeting, slider mapping and save coalescing, fallback presentation, migration of prior background values, and shadow realization.

## Excluded work

- Wallpaper sampling, position- or pointer-dependent blur, user-authored visual values, or a translucent tint layer in any mode.
- Any change to the shadow treatment's structure or rules: device comparison may calibrate values only, never the ring structure, step model, or mapping relationships.
- Behavior changes outside the Home edit-mode layout, the Drawer display-settings background group, and the style settings panel presentation.
- The Iteration 29 backup and restore scope and the Iteration 30 drag-to-favorite scope, beyond the shared display-settings persistence that carries the intensity level.
- The `1.6.0` version identifier update (`versionName`/`versionCode`), which remains a version-level closure concern unless a later authorized amendment assigns it here.
- The third favorite-module type, a vertical favorites list, and third-party License presentation.

## Technical change areas

- Home edit-mode layout: dock relocation to the top of safe content, the animated slot swap, viewport height computation across both panel states, and logical scroll-position preservation.
- Drawer background rendering: platform cross-window blur with the parameterized radius, availability detection, and the Transparent-presentation fallback; removal of the tint layer and opaque fallback.
- Display-settings store: the background field reworked into the switch and intensity level with versioned migration, and the slider-gesture save-coalescing path.
- Style settings panel UI: the switch-and-slider row and the edge shadow drawable or equivalent treatment shared by Home and Drawer panels.
- Backup and restore serialization: the blur-intensity level within the display-settings section.
- Resources: new or changed English and Simplified Chinese strings with complete name parity.
- Tests: focused local and instrumentation sources for the behaviors above.

## Dependencies and sequence

This iteration depends on the three accepted contract revisions integrated in `5997845de3c099f20b5f1d6184538a1ee8f24936` and the current integrated mainline. Its code areas overlap Iteration 29 in the display-settings store, backup serialization, and style panel, and Iteration 30 in the Home edit-mode layout, so the three iterations must not run concurrently on one line; their relative order is otherwise free, with the implementation line of each later iteration containing the integrated results of the earlier ones it depends on.

## Migration and compatibility impact

- The Drawer background setting changes shape: prior Transparent and Frosted-glass values and absent fields resolve under the contract default rule to the switch on at intensity level `5` where the field is absent; the versioned atomic persistence and its migration approach remain the implementation responsibility within that rule.
- Backups written before this change are interpreted under the directional schema-compatibility rule of the Settings contract; a missing background field resolves to the contracted default.
- No change to favorites persistence, the one-time adoption reset, or the Android cloud-backup boundary.

## Security, privacy, permission, and licensing impact

- No new permission, networking, account, or external service is introduced; blur rendering and shadow drawing are local presentation behavior.
- The blur-intensity level enters the backup file, which remains an unencrypted, user-managed local file within the disclosed Privacy boundary; no Privacy copy change is required. If implementation reveals a material divergence from the Privacy statement, stop and obtain author direction.

## Risks and unresolved decisions

- Platform cross-window blur support and performance vary; the Transparent-presentation fallback and retained setting values must behave exactly as contracted on the primary device, with broader devices remaining recommended validation.
- Migrating prior enum-valued background configurations must not lose unrelated display settings or trigger the failure path.
- The slot-swap animation must keep hit testing, scroll position, and drag-to-favorite destination geometry consistent with the committed state, including during interruptions and retargeting.
- The slider's coalesced save must respect the single-unresolved-save rule and cannot begin a second change before the coalesced save resolves.
- Shadow values are author-accepted delivery values subject to device comparison; per the author's 2026-09-08 direction, device calibration modifies values only and never the treatment's structure, step model, or mapping rules.

## Acceptance criteria

- The dock row sits at the top of safe content in both panel states with an unchanged toggle hot zone; toggling never scrolls the main list; the dock height and affordance target match the presentation specification.
- Expanding and collapsing the panel animates the contracted slot swap without covering the list, commits state immediately, resolves interruptions to the final state, retargets re-toggles, and skips to the final state under reduced motion.
- The background group presents the switch and slider with the contracted defaults; level mapping, snapping, accessibility announcements, and disabled presentation match the contract; switch-off presents Transparent.
- With platform blur unavailable, the frosted-glass mode renders the Transparent presentation while retaining the switch and intensity values; text and artwork shadows remain in effect in both modes.
- A slider drag previews continuously and commits exactly one display-setting save at release or pause; the single-unresolved-save rule holds.
- Existing configurations and absent fields migrate per the contract default rule; the blur-intensity level persists, survives recreation and restart, and appears in backup contents.
- The Home and Drawer style settings panels render the contracted edge shadow with the accepted values.
- All new or changed user-visible strings exist in English and Simplified Chinese with complete name parity.
- No regression of ordinary Home editing, Drawer launching, search, or the remaining display-setting groups.

## Validation requirements

Recommended scenarios unless explicitly promoted:

- Device journeys on the primary physical device: edit-mode dock placement, panel expand/collapse animation, interruption and re-toggle during the animation, and reduced-motion behavior.
- Background journeys: default state, each boundary level, switch-off, fallback with platform blur unavailable, persistence across recreation and restart, and migration from a prior enum-valued configuration.
- Slider save coalescing: continuous drag with release and with pause; one save per gesture; no second change during the unresolved save.
- Shadow rendering on both panels against the accepted values.
- Backup round trip including the blur-intensity level, coordinated with the Iteration 29 implementation state.
- String parity between English and Simplified Chinese resources.
- Regression of ordinary Home editing, Drawer search, launching, and the other display-setting groups.

## Related decisions and technical assessments

- [Design foundations](../../product/design-foundations.md) — the shared placement and toggle hot-zone principles this iteration implements.
- [Style settings panel presentation](../../product/presentation/style-settings-panel.md) — the switch-and-slider row and edge shadow values.
- [Product decision and scope-change governance](../../product-decisions.md) — the author's 2026-09-08 contract revisions and the value-only calibration direction.
