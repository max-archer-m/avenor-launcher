# Product Decision and Scope-Change Governance

> Semantic source: English. Chinese counterpart: [product-decisions.zh-CN.md](product-decisions.zh-CN.md).
>
> This document defines how product decisions and scope changes are handled. It does not define the contents of any formal version or authorize implementation.

## Purpose

Keep product intent, implementation evidence, and project progress aligned while allowing the product to evolve as design and development expose new information.

Product documentation records the current product contract. It may change when user evidence, technical evidence, or the project author's judgment supports a different direction. Existing code does not silently override that contract, and existing documentation does not prevent an author-directed change.

## Decision authority and responsibility

- The project author is the final decision-maker and first accountable person for all project matters.
- Product, project, and technical roles provide analysis, alternatives, evidence, and recommendations within their responsibilities; they do not replace the author's final authority.
- A specialist review is still required when a security, privacy, legal, financial, or platform-policy conclusion needs relevant expertise.
- Technical evidence may require a product decision to be reconsidered, but an implementation choice does not become a product decision merely because it has been coded.

## Sources of authority

Use each source for its own responsibility:

| Question | Authoritative source |
| --- | --- |
| Why the product exists and which long-term boundaries apply | Product overview |
| What users should be able to observe and accomplish | Current requirements and interaction specifications |
| Why an important product direction or scope changed | Product decision records in this document |
| Where the project is in its delivery lifecycle | Version-delivery or project-progress records |
| How the system is designed and implemented | Architecture documentation and technical decision records |
| What the current implementation actually does | Code, tests, builds, and validation evidence |
| Whether the current product contract has been satisfied | Traceable acceptance and validation evidence |

When documentation, implementation, and observed platform behavior disagree, record a contract mismatch and resolve it explicitly. Do not assume that either the oldest document or the current code automatically wins.

## Product scope hierarchy

Use the following planning hierarchy:

1. **Capability layer:** A long-term product capability boundary such as V1 fixed presentation or V2 basic adaptation. A capability layer is not automatically a semantic software version.
2. **Formal version:** A version delivery contract selecting one coherent, verifiable subset of the current product contract and containing one or more iterations.
3. **Iteration:** An independently reviewable delivery unit contributing to one formal version.
4. **Vertical slice or feature:** An independently understandable, demonstrable, and verifiable user outcome represented in the current product contract.
5. **Implementation task:** Engineering work needed to deliver a current slice or feature; it is not itself a product outcome.
6. **Patch or bug fix:** Work that restores current documented behavior without expanding the product contract.

Do not define a version, iteration, or tag by a fixed number of features. Feature counts may be used as a planning warning, but coherence, user outcome, dependency, risk, and evidence determine the boundary.

A version delivery contract should normally identify one primary product outcome. If it contains more than three independent primary user journeys, review whether its delivery should be divided. This is a review trigger, not an automatic rule.

## Tags and releases

A Git tag identifies an implementation snapshot. It is delivery evidence, not a product-scope level and not a substitute for a version-delivery record or version archive. The authoritative version, artifact, tag, GitHub Release, and milestone rules are defined in [`docs/release.md`](release.md).

- A formal application version may exist without a tag.
- Tags are reserved for author-approved important and stable implementation baselines; they are not required for every formal version.
- A tag does not require a GitHub Release. A GitHub Release requires an existing approved tag and separate author approval.
- This document does not authorize creating tags or releases.

## Version-delivery records

Version-delivery records describe project intent, selected product scope, technical feasibility, iteration composition, evidence, and outcome. They do not replace product requirements. Their location and format are defined in [`docs/versions/README.md`](versions/README.md).

A version-delivery record may contain:

- The primary product outcome being pursued
- Status and accountable owner
- Links to the current product baseline and included slices or features
- Entry and exit conditions
- Progress and completed evidence
- Blockers and unresolved contract mismatches
- Links to relevant product decisions and technical decision records
- Documented deviations from the original scope snapshot
- The final completion, partial-completion, cancellation, pause, or replacement conclusion
- Related builds, tags, or validation records when they exist

A version-delivery record must not become a second authoritative copy of detailed page behavior, acceptance criteria, architecture, or implementation rules. When a linked product contract changes, the version record records the change and decision link instead of silently rewriting its history.

Each formal version is defined separately through its own delivery contract when real delivery inputs exist.

## What is a product scope change?

A product scope change adds, removes, defers, replaces, or materially changes the current product contract.

Treat a change as a scope change when it affects any of the following:

- Target users or supported scenarios
- Capability-layer boundaries or long-term non-goals
- A formal version's primary product outcome or exit gate
- Included or excluded pages, dialogs, user journeys, or independently useful feature modules
- Supported operating systems, devices, screen forms, languages, regions, or distribution channels
- User-visible behavior, state, recovery, or acceptance criteria
- Data categories, permissions, package visibility, networking, accounts, cloud services, analytics, or third-party processing
- Accessibility, privacy, security, compatibility, or maintenance obligations

The document split for detailed interaction specifications may use a page, dialog, or relatively independent feature module as its unit. Document organization does not by itself determine whether a scope change occurred.

The following normally do not constitute a scope change:

- Correcting wording without changing normative meaning
- Clarifying behavior already present in the current contract
- Fixing an implementation that does not satisfy existing acceptance criteria
- Internal refactoring with no user-visible or contractual effect
- Minor presentation adjustments that do not change current behavior or quality requirements

