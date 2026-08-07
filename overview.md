# Avenor Launcher Project Overview

> Semantic source: English. Chinese counterpart: [overview.zh-CN.md](overview.zh-CN.md).
>
> Status: Planning draft. This document records confirmed product intent and explicitly marks unresolved scope. It may be revised or replaced as product discovery progresses.

## Status

Planning. Development has not started, and no build or validation baseline has been selected.

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
- Intended availability: global, subject to regional, legal, platform, and store-policy review.
- License: Apache License 2.0.
- Advertising and recommendation feeds are out of scope.

## Version direction

Major versions describe capability layers, not committed release dates or complete feature lists:

- **V1 — Fixed presentation:** User-controlled, deterministic presentation of applications and selected device information; no behavior-based automatic rearrangement.
- **V2 — Basic adaptation:** Optional behavior-based presentation or ordering, subject to explicit privacy, user-control, and validation requirements.
- **V3 — AI assistance:** A possible future direction with no approved scope.
- **V4 — Agent integration:** A possible future direction with no approved scope.

This direction does not authorize V2–V4 work. The first releasable scope and first vertical slice remain to be defined.

## Current scope status

The following are not yet product commitments:

- The exact V1 capability set and information shown on the home screen
- The first observable vertical slice and its acceptance criteria
- Minimum and target Android versions and supported device form factors
- Required Android roles, permissions, background capabilities, or package visibility
- Exact distribution channels; Google Play and selected application stores in China are candidates
- Account, networking, cloud synchronization, analytics, crash reporting, payment, or other third-party services
- Commercial model, which is intentionally deferred

In particular, use of `QUERY_ALL_PACKAGES` or any other sensitive capability is not approved by this overview. Permissions must be justified against the selected user journey and applicable distribution policy before implementation.

## Feature decision test

Before a feature enters an approved scope, answer:

1. Does it make finding the needed application or information faster, more accurate, or easier?
2. Does the default experience remain calm and understandable?
3. Would the primary user use it regularly?
4. Can users understand, control, disable, or recover from it where appropriate?
5. Are its privacy, accessibility, security, platform, and maintenance implications acceptable?

Features that do not pass this test should be rejected, deferred, or redesigned.

## Engineering intent

Engineering should prefer mature, maintainable choices, minimal dependencies, clear boundaries, safe defaults, and evidence-based validation. These are constraints on future technical evaluation, not advance approval of a specific architecture, framework, API, or abstraction.

AI is expected to perform a substantial share of project execution under human review. Max remains responsible for product direction, technical decisions, quality review, and release approval.

## Governance and documentation

- Owner and product, technical, and final release decision authority: Max
- Internal codename: To be decided
- Pre-development checklist: [todo.md](todo.md)
- Agent and Toolkit routing: [AGENTS.md](AGENTS.md)
- License: [LICENSE](LICENSE)

Architecture, requirements, development, validation, security, privacy, and release documentation will be established as their inputs are confirmed. Planned document locations must not be treated as completed evidence.
