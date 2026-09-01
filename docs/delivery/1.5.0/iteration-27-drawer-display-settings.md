# Iteration 27: Drawer Display Settings

> Applicable version: [Avenor Launcher 1.5.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Let the author configure and durably retain Drawer application geometry, section-anchor presentation, and background through one immediate-preview display-settings panel.

## Product and version references

- Product-contract baseline: `48d5bd470c84d222b6e89e128f438da1f25e595b`
- [Drawer display settings](../../product/surfaces/drawer.md#display-settings)
- [Drawer presentation](../../product/presentation/drawer.md)
- [Product data boundary](../../requirements/product-foundation.md#local-data-boundary)
- Applicable version contract: [delivery.md](delivery.md)

## Observable outcome

From ordinary Drawer, the author opens the modal display-settings panel, changes each contracted geometry, anchor, and background setting, sees each valid candidate immediately, and observes the last successfully saved complete state after dismissal, Drawer return, recreation, and restart.

## Included work

- The actionable ordinary-Drawer entry and complete modal settings surface defined by the linked Drawer contracts.
- Every contracted application-geometry, section-anchor, and background setting, including its presentation, accessibility, and valid-boundary behavior.
- Contracted preview, complete-state persistence, serialization, dismissal, rollback, position preservation, lifecycle restoration, and default adoption.
- Platform rendering capability and fallback handling required by the selected background behavior.
- Consistent application of the durable setting state across ordinary content, search, multi-selection, index, and Settings navigation.

## Excluded work

- Theme customization outside Drawer, user-authored colors, automatic adaptation, or wallpaper sampling.
- New application ordering, search ranking, or Home module-style changes.

## Technical change areas

Drawer display-setting persistence, panel state, item layout, anchor projection/pinning, blur capability and fallback, shadows, system bars, list-position mapping, resources, accessibility, and tests.

## Dependencies and sequence

Depends on Iteration 26 ordinary top app bar and Drawer mode boundaries. It must remain compatible with favorite multi-selection and the Home vertical-module style vocabulary without making their settings shared state.

## Migration and compatibility impact

Existing installations adopt the linked Drawer contract's missing-field and application-data-clear behavior. Drawer settings remain independent from Home module styles, and this record does not redefine their defaults.

## Security, privacy, permission, and licensing impact

Settings are local and excluded from Android cloud/device-transfer backup. No network, wallpaper sampling, analytics, permission, dependency, or license impact is selected. Platform blur use must remain within existing rendering capabilities.

## Risks and unresolved decisions

Immediate saves can race with selector animations or dismissal. Geometry changes can lose the top visible identity. Blur behavior varies by device and power/platform state. The least-opaque contracted background may reduce readability if its required contrast treatment is incomplete.

## Acceptance criteria

- Every contracted setting and boundary produces the linked preview, accessibility, serialized-save, disabled-state, dismissal, success, or rollback result.
- Saved and missing-field states restore correctly across the contracted Drawer, recreation, restart, and application-data-clear scenarios without a transient contradictory presentation.
- Geometry changes, section/index behavior, Settings navigation, and visible-position preservation match the linked behavior and presentation contracts.
- Each contracted background mode and platform-capability state produces the specified appearance or fallback without changing the selected value unexpectedly.
- Search and multi-selection reuse the durable presentation without changing their contracted order or selection semantics.

## Validation requirements

Recommended evidence covers every contracted setting combination and boundary; save success/failure/dismissal; missing-field adoption; recreation/restart/data clear; search/multi-selection; anchor pinning; Settings section; background contrast; rendering capability changes; system bars; locale, font scale, accessibility; multiple devices; and performance observations.

## Related decisions and technical assessments

No new decision is selected. Create an ADR only if evidence establishes a consequential persistence, rendering, or platform-compatibility boundary.
