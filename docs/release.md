# Version, Artifact, and Release Governance

> Semantic source: English. Chinese counterpart: [release.zh-CN.md](release.zh-CN.md).
>
> This document is the authoritative operational contract for application versions, version archives, APK artifacts, signing continuity, Git tags, and GitHub Releases. It does not authorize a particular version, tag, GitHub Release, public distribution, or implementation scope.

## Project version model

Avenor Launcher uses a project-defined versioning profile with the numeric form `MAJOR.MINOR.PATCH`. It is based on the shape of Semantic Versioning but is governed by this document rather than by the official Semantic Versioning specification.

- The first formal application version is `1.0.0`. The project does not use `0.x.y` versions.
- The project has no prerelease-version concept. Identifiers such as `alpha`, `beta`, and `rc`, and SemVer prerelease or build-metadata suffixes, are not part of the version format.
- Repository commits, pushed source states, local builds, and other work before `1.0.0` are development evidence, not formal application versions.
- A formal version identifies an installable and verified Android APK. It is not created by documentation work alone.
- Every formal version contains one or more completed iterations. The applicable version delivery contract selects the subset of the current product contract delivered by that version.
- `1.0.0` is the first minimum usable version. It is not required to implement every behavior described by the current product contract; its exact delivery scope must be established through the version and iteration contracts.

## Numeric progression

Every changed numeric component increments by exactly one. Skipping numbers is prohibited unless the project author explicitly replaces this contract.

- `PATCH` increments by one for a compatible implementation correction within the current product and version contract. When `PATCH` increments, `MAJOR` and `MINOR` remain unchanged.
- `MINOR` increments by one for a user-visible capability, an intentional behavior change, or a materially improved usable experience within the current capability layer. When `MINOR` increments, `PATCH` resets to zero.
- `MAJOR` increments by one only when the project enters the next authorized capability layer. When `MAJOR` increments, `MINOR` and `PATCH` reset to zero.
- An increment to an earlier component always resets every later component to zero.

The `MAJOR` number is reserved for the product capability layers:

| Version family | Capability layer |
| --- | --- |
| `1.x.x` | V1: fixed presentation |
| `2.x.x` | V2: basic adaptation |
| `3.x.x` | V3: AI capability |
| `4.x.x` | V4: agent capability |

Until the contract for entering V2 is explicitly authorized, every formal version remains in `1.x.x`. A major project achievement or an incompatible change does not independently permit a `MAJOR` increment. Within one capability layer, an incompatible change uses the next `MINOR` version and must document its impact and migration requirements.

Pure documentation changes never change the application version. A source, resource, manifest, dependency, or build-configuration change that affects the APK may justify a new formal version, but an ordinary code change does not become a formal version until the author declares the version boundary and its required evidence is complete.

## Android version identifiers

- Android `versionName` is the formal `MAJOR.MINOR.PATCH` value defined above.
- Android `versionCode` starts at `1` for `1.0.0`.
- Every formal `versionName` change increments `versionCode` by exactly one.
- `versionCode` is never calculated from `versionName`; it may not be skipped, reused, or decreased.
- Two different formal `versionName` values may not share a `versionCode`.
- Downgrade is not supported.

Within one `MAJOR` family, any older formal version must have a supported direct upgrade path to any newer formal version. For a cross-major upgrade, the applicable major-version delivery contract must define supported source versions, migrations, validation, and limitations before release.

## Iterations and version archives

Each formal version is planned under `docs/versions/<version>/` and must be represented by `docs/archives/v<version>/delivery-contract.md` after all included iterations are completed. The archive contains the completed version contract, its supporting inputs, and the original records for one or more included iterations, and must record:

- `versionName` and `versionCode`
- The exact Git commit represented by the APK
- The included iterations and the reason for declaring the version boundary
- Important product and implementation changes
- Migration or compatibility impact
- Completed validation evidence and affected devices or environments
- Known limitations, unresolved defects, and other legacy issues
- APK filename and external storage location
- APK SHA-256 digest
- Release-signing certificate SHA-256 fingerprint
- Related Git tag and GitHub Release when either exists

