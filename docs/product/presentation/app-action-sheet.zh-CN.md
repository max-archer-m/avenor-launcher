# 应用操作面板表现规范

> 英文公共语义源：[app-action-sheet.md](app-action-sheet.md)。

## 职责

本文档负责应用操作面板的精确表现数值。[应用操作面板行为](../surfaces/app-action-sheet.zh-CN.md)负责内容可用性、顺序语义和操作结果。

## 几何

- 应用信息与仅 Home 展示的启动器操作图形使用共享 `24dp` 功能图标 token。
- 浅色分割线使用 `16dp` 水平 inset。
- 每个可选应用快捷操作使用 `24dp` 图标和名称；该区域末尾使用同一 inset 分割线。
- 仅 Home 展示的启动器操作将 `24dp` 图标置于标签上方。Drawer 不预留启动器操作几何。
- Clone 或 profile badge 使用 `12dp x 12dp` 可见区域，对齐 Bottom Sheet 右下角且没有向外偏移。
