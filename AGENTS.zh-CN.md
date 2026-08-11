# Agent 路由

> 英文语义源：[AGENTS.md](AGENTS.md)。

## 启动流程

在提出或修改项目文档或代码前：

1. 阅读 `../max-ai-toolkit/prompts/project-bootstrap.md`。
2. 遵循 Toolkit 的路由和渐进式上下文加载规则。
3. 阅读本文，并且只读取与当前任务相关的项目文档。

Toolkit locator 为 `../max-ai-toolkit`。Toolkit 提供可复用的方法；本仓库仍然是 Avenor Launcher 产品事实、需求、架构、决策、实现、验证和发布信息的权威来源。

## 项目身份

- 产品名称：Avenor Launcher
- 仓库：`avenor-launcher`
- 项目所有权与决策权：项目作者

## 项目入口文档

- 公共项目入口：[README.md](README.md)
- 中文公共项目入口：[README.zh-CN.md](README.zh-CN.md)
- 产品概览英文语义源：[overview.md](overview.md)
- 中文产品概览：[overview.zh-CN.md](overview.zh-CN.md)
- 文档地图与治理规则：[docs/documentation.zh-CN.md](docs/documentation.zh-CN.md)
- 产品基础需求：[docs/requirements/product-foundation.zh-CN.md](docs/requirements/product-foundation.zh-CN.md)
- 版本、产物与发布治理：[docs/release.zh-CN.md](docs/release.zh-CN.md)
- 版本交付格式与活动版本：[docs/versions/version-delivery-format.zh-CN.md](docs/versions/version-delivery-format.zh-CN.md)
- 迭代记录格式：[docs/iterations/iteration-record-format.zh-CN.md](docs/iterations/iteration-record-format.zh-CN.md)
- 许可证：[LICENSE](LICENSE)

系统架构、开发、验证、安全和隐私文档尚未建立。架构决定只在已接受 ADR 所记录的准确范围内存在；不得从单份 ADR 推断更广泛的架构，也不得将其他规划中的路径视为当前证据。

## 语言

英文是公共项目文档和面向仓库输出的语义源。内部思考可以使用中文，并可维护 `.zh-CN.md` 中文对应文档。当双语文档存在实质差异时，应先更新英文语义源，并尽可能在同一变更中同步中文文档。

Commit message、Pull Request、Issue、发布说明及其他公开仓库输出使用英文。

## 项目特定规则

- 遵守 `overview.md` 中的产品原则和已确认边界。
- 将未知产品或技术事实标记为“待确认”，不得把假设变成决策。
- 产品决策与技术架构决策分开记录。
- 不在产品文档中规定实现细节。
- 不将对话的当前工作阶段持久化为项目文档状态。续接状态可保存在本产品仓库之外；持久的产品契约和项目约束仍保存在本仓库。
- 仓库中可见的权威文档描述当前项目与产品契约；不得因文档存在而推断另一套文档生命周期。
- 接入实现前，对比当前文档、代码、测试和验证证据。任何实质差异都作为契约不一致显式解决。
- 生产实现必须具备适用的当前产品契约，并获得项目作者的明确授权。
- 创建 Android 工程时，以本仓库根目录作为工程根目录。未经作者明确决定，不得将 Android 工程放入另一个嵌套仓库或外层包装工程。
- Android UI 代码不得硬编码面向用户的字符串、语义颜色或可复用尺寸；应在适用的 `res/values` XML 资源中定义，并通过项目资源或主题层访问，所有面向用户的字符串均须支持本地化。当 drawable 或 vector XML 资产中的默认颜色或尺寸属于资产自身，且调用方 UI 明确控制适用的语义 tint 或最终渲染尺寸时，可以保留该资产固有默认值；不得利用此资产例外绕过可复用设计 token。
- 日常代码编写或审查不自动运行 Gradle。通常由项目作者执行构建、安装和真机使用检查，并可将观察结果报告为迭代证据。只有项目作者明确要求，或已授权的正式版本或聚焦验证任务需要时，Agent 才运行 Gradle。Agent 未运行 Gradle 本身不构成迭代准入或准出阻塞。
- 项目当前采用单线开发。除非项目作者改变此约束，不引入多分支或多人协作流程。
- 默认一次只执行一个任务，报告结果并等待项目作者确认后，再开始下一个任务。修改、提交、推送等相互独立的写操作不得自动串行接续，除非项目作者明确授权本次可以串行执行。
- 当产品契约或边界发生变化时，更新对应的权威文档。
- 只有在覆盖规则明确、范围清晰，并记录在本文或适用决策记录中时，本仓库才覆盖 Toolkit 指导。
- 项目作者是项目内所有事项的第一责任人。安全、隐私、法务、财税或平台政策结论需要专业能力时，应取得具备相应能力的人员复核。

## 验证基线

Android 实现和初步技术栈已经存在，但开发与验证基线尚未建立，因此当前仍没有权威的构建、测试、lint、静态分析、模拟器或设备命令。对于纯文档变更，应检查 Git diff 并验证本地 Markdown 链接。作者报告的构建、安装和设备观察应按作者报告记录，不得推断缺失的命令、环境或结果。除非 Agent 实际执行了相应命令，否则不得报告 Agent 已运行的产品构建或测试结果。工具或依赖存在更新版本的提示属于建议维护信息，不是迭代失败结果，也不要求立即升级。
