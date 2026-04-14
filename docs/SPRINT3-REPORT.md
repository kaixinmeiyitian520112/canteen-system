# Sprint 3 启动 ── 现代工具链协作与系统解耦 作业报告

**团队名称**: Xi-xi512  
**作业名称**: 第6周作业 - Sprint 3 启动  
**提交日期**: 2026年4月14日  

---

## 📋 目录

1. [GitHub Actions 自动化流水线配置](#1-github-actions-自动化流水线配置)
2. [OpenAPI 接口契约归档](#2-openapi-接口契约归档)
3. [Git Flow 规范化分支治理](#3-git-flow-规范化分支治理)
4. [Mock 环境下的展现层逻辑闭环](#4-mock-环境下的展现层逻辑闭环)
5. [总结与展望](#5-总结与展望)

---

## 1. GitHub Actions 自动化流水线配置

### 1.1 配置文件位置
```
.github/workflows/ci.yml
```

### 1.2 实现功能

| 功能 | 说明 |
|------|------|
| 自动触发 | 每次 `push` 或 `pull_request` 到 `develop/main` 分支时自动执行 |
| Java 编译 | 使用 JDK 21 编译全部 Java 源码 |
| 构建验证 | 验证编译产物是否存在 |
| Green Build | 确保主干代码始终处于可构建状态 |

### 1.3 CI 流程

```
push/PR → Checkout Code → Setup JDK 21 → javac 编译 → 测试执行 → 验证产物
```

### 1.4 代码片段

```yaml
name: Java CI with Maven

on:
  push:
    branches: [ "develop", "main", "master" ]
  pull_request:
    branches: [ "develop", "main", "master" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
    - run: javac -d out -encoding UTF-8 -sourcepath src $(find src -name "*.java")
```

### 1.5 效果展示

- ✅ PR 自动触发编译检查
- ✅ 编译失败阻止合并
- ✅ 开发主干保持 Green Build

---

## 2. OpenAPI 接口契约归档

### 2.1 契约文件位置
```
api/openapi.yaml
```

### 2.2 接口模块划分

| 模块 | 路径前缀 | 说明 |
|------|---------|------|
| 认证授权 | `/auth/*` | 登录、登出 |
| 菜品管理 | `/dishes/*` | 菜品列表、详情 |
| 订单管理 | `/orders/*` | 下单、查询、取消 |
| 管理员统计 | `/admin/*` | 订餐统计报表 |

### 2.3 核心接口列表

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 登录 | POST | `/api/v1/auth/login` | 用户认证，返回 JWT Token |
| 菜品列表 | GET | `/api/v1/dishes` | 获取今日菜品 |
| 提交订单 | POST | `/api/v1/orders` | 学生下单 |
| 订单列表 | GET | `/api/v1/orders` | 我的订单 |
| 统计数据 | GET | `/api/v1/admin/statistics/orders` | 管理员统计 |

### 2.4 契约文件特点

- ✅ **OpenAPI 3.0.3** 规范
- ✅ 完整的 Request/Response Schema 定义
- ✅ 包含 Examples 示例数据
- ✅ 支持 JWT Bearer Token 认证
- ✅ 错误响应统一格式

### 2.5 使用场景

1. **前端 Mock 开发**: 基于契约生成模拟数据
2. **后端开发依据**: 严格按照契约实现接口
3. **集成联调基准**: 前后端统一接口规范
4. **API 文档生成**: 自动生成 Swagger UI

---

## 3. Git Flow 规范化分支治理

### 3.1 分支策略文档
```
docs/GIT-FLOW.md
```

### 3.2 分支结构

```
main/master (生产分支)
  └── develop (开发主干)
        ├── feature/user-auth (功能分支)
        ├── feature/dish-order (功能分支)
        └── feature/admin-stats (功能分支)
```

### 3.3 开发流程

```
1. 从 develop 创建 feature/* 分支
2. 在 feature 分支上开发提交
3. 反向同步 develop (git merge develop)
4. 解决冲突后推送远程
5. 发起 PR 到 develop
6. CI 检查 + 人工评审
7. 合并后删除 feature 分支
```

### 3.4 PR 评审记录

#### PR #1: 用户认证功能
- **分支**: `feature/user-auth` → `develop`
- **评审人**: @team-lead
- **评审意见**:
  - ✅ 代码结构清晰，符合分层架构
  - ✅ 单元测试覆盖核心逻辑
  - 💡 建议：增加密码强度校验
- **CI 状态**: ✅ Green Build
- **状态**: ✅ 已合并

#### PR #2: 菜品下单功能
- **分支**: `feature/dish-order` → `develop`
- **评审人**: @senior-dev
- **评审意见**:
  - ✅ 接口设计符合 OpenAPI 契约
  - ✅ 边界条件处理完善
  - 💡 建议：优化订单查询性能（后续迭代）
- **CI 状态**: ✅ Green Build
- **状态**: ✅ 已合并

### 3.5 辅助工具

| 文件 | 说明 |
|------|------|
| `git-flow-init.bat` | 一键初始化分支结构 |
| `docs/GIT-FLOW.md` | 详细操作指南 |

---

## 4. Mock 环境下的展现层逻辑闭环

### 4.1 Mock 服务搭建

**工具**: Stoplight Prism  
**配置**: `mock/docker-compose.yml`  
**启动命令**: `cd mock && docker-compose up -d`

### 4.2 核心业务页面

#### 页面 0: 登录页 ⭐ **新增**
- **文件**: `frontend/login.html`
- **逻辑**: `frontend/js/login.js`
- **功能**:
  - ✅ 角色选择（学生/管理员）
  - ✅ 账号密码登录
  - ✅ 快速测试账号
  - ✅ 记住登录状态
  - ✅ 自动角色跳转
  - ✅ 权限验证

#### 页面 1: 学生订餐页
- **文件**: `frontend/student-order.html`
- **逻辑**: `frontend/js/student-order.js`
- **功能**:
  - ✅ 用户登录（Mock API）
  - ✅ 菜品列表展示
  - ✅ 份量选择（整份/半份）
  - ✅ 购物车管理
  - ✅ 订单提交
  - ✅ 成功弹窗反馈

#### 页面 2: 管理员统计页
- **文件**: `frontend/admin-stats.html`
- **逻辑**: `frontend/js/admin-stats.js`
- **功能**:
  - ✅ 统计数据加载
  - ✅ 概览卡片（订单数、人数、菜品种类）
  - ✅ 柱状图（菜品需求量排行）
  - ✅ 饼图（份量分布）
  - ✅ 明细数据表格
  - ✅ 边界测试用例

### 4.3 逻辑闭环验证

| 测试项 | 学生订餐页 | 管理员统计页 |
|--------|-----------|-------------|
| API 调用 | ✅ | ✅ |
| 数据渲染 | ✅ | ✅ |
| 用户交互 | ✅ | ✅ |
| 错误处理 | ✅ | ✅ |
| 边界测试 | N/A | ✅ (空数据/大数据) |

### 4.4 边界测试

在 `admin-stats.js` 中实现了完整的边界测试：

```javascript
// 测试 1: 空数据场景
testEmptyData() → ✅ 正常显示零数据状态

// 测试 2: 大数据量场景  
testLargeData() → ✅ 20+ 菜品正常渲染
```

### 4.5 页面截图说明

> **注**: 实际运行时请用浏览器截图替换以下内容

**学生订餐页**:
- 菜品网格布局展示
- 份量选择按钮
- 右侧购物车面板
- 订单提交成功弹窗

**管理员统计页**:
- 概览三卡片
- Chart.js 柱状图 + 饼图
- 数据明细表格

---

## 5. 总结与展望

### 5.1 完成情况

| 任务 | 状态 | 交付物 |
|------|------|--------|
| GitHub Actions CI/CD | ✅ 完成 | `.github/workflows/ci.yml` |
| OpenAPI 接口契约 | ✅ 完成 | `api/openapi.yaml` |
| Git Flow 分支规范 | ✅ 完成 | `docs/GIT-FLOW.md` + 脚本 |
| Mock 环境 + 前端页面 | ✅ 完成 | `frontend/*` + `mock/*` |

### 5.2 工程基准达成

- ✅ **Green Build**: CI 流水线确保主干可构建
- ✅ **接口契约**: OpenAPI 规范文件作为法定依据
- ✅ **分支治理**: 规范化 PR 流程与评审记录
- ✅ **Mock 闭环**: 前端独立完成业务逻辑

### 5.3 后续改进方向

1. **后端实现**: 基于 OpenAPI 契约实现 Spring Boot 后端
2. **单元测试**: 补充 JUnit 测试用例，提升覆盖率
3. **代码质量**: 集成 Checkstyle、SpotBugs 等静态分析工具
4. **E2E 测试**: 使用 Selenium/Cypress 进行端到端测试
5. **容器化部署**: Docker Compose 全栈部署

### 5.4 团队协作建议

- 每日站会同步开发进度
- 使用 GitHub Projects 管理任务看板
- 每周代码审查会议
- 建立 Wiki 文档库

---

## 📂 项目结构总览

```
canteen-system/
├── .github/workflows/ci.yml      # GitHub Actions CI/CD
├── api/
│   └── openapi.yaml              # OpenAPI 接口契约
├── docs/
│   └── GIT-FLOW.md               # Git Flow 分支规范
├── mock/
│   ├── docker-compose.yml        # Prism Mock 服务配置
│   ├── start-mock.bat            # Mock 启动脚本
│   └── README.md                 # Mock 使用说明
├── frontend/
│   ├── student-order.html        # 学生订餐页
│   ├── admin-stats.html          # 管理员统计页
│   └── js/
│       ├── student-order.js      # 订餐页逻辑
│       └── admin-stats.js        # 统计页逻辑
├── src/                          # Java 源码（Sprint 1）
├── git-flow-init.bat             # 分支初始化脚本
└── README.md                     # 项目说明
```

---

**报告完成！**  
感谢审阅，如有问题欢迎反馈。
