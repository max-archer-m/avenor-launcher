# Application Action Sheet

> Public semantic source: English. Chinese counterpart: [app-action-sheet.zh-CN.md](app-action-sheet.zh-CN.md).

## Presentation

The application action sheet is a modal bottom sheet opened by long-pressing an application on Home or Drawer.

- It uses a dark surface with light text and preserves light system status-bar icons.
- A light short horizontal drag handle appears at the top.
- A gray scrim covers the underlying surface.
- Selecting the scrim, dragging the sheet to the bottom, or pressing Back dismisses it.
- Until dismissal, the underlying application surface cannot receive clicks, long presses, or scrolling.
- An action executes first; after invocation the sheet closes. Current product definition does not add separate success feedback.

## Content order

From top to bottom:

1. Application name aligned left and an application-information icon aligned right.
2. A light divider with `16dp` horizontal inset.
3. An optional application-shortcut region containing platform-provided shortcuts, each displayed as icon plus name, followed by a light divider with `16dp` horizontal inset.
4. Launcher actions in a horizontal row, each with icon above label.

If the platform exposes no application shortcuts, omit the complete application-shortcut region, including its trailing divider. The divider below the application identity remains, so identity and Launcher actions retain one boundary without adjacent duplicate dividers.

The current contract does not define a dedicated overflow interaction for unusually many application shortcuts. Such an interaction becomes an additive capability only after a real application demonstrates the need. Regardless of count, implementation must not crash or draw through protected system UI.

## Application identity and information

- The application name is not interactive.
- No application icon is shown in the header.
- For a cloned application, display the platform-provided clone badge independently at the Bottom Sheet's bottom-right corner to follow the identity treatment observed on the author's Samsung device.
- The badge uses a `12dp × 12dp` visual region aligned to the Bottom Sheet's bottom-right corner without an outward offset.
- The badge is decorative, cannot receive input, does not occupy a Launcher-action slot, and does not reserve content-layout space. Primary applications do not show it.
- Selecting the information icon opens the system application-information surface.
- Returning from system application information does not restore the sheet and refreshes the affected application state.
- If the system application-information surface cannot be opened, show the short localized Toast `Unable to open application information`, close the sheet, preserve the underlying Home or Drawer position, and do not change favorite state.

## Launcher actions

- Launcher actions use five fixed horizontal slots ordered from left to right. Visible actions compact into the leftmost available slots; hidden actions leave no internal gap, and unused slots remain empty on the right. Actions do not redistribute evenly across the full width.
- At most five Launcher actions are defined; this limit does not apply to platform application shortcuts.
- On Home the action order is remove favorite, reorder, then uninstall. Reorder is hidden when Home has fewer than two favorites; uninstall is hidden when unavailable; later visible actions shift left.
- On Drawer the action order is add favorite or remove favorite according to current state, then uninstall when available. Uninstall shifts into the next left slot and is hidden when unavailable.
- When favorite persistence cannot be reliably read, Avenor cannot determine whether the selected application is currently a favorite. Replace the add-favorite or remove-favorite action with one disabled favorite slot labeled `Favorites unavailable`, using the standard heart icon. The slot cannot receive input and does not produce a Toast. Application information and other non-favorite actions remain governed by their own availability. After a successful favorite-data read, restore the applicable add-favorite or remove-favorite action from the recovered state.
- Adding a favorite appends it on Home while Drawer remains at its current position.
- Removing a favorite closes the sheet and removes the Home entry. When invoked from Drawer, the application remains in Drawer and the current anchor and relative scroll position do not change.
- Uninstall is hidden for applications the system does not allow the user to uninstall or only permits disabling.
- For a cloned application, show uninstall only when the platform can be confirmed to address removal of that selected clone without uninstalling the primary application. If that guarantee is unavailable, hide uninstall and leave system management available through application information. Never degrade a clone-removal action into primary-application uninstall.
- Destructive confirmation and actual removal remain under system control.
- If the user cancels the system uninstall confirmation, return to the originating Home or Drawer position, do not restore the sheet, and refresh the application state.
- If the system uninstall surface cannot be opened, show the short localized Toast `Unable to open uninstall`, close the sheet, preserve the underlying Home or Drawer position, and do not change favorite state.
- Current product scope does not add separate failure handling for invocation of a platform-provided application shortcut.

## Icons

- Add favorite: heart.
- Remove favorite: broken heart.
- Reorder: vertical-direction arrows.
- Uninstall: trash.
- Application information: information symbol.

Final icon assets and accessibility descriptions belong to the visual and implementation specifications.
