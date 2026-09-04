# Iteration 25: Upgrade, Regression, and Version Closure

> Applicable version: [Avenor Launcher 1.4.0 Delivery](delivery.md). Status and actual evidence belong only in `delivery.md`; this contract authorizes no implementation, version allocation, Git, artifact, tag, publication, or release action.

## Objective

Produce truthful evidence that the selected `1.4.0` Home-module journey adopts safely from `1.3.0`, remains compatible with established Launcher behavior, and is ready for the author's separate version-completion decision.

## Product and version references

- Product-contract baseline: `78d2aab18066c2d9b57b56581e0ab8c17402d104`
- [Accepted Home behavior](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/surfaces/home.md)
- [Accepted Home presentation and transitions](https://github.com/max-archer-m/avenor-launcher/blob/78d2aab18066c2d9b57b56581e0ab8c17402d104/docs/product/presentation/home.md)
- [Iteration 24 amended scope](iteration-24-application-editing-and-cross-module-movement.md)
- [1.4.0 delivery](delivery.md)
- [1.3.0 completed delivery](../1.3.0/delivery.md)
- [Validation guide](../../validation.md)
- [Release governance](../../release.md)
- [Product foundation](../../requirements/product-foundation.md)

### Authorized amendment: 2026-09-03

- Author decision and reason: The author accepted the closure-preparation proposal and authorized progressive execution after Iterations 22-24 were completed and integrated. Closure must validate the accepted Iteration 24 refinements rather than its former source-placeholder and transition rules.
- Previous baseline: `7cae837dafb188896dd24bd43aae58022c81fe11`.
- New baseline: `78d2aab18066c2d9b57b56581e0ab8c17402d104`; the affected Home contracts are pinned above.
- Scope and acceptance impact: Apply the already accepted placeholder-free movement, current-gap ribbon feedback, add-entry surfaces, content transitions, and single-preview save handoff when regressing the complete Home journey. The upgrade/reset boundary, excluded Drawer features, delivery level, and mandatory-versus-recommended evidence remain unchanged. No new functionality, version allocation, signing, device-data reset, or release operation is authorized by this document.

## Observable outcome

One traceable installable candidate upgrades the accepted `1.3.0` baseline on the author-designated primary device, performs the contracted favorite reset while preserving unrelated settings, completes the Iterations 22–24 Home journey, and records every result and gap without implying tag or publication approval.

## Included work

- Reconcile product contracts, implementation, tests, identifiers, and evidence for Iterations 22–24.
- Validate in-place upgrade, favorite reset, preservation of unrelated configuration, clean-install defaults, and unreadable-data protection.
- Validate complete Home module creation, style, ordering, removal, Undo, movement, auto-scroll, inventory, interruption, restoration, and failure recovery.
- Regress established Drawer, Settings, launch, double-tap-lock, Privacy, offline, and profile behavior without adding deferred Drawer features.
- Confirm backup exclusion for favorite-module data.
- Produce or identify an installable candidate with accepted identifiers, allocated `versionCode`, source commit, signing category, digest when available, and upgrade limitation.
- Record actual and skipped checks and update `delivery.md` only when supported and authorized.

## Excluded work

- Drawer search, revised ordinary Drawer navigation, Drawer display settings, or their version evidence.
- Adding product functionality or changing acceptance intent inside closure.
- Formal release artifact, tag, GitHub Release, upload, publication, store action, or public distribution.

## Technical change areas

Upgrade fixtures and journey, regression tests, backup inspection, build/artifact traceability, physical-device acceptance, evidence recording, and focused defect correction only when within selected scope.

## Dependencies and sequence

Depends on accepted Iterations 22–24. A discovered product or implementation gap returns to its owning scope or a new author decision; closure does not silently absorb it.

## Migration and compatibility impact

This iteration validates rather than redefines adoption. Upgrade must intentionally reset former favorites, preserve unrelated readable configuration and system authorization, and retain unreadable-data protection. Downgrade is unsupported.

## Security, privacy, permission, and licensing impact

No new impact is selected. Validate least privilege, Privacy accuracy, accessibility isolation, and backup exclusion. Any expanded boundary requires separate review.

## Risks and unresolved decisions

The candidate source, allocated `versionCode`, artifact, exact device evidence, and tag disposition do not exist at planning time. A build pass alone does not establish adoption or daily-use acceptance. Legacy Home instrumentation gaps may require test-environment correction without implying product failure.

## Acceptance criteria

- Iterations 22–24 have accepted results and no unresolved material contract mismatch.
- A traceable candidate retains `com.avenor.launcher`, uses accepted `1.4.0` identifiers, and records source and signing category.
- The primary physical device upgrades from accepted `1.3.0`, resets former favorites exactly as selected, and preserves unrelated readable configuration.
- The author completes the selected Home-module journey and established Drawer, Settings, launch, double-tap-lock, Privacy, profile, and offline paths without included-path crash, ANR, destructive state error, wrong identity launch, or navigation dead end.
- Favorite-module data remains excluded from Android backup and transfer.
- Every failure, skipped check, unknown, and limitation is recorded accurately.
- The author separately decides version completion and tag disposition.

## Validation requirements

The physical-device in-place upgrade, contracted reset/preservation result, complete selected Home journey, traceable candidate identity, and author acceptance are mandatory version evidence. Clean installation is recommended supplemental evidence. Focused automated checks and broader API, OEM, profile, clone, locale, accessibility, interruption, failure-injection, and performance coverage remain recommended unless promoted. Actual results belong in `delivery.md`.

## Related decisions and technical assessments

Use applicable persistence, identity, privacy, validation, and release decisions. Create or amend a durable decision only if closure evidence establishes a consequential boundary.
