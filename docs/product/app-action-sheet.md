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
2. A light divider with 12dp horizontal inset.
3. Platform-provided application shortcuts, each displayed as icon plus name.
4. A light divider with 12dp horizontal inset.
5. Launcher actions in a horizontal row, each with icon above label.

If the platform exposes no application shortcuts, the application-shortcut region is omitted. The current contract does not define overflow behavior for unusually many application shortcuts.

## Application identity and information

- The application name is not interactive.
- No application icon is shown in the header.
- The platform badge is intended to appear at the bottom-right of the sheet, but its standalone visual meaning requires confirmation.
- Selecting the information icon opens the system application-information surface.
- Returning from system application information does not restore the sheet and refreshes the affected application state.
- If the system destination cannot be opened, the user receives a short localized error message.

## Launcher actions

- Launcher actions use five fixed horizontal slots. Fewer actions occupy their assigned slots and do not redistribute evenly across the full width.
- At most five Launcher actions are defined; this limit does not apply to platform application shortcuts.
- On Home the actions are: remove favorite, reorder, and uninstall when available.
- On Drawer the actions are: add favorite or remove favorite according to current state, and uninstall when available.
- Adding a favorite appends it on Home while Drawer remains at its current position.
- Removing a favorite closes the sheet and removes the Home entry.
- Uninstall is hidden for applications the system does not allow the user to uninstall or only permits disabling.
- For a cloned application, uninstall means removal of that clone and must not uninstall the primary application.
- Destructive confirmation and actual removal remain under system control.

## Icons

- Add favorite: heart.
- Remove favorite: broken heart.
- Reorder: vertical-direction arrows.
- Uninstall: trash.
- Application information: information symbol.

Final icon assets and accessibility descriptions belong to the visual and implementation specifications.
