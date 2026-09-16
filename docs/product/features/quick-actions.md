# Quick Actions Interaction Specification

> Public semantic source: English. Chinese counterpart: [quick-actions.zh-CN.md](quick-actions.zh-CN.md). The optional screen-locking capability and its accessibility boundary are defined in [double-tap-lock.md](double-tap-lock.md); the shared eligible blank space is defined by the [Home interaction specification](../surfaces/home.md).

## Purpose and scope

Quick actions let the user bind Home basic-information blank-space gestures to product actions instead of fixing those assignments in the product rules. The contract registers exactly two slots:

- the basic-information double-tap slot, and
- the basic-information long-press slot.

Each slot resolves to exactly one action: `No action`, `Edit mode`, or `Screen lock`. Every slot defaults to `No action`; on a new installation no blank-space gesture has a product action.

Quick actions cover only these two registered slots. Selecting time or date keeps its immediate Clock or Calendar destination, application targets keep their normal tap and long-press behavior, edit-mode-internal gestures, Drawer gestures, and the remaining Home layout are not configurable, and no slot exists today in the favorite main list or elsewhere in Home. Future slots are additive contract changes.

## Bindings and configuration

- Slots are configured independently. Avenor applies no uniqueness, exclusivity, or cross-slot validation: two slots may bind the same action, and no binding constrains another.
- Binding changes take effect immediately and persist locally with the complete settings state.
- The binding state is part of the manual backup file and follows its schema rules; a missing bindings section or field restores the default `No action` value.
- Changing one binding never affects the other slot's recognition, an in-progress gesture recognition, or any other Home behavior.

## Gesture recognition

- Both slots operate on the eligible blank space of the basic-information region defined by the [Home interaction specification](../surfaces/home.md). A double tap must begin and end inside that eligible blank space; a long press must begin there and remain within platform movement tolerance. Time, date-and-weekday, and every other interactive target stay excluded.
- Future information displayed in this region reduces the eligible area by its own content and targets but must not consume all practical blank space.
- Selecting time continues to open Clock immediately and selecting date continues to open Calendar immediately; neither waits for a possible second tap.
- The double-tap slot uses the platform double-tap timing and movement tolerance; the long-press slot uses the platform long-press threshold. Avenor defines no product-specific hard-coded thresholds.
- A drag beyond the platform tolerance, an upward Home-to-Drawer gesture taking transition ownership, cancellation, or a gesture crossing into an excluded target cancels recognition, following [Navigation](../navigation.md).

## Bound actions

- `No action`: the recognized gesture produces no product action and no feedback.
- `Edit mode`: recognition enters Home edit mode exactly as the other edit-mode entry paths do, preserving main-list and ribbon positions, and produces one platform-standard haptic at recognition.
- `Screen lock`: recognition requests one system lock-screen action through the purpose-limited accessibility service defined in [double-tap-lock.md](double-tap-lock.md). A successful request produces no Toast, haptic, animation, or additional confirmation. While the service is disabled, the gesture produces no product action and does not open system settings; when the service is enabled but the action is unavailable or fails, Home stays available and shows the localized short Toast `Unable to lock screen` without retrying.

## Settings surface

Settings contains one primary item titled `Quick action settings`, which opens the dedicated page defined here. Exact page and popup values belong to the [Settings presentation specification](../presentation/settings.md#quick-action-settings-page-and-selection-dialog).

- The page title is `Quick action settings` and it reuses the Settings page structure and top app bar, including its Back control. No separate low-fidelity wireframe is defined for this page.
- The page lists one row per registered slot — `Basic-information double tap` then `Basic-information long press` — and each row's supporting text shows its current action name: `No action`, `Edit mode`, or `Screen lock`.
- The page additionally lists the screen-lock service state row defined below. Every row on the page carries the same trailing arrow as a Settings row, including the two slot rows.
- Selecting a slot row opens one local single-choice popup listing `No action`, `Edit mode`, and `Screen lock`. Each option row shows one leading single-selection indicator and its action label, with the current binding marked. Three paths close the popup: selecting an option saves that binding immediately and refreshes the row's supporting text; an outside tap closes it without changing any binding; system Back closes it without changing any binding.
- Selecting `Screen lock` saves that binding exactly like any other option, with no confirmation and without depending on the accessibility state. When the service is `Off` at that moment, Avenor enters the screen-lock authorization flow defined by [double-tap-lock.md](double-tap-lock.md): the local explanation surface with `Open accessibility settings`, and the prominent disclosure before that handoff. The flow opens after the popup closes, and the binding stays saved regardless of whether the user cancels the flow, agrees without enabling the service, or enables the service.
- Selecting `No action` or `Edit mode` never opens the accessibility flow.
- Returning from the page to Settings preserves Settings' prior position. Settings' own Back behavior is unchanged.

### Screen-lock service state row

- The row is always visible on the page regardless of the current bindings. It is the standing channel for Avenor's accessibility screen-lock capability, so the user can always see whether that capability is enabled and reach its explanation and authorization flow.
- Its title is `Screen lock`. Its supporting text is `On` while the required Avenor accessibility service is enabled and connected, and `Off` otherwise.
- It opens the local explanation surface defined by [double-tap-lock.md](double-tap-lock.md), which shows the current state, purpose, privacy boundary, and the `Open accessibility settings` action. The prominent disclosure precedes that handoff per [privacy.md](privacy.md#screen-locking-prominent-disclosure).
- Returning from system settings refreshes the supporting text immediately; Android's state remains authoritative.

## Acceptance intent

- Given the default bindings, when the user double-taps or long-presses eligible basic-information blank space, then no product action occurs.
- Given the double-tap slot is bound to `Edit mode`, when a double tap is recognized in eligible blank space, then Home enters edit mode with one standard haptic and unchanged list positions.
- Given both slots are bound to `Screen lock`, when either bound gesture is recognized with the service enabled, then each recognition requests one system lock action.
- Given a slot is bound to `Screen lock` while the service is disabled, when the bound gesture is recognized, then no action and no feedback occur.
- Given a backup contains bindings, when restore completes, then Home presents exactly the restored bindings; given the bindings section or a field is missing, then that slot restores to `No action`.
- Given the default bindings, when the user opens `Quick action settings`, then both slot rows show `No action` and the screen-lock service state row is visible with `Off`.
- Given no slot is bound to `Screen lock` while the service is enabled, when the page is shown, then the service state row stays visible with `On`.
- Given a slot is bound to `Screen lock` while the service is disabled, when the page is shown, then the service state row shows `Off` and opens the explanation surface.
- Given the service is `Off`, when the user selects `Screen lock`, then the binding is saved and the authorization flow is entered; cancelling that flow or leaving the service disabled keeps the binding.
- Given the service is `Off`, when the user selects `No action` or `Edit mode`, then no authorization flow appears.
- Given a selection popup is open, when the user taps outside it or invokes system Back, then the popup closes and no binding changes.
- Given a slot is re-bound, when the user next uses the other slot, then the other slot keeps its previous binding.
