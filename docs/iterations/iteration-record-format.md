# Iteration Contract Format

> Semantic source: English. Chinese counterpart: [iteration-record-format.zh-CN.md](iteration-record-format.zh-CN.md).

## Purpose and naming

Each `docs/delivery/<version>/` directory contains one separate iteration contract for every iteration included in that version. Each iteration is a bounded, independently reviewable delivery unit that contributes to one formal version.

An iteration document is a stable product-delivery contract. It defines the selected objective, scope, constraints, acceptance conditions, and validation requirements. It does not own project execution state, actual evidence, commits, or final results. The sibling version `delivery.md` is the single authoritative source for iteration status and delivery history.

Use `iteration-<number>-<title>.md`, with one project-wide, monotonically increasing positive-integer sequence beginning at `1`, for example `iteration-1-home-minimum-usable-surface.md`. Never renumber, reuse, or restart identifiers. Do not add leading zeroes. Use a concise lowercase English slug for `<title>`.

When a Chinese counterpart is maintained, append `.zh-CN` before `.md`, for example `iteration-1-home-minimum-usable-surface.zh-CN.md`. It keeps the same iteration identifier and English slug.

## Required format

New iteration contracts use this structure. The format may be changed through an authorized update to this document; existing historical records are not rewritten only to adopt a later format.

A Chinese counterpart may translate the title and section headings, but it preserves the same section order, scope, and normative meaning as the English semantic source.

## Contract and validation policy

Avenor Launcher is currently maintained by its project author as a personal project. Validation requirements in an iteration contract are recommendations for reducing risk and improving evidence by default; they are not automatic entry or exit gates unless the project author or an applicable higher-level contract explicitly makes them mandatory.

- Every iteration must have a separate contract. Do not replace it with a section in `delivery.md`.
- The contract does not contain `Planned`, `In Progress`, `Completed`, or `Cancelled` state. The sibling version `delivery.md` owns those values, their transition dates and bases, actual evidence, final results, and known delivery gaps.
- Author-directed contract changes are permitted while the iteration remains incomplete. Record a material amendment explicitly in the contract when it changes the objective, primary outcome, product scope, architecture direction, dependency sequence, acceptance conditions, or validation obligations. The amendment must state its date, author decision, reason, previous boundary, new boundary, and affected acceptance or validation obligations. When the change would replace the iteration's identity, cancel and replace it through the version delivery record rather than silently rewriting the contract; link the replacement contract from the cancelled delivery row and the cancelled iteration from the replacement contract.
- After the version delivery record marks an iteration `Completed` or `Cancelled`, do not change the contract's historical scope or acceptance meaning except to correct an identified factual, link, or translation error without changing its historical meaning.
- The version delivery record must link this contract and record actual commands, environments, devices, results, skipped or unavailable checks, failures, follow-up ownership, related commits or tags, and the author decision supporting its current state. An unperformed or unavailable check is not a passing result.
- An accurately attributed author report of a build, installation, or device-use result is valid delivery evidence for the observations it contains. It does not establish omitted commands, environment identities, reproducibility, or broader validation results.
- Routine agent implementation does not require an automatic Gradle run. Agent-executed Gradle validation occurs only when the author requests it or an authorized higher-level validation task requires it.
- Iteration completion does not imply formal-version completion. The applicable version contract retains its own completion, validation, signing, artifact-traceability, archive, and release gates.
- This state-ownership separation applies to new and subsequently revised records. Existing historical iteration documents may retain former status, evidence, and result sections as legacy history; do not rewrite them or backfill delivery records solely to adopt this rule.

```markdown
# Iteration <number>: <Iteration title>

> Applicable version contract and non-authorization boundary

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
## Acceptance criteria
## Validation requirements
## Related decisions and technical assessments
```

## Field rules

- `Objective` states one coherent delivery result rather than a list of unrelated tasks.
- `Product and version references` links the current product sources and the applicable sibling `delivery.md`. When the timing of a contract change matters, record the applicable product-contract baseline or source revision. It does not duplicate those documents.
- `Observable outcome` describes what can be demonstrated or verified when the iteration succeeds.
- `Included work` defines the positive, observable delivery boundary. `Excluded work` records only boundary-sensitive non-goals that could otherwise be mistaken for included work; do not enumerate every behavior absent from the iteration. Use `deferred` only when a later destination or future commitment has been confirmed.
- `Technical change areas` identifies affected behavior, components, interfaces, data, build, and validation surfaces at a durable level; Git remains authoritative for line-level changes.
- Impact sections must say `None identified` only after the area was considered; omit no material impact silently.
- `Acceptance criteria` states the observable conditions against which the iteration may be accepted. `Validation requirements` defines recommended scenarios and identifies any check explicitly made mandatory; it does not record execution results.
- `Related decisions and technical assessments` links only the durable decisions and assessments needed to interpret the contract. The version delivery record owns implementation commits, tags, actual evidence, final results, and remaining delivery issues.
- An iteration may recommend a product or technical decision but does not authorize one.

When a version completes, its iteration contracts remain in the stable `docs/delivery/<version>/` directory and retain the historical protection defined above. The identifier sequence remains project-wide; completing a version containing `iteration-7-...` through `iteration-10-...`, for example, makes `11` the next available identifier.
