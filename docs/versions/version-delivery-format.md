# Version Delivery Document Format

> Semantic source: English. Chinese counterpart: [version-delivery-format.zh-CN.md](version-delivery-format.zh-CN.md).

## Purpose

`docs/delivery/<version>/` contains one version delivery document and all of its iteration contracts from initial planning through completion. The directory selects work from the current product definition; it does not redefine the product or imply that every current product behavior will be delivered in that version.

The version `delivery.md` is the single authoritative source for iteration status and delivery history. Each separate iteration document is a stable product-delivery contract and does not duplicate changing state, actual evidence, commits, or final results.

The current product definition may change after a version or iteration is authorized. A version record therefore describes its selected scope at the applicable delivery baseline, not an obligation to match every later revision of the current product definition. Completion applies only to the selected scope, acceptance criteria, delivery level, and evidence recorded by that version.

Use the exact `versionName` without a `v` prefix, for example `docs/delivery/1.0.0/`. Do not append lifecycle suffixes such as `-active`, `-completed`, or `-archived`.

## Required structure

Create the version document when real delivery inputs exist. Create iteration contracts only when their real planning inputs exist:

```text
docs/delivery/<version>/
- delivery.md
- delivery.zh-CN.md
- iteration-<number>-<title>.md
- iteration-<number>-<title>.zh-CN.md
```

- `delivery.md` is the English semantic source for the version's user value, selected scope, exclusions, necessary technical conclusions, included iterations, iteration status and delivery history, validation, known limitations, completion criteria, and result. It should identify the applicable product-contract baseline when that baseline matters to interpreting the version boundary.
- Every `delivery.md` declares exactly one delivery level from [release governance](../release.md): `Development build`, `Author daily-use baseline`, or `Formal release artifact`. It applies only that level's gates plus any explicitly promoted version-specific gate. `Development build` cannot complete a formal application version.
- Every iteration has a separate contract beside `delivery.md`, uses the project-wide identifier sequence, and links to the version document without duplicating version-wide rules or execution state.
- Create a separate technical assessment only when an independent technical review is genuinely needed. It is supporting analysis, not a mandatory layer. After the issue is resolved, place durable conclusions in the applicable product, architecture, development, validation, release, decision, or delivery source; do not maintain duplicate conclusions indefinitely.

## Version document format

```markdown
# <Product> <version> Delivery

> Semantic-source notice and authorization boundary

## Version intent
## Delivery level
## Product references
## Included scope and user journey
## Exclusions
## Technical approach and risks
## Included iterations
| Iteration | Status | Updated | Basis |
| --- | --- | --- | --- |
| [Iteration <number>: <title>](iteration-<number>-<title>.md) | `Planned` | YYYY-MM-DD | <Why this state currently applies> |

## Iteration evidence and results
## Dependencies and sequence
## Validation
## Artifact and release requirements
## Known limitations and legacy issues
## Completion criteria
## Completion result
```

Remove a section only when it is genuinely inapplicable. Link detailed product behavior, validation methods, architecture, and release rules instead of copying them. `Delivery level` names one exact level and any explicitly promoted gate. `Included scope and user journey` should define the positive delivery boundary and observable result. `Exclusions` should contain only boundary-sensitive non-goals; use `deferred` only when a later destination or commitment is confirmed. Before completion, `Completion criteria` states the required evidence and `Completion result` states that no final result exists; after completion, `Completion result` records the factual outcome and makes clear that it applies only to the selected version scope.

## Iteration state and result rules

- `Included iterations` lists every separate iteration contract in the version and is the single authoritative location for its status. Each row contains exactly one value from this closed enumeration: `Planned`, `In Progress`, `Completed`, or `Cancelled`.
- `Updated` records the latest status transition date. `Basis` briefly states the author decision or evidence supporting the current value. Ordinary contract or delivery-document edits do not change `Updated`.
- `Planned` permits author-directed contract changes and technical research needed to confirm feasibility. It does not authorize production-code implementation.
- `In Progress` begins when the project author authorizes production-code implementation. Documentation, code, tests, and local validation may proceed within the authorized contract.
- `Completed` means the authorized implementation is finished, the project author has accepted the observable result, and the implementation plus applicable delivery documentation have been committed and synchronized to the author-designated shared Git history. Recommended or version-level validation may remain incomplete only when each known gap is recorded accurately; a gap is not a passing result.
- `Cancelled` means the author stopped the iteration before completion. Record the reason, delivered or partially delivered work, affected contracts, dependency and migration consequences, disposition of produced code or data, and required follow-up. Cancellation does not authorize reverting or deleting work.
- A state transition normally updates `delivery.md` and its maintained language counterpart in the same change. Do not modify the separate iteration contract merely to mirror state, evidence, commits, or results.
- `Iteration evidence and results` must contain exactly one identifiable subsection or record for every row in `Included iterations`. Each record links the applicable contract and records actual commands, environments, devices, results, unavailable or skipped checks, failures, known gaps, follow-up ownership, related commits or tags, and the final author decision. A status must not be advanced to `Completed` or `Cancelled` without its corresponding record.
- If a contract amendment materially changes the iteration objective, primary outcome, product scope, architecture direction, dependency sequence, acceptance conditions, or validation obligations, record an explicit amendment in the contract before completion. The amendment must state its date, author decision, reason, previous boundary, new boundary, and affected acceptance or validation obligations. When the change replaces the iteration's identity, mark the original iteration `Cancelled`, link the replacement contract from its delivery record, and link the cancelled iteration from the replacement contract.
- Marking every included iteration `Completed` does not by itself complete the formal version. The version-wide completion, validation, signing, artifact-traceability, archive, and release gates remain independent.
- This state-ownership separation applies to new and subsequently revised records. Existing historical iteration documents may retain their former status, evidence, and result sections as legacy history; do not rewrite them or backfill their delivery records solely to adopt this rule.

## Version completion and historical protection

After the version is completed, update `delivery.md` with the factual result and retain the same `docs/delivery/<version>/` path. The completed version document and its iteration contracts become protected delivery history: do not rewrite the version's evidence or results, or a contract's scope or acceptance meaning, except to correct an identified factual, link, or translation error without changing historical meaning. A tag or GitHub Release is not required for version completion.

## Milestone boundary

For this project, a **milestone** is an exceptional project baseline explicitly declared by the author and represented by an approved Git tag. A GitHub Release is optional and is created only when the author also chooses outward-facing publication. A formal version, iteration, unapproved tag, or approved tag not declared as a milestone does not become a milestone automatically. Ordinary version planning must not depend on a milestone.

This definition is intentionally narrower than generic project-management usage. It may be changed only through an authorized update to the applicable governance documents.
