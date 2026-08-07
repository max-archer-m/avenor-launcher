# Agent 路由

> 英文语义源：[AGENTS.md](AGENTS.md)。

## 启动流程

在提出或修改项目文档或代码前：

1. 阅读 `../max-ai-toolkit/prompts/project-bootstrap.md`。
2. 遵循 Toolkit 的路由和渐进式上下文加载规则。
3. 阅读本文，并且只读取与当前任务相关的项目文档。

已批准的 Toolkit locator 为 `../max-ai-toolkit`。Toolkit 提供可复用的方法；本仓库仍然是 Avenor Launcher 产品事实、需求、架构、决策、实现、验证和发布信息的权威来源。

## 项目身份

- 产品名称：Avenor Launcher
- 仓库：`avenor-launcher`
- 项目所有者：Max
- 产品决策权：Max
- 技术决策权：Max
- 最终发布批准权：Max
- 内部代号：待确认

## 项目入口文档

- 公共项目入口：[README.md](README.md)
- 中文公共项目入口：[README.zh-CN.md](README.zh-CN.md)
- 产品概览英文语义源：[overview.md](overview.md)
- 中文产品概览：[overview.zh-CN.md](overview.zh-CN.md)
- 开发前检查清单与开放问题：[todo.md](todo.md)
- 许可证：[LICENSE](LICENSE)

架构、需求、开发、验证、安全、隐私和发布文档尚未建立。不得推断其内容，也不得将规划中的路径视为当前证据。

## 语言

英文是公共项目文档和面向仓库输出的语义源。内部思考可以使用中文，并可维护 `.zh-CN.md` 中文对应文档。当双语文档存在实质差异时，应先更新英文语义源，并尽可能在同一变更中同步中文文档。

Commit message、Pull Request、Issue、发布说明及其他公开仓库输出使用英文。

## 项目特定规则

- 遵守 `overview.md` 中的产品原则和已确认边界。
- 将未知产品或技术事实标记为“待确认”，不得把假设变成决策。
- 产品决策与技术架构决策分开记录。
- 不在产品文档中规定实现细节。
- 当产品契约或边界发生变化时，更新对应的权威文档。
- 只有在覆盖规则明确、范围清晰，并记录在本文或适用决策记录中时，本仓库才覆盖 Toolkit 指导。

## 验证基线

技术栈尚未选定，因此当前没有权威的构建、测试、lint、静态分析、模拟器或设备命令。对于纯文档变更，应检查 Git diff 并验证本地 Markdown 链接。在相应命令形成文档并实际执行前，不得报告产品构建或测试结果。
