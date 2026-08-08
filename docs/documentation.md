# Project Documentation Map and Governance

> Semantic source for public use: English. Chinese counterpart: [documentation.zh-CN.md](documentation.zh-CN.md).

## Purpose

This document defines the single authoritative location, creation condition, and maintenance rules for each type of Avenor Launcher project information. A planned path does not mean that its conclusions exist, and empty documents must not be created merely to complete the directory structure.

The project author is the first accountable person for all project matters. Security, privacy, legal, financial, or platform-policy conclusions must receive qualified specialist review when required.

## Contract-document model

A **contract document** is a repository-authoritative document whose statements either govern a defined scope and period or authoritatively record completed facts for that scope and period. “Contract” does not mean that every document defines current product behavior. Each contract document has a contract class, applicability, and time boundary that determine how it must be interpreted.

| Contract class | Governing question | Applicability |
| --- | --- | --- |
| Project-governance contract | How is this project directed, documented, authorized, and maintained? | Applies to project work while the rule remains in the current governance documents |
| Current product contract | What product behavior, user-visible state, scope, terminology, and acceptance intent apply now? | Applies to the current product definition until an authorized change updates the applicable document |
| Delivery contract | What must a roadmap boundary, milestone, or iteration deliver, exclude, validate, and report? | Applies to the named delivery scope; it selects work from the current product contract but does not independently redefine that contract |
| Technical or operational contract | How must the current system be designed, built, validated, secured, operated, or released? | Applies to the documented technical or operational boundary and must remain compatible with the current product contract |
| Historical contract record | What scope, decisions, implementation changes, validation evidence, and outcome actually applied in a completed period? | Remains authoritative for that historical period but does not govern current product behavior |
| Legal instrument | What legal permissions, obligations, or restrictions apply? | Applies according to the instrument's own terms and scope |

Contract classes interact as follows:

- Product direction, Requirements Briefs, interaction specifications, design foundations, and the product glossary form the current product contract for their respective responsibilities.
- A delivery contract may commit to implementing all or part of the current product contract. It may add delivery sequencing, affected areas, exclusions, risks, and validation evidence, but it must not introduce or change product behavior unless the applicable current product contract is updated through an authorized product decision.
- A milestone may aggregate multiple iterations. An iteration is independently reviewable delivery scope, not a substitute for the product definition.
- When a delivery contract completes, its final result remains authoritative delivery history. Moving it into a version archive changes its temporal applicability, not its authority or factual meaning.
- Architecture, development, validation, security, privacy, and release documents become technical or operational contracts only after their real inputs and conclusions exist.
- Git history supports provenance but does not replace the applicable contract document.
- Temporary prompts, scratch notes, unchecked task lists, and conversation transcripts are working materials, not contract documents. They must not be committed as authoritative project documentation or used to override a contract.

## Current authoritative documents

