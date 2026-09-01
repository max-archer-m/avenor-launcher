# Iteration 24: Application Editing and Cross-Module Movement

> Applicable version: [Avenor Launcher 1.4.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Complete the Home application-editing loop with explicit removal and latest-removal Undo plus insertion-only movement within and across every module type.

## Product and version references

- Product-contract baseline: `48d5bd470c84d222b6e89e128f438da1f25e595b`
- [Home application editing](../../product/surfaces/home.md#adding-removing-and-moving-applications)
- [Home edge auto-scroll](../../product/surfaces/home.md#edge-auto-scroll-during-movement)
- [Home presentation](../../product/presentation/home.md#drag-and-overflow-feedback)
- [Navigation](../../product/navigation.md)
- Applicable version contract: [delivery.md](delivery.md)

## Observable outcome

In collapsed-dock edit mode, the author can remove and undo the latest eligible favorite removal, reorder applications within a module, move them between vertical modules and ribbons at explicit insertion boundaries, and retain one valid saved result after interruption or failure.

## Included work

- The contracted application-level edit surface and separation from ordinary Home actions.
- The contracted removal and latest-removal Undo lifecycle, including durable module and capacity consequences.
- Application movement within and across every selected module type, with the linked insertion, feedback, persistence, and empty-source behavior.
- Applicable scrolling, gesture ownership, interruption, inventory reconciliation, and failure recovery across the movement journey.
- Home application-action-sheet entry points aligned with the same contracted edit lifecycle.

## Excluded work

- Whole-module style or ordering, which belongs to Iteration 23.
- General multi-step undo history, Drawer drag destinations, or exchange-based movement.

## Technical change areas

Favorite aggregate transactions, edit coordinator, drag geometry, two-axis auto-scroll, removal/Undo state, action-sheet handoff, inventory reconciliation, interruption, persistence rollback, resources, and tests.

## Dependencies and sequence

Depends on accepted Iterations 22–23 so module identities, types, orders, geometries, selection states, and persistence are stable. It completes the selected Home editing scope before version closure.

## Migration and compatibility impact

No additional migration is selected. Mutations must preserve the identity, unaffected-order, inventory, and recovery invariants owned by the linked Home and persistence contracts.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, dependency, or license impact is selected. Undo remains transient local state rather than durable history.

## Risks and unresolved decisions

Gesture recognition can suppress scrolling if ownership is taken early. Cross-module save failure can conflict with newer inventory facts. Undo can exceed natural capacity or restore a deleted module incorrectly. Consequential coordinator changes require author review.

## Acceptance criteria

- Removal, Undo eligibility, replacement, invalidation, and restoration match the linked Home contract without accidental ordinary action.
- Same-module and cross-module movement succeeds at every contracted destination boundary and preserves identity and unaffected order.
- Recognition, feedback, eligible viewport scrolling, invalid release, cancellation, navigation, interruption, and additional-pointer scenarios produce the contracted result.
- Save and inventory failures restore a valid reliable state without duplication, stale resurrection, or overwrite of newer inventory facts.

## Validation requirements

Recommended evidence covers removal/Undo lifecycle; all module-type source/target combinations; every boundary; non-overflowing/overflowing axes; pre-recognition scroll; auto-scroll; no-change/invalid release; empty-source deletion; save failure; inventory change; Back/Home/external interruption; multi-pointer input; recreation; action-sheet handoff; and launch/navigation regression.

## Related decisions and technical assessments

- [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)
- [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)
- No new decision is selected unless implementation establishes a consequential coordinator or transaction boundary.
