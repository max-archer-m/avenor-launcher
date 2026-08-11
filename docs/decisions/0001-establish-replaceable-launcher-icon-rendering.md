# ADR-0001: Establish a Replaceable Launcher Icon Rendering Boundary

## Status

Accepted

## Date

2026-08-11

## Context

Avenor must display each platform-exposed application's icon and applicable profile or clone badge in Home and Drawer. Android adaptive icons use a device-provided mask, so the same icon may appear as a squircle on a Samsung device and as a circle on a Pixel device. Legacy icons do not provide adaptive foreground and background layers and can appear as unmasked squares, become unrecognizable when cropped directly, or show an unrelated wrapper edge when placed on a fixed background.

The Iteration 2 device spike compared direct platform-drawable rendering, a fixed white adaptive wrapper, direct device-mask clipping, and a project-owned normalization path. Samsung device validation accepted the path that preserves native adaptive icons, analyzes legacy icons at a higher resolution, derives a wrapper background from their edge pixels, scales their complete artwork into a safe region, applies the device mask, and adds the platform badge afterward.

Theme customization remains outside the `1.0.0` product contract, but a future theme may select a circle, square, rounded square, device shape, or another approved mask. The current implementation must not make that future capability require changes to launchable identity, inventory ownership, favorite persistence, or every icon-consuming UI.

## Decision

Avenor owns a replaceable launcher-icon rendering boundary with the following responsibilities:

- `LauncherApps` remains the platform source for the launchable entry, raw icon, user/profile identity, and related metadata.
- A project-owned renderer converts the raw icon plus an explicit appearance policy into the final process-local presentation consumed by Home, Drawer, and related application UI.
- `SystemAdaptive` is the only implemented and selected appearance policy for `1.0.0`. It uses Android's current device mask and does not branch on device manufacturer or model.
- Native `AdaptiveIconDrawable` layers retain their platform adaptive behavior.
- Legacy icons are normalized without assuming that an opaque square can safely fill the mask. The renderer analyzes a higher-resolution source, derives an appropriate edge background when available, preserves the complete artwork inside a safe region, and applies the current device mask with antialiased output.
- Profile or clone badging is applied after shape normalization so the badge is not cropped into the icon mask.
- A platform-default generic application icon goes through the same rendering boundary when the application icon cannot be loaded or rendered.
- Rendered icons are derived inventory presentation. They are not favorite identity and must not be persisted as favorite truth or as a historical icon archive.
- Future theme work may add an appearance policy or renderer implementation, but it must preserve the inventory and identity boundaries unless a later decision explicitly supersedes this ADR.

## Rationale

This boundary keeps platform discovery, icon presentation, application identity, and UI rendering separate. It satisfies current OEM-adaptive behavior while leaving a narrow substitution point for future themes. It also makes badge ordering, fallback behavior, cache invalidation, and cross-surface consistency reviewable in one place.

Using Android's device mask avoids hard-coded Samsung or Pixel shapes and follows the platform's cross-device adaptive-icon model. Keeping theme selection out of the inventory and favorite models prevents a future presentation change from becoming a data migration.

## Considered Options

### Display `LauncherActivityInfo.getBadgedIcon()` directly

- Benefits: Smallest implementation and preserves the platform-returned badge.
- Trade-offs: Legacy icons remain visually inconsistent because the API does not perform a launcher's complete normalization responsibility.

### Wrap every legacy icon on a fixed white adaptive background

- Benefits: Produces a device-shaped outer silhouette with little code.
- Trade-offs: Creates visible white borders or a small square inside the device shape when the source background is not white.

### Clip every opaque legacy icon directly to the device mask

- Benefits: Removes the outer square and avoids an added wrapper color.
- Trade-offs: Opaque corners do not prove that important artwork is inside the adaptive safe region; device-mask clipping can enlarge or remove identifying content.

### Use a project-owned replaceable renderer

- Benefits: Centralizes normalization and badge ordering, supports device masks today, and permits future appearance policies without changing identity or persistence.
- Trade-offs: Requires maintained image-analysis and rendering code, OEM device validation, and explicit cache/configuration handling as the implementation grows.

## Consequences

- Home and Drawer must consume the same rendered icon result rather than implementing their own masking rules.
- Icon rendering remains an Android adapter responsibility and may use process-local caching when measurement demonstrates a need. Any cache key must account for launchable identity, user/profile, density, icon size, applicable configuration, appearance policy, and theme generation.
- Package, icon, profile, density, or relevant configuration changes must invalidate affected derived results.
- The implementation must continue to validate adaptive, legacy, fallback, primary, cloned, and work-profile cases on the required devices.
- Exact visual parity with proprietary OEM launcher effects such as private shadows, icon packs, or theme services is not guaranteed by this decision. No hidden API or manufacturer-specific branch is authorized.
- Adding a future theme policy remains a separate product decision and delivery scope; this ADR provides an implementation seam but does not authorize that capability.

## Validation Evidence and Gaps

- Samsung physical-device validation accepted the current device-mask shape, normalized legacy-icon recognizability, and profile/clone badge presentation after the Iteration 2 icon spike.
- Debug Kotlin and Android-test Kotlin compilation succeeded for the implemented boundary and regression coverage.
- Pixel physical-device behavior remains required compatibility evidence before the applicable `1.0.0` exit gate. That evidence may tune the renderer without superseding this boundary; a conflicting architectural requirement requires a new ADR.

## Implementation Notes

- Keep implementation constants and image-analysis heuristics in code and tests rather than freezing them in this ADR.
- Preserve raw platform metadata only for the lifetime needed to derive the current immutable inventory presentation.
- If a future theme changes icon shape at runtime, publish a new derived inventory presentation and invalidate process-local rendered-icon entries; do not rewrite favorites.

## Supersedes

None
