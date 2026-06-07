# 法律助手 - 服务器配置指南

## 目录
1. [环境要求](#1-环境要求)
2. [Redis 配置](#2-redis-配置)
3. [MySQL 配置](#3-mysql-配置)
4. [微信登录配置](#4-微信登录配置)
5. [短信服务配置](#5-短信服务配置)
6. [邮件服务配置](#6-邮件服务配置)
7. [启动服务](#7-启动服务)
8. [验证配置](#8-验证配置)

---

## 1. 环境要求

```bash
# 检查已安装的服务
java -version          # 需要 Java 17+
mvn -version           # 需要 Maven 3.8+
node -v                # 需要 Node.js 18+
redis-cli --version    # 需要 Redis 7.0+
mysql --version        # 需要 MySQL 8.0+
```

---

## 2. Redis 配置

### 2.1 安装 Redis

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install redis-server -y

# 启动 Redis
sudo systemctl start redis
sudo systemctl enable redis

# 验证
redis-cli ping
# 输出: PONG
```

### 2.2 Redis 配置（可选）

配置文件位置: `/etc/redis/redis.conf`

```bash
# 设置密码（可选）
requirepass your_redis_password

# 配置远程访问（如果需要）
bind 0.0.0.0
port 6379
```

### 2.3 环境变量配置

```bash
# 在运行前设置环境变量
export REDIS_PASSWORD=your_redis_password

# 或在 application.yml 中配置
spring:
  data:
    redis:
      password: your_redis_password
```

---

## 3. MySQL 配置

### 3.1 安装 MySQL

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server -y

# 启动 MySQL
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 3.2 创建数据库和用户

```bash
# 登录 MySQL
sudo mysql -u root -p
```

```sql
-- 创建数据库
CREATE DATABASE legal_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'legal_user'@'localhost' IDENTIFIED BY 'your_password';

-- 授权
GRANT ALL PRIVILEGES ON legal_assistant.* TO 'legal_user'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;

-- 退出
EXIT;
```

### 3.3 导入数据表

```bash
# 导入数据库表结构
mysql -u legal_user -p legal_assistant < /workspace/server/src/main/resources/sql/schema.sql

# 或在 MySQL 中执行
mysql -u legal_user -p
USE legal_assistant;
SOURCE /workspace/server/src/main/resources/sql/schema.sql;
```

### 3.4 修改 application.yml

编辑 `/workspace/server/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/legal_assistant?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: legal_user
    password: your_password
```

### 3.5 设置环境变量（推荐）

```bash
export DB_PASSWORD=your_password
```

---

## 4. 微信登录配置

微信登录需要以下配置之一：
- 微信开放平台（用于 Web 扫码登录）
- 微信服务号（用于网页授权登录）

### 4.1 申请微信开放平台

1. 访问 https://open.weixin.qq.com
2. 注册开发者账号
3. 创建应用（网站应用）
4. 获取 AppID 和 AppSecret

### 4.2 配置微信参数

编辑 `application.yml`:

```yaml
wechat:
  open:
    appid: wx1234567890abcdef  # 你的 AppID
    secret: your_app_secret     # 你的 AppSecret
    redirect-uri: http://localhost:5173/wechat/callback
```

### 4.3 配置回调域名

在微信开放平台设置授权回调域名为：
- 开发环境: `localhost`
- 生产环境: `yourdomain.com`

---

## 5. 短信服务配置

### 5.1 阿里云短信服务

1. 访问 https://www.aliyun.com/product/sms
2. 开通短信服务
3. 创建签名和模板
4. 获取 AccessKey ID 和 AccessKey Secret

### 5.2 配置短信参数

编辑 `application.yml`:

```yaml
sms:
  secret-id: your_access_key_id
  secret-key: your_access_key_secret
  app-id: your_app_id
  template-login: SMS_123456789  # 登录验证码模板 ID
```

### 5.3 短信模板示例

```
模板名称: 登录验证码
模板内容: 您的验证码为 ${code}，5 分钟内有效，请勿泄露。
模板变量: code
```

---

## 6. 邮件服务配置

### 6.1 配置邮件发送

编辑 `application.yml`:

```yaml
spring:
  mail:
    host: smtp.aliyun.com
    port: 465
    username: your_email@aliyun.com
    password: your_email_password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          ssl:
            enable: true
```

### 6.2 常用邮箱配置

| 邮箱服务 | SMTP 服务器 | 端口 | SSL |
|---------|------------|------|-----|
| 阿里云邮箱 | smtp.aliyun.com | 465 | 是 |
| 腾讯企业邮箱 | smtp.exmail.qq.com | 465 | 是 |
| 163 邮箱 | smtp.163.com | 465 | 是 |
| Gmail | smtp.gmail.com | 587 | 是 |

---

## 7. 启动服务

### 7.1 一键启动

```bash
cd /workspace
./start.sh
```

### 7.2 手动启动

```bash
# 1. 启动 Redis
redis-server --daemonize yes

# 2. 启动 MySQL
sudo systemctl start mysql

# 3. 编译后端
cd /workspace/server
mvn clean package -DskipTests

# 4. 启动后端
nohup java -jar target/legal-assistant-1.0.0.jar \
  --spring.datasource.password=your_password \
  --spring.data.redis.password=your_redis_password \
  > /tmp/backend.log 2>&1 &

# 5. 启动前端
cd /workspace/client
npm run dev
```

### 7.3 使用环境变量

```bash
export DB_PASSWORD=your_password
export REDIS_PASSWORD=your_redis_password
export JWT_SECRET=your_jwt_secret
export WECHAT_OPEN_APPID=your_wechat_appid
export WECHAT_OPEN_SECRET=your_wechat_secret

# 启动应用
java -jar target/legal-assistant-1.0.0.jar
```

---

## 8. 验证配置

### 8.1 健康检查

```bash
# 检查服务状态
ps aux | grep -E "redis|java|mysql" | grep -v grep

# 检查端口
netstat -tlnp | grep -E "3306|6379|8080|5173"
```

### 8.2 API 测试

```bash
# 测试 Redis 连接
redis-cli ping
# 输出: PONG

# 测试 MySQL 连接
mysql -u legal_user -p -e "SELECT 1"
# 输出: 1

# 测试后端
curl http://localhost:8080/api/v1/auth/wechat/qr/generate
# 输出: {"code":0,"message":"success",...}

# 测试前端
curl http://localhost:5173
# 输出: HTML 页面
```

### 8.3 完整登录测试

```bash
# 1. 发送验证码
curl -X POST http://localhost:8080/api/v1/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","type":"login"}'

# 2. 获取验证码
redis-cli GET "sms:code:13800138000"

# 3. 使用验证码登录
curl -X POST http://localhost:8080/api/v1/auth/sms/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"你获取的验证码"}'
```

---

## 9. 生产环境配置清单

### 必填配置

| 配置项 | 说明 | 示例 |
|-------|------|------|
| spring.datasource.password | MySQL 密码 | ********** |
| spring.data.redis.password | Redis 密码 | ********** |
| jwt.secret | JWT 密钥 | 随机字符串 |

### 选填配置

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| wechat.open.appid | 微信 AppID | 空 |
| wechat.open.secret | 微信 Secret | 空 |
| sms.* | 短信服务配置 | 使用模拟发送 |
| spring.mail.* | 邮件服务配置 | 使用模拟发送 |

### 安全建议

1. **不要提交密钥到 Git**
   - 使用环境变量
   - 或使用配置文件本地覆盖

2. **生产环境必须修改**
   - JWT Secret
   - 数据库密码
   - Redis 密码

3. **使用 HTTPS**
   - 生产环境务必启用 HTTPS
   - 配置微信回调域名

---

## 10. 快速配置脚本

创建 `/workspace/config-env.sh`:

```bash
#!/bin/bash

# MySQL 配置
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=legal_assistant
export DB_USER=legal_user
export DB_PASSWORD=your_password

# Redis 配置
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=

# JWT 配置
export JWT_SECRET=your-super-secret-key-change-in-production

# 微信配置（可选）
export WECHAT_OPEN_APPID=
export WECHAT_OPEN_SECRET=

# 短信配置（可选）
export SMS_ACCESS_KEY_ID=
export SMS_ACCESS_KEY_SECRET=

echo "环境变量已配置"
```

使用：
```bash
source /workspace/config-env.sh
java -jar target/legal-assistant-1.0.0.jar
```

---

**配置完成后，重新编译并启动服务即可！**
