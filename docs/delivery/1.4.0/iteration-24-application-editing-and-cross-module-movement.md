# Iteration 24: Application Editing and Cross-Module Movement

> Applicable version: [Avenor Launcher 1.4.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Complete the Home application-editing loop with explicit removal and latest-removal Undo, insertion-only movement within and across every module type, and the drop-to-create destination on a main-list add-favorite entry.

## Product and version references

- Product-contract baseline: `78d2aab18066c2d9b57b56581e0ab8c17402d104`
- [Home application editing](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/surfaces/home.md#adding-removing-and-moving-applications)
- [Home add-favorite entries](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/surfaces/home.md#add-favorite-entries)
- [Home edge auto-scroll](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/surfaces/home.md#edge-auto-scroll-during-movement)
- [Home drag presentation](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/presentation/home.md#drag-and-overflow-feedback)
- [Home content transitions](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/presentation/home.md#content-transitions)
- [Home edit-region background](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/presentation/home.md#surface-and-basic-information)
- [Add-favorite entry presentation](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/presentation/home.md#add-favorite-entries)
- [Shared short-duration animation token](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/presentation/style-settings-panel.md#shared-selectors-and-controls)
- [Navigation](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/navigation.md)
- Applicable version contract: [delivery.md](delivery.md)

### Authorized amendment: 2026-09-03

- Author decision and reason: The project author accepted the Home movement and presentation refinements and authorized this contract amendment to resolve source-space obstruction, unstable ribbon insertion feedback, and underspecified content transitions.
- Previous baseline: `7cae837dafb188896dd24bd43aae58022c81fe11`. It retained an application-source placeholder, used item-edge ribbon insertion feedback, and left add-entry rendering to Iteration 23 without the newly selected transition rules.
- New baseline: `78d2aab18066c2d9b57b56581e0ab8c17402d104`, with the affected behavior, presentation, and shared references pinned above. The amendment selects placeholder-free application movement, ribbon gap-centered feedback using current visible geometry, content transitions and preview handoff, and the revised add-entry surface treatments.
- Identity and scope: The application-editing loop and primary outcome remain unchanged; this is a bounded amendment, not a replacement iteration. Existing whole-module style and ordering behavior remains the Iteration 23 baseline. Only the linked shared content-transition treatment and the explicitly included surface refinements extend that existing presentation; Iteration 23 history is unchanged.
- Acceptance and validation impact: Apply the additional geometry, temporary empty-source, transition, handoff, surface, and recovery criteria and recommended scenarios below. Earlier evidence does not establish these new outcomes. The implementation line must contain the new product baseline before dependent implementation continues.

## Observable outcome

In collapsed-dock edit mode, the author can remove and undo the latest eligible favorite removal, reorder applications within a module, move them between vertical modules and ribbons at explicit insertion boundaries, drop one onto a main-list add-favorite entry to create that module type with the application as its first favorite, and retain one valid saved result after interruption or failure.

## Included work

- The contracted application-level edit surface and separation from ordinary Home actions.
- The contracted removal and latest-removal Undo lifecycle, including durable module and capacity consequences.
- Application movement within and across every selected module type, with the linked insertion, feedback, persistence, and empty-source behavior.
- The contracted drop-to-create destination on a main-list add-favorite entry: its hover outline emphasis, exclusion of the insertion line, the single atomic mutation that creates the module, adds the identity, removes it from the source, and deletes an emptied source module, and the invalid-destination result for a trailing module entry.
- Applicable scrolling, gesture ownership, interruption, inventory reconciliation, and failure recovery across the movement journey.
- Home application-action-sheet entry points aligned with the same contracted edit lifecycle.
- Placeholder-free lifting and temporary application-empty source handling, with insertion hit testing and feedback aligned to current visible geometry.
- The linked Home content transitions for addition, removal, Undo, module creation or automatic deletion, and successful application or module repositioning, including application-preview handoff and animation-independent persistence.
- The revised vertical trailing add-entry treatment and main-list add-entry background and border treatment, including the shared edit-region background they reuse. Ribbon trailing-entry treatment, add-entry geometry, tap destinations, and labels remain unchanged.

## Excluded work

- Changes to whole-module style controls or ordering behavior delivered by Iteration 23; the shared content-transition refinements above remain included.
- Changes to add-favorite entry geometry, tap-to-multi-selection destinations, and labels delivered by Iteration 23; the explicitly selected surface and transition refinements above remain included.
- General multi-step undo history, Drawer drag destinations, or exchange-based movement.

## Technical change areas

Favorite aggregate transactions, edit coordinator, drag geometry, drop-destination resolution, two-axis auto-scroll, removal/Undo state, content transitions and preview handoff, add-entry surfaces, action-sheet handoff, inventory reconciliation, interruption, persistence rollback, resources, and tests.

## Dependencies and sequence

Depends on accepted Iterations 22–23 so module identities, types, orders, geometries, selection states, persistence, and add-favorite entries are stable. Dependent implementation must include the amended product baseline above; updating this contract does not integrate that baseline into another implementation line. It completes the selected Home editing scope before version closure.

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
- Lifting closes the source gap without reserving an application slot. A temporarily application-empty source retains only its contracted add entry and is not an insertion destination; cancellation restores it and successful departure deletes it atomically.
- Ribbon insertion feedback remains attached to the same semantic gap when equivalent hit regions change. During reflow and scrolling, hit testing and line drawing agree on current visible geometry and respect viewport clipping.
- Add-entry surfaces match the linked presentation without duplicate background layers; vertical trailing entries remain icon-and-label targets, ribbon trailing entries keep their established treatment, and main-list creation entries retain distinct drop emphasis.
- Add, removal, Undo, and module lifecycle changes use the linked transition level without duplicate parent/child fades. Successful repositioning does not flash the complete module; ordinary data updates and returning or scrolling into view do not animate as additions.
- Application lift, pending save, success, and recovery follow the single-preview handoff contract, including the drag-created-module exception. Animations neither delay saves or recovery nor extend Undo, and disabling animations preserves the same state and available actions.

## Validation requirements

Recommended evidence covers removal/Undo lifecycle; all module-type source/target combinations; every boundary; drop-to-create for both module types including from a single-application source module; hover emphasis without an insertion line; invalid trailing-entry release; non-overflowing/overflowing axes; pre-recognition scroll; auto-scroll; no-change/invalid release; empty-source deletion; save failure; inventory change; Back/Home/external interruption; multi-pointer input; recreation; action-sheet handoff; and launch/navigation regression.

Additional recommended amendment evidence covers:

- Unequal-width ribbon entries, equivalent adjacent hit regions, first/last application boundaries, viewport clipping, and insertion during animated source-gap closure or auto-scroll.
- Lifting the only application from vertical and ribbon sources, invalid return to the temporary empty source, cancellation, successful existing/new-module departure, and save failure.
- Vertical and ribbon trailing add entries and both main-list creation entries in applicable edit-panel states, including ordinary, disabled, pressed, and drop-hover presentation.
- Application addition/removal/Undo, final-application removal and module restoration, drag-created modules, successful application/module repositioning, and an allowed new action while a previous transition remains visible.
- Release before gap closure finishes, unresolved save, successful handoff, cancellation/failure recovery, external interruption, and system animations disabled; check for duplicate images, stale candidates, unintended input locks, and altered Undo timing.
- Icon/name/availability updates, first display, ordinary return, and scrolling newly visible content, without replaying enter animations for unchanged content.

These scenarios remain recommendations under the iteration-format policy; this amendment adds no mandatory build or device gate and records no performed result.

## Related decisions and technical assessments

- [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)
- [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)
- Assess after implementation whether the drop-to-create module-creation path establishes a consequential durable-state or transaction boundary requiring an ADR. Record the assessment result; do not leave it implicit.
- No new decision is selected unless implementation establishes a consequential coordinator or transaction boundary.
