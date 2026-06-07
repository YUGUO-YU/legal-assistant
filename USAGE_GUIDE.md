# 法律助手 - 使用指南

## 快速开始

### 一键启动所有服务

```bash
cd /workspace
./start.sh
```

这将启动：
- Redis（端口 6379）
- 后端服务（端口 8080）
- 前端服务（端口 5173）

### 访问地址

- **Web 端**: http://localhost:5173/

## 登录方式

### 1. 邮箱验证码登录（新增）

**页面路径**: `/pages/auth/email-code-login`

**登录流程**:
1. 输入邮箱地址
2. 点击"发送验证码"
3. 查看邮箱获取验证码（当前在日志中查看）
4. 输入验证码
5. 点击"立即登录"

**测试步骤**:
```bash
# 1. 发送验证码
curl -X POST http://localhost:8080/api/v1/auth/email/code/send \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","type":"login"}'

# 2. 查看验证码（日志）
tail -100 /tmp/backend.log | grep "验证码"

# 3. 或从 Redis 查看
redis-cli GET "email:code:test@example.com"

# 4. 登录
curl -X POST http://localhost:8080/api/v1/auth/email/code/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","code":"验证码"}'
```

### 2. 短信验证码登录

**页面路径**: `/pages/auth/login`

**登录流程**:
1. 切换到"验证码登录"
2. 输入手机号
3. 点击"获取验证码"
4. 查看日志获取验证码
5. 输入验证码
6. 点击"登录"

**测试命令**:
```bash
# 发送验证码
curl -X POST http://localhost:8080/api/v1/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","type":"login"}'

# 登录
curl -X POST http://localhost:8080/api/v1/auth/sms/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"验证码"}'
```

### 3. 微信扫码登录

**页面路径**: `/pages/auth/login`（切换至微信扫码）

**配置要求**:
- 微信开放平台账号
- 已认证的服务号
- AppID 和 Secret 配置到 `application.yml`

**测试命令**:
```bash
# 生成二维码
curl http://localhost:8080/api/v1/auth/wechat/qr/generate

# 查询扫码状态
curl -X POST http://localhost:8080/api/v1/auth/wechat/qr/status \
  -H "Content-Type: application/json" \
  -d '{"scene":"WEBLogin_scene_123"}'
```

## 配置说明

### Redis 配置

默认配置（无需修改）：
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 微信配置

编辑 `src/main/resources/application.yml`：
```yaml
legal:
  assistant:
    wechat:
      app-id: "your_app_id"
      secret: "your_secret"
```

### 数据库配置

编辑 `pom.xml` 和 `application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/legal_assistant
    username: root
    password: your_password
```

## 核心功能

### 后端功能

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/v1/auth/sms/send` | POST | 发送短信验证码 |
| `/api/v1/auth/sms/login` | POST | 短信验证码登录 |
| `/api/v1/auth/email/code/send` | POST | 发送邮箱验证码 |
| `/api/v1/auth/email/code/login` | POST | 邮箱验证码登录 |
| `/api/v1/auth/wechat/qr/generate` | GET | 生成微信扫码二维码 |
| `/api/v1/auth/wechat/qr/status` | POST | 查询扫码状态 |

### 前端页面

| 页面 | 路径 | 功能 |
|------|------|------|
| 登录页 | `/pages/auth/login` | 短信验证码登录 + 微信扫码 |
| 邮箱验证码登录 | `/pages/auth/email-code-login` | 邮箱验证码登录 |
| 扫码登录组件 | `/src/components/WechatQrLogin.vue` | 微信扫码 UI 组件 |

## 验证码机制

### 验证码生成规则

- **长度**: 6 位数字
- **有效期**: 5 分钟
- **存储**: Redis

### Redis Key 格式

- **短信验证码**: `sms:code:{手机号}`
- **邮箱验证码**: `email:code:{邮箱}`

### 查看验证码

```bash
# 短信验证码
redis-cli GET "sms:code:13800138000"

# 邮箱验证码
redis-cli GET "email:code:test@example.com"

# 查看日志
tail -100 /tmp/backend.log | grep "验证码"
```

## 用户注册流程

首次登录时自动创建账号：
- **用户名**: 手机号/邮箱
- **默认角色**: lawyer（律师）
- **状态**: 启用

## 开发调试

### 查看服务状态

```bash
# 检查 Redis
redis-cli ping

# 检查后端
ps aux | grep java | grep legal

# 检查前端
ps aux | grep vite

# 查看端口
netstat -tlnp | grep -E "8080|5173|6379"
```

### 重启服务

```bash
# 停止所有服务
pkill -9 java
pkill -9 vite
redis-cli shutdown

# 启动所有服务
./start.sh
```

## 常见问题

### 1. 验证码无法获取

**原因**: Redis 未启动或连接失败

**解决**:
```bash
redis-server --daemonize yes
```

### 2. 后端启动失败

**原因**: 端口 8080 被占用

**解决**:
```bash
lsof -i:8080 | awk 'NR>1 {print $2}' | xargs kill -9
```

### 3. 前端编译错误

**原因**: Sass 变量或依赖问题

**解决**:
```bash
cd /workspace/client
npm install
npm run dev
```

## 下一步配置

### 生产环境部署

1. ✅ **配置真实短信服务商**（阿里云/腾讯云）
2. ✅ **配置真实邮件服务商**（SMTP/SendGrid）
3. ✅ **配置微信开放平台**（AppID + Secret）
4. ✅ **配置 MySQL 数据库**
5. ✅ **启用 HTTPS**

### 集成真实服务

#### 阿里云短信服务
编辑 `application.yml`：
```yaml
aliyun:
  sms:
    access-key: "your_access_key"
    secret: "your_secret"
    sign-name: "法律助手平台"
    template-code: "SMS_123456789"
```

#### 邮件服务（SMTP）
编辑 `application.yml`：
```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: noreply@example.com
    password: your_password
```

## 文件位置

### 后端核心文件

- `server/src/main/java/com/legal/assistant/module/auth/service/impl/AuthServiceImpl.java` - 认证核心逻辑
- `server/src/main/java/com/legal/assistant/module/auth/controller/AuthController.java` - 认证接口
- `server/src/main/resources/application.yml` - 配置文件

### 前端核心文件

- `client/src/pages/auth/login.vue` - 登录页面
- `client/src/pages/auth/email-code-login.vue` - 邮箱验证码登录页面
- `client/src/components/WechatQrLogin.vue` - 微信扫码组件

### 配置文件

- `start.sh` - 一键启动脚本
- `server/pom.xml` - Maven 配置
- `client/package.json` - npm 配置

## 技术支持

- Redis: https://redis.io/
- Vue 3: https://vuejs.org/
- Spring Boot: https://spring.io/projects/spring-boot
