# Iteration 12: Loading and Home Return Foundation

> Applicable version: [Avenor Launcher 1.2.0 Delivery](delivery.md).
>
> This stable contract defines selected delivery scope. Iteration status, actual evidence, commits, artifacts, and results belong only in the sibling version delivery record. This document does not authorize production implementation or any Git or release action.

## Objective

Establish predictable application-data loading and Launcher return behavior so usable Home state is not replaced by unnecessary blocking reloads and system Home or external-application return reaches the correct Avenor surface.

## Product and version references

- [1.2.0 delivery](delivery.md)
- [Navigation](../../product/navigation.md)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Validation guide](../../validation.md)

## Observable outcome

Cold start and process recreation still perform the required initial reads. Re-entering or navigating within an already alive Avenor process reuses valid state where the product contract requires it. The Android system Home action displays Avenor Home from any Avenor surface when Avenor is the default Launcher, and same-process return from an ordinary external application displays Home without restoring a transient surface or imposing a new blocking initial read.

## Included work

- Identify and correct duplicate favorite or application-inventory loads caused by ordinary same-process surface navigation or return.
- Distinguish cold process start, process recreation, same-process return, explicit Retry, package/profile changes, and Settings-related system-state changes.
- Preserve required refresh and reconciliation for installed, removed, enabled, disabled, renamed, cloned, or profile-scoped launchable entries.
- Apply the contracted system Home behavior independently from Avenor Back and Drawer transition behavior.
- Apply the contracted external-application excursion and same-process return behavior from applications launched through Home or Drawer.
- Preserve usable Home content and meaningful favorite-group positions on same-process return; reset only the transient surfaces excluded by the navigation contract.
- Keep Loading, Error, Retry, and unreadable-persistence behavior consistent with the current Home and Drawer contracts.

## Excluded work

- Primary/companion favorite layout, migration, and editing.
- Home date alignment.
- Home–Drawer gesture, opacity, settle-animation, nested-scroll, or index-anchor changes.
- Continuous background polling, network-triggered reload, cloud synchronization, or a speculative general-purpose caching framework.

## Technical change areas

- Launcher lifecycle and Avenor surface-state ownership.
- Favorite-persistence and application-inventory load triggers.
- Package/profile update reconciliation.
- External-application launch return handling and system Home handling.
- Loading, error, retry, and process-recreation tests or seams.

## Dependencies and sequence

The accepted `1.1.0` daily-use baseline is the entry baseline. This iteration is the first currently planned `1.2.0` increment and establishes state behavior used by Iterations 13 and 14. It does not authorize their implementation.

## Migration and compatibility impact

No favorite data-format change is selected. Existing readable favorites and order must remain intact. Unreadable favorite state must remain preserved and must not be interpreted, repaired, cleared, or overwritten as an empty state. Required package and profile updates must not be suppressed merely to avoid duplicate loads.

## Security, privacy, permission, and licensing impact

No new permission, network access, user-data category, external service, dependency, or license impact is selected. Existing least-privilege, profile, Private Space, backup-exclusion, and local-data boundaries remain unchanged.

## Risks and unresolved decisions

- The exact source of duplicate loads and frame blocking requires implementation evidence; this contract does not assume a particular architecture.
- Lifecycle deduplication can produce stale inventory if required package/profile reconciliation is accidentally removed.
- Same-process return and process recreation must remain distinguishable under Android and OEM lifecycle behavior.
- A consequential change to state ownership or persistence architecture requires author direction and an ADR when applicable.

## Acceptance criteria

- Cold process start and process recreation perform the required initial reads and begin on Home.
- Ordinary Home, Drawer, Settings, and action-sheet navigation does not start a redundant blocking initial read.
- When Avenor is the selected default Launcher, the Android system Home action from Drawer and Settings displays Home without being treated as Back.
- Returning from an ordinary external application in the same process displays Home, does not restore Drawer, Settings, an action sheet, edit mode, or an in-progress transition, and does not replace usable Home content with Loading.
- Same-process return preserves readable favorite state and each group's meaningful Home scroll position.
- Explicit Retry and confirmed application/profile changes still perform the required read or reconciliation.
- A loading or reconciliation failure does not clear readable or unreadable saved favorites and does not crash or create a navigation dead end.

## Validation requirements

Recommended focused scenarios include cold start, process recreation, Home–Drawer–Home, Settings–Back, system Home from Drawer and Settings, launch from Home and Drawer followed by same-process return, process death during an external excursion, explicit Retry, and application install/remove/enable/disable/rename or clone/profile change. Record load-trigger observations when practical.

Relevant automated checks, an installable debug build, and author observation on the designated primary device are recommended. Actual results belong in `delivery.md`; no check is recorded as run by this contract.

## Related decisions and technical assessments

No additional decision or technical assessment is currently required. Add one only if implementation evidence exposes a consequential state-ownership, persistence, permission, privacy, or architecture decision.
