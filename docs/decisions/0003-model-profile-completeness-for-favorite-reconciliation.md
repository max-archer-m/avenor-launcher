# ADR-0003: Model Profile Completeness for Favorite Reconciliation

## Status

Proposed

## Date

2026-08-12

## Context

A saved favorite identifies one exact launchable activity by Android user/profile serial number and `ComponentName`. A missing entry in a Drawer snapshot is ambiguous: the application or component may be disabled, a profile may be locked or temporarily unreadable, a non-current profile read may have failed, an update may be in progress, or the exact identity may have permanently disappeared.

Treating every missing entry as unavailable loses trusted presentation and management access. Treating package absence or one callback as permanent disappearance can delete the wrong favorite, especially for multiple launcher activities, clones, and profiles.

## Decision

Inventory snapshots carry the read result for each profile that was in scope for the read. Favorite reconciliation uses exact identity and exposes five states: available, disabled, temporarily unavailable, unknown, and confirmed permanently removed.

- An available exact identity uses the current snapshot entry.
- A disabled identity retains the last trusted derived entry when available, may derive a current application-level fallback for the current user, remains selectable for unavailable feedback, and retains its applicable long-press management entry.
- A failed or unavailable profile read produces a temporary or unknown state and never deletion evidence.
- A profile that still exists but is not currently readable produces a temporary state.
- A removed profile may confirm removal only when the ordinary profile boundary can establish that its serial no longer exists.
- For the current user, a complete profile read checks the exact component with disabled components included and distinguishes application or component disablement. When the package remains installed, exact component disappearance requires two consecutive complete observations; an intervening available or disabled result clears the candidate.
- For a non-current profile, package presence cannot prove exact component presence or disappearance. When public platform evidence cannot distinguish the states, the favorite remains unknown.
- Package callbacks trigger a serialized refresh. Availability, suspension, update, and resumption callbacks may mark or clear temporary package evidence, but no callback directly mutates favorites or alone proves permanent disappearance.
- Only identities resolved as confirmed permanently removed are passed to the serialized favorite-store removal operation.

## Rationale

Profile-level completeness is the smallest snapshot metadata that prevents a successful partial Drawer result from becoming destructive evidence for an unreadable profile. A separate favorite availability model prevents UI nullability from owning reconciliation semantics. Exact component checks preserve independent activities and profiles without falling back to package-name identity.

The conservative unknown state is intentional. Retaining a favorite when public APIs provide insufficient evidence is reversible; deleting it is not.

## Considered Options

### Treat every missing snapshot identity as unavailable

This is simple but merges loading, failure, disabled, temporary, and permanent states, loses management behavior, and cannot safely reconcile favorites.

### Use package installation as final evidence

This distinguishes uninstall from some temporary states but cannot distinguish exact launcher activities or independent profiles and clones.

### Let callbacks directly remove favorites

This is responsive but callback meaning and ordering do not prove that a later snapshot completely covered the affected profile or exact component.

### Carry profile completeness and resolve exact identity conservatively

This adds explicit state and focused platform checks while keeping destructive mutation behind sufficient evidence.

## Consequences

- Inventory loaders return snapshots rather than an unqualified entry list.
- Home consumes explicit favorite availability rather than nullable entries.
- Last trusted labels, icons, badges, and user handles remain process-local derived presentation and are never persisted as favorite truth.
- A disabled non-current-profile entry after process restart may lack a trusted presentation because public APIs do not always expose its disabled exact activity. It remains retained and unknown rather than being deleted.
- Some exact component removals in non-current profiles may remain unknown until stronger platform evidence exists. No hidden API, broad package visibility, or hidden-profile permission is authorized.
- Tests must cover partial profile reads, exact component independence, profile independence, callback non-authority, and non-destructive failures.

## Validation Evidence and Gaps

- Focused test source covers explicit disabled presentation, partial-profile retention, exact-identity-only reconciliation, profile independence, callback-triggered refresh, and shared marquee priority.
- The project author reported a successful Gradle build for the implementation under review and considers its functional result acceptable for continuing delivery. The exact command, build variant, environment, procedure, and retained output were not reported.
- API 31, Pixel, locked-profile, disabled-component, damaged-file, and process-restart validation for this change remain unexecuted or unknown.
- Functional acceptance does not itself accept this architecture decision. The project author must explicitly accept the reconciliation trade-offs before this ADR becomes an authoritative architecture boundary.

## Supersedes

None
