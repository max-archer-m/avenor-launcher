# Application Action Sheet Presentation Specification

> Public semantic source: English. Chinese counterpart: [app-action-sheet.zh-CN.md](app-action-sheet.zh-CN.md).

## Responsibility

This document owns exact application-action-sheet presentation values. [Application action sheet behavior](../surfaces/app-action-sheet.md) owns content availability, order semantics, and action results.

## Geometry

- On the current portrait-phone scope, the Bottom Sheet occupies the complete available screen width and attaches to the bottom edge. It has `12dp` top-start and top-end corner radii. Its height follows content without a fixed product height or separate maximum; it may grow only to the lower edge of the status-bar safe inset and never draws through protected system UI.
- The sheet container has `0dp` global content padding. Each internal region owns its spacing. Content beyond the available height scrolls only in the application-shortcut region; the identity row and applicable Home Launcher-action row remain visible.
- The top drag handle is `32dp x 4dp` with `12dp` vertical padding above and below it.
- The application-identity row is at least `48dp` high and uses `16dp` horizontal content insets. The name uses Material 3 `titleLarge`, remains static on one line, and uses end ellipsis when necessary. Its visual truncation does not truncate the complete accessibility name.
- Application information uses the shared `24dp` functional-icon token in a `48dp x 48dp` interaction target.
- Light dividers use `16dp` horizontal inset.
- Each optional application shortcut occupies a full-width interaction row at least `48dp` high with `16dp` horizontal content insets. It uses a `24dp` icon, a `16dp` icon-to-name gap, and one static end-ellipsized name whose complete accessibility name remains available.
- The Home-only Launcher-action region has `16dp` vertical padding and five equal-width slots spanning the complete sheet width. Visible actions fill slots from the physical left; hidden or unused slots retain equal width on the right. Each visible slot is one complete interaction target with a `24dp` icon, `4dp` icon-to-label spacing, and a static single-line end-ellipsized label. The region has no fixed total height and grows only as required by its single-line content and system font scaling. Drawer reserves no Launcher-action geometry.
- The clone or profile badge uses a `12dp x 12dp` visual region overlaid at the Bottom Sheet's bottom-end corner without outward offset, margin, a dedicated row, an exclusion region, or reserved content space. The current geometry must not visibly cover essential text or an interaction target; any actual collision caused by later content change is a presentation defect to correct in that content rather than a reason to reserve badge space by default.
