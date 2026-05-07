# 法律助手 - 开发启动指南

## 环境要求

### 后端
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 前端
- Node.js 18+
- HBuilderX (推荐) 或 VS Code + uni-app 插件

## 后端启动

### 1. 初始化数据库

```bash
mysql -u root -p < scripts/init.sql
```

### 2. 配置数据库连接

编辑 `server/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/legal_assistant?useUnicode=true&characterEncoding=utf-8
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 3. 启动后端服务

```bash
cd server
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动。

### 4. 验证后端 API

```bash
curl http://localhost:8080/api/v1/health
```

## 前端启动

### 使用 HBuilderX（推荐）

1. 用 HBuilderX 打开 `client` 目录
2. 点击「运行」→「运行到浏览器」→「Chrome」
3. 或点击「运行」→「运行到小程序模拟器」→「微信开发者工具」

### 使用命令行

```bash
cd client
npm install
npm run dev:h5
```

前端将在 http://localhost:5173 启动（Vite 开发服务器）。

## Docker 部署

### 启动所有服务

```bash
docker-compose up -d
```

这将启动：
- OpenClaw Gateway (端口 8090)
- Legal Assistant Backend (端口 8080)

### 停止服务

```bash
docker-compose down
```

## API 文档

启动后端后，访问 Swagger UI:
- http://localhost:8080/swagger-ui.html

## 测试账号

| 手机号 | 密码 | 角色 |
|--------|------|------|
| 13800138000 | 123456 | 律师 |
| 13800138001 | 123456 | 用户 |

## 常见问题

### Q: Maven 依赖下载失败
A: 配置 Maven 镜像源或使用 VPN

### Q: MySQL 连接被拒绝
A: 检查 MySQL 服务是否启动，确认用户名密码正确

### Q: Redis 连接失败
A: 检查 Redis 服务是否启动，端口是否正确

### Q: 前端无法访问后端 API
A: 确认后端 CORS 配置允许前端域名访问
