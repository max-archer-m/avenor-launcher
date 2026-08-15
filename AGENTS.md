# Agent Routing

> Semantic source: English. Chinese counterpart: [AGENTS.zh-CN.md](AGENTS.zh-CN.md).

## Bootstrap

Before proposing or editing project documentation or code:

1. Read `../max-ai-toolkit/prompts/project-bootstrap.md`.
2. Follow the Toolkit routing and progressive-consumption guidance.
3. Read this file and only the project documents relevant to the task.

The Toolkit locator is `../max-ai-toolkit`. The Toolkit provides reusable methods; this repository remains authoritative for Avenor Launcher product facts, requirements, architecture, decisions, implementation, validation, and releases.

## Project identity

- Product name: Avenor Launcher
- Repository: `avenor-launcher`
- Project ownership and decision authority: The project author

## Project entry documents

- Public project entry: [README.md](README.md)
- Chinese public project entry: [README.zh-CN.md](README.zh-CN.md)
- Product overview semantic source: [overview.md](overview.md)
- Chinese product overview: [overview.zh-CN.md](overview.zh-CN.md)
- Documentation map and governance: [docs/documentation.md](docs/documentation.md)
- Development guide: [docs/development.md](docs/development.md)
- Validation guide: [docs/validation.md](docs/validation.md)
- Product foundation requirements: [docs/requirements/product-foundation.md](docs/requirements/product-foundation.md)
- Privacy and data handling: [docs/product/features/privacy.md](docs/product/features/privacy.md)
- Version, artifact, and release governance: [docs/release.md](docs/release.md)
- Version and iteration delivery formats: [docs/versions/version-delivery-format.md](docs/versions/version-delivery-format.md) and [docs/iterations/iteration-record-format.md](docs/iterations/iteration-record-format.md)
- License: [LICENSE](LICENSE)

System architecture and security documents have not yet been established. The current product Privacy statement and data-handling boundary are defined in the product document linked above; it is not a specialist legal or store-approval conclusion. The development and validation guides record the current minimum project baseline without claiming unperformed commands or results. Architecture decisions exist only where an active ADR records their exact scope; do not infer broader architecture from an ADR or treat implementation assessments and other planned paths as current architecture evidence.

## Language

English is the semantic source for public project documentation and repository-facing output. Chinese may be used for internal reasoning and maintained `.zh-CN.md` counterparts. When a bilingual pair differs materially, update the English source first and keep the Chinese counterpart aligned in the same change whenever practical.

Use English for commit messages, pull requests, issues, release notes, and other public repository output.

## Project-specific rules

- Preserve the product principles and confirmed boundaries in `overview.md`.
- Mark unknown product or technical facts as `To be decided`; do not turn assumptions into decisions.
- Keep product decisions separate from technical architecture decisions.
- Do not prescribe implementation details from product documentation.
- Do not persist the conversation's current working phase as a project-document status. Continuation state may be kept outside this product repository; durable product contracts and project constraints remain in this repository.
- Repository-visible authoritative documents describe the current project and product contract; do not infer a separate document lifecycle from their presence.
- Before integrating implementation, compare the current documentation, code, tests, and validation evidence. Treat any material difference as a contract mismatch and resolve it explicitly.
- Production implementation requires an applicable current product definition and explicit authorization from the project author. When the author authorizes a defined task or iteration, that authorization covers the documentation, code, tests, and local validation needed to complete its approved scope; do not stop for separate confirmation between those in-scope steps.
- Stop and obtain separate author direction when new evidence would materially change product scope or acceptance intent, require a consequential architecture or technical decision, introduce a specialist-review need, or otherwise exceed the authorized task or iteration. Signing credentials, commits, pushes, tags, uploads, publication, and other remote or difficult-to-reverse actions remain separately authorized unless the author explicitly includes the exact action in the current authorization.
- When the Android project is created, use this repository root as its project root. Do not place the Android project inside another nested repository or wrapper project without an explicit author decision.
- In Android UI code, do not hard-code user-facing strings, semantic colors, or reusable dimensions. Define them in the applicable `res/values` XML resources and access them through the project's resource or theme layer; keep all user-facing strings localizable. A drawable or vector XML asset may retain an intrinsic fallback color or size when it belongs to the asset itself and the consuming UI explicitly controls the applicable semantic tint or rendered size. Do not use this asset exception to bypass reusable design tokens.
- Do not automatically run Gradle for routine code authoring or review. The project author normally performs the build, installation, and device-use check and may report the observed result as iteration evidence. Run Gradle through an agent only when the project author explicitly requests it or an authorized formal-version or focused-validation task requires it. Absence of an agent-run Gradle command is not by itself an iteration entry or exit blocker.
- Update authoritative documentation when its product contract or boundary changes.
- This repository overrides Toolkit guidance only when an override is explicit, scoped, and recorded here or in an applicable decision record.
- The project author is the first accountable person for all project matters. Obtain qualified specialist review when a security, privacy, legal, financial, or platform-policy conclusion requires expertise.

