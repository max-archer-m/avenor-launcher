# Avenor Launcher Low-Fidelity Wireframes

> Semantic source: English. Chinese counterpart: [low-fidelity-wireframes.zh-CN.md](low-fidelity-wireframes.zh-CN.md).

The wireframes visualize the current product contract's spatial hierarchy, region relationships, and primary content order. They do not define pixel-perfect styling or implementation structure. Normative behavior remains in the applicable interaction specification.

Each language-neutral ASCII canvas is stored once under `docs/product/wireframes/`. Full-screen surfaces target approximately 80 columns by 96 lines; modal surfaces may be shorter. Home uses a complete canvas to show content-driven modules within the non-scrolling screen, including unallocated transparent space that is not a product region. Canvas labels are structural annotations, not user-visible copy.

## Reading rules

- `L1` and `L2` mark illustrative equal-status vertical favorite-list entries. The canvas shows the default medium size: a `40dp` icon, `56dp` item height, and `16sp/24sp` name. Large uses `48dp`, `64dp`, and `18sp/28sp`; small uses `32dp`, `48dp`, and `14sp/20sp`.
- The favorite composition has `8dp` internal padding on each side and a `16dp` inter-list gap. One list uses full width; two visible lists divide the remaining width equally. Items use a `16dp` icon-to-name gap and the current drag-handle geometry. Normal and edit-mode item heights preserve each list's selected size.
- Numbered entries illustrate placement and do not establish a fixed capacity. Each list scrolls independently only when its content overflows; losing its final entry deletes that list. A secondary ribbon likewise deletes when it loses its final application entry.
- Drawer action-sheet additions enter the first vertical list by default. In Home edit mode, the source position remains stable while an independent opaque preview follows the touch point with its fixed grab offset. The preview does not participate in list animation. In-list exchange targets may update visible positions during the drag; cross-list exchange and insertion remain feedback-only until release, when the operation is finalized and saved.
- Dragging onto another favorite in the source list means exchange; same-list gaps never accept insertion or show an insertion line. In the other list, a favorite body means exchange and a valid gap means insertion. First, between-item, last, and empty-list insertion boundaries are valid, and exchange and insertion feedback are mutually exclusive.
- Only persisted or provisional vertical favorite lists accept the currently defined drops. Target feedback and edge auto-scroll use the finger touch point. Same-list exchange updates during the drag; cross-list exchange and insertion update only on release. The current operation is finalized and saved on release.
- Application names remain one line and use end ellipsis when required.
- Dot fills show allocated space and do not prescribe a visible texture, color, or additional layer.
- The secondary favorites area sits below the favorite-list area with `16dp` vertical spacing, uses the full available Home content width, and has zero height without content. It contains at most five `56dp`-high horizontal ribbons; Home itself does not scroll vertically because of this area. Unallocated transparent Home space is not that module.
- Each ribbon contains a finite user-defined sequence of `128dp`-wide launchable entries and scrolls horizontally only when those entries overflow. Ribbons use `8dp` vertical spacing. Entries show a `40dp` icon and one `16sp/24sp` name line, align left with `8dp` spacing, and do not redistribute unused width. The wireframe leaves ribbon titles undecided; the normative contract defines the confirmed subtle item boundary. Selecting an entry launches its application, while long-pressing it opens the existing application action sheet.
- Home edit mode adds a horizontally centered `24dp` plus control in at least a `48dp` target at each favorite-list or ribbon end, shows one provisional list while fewer than two lists exist, and shows one provisional ribbon while fewer than five ribbons exist. The same Drawer canvas represents ordinary and favorite multi-selection modes: ordinary mode has a Back-only transparent top bar, while multi-selection replaces it with Cancel, target description, and Confirm and reserves a sequence-number slot to the left of every application icon.
- The Home basic-information label marks eligible blank space only; time and date targets are excluded from double-tap locking.

## Wireframe index

- [Home](wireframes/home.txt) - complete 80 x 96 canvas; shows content-driven vertical sizing, the current two-list equal-width state, and unallocated transparent space. One-list normal Home expands that list to full composition width.
- [Home edit mode](wireframes/home-edit-mode.txt) - complete 80 x 96 canvas; shows editing surfaces, favorite drag handles, list-end add controls, provisional list and ribbon entries, and independent list scrolling.
- [Drawer](wireframes/drawer.txt) - complete 80 x 96 Content-state canvas; shows the fixed transparent top app bar, ordinary and multi-selection bar semantics, the single application list, non-pinned anchors aligned with the application-name column, final ordinary-mode Settings section, and fixed alphabet index. Full-surface Loading and Error states hide that index as defined by the Drawer contract.
- [Application action sheet](wireframes/app-action-sheet.txt) - 80 x 80 modal-state canvas; shows the blocked background, scrim, and BottomSheet content order.
- [Settings](wireframes/settings.txt) - complete 80 x 96 canvas; shows navigation, default-Launcher status, and product-information rows.

## Authority boundary

A wireframe is a visual aid, not an independent product decision. When a wireframe and normative prose differ, the applicable interaction specification and product foundation requirements govern. Update the wireframe in the same documentation change when a confirmed spatial contract changes.
