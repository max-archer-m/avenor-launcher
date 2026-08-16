# Iteration 9: Basic Settings loop

> Applies to [Avenor Launcher 1.1.0](delivery.md). This record plans one product increment and does not authorize implementation or Git/remote actions.

## Status

- Value: `Completed`
- Updated: 2026-08-16
- Basis: The project author reported completing experience acceptance for this iteration on 2026-08-16. This record does not infer build, automated-test, source-identity, or device evidence that was not supplied.

## Objective

Deliver a coherent offline Settings loop for default-Launcher management and essential product information without expanding into diagnostics, privacy claims, or broader customization.

## Product and version references

- [1.1.0 delivery](delivery.md)
- [Settings](../../product/surfaces/settings.md)
- [Drawer](../../product/surfaces/drawer.md#alphabet-index)
- [Navigation](../../product/navigation.md)
- [Design foundations](../../product/design-foundations.md)
- [Avenor License](../../../LICENSE)

## Observable outcome

The author selects the Settings gear in AlphabetIndex and lands on the fixed Settings section at the end of Drawer without opening Settings, then selects its Settings row to open the page titled `Settings`. From there the author sees the current default-home state, opens the system default-home settings and observes refreshed state on return, reads the local Avenor License, opens the project repository through the system browser, sees the exact application version, and returns to the preserved Drawer position.

## Included work

- Fixed Settings section and clickable Settings row at the end of Drawer.
- AlphabetIndex Settings gear as an index anchor with normal index-step haptic feedback, a `11dp` graphic in a full `20dp` slot, and no direct Settings launch.
- Complete AlphabetIndex suppression during full-surface Drawer Loading and Error states.
- Settings navigation/restoration and the localized page title `Settings` / `设置`.
- Opaque Material 3 dark Settings surface and contracted item presentation.
- Default-home state and system settings handoff with refresh on return.
- Local offline Avenor License presentation.
- Project repository implicit browser action and unavailable-handler feedback.
- Noninteractive `v<version-name>(<version-code>)` display using actual build identity.
- English, Simplified Chinese, and English fallback resources for included UI.

## Excluded work

- Privacy entry and content, which use the now-confirmed product copy and are delivered with the Iteration 10 authorization loop rather than this Settings-foundation increment.
- Third-party License entry, inventory, and presentation.
- Manual language selection, logs, diagnostics, update checks, backup, reset, cloud synchronization, or information copy actions.
- Double-tap lock and any related setting.

## Technical change areas

Drawer Settings-section anchoring, AlphabetIndex state and haptics, navigation state, Settings UI/resources, default-home platform intent and state query, local license asset presentation, repository URI invocation, and build-version display. Exact navigation and asset-loading structure remain implementation choices.

## Dependencies and sequence

Depends on the accepted Drawer index, navigation, system-bar behavior, and build identity. Its completed Settings navigation and state-refresh foundation is an entry dependency for Iteration 10.

## Migration and compatibility impact

No user-data migration is intended. Existing Home, Drawer position restoration, favorite data, and application actions must remain compatible. During authorized version implementation and closure, `versionName` becomes `1.1.0` and the completed APK uses the next unused `versionCode` at its accepted artifact boundary.

## Security, privacy, permission, and licensing impact

Core Settings remains offline. The repository entry delegates URL handling to the system browser and does not preflight network state. The Avenor License presentation must faithfully use the repository `LICENSE`; it does not create a third-party-license conclusion. No signing secret or private data is displayed.

## Risks and unresolved decisions

- Default-home destinations and state reporting may vary by Android/OEM and require defensive handling.
- The repository URL must match the configured public project location.
- Long local license content must remain readable and dismissible without losing Settings position.
- Privacy and third-party-license work must not be implied by this reduced version scope.

## Validation plan

Recommended scenarios include tapping and sliding onto the AlphabetIndex gear; one normal index-step haptic response per gear-anchor change; landing on the final Settings heading and row without opening Settings; opening Settings only from that row; preserving the full `20dp` gear slot with an `11dp` graphic; hiding the entire AlphabetIndex in full-surface Loading and Error states; the `Settings` / `设置` page title; Back and Drawer-position restoration; default/not-default states; system destination return and cancellation; unavailable destination; English, Simplified Chinese, and fallback locale; offline license reading; long-content scrolling; repository browser success/failure; exact `1.1.0(2)` display; process recreation; and regression of Home/Drawer/action sheets. Unless promoted, these are not automatic iteration gates.

## Acceptance evidence

The project author reported completing experience acceptance for Iteration 9 on 2026-08-16. No build command or result, automated-test result, source/build identity, device/API/OEM details, or per-scenario execution record was supplied with that acceptance; this document makes no claim that any of those checks ran or passed.

## Related decisions, commits, and tags

- No new ADR is required by this clarification. No implementation commit or tag evidence was supplied for this record.

## Final result

Iteration 9 is complete on the basis of the author's reported experience acceptance. Completion records the accepted Settings loop and revised Drawer entry behavior; it does not certify unreported build, automation, or device validation and does not complete the `1.1.0` version.

## Remaining issues and handoff

Any later release closure still needs the applicable version-level evidence in [delivery.md](delivery.md), including evidence not supplied with this iteration acceptance. Iteration completion alone does not complete `1.1.0`, and the remaining Iteration 10 scope and Settings navigation boundaries are unchanged.
