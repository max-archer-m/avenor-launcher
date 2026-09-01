# Iteration 23: Module Style and Ordering

> Applicable version: [Avenor Launcher 1.4.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Let the author create both module types, select any complete module, change a vertical module's presentation, and reorder peer modules through the expanded inline style panel.

## Product and version references

- Product-contract baseline: `48d5bd470c84d222b6e89e128f438da1f25e595b`
- [Home behavior](../../product/surfaces/home.md#home-edit-mode)
- [Home presentation](../../product/presentation/home.md#edit-dock-and-style-panel)
- [Navigation interruption behavior](../../product/navigation.md#home-edit-mode-interruption)
- Applicable version contract: [delivery.md](delivery.md)

## Observable outcome

The author expands the Home style panel, creates or selects a vertical module or ribbon, changes valid vertical-module size/name-placement/items-per-row combinations with immediate durable preview, and moves complete modules to insertion boundaries while retaining selection and order.

## Included work

- The expanded Home editing surface and complete-module selection flow defined by the linked Home contracts.
- Creation and presentation of both contracted module types.
- Contracted vertical-module style editing, immediate durable preview, save serialization, and failure recovery.
- Contracted whole-module ordering, movement feedback, scrolling, interruption, persistence, and restoration.
- Module lifecycle and inventory reconciliation needed to keep selection, order, and persisted state valid.

## Excluded work

- Application-level removal, Undo, or application drag movement.
- Module naming, complete-module delete action, or ribbon style controls.
- Drawer search and display settings.

## Technical change areas

Home edit state, inline panel, module style persistence, ribbon rendering, module selection and movement coordinator, geometry, scrolling, interruption, resources, and tests.

## Dependencies and sequence

Depends on Iteration 22 module identity, persistence, normal rendering, and add destination. Iteration 24 depends on both module types and their stable geometries.

## Migration and compatibility impact

Adds the durable module type, order, and style state selected by the linked contracts. Adoption of those fields must preserve unaffected application identity and order without redefining product defaults in this iteration record.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, dependency, or license impact is selected. New display fields remain local and excluded from Android backup/transfer.

## Risks and unresolved decisions

Panel layout may destabilize scroll position; selection and movement identity may diverge; style save failure may leave a session-only preview; unlimited modules may expose performance pressure. Consequential state-ownership changes require author review.

## Acceptance criteria

- Panel, selection, position, and information-region behavior match the linked Home and navigation contracts across expansion, collapse, and interruption.
- Both contracted module types can be created and retained without an invalid durable state.
- Every contracted vertical style and boundary produces the specified preview, atomic saved result, disabled state, or rollback outcome.
- Ribbon presentation and available controls remain within the linked product boundary.
- Whole-module ordering, cancellation, failure recovery, and external interruption produce the contracted durable order without unpublished state.

## Validation requirements

Recommended evidence covers empty/non-empty panel states; both module types; every size/placement/count boundary; save success/failure; many modules; selection retention; first/middle/last reorder; auto-scroll; Back/Home/external interruption; process recreation; inventory removal; font scaling; and normal-Home regression.

## Related decisions and technical assessments

No new decision is selected. Create an ADR only if implementation establishes a consequential durable state or gesture architecture.
