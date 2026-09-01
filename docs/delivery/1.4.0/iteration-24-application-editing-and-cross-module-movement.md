# Iteration 24: Application Editing and Cross-Module Movement

> Applicable version: [Avenor Launcher 1.4.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Complete the Home application-editing loop with explicit removal and latest-removal Undo, insertion-only movement within and across every module type, and the drop-to-create destination on a main-list add-favorite entry.

## Product and version references

- Product-contract baseline: `7cae837dafb188896dd24bd43aae58022c81fe11`
- [Home application editing](../../product/surfaces/home.md#adding-removing-and-moving-applications)
- [Home add-favorite entries](../../product/surfaces/home.md#add-favorite-entries)
- [Home edge auto-scroll](../../product/surfaces/home.md#edge-auto-scroll-during-movement)
- [Home presentation](../../product/presentation/home.md#drag-and-overflow-feedback)
- [Add-favorite entry presentation](../../product/presentation/home.md#add-favorite-entries)
- [Navigation](../../product/navigation.md)
- Applicable version contract: [delivery.md](delivery.md)

## Observable outcome

In collapsed-dock edit mode, the author can remove and undo the latest eligible favorite removal, reorder applications within a module, move them between vertical modules and ribbons at explicit insertion boundaries, drop one onto a main-list add-favorite entry to create that module type with the application as its first favorite, and retain one valid saved result after interruption or failure.

## Included work

- The contracted application-level edit surface and separation from ordinary Home actions.
- The contracted removal and latest-removal Undo lifecycle, including durable module and capacity consequences.
- Application movement within and across every selected module type, with the linked insertion, feedback, persistence, and empty-source behavior.
- The contracted drop-to-create destination on a main-list add-favorite entry: its hover outline emphasis, exclusion of the insertion line, the single atomic mutation that creates the module, adds the identity, removes it from the source, and deletes an emptied source module, and the invalid-destination result for a trailing module entry.
- Applicable scrolling, gesture ownership, interruption, inventory reconciliation, and failure recovery across the movement journey.
- Home application-action-sheet entry points aligned with the same contracted edit lifecycle.

## Excluded work

- Whole-module style or ordering, which belongs to Iteration 23.
- Add-favorite entry rendering, tap-to-multi-selection destinations, and their string resources, which belong to Iteration 23.
- General multi-step undo history, Drawer drag destinations, or exchange-based movement.

## Technical change areas

Favorite aggregate transactions, edit coordinator, drag geometry, drop-destination resolution, two-axis auto-scroll, removal/Undo state, action-sheet handoff, inventory reconciliation, interruption, persistence rollback, resources, and tests.

## Dependencies and sequence

Depends on accepted Iterations 22–23 so module identities, types, orders, geometries, selection states, persistence, and add-favorite entries are stable. It completes the selected Home editing scope before version closure.

## Migration and compatibility impact

No additional migration is selected. Mutations must preserve the identity, unaffected-order, inventory, and recovery invariants owned by the linked Home and persistence contracts.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, dependency, or license impact is selected. Undo remains transient local state rather than durable history.

## Risks and unresolved decisions

Gesture recognition can suppress scrolling if ownership is taken early. Cross-module save failure can conflict with newer inventory facts. Undo can exceed natural capacity or restore a deleted module incorrectly. The drop-to-create destination introduces a second module-creation path that does not pass through Drawer favorite multi-selection, so durable state ownership must remain single-sourced; whether that warrants an ADR is assessed after this iteration's implementation rather than assumed here. Destination release and boundary insertion must stay visually and semantically distinct. Consequential coordinator changes require author review.

## Acceptance criteria

- Removal, Undo eligibility, replacement, invalidation, and restoration match the linked Home contract without accidental ordinary action.
- Same-module and cross-module movement succeeds at every contracted destination boundary and preserves identity and unaffected order.
- Dropping on a main-list add-favorite entry creates the contracted module type with the moved application as its only favorite in one atomic mutation, deletes an emptied source module in that same mutation, and never duplicates, redirects, or exposes an intermediate persisted state.
- Hovering a valid add-favorite entry shows the contracted outline emphasis and no insertion line, and a trailing module add-favorite entry produces the invalid-destination result.
- Recognition, feedback, eligible viewport scrolling, invalid release, cancellation, navigation, interruption, and additional-pointer scenarios produce the contracted result.
- Save and inventory failures restore a valid reliable state without duplication, stale resurrection, or overwrite of newer inventory facts.

## Validation requirements

Recommended evidence covers removal/Undo lifecycle; all module-type source/target combinations; every boundary; drop-to-create for both module types including from a single-application source module; hover emphasis without an insertion line; invalid trailing-entry release; non-overflowing/overflowing axes; pre-recognition scroll; auto-scroll; no-change/invalid release; empty-source deletion; save failure; inventory change; Back/Home/external interruption; multi-pointer input; recreation; action-sheet handoff; and launch/navigation regression.

## Related decisions and technical assessments

- [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)
- [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)
- Assess after implementation whether the drop-to-create module-creation path establishes a consequential durable-state or transaction boundary requiring an ADR. Record the assessment result; do not leave it implicit.
- No new decision is selected unless implementation establishes a consequential coordinator or transaction boundary.
