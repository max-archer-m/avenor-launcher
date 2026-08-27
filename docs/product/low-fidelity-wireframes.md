# Avenor Launcher Low-Fidelity Wireframes

> Public semantic source: English. Chinese counterpart: [low-fidelity-wireframes.zh-CN.md](low-fidelity-wireframes.zh-CN.md).

The language-neutral ASCII canvases under `docs/product/wireframes/` visualize spatial hierarchy, region relationships, and major content order. They do not independently define product behavior, user-visible copy, exact visual values, or implementation structure.

## Reading rules

- Canvas labels are structural annotations, not user-visible copy.
- Dotted fill means only that space is allocated; it does not prescribe texture, color, opacity, or another layer.
- Repeated labels such as `L1`, `L2`, or numbered application entries are illustrative. They do not establish fixed content, capacity, or identity.
- A canvas may show one representative content state without defining all valid sizes, arrangements, loading states, error states, or edit states.
- The canvas boundary communicates composition, not a device profile or pixel-to-character scale. Dimensions written in a wireframe description are ASCII canvas dimensions, not product UI values.
- Application names remain illustrative. Localization, truncation, typography, icon size, spacing, targets, and visual states belong to the applicable behavior or presentation contract.
- Unallocated Home space remains transparent background rather than an unnamed product module.

## Contract routing

Use the owning document for a product decision instead of interpreting the wireframe:

| Question | Behavior owner | Exact presentation owner |
| --- | --- | --- |
| Home content, editing, drag results, and container lifecycle | [Home interaction](surfaces/home.md) | [Home presentation](presentation/home.md) |
| Drawer inventory, sorting, selection, index behavior, and settings actions | [Drawer interaction](surfaces/drawer.md) | [Drawer presentation](presentation/drawer.md) |
| Application action availability, order semantics, and results | [Application action sheet](surfaces/app-action-sheet.md) | [Application action sheet presentation](presentation/app-action-sheet.md) |
| Settings content, navigation, and results | [Settings interaction](surfaces/settings.md) | [Settings presentation](presentation/settings.md) |
| Cross-surface transitions and system-return behavior | [Navigation](navigation.md) | Applicable surface presentation specification |

## Wireframe index

- [Home](wireframes/home.txt) — complete `80 x 96` canvas showing content-driven vertical regions, the representative two-list state, and unallocated transparent space. The Home contracts define other valid states.
- [Home edit mode](wireframes/home-edit-mode.txt) — complete `80 x 103` canvas showing editing regions, list controls, add actions, provisional containers, and independent scrolling relationships.
- [Drawer](wireframes/drawer.txt) — complete `80 x 96` representative Content-state canvas showing the fixed top region, ordinary and multi-selection semantics, list anchors, Settings section, and alphabet index.
- [Application action sheet](wireframes/app-action-sheet.txt) — `80 x 80` modal-state canvas showing the blocked background, scrim, and content order.
- [Settings](wireframes/settings.txt) — complete `80 x 96` canvas showing navigation, Launcher status, and product-information regions.

## Authority and update rules

A wireframe is a visual aid, not an independent product decision. When it differs from normative prose, the applicable behavior contract governs outcomes and the applicable presentation specification governs exact visual values.

Update a wireframe in the same documentation change when a confirmed spatial hierarchy, region relationship, or major content order changes. A behavior-only change or presentation-token-only change does not require editing a wireframe unless the canvas would otherwise become structurally misleading.
