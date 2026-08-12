# ADR-0002: Use a Versioned Atomic File for Favorites

## Status

Proposed

## Date

2026-08-12

## Context

Avenor `1.0.0` persists an ordered list of exact launchable identities. A readable empty list must remain distinct from an unreadable document, Retry must not write, and a failed read must never replace or clear the original bytes. Mutations are permitted only after a complete successful read and must publish atomically and serially.

The technical assessment identified Proto DataStore as a preferred candidate, subject to proving these failure invariants and reviewing its dependency and notice obligations. Iteration 4 does not otherwise need protocol buffers, a general preferences layer, background synchronization, or cross-device migration.

## Decision

Avenor `1.0.0` stores favorites in one credential-encrypted, application-private file managed through Android `AtomicFile` and a project-owned binary serializer.

- The document begins with a format magic value and explicit schema version.
- Each ordered entry contains only the Android user/profile serial number and exact flattened `ComponentName`.
- Labels, icons, badges, availability, timestamps, analytics, and inventory history are not persisted.
- Reads validate the header, version, nonnegative entry count, exact component encoding, duplicate absence, and end of document before publishing a readable state. The corruption-defense parser does not impose a separate user-facing favorite-count limit.
- A missing file is a successful empty state. Invalid or unreadable bytes publish a distinct read-failure state without modifying the file.
- Add, remove, reconciliation, and Retry operations share one mutation mutex. Mutations require a readable state and use `AtomicFile` commit or rollback behavior.
- Cloud backup and device-to-device transfer remain disabled by the Manifest and Android data-extraction rules.
- Future schema versions must migrate from the previous valid document without treating unknown or damaged data as empty.

## Rationale

This is the smallest persistence boundary that directly expresses the required failure semantics and ordered identity model. It uses Android platform and Kotlin runtime APIs already present in the application, so Iteration 4 adds no persistence library, generated schema toolchain, transitive dependency, or new third-party notice obligation.

Choosing a project-owned format also makes the read-before-write rule and preservation of unreadable source bytes explicit. The decision does not reject DataStore generally; it avoids adding it where its broader lifecycle and dependency surface provide no required `1.0.0` capability.

## Considered Options

### Proto DataStore

- Benefits: Typed schema, ordered repeated values, atomic updates, and established migration facilities.
- Trade-offs: Adds protobuf schema generation and persistence dependencies for a single small document; the project would still need explicit handling to preserve the current unreadable-source and mutation-gating invariants.

### Preferences DataStore

- Benefits: Avoids a generated protobuf schema and provides serialized updates.
- Trade-offs: Exact ordered structured identities require an additional custom encoding, weakening the benefit over a directly versioned document.

### SharedPreferences

- Benefits: Small familiar platform API.
- Trade-offs: Does not naturally model an ordered typed identity document or expose the required corruption and read-failure boundary.

### Versioned `AtomicFile`

- Benefits: Minimal dependency surface, explicit schema and validation, atomic replacement, direct preservation of unreadable bytes, and straightforward ordered identity encoding.
- Trade-offs: Avenor owns serializer correctness, schema migration, parser resource-safety constraints, and focused persistence tests. No separate user-facing favorite-count limit is established by this decision.

## Consequences

- Favorite storage remains a narrow repository-owned component rather than leaking file operations into UI or inventory code.
- Schema changes require an explicit migration path and validation against data written by schema version 1.
- The serializer must reject invalid counts, duplicates, or trailing data before exposing mutations without turning a corruption-defense constant into an undocumented product limit.
- Mutation failures retain the last readable in-memory state and do not claim a successful write.
- A future decision may replace this implementation with DataStore or another backend, but it must preserve the same identity, ordering, failure, atomicity, backup, and migration contracts.
- The application dependency graph receives no new persistence or serialization license obligations from this decision. Release-wide dependency and license review remains an Iteration 6 responsibility.

## Validation Evidence and Gaps

- The implementation contains an explicit magic value, schema version, count validation, duplicate validation, trailing-data validation, `AtomicFile` commit/rollback, and mutex-serialized reads and mutations.
- The Manifest disables backup and the Android 12+ data-extraction rules exclude every application storage domain from cloud backup and device transfer.
- The project author reported a successful Gradle build for the implementation under review and correct persistence in the tested daily scenarios. The exact command, build variant, environment, and retained output were not reported.
- Static source and XML checks support this proposal. Agent-run Gradle, instrumented persistence recreation, damaged-file injection, and physical-device restart evidence have not been performed in this change.
- Functional acceptance does not itself accept this architecture decision. The project author must explicitly accept the persistence trade-offs before this ADR becomes an authoritative architecture boundary.

## Supersedes

None
