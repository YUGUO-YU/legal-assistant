# 法律助手 - 本地部署指南

## 环境要求

### 后端依赖
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 前端依赖
- Node.js 18+
- npm 或 pnpm

## 快速启动

### 1. 克隆项目
```bash
git clone https://github.com/YUGUO-YU/legal-assistant.git
cd legal-assistant
```

### 2. 启动后端

```bash
# 初始化数据库
mysql -u root -p < scripts/init.sql

# 配置数据库连接
# 编辑 server/src/main/resources/application.yml

# 启动后端
cd server
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd client
npm install
npm run dev:h5
```

访问 http://localhost:5173

## Docker 部署（生产环境）

### 1. 配置环境变量

创建 `.env` 文件：
```bash
JWT_SECRET=your-jwt-secret-key
OPENCLAW_API_KEY=your-openclaw-api-key
WECHAT_CORP_ID=your-corp-id
WECHAT_AGENT_ID=your-agent-id
WECHAT_SECRET=your-secret
WECHAT_TOKEN=your-token
WECHAT_AES_KEY=your-aes-key
TELEGRAM_BOT_TOKEN=your-telegram-bot-token
```

### 2. 启动服务

```bash
docker-compose up -d
```

### 3. 服务端口

| 服务 | 端口 | 地址 |
|------|------|------|
| 前端 H5 | 5173 | http://localhost:5173 |
| 后端 API | 8080 | http://localhost:8080 |
| OpenClaw Gateway | 8090 | http://localhost:8090 |
| Swagger UI | 8080 | http://localhost:8080/swagger-ui.html |

## OpenClaw AI 服务配置

### Minimax2.7 大模型配置

配置文件位置：`openclaw/.openclaw/config/openclaw.json`

```json
{
  "llm": {
    "minimax2.7": {
      "apiKey": "your-minimax-api-key",
      "baseURL": "https://api.minimax.chat/v2",
      "model": "Minimax2.7"
    }
  }
}
```

### 法律 Skills

已配置以下 Skills：
- china-legal-query - 法律查询
- china-contract-review - 合同审查
- china-legal-analysis - 法律分析
- mova-contract-generation - 合同生成
- regulation-monitor - 法规监控
- china-company-search - 企业查询
- caseclaw - 案例分析
- web-search - 网页搜索
- document-pro - 文档处理
- internet-search - 互联网搜索

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

### Q: OpenClaw Gateway 启动失败
A: 检查 Node.js 是否安装，确认端口 8090 是否被占用

### Q: 微信渠道接入失败
A: 检查企业微信配置是否正确，确保公网可访问回调地址