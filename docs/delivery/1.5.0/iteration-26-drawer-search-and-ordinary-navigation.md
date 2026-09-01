# Iteration 26: Drawer Search and Ordinary Navigation

> Applicable version: [Avenor Launcher 1.5.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation or Git/release action.

## Objective

Make ordinary Drawer application discovery faster through local name search while establishing the revised top app bar, final Settings row, and Drawer-specific action-sheet boundary.

## Product and version references

- Product-contract baseline: `48d5bd470c84d222b6e89e128f438da1f25e595b`
- [Drawer behavior](../../product/surfaces/drawer.md#top-app-bar)
- [Drawer search](../../product/surfaces/drawer.md#application-search)
- [Drawer presentation](../../product/presentation/drawer.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Navigation](../../product/navigation.md)
- Applicable version contract: [delivery.md](delivery.md)

## Observable outcome

The author enters search in ordinary Drawer, filters the reliable local inventory by displayed name without changing its order, launches the intended result, cancels back to the prior ordinary position, uses the fixed Settings row for Settings entry, and sees no obsolete Drawer Launcher-action region.

## Included work

- The ordinary Drawer top app bar and search-mode boundary selected from the linked Drawer contracts. Iteration 27 adds the complete actionable display-settings entry and panel together.
- Contracted local-name search, result projection, transient lifecycle, inventory refresh, accessibility, and empty/failure states.
- Contracted section/index behavior and ordinary Settings navigation across search and non-search modes.
- Drawer-sourced application-action-sheet content aligned with the linked source policy.
- Ordinary Drawer position, Home return, external-launch return, and failure preservation needed by the selected journey.

## Excluded work

- Display-setting panel behavior and configurable Drawer presentation, which belong to Iteration 27.
- Package-name, metadata, fuzzy, pinyin, initials, or relevance-ranked search.

## Technical change areas

Drawer mode/state model, top bar, text input and matching, filtered section/index projection, Settings row navigation, action-sheet source policy, inventory updates, accessibility semantics, resources, and tests.

## Dependencies and sequence

Relies on the existing reliable inventory and navigation model. It establishes the ordinary Drawer shell required by Iteration 27 without exposing a nonfunctional settings control. It has no product dependency on completion of Home Iterations 22–24 beyond an accepted prior-version baseline and shared integration compatibility.

## Migration and compatibility impact

The iteration adopts the linked contract's transient-search and ordinary-navigation changes. Existing ordering, identities, favorite/Home state, and Settings content remain compatible; this record does not redefine their product behavior.

## Security, privacy, permission, and licensing impact

Search remains local, uses only displayed reliable inventory, creates no usage history, and requires no new permission, network access, dependency, or license impact. Privacy text must remain accurate.

## Risks and unresolved decisions

Locale normalization can diverge from Drawer ordering; keyboard Back can be confused with exit search; live inventory can invalidate filtered anchors; source-specific action-sheet content can regress Home actions.

## Acceptance criteria

- Empty, matching, clearing, cancellation, Back/IME, no-result, recreation, and inventory-change scenarios match the linked Drawer search contract without changing the reliable application order.
- Search results launch the intended current identity and do not retain stale targets or create query/usage history.
- Section/index projection, Settings navigation, ordinary-position restoration, and external-return behavior match the linked Drawer and navigation contracts.
- Drawer and Home action sheets each retain the source-specific actions defined by the linked action-sheet contract.
- The complete selected discovery journey remains local and offline with no unresolved material contract mismatch.

## Validation requirements

Recommended evidence covers empty/whitespace/Latin/diacritic/Han queries; no result; clear/cancel/Back/IME; keyboard and focus; match emphasis and ellipsis; inventory changes; anchors/index; Settings row; launch success/failure; Home/Drawer action-sheet differences; locale and accessibility; recreation; and ordinary navigation regression.

## Related decisions and technical assessments

No new decision or assessment is selected. Escalate only if evidence requires a consequential inventory, normalization, or navigation architecture change.
