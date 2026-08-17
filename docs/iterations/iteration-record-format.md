# Iteration Record Format

> Semantic source: English. Chinese counterpart: [iteration-record-format.zh-CN.md](iteration-record-format.zh-CN.md).

## Purpose and naming

Each `docs/delivery/<version>/` directory contains that version's iteration records beside `delivery.md` from planning through completion. Each iteration is a bounded, independently reviewable delivery unit that contributes to one formal version.

Use `iteration-<number>-<title>.md`, with one project-wide, monotonically increasing positive-integer sequence beginning at `1`, for example `iteration-1-home-minimum-usable-surface.md`. Never renumber, reuse, or restart identifiers. Do not add leading zeroes. Use a concise lowercase English slug for `<title>`.

When a Chinese counterpart is maintained, append `.zh-CN` before `.md`, for example `iteration-1-home-minimum-usable-surface.zh-CN.md`. It keeps the same iteration identifier and English slug.

## Required format

New iteration records use this structure. The format may be changed through an authorized update to this document; existing historical records are not rewritten only to adopt a later format.

A Chinese counterpart may translate the title and section headings, but it preserves the same section order, scope, and normative meaning as the English semantic source.

## Iteration progression and validation policy

Avenor Launcher is currently maintained by its project author as a personal project. Validation scenarios in an iteration record are therefore recommendations for reducing risk and improving evidence by default; they are not automatic entry or exit gates for that iteration or the next one.

- Every iteration record must contain exactly one status value from this closed enumeration: `Planned`, `In Progress`, `Completed`, or `Cancelled`. Do not introduce synonyms, qualifiers, or additional status values.
- `Planned` permits author-directed changes to any part of the iteration and technical research needed to make the scope feasible. It does not authorize production-code implementation. Research output remains evidence or a proposal until the author authorizes implementation and the status changes to `In Progress`.
- `In Progress` begins when the project author authorizes production-code implementation. Documentation, code, tests, and local validation may proceed within the authorized scope. Small scope adjustments may be made cautiously during implementation when they preserve the iteration's objective and current product definition; record the reason and affected boundary. A change that materially replaces the objective, primary outcome, product scope, architecture direction, dependency sequence, or validation obligation should normally cancel or replace the iteration rather than silently rewrite it.
- `Completed` means the authorized implementation is finished, the project author has accepted the observable result, and the implementation plus its corresponding iteration documentation have been committed and synchronized to the author-designated shared Git history. Recommended or version-level validation may remain incomplete only when each known gap is recorded accurately; a gap is not a passing result. After completion, do not change the iteration's historical scope, evidence, or outcome except to correct an identified factual or link error without changing its historical meaning.
- `Cancelled` means the author has stopped the iteration before completion. Record the cancellation date, reason, delivered or partially delivered work, affected product or technical contracts, dependency and migration consequences, disposition of any code or data already produced, and the required action for later iterations. Cancellation does not by itself authorize reverting or deleting work.
- Iteration completion does not imply formal-version completion. The applicable version contract independently governs version-wide validation, signing, artifact traceability, archive, and release results.
- Record known gaps, failures, skipped scenarios, affected behavior, and follow-up ownership accurately. An unperformed or unavailable check is not a passing result.
- An accurately attributed author report of a build, installation, or device-use result is valid iteration evidence for the observations it contains. It does not establish omitted commands, environment identities, reproducibility, or broader validation results.
- Routine agent implementation does not require an automatic Gradle run. Agent-executed Gradle validation occurs only when the author requests it or an authorized higher-level validation task requires it.
- A specific check becomes mandatory only when the project author explicitly makes it an iteration gate or when an applicable higher-level contract requires it for a formal version, artifact, signing, archive, or release result.
- Iteration progression does not imply formal-version completion. The applicable version contract retains its own completion and release gates.
- Iteration completion means that the recorded selected scope and acceptance criteria are complete. It does not claim complete implementation of the current product contract, including later changes to that contract.

```markdown
# Iteration <number>: <Iteration title>

> Applicable version contract and non-authorization boundary

## Status

- Value: `Planned`
- Updated: YYYY-MM-DD
- Basis: <Why this value currently applies, including the author decision or synchronization evidence when applicable>

## Objective
## Product and version references
## Observable outcome
## Included work
## Excluded work
## Technical change areas
## Dependencies and sequence
## Migration and compatibility impact
## Security, privacy, permission, and licensing impact
## Risks and unresolved decisions
## Validation plan
## Acceptance evidence
## Related decisions, commits, and tags
## Final result
## Remaining issues and handoff
```

## Field rules

- `Status` is mandatory. `Value` must exactly match one enumeration value. `Updated` records the date of the latest status transition, not an ordinary content edit. `Basis` briefly states the evidence and author decision supporting the value; for `Completed`, identify the synchronized implementation and documentation history or link to where it is recorded. When synchronization uses something other than the normal shared Git branch, identify the author-designated shared history without adding machine-specific or secret information.
- Content changes that do not transition status do not change the `Updated` date. Record material in-status scope adjustments in the relevant body section and Git history. If a product-contract change materially changes the iteration objective, primary outcome, acceptance criteria, or boundary, record an explicit scope amendment before completion or cancel and replace the iteration rather than silently changing its meaning.
- `Objective` states one coherent delivery result rather than a list of unrelated tasks.
- `Product and version references` links the current product sources and the applicable sibling `delivery.md`. When the timing of a contract change matters, record the applicable product-contract baseline or source revision. It does not duplicate those documents.
- `Observable outcome` describes what can be demonstrated or verified when the iteration succeeds.
- `Included work` defines the positive, observable delivery boundary. `Excluded work` records only boundary-sensitive non-goals that could otherwise be mistaken for included work; do not enumerate every behavior absent from the iteration. Use `deferred` only when a later destination or future commitment has been confirmed.
- `Technical change areas` identifies affected behavior, components, interfaces, data, build, and validation surfaces at a durable level; Git remains authoritative for line-level changes.
- Impact sections must say `None identified` only after the area was considered; omit no material impact silently.
- `Validation plan` lists recommended scenarios and identifies any explicitly promoted mandatory check. `Acceptance evidence` records commands, environments, devices, results, and unavailable checks actually observed without treating missing evidence as a pass.
- `Final result` records the observable outcome and the author decision supporting `Completed` or `Cancelled`; while `Planned` or `In Progress`, it states that no final result exists yet. `Remaining issues and handoff` records all known evidence gaps, follow-up owners, and downstream effects. Recommended validation does not block `Completed` when the rules above are satisfied, but the document must not claim that an unperformed check passed.
- An iteration may recommend a product or technical decision but does not authorize one.

When a version completes, its iteration records remain in the stable `docs/delivery/<version>/` directory and retain the historical protection defined above. The identifier sequence remains project-wide; completing a version containing `iteration-7-...` through `iteration-10-...`, for example, makes `11` the next available identifier.
