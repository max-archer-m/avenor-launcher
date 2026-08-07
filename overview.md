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
- **Privacy-conscious:** Do not collect data unrelated to core functionality. Any future data access must be necessary, disclosed, minimized, and reviewed before implementation.
- **User control:** Avoid dark patterns, forced engagement, misleading permission requests, and designs intended to maximize time spent in the launcher.
- **Incremental evolution:** Grow one coherent product over time instead of maintaining unrelated variants.

## Confirmed boundaries

- Product form: an Android launcher intended to act as the device's default home-screen application.
- User-facing names: “Avenor Launcher” in English and “Avenor 启动器” in Simplified Chinese.
- Initial languages: English and Simplified Chinese.
- Initial use: maintained on GitHub for the author's daily use, with no V1 distribution or store-release requirement.
- Platform baseline: ordinary Android phones in portrait orientation, with Android 16 (API 36) as the minimum and Android 17 (API 37) as the target.
- License: Apache License 2.0.
- Advertising and recommendation feeds are out of scope.

## Version direction

Major versions describe capability layers, not committed release dates or complete feature lists:

- **V1 — Fixed presentation:** User-controlled, deterministic presentation of applications and selected device information; no behavior-based automatic rearrangement.
- **V2 — Basic adaptation:** Optional behavior-based presentation or ordering, subject to explicit privacy, user-control, and validation requirements.
- **V3 — AI assistance:** A possible future direction outside the current product scope.
- **V4 — Agent integration:** A possible future direction outside the current product scope.

This direction does not authorize V2–V4 work. The detailed first vertical slice remains to be defined.

## Current scope status

The first milestone is intended to validate the minimum daily-use utility loop: Home, Drawer, application launching, and the minimum necessary Settings. Home shows time, date, favorite applications, and a Drawer entry. Drawer initially uses a single-list presentation and includes every launchable entry exposed by Android, including cloned application entries when the platform exposes them. Its core tasks must remain fully local and offline. Widgets, folders, themes, extensive customization, network-backed information, accounts, cloud synchronization, and server development are not first-milestone requirements.

The detailed boundary and evidence are recorded in the [product foundation requirements](docs/requirements/product-foundation.md). The following remain unresolved:

- The first observable vertical slice and its acceptance criteria
- Required Android roles, permissions, background capabilities, or package visibility
- Analytics, crash reporting, payment, or other third-party services beyond the first milestone
- Commercial model, which is intentionally deferred

In particular, this overview does not place `QUERY_ALL_PACKAGES` or any other sensitive capability in the product scope. The product requires discovery of launchable entries, not unrestricted access to installed-package data. The implementation must use the smallest Android visibility scope that satisfies the current user journey.

## Additive requirements

An additive requirement is a capability that may be delivered when useful without defining or blocking the transition between V1, V2, and later capability layers. Landscape support, foldable and tablet adaptation, themes and colors, weather information, widgets, and folders are current examples.

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

Architecture, requirements, development, validation, security, privacy, and release documentation will be established as their inputs are confirmed. Planned document locations must not be treated as completed evidence.
