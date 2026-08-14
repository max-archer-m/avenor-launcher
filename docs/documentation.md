# Project Documentation Map and Governance

> Semantic source for public use: English. Chinese counterpart: [documentation.zh-CN.md](documentation.zh-CN.md).

## Purpose

This document defines the single authoritative location, creation condition, and maintenance rules for each type of Avenor Launcher project information. A planned path does not mean that its conclusions exist, and empty documents must not be created merely to complete the directory structure.

The project author is the first accountable person for all project matters. Security, privacy, legal, financial, or platform-policy conclusions must receive qualified specialist review when required.

## Document categories

Repository documentation uses four practical categories. The category explains a document's responsibility; it does not create an additional approval or lifecycle system.

| Category | Governing question | Examples |
| --- | --- | --- |
| Project governance | How is project work routed, authorized, documented, and maintained? | `AGENTS.md`, this document, delivery formats |
| Current facts and rules | What product behavior, technical configuration, development, validation, and release rules apply now? | `overview.md`, `docs/product/`, `development.md`, `validation.md`, `release.md` |
| Decision rationale | Why does a consequential confirmed product or architecture direction exist? | Product decisions and ADRs |
| Delivery records | What does a named version or iteration deliver, and what actually happened? | Stable version delivery directories and their iteration records |

`LICENSE` remains the applicable legal instrument and does not need a documentation category. Temporary prompts, scratch notes, unchecked task lists, and conversation transcripts are working material, not authoritative project documentation.

## Single authoritative source

Each durable fact has one primary location. Other documents link to or briefly identify that source instead of reproducing its rules.

| Information | Primary source |
| --- | --- |
| Project routing and project-specific authorization boundaries | `AGENTS.md` |
| Documentation locations and maintenance rules | `docs/documentation.md` |
| Product direction and current user behavior | `overview.md`, requirements, and the applicable file under `docs/product/` |
| Development environment and build entry points | `docs/development.md` |
| Validation methods, evidence states, and execution authority | `docs/validation.md` |
| Delivery levels, artifacts, signing, and release operations | `docs/release.md` |
| Version scope, selected level, gates, and result | The version's `delivery.md` |
| Iteration scope, status, evidence, and result | The applicable iteration record |
| Consequential technical rationale | The applicable active or superseded ADR |
| Role authority | The Toolkit role definitions and authorization matrix |

When two authoritative documents state the same rule, select one owner and replace the duplicate with a link. A delivery record may summarize the scope it selects, but it must not become a second product specification, validation guide, architecture document, or release policy.

## Current authoritative documents

