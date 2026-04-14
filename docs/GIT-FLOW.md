# Git Flow 分支管理规范

## 📌 分支策略

本项目采用 **Git Flow** 工作流进行分支管理：

```
main/master (生产)
  └── develop (开发主干)
        ├── feature/user-login (功能分支)
        ├── feature/dish-order (功能分支)
        └── feature/admin-stats (功能分支)
```

## 🔄 分支命名规范

| 分支类型 | 命名格式 | 说明 | 示例 |
|---------|---------|------|------|
| 生产分支 | `main` / `master` | 生产环境代码 | `main` |
| 开发主干 | `develop` | 集成开发代码 | `develop` |
| 功能分支 | `feature/*` | 新功能开发 | `feature/user-auth` |
| 修复分支 | `fix/*` | Bug 修复 | `fix/order-calc` |
| 发布分支 | `release/*` | 版本发布准备 | `release/v1.0.0` |

## 📝 开发流程

### 1. 初始化开发环境

```bash
# 克隆仓库
git clone https://github.com/<your-org>/canteen-system.git
cd canteen-system

# 创建并切换到 develop 分支
git checkout -b develop

# 推送 develop 到远程
git push -u origin develop
```

### 2. 创建功能分支

```bash
# 从 develop 创建功能分支
git checkout develop
git checkout -b feature/<功能名称>

# 示例：创建用户登录功能
git checkout -b feature/user-auth
```

### 3. 开发与提交

```bash
# 开发过程中定期提交
git add .
git commit -m "feat: 实现用户登录功能"

# 提交信息规范：
# feat: 新功能
# fix: 修复 bug
# docs: 文档更新
# style: 代码格式调整
# refactor: 代码重构
# test: 测试相关
# chore: 构建/工具链相关
```

### 4. 反向同步主干（冲突预解决）

```bash
# 在发起 PR 前，先将 develop 的最新代码合并到功能分支
git checkout develop
git pull origin develop

git checkout feature/<功能名称>
git merge develop

# 如果有冲突，解决后提交
git add .
git commit -m "merge: 解决与 develop 的冲突"
```

### 5. 发起 Pull Request

```bash
# 推送功能分支到远程
git push origin feature/<功能名称>

# 在 GitHub 上发起 PR：
# - 源分支: feature/<功能名称>
# - 目标分支: develop
# - 填写 PR 描述，关联相关 issue
```

### 6. PR 评审流程

1. **创建 PR** 后，系统自动触发 CI 检查
2. **至少一名 Reviewer** 进行代码审查
3. **CI 通过** 且 **评审通过** 后合并
4. 合并后删除远程功能分支

## 📋 PR 模板

```markdown
## 📝 变更说明
<!-- 简要描述本次变更的目的 -->

## ✅ 测试
- [ ] 单元测试通过
- [ ] 功能测试通过
- [ ] CI 构建成功 (Green Build)

## 📸 截图 (如适用)
<!-- 添加 UI 变更截图 -->

## 🔗 关联 Issue
<!-- 关联相关 Issue，如: Closes #123 -->
```

## ⚠️ 注意事项

1. **禁止直接 push 到 develop/main**，必须通过 PR
2. **PR 前必须反向同步 develop**，解决冲突
3. **CI 必须通过** 才能合并（Green Build）
4. **至少一次 PR 评审** 记录
5. 合并后及时删除已合并的功能分支

## 📊 评审记录示例

### PR #1: 用户登录功能
- **分支**: `feature/user-auth` → `develop`
- **评审人**: @reviewer1
- **评审意见**: 
  - ✅ 代码结构清晰
  - ✅ 单元测试覆盖率高
  - 💡 建议：添加密码强度校验
- **状态**: ✅ 已合并

### PR #2: 菜品下单功能
- **分支**: `feature/dish-order` → `develop`
- **评审人**: @reviewer2
- **评审意见**:
  - ✅ 接口设计符合 OpenAPI 契约
  - 💡 建议：优化订单查询性能
- **状态**: ✅ 已合并
