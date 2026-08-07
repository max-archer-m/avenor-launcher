# Agent Routing

> Semantic source: English. Chinese counterpart: [AGENTS.zh-CN.md](AGENTS.zh-CN.md).

## Bootstrap

Before proposing or editing project documentation or code:

1. Read `../max-ai-toolkit/prompts/project-bootstrap.md`.
2. Follow the Toolkit routing and progressive-consumption guidance.
3. Read this file and only the project documents relevant to the task.

The approved Toolkit locator is `../max-ai-toolkit`. The Toolkit provides reusable methods; this repository remains authoritative for Avenor Launcher product facts, requirements, architecture, decisions, implementation, validation, and releases.

## Project identity

- Product name: Avenor Launcher
- Repository: `avenor-launcher`
- Project ownership and decision authority: The project author
- Internal codename: To be decided

## Project entry documents

- Public project entry: [README.md](README.md)
- Chinese public project entry: [README.zh-CN.md](README.zh-CN.md)
- Product overview semantic source: [overview.md](overview.md)
- Chinese product overview: [overview.zh-CN.md](overview.zh-CN.md)
- Pre-development checklist and open questions: [todo.md](todo.md)
- Documentation map and governance: [docs/documentation.md](docs/documentation.md)
- Product foundation requirements: [docs/requirements/product-foundation.md](docs/requirements/product-foundation.md)
- License: [LICENSE](LICENSE)

Architecture, development, validation, security, privacy, and release documents have not yet been established. Do not infer their contents or treat planned paths as current evidence.

## Language

English is the semantic source for public project documentation and repository-facing output. Chinese may be used for internal reasoning and maintained `.zh-CN.md` counterparts. When a bilingual pair differs materially, update the English source first and keep the Chinese counterpart aligned in the same change whenever practical.

Use English for commit messages, pull requests, issues, release notes, and other public repository output.

## Project-specific rules

- Preserve the product principles and confirmed boundaries in `overview.md`.
- Mark unknown product or technical facts as `To be decided`; do not turn assumptions into decisions.
- Keep product decisions separate from technical architecture decisions.
- Do not prescribe implementation details from product documentation.
- Update authoritative documentation when its product contract or boundary changes.
- This repository overrides Toolkit guidance only when an override is explicit, scoped, and recorded here or in an applicable decision record.
- The project author is the first accountable person for all project matters. Obtain qualified specialist review when a security, privacy, legal, financial, or platform-policy conclusion requires expertise.

## Validation baseline

No build, test, lint, static-analysis, emulator, or device command is authoritative yet because the technical stack has not been selected. For documentation-only changes, inspect the Git diff and verify local Markdown links. Do not report product build or test results until corresponding commands are documented and executed.
