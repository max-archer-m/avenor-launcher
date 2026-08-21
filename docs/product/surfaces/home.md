# Home Interaction Specification

> Public semantic source: English. Chinese counterpart: [home.zh-CN.md](home.zh-CN.md). Shared navigation is defined in [navigation.md](../navigation.md); double-tap locking is defined in [double-tap-lock.md](../features/double-tap-lock.md); shared visual rules are defined in [design-foundations.md](../design-foundations.md); the complete spatial sketches are the shared [normal Home](../wireframes/home.txt) and [Home edit mode](../wireframes/home-edit-mode.txt) wireframes, with reading rules in the [wireframe index](../low-fidelity-wireframes.md).

## Purpose and structure

Home is the Launcher primary surface. It is one fixed, non-pageable, non-collapsible surface without folders. The page itself does not scroll; only the primary and companion favorite regions can scroll independently under the overflow rules below. Its content is arranged from top to bottom:

1. Basic information region: time, then date and weekday.
2. Middle favorite composition: primary favorites and companion favorites, containing every accepted saved launchable entry.
3. Bottom secondary region: a conditional later favorite capability that has no current content and therefore occupies no space.

Home uses content-driven vertical sizing rather than fixed proportional regions. The basic-information region wraps its required content and interaction targets. The middle favorite composition grows with its content up to the remaining safe available height; content beyond that bound scrolls inside the applicable favorite group, while Home itself never scrolls. The bottom secondary region has zero height while it contains no user-created favorite content. Any remaining screen space is unallocated transparent Home space, not a persistent product region.

Within the middle favorite composition, the primary-favorite area uses approximately the left 55% and the companion-favorite area the right 45% of the available horizontal space. The composition uses `8dp` internal padding on each side and a `16dp` gap between the two groups; the 55:45 split applies to the remaining width after those fixed spaces. This is a compositional hierarchy, not a user-adjustable divider. “Calm” and “restrained” do not impose a deliberately low favorite count; they constrain distraction and avoidable operations. Both groups share one favorite-composition viewport height, which grows to the larger content requirement of the two groups up to the remaining safe available height, and independently scroll their content only when it exceeds that shared viewport.

Home content uses `16dp` horizontal and vertical container padding. Adjacent visible Home regions use `16dp` spacing. These are internal layout distances rather than external margins because they define spacing within Home's available bounds.

## Time, date, and weekday

- Time is left-aligned above the date and uses a `57sp` font size, `64sp` line height, and bold weight. It uses tabular numerals where the selected system font supports them so minute changes do not shift the clock width.
- Time follows the system 12-hour or 24-hour preference and does not show seconds.
- Date and weekday are displayed below time with an `8dp` start inset relative to the time text, use a `16sp` font size with `24sp` line height and normal weight, and follow the active locale, for example `Sat, Aug 15` in English. The inset affects the visible text only; the complete date-and-weekday row remains its `48dp` focusable touch target. Use logical `start` alignment rather than a fixed physical left offset.
- Selecting the visible time text opens the main surface of the system Clock application when it is exposed. Avenor does not hard-code a vendor Clock package. If the resolved Clock application does not expose a main launchable surface, Avenor falls back to its system alarm destination; if neither destination is available, it shows localized failure feedback without crashing.
- Selecting the visible date-and-weekday text invokes an implicit system Calendar destination without targeting a package.
- The time line is at least `64dp` high and does not require a separately enlarged target beyond its rendered line region.
- The date-and-weekday row is `48dp` high and vertically centers its text. The complete row is its focusable touch target.

### Blank-space double tap

- The basic-information module remains full width within Home's content bounds and preserves eligible blank space outside its current text and other interactive targets. That blank space supports the optional double-tap lock capability defined in [double-tap-lock.md](../features/double-tap-lock.md). Future information such as weather must preserve a practical blank portion of the module for this gesture.
- The time and date-and-weekday targets are excluded. Their existing single-tap actions remain immediate and do not wait for double-tap recognition.
- The gesture is inactive unless the user has explicitly enabled the narrowly scoped Avenor accessibility service in Android system settings.

## Favorite region

