# Iteration 7: Primary-favorite reorder loop

> Applies to [Avenor Launcher 1.1.0](delivery.md). This record plans one product increment and does not authorize implementation or Git/remote actions.

## Status

- Value: `Completed`
- Updated: 2026-08-16
- Basis: The implementation was completed and the project author reported that Iteration 7 acceptance passed.

## Objective

Let the author deliberately reorder two or more primary Home favorites and retain each completed move without weakening the accepted favorite lifecycle. This iteration does not include the companion-favorite model or complete cross-group editing within the authorized `1.1.0` iteration boundary. This statement records the iteration boundary and does not determine the future product status of those capabilities.

## Product and version references

- [1.1.0 delivery](delivery.md)
- [Home edit-mode contract](../../product/surfaces/home.md#edit-mode)
- [Application action sheet](../../product/surfaces/app-action-sheet.md#launcher-actions)
- [Navigation](../../product/navigation.md#home-to-drawer)
- [Design foundations](../../product/design-foundations.md#layout)
- [Home edit-mode wireframe](../../product/wireframes/home-edit-mode.txt)

The selected behavior is interpreted from the product contract and `1.1.0` delivery scope applicable when this iteration is authorized. A later change to the current product contract does not retroactively expand this completed iteration's scope or alter its result.

## Observable outcome

From a primary favorite's Launcher actions, the author enters reorder mode and sees the defined editing surfaces behind the visible Home modules, reorders primary favorites across stored positions with the defined feedback and edge scrolling, exits with Back, and later observes the saved primary order.

## Included work

- An `Edit` Launcher action for two or more primary favorites; it is hidden when fewer than two primary favorites exist.
- Primary-favorite reorder presentation, drag handles, in-group position changes, immediate persistence, haptics, auto-scroll, Back exit, and gesture ownership.
- The primary-favorite module retains the accepted `1.0.0` full available width. No companion-favorite region, right-side placeholder, reserved companion width, or inter-group gap exists in this version subset.
- The same subtle translucent light-gray, small-rounded editing surface behind each visible Home module: currently the basic-information module and favorite-composition module. The surface does not tint or dim their content, does not imply that the basic-information module is editable, and is not drawn for a zero-height conditional module or unallocated transparent Home space.
- Product-defined handling of inventory and persistence changes during reorder.
- Preservation of `1.0.0` favorite data and behavior outside reorder mode.

## Excluded work

- Automatic, usage-based, alphabetical, or recommended ordering.
- Undo, reset, Done button, additional mode banner, or cross-device synchronization.
- Companion-favorite presentation, assignment, persistence, or editing; movement or swapping between favorite groups; and the complete two-group edit capability are not selected for this iteration. This exclusion does not classify their future product status.
- Application shortcuts, Settings, and double-tap lock.

## Technical change areas

Home interaction state, primary-favorite persistence mutation, list gesture arbitration, inventory reconciliation, shared editing-surface color, opacity, and corner-radius tokens, resources, and focused tests. Exact drag primitives, state organization, and visually calibrated token values remain implementation choices unless evidence requires a consequential decision.

## Dependencies and sequence

Uses the accepted `1.0.0` favorite identity, full-width Home presentation, and versioned atomic persistence foundation. It has no dependency on another planned `1.1.0` iteration.

## Migration and compatibility impact

Existing `1.0.0` favorites become primary favorites and must load in their current append order without destructive migration. Each saved primary reorder must remain readable by the current application version. This iteration does not create companion-group data or claim forward compatibility for an unimplemented group model; downgrade support is not promised.

## Security, privacy, permission, and licensing impact

No new permission, network operation, personal-data category, or dependency is required by the product outcome. Any contrary implementation finding must be raised before adoption.

## Risks and unresolved decisions

- Gesture competition among primary-favorite scrolling, reorder auto-scroll, and Home-to-Drawer transition needs device evidence.
- Editing surfaces must remain distinguishable over the transparent Home's underlying system background without tinting content, creating a persistent Home scrim, or suggesting that non-favorite content can be reordered.
- Write failure, inventory change, and profile availability must not silently corrupt or reorder favorites.
- A persistence-format or architecture change beyond the active ADR boundary requires author review.

## Validation plan

Recommended scenarios include zero/one/two/many primary favorites; confirmation that Edit is hidden below two favorites; retained full-width primary layout; correct editing surfaces for every visible module and no surface in unallocated or zero-height space; confirmation that no companion group, placeholder, reserved width, or cross-group target is presented; content readability over representative underlying system backgrounds; ordinary and edge-zone moves; Back exit; reopen/process restart; persistence failure; inventory rename, disable, removal, and load failure during reorder; and confirmation that Drawer transition remains unavailable while dragging. Unless promoted, these are not automatic entry or exit gates.

## Acceptance evidence

- On 2026-08-16, the project author reported that the Iteration 7 changes passed acceptance.
- The accepted implementation covers the Home-only Edit entry for two or more primary favorites, full-width edit presentation, primary-favorite reorder and persistence, Back exit, and edit-mode gesture ownership.
- The author did not provide the build identity, device identity, detailed scenario log, or separate automated-command results in this acceptance report; those details remain `Unknown` and no additional result is inferred.

## Related decisions, commits, and tags

- [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)
- [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)
- The implementation and completion-state change are included in the corresponding Git commit; no tag was created.

## Final result

Iteration 7 is complete. The accepted result provides the selected `1.1.0` primary-favorite reorder loop without adding companion-favorite or cross-group behavior.

## Remaining issues and handoff

No Iteration 7 delivery issue remains open. Validation details absent from the author report remain `Unknown`; broader companion-favorite and cross-group editing remain outside the selected `1.1.0` scope.
