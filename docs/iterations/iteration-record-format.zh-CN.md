# 迭代记录格式

> 英文语义源：[iteration-record-format.md](iteration-record-format.md)。

## 目的与命名

`docs/iterations/` 保存活动迭代交付契约。每个迭代是边界明确、可独立评审并服务于一个正式版本的交付单元。

文件名使用 `iteration-<number>-<title>.md`，全项目采用从 `1` 开始、单调递增的正整数唯一序列，例如 `iteration-1-home-minimum-usable-surface.md`。不得重编号、复用或重启序列，也不添加前导零。`<title>` 使用简短的小写英文 slug。

维护中文对应文档时，在 `.md` 前追加 `.zh-CN`，例如 `iteration-1-home-minimum-usable-surface.zh-CN.md`。中文文档使用相同迭代编号和英文 slug。

## 必需格式

新迭代记录使用以下结构。获得授权后可通过修改本文调整格式；已有历史记录不因后来格式变化而重写。

中文对应文档可以翻译标题和章节标题，但必须与英文语义源保持相同的章节顺序、范围和规范含义。

```markdown
# 迭代 <number>：<迭代标题>

> 适用版本契约与非授权边界

## 目标
## 产品和版本引用
## 可观察结果
## 纳入工作
## 排除工作
## 技术影响面
## 依赖和序列
## 迁移和兼容性影响
## 安全、隐私、权限和许可证影响
## 风险和未解决决定
## 验证计划
## 验收证据
## 相关决定、commit 和 tag
## 最终结果
## 剩余问题和交接
```

## 字段规则

- `Objective` 描述一个内聚交付结果，不是互不相关的任务清单。
- `Product and version references` 链接当前产品契约以及适用的 `docs/versions/<version>/delivery-contract.md` 或支撑输入，不复制这些文档。
- `Observable outcome` 描述迭代成功后可演示或验证的结果。
- `Included work` 与 `Excluded work` 明确迭代边界。
- `Technical change areas` 在持久层级标识受影响行为、组件、接口、数据、构建与验证表面；逐行变更仍以 Git 为准。
- 只有实际考虑过某一影响领域后才能写 `None identified`，不得静默省略重大影响。
- `Validation plan` 是预期计划；`Acceptance evidence` 记录实际观察的命令、环境、设备、结果和无法执行的检查。
- 迭代关闭时，根据证据填写 `Final result` 与 `Remaining issues and handoff`。关闭前只能描述所需关闭与交接证据，不得声称已完成。
- 迭代可以建议产品或技术决定，但不授权该决定。

正式版本完成后，将其纳入的原始迭代记录移入 `docs/archives/v<version>/` 并更新所有入站链接；下一迭代标识继续使用全项目序列。例如归档 `iteration-1-...` 至 `iteration-6-...` 后，下一个可用标识为 `7`。
