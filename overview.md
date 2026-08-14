# Avenor Launcher Project Overview

> Semantic source: English. Chinese counterpart: [overview.zh-CN.md](overview.zh-CN.md).
>
> This document records confirmed product intent and explicitly marks unresolved scope. It may be revised or replaced as product discovery progresses.

## Purpose

### Problem

An Android home screen should help people reach the application or device information they need quickly and accurately. Many launchers add widgets, feeds, advertisements, recommendations, and extensive customization until the home screen itself becomes another attention-demanding destination.

Avenor Launcher begins with the author's daily need for a quieter and more direct path to applications and essential device information. Broader user demand and market differentiation remain hypotheses to validate.

### Product goal

Provide a restrained Android home screen that helps users find and open what they currently need with minimal distraction and cognitive overhead.

## Intended users

- Primary user: the author, who will judge whether the product is suitable for everyday use.
- Potential future users: Android users who prefer a calm, efficient, bilingual launcher experience.
- Explicitly unsupported user groups: To be decided through product discovery rather than inferred from the current concept.

## Product principles

- **Calm and intentional:** Every default element and interaction must justify its contribution to finding what the user needs.
- **Personal-first:** The author's willingness to use the product every day is the first product filter, while broader applicability is evaluated separately.
- **Direct Home access:** Restraint limits distraction, cognitive overhead, and avoidable steps; it does not require Home to contain few applications. Favorites remain available in Home's two fixed groups without paging, folders, or another reveal surface. Each group scrolls only when its content exceeds its visible region.
- **Privacy-conscious:** Do not collect data unrelated to core functionality. Any future data access must be necessary, disclosed, minimized, and reviewed before implementation.
- **User control:** Avoid dark patterns, forced engagement, misleading permission requests, and designs intended to maximize time spent in the launcher.
- **Incremental evolution:** Grow one coherent product over time instead of maintaining unrelated variants.

## Confirmed boundaries

- Product form: an Android launcher intended to act as the device's default home-screen application.
- User-facing names: “Avenor Launcher” in English and “Avenor 启动器” in Simplified Chinese.
- Supported languages: English and Simplified Chinese.
- Distribution boundary: maintained on GitHub for the author's daily use, with no public distribution or store-release requirement.
- Platform baseline: ordinary Android phones in portrait orientation, with Android 12 (API 31) as the minimum supported version and Android 16–17 (API 36–37) as the primary physical-validation range.
- Author-centered ergonomics: interaction and placement decisions primarily optimize for right-hand holding with right-thumb input and left-hand holding with right-hand tapping. Other postures remain usable where practical but are not equal product-optimization targets.
- License: Apache License 2.0.
- Advertising and recommendation feeds are out of scope.

## Version direction

The following capability layers are a non-binding directional outlook. They are not a committed product roadmap, release plan, delivery sequence, or guarantee that any later capability will be implemented:

- **V1 — Fixed presentation:** User-controlled, deterministic presentation of applications and selected device information; no behavior-based automatic rearrangement.
- **V2 — Basic adaptation:** Optional behavior-based presentation or ordering, subject to explicit privacy, user-control, and validation requirements.
- **V3 — AI assistance:** A possible future direction outside the current product scope.
- **V4 — Agent integration:** A possible future direction outside the current product scope.

These labels describe capability direction rather than semantic software versions. They do not authorize V2–V4 work.

## Current product scope

The current product contract defines a minimum daily-use utility loop: Home, Drawer, application launching, and the necessary Settings. Home uses an approximate 20:60:20 vertical composition for system time and date, the favorite composition, and a reserved bottom secondary region on one non-pageable, non-collapsible surface. The bottom capability itself remains undefined and the favorite composition does not expand into its reserved region. Primary and companion favorites share the middle composition, using approximately the left 60% and right 40% respectively. Each group scrolls independently only after its content exceeds the visible region; both remain labeled Home favorites, while their different prominence represents relative usage frequency rather than importance or availability. Drawer uses a single-list presentation and includes every launchable entry successfully read from the sources Android exposes, including cloned application entries when the platform exposes and Avenor successfully reads them. An isolated non-current-profile read failure does not block entries already available from other profiles. Its core tasks must remain fully local and offline. Widgets, folders, themes, extensive customization, network-backed information, accounts, cloud synchronization, and server development are outside the current product scope.

The detailed boundary and evidence are recorded in the [product foundation requirements](docs/requirements/product-foundation.md). The following remain unresolved:

- Required Android roles, permissions, background capabilities, or package visibility
- Analytics, crash reporting, payment, or other third-party services
- Commercial model, which is intentionally deferred

In particular, this overview does not place `QUERY_ALL_PACKAGES` or any other sensitive capability in the product scope. The product requires discovery of launchable entries, not unrestricted access to installed-package data. The implementation must use the smallest Android visibility scope that satisfies the current user journey.

The current product does not support Android Private Space. Avenor does not declare `ACCESS_HIDDEN_PROFILES`, actively access or display Private Space entries, or provide a Private Space container or controls for visibility, locking, unlocking, favorites, or state restoration. “Every launchable entry” means only entries Android normally exposes within Avenor's current role and least-privilege boundary. Ordinary, work-profile, and cloned entries that the platform exposes without hidden-profile access remain governed by the current product contract. Future Private Space support is a separate product capability requiring a new author decision and renewed interaction, permission, privacy, compatibility, and validation review.

## Additive requirements

An additive requirement is a capability that may be delivered when useful without defining or blocking the transition between V1, V2, and later capability layers. Landscape support, foldable and tablet adaptation, themes and colors, weather information, and widgets are current examples. Folder-like grouping is not an additive candidate because it conflicts with the current flat two-group Home organization and adds a secondary reveal hierarchy.

Additive requirements are not automatically part of the current scope. Each must still pass the Feature decision test, be explicitly added to the current product contract, and satisfy applicable privacy, security, validation, and maintenance constraints.

## Feature decision test

Before a feature enters the current scope, answer:

1. Does it make finding the needed application or information faster, more accurate, or easier?
2. Does the default experience remain calm and understandable?
3. Would the primary user use it regularly?
4. Can users understand, control, disable, or recover from it where appropriate?
5. Are its privacy, accessibility, security, platform, and maintenance implications acceptable?

Features that do not pass this test should be rejected, deferred, or redesigned.

## Engineering intent

Engineering should prefer mature, maintainable choices, minimal dependencies, clear boundaries, safe defaults, and evidence-based validation. These are constraints on future technical evaluation, not a selection of a specific architecture, framework, API, or abstraction.

AI is expected to perform a substantial share of project execution under human review. The project author remains accountable for product direction, technical decisions, quality, and release decisions.

## Governance and documentation

- Project ownership and decision authority: the project author
- Agent and Toolkit routing: [AGENTS.md](AGENTS.md)
- Documentation map and governance: [docs/documentation.md](docs/documentation.md)
- License: [LICENSE](LICENSE)

Architecture, development, validation, security, privacy, and release documentation will be established as their inputs are confirmed. Planned document locations must not be treated as completed evidence.
