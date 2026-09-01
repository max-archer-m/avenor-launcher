# Iteration 23: Module Style and Ordering

> Applicable version: [Avenor Launcher 1.4.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Let the author create both module types through main-list add-favorite entries, select any complete module, change a vertical module's presentation, and reorder peer modules through the expanded inline style settings panel.

## Product and version references

- Product-contract baseline: `7cae837dafb188896dd24bd43aae58022c81fe11`
- [Home behavior](../../product/surfaces/home.md#home-edit-mode)
- [Home add-favorite entries](../../product/surfaces/home.md#add-favorite-entries)
- [Home presentation](../../product/presentation/home.md#edit-dock-and-style-settings-panel)
- [Shared style settings panel presentation](../../product/presentation/style-settings-panel.md)
- [Add-favorite entry presentation](../../product/presentation/home.md#add-favorite-entries)
- [Drawer favorite multi-selection](../../product/surfaces/drawer.md#favorite-multi-selection-mode)
- [Navigation interruption behavior](../../product/navigation.md#home-edit-mode-interruption)
- Applicable version contract: [delivery.md](delivery.md)

## Observable outcome

The author enters Home edit mode, creates a vertical module or ribbon from the main-list add-favorite entries, adds applications to an existing module from its trailing add-favorite entry, selects any complete module, changes valid vertical-module size/name-placement/items-per-row combinations with immediate durable preview, and moves complete modules to insertion boundaries while retaining selection and order.

## Included work

- The Home editing surface and complete-module selection flow defined by the linked Home contracts.
- Contracted add-favorite entries in the favorite main list and inside every module, their position-resolved destinations, availability in both panel states, disabled behavior during an unresolved exclusive save, and preserved list scroll position.
- Removal of both add actions and the module-type row from the style settings panel, including the contracted selection prompt for no selection and fixed-style message for a selected ribbon.
- Home integration of the shared style settings panel presentation, with the shared surface and controls unchanged and only the linked Home-specific inline placement, height, scrolling, and edit-dock relationship applied.
- Creation and presentation of both contracted module types through those entries.
- Contracted vertical-module style editing, immediate durable preview, save serialization, and failure recovery.
- Contracted whole-module ordering, movement feedback, scrolling, interruption, persistence, and restoration.
- Module lifecycle and inventory reconciliation needed to keep selection, order, and persisted state valid.
- Localizable `Add favorite list` / `新增收藏列表`, `Add favorite ribbon` / `新增收藏织带`, `Add favorite` / `新增收藏`, `Select a favorite list to edit its style` / `选择收藏列表以编辑样式`, and `Favorite ribbons use a fixed style` / `收藏织带使用固定样式` string resources.

## Excluded work

- Application-level removal, Undo, or application drag movement, including the drop-to-create destination on a main-list add-favorite entry, which belongs to Iteration 24.
- Module naming, complete-module delete action, or ribbon style controls.
- Drawer search and display settings.

## Technical change areas

Home edit state, add-favorite entry rendering and destination capture, inline panel, module style persistence, ribbon rendering, module selection and movement coordinator, geometry, scrolling, interruption, resources, and tests.

## Dependencies and sequence

Depends on Iteration 22 module identity, persistence, normal rendering, and add destination. Iteration 24 depends on both module types, their stable geometries, and the add-favorite entries that its drop-to-create destination targets.

## Migration and compatibility impact

Adds the durable module type, order, and style state selected by the linked contracts. Adoption of those fields must preserve unaffected application identity and order without redefining product defaults in this iteration record. Add-favorite entries are not persisted and add no durable field.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, dependency, or license impact is selected. New display fields remain local and excluded from Android backup/transfer.

## Risks and unresolved decisions

Panel layout may destabilize scroll position; selection and movement identity may diverge; style save failure may leave a session-only preview; unlimited modules may expose performance pressure. A trailing add-favorite entry inside every module increases edit-mode density and shares its module's surface with remove and movement targets, so target ownership must remain unambiguous. The panel contains only an informational row for no selection or a selected ribbon; whether the panel should later become a pure mode affordance is deferred and not decided here. Consequential state-ownership changes require author review.

## Acceptance criteria

- Panel, selection, position, and information-region behavior match the linked Home and navigation contracts across expansion, collapse, and interruption.
- Add-favorite entries appear only in edit mode, scroll with the main list, remain available in both panel states for the two main-list entries, and are blocked by the selection layer for a module's trailing entry.
- Entry position alone resolves the captured destination, so adding to an existing module and creating a new module produce distinct results without relying on panel state or prior selection.
- Both contracted module types can be created and retained without an invalid durable state. Drawer selection alone never creates a module; only a successful Confirm retaining at least one valid identity atomically creates a provisional module with all retained identities in displayed selection order. Every discard, empty revalidation result, or save failure leaves module order and content unchanged.
- Every add-favorite entry is excluded from insertion-boundary resolution, and the last module's end boundary and every module's own end boundary remain reachable.
- Every contracted vertical style and boundary produces the specified preview, atomic saved result, disabled state, or rollback outcome, and every add-favorite entry is disabled while an exclusive save is unresolved.
- The style settings panel exposes no add action in any selection state.
- The panel exposes no module-type row: no selection shows the contracted selection prompt, a selected vertical module starts directly with applicable controls, and a selected ribbon shows only the contracted fixed-style message.
- Home and Drawer use the same contracted style settings panel surface and applicable controls; Home differs only through its documented non-modal inline host rules and Drawer differs only through its documented modal host rules.
- Ribbon presentation and available controls remain within the linked product boundary.
- Whole-module ordering, cancellation, failure recovery, and external interruption produce the contracted durable order without unpublished state.

## Validation requirements

Recommended evidence covers empty/non-empty Home; the exact no-selection and selected-ribbon informational states without a module-type row; shared panel `12dp` corners, surface, and control parity with Drawer; Home-specific inline margins, shadow, dock attachment, height, and scrolling; both main-list entries and a trailing entry in each module type; entry availability in both panel states; destination resolution by position; discarded provisional destination; entry exclusion from insertion boundaries; every size/placement/count boundary; save success/failure with entries disabled; many modules; selection retention; first/middle/last reorder; auto-scroll; Back/Home/external interruption; process recreation; inventory removal; font scaling; and normal-Home regression.

## Related decisions and technical assessments

No new decision is selected. Create an ADR only if implementation establishes a consequential durable state or gesture architecture.