| Information type | English or public entry | Chinese entry | Category | Responsibility |
| --- | --- | --- | --- | --- |
| Project entry | [`README.md`](../README.md) | [`README.zh-CN.md`](../README.zh-CN.md) | Current facts and rules | Provides the current project and product summary and links to deeper documentation |
| Product overview | [`overview.md`](../overview.md) | [`overview.zh-CN.md`](../overview.zh-CN.md) | Current facts and rules | Records product direction, principles, boundaries, and unresolved scope |
| Agent routing | [`AGENTS.md`](../AGENTS.md) | [`AGENTS.zh-CN.md`](../AGENTS.zh-CN.md) | Project governance | Records the Toolkit entry point and project-specific working rules |
| Documentation governance | [`docs/documentation.md`](documentation.md) | [`docs/documentation.zh-CN.md`](documentation.zh-CN.md) | Project governance | Defines document responsibilities, locations, and maintenance rules |
| Development guide | [`docs/development.md`](development.md) | [`docs/development.zh-CN.md`](development.zh-CN.md) | Current facts and rules | Defines the current development environment, project configuration, and build or run entry points |
| Validation guide | [`docs/validation.md`](validation.md) | [`docs/validation.zh-CN.md`](validation.zh-CN.md) | Current facts and rules | Defines available checks, execution authority, manual validation, evidence, and result reporting |
| Product foundation requirements | [`docs/requirements/product-foundation.md`](requirements/product-foundation.md) | [`docs/requirements/product-foundation.zh-CN.md`](requirements/product-foundation.zh-CN.md) | Current facts and rules | Records the product problem, author context, current scope, acceptance intent, and open product questions |
| Product decisions and scope changes | [`docs/product-decisions.md`](product-decisions.md) | [`docs/product-decisions.zh-CN.md`](product-decisions.zh-CN.md) | Project governance and decision rationale | Defines decision authority and records consequential confirmed product choices when used |
| Product navigation | [`docs/product/navigation.md`](product/navigation.md) | [`docs/product/navigation.zh-CN.md`](product/navigation.zh-CN.md) | Current facts and rules | Defines surface hierarchy, entry, exit, Back, restoration, and shared transitions |
| Home interaction | [`docs/product/home.md`](product/home.md) | [`docs/product/home.zh-CN.md`](product/home.zh-CN.md) | Current facts and rules | Defines Home information, favorites, scrolling, launch behavior, and edit mode |
| Drawer interaction | [`docs/product/drawer.md`](product/drawer.md) | [`docs/product/drawer.zh-CN.md`](product/drawer.zh-CN.md) | Current facts and rules | Defines the application inventory, grouping, sorting, alphabet index, and live updates |
| Application action sheet | [`docs/product/app-action-sheet.md`](product/app-action-sheet.md) | [`docs/product/app-action-sheet.zh-CN.md`](product/app-action-sheet.zh-CN.md) | Current facts and rules | Defines modal application shortcuts and Launcher actions |
| Settings interaction | [`docs/product/settings.md`](product/settings.md) | [`docs/product/settings.zh-CN.md`](product/settings.zh-CN.md) | Current facts and rules | Defines current Settings information and behavior |
| Low-fidelity wireframes | [`docs/product/low-fidelity-wireframes.md`](product/low-fidelity-wireframes.md) | [`docs/product/low-fidelity-wireframes.zh-CN.md`](product/low-fidelity-wireframes.zh-CN.md) | Current product visualization | Visualizes the current Home, Drawer, application action sheet, and Settings contracts without replacing their normative text |
| Product design foundations | [`docs/product/design-foundations.md`](product/design-foundations.md) | [`docs/product/design-foundations.zh-CN.md`](product/design-foundations.zh-CN.md) | Current facts and rules | Defines current theme, layout, typography, icon, accessibility, and resource principles |
| Product glossary | [`docs/product/glossary.md`](product/glossary.md) | [`docs/product/glossary.zh-CN.md`](product/glossary.zh-CN.md) | Current facts and rules | Defines canonical product terms |
| Version, artifact, and release governance | [`docs/release.md`](release.md) | [`docs/release.zh-CN.md`](release.zh-CN.md) | Current facts and rules | Defines delivery levels, application versions, completed records, APK artifacts, signing continuity, tags, and GitHub Releases |
| Version-delivery format | [`docs/versions/version-delivery-format.md`](versions/version-delivery-format.md) | [`docs/versions/version-delivery-format.zh-CN.md`](versions/version-delivery-format.zh-CN.md) | Project governance | Defines the unified delivery directory, delivery-level selection, format, and migration exception |
| Iteration-record format | [`docs/iterations/iteration-record-format.md`](iterations/iteration-record-format.md) | [`docs/iterations/iteration-record-format.zh-CN.md`](iterations/iteration-record-format.zh-CN.md) | Project governance | Defines iteration naming, required sections, evidence, and historical protection |
| 1.0.0 completed delivery | [`docs/delivery/1.0.0/delivery.md`](delivery/1.0.0/delivery.md) | [`docs/delivery/1.0.0/delivery.zh-CN.md`](delivery/1.0.0/delivery.zh-CN.md) | Delivery history | Records the completed author daily-use baseline, included iterations, evidence, and known gaps |
| Architecture decisions | [`docs/decisions/`](decisions/) | - | Decision rationale | Records consequential implemented and accepted architecture decisions; only an active ADR establishes its stated current architecture boundary |
| License | [`LICENSE`](../LICENSE) | - | Legal instrument | Contains the Apache License 2.0 text |

The current active architecture decisions are [ADR-0001](decisions/0001-establish-replaceable-launcher-icon-rendering.md), [ADR-0002](decisions/0002-use-versioned-atomic-file-for-favorites.md), and [ADR-0003](decisions/0003-model-profile-completeness-for-favorite-reconciliation.md).

## Planned authoritative locations

Create the following documents only when real inputs exist:

| Path | Single responsibility | Creation condition | Language strategy |
| --- | --- | --- | --- |
| `docs/architecture.md` | System boundaries, components, dependencies, data flows, and technical direction | The selected stack or current product definition requires architectural conclusions | English by default; add Chinese when a sustained cross-language need exists |
| `docs/security.md` | Security model, threats, controls, and response process | Architecture, permissions, data flows, or distribution provide enough input for security analysis | English by default; specialist conclusions require review; translate when needed |
| `docs/privacy.md` | Data inventory, processing purposes, retention, and user rights | Data, permissions, regions, or third-party processing are confirmed | English by default; specialist conclusions require review; translate user-facing material as distribution requires |
| `CHANGELOG.md` | User-visible version changes | The first user-visible version or change exists | English public semantic source; add Chinese according to the actual audience |