| Information type | English or public entry | Chinese entry | Contract class | Responsibility |
| --- | --- | --- | --- | --- |
| Project entry | [`README.md`](../README.md) | [`README.zh-CN.md`](../README.zh-CN.md) | Current product contract | Provides the current project and product summary and links to deeper documentation |
| Product overview | [`overview.md`](../overview.md) | [`overview.zh-CN.md`](../overview.zh-CN.md) | Current product contract | Records product direction, principles, boundaries, and unresolved scope |
| Agent routing | [`AGENTS.md`](../AGENTS.md) | [`AGENTS.zh-CN.md`](../AGENTS.zh-CN.md) | Project-governance contract | Records the Toolkit entry point and project-specific working rules |
| Documentation governance | [`docs/documentation.md`](documentation.md) | [`docs/documentation.zh-CN.md`](documentation.zh-CN.md) | Project-governance contract | Defines the contract-document model and maintenance rules |
| Product foundation requirements | [`docs/requirements/product-foundation.md`](requirements/product-foundation.md) | [`docs/requirements/product-foundation.zh-CN.md`](requirements/product-foundation.zh-CN.md) | Current product contract | Records the product problem, author context, current scope, acceptance intent, and open product questions |
| Product decisions and scope changes | [`docs/product-decisions.md`](product-decisions.md) | [`docs/product-decisions.zh-CN.md`](product-decisions.zh-CN.md) | Project-governance contract; future records are historical contract records | Defines decision authority, scope-change handling, contract-mismatch handling, and the future decision-record format |
| Product navigation | [`docs/product/navigation.md`](product/navigation.md) | [`docs/product/navigation.zh-CN.md`](product/navigation.zh-CN.md) | Current product contract | Defines surface hierarchy, entry, exit, Back, restoration, and shared transitions |
| Home interaction | [`docs/product/home.md`](product/home.md) | [`docs/product/home.zh-CN.md`](product/home.zh-CN.md) | Current product contract | Defines Home information, favorites, launch behavior, and reorder mode |
| Drawer interaction | [`docs/product/drawer.md`](product/drawer.md) | [`docs/product/drawer.zh-CN.md`](product/drawer.zh-CN.md) | Current product contract | Defines the application inventory, grouping, sorting, alphabet index, and live updates |
| Application action sheet | [`docs/product/app-action-sheet.md`](product/app-action-sheet.md) | [`docs/product/app-action-sheet.zh-CN.md`](product/app-action-sheet.zh-CN.md) | Current product contract | Defines modal application shortcuts and Launcher actions |
| Settings interaction | [`docs/product/settings.md`](product/settings.md) | [`docs/product/settings.zh-CN.md`](product/settings.zh-CN.md) | Current product contract | Defines current Settings information and behavior |
| Product design foundations | [`docs/product/design-foundations.md`](product/design-foundations.md) | [`docs/product/design-foundations.zh-CN.md`](product/design-foundations.zh-CN.md) | Current product contract | Defines current theme, layout, typography, icon, accessibility, and resource principles |
| Product glossary | [`docs/product/glossary.md`](product/glossary.md) | [`docs/product/glossary.zh-CN.md`](product/glossary.zh-CN.md) | Current product contract | Defines canonical product terms |
| Version, artifact, and release governance | [`docs/release.md`](release.md) | [`docs/release.zh-CN.md`](release.zh-CN.md) | Technical or operational contract | Defines application versions, archives, APK artifacts, signing continuity, tags, and GitHub Releases |
| License | [`LICENSE`](../LICENSE) | — | Legal instrument | Contains the Apache License 2.0 text |

## Planned authoritative locations

Create the following documents only when real inputs exist:

| Path | Single responsibility | Creation condition | Language strategy |
| --- | --- | --- | --- |
| `docs/architecture.md` | System boundaries, components, dependencies, data flows, and technical direction | The selected stack or current product contract requires architectural conclusions | English by default; add Chinese when a sustained cross-language need exists |
| `docs/decisions/` | Append-only architecture decision records | The first consequential technical decision is actually made | English by default; translate when needed |
| `docs/development.md` | Development environment, build, run, and troubleshooting guidance | The actual stack and authoritative commands are verified | English by default; translate when needed |
| `docs/validation.md` | Tests, static checks, manual validation, and release gates | Actual quality tools, commands, or validation processes are verified | English by default; translate when needed |
| `docs/security.md` | Security model, threats, controls, and response process | Architecture, permissions, data flows, or distribution provide enough input for security analysis | English by default; specialist conclusions require review; translate when needed |
| `docs/privacy.md` | Data inventory, processing purposes, retention, and user rights | Data, permissions, regions, or third-party processing are confirmed | English by default; specialist conclusions require review; translate user-facing material as distribution requires |
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

These delivery contracts and historical contract records answer different questions and must not replace one another or the current product contract.

### Roadmap

A future `docs/roadmap.md` records long-term capability-layer direction and major milestones. It may describe movement between V1, V2, V3, and V4, but it does not authorize later capability layers, prescribe detailed page behavior, or track ordinary implementation tasks.

