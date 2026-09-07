# Iteration 28: Upgrade, Regression, and Version Closure

> Applicable version: [Avenor Launcher 1.5.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation, version allocation, Git, artifact, tag, publication, or release action.

## Objective

Produce truthful evidence that the selected `1.5.0` Drawer journey upgrades safely from the accepted prior version, preserves accepted Home state, and is ready for the author's separate version-completion decision.

## Product and version references

- Product-contract baseline: `48d5bd470c84d222b6e89e128f438da1f25e595b`
- [1.5.0 delivery](delivery.md)
- [1.4.0 planned delivery](../1.4.0/delivery.md)
- [Validation guide](../../validation.md)
- [Release governance](../../release.md)
- [Product foundation](../../requirements/product-foundation.md)

## Observable outcome

One traceable installable candidate upgrades the accepted prior-version baseline on the author-designated primary device, preserves readable Home modules and unrelated settings, completes the Iterations 26–27 Drawer journey, and records every result and gap without implying tag or publication approval.

## Included work

- Reconcile product contracts, implementation, tests, identifiers, and evidence for Iterations 26–27.
- Validate in-place upgrade, readable-state preservation, missing-field Drawer defaults, clean-install defaults, and unreadable-data protection.
- Validate Drawer search, Settings row, source-specific action sheet, display settings, blur fallback, persistence, inventory changes, and navigation.
- Apply and validate the revised shared style settings panel layout (stacked title and content lines, application-size block last, compact selector geometry with author-accepted `40dp` targets, and baseline-device content-line non-overflow) across Drawer display settings and the Home module-style panel.
- Regress accepted Home modules, favorites, launch, Settings, double-tap lock, Privacy, offline, and profile behavior.
- Confirm backup exclusion for Drawer display-setting and favorite-module data.
- Produce or identify an installable candidate with accepted identifiers, allocated `versionCode`, source commit, signing category, digest when available, and upgrade limitation.
- Record actual and skipped checks and update `delivery.md` only when supported and authorized.

## Excluded work

- Adding product functionality or changing acceptance intent inside closure.
- Formal release artifact, tag, GitHub Release, upload, publication, store action, or public distribution.

## Technical change areas

Upgrade fixtures and journey, regression tests, backup inspection, build/artifact traceability, physical-device acceptance, evidence recording, and focused defect correction only when within selected scope.

## Dependencies and sequence

Depends on accepted Iterations 26–27 and an accepted prior-version baseline. A discovered product or implementation gap returns to its owning scope or a new author decision; closure does not silently absorb it.

## Migration and compatibility impact

This iteration validates rather than redefines adoption. Upgrade must preserve readable Home modules, favorites, unrelated configuration, and system authorization; absent new Drawer fields adopt current defaults. Downgrade is unsupported.

## Security, privacy, permission, and licensing impact

No new impact is selected. Validate least privilege, local-only search and settings, Privacy accuracy, accessibility isolation, and backup exclusion. Any expanded boundary requires separate review.

## Risks and unresolved decisions

The accepted prior-version artifact, candidate source, allocated `versionCode`, artifact, exact device evidence, and tag disposition may not exist at planning time. A build pass alone does not establish upgrade or daily-use acceptance. Platform blur variation must not be mistaken for loss of the selected setting.

## Acceptance criteria

- Iterations 26–27 have accepted results and no unresolved material contract mismatch.
- A traceable candidate retains `com.avenor.launcher`, uses accepted `1.5.0` identifiers, and records source and signing category.
- The primary physical device upgrades from the accepted prior version and preserves readable Home modules, favorites, unrelated configuration, and system authorization.
- The author completes the selected Drawer journey and accepted Home, Settings, launch, double-tap-lock, Privacy, profile, and offline paths without included-path crash, ANR, destructive state error, wrong identity launch, or navigation dead end.
- Drawer display-setting and favorite-module data remain excluded from Android backup and transfer.
- Every failure, skipped check, unknown, and limitation is recorded accurately.
- The author separately decides version completion and tag disposition.

## Validation requirements

The physical-device in-place upgrade, preservation/default-adoption result, complete selected Drawer journey, traceable candidate identity, and author acceptance are mandatory version evidence. Clean installation is recommended supplemental evidence. Focused automated checks and broader API, OEM, profile, clone, locale, accessibility, blur, inventory-change, failure-injection, and performance coverage remain recommended unless promoted. Actual results belong in `delivery.md`.

## Related decisions and technical assessments

Use applicable persistence, identity, privacy, validation, and release decisions. Create or amend a durable decision only if closure evidence establishes a consequential boundary.

### Amendment: style settings panel layout on 2026-09-06

The author directed on 2026-09-06 that the revised shared style settings panel layout be delivered and validated within this iteration instead of a new iteration. This is an authorized scope addition to this incomplete iteration: it expands the included work above to apply and validate the presentation change, and it updates the corresponding acceptance and validation boundaries. No new iteration is created. The original `48d5bd470c84d222b6e89e128f438da1f25e595b` baseline remains this iteration's baseline; the presentation change's accepted product-contract revision is recorded with its implementation branch. The change reopens the Iteration 27-accepted panel presentation, and its acceptance is part of this iteration's author acceptance.

### Amendment: 1.5.0 scope boundary on 2026-09-07

The author confirmed on 2026-09-07 that the local manual backup and restore contract and the hidden-name and vertical-list-geometry presentation contract are excluded from the 1.5.0 delivery and deferred to the next version. These contracts are not part of this iteration and receive no implementation here. The backup and restore contract is defined at `a1b7833609c5c83d423dc8c3c582ec1fb3edc70a` and the hidden-name and geometry presentation change at `33662ca360400c2173aa96a6a52e59573ab6a104`; both remain current product contracts on `main` and become the next version's product-contract baseline. This iteration's included work, acceptance, and validation cover only the accepted baseline `48d5bd470c84d222b6e89e128f438da1f25e595b` plus the 2026-09-06 style-settings-panel presentation revision. Closure evidence explicitly records the two deferred contracts as out of scope.
