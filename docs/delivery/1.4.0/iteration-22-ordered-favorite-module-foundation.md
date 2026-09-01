# Iteration 22: Ordered Favorite Module Foundation

> Applicable version: [Avenor Launcher 1.4.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Establish a usable Home foundation in which the former `1.3.0` favorite composition is replaced once by one persisted ordered module sequence and the author can create and use an initial vertical favorite module.

## Product and version references

- Product-contract baseline: `48d5bd470c84d222b6e89e128f438da1f25e595b`
- [Product foundation](../../requirements/product-foundation.md)
- [Home behavior](../../product/surfaces/home.md) and [Home presentation](../../product/presentation/home.md)
- [Drawer favorite multi-selection](../../product/surfaces/drawer.md#favorite-multi-selection-mode)
- Applicable version contract: [delivery.md](delivery.md)

## Observable outcome

After clean installation or one-time adoption, Home presents one vertically scrolling full-width module list with no former list/bar containers. From empty Home edit mode, the author can create a vertical module, add ordered applications through Drawer, return to Home, scroll the module list, and launch the intended identities.

## Included work

- The one-time Home-model adoption boundary defined by the linked Home contract, including preservation of unrelated readable configuration.
- The persisted ordered module foundation, identity and inventory integrity, lifecycle states, and failure-safe behavior required by the selected slice.
- The first usable vertical-module presentation and its empty-Home creation flow through destination-bound Drawer multi-selection.
- Compatibility of established Home information, launch, action-sheet, restoration, and Home–Drawer behavior with the new foundation.

## Excluded work

- Horizontal ribbon creation, module style changes, whole-module selection or ordering, and application drag/remove editing.
- Drawer search and display settings.

## Technical change areas

Favorite persistence and adoption, module/identity model, Home normal composition, empty/edit state, Drawer destination capture, inventory reconciliation, backup exclusion, resources, and focused tests.

## Dependencies and sequence

Depends on the completed `1.3.0` aggregate and accepted current product baseline. Iteration 23 depends on stable module identity, persistence, normal rendering, and add flow from this iteration.

## Migration and compatibility impact

This iteration selects the linked Home contract's one-time adoption behavior rather than defining a second migration rule. Its implementation must preserve the unrelated configuration and unreadable-data protections owned by the product and persistence contracts.

## Security, privacy, permission, and licensing impact

No new permission, network access, external recipient, dependency, or license impact is selected. Privacy text and backup exclusion must remain accurate for the new stored module fields.

## Risks and unresolved decisions

Reset scope can destroy unrelated state if ownership is unclear. Provisional creation can persist an invalid empty module. A consequential persistence-format or state-ownership decision requires author review and an ADR when applicable.

## Acceptance criteria

- Adoption, preservation, and unreadable-data outcomes match the linked Home and product-foundation contracts.
- Empty, loading, error, creation, cancellation, and save-failure scenarios remain distinguishable and produce the contracted durable result.
- The first vertical module can be created through the contracted Drawer destination flow, restored, reconciled with inventory, and used to launch the intended identities.
- Main-list scrolling and Home–Drawer transfer remain usable without accidental activation or an unresolved material contract mismatch.

## Validation requirements

Recommended evidence covers clean install; readable and unreadable `1.3.0` adoption; preservation of unrelated settings; empty/loading/error states; create/cancel/failure paths; primary, clone, and work-profile identity; recreation; inventory changes; backup exclusion; and Home–Drawer regression. Actual results belong in `delivery.md`.

## Related decisions and technical assessments

- [ADR-0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)
- [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)
- Create or supersede a decision only if implementation establishes a consequential new persistence boundary.
