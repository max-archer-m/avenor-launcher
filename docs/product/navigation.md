# Navigation and Surface Model

> Public semantic source: English. Chinese counterpart: [navigation.zh-CN.md](navigation.zh-CN.md).

## Responsibility

This document defines the current relationship, entry, exit, restoration, and shared transition behavior of Home, Drawer, Settings, and the application action sheet. It does not prescribe whether Home and Drawer are implemented as separate composable destinations or as coordinated regions of one implementation surface.

## Initial surface

- Home is the initial surface whenever the Launcher starts or restores after process recreation.
- Home and Drawer are distinct product surfaces even if implementation later uses one draggable container.
- Settings is reachable only from the fixed Settings index at the bottom of the Drawer alphabet index.
- Home has no Settings entry.

## Transition model

Home and Drawer use one reversible direct-manipulation transition. The following terms define its observable behavior:

- **Gesture displacement:** The accumulated drag distance in the direction of the target surface. Opposite movement reduces the accumulated distance toward zero.
- **Gesture progress:** Gesture displacement divided by 200dp, clamped to the range from 0 to 1. The 200dp value is the current full gesture distance and may be reconsidered through a later product change.
- **Interactive Drawer displacement:** While the pointer remains down, Drawer moves 1.5dp for each 1dp of gesture displacement. This 1:1.5 mapping applies in both directions.
- **Surface travel:** Drawer travels between its off-screen position below the viewport and its final position at the top of the viewport. The 200dp gesture distance does not mean that the viewport is 300dp high.
- **Settle animation:** After the release decision, the system animates the remaining screen distance from the current interactive position to the selected endpoint. The user is never required to drag Drawer across the full physical screen height.

### Opacity

Opacity is derived from gesture progress, not from Drawer screen-travel percentage. For gesture progress `p`:

- Home content opacity is `max(0, 1 - 2p)`.
- Drawer content opacity is `min(1, 2p)`.
- At 0% progress, Home is fully visible and Drawer is transparent.
- At 25% progress, both surfaces have 50% content opacity.
- At 50% progress, Home is transparent and Drawer is fully visible.
- From 50% through 100%, Home remains transparent and Drawer remains fully visible while position continues to change.

The reverse transition uses the same values in reverse. Rebound and completion animations continue from the current position and opacity without a jump.

### Release decision

- Reaching or exceeding 60% gesture progress, equivalent to 120dp, completes the transition to the target surface.
- Releasing below 60% returns to the origin surface unless the release constitutes a fling in the target direction.
- A target-directed fling completes the transition even below 60%. The velocity threshold and detector are implementation details that must preserve this observable result.
- A fling in the opposite direction does not complete toward the target; it returns toward the origin.
- Cancellation or system interruption returns to the origin through the same position and opacity path.
- Additional pointers have no product action. They must not cause a position jump, reverse the transition, activate an application, or create a second simultaneous transition.

## Home to Drawer

- An upward drag on Home is the only current entry into Drawer.
- In normal Home mode, the drag may begin anywhere inside Avenor's Home interaction area, including over the time, date, favorite entries, empty state, loading state, error state, and Retry control. System-reserved inset gestures remain governed by Android.
- An upward drag beginning outside a favorite group enters the Home-to-Drawer transition directly. Inside either favorite group, that group first consumes upward displacement while it can scroll toward its end. The primary and companion groups arbitrate independently.
- If the touched group does not overflow, it consumes no scroll displacement and the upward drag enters the transition directly. If it overflows, reaching its end boundary transfers the remaining displacement from the same continuous gesture into the Home-to-Drawer transition without requiring the user to lift. Transition progress begins from the displacement transferred after the boundary, not from distance already consumed as list scrolling.
- Downward dragging inside an overflowing favorite group scrolls that group toward its start and does not initiate a Home surface transition. The other favorite group's position remains unchanged.
- Home-to-Drawer dragging is disabled while Home edit mode is active. Handle drags own item movement; other vertical drags inside an overflowing favorite group scroll that group until edit mode ends.
- A pointer sequence can resolve to only one Home action. Before an interactive element's selection or long-press action has been committed, recognition of an upward drag transfers ownership to the Home-to-Drawer transition and suppresses that element's selection, long-press, and haptic response.
- Selection is committed only when the pointer is released without the sequence becoming an upward drag or a long-press. Once a long-press has been recognized and its action has opened, the same pointer sequence does not also begin the Drawer transition.
- A cancelled sequence performs no selection or long-press action and follows the transition cancellation behavior when a Home-to-Drawer drag had already taken ownership.

## Drawer to Home

- System Back returns Drawer to Home using the completion path of the reverse transition.
- Downward dragging first scrolls the application list toward its top boundary.
- While list scroll offset is greater than zero, the list exclusively consumes downward displacement and Drawer does not begin to leave.
- When the list reaches offset zero during the same continuous gesture, remaining downward displacement transfers immediately into the Drawer-to-Home transition; the user does not need to lift and drag again.
- Once the surface transition owns the remaining gesture, the list stays at its top boundary until the transition settles.

## Back and restoration

- On Home, Back has no visible effect when Avenor is the system default Launcher. When Avenor is not the default Launcher, Avenor performs ordinary system Back behavior and does not prescribe the external destination.
- On Drawer, Back returns to Home.
- On Settings, Back returns to the previous Avenor surface, currently Drawer.
- On the application action sheet, Back dismisses the sheet and returns to the unchanged underlying surface.
- Process restoration begins at Home. Drawer, Settings, an action sheet, edit mode, and their transient positions are not restored after process recreation. Within the same process, each favorite group's meaningful scroll position is preserved when returning to Home; process recreation resets both groups to their start positions.

## System surfaces

- Home and Drawer show the system status bar.
- Home and Drawer request full transparency for their application surface and for the status-bar and navigation-bar regions. They do not draw an Avenor gradient, scrim, blur, or opaque bar background.
- Avenor leaves platform and device contrast enforcement at its default behavior. A system-provided translucent protection, including protection used by three-button navigation on applicable Android versions, is not replaced or forcibly disabled by Avenor.
- The system navigation bar and controls follow the user's current navigation mode. Avenor does not hide them or enter immersive mode.
- Home, Drawer, Settings, and every action sheet use the dark theme with light foreground elements. Opening a sheet must not switch system status-bar icons to a dark appearance.
- Content and interaction targets must respect system-bar, display-cutout, and gesture insets even though Home and Drawer draw edge to edge behind transparent system bars.

## Acceptance intent

- A 100dp drag produces 50% gesture progress and 150dp of interactive Drawer displacement before endpoint settling.
- Releasing at 119dp without a qualifying fling returns to the origin; releasing at 120dp or beyond completes to the target.
- A qualifying target-directed fling below 120dp completes to the target.
- Position and opacity remain continuous when the user reverses direction, releases, completes, or rebounds. Drawer-to-Home remains continuous when a gesture transfers from Drawer list scrolling.
- In normal Home mode, an upward drag can begin over every Avenor-managed Home element without also activating or long-pressing that element.
- Drawer cannot be dismissed by a downward list gesture until its list has reached the top boundary; remaining displacement then transfers in the same gesture.
- Opening or closing Drawer does not accidentally launch, long-press, or scroll an application entry.
- The implementation may choose any architecture that satisfies these observable behaviors.
