# Project Documentation Map and Governance

> Semantic source for public use: English. Chinese counterpart and initial authoring version: [documentation.zh-CN.md](documentation.zh-CN.md).

## Purpose

This document defines the single authoritative location, creation condition, and maintenance rules for each type of Avenor Launcher project information. A planned path does not mean that its conclusions exist, and empty documents must not be created merely to complete the directory structure.

The project author is the first accountable person for all project matters. Security, privacy, legal, financial, or platform-policy conclusions must receive qualified specialist review when required.

## Current authoritative documents

| Information type | English or public entry | Chinese entry | Responsibility |
| --- | --- | --- | --- |
| Project entry | [`README.md`](../README.md) | [`README.zh-CN.md`](../README.zh-CN.md) | Provides the project summary and links to deeper documentation |
| Product overview | [`overview.md`](../overview.md) | [`overview.zh-CN.md`](../overview.zh-CN.md) | Records product direction, principles, boundaries, and unresolved scope |
| Agent routing | [`AGENTS.md`](../AGENTS.md) | [`AGENTS.zh-CN.md`](../AGENTS.zh-CN.md) | Records the Toolkit entry point and project-specific working rules |
| Documentation governance | [`docs/documentation.md`](documentation.md) | [`docs/documentation.zh-CN.md`](documentation.zh-CN.md) | Defines the document model and maintenance rules |
| Product foundation requirements | [`docs/requirements/product-foundation.md`](requirements/product-foundation.md) | [`docs/requirements/product-foundation.zh-CN.md`](requirements/product-foundation.zh-CN.md) | Records the product problem, author context, first-milestone boundary, acceptance intent, and open product questions |
| Product decisions and scope changes | [`docs/product-decisions.md`](product-decisions.md) | [`docs/product-decisions.zh-CN.md`](product-decisions.zh-CN.md) | Defines decision authority, scope-change handling, contract-mismatch handling, and the future decision-record format |
| Product navigation | [`docs/product/navigation.md`](product/navigation.md) | [`docs/product/navigation.zh-CN.md`](product/navigation.zh-CN.md) | Defines surface hierarchy, entry, exit, Back, restoration, and shared transitions |
| Home interaction | [`docs/product/home.md`](product/home.md) | [`docs/product/home.zh-CN.md`](product/home.zh-CN.md) | Defines Home information, favorites, launch behavior, and reorder mode |
| Drawer interaction | [`docs/product/drawer.md`](product/drawer.md) | [`docs/product/drawer.zh-CN.md`](product/drawer.zh-CN.md) | Defines the application inventory, grouping, sorting, alphabet index, and live updates |
| Application action sheet | [`docs/product/app-action-sheet.md`](product/app-action-sheet.md) | [`docs/product/app-action-sheet.zh-CN.md`](product/app-action-sheet.zh-CN.md) | Defines modal application shortcuts and Launcher actions |
| Settings interaction | [`docs/product/settings.md`](product/settings.md) | [`docs/product/settings.zh-CN.md`](product/settings.zh-CN.md) | Defines the first Settings information and behavior boundary |
| Product design foundations | [`docs/product/design-foundations.md`](product/design-foundations.md) | [`docs/product/design-foundations.zh-CN.md`](product/design-foundations.zh-CN.md) | Defines current theme, layout, typography, icon, accessibility, and resource principles |
| Product glossary | [`docs/product/glossary.md`](product/glossary.md) | [`docs/product/glossary.zh-CN.md`](product/glossary.zh-CN.md) | Defines canonical product terms |
| License | [`LICENSE`](../LICENSE) | — | Contains the Apache License 2.0 text |

## Planned authoritative locations

Create the following documents only when real inputs exist:

| Path | Single responsibility | Creation condition | Language strategy |
| --- | --- | --- | --- |
| `docs/architecture.md` | System boundaries, components, dependencies, data flows, and technical direction | The selected stack or first vertical slice requires architectural conclusions | English by default; add Chinese when a sustained cross-language need exists |
| `docs/decisions/` | Append-only architecture decision records | The first consequential technical decision is actually made | English by default; translate when needed |
| `docs/development.md` | Development environment, build, run, and troubleshooting guidance | The actual stack and authoritative commands are verified | English by default; translate when needed |
| `docs/validation.md` | Tests, static checks, manual validation, and release gates | Actual quality tools, commands, or validation processes are verified | English by default; translate when needed |
| `docs/security.md` | Security model, threats, controls, and response process | Architecture, permissions, data flows, or distribution provide enough input for security analysis | English by default; specialist conclusions require review; translate when needed |
| `docs/privacy.md` | Data inventory, processing purposes, retention, and user rights | Data, permissions, regions, or third-party processing are confirmed | English by default; specialist conclusions require review; translate user-facing material as distribution requires |
| `docs/release.md` | Versions, signing, channels, rollback, and release decisions | The first distribution channel and release process are confirmed | English by default; translate when needed |
| `CHANGELOG.md` | User-visible version changes | The first user-visible version or change exists | English public semantic source; add Chinese according to the actual audience |

## Product-document model

Maintain product documentation in three distinct layers:

1. **Product direction:** `overview.md` records durable purpose, principles, capability layers, and long-term boundaries. A future `docs/roadmap.md` may record capability-layer movement from V1 to V2 to V3 to V4 and the major milestones between those layers. It must remain more macroscopic than release or iteration planning.
2. **Current product contract:** Requirements Briefs and interaction specifications record the current user behavior, state, constraints, and acceptance intent. They describe the current product rather than preserving version-by-version narrative history.
3. **Change rationale and delivery history:** Product decision records explain consequential scope choices. Iteration records and version archives describe project progress and implementation evolution without becoming a second copy of the current product contract.

### Interaction specifications

Create interaction specifications only when the applicable behavior is ready to be defined. Split them by page, dialog, or relatively independent feature module, for example:

- `docs/product/home.md`
- `docs/product/drawer.md`
- `docs/product/settings.md`
- `docs/product/<feature>.md`
- `docs/product/shared-components.md` when multiple specifications need a genuinely shared interaction contract

Each specification is the authoritative current contract for its own responsibility. It may link to shared component rules instead of duplicating them. It must not preserve a chronological history of every version or iteration; use product decisions, iteration records, version archives, and Git history for that purpose.

When an iteration changes current behavior, record the before-and-after delivery scope in the iteration record, follow `docs/product-decisions.md` when the author has enabled decision records, and update the affected current product specification in the same change or before integrating the implementation.

## Roadmap, milestones, iterations, and version archives

These records answer different questions and must not replace one another.

### Roadmap

A future `docs/roadmap.md` records long-term capability-layer direction and major milestones. It may describe movement between V1, V2, V3, and V4, but it does not authorize later capability layers, prescribe detailed page behavior, or track ordinary implementation tasks.

### Milestone records

Milestone records describe a major project outcome, progress, deviations, evidence, and final conclusion. They link to authoritative product requirements and do not duplicate detailed product behavior. The first milestone remains to be discussed before its record location and contents are established.

### Iteration records

Use `docs/iterations/NNNN-<title>.md` when implementation planning begins and an actual delivery iteration exists.

- Iteration identifiers use one project-wide, zero-padded, monotonically increasing sequence starting with `0001`.
- Never renumber, reuse, or restart the sequence after a version archive.
- An iteration is a reviewable delivery unit. Its boundary is decided from implementation difficulty, expected time, change breadth, dependencies, technical risk, and validation cost together—not solely from the product hierarchy or a fixed number of features.
- An iteration may implement all or part of a feature in the current product contract, or combine tightly coupled work required to produce one verifiable result. It must not silently introduce scope absent from the current product documents.
- Each record should state the objective, product-document references, before-and-after behavior where applicable, in-scope work, exclusions, dependencies, risks, affected code areas at a durable level, validation plan and evidence, related decisions and ADRs, commits or tags, and final outcome.
- Record detailed code evolution at the level of behavior, components, interfaces, data, architecture, build, migration, and validation consequences. Git commits and diffs remain authoritative for line-by-line source history.

Iteration records are project and delivery records, not permanent copies of product requirements or architecture.

### Version archives

After a software version boundary is actually declared and its included iterations are closed, create a version folder such as `docs/archives/v1.1.0/`.

