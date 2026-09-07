# Iteration 30: Drawer Drag-to-Favorite

> Applicable version contract: [1.6.0 delivery](delivery.md). This contract defines the authorized delivery boundary only. It does not own execution state, evidence, commits, or results, and it does not by itself authorize production implementation, a commit, a push, a tag, or any release action.

## Objective

Deliver the Drawer drag-to-favorite journey exactly as defined by the accepted product contract: the author long-presses an eligible application in ordinary Drawer or search results, drags it through the programmatic Drawer-to-Home transition into Home edit mode, and releases it on an edit-mode destination to add it to favorites in one atomic operation, with the contracted gesture boundaries, eligibility rules, feedback, cancellation, and interruption behavior.

## Product and version references

- Product-contract baseline: `c0a1a44e38cf38919fbb3e9cf10dc75970081764` — the commit that integrates the accepted 2026-09-07 drag-to-favorite contract revision across the Drawer, Home, application-action-sheet, and product-foundation documents.
- Applicable product documents:
  - [Drawer behavior](../../product/surfaces/drawer.md), including the Drag-to-favorite section
  - [Home behavior](../../product/surfaces/home.md), including the Drag-to-favorite journeys section
  - [Application action sheet](../../product/surfaces/app-action-sheet.md)
  - [Navigation](../../product/navigation.md)
  - [Privacy and data handling](../../product/features/privacy.md)
- Applicable version contract: `delivery.md`

## Observable outcome

On the primary device, in ordinary Drawer and in search results: releasing after the long-press haptic opens the application action sheet, while moving beyond the touch slop on a drag-eligible row lifts a real-time preview of that row, slides Drawer away programmatically, and brings Home into collapsed-panel edit mode with its list position preserved. Dropping on an edit-mode destination adds the application to favorites in one atomic operation and exits edit mode immediately; invalid releases, cancellation, and Back leave normal Home without a mutation and without returning to Drawer. Already-favorited and reliably disabled rows show their contracted toasts and still open the action sheet on release.

## Included work

- The Drawer long-press gesture split: haptic, release-opens-action-sheet, movement-beyond-touch-slop-begins-drag on drag-eligible rows, with the action sheet and `600ms` rapid-activation behavior otherwise unchanged.
- Drag-eligibility evaluation per row: ordinary Content and search mode eligible; favorite multi-selection, Loading, and Error ineligible; the Settings row never participates; an identity already in any favorite module or reliably disabled is not drag-eligible.
- The two ineligible-row toasts with localized English and Simplified Chinese strings, and the unchanged release-opens-action-sheet result on those rows.
- The non-interactive drag preview built from the current real-time Drawer-row snapshot, retaining source presentation until handoff, with no haptic beyond the one long-press haptic.
- The programmatic downward Drawer-to-Home transition with the Drawer list layout unchanged during the transition, and Home entering collapsed-panel edit mode with its logical main-list scroll position preserved, add-favorite entries visible, and no entry feedback.
- Destination resolution and candidate feedback exactly through the existing Home edit-mode rules, including insertion boundaries in destination modules and a main-list add-favorite entry creating that entry's module type with the application as its first favorite.
- The one atomic favorite mutation with Home edit-mode persistence semantics, save-failure restoration plus the localized `Unable to save favorite` toast, and edit-mode exit in the same state change as the completed operation regardless of outcome, with success silent.
- The cancellation and interruption paths: invalid release, pointer cancellation, and Back leaving normal Home without a mutation and without returning to Drawer; system Home and external interruptions following the edit-mode interruption rules.
- The search-mode journey path that clears the transient query and search mode under the existing search-exit rule.
- Drag-start activation suppression for that pointer sequence and the existing latest-removal Undo snapshot invalidation through the Home rule.
- Focused test sources covering eligibility and toast paths, journey lifecycle and one-shot exit, destination resolution, atomic save success and failure, cancellation and interruption paths, and search-mode entry.

## Excluded work

- Drag-to-favorite from Home normal mode; Home long-press and edit-mode movement behavior are unchanged.
- Any new presentation token or wireframe change: the preview derives from the existing Drawer-row presentation, and no exact value outside current sources is introduced.
- The third favorite-module type (column favorites) and any other favorites-model change.
- The Iteration 29 Settings backup and restore scope.
- The `1.6.0` version identifier update (`versionName`/`versionCode`), which remains a version-level closure concern unless a later authorized amendment assigns it here.
- Style-edit dialog shadow and frosted-glass presentation changes; UI-animation and experience-polish work.

## Technical change areas

