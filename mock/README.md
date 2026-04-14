# Mock 环境使用说明

## 📦 启动 Mock 服务

### 方式一：使用 Docker（推荐）

```bash
cd mock
docker-compose up -d
```

服务启动后访问：
- Mock API: http://localhost:4010
- API 文档: http://localhost:4010/docs

### 方式二：使用 Prism CLI

```bash
# 安装 Prism
npm install -g @stoplight/prism-cli

# 启动 Mock 服务
prism mock api/openapi.yaml -h 0.0.0.0
```

## 🧪 测试接口

### 1. 登录接口

```bash
curl -X POST http://localhost:4010/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "stu1", "password": "123456"}'
```

### 2. 获取菜品列表

```bash
curl http://localhost:4010/api/v1/dishes
```

### 3. 提交订单

```bash
curl -X POST http://localhost:4010/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"dishId": "D001", "portion": "whole", "quantity": 1},
      {"dishId": "D002", "portion": "half", "quantity": 1}
    ]
  }'
```

### 4. 管理员统计

```bash
curl http://localhost:4010/api/v1/admin/statistics/orders?date=2026-04-15
```

## 🌐 前端页面

启动 Mock 服务后，直接用浏览器打开：

- **学生订餐页**: `frontend/student-order.html`
- **管理员统计页**: `frontend/admin-stats.html`

## ⚠️ 注意事项

1. Mock 服务返回的是基于 OpenAPI 契约的模拟数据
2. 每次请求的响应可能略有不同（动态生成）
3. 前端页面已通过 JavaScript 实现完整逻辑闭环
4. 边界测试用例已在 `admin-stats.js` 中实现

## 🛑 停止服务

```bash
cd mock
docker-compose down
```
