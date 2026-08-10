# Version Delivery Document Format

> Semantic source: English. Chinese counterpart: [version-delivery-format.zh-CN.md](version-delivery-format.zh-CN.md).

## Purpose

`docs/versions/` contains the active delivery contract and supporting inputs for each formal application version. A version directory selects work from the current product contract; it does not redefine the product or imply that every current product behavior will be delivered in that version.

Use `docs/versions/<version>/`, where `<version>` is the exact `versionName` without a `v` prefix, for example `docs/versions/1.0.0/`.

## Required structure

Create only files whose real inputs exist:

```text
docs/versions/<version>/
├── product-scope.md
├── product-scope.zh-CN.md       # when a maintained Chinese counterpart is needed
├── technical-assessment.md
└── delivery-contract.md
```

- `product-scope.md` is the English semantic source for the user value, selected current-product scope, exclusions, and product acceptance intent.
- `technical-assessment.md` records feasibility evidence, constraints, alternatives, dependencies, migration cost, risks, and proposed validation. It must not silently change product scope.
- `delivery-contract.md` becomes the integrated version delivery contract after the product scope and technical assessment provide enough evidence. It defines the approved iteration set, dependencies, risks, version exit gates, and required handoffs without duplicating detailed product or technical sources.
- Iteration records remain under [`docs/iterations/`](../iterations/) while active and link back to the applicable version contract.

## Product-scope format

A version product-scope document uses the following section order, removing only sections that are genuinely inapplicable:

```markdown
# <Product> <version> Product Scope

> Semantic-source and counterpart notice
> Responsibility and non-authorization boundary

## Version intent
## Authoritative product references
## Included user journey
## Included product scope
## Explicitly excluded from <version>
## Product acceptance intent
## Technical assessment inputs
## Version and release boundary
## Completion handoff
```

## Technical-assessment format

```markdown
# <Product> <version> Technical Assessment

> Relationship to product scope and decision authority

## Assessment question
## Inputs and evidence
## Platform and compatibility findings
## Proposed system boundaries
## Data, identity, persistence, and migration
## Permissions, security, privacy, and licensing impact
## Dependencies and alternatives
## Build and validation approach
## Quality-gate proposals
## Delivery risks and unresolved decisions
## Iteration recommendations
## Product-scope impact proposals
## Assessment conclusion
```

Separate consequential, durable architecture choices into ADRs when architecture decision recording exists. The assessment may recommend decisions but does not authorize implementation or change the product contract.

## Integrated version-contract format

```markdown
# <Product> <version> Delivery Contract

> Applicable product scope, technical assessment, and authorization boundary

## Version outcome
## Included and excluded scope
## Technical feasibility conclusion
## Included iterations
## Dependencies and sequence
## Risks and required decisions
## Validation and exit gates
## Artifact, signing, and archive requirements
## Known limitations and legacy issues
## Completion result
```

Before completion, `Completion result` defines the evidence required to close the version; after completion, it records the factual result. Do not use lifecycle labels as a substitute for evidence.

## Version completion and archive

After the version is formally completed, move its integrated contract, supporting inputs, and original included iteration records into `docs/archives/v<version>/` in accordance with [release governance](../release.md). Update links during the move. A tag or GitHub Release is not required for version completion.

## Milestone boundary

For this project, a **milestone** is an exceptional project baseline explicitly declared by the author and represented by an approved Git tag. A GitHub Release is optional and is created only when the author also chooses outward-facing publication. A formal version, iteration, unapproved tag, or approved tag not declared as a milestone does not become a milestone automatically. Ordinary version planning must not depend on a milestone.

This definition is intentionally narrower than generic project-management usage. It may be changed only through an authorized update to the applicable governance documents.