- Drawer gesture layer: per-row long-press detection with touch-slop movement discrimination, eligibility evaluation against the current favorite assignment and inventory reliability state, and ineligible-row toast dispatch.
- Journey state: a one-shot drag-to-favorite journey state connecting Drawer and Home edit mode, carrying the dragged identity and surviving the programmatic transition; it must not leak into ordinary edit-mode entry or a second journey.
- Navigation transition: a programmatic downward Drawer-to-Home transition path distinct from gesture-driven dismissal, coordinated with the journey state.
- Home edit mode: programmatic entry with preserved logical scroll position, and destination resolution and preview handoff for a moving identity that originates outside every Home source order.
- Favorites store: atomic insertion of one external identity with possible new-module creation under the existing versioned atomic persistence; no schema change.
- Resources: three new user-visible strings in English and Simplified Chinese with complete name parity.
- Tests: focused local and instrumentation sources for the behaviors above.

## Dependencies and sequence

This iteration depends only on the accepted drag-to-favorite contract and the current integrated mainline. Its code areas overlap Iteration 29's Settings scope only in shared state stores and strings resources, so the two iterations must not run concurrently on one line; their relative order is otherwise free.

## Migration and compatibility impact

- No schema change: a drag-created module is an ordinary module that enters the existing persistence and the backup file content defined by the Settings contract.
- No change to the one-time Home-model adoption reset, the adoption rules, or the Android cloud-backup boundary.

## Security, privacy, permission, and licensing impact

- No new permission, networking, account, or external service is introduced; the journey processes only locally available inventory and favorite state.
- No Privacy copy change is required; if implementation reveals a material divergence from the Privacy statement, stop and obtain author direction.

## Risks and unresolved decisions

- Gesture discrimination must not regress the existing action-sheet entry, the `600ms` rapid-activation window, or ordinary scrolling.
- Preserving the Home main-list logical scroll position across the viewport change caused by the edit dock requires care; a wrong restoration surfaces as a jump or a misresolved drop target.
- The preview must stay non-interactive and stationary at the release position while the save is unresolved, and the success handoff must occur in one display update.
- Back during the programmatic transition is a timing-sensitive boundary between Drawer and Home ownership and must resolve to the contracted cancellation result.
- The drag-eligibility read and the save must see the same favorite state; an inventory or favorite change mid-journey must resolve through the existing reliability and atomicity rules rather than a new path.

## Acceptance criteria

- In ordinary Content and search results, the long-press haptic followed by release opens the action sheet; movement beyond the touch slop on a drag-eligible row begins the journey; ineligible rows show the contracted toast and still open the action sheet on release.
- The journey slides Drawer away programmatically, enters collapsed-panel Home edit mode with the logical scroll position preserved and add-favorite entries visible, and shows no entry feedback.
- Destination resolution, candidate feedback, edge auto-scroll, and save semantics match the Home edit-mode rules, including new-module creation from a main-list add-favorite entry.
- A valid release commits one atomic favorite mutation, exits edit mode in the same state change, and shows no success feedback; save failure restores the last reliably persisted complete favorite state and shows the contracted toast.
- Invalid release, pointer cancellation, and Back leave normal Home with edit mode exited, no mutation, and no return to Drawer; system Home and external interruptions follow the edit-mode interruption rules.
- A search-mode journey clears the transient query and search mode; the next ordinary Drawer entry starts outside search.
- All three new user-visible strings exist in English and Simplified Chinese with complete name parity.
- No regression of ordinary Drawer launch, search, action sheet, or Home edit-mode movement behavior.

## Validation requirements

Recommended scenarios unless explicitly promoted:

- Device journeys on the primary physical device: drag into an existing module boundary, drag onto both main-list add-favorite entries to create each module type, and verify the saved result and one-shot edit-mode exit.
- Toast paths for an already-favorited identity and a reliably disabled identity, including the release-opens-action-sheet result.
- Cancellation paths: invalid region, pointer cancellation, Back during the drag and during the transition, system Home, and an external interruption.
- Save-failure path with a controlled write failure, verifying restoration, toast, and edit-mode exit.
- Search-mode journey: enter search, start a drag, verify query and mode clearing and the next ordinary Drawer entry state.
- Regression: ordinary long-press action sheet, application launch, `600ms` window, scrolling, and Home edit-mode movement.
- String parity between English and Simplified Chinese resources.

## Related decisions and technical assessments

- [ADR-0002: versioned atomic file for favorites](../../decisions/0002-use-versioned-atomic-file-for-favorites.md) — the persistence model the atomic favorite mutation must preserve.
- [Privacy and data handling](../../product/features/privacy.md) — the local-only data boundary the journey operates within.
- [Product decision and scope-change governance](../../product-decisions.md) — the author's 2026-09-07 decision record for this feature and its contract revision.
