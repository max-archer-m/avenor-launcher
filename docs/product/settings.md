# Settings Interaction Specification

> Public semantic source: English. Chinese counterpart: [settings.zh-CN.md](settings.zh-CN.md).

## Entry and return

- Settings opens only from the fixed gear destination below the Drawer alphabet index.
- Back returns to Drawer and preserves its prior list position during the same process.
- Settings uses an opaque standard Material 3 dark color scheme. Unlike Home and Drawer, it paints its Material surface background rather than exposing the system background beneath the application.

## Initial scope

### Launcher settings

- Open the system default-home application settings.
- Select application language from the supported product languages. Exact options and change timing remain to be specified.

### About

- Product name and basic product information.
- Application version name and version code or build identifier.
- Privacy statement.
- Avenor License information, using the English label `License`.
- Third-party license information after actual dependencies exist and can be inventoried.
- Project repository link when its presentation and offline behavior are defined.

### Support and diagnostics

Complex logs, update checks, backup, cloud synchronization, and diagnostic export are outside the current initial scope. Copyable version or device information remains a candidate rather than current behavior.

## Privacy presentation

- Selecting Privacy opens a dark modal bottom sheet containing a local, readable privacy statement.
- The statement must remain available offline.
- The actual privacy text remains to be authored and professionally reviewed when the product's data and distribution conditions require it.

## State refresh

- Returning from a system settings destination refreshes affected Launcher state.
- Settings does not restore a previously open modal sheet after leaving for a system surface.