- The folder name uses the declared software version. Version numbering, tag naming, signing, and release policy remain undefined until an actual delivery and release baseline exists.
- Move the original included iteration records into the version folder; do not copy them while leaving a second canonical version under `docs/iterations/`.
- Add `README.md` inside the version folder as its summary and entry point.
- The summary lists each included iteration as `<iteration identifier> — <title>` and links to the original iteration file now stored in the same archive folder.
- The summary records the version outcome, included iteration range or explicit set, important product changes, implementation evolution, decisions, migrations, validation evidence, known limitations, related tag or release when one exists, and the reason the version boundary was declared.
- Archiving does not reset the project-wide iteration sequence. If `docs/archives/v1.1.0/` contains iterations `0005` through `0010`, the next active iteration under `docs/iterations/` is `0011`.
- Do not rewrite archived iteration records to make later history appear cleaner. Correct factual errors explicitly and preserve their original delivery meaning.
- Update every link that referenced an iteration when moving it into an archive folder.
- A version such as `1.0.0` to `1.1.0` may contain multiple iterations.

Do not create roadmap, milestone, iteration, or archive files before their real planning or implementation inputs exist.

## Language and translation

- Temporary working notes and checklists do not require English versions and must not become dependencies of committed authoritative documents.
- README, the product overview, and agent instructions currently have English and Chinese versions.
- Translate other documents only when cross-language reading or the external audience creates a real need; do not create counterparts for structural symmetry.
- During this project initialization, organize the author's intent in Chinese first and then provide the public English version. After publication, the public English document is the external semantic source.
- Bilingual documents must preserve the same scope, constraints, and normative meaning. Correct material differences in the same change; when synchronization cannot be completed, explicitly record the discrepancy and follow-up.
- Use English for commit messages, pull requests, issues, release notes, and other public repository output.

## Updates and review

- Update affected authoritative documents immediately when product scope, architecture, data processing, platform, distribution channel, validation process, or another documented boundary changes.
- At the end of each milestone, review documentation entry points, links, status, and cross-document consistency.
- Review security, privacy, license, and release documentation again before the applicable release gate.
- Do not present plans, assumptions, or unresolved items as completed facts.
- For documentation-only changes, at minimum validate local Markdown links and inspect the Git diff and bilingual semantic alignment.

## Versioning and archival

- Update ordinary guides and current-state documents in the same change as the affected boundary; do not retain obsolete content as narrative history.
- Use append-only, zero-padded ADR names in the form `0001-<decision>.md`. Never renumber, reuse identifiers, or rewrite historical decisions. Create and cross-link a new ADR when replacing one.
- Keep Requirements Brief boundaries and acceptance criteria traceable. Record material scope changes explicitly rather than silently overwriting the current contract.
- Keep current product specifications current; preserve consequential rationale in product decisions and delivery history in iteration records and version archives.
- Product scope changes require an explicit decision from the project author and, when applicable, a technical impact assessment. A request becomes current product scope only when it is written into the applicable authoritative document.
- Security, privacy, and release records must preserve their applicable scope, version or date, and required specialist-review evidence.
- Move an obsolete document into historical storage only when it retains decision, audit, or migration value; otherwise delete it. Historical material must identify its replacement and must not be loaded as current guidance.

## Current-state document rule

- Repository-visible authoritative documents describe the current project or product state. They do not carry lifecycle status fields.
- Content that is not ready to become current project state remains in the conversation or an external continuation workspace such as `max-dev-context`; it does not enter this product repository as an operative document.
- Updating an authoritative document changes the current contract. Preserve prior states through Git history, iteration records, and version archives rather than status labels inside the current document.
- When code exists, compare documentation, implementation, tests, and validation evidence before integrating a change. A difference is a contract mismatch to resolve, not evidence that one side silently replaces the other.
- Historical files stored under `docs/archives/<version>/` are excluded from the current contract by location rather than an in-document status field.

## Git and task workflow

- The project currently follows one development line and does not maintain a multi-branch or multi-contributor workflow. Branching and collaboration conventions should be defined only if the author later introduces that need.
- Treat each independently reviewable operation as one task. Complete it, report the result and evidence, and wait for author confirmation before beginning the next task.
- Modifying files does not authorize committing them; committing does not authorize pushing them. An Agent may perform modify, commit, and push in one uninterrupted sequence only when the project author explicitly authorizes that serial continuation.
- A broad objective does not by itself authorize every later mutation in its delivery chain. Read-only inspection and validation needed to report the current task remain part of that task.
