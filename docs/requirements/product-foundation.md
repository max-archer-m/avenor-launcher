# Requirements Brief: Product Foundation and First Milestone

> Public semantic source: English. Chinese authoring counterpart: [product-foundation.zh-CN.md](product-foundation.zh-CN.md). Keep both versions materially aligned.
>
> Status: Approved product-foundation baseline. The problem, primary context, V1 core boundary, minimum Settings scope, daily-trial method, and quality guardrails are confirmed. Platform details, interactions, and some user-control requirements remain for later definition.

## Problem

The author uses an Android phone as their primary device and commonly uses Samsung and Google phones. The Launcher is used whenever the author enters the home screen or needs to find an application or obtain information from it. There is no reliable usage-duration or frequency measurement yet.

The author prefers a custom Home page combined with an application Drawer, but current alternatives do not satisfy all of the following needs:

- Stock launchers and competing products commonly use lists or free-form grids for applications and widgets.
- Highly flexible products allow extensive customization but provide too few constraints to sustain an attractive, comfortable, and minimal result.
- Minimal products may reduce visual noise without matching the author's preferred Home-and-Drawer combination or personal layout needs.

The author therefore wants an Android Launcher with deliberate design constraints that first meets their own long-term daily needs.

## Evidence boundary

### Known facts and author experience

- The author has long-term Android experience and uses Android as their primary phone platform.
- The author has used Samsung and Google stock launchers, Niagara Launcher, and Microsoft Launcher as alternatives.
- Current problem evidence consists of the author's sustained usage experience and understanding of their own needs.
- No usage counts, task-duration data, interviews, external user feedback, or market research are currently available.

### Hypotheses to validate

- Limited and intentional layout rules may make an attractive, comfortable, and minimal daily home screen easier to sustain than unrestricted customization.
- Whether Android users other than the author share the same need is unverified.
- Whether the product should later be published and maintained through GitHub or application stores is unverified.

## Project motivation

- Product motivation: create a personally owned home-screen tool suitable for the author's long-term daily use.
- Learning motivation: use a real project to learn agent systems and build experience toward agent-engineering or prompt-engineering work.

The learning motivation may shape the project process and retrospectives, but it must not replace user value, product acceptance, or quality standards.

## Target user and scenarios

### Primary user

The author. Initial product trade-offs are evaluated first against whether the author can use the product reliably every day.

### Secondary users

There are no identified secondary users. Possible future publication on GitHub or in application stores does not demonstrate broader demand or expand the first milestone's target audience.

### Primary scenarios

- Enter the Android default home screen.
- Move from Home to the Drawer, find an installed application, and launch it.
- Open Settings and configure what is necessary to preserve the Launcher's basic utility.
- Continue completing the preceding core tasks without a network connection.

Usage frequency, task duration, and the exact types of information obtained from the home screen have not been measured. The minimum Settings items are confirmed in the first-milestone scope.

## Product goal

Provide the author with an Android Launcher suitable for long-term daily use that is fully local and follows least-privilege principles, using Home, Drawer, and Settings to complete the most basic home-screen utility tasks.

The first milestone validates utility, not visual refinement, extensive customization, network information, or mass-market suitability.

## First milestone scope

### In scope

- Home: a basic entry point capable of acting as the Android default home screen.
- Drawer: discovery and launching of applications installed on the device.
- Settings: language selection and an entry to Android system settings for the default home application.
- Complete offline availability for core tasks.
- Local storage of core product data without a self-hosted server, account, or cloud synchronization.
- Only system permissions that are necessary for, and traceable to, core functionality.

### Out of scope

- Visual refinement whose primary goal is aesthetic quality.
- Extensive or unrestricted layout and theme customization.
- One-action clearing or restoration of all local configuration.
- Network-backed information such as weather.
- Accounts, cloud synchronization, or a self-hosted server.
- Behavioral analytics, automatic ordering, recommendations, AI assistance, or agent integration.
- Business-model validation, formal store release, or mass-market adaptation.

“Out of scope” means excluded from the first milestone, not permanently rejected. Advertising, recommendation feeds, and engagement-maximizing design remain constrained by the long-term boundaries in the project overview.

## Functional requirements

