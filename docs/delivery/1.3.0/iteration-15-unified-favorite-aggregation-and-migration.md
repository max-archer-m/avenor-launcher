# Iteration 15: Unified Favorite Aggregation and Compatible Migration

> Applicable version: [Avenor Launcher 1.3.0 Delivery](delivery.md).
>
> This stable contract selects delivery scope. Status, evidence, commits, artifacts, and results belong only in `delivery.md`. It does not authorize implementation or any Git or release action.

## Objective

Establish one durable favorite aggregate that can represent every current vertical-list and favorite-bar destination while migrating readable `1.2.0` primary/companion favorites without loss, duplication, identity collapse, or silent repair.

## Product and version references

- [1.3.0 delivery](delivery.md)
- [Home](../../product/surfaces/home.md)
- [Drawer](../../product/surfaces/drawer.md)
- [Product foundation](../../requirements/product-foundation.md)
- [Privacy](../../product/features/privacy.md)
- [ADR 0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md)
- [ADR 0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md)

## Observable outcome

Readable existing favorites open in the unified model with stable identity and order. New-format data can preserve container identity, type, physical order, list size, membership, and item order. Unreadable data remains preserved and mutation-disabled rather than becoming an empty state.

## Included work

- Define one aggregate for at most two ordered vertical lists and five ordered favorite bars, with stable container identities and no persisted empty container.
- Preserve exactly one destination per stable launchable identity, including distinct primary, cloned, and work-profile entries.
- Version and atomically persist the aggregate using the current accepted persistence boundary unless evidence requires an author-approved decision.
- Migrate readable primary/companion `1.2.0` data deterministically into ordered medium-size vertical lists while preserving entry identity and order.
- Reconcile confirmed inventory removal without treating temporary profile unavailability or partial inventory failure as permanent absence.
- Preserve original unreadable or damaged data, disable favorite mutation, and retain read-only Retry.
- Add focused migration, serialization, invariant, interruption, and compatibility tests or seams.

## Excluded work

- New Home composition, edit controls, Drawer multi-selection, favorite-bar UI, or drag behavior.
- Repair, export, reset, restore, downgrade, cloud backup, or device-transfer backup.
- Migration based only on package name.

## Technical change areas

Favorite domain model, serialization schema, AtomicFile transaction boundary, migration versioning, inventory reconciliation, repository/state interfaces, and focused tests.

## Dependencies and sequence

The protected `1.2.0` aggregate is the input baseline. This iteration precedes every later `1.3.0` iteration that reads or mutates unified destinations.

## Migration and compatibility impact

Migration is the primary impact. Readable data must remain usable and ordered; unreadable source bytes must not be overwritten. Downgrade support is not added. Android cloud and device-transfer backup remain disabled for Avenor-owned favorite data.

## Security, privacy, permission, and licensing impact

No new permission, network access, data category, external service, dependency, or license impact is selected. If implementation requires broader storage, backup, telemetry, or identity access, stop and request author review.

## Risks and unresolved decisions

- A non-atomic conversion can destroy the only readable baseline.
- Container identity or unique-destination errors can duplicate or misassign favorites.
- A consequential persistence or state-ownership change requires author direction and an ADR supported by evidence.

## Acceptance criteria

- Representative readable `1.2.0` empty, primary-only, and primary/companion states migrate deterministically without identity, order, or membership loss.
- Migrated lists use stable identities, physical order, and medium size; valid new data round-trips all contracted fields.
- Duplicate destination assignment and persisted empty containers are rejected or normalized without destructive ambiguity.
- Primary, clone, and work-profile identities remain distinct.
- Unreadable data is preserved, writes remain disabled, and Retry does not clear or repair it.
- Interruption or write failure resolves to the last complete readable state.

## Validation requirements

Recommended evidence includes unit tests for schema versions and invariants, fixture-based `1.2.0` migration, failed/interrupted writes, inventory reconciliation, and process reopen. Actual results belong in `delivery.md`.

## Related decisions and technical assessments

[ADR 0002](../../decisions/0002-use-versioned-atomic-file-for-favorites.md) and [ADR 0003](../../decisions/0003-model-profile-completeness-for-favorite-reconciliation.md) govern their existing boundaries. Create another ADR only after evidence establishes a consequential new decision.
