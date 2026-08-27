# Home Presentation Specification

> Public semantic source: English. Chinese counterpart: [home.zh-CN.md](home.zh-CN.md).

## Responsibility

This document is the authoritative source for exact Home layout, typography, component geometry, and visual-state values. [Home behavior](../surfaces/home.md) owns state and action results; [design foundations](../design-foundations.md) owns shared principles.

## Surface and basic information

- Home applies uniform `8dp` content padding inside `safeDrawing`. Adjacent visible modules use `8dp` spacing.
- The basic-information module adds no padding. Its time and date rows use `8dp` start and end margins inside the padded boundary, producing `16dp` row boundaries from the corresponding safe edges. The time row adds `8dp` top margin for a final `16dp` top distance. Date text adds another `8dp` start inset and begins `24dp` from the safe start edge while its complete row target remains at `16dp`.
- The time row is at least `64dp` high and uses `57sp/64sp` bold display text with tabular numerals where supported. The date row is `48dp` high and uses `16sp/24sp` normal text.
- Home's favorite-list area, vertical lists, favorite bars, and favorite items add no outer padding beyond the Home content padding. Normal and edit modes preserve these outer boundaries.

## Vertical favorite lists

- One list uses the complete padded favorite-area width. Two lists divide that width equally after an `8dp` inter-list gap. Both share one viewport height.
- Large uses a `48dp` icon, `64dp` item height, and `18sp/28sp` normal-weight name. Medium uses `40dp`, `56dp`, and `16sp/24sp`. Small uses `32dp`, `48dp`, and `14sp/20sp`.
- Favorite items add no padding. The icon uses an `8dp` start margin and vertical centering, followed by a `16dp` icon-to-name gap. Names use system scaling, one static line, and end ellipsis.
- An application drag handle uses `24dp` artwork centered in a physical-right-edge `40dp`-wide target occupying the item's full height without an end margin.

## Edit presentation

- Each editable Home module uses the shared subtle translucent light-gray editing surface with a small corner radius. Exact shared surface color, opacity, and radius remain subject to visual calibration.
- An application remove control is a white X on a solid error-red `20dp x 20dp` background at the physical top-left. Its visible and interaction bounds are both `20dp x 20dp`; this is an author-approved compact-target exception.
- A persisted vertical list has a `40dp`-high control bar without padding, fill, rounded whole-bar surface, outer border, or following gap. A `1dp` light-foreground bottom divider at `12%` opacity separates it from content.
- Three `40dp x 40dp` control targets align flush to the physical right edge in physical right-to-left reorder, size, remove order. Reorder centers `24dp` artwork; remove centers a `24dp x 24dp` error-red background with white X; size contains only its localized value. One list hides reorder while retaining its position.
- The size menu uses three `64dp` rows. Each row orders its selection indicator, bundled generic application icon preview, and localized label without RTL mirroring. Preview sizes are `48dp`, `40dp`, and `32dp`.
- Add actions are plain localized text targets at least `48dp` high and wide enough for the label, with no decorative container or separate plus icon.

## Favorite bars

- The secondary area uses the complete padded Home width and `8dp` spacing from adjacent content. Populated favorite bars are `56dp` high with `8dp` vertical spacing and no container padding.
- Each entry uses an `8dp` start inset, `40dp` icon, `8dp` icon-to-name gap, one `16sp/24sp` normal line measured up to `64dp`, and `8dp` trailing inset. Total width is content-measured up to `128dp`; longer labels use end ellipsis. Entries use `8dp` spacing.
- Each entry uses the light foreground role at `6%` for its background, `12%` for a `1dp` boundary, and a `12dp` corner radius.
- An editable favorite bar uses equal `40dp x 56dp` fixed rails. The start rail centers a `24dp x 24dp` error-red remove graphic and the end rail centers a `24dp` reorder handle. Each rail leaves `8dp` on both horizontal sides; `1dp` dividers separate rails from the viewport.
- Each editable favorite bar uses a transparent interior with a `1dp` light-foreground outline at `12%` opacity and `12dp` radius.

## Drag and overflow feedback

- Application auto-scroll uses a `48dp` inward light-foreground gradient from transparent to at most `8%` opacity. It changes no geometry, intercepts no input, and remains below insertion and exchange feedback.
- Drag artwork uses source presentation until a valid cross-container release applies the destination presentation.
