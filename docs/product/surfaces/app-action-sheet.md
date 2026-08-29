# Application Action Sheet

> Public semantic source: English. Chinese counterpart: [app-action-sheet.zh-CN.md](app-action-sheet.zh-CN.md). Exact visual values are defined in the [application action sheet presentation specification](../presentation/app-action-sheet.md); the spatial sketch is the shared [application action sheet wireframe](../wireframes/app-action-sheet.txt), with reading rules in the [wireframe index](../low-fidelity-wireframes.md).

## Presentation

The application action sheet is a modal bottom sheet opened by long-pressing an application on Home or Drawer.

- It uses a dark surface with light text and preserves light system status-bar icons.
- A light short horizontal drag handle appears at the top.
- A gray scrim covers the underlying surface.
- Selecting the scrim, dragging the sheet to the bottom, or pressing Back dismisses it.
- Until dismissal, the underlying application surface cannot receive clicks, long presses, or scrolling.
- An action executes first; after invocation the sheet closes. Current product definition does not add separate success feedback.
- Invoking the selected ordinary application from Home or Drawer closes the sheet and creates the external application excursion defined in [Navigation](../navigation.md#system-home-and-external-application-return). Returning in the same process displays Home without Loading rather than restoring Drawer.

## Content order

From top to bottom:

1. Application name aligned left and the application-information control aligned right.
2. A light inset divider.
3. An optional application-shortcut region containing platform-provided shortcuts, each displayed with an icon plus name, followed by the same inset divider.
4. On Home only, Launcher actions in a horizontal row, each with its icon above its label.

Exact icon, divider, and badge geometry belongs to the [application action sheet presentation specification](../presentation/app-action-sheet.md).

If the platform exposes no application shortcuts, omit the complete application-shortcut region, including its trailing divider. On Home, the divider below application identity remains as the boundary before Launcher actions. On Drawer, no trailing empty Launcher-action region or divider is reserved.

The current contract does not define a dedicated overflow interaction for unusually many application shortcuts. Such an interaction becomes an additive capability only after a real application demonstrates the need. Regardless of count, implementation must not crash or draw through protected system UI.

## Application identity and information

- The application name is not interactive.
- No application icon is shown in the header.
- For a cloned application, display the platform-provided clone badge independently at the Bottom Sheet's bottom-right corner to follow the identity treatment observed on the author's Samsung device.
- The badge aligns to the Bottom Sheet's bottom-right corner without an outward offset. Its exact visual region belongs to the application action sheet presentation specification.
- The badge is decorative, cannot receive input, does not occupy a Launcher-action slot, and does not reserve content-layout space. Primary applications do not show it.
- Selecting the information icon opens the system application-information surface.
- Returning from system application information does not restore the sheet and refreshes the affected application state.
- If the system application-information surface cannot be opened, show the short localized Toast `Unable to open application information`, close the sheet, preserve the underlying Home or Drawer position, and do not change favorite state.

## Launcher actions

- Launcher actions are available only when the sheet originates from Home. Drawer omits the complete Launcher-action region; favorite management is entered from Home edit mode, while application information remains the system-management path for a Drawer entry.
- Home Launcher actions use five fixed horizontal slots ordered from left to right. Visible actions compact into the leftmost available slots; hidden actions leave no internal gap, and unused slots remain empty on the right. Actions do not redistribute evenly across the full width.
- Each Launcher action's complete icon-and-label item is one interaction target. The icon is not an independent or smaller touch target.
- At most five Launcher actions are defined; this limit does not apply to platform application shortcuts.
- On Home the action order is remove favorite, edit, then uninstall. Edit opens Home edit mode and remains available for a single favorite; uninstall is hidden when unavailable, and later visible actions shift left.
- Removing a favorite from Home closes the sheet and removes that Home entry.
- Uninstall is hidden for applications the system does not allow the user to uninstall or only permits disabling.
- For a cloned application, show uninstall only when the platform can be confirmed to address removal of that selected clone without uninstalling the primary application. If that guarantee is unavailable, hide uninstall and leave system management available through application information. Never degrade a clone-removal action into primary-application uninstall.
- Destructive confirmation and actual removal remain under system control.
- If the user cancels the system uninstall confirmation, return to the originating Home position, do not restore the sheet, and refresh the application state.
- If the system uninstall surface cannot be opened, show the short localized Toast `Unable to open uninstall`, close the sheet, preserve the underlying Home position, and do not change favorite state.
- Current product scope does not add separate failure handling for invocation of a platform-provided application shortcut.

## Icons

- Remove favorite: broken heart.
- Edit: vertical-direction arrows.
- Uninstall: trash.
- Application information: information symbol.

Final icon assets and accessibility descriptions belong to the visual and implementation specifications.