- A new installation starts with no favorites and shows a concise empty-state invitation to add applications from Drawer.
- Every favorite belongs to exactly one of two direct-access groups: primary favorites or companion favorites. A favorite added from Drawer is assigned to the primary group by default. The user can later move it between groups through Home edit mode; adding a favorite does not navigate to Home or enter edit mode.
- Primary favorites represent the author's relatively highest-frequency direct-access applications and occupy the approximately 55% area.
- Companion favorites are direct-access applications used relatively less often than primary favorites and occupy the approximately 45% area. “Companion” does not mean hidden, disabled, unavailable, or optional; the two groups have the same launch and editing availability.
- Drawer additions are appended to the primary group. The product does not reject an addition because the primary viewport is full; overflow extends the primary group's scrollable content while preserving existing order. Avenor does not place the entry into companion favorites automatically.
- The same launchable entry cannot appear more than once. A primary application and its clone are distinct entries and may each be favorited.
- Primary and companion favorites must be visually distinguishable without becoming two ordinary list-density variants. Every item uses `8dp` internal padding on all sides. Primary favorites use `40dp × 40dp` application icons inside default `56dp`-high interaction targets. Companion favorites use `32dp × 32dp` application icons inside default `48dp`-high interaction targets. The `16dp` horizontal icon-to-name gap matches Drawer application entries. The vertical component of the shared padding produces these default heights; no additional row padding is added merely to enlarge them.
- An icon and its application name use a `16dp` horizontal gap. Primary names use `16sp` type with `24sp` line height and normal weight; companion names use `14sp` type with `20sp` line height and normal weight. Both follow system font scaling, remain one line, use end ellipsis when they do not fit, and never use marquee text.
- Neither group has a product-defined slot limit derived from the current viewport. The two groups use the shared composition viewport height and maintain independent content extents and scroll positions. When a group's content fits, that group does not scroll; when it exceeds the shared viewport, it scrolls vertically without changing the other group's position or scroll state. The two groups do not borrow visible space from one another, and fewer favorites preserve unused space rather than enlarging icons or spacing.
- Icon shape and badge use the platform-provided representation consistently across Home, Drawer, and related application UI.
- Selecting an entry immediately launches it. Ordinary selection does not produce haptic feedback. Duplicate rapid activation must be suppressed.
- Long-pressing an entry produces long-press haptic feedback and opens the application action sheet defined in [app-action-sheet.md](app-action-sheet.md).
- Favorite selection and long-press participate in the Home-wide upward-drag arbitration defined in [navigation.md](../navigation.md). An upward drag that takes ownership does not launch the favorite, open its action sheet, or produce the favorite's long-press feedback.
- A favorite is removed automatically only after a successful inventory refresh confirms that its application was uninstalled, its clone was removed, or that specific launchable identity permanently disappeared.
- An inventory loading failure never deletes or hides saved favorites.
- An application that still exists but is disabled remains in its stored position as a visibly disabled favorite. Selecting it does not attempt a normal launch and provides a short localized unavailable Toast. Long-press remains available for applicable information or management actions.
- A transient launch failure retains the favorite, provides a short localized error Toast, and refreshes that entry's state.
- A later successful refresh restores an available entry in place without changing its favorite order.

### Favorite persistence loading and failure

- A favorite persistence read is successful only when Avenor can reliably interpret the complete stored favorite state. An unreadable or damaged result is not an empty favorite list and must not show the new-installation empty state.
- Loading, unreadable, and fully empty favorite states are shared states of the complete favorite composition: they span the primary and companion areas and are presented once rather than duplicated in both groups. A group with no entries inside an otherwise readable non-empty state does not trigger the shared empty state; it preserves its portion of the composition without stretching items or creating fixed empty slots.
- While the initial read or a user-requested retry is in progress, the favorite region shows a progress indicator and the localized message `Loading favorites…`. It does not show favorite rows, the empty-state invitation, an error icon, or a Retry action concurrently.
- If the state cannot be reliably read, the favorite region hides the progress indicator, shows a non-interactive `40dp` error icon, displays the localized message `Unable to load favorites`, and provides a separate `Retry` action.
- The failure state preserves the original unreadable data and must not initialize, migrate, repair, clear, replace, or otherwise overwrite it. Process recreation and device restart do not reinterpret the failure as an empty list.
- While the failure or retry state remains active, all add-favorite, remove-favorite, and edit-mode mutations are disabled. Home time and date, Home-to-Drawer navigation, Drawer inventory, and application launching remain available.
- Selecting Retry starts one read-only reload, changes the favorite region to the loading presentation, and disables repeated Retry activation until that attempt completes. Retry does not write, repair, migrate, clear, or replace stored favorite data.
- A successful retry restores the exact readable favorite state and its order without an additional write. A reliably read empty state shows the normal empty-state invitation and restores favorite mutations.
- A failed retry returns to the same persistent failure presentation, restores the Retry action, preserves the original unreadable data, and keeps favorite mutations disabled. It does not add a Toast because the visible favorite-region error already communicates the result. The user may retry again.
- A normal initial read occurs on a new process start. Avenor does not continuously retry, poll, or use network-state changes as a retry trigger. Any additional safe read trigger requires technical validation and must preserve the same no-write boundary.
- If a favorite mutation surface is already open when the failure is detected, close it without applying a favorite change. Repair, export, reset, backup, and restore behavior require a later product decision and technical assessment.

