# Project Documentation Map and Governance

> Semantic source for public use: English. Chinese counterpart and initial authoring version: [documentation.zh-CN.md](documentation.zh-CN.md).

## Purpose

This document defines the single authoritative location, creation condition, and maintenance rules for each type of Avenor Launcher project information. A planned path does not mean that its conclusions exist, and empty documents must not be created merely to complete the directory structure.

The project author is the first accountable person for all project matters. Security, privacy, legal, financial, or platform-policy conclusions must receive qualified specialist review when required.

## Current authoritative documents

| Information type | English or public entry | Chinese entry | Status and responsibility |
| --- | --- | --- | --- |
| Project entry | [`README.md`](../README.md) | [`README.zh-CN.md`](../README.zh-CN.md) | Established; provides the project summary and links to deeper documentation |
| Product overview | [`overview.md`](../overview.md) | [`overview.zh-CN.md`](../overview.zh-CN.md) | Established; records product intent, principles, boundaries, and unresolved scope |
| Agent routing | [`AGENTS.md`](../AGENTS.md) | [`AGENTS.zh-CN.md`](../AGENTS.zh-CN.md) | Established; records the Toolkit entry point and project-specific working rules |
| Documentation governance | [`docs/documentation.md`](documentation.md) | [`docs/documentation.zh-CN.md`](documentation.zh-CN.md) | Established; English and Chinese versions of this document |
| Product foundation requirements | [`docs/requirements/product-foundation.md`](requirements/product-foundation.md) | [`docs/requirements/product-foundation.zh-CN.md`](requirements/product-foundation.zh-CN.md) | Approved baseline; records the product problem, author context, first-milestone boundary, acceptance intent, and open product questions |
| License | [`LICENSE`](../LICENSE) | — | Established; Apache License 2.0 text |

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
| `docs/release.md` | Versions, signing, channels, rollback, and release approval | The first distribution channel and release process are confirmed | English by default; translate when needed |
| `CHANGELOG.md` | User-visible version changes | The first user-visible version or change exists | English public semantic source; add Chinese according to the actual audience |

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
- Keep Requirements Brief boundaries and acceptance criteria traceable. Record material scope changes explicitly rather than silently overwriting approved commitments.
- Product scope changes require approval from the project author and, when applicable, a technical impact assessment. Unapproved requests are not current commitments.
- Security, privacy, and release records must preserve their applicable scope, version or date, and required specialist-review evidence.
- Archive an obsolete document only when it retains decision, audit, or migration value; otherwise delete it. Archived material must identify its replacement and must not be loaded as current guidance.
