# Version Delivery Document Format

> Semantic source: English. Chinese counterpart: [version-delivery-format.zh-CN.md](version-delivery-format.zh-CN.md).

## Purpose

`docs/delivery/<version>/` contains one version delivery document and all of its iteration records from initial planning through completion. The directory selects work from the current product definition; it does not redefine the product or imply that every current product behavior will be delivered in that version.

Use the exact `versionName` without a `v` prefix, for example `docs/delivery/1.0.0/`. Do not append lifecycle suffixes such as `-active`, `-completed`, or `-archived`.

## Required structure

Create the version document when real delivery inputs exist. Create iteration records only when their real planning inputs exist:

```text
docs/delivery/<version>/
- delivery.md
- delivery.zh-CN.md
- iteration-<number>-<title>.md
- iteration-<number>-<title>.zh-CN.md
```

- `delivery.md` is the English semantic source for the version's user value, selected scope, exclusions, necessary technical conclusions, included iterations, validation, known limitations, completion criteria, and result.
- Every `delivery.md` declares exactly one delivery level from [release governance](../release.md): `Development build`, `Author daily-use baseline`, or `Formal release artifact`. It applies only that level's gates plus any explicitly promoted version-specific gate. `Development build` cannot complete a formal application version.
- Iteration records remain beside `delivery.md`, use the project-wide identifier sequence, and link to it without duplicating version-wide rules.
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
## Dependencies and sequence
## Validation
## Artifact and release requirements
## Known limitations and legacy issues
## Completion criteria
## Completion result
```

Remove a section only when it is genuinely inapplicable. Link detailed product behavior, validation methods, architecture, and release rules instead of copying them. `Delivery level` names one exact level and any explicitly promoted gate. Before completion, `Completion criteria` states the required evidence and `Completion result` states that no final result exists; after completion, `Completion result` records the factual outcome.

## Version completion and historical protection

After the version is completed, update `delivery.md` with the factual result and retain the same `docs/delivery/<version>/` path. The completed version document and its iteration records become protected delivery history: do not rewrite their scope, evidence, or result except to correct an identified factual or link error without changing historical meaning. A tag or GitHub Release is not required for version completion.

## Milestone boundary

For this project, a **milestone** is an exceptional project baseline explicitly declared by the author and represented by an approved Git tag. A GitHub Release is optional and is created only when the author also chooses outward-facing publication. A formal version, iteration, unapproved tag, or approved tag not declared as a milestone does not become a milestone automatically. Ordinary version planning must not depend on a milestone.

This definition is intentionally narrower than generic project-management usage. It may be changed only through an authorized update to the applicable governance documents.