## Edit mode

- Edit mode is entered from the selected favorite's Launcher actions and is available whenever that action sheet can open for a favorite. The action is labeled as editing rather than ordering because the mode supports both ordering and group assignment.
- In edit mode, every favorite displays a drag handle. Each visible Home module receives the shared translucent light-gray, small-rounded-corner editing surface defined in [design-foundations.md](../design-foundations.md). In the current product this means the basic-information and favorite-composition modules; a zero-height bottom secondary region has no surface. These surfaces communicate module boundaries and do not imply that the basic-information module is editable.
- Edit mode preserves the normal-mode favorite item heights: primary items remain `56dp` high and companion items remain `48dp` high. Both groups use the same `24dp` drag-handle graphic. Each handle's hit region is `24dp` wide plus `12dp` horizontal padding on each side (`48dp` total) and follows the current item's full height (`56dp` for primary, `48dp` for companion). The graphic has no additional right margin; visible spacing comes from the item's existing padding. Handles are contained within those rows and must not increase row height or stretch, compress, or remeasure either group's list.
- A handle drag uses an independent follow-pointer preview. The preview preserves the grab offset and moves continuously across the complete Home safe content area, including both groups and the space between them. Only the primary and companion regions accept a drop; other Home areas allow the preview to pass through but do not accept it. The dragged favorite does not participate in list-position animation, and target changes or other-item animations must not move, jump, or rebound the preview.
- The source favorite's original layout slot remains stable for the entire drag and preserves the source row height, so the list does not shrink, the shared viewport does not change, and other favorites do not shift because the source was temporarily removed. Before an exchange target is selected, the slot may retain an inert representation of the source; when an in-group exchange target moves there, that target replaces or covers the inert source representation. The slot never contains two interactive favorites. In-group exchange may update visible list positions during the drag; cross-group exchange and insertion show target feedback without changing either group's visible list until release. Saved state is finalized only when the current valid operation is released.
- While the touch point remains in the source group, the only operation is exchange. Entering another favorite's effective body makes that favorite the exchange target; the target favorite immediately exchanges positions with the source favorite during the drag, while the independent preview remains controlled by the pointer and does not join the target animation. If the touch point changes to another eligible body, the visible exchange updates to that target. Gaps in the same group never show an insertion line and never accept insertion. Touching the dragged favorite, the source position, or an invalid area produces no new exchange.
- When the touch point enters the other group, two mutually exclusive target types are available. An existing favorite body means cross-group exchange: the target receives an exchange highlight or border without an insertion line, but neither group changes its visible list position or assignment during the drag. A valid gap means cross-group insertion: an insertion line marks the target boundary, no exchange border appears, and neither group changes until release. On release, the current cross-group exchange or insertion is applied once; insertion removes the dragged favorite from the source group and places it at the boundary while preserving the target group's relative order. The first boundary, between-item boundaries, last boundary, and an empty target group are all valid insertion targets.
- For example, with primary `[A, B, C]` and companion `[D, E, F]`, releasing `A` over `E` exchanges the entries and produces primary `[E, B, C]` and companion `[D, A, F]`. Releasing `A` over the gap between `D` and `E` instead inserts it and produces primary `[B, C]` and companion `[D, A, E, F]`.
- Target classification uses the finger touch point, not the preview center or edge. The preview may cover a target visually without changing its type. Moving from an exchange body to an insertion gap switches the border to an insertion line; moving back switches to exchange feedback. No target switch changes saved data. During edge auto-scroll, feedback is recalculated from the current target-group geometry and remains tied to the touch point; scrolling or group switching must not cause an unexplained target jump.
- In-group exchange updates the visible positions as the touch point enters eligible favorite bodies. Cross-group exchange and insertion remain feedback-only until release and do not change either group's visible list during the drag. On release over the current valid target, apply the active operation once and save it. An invalid release restores the original source position and group without changing saved state. The dragged preview remains independent; in-group exchange transitions may animate the target favorite, but cross-group feedback must not move the preview or either list. If persistence fails, restore the last successfully saved group assignments and order for favorites whose identities remain valid, then retain any newer confirmed inventory reconciliation result. Failure recovery must not restore an application, clone, or launchable identity that a later inventory event confirmed as removed or invalid. Do not present the operation as completed, and show the existing favorite-update failure feedback.
- System Back is the only current exit from edit mode. If Back occurs during an active drag, first cancel that drag, remove its preview and target feedback, restore the last saved visible group and order, and invalidate any later release from that gesture so it cannot save. Exiting then removes the editing surfaces and drag handles without undoing moves completed before Back.
- The editing surfaces and visible drag handles are the current indications that edit mode is active. No additional title, banner, completion button, or exit action is defined.

