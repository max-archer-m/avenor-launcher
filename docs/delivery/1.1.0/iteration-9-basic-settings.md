# Iteration 9: Basic Settings loop

> Applies to [Avenor Launcher 1.1.0](delivery.md). This record plans one product increment and does not authorize implementation or Git/remote actions.

## Status

- Value: `Planned`
- Updated: 2026-08-13
- Basis: The author selected basic Settings capability for `1.1.0`; production implementation has not yet been authorized.

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

The author opens Settings from the fixed Drawer gear, sees the current default-home state, opens the system default-home settings and observes refreshed state on return, reads the local Avenor License, opens the project repository through the system browser, sees the exact application version, and returns to the preserved Drawer position.

## Included work

- Fixed Settings gear destination and Settings navigation/restoration.
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

Drawer index destination, navigation state, Settings UI/resources, default-home platform intent and state query, local license asset presentation, repository URI invocation, and build-version display. Exact navigation and asset-loading structure remain implementation choices.

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

Recommended scenarios include gear entry; Back and position restoration; default/not-default states; system destination return and cancellation; unavailable destination; English, Simplified Chinese, and fallback locale; offline license reading; long-content scrolling; repository browser success/failure; exact `1.1.0(2)` display; process recreation; and regression of Home/Drawer/action sheets. Unless promoted, these are not automatic iteration gates.

## Acceptance evidence

No implementation evidence exists. When performed, record executor, source/build identity, device/API/OEM, initial default-home state, system handoff result, displayed version, local content behavior, browser result, regressions, and skipped scenarios.

## Related decisions, commits, and tags

- No new ADR, implementation commit, or tag exists for this iteration.

## Final result

No final result exists while the iteration is `Planned`.

## Remaining issues and handoff

Development should validate the OEM system destination before fixing implementation behavior. After author acceptance, the version still requires the completion evidence in [delivery.md](delivery.md); iteration completion alone does not complete `1.1.0`.