## Product-document responsibilities

Maintain product information in three distinct responsibilities:

1. **Product direction:** `overview.md` records durable purpose, principles, capability layers, and long-term boundaries. A future `docs/roadmap.md` may record capability-layer movement from V1 to V2 to V3 to V4 and major project outcomes between those layers. It must remain more macroscopic than version or iteration planning.
2. **Current product definition:** Requirements Briefs and interaction specifications record current user behavior, state, constraints, and acceptance intent. They describe the current product rather than preserving version-by-version narrative history.
3. **Change rationale and delivery history:** Product decision records explain consequential scope choices. Iteration and version records describe project progress and implementation evolution without becoming a second copy of the current product definition.

### Interaction specifications

Create interaction specifications only when the applicable behavior is ready to be defined. Split them by page, dialog, or relatively independent feature module, for example:

- `docs/product/home.md`
- `docs/product/drawer.md`
- `docs/product/settings.md`
- `docs/product/<feature>.md`
- `docs/product/shared-components.md` when multiple specifications need a genuinely shared interaction contract

Each specification is the authoritative current definition for its own responsibility. It may link to shared component rules instead of duplicating them. It must not preserve a chronological history of every version or iteration; use product decisions, delivery records, and Git history for that purpose.

When an iteration changes current behavior, record the before-and-after delivery scope in the iteration record, follow `docs/product-decisions.md` when the author has enabled decision records, and update the affected current product specification in the same change or before integrating the implementation.

## Roadmap, versions, iterations, milestones, and completed records

These records answer different questions and must not replace one another or the current product definition.

### Roadmap

A future `docs/roadmap.md` records long-term capability-layer direction and major project outcomes. It may describe movement between V1, V2, V3, and V4, but it does not authorize later capability layers, prescribe detailed page behavior, or track ordinary implementation tasks.

### Version-delivery records

Use one stable `docs/delivery/<version>/` directory for each version from initial planning through completion, where `<version>` is the exact `versionName` without a `v` prefix. Keep the version summary and its iterations together:

```text
docs/delivery/<version>/
- delivery.md
- delivery.zh-CN.md
- iteration-<number>-<title>.md
- iteration-<number>-<title>.zh-CN.md
```

Follow [`docs/versions/version-delivery-format.md`](versions/version-delivery-format.md). `delivery.md` contains the selected product scope, necessary technical conclusions, included iterations, validation, limitations, completion criteria, and result. Create a separate technical assessment only when an independent review is genuinely needed; after resolution, place durable conclusions in their single authoritative current or delivery source instead of maintaining a permanent duplicate.

The directory name and path do not encode lifecycle state. `delivery.md` records whether the version is incomplete or completed and the evidence supporting that result.

### Milestones

For this project, a milestone is an exceptional baseline explicitly declared by the project author and represented by an approved Git tag. A GitHub Release is optional and exists only when the author also chooses an outward-facing publication. A formal version, iteration, unapproved tag, or approved tag not declared as a milestone does not become a milestone automatically. Milestones do not organize ordinary version delivery, and no `docs/milestones/` directory is used.

### Iteration records

Create iteration records beside `delivery.md` under `docs/delivery/<version>/` when implementation planning begins and an actual delivery iteration exists. Follow [`docs/iterations/iteration-record-format.md`](iterations/iteration-record-format.md).

- Iteration identifiers use one project-wide, monotonically increasing positive-integer sequence starting with `1`, without leading zeroes.
- Never renumber, reuse, or restart the sequence after a version completes.
- An iteration is a reviewable delivery unit. Its boundary is decided from implementation difficulty, expected time, change breadth, dependencies, technical risk, and validation cost together, not solely from the product hierarchy or a fixed number of features.
- An iteration may implement all or part of the current product definition, or combine tightly coupled work required to produce one verifiable result. It must not silently introduce scope absent from current product documents.
- Each record should state the objective, product-document references, before-and-after behavior where applicable, in-scope work, exclusions, dependencies, risks, affected code areas at a durable level, validation plan and evidence, related decisions and ADRs, commits or tags, and final outcome.
- Record detailed code evolution at the level of behavior, components, interfaces, data, architecture, build, migration, and validation consequences. Git commits and diffs remain authoritative for line-by-line source history.

Iteration records define their delivery scope while active and preserve factual delivery history after completion. They are not permanent copies of product requirements or architecture.

### Completed version records