## Decision impact levels

Classify product decisions by impact:

### D1: Strategic decision

A D1 decision changes a capability-layer boundary, core principle, target audience, major platform or device boundary, data posture, distribution model, or other long-term product constraint.

Requirements:

- Record a product decision.
- Update the overview and every affected authoritative requirement.
- Re-evaluate affected versions and professional-review needs.

### D2: Scope decision

A D2 decision changes a current formal version, vertical slice, feature, user journey, non-goal, or acceptance criterion without redefining the product's long-term direction.

Requirements:

- Record a product decision.
- Update the affected Requirements Brief or interaction specification.
- Record the version-delivery impact when an applicable version contract exists.

### D3: Local product decision

A D3 decision selects or clarifies local page or interaction behavior without changing the current scope.

Requirements:

- Record the result in the applicable interaction specification.
- Add a separate product decision record only when alternatives, consequences, or a likely future reconsideration make the rationale valuable.

## Mandatory decision-record triggers

Create a product decision record when any of the following applies:

- The change is D1 or D2.
- Multiple reasonable alternatives have materially different consequences.
- The decision is difficult or expensive to reverse.
- The same question is likely to be raised again.
- The decision affects multiple authoritative documents or multiple features.
- The change adds data, permissions, networking, accounts, external services, supported platforms, or distribution obligations.
- A current requirement is deferred, removed, or replaced because of technical evidence.
- A formal version is materially re-scoped, cancelled, or replaced.

Do not create a record for ordinary wording edits, routine defect fixes, or implementation details with no product-contract impact.

## Contract-mismatch handling

Classify a mismatch before changing product documentation or implementation:

### Documentation clarification

The current contract is unchanged, but the document is incomplete or ambiguous. Clarify the authoritative product document. A separate decision record is optional unless the rationale is consequential.

### Implementation defect

The current contract is clear, but the implementation does not satisfy it. Fix the code and validation evidence; do not change product documentation merely to match the defect.

### Product scope change

New learning leads the author to change current user behavior, scope, or acceptance. Update the affected product documents and then align implementation and tests. Record the decision only after the author explicitly enables decision records.

### Technical constraint requiring product reconsideration

Platform or implementation evidence shows that the current contract is infeasible, unreliable, unsafe, or disproportionate. Record the evidence and alternatives, obtain the author's product direction, and then update the appropriate product and technical documents.

### Exploratory implementation

A prototype or technical spike may precede current product documentation when evidence cannot be obtained otherwise. Keep it outside the current product contract until the resulting behavior is written into the applicable authoritative documents by author direction.

## Change workflow

Use the lightest process proportional to impact:

1. Identify the observed problem, evidence, affected contract, and intended outcome.
2. Classify it as clarification, defect, scope change, technical constraint, or exploration.
3. Determine the D1, D2, or D3 impact level when a product decision is involved.
4. Present meaningful alternatives and consequences to the project author.
5. Reflect the author's direction in the current authoritative documents; add a decision record only after the author explicitly enables decision records.
6. Update every affected authoritative product document.
7. Update implementation and tests only within the resulting current contract.
8. Update version-delivery progress and validation evidence without duplicating the product contract.

For a local clarification or small product adjustment, documentation, code, and tests may be updated in the same focused commit. For D1 or D2 changes, the resulting current product contract must be reviewable before or together with implementation. Existing code does not silently change the contract.

## Product decision record format

Append records to this document using stable, never-reused identifiers:

```markdown
## PD-0001: <Decision title>

- Date: YYYY-MM-DD
- Impact: D1 | D2 | D3
- Decider: Project author
- Context: <Why a decision is required>
- Decision: <The direction established by the project author>
- Alternatives: <Meaningful alternatives considered>
- Rationale: <Why this direction was selected>
- Scope impact: <Affected capability layer, formal version, slice, feature, or non-goal>
- Documentation impact: <Authoritative documents to update>
- Technical or professional review: <Required evidence or review, if any>
- Revisit when: <Conditions that trigger reassessment>
- Related records: <Requirements, version-delivery records, ADRs, validation, or replacement decision>
```

- Keep each record focused on one decision that can be reconsidered independently.
- Do not duplicate full requirements or technical designs in a decision record; link to their authoritative locations.
- Do not rewrite a historical decision to represent a later outcome. Add a later record explaining what changed and cross-link the records.
- A version may link to multiple product decisions, and one product decision may affect multiple versions or features.

## Product-decision record authorization

- Do not append any actual `PD-NNNN` record until the project author explicitly requests product decision recording to begin.
- The template above is a format definition, not an existing decision record.
- Until record creation is authorized, an explicit author decision may update the applicable authoritative product documents directly. Do not create a product decision entry merely because a mandatory trigger would normally apply.
- When the author enables product decision recording, start with `PD-0001`; do not retroactively invent records unless the author explicitly requests historical reconstruction.

## Current boundary

This governance establishes the decision process only. It does not:

- Define any formal-version delivery scope
- Authorize a vertical slice or implementation task
- Select a technical stack or architecture
- Authorize any tag or GitHub Release action
- Authorize V2, V3, or V4 work
- Authorize public distribution or commercialization
- Authorize writing an actual product decision record before the project author explicitly requests it
