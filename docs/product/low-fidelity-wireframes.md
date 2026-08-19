# Avenor Launcher Low-Fidelity Wireframes

> Semantic source: English. Chinese counterpart: [low-fidelity-wireframes.zh-CN.md](low-fidelity-wireframes.zh-CN.md).

The wireframes visualize the current product contract's spatial hierarchy, region relationships, and primary content order. They do not define pixel-perfect styling or implementation structure. Normative behavior remains in the applicable interaction specification.

Each language-neutral ASCII canvas is stored once under `docs/product/wireframes/`. Full-screen surfaces target approximately 80 columns by 96 lines; modal surfaces may be shorter. Home uses a complete canvas to show content-driven modules within the non-scrolling screen, including unallocated transparent space that is not a product region. Canvas labels are structural annotations, not user-visible copy.

## Reading rules

- `P` marks an illustrative primary-favorite item with a 48dp application icon, a default 64dp item height, and a `16sp/24sp` name.
- `C` marks an illustrative companion-favorite item with a 32dp application icon, a default 48dp item height, and a `14sp/20sp` name.
- The favorite composition has `8dp` internal padding and an `8dp` gap between its 55:45 groups. Each item uses `8dp` internal padding on all sides and a `16dp` icon-to-name gap.
- Numbered entries illustrate placement and do not establish a fixed capacity. Each favorite group scrolls independently only when its content overflows.
- Drawer additions enter primary favorites by default. In Home edit mode, a drag to a valid insertion position in the other group moves the favorite; a drag to an occupied position swaps the two favorites. The model has no predefined empty slots.
- Application names remain one line and use end ellipsis when required.
- Dot fills show allocated space and do not prescribe a visible texture, color, or additional layer.
- A bottom secondary favorite module is absent and has zero height until applicable user-created content is defined. Unallocated transparent Home space is not that module.
- The Home basic-information label marks eligible blank space only; time and date targets are excluded from double-tap locking.

## Wireframe index

- [Home](wireframes/home.txt) - complete 80 x 96 canvas; shows content-driven vertical sizing, the 55:45 favorite composition, and unallocated transparent space.
- [Home edit mode](wireframes/home-edit-mode.txt) - complete 80 x 96 canvas; shows editing surfaces only for visible modules, favorite drag handles, and independent group scrolling.
- [Drawer](wireframes/drawer.txt) - complete 80 x 96 Content-state canvas; shows the single application list, non-pinned anchors aligned with the application-name column, final Settings section, and fixed alphabet index. Full-surface Loading and Error states hide that index as defined by the Drawer contract.
- [Application action sheet](wireframes/app-action-sheet.txt) - 80 x 80 modal-state canvas; shows the blocked background, scrim, and BottomSheet content order.
- [Settings](wireframes/settings.txt) - complete 80 x 96 canvas; shows navigation, default-Launcher status, and product-information rows.

## Authority boundary

A wireframe is a visual aid, not an independent product decision. When a wireframe and normative prose differ, the applicable interaction specification and product foundation requirements govern. Update the wireframe in the same documentation change when a confirmed spatial contract changes.
