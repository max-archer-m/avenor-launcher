# Avenor Launcher Low-Fidelity Wireframes

> Semantic source: English. Chinese counterpart: [low-fidelity-wireframes.zh-CN.md](low-fidelity-wireframes.zh-CN.md).

The wireframes visualize the current product contract's spatial hierarchy, region relationships, and primary content order. They do not define pixel-perfect styling or implementation structure. Normative behavior remains in the applicable interaction specification.

Each language-neutral ASCII canvas is stored once under `docs/product/wireframes/`. Full-screen surfaces target approximately 80 columns by 96 lines; modal surfaces may be shorter. Home uses a complete canvas to show content-driven modules within the non-scrolling screen, including unallocated transparent space that is not a product region. Canvas labels are structural annotations, not user-visible copy.

## Reading rules

- `P` marks an illustrative primary-favorite item with a 40dp application icon, a default 56dp item height, and a `16sp/24sp` name.
- `C` marks an illustrative companion-favorite item with a 32dp application icon, a default 48dp item height, and a `14sp/20sp` name.
- The favorite composition has `8dp` internal padding on each side and a `16dp` gap between its 55:45 groups. Each item uses `8dp` internal padding on all sides and a `16dp` icon-to-name gap. Both groups use the same `24dp` drag-handle graphic in a `48dp`-wide hit region with `12dp` horizontal padding on each side; the hit-region height follows the current item height. The graphic has no additional right margin; visible spacing comes from the existing item padding. Normal and edit-mode item heights are identical; drag handles do not stretch rows.
- Numbered entries illustrate placement and do not establish a fixed capacity. Each favorite group scrolls independently only when its content overflows.
- Drawer additions enter primary favorites by default. In Home edit mode, the source position remains stable while an independent opaque preview follows the touch point with its fixed grab offset. The preview does not participate in list animation. In-group exchange targets may update visible positions during the drag; cross-group exchange and insertion remain feedback-only until release, when the operation is finalized and saved.
- Dragging onto another favorite in the source group means exchange; same-group gaps never accept insertion or show an insertion line. In the other group, a favorite body means exchange and a valid gap means insertion. First, between-item, last, and empty-group insertion boundaries are valid, and exchange and insertion feedback are mutually exclusive.
- Only the two favorite groups accept a drop. Target feedback and edge auto-scroll use the finger touch point. Same-group exchange updates during the drag; cross-group exchange and insertion update only on release. The current operation is finalized and saved on release.
- Application names remain one line and use end ellipsis when required.
- Dot fills show allocated space and do not prescribe a visible texture, color, or additional layer.
- The secondary favorites area sits below the primary favorites area with `16dp` vertical spacing, uses the full available Home content width, has zero height without content, and scrolls horizontally only when its content exceeds its visible width. Home itself does not scroll vertically because of this area. Unallocated transparent Home space is not that module.
- Secondary favorites content is user-defined, has no product-defined type grouping or per-item title, and consists of launchable application entries. Each horizontal item list is finite with an explicit first and last entry; it does not scroll cyclically. Selecting an entry launches its application, while long-pressing it opens the existing application action sheet.
- The Home basic-information label marks eligible blank space only; time and date targets are excluded from double-tap locking.

## Wireframe index

- [Home](wireframes/home.txt) - complete 80 x 96 canvas; shows content-driven vertical sizing, the 55:45 favorite composition, and unallocated transparent space.
- [Home edit mode](wireframes/home-edit-mode.txt) - complete 80 x 96 canvas; shows editing surfaces only for visible modules, favorite drag handles, and independent group scrolling.
- [Drawer](wireframes/drawer.txt) - complete 80 x 96 Content-state canvas; shows the single application list, non-pinned anchors aligned with the application-name column, final Settings section, and fixed alphabet index. Full-surface Loading and Error states hide that index as defined by the Drawer contract.
- [Application action sheet](wireframes/app-action-sheet.txt) - 80 x 80 modal-state canvas; shows the blocked background, scrim, and BottomSheet content order.
- [Settings](wireframes/settings.txt) - complete 80 x 96 canvas; shows navigation, default-Launcher status, and product-information rows.

## Authority boundary

A wireframe is a visual aid, not an independent product decision. When a wireframe and normative prose differ, the applicable interaction specification and product foundation requirements govern. Update the wireframe in the same documentation change when a confirmed spatial contract changes.