- The user can launch Avenor Launcher as the Android home-screen entry point.
- The user can access the Drawer from Home.
- Whether or not Avenor Launcher is selected as the default home application, the Drawer presents every installed application that should be visible and launchable under Android platform rules and allows the selected application to be launched.
- The user can select follow-system, English, or Simplified Chinese language behavior in Settings.
- The user can open Android system settings for the default home application from Settings.
- The first milestone does not provide one-action clearing or restoration of all local configuration; users edit configuration through individual settings.
- The core Home, Drawer, and Settings paths do not depend on a network, account, cloud synchronization, or a self-hosted server.
- If a network-backed capability such as weather is added later, its unavailable, unauthorized, or offline state does not block core paths.

## Non-functional requirements

- **Local first:** First-milestone product state and core data remain on the device.
- **Offline capable:** Home, Drawer, application launching, and core Settings remain available without a network connection.
- **Least privilege:** Every permission maps to an approved core capability and records its purpose, trigger, denial behavior, and distribution-policy impact.
- **Compliance readiness:** Permission and application-visibility design anticipates possible application-store review without presenting unverified store policy as product fact.
- **Reliability:** The first milestone must support daily trials on the author's actual primary devices; measurable stability thresholds remain unresolved.
- **Compatibility:** Samsung and Google devices are the author's current real-world environments; minimum and target Android versions and the device matrix remain unresolved.

## Acceptance criteria

- Given Avenor Launcher is selected as the home application on a supported Android device, when the user performs the system action to return home, then the system displays Avenor Launcher Home.
- Given the user is on Home, when the user performs the defined Drawer-entry action, then the Drawer displays installed applications that can be launched.
- Given Avenor Launcher is not selected as the default home application, when the user launches Avenor Launcher directly and enters the Drawer, then the Drawer still displays every installed application that should be visible and launchable under Android platform rules.
- Given a launchable application appears in the Drawer, when the user selects it, then the system launches that application.
- Given the user opens Settings, when the user selects follow-system, English, or Simplified Chinese, then the Launcher presents its interface according to that language behavior.
- Given the user opens Settings, when the user selects the default-home settings entry, then the system opens the corresponding Android settings page.
- Given the device has no network connection, when the user accesses Home, Drawer, application launching, or core Settings, then the core task remains completable.
- Given a non-core network capability is unavailable or unauthorized, when the user performs a core task, then that capability does not block Home, Drawer, application launching, or core Settings.
- Given a system permission is proposed for the first milestone, when requirements and distribution review occur, then the permission is traceable to a necessary core capability and its denial behavior.

## Success assessment and guardrails

### Confirmed direction

- Core first-milestone success means that Home, Drawer, and Settings form a minimum utility loop suitable for the author's daily trial.
- Development completion alone is not sufficient evidence of success; results must be observed on the author's real primary devices.
- Success is judged by the author's own daily experience; uncollected external-user opinions are not acceptance evidence.
- The author conducts a seven-consecutive-day daily trial and keeps Avenor Launcher selected as the default Launcher throughout the trial.
- The author may intentionally open another Launcher for comparison, but missing Avenor Launcher core functionality must not force a switch.
- Core paths must not crash or become unresponsive because of Avenor Launcher.
- The Drawer must not omit, duplicate, or incorrectly display applications that should be visible and launchable under Android platform rules.
- Selecting an application must launch the intended application.
- Local configuration must not be unexpectedly lost or corrupted.
- Permission denial may degrade only the dependent capability and must not block other core tasks.
- Home, Drawer, application launching, and core Settings must remain available while the device is offline.

Minimum acceptable performance, power, memory, and startup-response levels will be defined in the quality baseline and are not completion conditions for this product-foundation baseline.

## User control

The first milestone lets users edit configuration through individual settings but does not provide one-action clearing or restoration of all configuration. Define any other reversal, export, deletion, or restoration capabilities alongside the local data inventory rather than inventing them at this stage.

## Dependencies and risks

- The Android home role, application-enumeration approach, and relevant permission or application-visibility policies still require technical and distribution review.
- Settings includes language selection and an entry to system default-home settings; define its detailed interface and state behavior in the vertical slice.
- Current evidence represents only the author and cannot support a mass-market demand conclusion.
- “Attractive, comfortable, and minimal” has not been converted into observable standards and is not a first-milestone acceptance target.
- Using the project to learn agent systems may encourage process or technical complexity beyond product needs; control this through explicit scope changes.

## Open questions

- What are the minimum and target Android versions and the Samsung and Google device-validation scope?
- How should concrete user-control requirements be defined after Settings and the local data inventory are known?

## Recommended next step

Next, decompose the first milestone into a minimum observable vertical slice and add user journeys and interaction-level acceptance boundaries for Home, Drawer, application launching, and Settings. Evaluate architecture and permissions separately after that scope is approved.
