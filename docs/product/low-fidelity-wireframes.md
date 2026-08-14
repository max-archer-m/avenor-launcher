# Avenor Launcher Low-Fidelity Wireframes

> Semantic source: English. Chinese counterpart: [low-fidelity-wireframes.zh-CN.md](low-fidelity-wireframes.zh-CN.md).

The wireframes visualize the current product contract's spatial hierarchy, region relationships, and primary content order. They do not define pixel-perfect styling or implementation structure. Normative behavior remains in the applicable interaction specification.

Each language-neutral ASCII canvas is stored once under `docs/product/wireframes/`. Full-screen surfaces target approximately 80 columns by 96 lines; modal surfaces may be shorter. Home uses a complete canvas because its vertical proportions are part of the current contract. Canvas labels are structural annotations, not user-visible copy.

## Reading rules

- `P` marks an illustrative primary-favorite slot with a 48dp application icon and at least a 64dp interaction target.
- `C` marks an illustrative companion-favorite slot with a 32dp application icon and at least a 48dp interaction target.
- Numbered entries illustrate placement and do not establish a fixed capacity. Each favorite group scrolls independently only when its content overflows.
- Drawer additions enter primary favorites by default. In Home edit mode, a drag to an empty position in the other group moves the favorite; a drag to an occupied position swaps the two favorites.
- Application names remain one line and use end ellipsis when required.
- Dot fills show allocated space and do not prescribe a visible texture, color, or additional layer.
- The Home bottom region is reserved space only; its pattern does not define a ribbon, carousel, paging, or another interaction.

## Wireframe index

- [Home](wireframes/home.txt) - complete 80 x 96 canvas; shows the approximate 20:60:20 vertical composition and the 60:40 favorite composition.
- [Home edit mode](wireframes/home-edit-mode.txt) - complete 80 x 96 canvas; shows the three editing surfaces, favorite drag handles, and independent group scrolling.
- [Drawer](wireframes/drawer.txt) - complete 80 x 96 canvas; shows the single application list, sticky anchors, and persistent alphabet index.
- [Application action sheet](wireframes/app-action-sheet.txt) - 80 x 80 modal-state canvas; shows the blocked background, scrim, and BottomSheet content order.
- [Settings](wireframes/settings.txt) - complete 80 x 96 canvas; shows navigation, default-Launcher status, and product-information rows.

## Authority boundary

A wireframe is a visual aid, not an independent product decision. When a wireframe and normative prose differ, the applicable interaction specification and product foundation requirements govern. Update the wireframe in the same documentation change when a confirmed spatial contract changes.