## Development lines and branches

- An iteration defines a delivery scope and its acceptance result. Do not bind an iteration to a terminal, contributor, branch, planned start order, or forecast completion date. Record real dependencies where they exist; otherwise treat line assignments, sequence, and dates as adjustable execution plans.
- Authorized work may use one task line or multiple concurrent task lines. Use concurrent lines only when their scopes have clear ownership, independently verifiable results, and low overlap in code, files, interfaces, and mutable project state. By default, do not have more than two active task lines; exceeding two requires an explicit project-author decision.
- A task line is an execution assignment, not a durable project status. A terminal or contributor may change, and an iteration may be reassigned between lines, without changing the iteration scope or status unless its delivery facts have actually changed. A planned line that cannot start remains queued or unassigned.
- Serialize work, or assign it to one line, when tasks would concurrently change the same files, shared contracts, public interfaces, build configuration, migrations, or other integration-sensitive state. Record real dependencies and the intended integration order where they affect execution.
- Create branches just in time for active, authorized work, and only when isolation from another active line or from the current development state is useful. Do not pre-create branches for forecast work or inactive lines. If two lines were planned but only one can start, create at most the branch needed by that active line; leave the other work queued without a placeholder branch.
- Prefer one short-lived branch for each currently active, independently integrable task or iteration. Do not create a long-lived branch named after a terminal or accumulate several future iterations on a terminal-specific branch. Use scope-based lowercase names: `work/iteration-<number>-<short-slug>` for iteration work and `work/task-<short-slug>` for other tasks.
- Before integrating a line, re-check the target branch, working tree, relevant documentation, code, tests, and validation evidence. Resolve contract mismatches and cross-line conflicts explicitly, and integrate dependent lines in dependency order.
- Creating or switching a local branch under an authorized task-line arrangement is a reversible coordination step. It does not authorize a commit, push, merge, remote-branch write, branch deletion, history rewrite, or a separate queued task. Those actions continue to follow their applicable authorization rules.
- Unless the project author has authorized a multi-line arrangement with named scopes, complete one authorized task or iteration through its in-scope local work, report its result, and wait for confirmation before starting a separate task or iteration. Do not treat modify, commit, and push as one automatically authorized sequence.

## Development and validation baseline

Follow [docs/development.md](docs/development.md) for the current project configuration and development entry points, and [docs/validation.md](docs/validation.md) for applicable checks, execution authority, device evidence, and result reporting. A documented command is an available entry point, not evidence that it has run successfully. For documentation-only changes, inspect the Git diff and verify local Markdown links. Record author-reported build, installation, and device observations as such without inferring missing commands, environments, or results. Do not report an agent-run product build or test result unless the corresponding command was actually executed. A notice that a newer tool or dependency version exists is advisory maintenance information, not a failing iteration result or an obligation to update immediately.