### Milestone records

Milestone records describe a major project outcome, progress, deviations, evidence, and final conclusion. They link to authoritative product requirements and do not duplicate detailed product behavior. Create their location and contents only when a real milestone contract exists.

### Iteration records

Use `docs/iterations/NNNN-<title>.md` when implementation planning begins and an actual delivery iteration exists.

- Iteration identifiers use one project-wide, zero-padded, monotonically increasing sequence starting with `0001`.
- Never renumber, reuse, or restart the sequence after a version archive.
- An iteration is a reviewable delivery unit. Its boundary is decided from implementation difficulty, expected time, change breadth, dependencies, technical risk, and validation cost together—not solely from the product hierarchy or a fixed number of features.
- An iteration may implement all or part of a feature in the current product contract, or combine tightly coupled work required to produce one verifiable result. It must not silently introduce scope absent from the current product documents.
- Each record should state the objective, product-document references, before-and-after behavior where applicable, in-scope work, exclusions, dependencies, risks, affected code areas at a durable level, validation plan and evidence, related decisions and ADRs, commits or tags, and final outcome.
- Record detailed code evolution at the level of behavior, components, interfaces, data, architecture, build, migration, and validation consequences. Git commits and diffs remain authoritative for line-by-line source history.

Iteration records are delivery contracts while their scope is active and authoritative historical contract records after completion. They are not permanent copies of product requirements or architecture.

### Version archives

After a software version boundary is actually declared and its included iterations are closed, create a version folder such as `docs/archives/v1.1.0/`.

- The folder name uses the declared software version and follows [`docs/release.md`](release.md). Tag presence is optional and does not determine whether a formal version archive exists.
- Move the original included iteration records into the version folder; do not copy them while leaving a second canonical version under `docs/iterations/`.
- Add `README.md` inside the version folder as its summary and entry point.
- The summary lists each included iteration as `<iteration identifier> — <title>` and links to the original iteration file now stored in the same archive folder.
- The summary records the version outcome, included iteration range or explicit set, important product changes, implementation evolution, decisions, migrations, validation evidence, known limitations, related tag or release when one exists, and the reason the version boundary was declared.
- Archiving does not reset the project-wide iteration sequence. If `docs/archives/v1.1.0/` contains iterations `0005` through `0010`, the next active iteration under `docs/iterations/` is `0011`.
- Do not rewrite archived iteration records to make later history appear cleaner. Correct factual errors explicitly and preserve their original delivery meaning.
- Update every link that referenced an iteration when moving it into an archive folder.
- Every formal version contains one or more completed iterations.

Do not create roadmap, milestone, iteration, or archive files before their real planning or implementation inputs exist.

## Language and translation

- Temporary working notes and checklists do not require English versions and must not become dependencies of committed authoritative documents.
- README, the product overview, and agent instructions currently have English and Chinese versions.
- Translate other documents only when cross-language reading or the external audience creates a real need; do not create counterparts for structural symmetry.
- When authoring begins in Chinese, provide the public English version before treating the document pair as complete. The published English document is the external semantic source.
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
- Historical files stored under `docs/archives/<version>/` remain authoritative historical contract records. Their location excludes them from the current product, governance, technical, and delivery contracts; it does not remove their authority for the historical period they describe.

## Git and task workflow

- The project currently follows one development line and does not maintain a multi-branch or multi-contributor workflow. Branching and collaboration conventions should be defined only if the author later introduces that need.
- Treat each independently reviewable operation as one task. Complete it, report the result and evidence, and wait for author confirmation before beginning the next task.
- Modifying files does not authorize committing them; committing does not authorize pushing them. An Agent may perform modify, commit, and push in one uninterrupted sequence only when the project author explicitly authorizes that serial continuation.
- A broad objective does not by itself authorize every later mutation in its delivery chain. Read-only inspection and validation needed to report the current task remain part of that task.
