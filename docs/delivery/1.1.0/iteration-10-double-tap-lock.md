# Iteration 10: Double-tap lock authorization loop

> Applies to [Avenor Launcher 1.1.0](delivery.md). This record plans one product increment and does not authorize implementation, credentials, publication, or Git/remote actions.

## Status

- Value: `Completed`
- Updated: 2026-08-16
- Basis: Physical-device validation confirmed the debug-only implementation path satisfies the product boundary. [ADR-0004](../../decisions/0004-purpose-limited-accessibility-service-for-double-tap-lock.md) records the accessibility-service constraint required before mainline integration.

## Objective

Allow the author to opt into Avenor's purpose-limited accessibility service and request one Android system lock action by double-tapping eligible blank space on Home, without gating or observing any independent Launcher path.

## Product and version references

- [1.1.0 delivery](delivery.md)
- [Double-tap lock](../../product/features/double-tap-lock.md)
- [Home](../../product/surfaces/home.md)
- [Settings](../../product/surfaces/settings.md)
- [Navigation](../../product/navigation.md#home-to-drawer)
- [Product foundation](../../requirements/product-foundation.md)
- [Privacy and prominent disclosure](../../product/features/privacy.md)

## Observable outcome

Settings reports Android's actual service state and explains the capability. After the author reviews the confirmed local privacy information and prominent disclosure, explicitly continues to system accessibility settings, and enables the service there, a valid double tap entirely within eligible Home basic-information blank space requests one lock action. Revocation or failure affects only this capability.

## Included work

- A Settings item labeled `Double-tap to lock`, with actual `On` or `Off` state and return-time refresh.
- The local explanation surface, system-settings handoff, and the separate prominent disclosure with `Cancel` and `Agree and continue`.
- A confirmed local Privacy presentation that describes the current product data boundary and the accessibility service's sole lock purpose, data-access behavior, authorization, and disable path.
- A purpose-limited accessibility service that performs no global action other than the explicit user-triggered lock request and does not request window-content retrieval.
- Double-tap recognition only in eligible basic-information blank space, using platform timing and movement tolerance and respecting the contracted gesture ownership and excluded targets.
- Fail-closed handling for disabled, revoked, disconnected, unavailable, or failed service/action state, including the contracted localized feedback.
- English, Simplified Chinese, and English fallback resources for included UI and disclosure content.

## Excluded work

- Device Administrator fallback, background-triggered locking, screen-content retrieval, other global actions, behavioral observation, analytics, or automation.
- An Avenor-owned permission toggle, first-launch permission request, automatic settings navigation, or retained disclosure-acknowledgement history.
- Store publication, a claim of store-policy approval, or a legal or specialist conclusion not supported by an applicable review.
- Any accessibility-service purpose beyond the current double-tap lock contract.

## Technical change areas

Accessibility-service declaration and configuration, service-state observation, lock-action adapter, Settings navigation and disclosure state, Privacy content presentation, Home hit testing and gesture arbitration, resources, and focused tests. Exact code organization remains a technical choice; any broader permission, data, background, or service boundary requires author direction and applicable specialist review.

## Dependencies and sequence

Depends on the completed Iteration 9 Settings navigation and state-refresh foundation. The author-approved local Privacy and prominent-disclosure text is now defined by the linked product contract. Focused technical validation may occur before an ADR exists and must prove the minimum AccessibilityService declaration, exact state detection, lock-screen operation, fail-closed behavior, supported API behavior, data boundary, and implementation alignment with that text.

After those boundaries are supported by evidence and before the service is integrated into the `1.1.0` mainline, an ADR must record the service's sole purpose, permission and manifest boundary, explicitly prohibited behavior, fail-closed rules, relationship to Privacy and prominent disclosure, and the requirement to re-review future expansion or store distribution. Do not create an empty ADR before the evidence exists. If validation requires broader permission, data processing, background behavior, or product scope, stop and request author direction rather than integrating the finding.

## Migration and compatibility impact

No favorite-data migration is planned. The service is disabled until the user enables it in Android settings. Installation or upgrade must not enable it automatically, and absence or revocation of authorization must preserve Home, Drawer, application launching, and Settings.

## Security, privacy, permission, and licensing impact

This iteration introduces an accessibility-service capability and therefore requires explicit least-privilege, privacy, security, disclosure, and future distribution-policy review. The service must not read window content, observe other applications for product behavior, collect accessibility events for analytics, or use Device Administrator. No monitoring SDK or network service is authorized by this iteration.

## Risks and unresolved decisions

- OEM accessibility settings, service connection, and lock-action behavior require device evidence across the supported baseline.
- Gesture recognition must not delay Clock or Calendar selection or conflict with Home-to-Drawer dragging, long press, edit surfaces, system insets, favorites, or other targets.
- Implementation must reproduce the current author-approved Privacy and prominent-disclosure text without weakening, merging, or bypassing its separate presentation boundaries.
- Future store distribution requires a fresh platform-policy and disclosure review even if the GitHub-distributed build is accepted.
- ADR-0004 now records the evidence-supported AccessibilityService boundary required before any mainline integration. The completed result remains debug-only and does not claim mainline integration.

## Validation plan

The iteration behavior must be accepted on the author-designated primary physical device as part of the version journey. Additional recommended scenarios include service never enabled, enabled, revoked, disconnected, and action failure; `Cancel` and `Agree and continue` disclosure paths; system-settings handler failure; return-time state refresh; valid and invalid double taps; time, date, favorite, edit-surface, inset, drag, long-press, and cancellation exclusions; process recreation; offline use; English, Simplified Chinese, and fallback resources; API 31; one additional API 36 or API 37 physical device; and additional OEM/profile coverage. Unperformed recommended evidence remains `Unknown`, `Not run`, or `Unavailable` and does not by itself block the version. A performed included-path failure cannot be ignored because its scenario was recommended.

## Acceptance evidence

The project author accepted the debug implementation on the designated primary physical device. The completion result records the accepted journey and device environment; the focused technical validation record contains the pre-ADR evidence and remaining recommended scenarios. The approved disclosure revision, manifest/service boundary, source/build identity, initial and resulting service state, gesture location and outcome, system handoff, failure behavior, regressions, and skipped scenarios remain the applicable evidence fields.

## Related decisions, commits, and tags

- [ADR-0004: Purpose-Limited AccessibilityService Boundary for Double-Tap Lock](../../decisions/0004-purpose-limited-accessibility-service-for-double-tap-lock.md) records the service's sole purpose, manifest and data-access boundary, explicitly prohibited behavior, fail-closed rules, relationship to Privacy and prominent disclosure, and the requirement to re-review for store distribution.
- [Focused backup and AccessibilityService technical validation](focused-technical-validation.md) records pre-ADR evidence and confirms the remaining runtime gaps are satisfied by the ADR boundary.

## Final result

The iteration has completed with physical-device evidence supporting the product boundary. The debug implementation successfully demonstrates double-tap lock on Home, Settings state and disclosure flow, service state detection, and fail-closed behavior. ADR-0004 now establishes the architectural constraint required for mainline integration.

## Remaining issues and handoff

The accessibility service remains debug-only and is not yet integrated into the mainline. Mainline integration requires an explicit author decision, a build-configuration change to move the service from `src/debug` to the main manifest with identical boundary constraints, and version-delivery coordination. Store distribution would require a fresh platform-policy and disclosure review even after mainline integration.