When a version completes, retain its stable `docs/delivery/<version>/` path and update `delivery.md` as the factual completion summary and entry point. Completion changes the record's historical protection, not its location.

- The folder name remains the exact declared software version without a `v` prefix and follows [`docs/release.md`](release.md). Do not add lifecycle suffixes such as `-archived`. Tag presence is optional and does not determine whether a version is complete.
- Do not create a second authoritative copy under an archive, completed, or status-specific directory.
- The summary lists each included iteration as `<iteration identifier> - <title>` and links to the original iteration file in the same stable version directory.
- The summary records the version outcome, included iteration range or explicit set, important product changes, implementation evolution, decisions, migrations, validation evidence, known limitations, related tag or release when one exists, and the reason the version boundary was declared.
- Completion does not reset the project-wide iteration sequence. If one completed version contains iterations `iteration-7-...` through `iteration-10-...`, the next active iteration is `iteration-11-...`.
- Do not rewrite completed iteration records to make later history appear cleaner. Correct factual errors explicitly and preserve their original delivery meaning.
- Every formal version contains one or more completed iterations.

Do not create roadmap, version, or iteration files before their real planning or implementation inputs exist. Format documents may exist before individual delivery records because they govern how later records are created.

## Language and translation

- Temporary working notes and checklists do not require English versions and must not become dependencies of committed authoritative documents.
- README, the product overview, and agent instructions currently have English and Chinese versions.
- Translate other documents only when cross-language reading or the external audience creates a real need; do not create counterparts for structural symmetry.
- When authoring begins in Chinese, provide the public English version before treating the document pair as complete. The published English document is the external semantic source.
- Bilingual documents must preserve the same scope, constraints, and normative meaning. Correct material differences in the same change; when synchronization cannot be completed, explicitly record the discrepancy and follow-up.
- Use English for commit messages, pull requests, issues, release notes, and other public repository output.

## Updates and review

- Update affected authoritative documents immediately when product scope, architecture, data processing, platform, distribution channel, validation process, or another documented boundary changes.
- At the end of each formal version and milestone, review documentation entry points, links, status, and cross-document consistency.
- Review security, privacy, license, and release documentation again before the applicable release gate.
- Do not present plans, assumptions, or unresolved items as completed facts.
- For documentation-only changes, at minimum validate local Markdown links and inspect the Git diff and bilingual semantic alignment.

## Versioning and historical protection

- Update ordinary guides and current-state documents in the same change as the affected boundary; do not retain obsolete content as narrative history.
- Use append-only, zero-padded ADR names in the form `0001-<decision>.md`. Never renumber, reuse identifiers, or rewrite historical decisions. Apply the Toolkit ADR rule.
- Keep Requirements Brief boundaries and acceptance criteria traceable. Record material scope changes explicitly rather than silently overwriting the current product definition.
- Keep current product specifications current; preserve consequential rationale in product decisions and delivery history in version and iteration records.
- Product scope changes require an explicit decision from the project author and, when applicable, a technical impact assessment. A request becomes current product scope only when it is written into the applicable authoritative document.
- Security, privacy, and release records must preserve their applicable scope, version or date, and required specialist-review evidence.
- Move an obsolete document into historical storage only when it retains decision, audit, or migration value; otherwise delete it. Historical material must identify its replacement and must not be loaded as current guidance.

## Current-state document rule

- Repository-visible authoritative documents describe the current project or product state. They do not carry lifecycle status fields.
- Content that is not ready to become current project state remains in the conversation or an external continuation workspace such as `max-dev-context`; it does not enter this product repository as an operative document.
- Updating an authoritative current-state document changes the applicable current rule or definition. Preserve prior states through Git history, decisions, and completed delivery records rather than status labels inside the current document.
- When code exists, compare documentation, implementation, tests, and validation evidence before integrating a change. Resolve a material mismatch explicitly; neither side silently replaces the other.
- A completed `docs/delivery/<version>/` directory remains authoritative for the delivery history it describes but does not define current product or project rules. Its stable path does not make it active work.

## Git and task workflow

- The project currently follows one development line and does not maintain a multi-branch or multi-contributor workflow. Branching and collaboration conventions should be defined only if the author later introduces that need.
- Treat each independently reviewable operation as one task. Complete it, report the result and evidence, and wait for author confirmation before beginning the next task.
- Modifying files does not authorize committing them; committing does not authorize pushing them. An Agent may perform modify, commit, and push in one uninterrupted sequence only when the project author explicitly authorizes that serial continuation.
- A broad objective does not by itself authorize every later mutation in its delivery chain. Read-only inspection and validation needed to report the current task remain part of that task.