The archive must never contain a private signing key, keystore password, key password, or another signing secret.

## APK artifact contract

The verified APK for each formal version is stored outside this product repository under `../max-dev-context`. Its exact directory convention remains to be decided before the first formal version is archived.

- APK files must not be committed to the `avenor-launcher` Git repository.
- Whether APK files are tracked by the `max-dev-context` repository is not decided by this document. Until explicitly decided, treat the location as external filesystem storage rather than authorization to commit binary artifacts.
- Product-repository records use a stable relative or logical artifact location rather than a machine-specific absolute path.
- The recorded SHA-256 digest must be computed from the exact archived APK and verified after copying it to the external location.
- The artifact record must also identify the build time and the environment used to produce and validate the APK when that evidence becomes available.

An APK is a formal version artifact only after it is installable, has passed the applicable version validation, matches the archived commit and identifiers, and has the recorded digests and signing fingerprint.

## Signing continuity and custody

Formal versions beginning with `1.0.0` must use one stable release-signing identity so supported updates can be recognized as updates of the same Android application.

- The project author creates or explicitly authorizes creation of the release keystore and retains ownership and final control of it.
- The release keystore, private key, passwords, and signing-property files must remain outside Git and outside authoritative project documentation.
- Agents may recommend or assist with generation, build integration, fingerprint verification, and signing verification only with explicit author authorization. The author selects and retains the secrets and backup locations.
- Each formal version archive records only the release certificate's SHA-256 fingerprint, never private signing material.
- Before `1.0.0`, the project must establish secure storage and at least two independent, encrypted, author-controlled backups of the release keystore and required recovery information.
- Loss, compromise, rotation, or platform-managed migration of the signing identity requires explicit author approval, an impact assessment, and a documented migration decision before another formal version is declared.

A future decision to distribute through Google Play or another store must separately define platform-managed signing, upload keys, channel requirements, and migration consequences. No distribution platform is currently authorized.

## Git tags and GitHub Releases

Application versions, Git tags, and GitHub Releases are separate records:

- Every formal version is archived, but not every formal version receives a Git tag.
- A tag is appropriate when accumulated related functionality or experience improvements establish an important, stable implementation baseline. For example, `1.0.0` may remain untagged while a later `1.3.0` becomes a tagged baseline.
- Tag creation and the exact target commit require explicit project-author approval. Agents may recommend whether a version is worth tagging.
- Tag naming remains to be decided before the first tag is created.
- Creating a tag does not require creating a GitHub Release.
- A GitHub Release must reference an existing approved tag and requires separate explicit author approval.
- No GitHub Release is required while the project has no public distribution line. A future release may attach the verified APK only when the author explicitly approves that distribution action and its security and validation gates are satisfied.
- Neither this document nor a version archive authorizes a tag, remote push, GitHub Release, or APK upload.

For this project, a milestone exists only when the project author explicitly declares an important baseline and its approved Git tag exists. A GitHub Release is optional and requires separate approval when the author chooses outward-facing publication. A version or an approved tag not declared as a milestone is not a milestone automatically.

## Migration-cost discipline

Product definition, technical research, architecture, and implementation must consider likely future capability-layer migration cost. Where evidence supports it, preserve replaceable boundaries, data migration paths, and appropriate extension or API seams so later capability layers do not require avoidable rewrites. This requirement does not authorize speculative frameworks, unused abstractions, or implementation details in product documentation; the chosen preparation must remain proportional to current evidence and must not silently expand current scope.

## Remaining implementation decisions

The following operational details must be decided from the actual Android project and release environment before they become executable instructions:

- The exact APK directory beneath `../max-dev-context` and its retention, synchronization, backup, and Git-ignore policy
- Release-keystore format, parameters, secure locations, backup procedure, and authorized signing workflow
- Authoritative build, signing, digest, install, upgrade, and validation commands
- The exact tag naming convention
- Any future distribution channel and its publication gates
