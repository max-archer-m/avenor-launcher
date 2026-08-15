# Iteration 8: Application shortcuts

> Applies to [Avenor Launcher 1.1.0](delivery.md). This record plans one product increment and does not authorize implementation or Git/remote actions.

## Status

- Value: `Completed`
- Updated: 2026-08-16
- Basis: The implementation was completed and the project author reported that Iteration 8 acceptance passed.

## Objective

Expose and invoke platform-provided application shortcuts through the shared action sheet from both Home and Drawer while preserving the selected application identity and originating-surface state.

## Product and version references

- [1.1.0 delivery](delivery.md)
- [Application action sheet](../../product/surfaces/app-action-sheet.md)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)

## Observable outcome

Long-pressing an eligible application on Home or Drawer shows its platform-provided shortcuts between application identity and Launcher actions; selecting one invokes the matching platform action and closes the sheet. An application without exposed shortcuts retains the existing compact sheet.

## Included work

- Shortcut discovery for the exact selected launchable entry and profile exposed to Avenor.
- Shared shortcut-region presentation, icons and labels, conditional divider, invocation, dismissal, and source-position preservation.
- The same shortcut capability from Home and Drawer.
- Safe layout for the platform-returned count under the current no-dedicated-overflow contract.

## Excluded work

- Creating, pinning, editing, ranking, recommending, or persisting Avenor-owned shortcuts.
- A new overflow product interaction or shortcut-specific success/failure UI.
- Uninstall behavior changes, Settings, double-tap lock, or Private Space access.

## Technical change areas

Platform shortcut adapter, launchable/profile identity mapping, shared action-sheet state and layout, invocation boundary, resources, and focused tests. Supported platform APIs and filtering must be validated from current project constraints rather than inferred from `1.0.0` assessment prose.

## Dependencies and sequence

Depends on the accepted `1.0.0` shared action sheet and stable launchable/profile identity model. It has no functional dependency on Iteration 7 or Iteration 9.

## Migration and compatibility impact

No stored-data migration is intended. Existing favorite, Drawer ordering, action-sheet Launcher actions, and origin restoration must remain compatible. Shortcut availability may legitimately differ by application, profile, device, and platform state.

## Security, privacy, permission, and licensing impact

Use only platform-exposed shortcut capability within the current least-privilege boundary. Any new manifest permission, visibility expansion, sensitive profile access, or dependency requires explicit review before adoption.

## Risks and unresolved decisions

- The platform may expose no shortcuts or different shortcut sets for primary, clone, and work-profile identities.
- Invocation must never target a different profile or application identity.
- Unusually many shortcuts have no dedicated overflow design; the implementation must remain safe without inventing one.
- Product-specific failure UI is intentionally absent; a material usability failure must return to product review.

## Validation plan

Recommended scenarios include Home and Drawer origins; no/one/multiple shortcuts; primary and any available cloned/work-profile entry; correct label/icon; invocation; Back and scrim dismissal; unavailable or changed shortcut state; large counts; and preservation of Drawer anchor, Home order, and Launcher actions. These are not automatic gates unless promoted.

## Acceptance evidence

- On 2026-08-16, the project author reported that the Iteration 8 changes passed acceptance.
- The accepted implementation covers platform shortcut discovery for the exact selected application identity and profile, shared Home and Drawer presentation, invocation and dismissal, omission when unavailable, and adaptive scrolling for larger shortcut sets.
- Focused UI tests cover exact shortcut presentation/invocation, the absent-shortcut state, application-to-controller identity flow, dismissal, and scrolling to the end of a larger shortcut set; these tests were added but were not run in this completion turn.
- The author did not provide the build identity, device/API identity, detailed scenario log, clone/work-profile evidence, or separate automated-command results in this acceptance report; those details remain `Unknown` and no additional result is inferred.

## Related decisions, commits, and tags

- [ADR-0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)
- The implementation and completion-state change are included in the corresponding Git commit; no tag was created.

## Final result

Iteration 8 is complete. The accepted result presents and invokes platform-provided application shortcuts within the current Launcher role and permission boundary without persisting shortcut data or adding a dedicated overflow product interaction.

## Remaining issues and handoff

No Iteration 8 delivery issue remains open. Validation details absent from the author report remain `Unknown`; shortcut availability outside the current Launcher role and permission boundary remains intentionally unsupported.