- A drag beginning on a favorite's handle owns item movement. A vertical drag elsewhere inside a favorite group scrolls that group when it overflows. Edit mode never changes pages or reveals a collapsed group.
- Releasing a dragged favorite finalizes the current in-group exchange, cross-group exchange, or cross-group insertion when the current target is valid, then immediately saves it. Cross-group release follows the mutually exclusive target, preview, invalid-drop, and save rules above.
- Edit mode disables Home-to-Drawer dragging. Item dragging, group scrolling, and automatic edge scrolling retain exclusive ownership until edit mode ends.

## Bottom secondary region

- The bottom secondary region is a product-definition boundary for a later favorite capability, not a persistent empty container.
- It exists and occupies height only when the user has created applicable favorite content. Because the current product defines no such content or creation behavior, its current height is zero.
- Unallocated transparent space below the visible modules is not the bottom secondary region and receives no module behavior or edit-mode surface.
- Content, selection, scrolling, ribbon, paging, sizing, and creation behavior require a later author decision and updates to the applicable Home and navigation contracts before this region can appear.

### Inventory changes during edit mode

Any inventory event that ends edit mode also cancels an active drag, removes its preview and target feedback, and does not commit an uncompleted move or swap.

| Inventory event | Current behavior |
| --- | --- |
| A new application is installed | Keep edit mode; favorites are unchanged |
| A non-favorite application is removed | Keep edit mode; favorites are unchanged |
| A favorite's name changes | Update the row in place and keep edit mode |
| A favorite's icon or platform badge changes | Update the row in place and keep edit mode |
| A favorite application is uninstalled | End edit mode, remove the favorite, preserve already saved moves, and notify the user |
| A favorite clone is removed | End edit mode, remove that clone favorite, preserve already saved moves, and notify the user |
| A favorite application is disabled | End edit mode, retain it as a disabled favorite in its saved position, preserve already saved moves, and notify the user |
| Favorite identity can no longer be confirmed | End edit mode, retain the saved entry and order pending refresh, and notify the user |
| The full application inventory fails to load | End edit mode, retain all favorites and their saved order, and notify the user |
| The favorite count falls below two | Keep edit mode while at least one favorite remains; end it when no favorite remains |

- Use one localized message for a confirmed favorite change: `Favorites changed. Edit mode ended.`
- Use one localized message for an inventory or identity refresh failure: `Unable to update application status. Edit mode ended.`
- Present both messages as short Toasts.

## Acceptance intent

- Empty and populated favorite states preserve the same fixed Home structure. Primary and companion favorites remain part of one middle-screen composition and approximately follow the 55:45 horizontal hierarchy after the composition padding and inter-group gap. A group stays stationary when its content fits and scrolls independently when its content overflows.
- Loading, unreadable, and fully empty favorite states are presented once across the complete favorite composition; a single empty group does not replace the other group's readable content with a shared empty state.
- Outside edit mode, Home-to-Drawer dragging remains available from the favorite region according to its scroll-boundary handoff and from every other Avenor-managed Home element without producing the element's click or long-press action.
- Primary and cloned entries remain visibly distinguishable when the platform supplies a badge; Avenor-specific fallback distinction is outside the current scope.
- Returning to Home during the same process preserves its meaningful list state; process recreation follows [navigation.md](../navigation.md).
- An unreadable favorite state remains distinguishable from a valid empty list, cannot be overwritten through favorite actions, and can recover through a successful read-only Retry without blocking independent application discovery and launch paths.
