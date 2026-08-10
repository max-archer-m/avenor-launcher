# Iteration Record Format

> Semantic source: English. Chinese counterpart: [iteration-record-format.zh-CN.md](iteration-record-format.zh-CN.md).

## Purpose and naming

`docs/iterations/` contains active iteration delivery contracts. Each iteration is a bounded, independently reviewable unit that contributes to one formal version.

Use `iteration-<number>-<title>.md`, with one project-wide, monotonically increasing positive-integer sequence beginning at `1`, for example `iteration-1-home-minimum-usable-surface.md`. Never renumber, reuse, or restart identifiers. Do not add leading zeroes. Use a concise lowercase English slug for `<title>`.

When a Chinese counterpart is maintained, append `.zh-CN` before `.md`, for example `iteration-1-home-minimum-usable-surface.zh-CN.md`. It keeps the same iteration identifier and English slug.

## Required format

New iteration records use this structure. The format may be changed through an authorized update to this document; existing historical records are not rewritten only to adopt a later format.

A Chinese counterpart may translate the title and section headings, but it preserves the same section order, scope, and normative meaning as the English semantic source.

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
## Validation plan
## Acceptance evidence
## Related decisions, commits, and tags
## Final result
## Remaining issues and handoff
```

## Field rules

- `Objective` states one coherent delivery result rather than a list of unrelated tasks.
- `Product and version references` links the current product contract and the applicable `docs/versions/<version>/delivery-contract.md` or supporting input. It does not duplicate those documents.
- `Observable outcome` describes what can be demonstrated or verified when the iteration succeeds.
- `Included work` and `Excluded work` make the iteration boundary explicit.
- `Technical change areas` identifies affected behavior, components, interfaces, data, build, and validation surfaces at a durable level; Git remains authoritative for line-level changes.
- Impact sections must say `None identified` only after the area was considered; omit no material impact silently.
- `Validation plan` is prospective. `Acceptance evidence` records commands, environments, devices, results, and unavailable checks actually observed.
- `Final result` and `Remaining issues and handoff` are completed from evidence when the iteration closes. Before then, they state the required closure and handoff evidence without claiming completion.
- An iteration may recommend a product or technical decision but does not authorize one.

When a formal version completes, move its original included iteration records into `docs/archives/v<version>/` and update all inbound links. The next identifier continues the project-wide sequence. Archiving `iteration-1-...` through `iteration-6-...`, for example, makes `7` the next available identifier.
