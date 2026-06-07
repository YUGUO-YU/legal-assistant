# 法律助手 - 安装与测试完整指南

## 目录

1. [环境要求](#1-环境要求)
2. [安装步骤](#2-安装步骤)
3. [配置说明](#3-配置说明)
4. [启动服务](#4-启动服务)
5. [功能测试](#5-功能测试)
6. [问题排查](#6-问题排查)

---

## 1. 环境要求

### 必需软件

| 软件 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 后端运行 |
| Maven | 3.8+ | 后端编译 |
| Node.js | 18+ | 前端运行 |
| Redis | 7.0+ | 验证码存储 |
| MySQL | 8.0+ | 数据存储 |

### 检查命令

```bash
java -version
mvn -version
node -v
npm -v
redis-cli --version
mysql --version
```

---

## 2. 安装步骤

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
```

### 2.2 安装 MySQL

```bash
sudo apt update
sudo apt install mysql-server -y

sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置
sudo mysql_secure_installation
```

### 2.3 安装 Java 17

```bash
sudo apt update
sudo apt install openjdk-17-jdk -y

java -version
```

### 2.4 安装 Maven

```bash
sudo apt install maven -y
mvn -version
```

### 2.5 安装 Node.js 18

```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

node -v
npm -v
```

### 2.6 安装前端依赖

```bash
cd /workspace/client
npm install
```

### 2.7 编译后端

```bash
cd /workspace/server
mvn clean package -DskipTests
```

---

## 3. 配置说明

### 3.1 Redis 配置

文件：`server/src/main/resources/application.yml`

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

### 3.2 MySQL 配置

**创建数据库:**

```bash
mysql -u root -p
```

```sql
CREATE DATABASE legal_assistant DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'legal_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON legal_assistant.* TO 'legal_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**修改配置:**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/legal_assistant?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: legal_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3.3 微信配置

```yaml
legal:
  assistant:
    wechat:
      app-id: "your_app_id"
      secret: "your_secret"
```

---

## 4. 启动服务

### 4.1 一键启动

```bash
cd /workspace
./start.sh
```

### 4.2 手动启动

**启动 Redis:**
```bash
redis-server --daemonize yes
redis-cli ping
```

**启动 MySQL:**
```bash
sudo systemctl start mysql
```

**启动后端:**
```bash
cd /workspace/server
mvn clean package -DskipTests
nohup java -jar target/legal-assistant-1.0.0.jar > /tmp/backend.log 2>&1 &
tail -20 /tmp/backend.log | grep "Started"
```

**启动前端:**
```bash
cd /workspace/client
npm run dev > /tmp/frontend.log 2>&1 &
tail -10 /tmp/frontend.log | grep "ready"
```

### 4.3 检查服务状态

```bash
ps aux | grep -E "redis|java|vite|node|mysql" | grep -v grep
netstat -tlnp | grep -E "6379|8080|5173|3306"
```

---

## 5. 功能测试

### 5.1 访问前端页面

```
http://localhost:5173/#/pages/auth/login
```

### 5.2 测试短信验证码

**发送验证码:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","type":"login"}'
```

**查看验证码:**
```bash
redis-cli GET "sms:code:13800138000"
```

### 5.3 测试邮箱验证码

**发送验证码:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/email/code/send \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","type":"login"}'
```

**查看验证码:**
```bash
redis-cli GET "email:code:test@example.com"
```

### 5.4 测试登录

```bash
SMS_CODE=$(redis-cli GET "sms:code:13800138000" | tr -d '"')

curl -X POST http://localhost:8080/api/v1/auth/sms/login \
  -H "Content-Type: application/json" \
  -d "{\"phone\":\"13800138000\",\"code\":\"$SMS_CODE\"}"
```

### 5.5 测试微信扫码

```bash
curl http://localhost:8080/api/v1/auth/wechat/qr/generate
```

### 5.6 测试响应式

打开浏览器 F12 -> 设备模拟器 -> 选择不同设备：
- iPhone 12 Pro (390x844)
- iPad Pro (1024x1366)
- Desktop (1920x1080)

---

## 6. 问题排查

### 6.1 Redis 无法连接

```bash
redis-cli ping
# 如果无响应
redis-server --daemonize yes
```

### 6.2 端口被占用

```bash
lsof -i:8080 | awk 'NR>1 {print $2}' | xargs kill -9
```

### 6.3 数据库连接失败

```bash
sudo systemctl status mysql
sudo systemctl start mysql
```

### 6.4 前端编译错误

```bash
cd /workspace/client
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### 6.5 后端编译失败

```bash
cd /workspace/server
mvn clean
mvn package -DskipTests
```

### 6.6 查看日志

```bash
# 后端日志
tail -100 /tmp/backend.log

# 查看错误
tail -100 /tmp/backend.log | grep ERROR

# 前端日志
tail -100 /tmp/frontend.log
```

### 6.7 验证码相关

```bash
# 查看验证码
tail -100 /tmp/backend.log | grep "验证码"

# Redis 中的验证码
redis-cli KEYS "*:code:*"

# 清空验证码
redis-cli KEYS "*:code:*" | xargs redis-cli DEL
```

---

## 7. 快速参考

### 常用命令

```bash
# 启动所有服务
cd /workspace && ./start.sh

# 停止所有服务
pkill -9 java && pkill -f vite && redis-cli shutdown

# 查看服务状态
ps aux | grep -E "redis|java|vite" | grep -v grep

# 重启后端
pkill -f legal-assistant && cd /workspace/server && java -jar target/legal-assistant-1.0.0.jar &

# 重启前端
pkill -f vite && cd /workspace/client && npm run dev
```

### 访问地址

| 服务 | URL |
|------|-----|
| 登录页面 | http://localhost:5173/#/pages/auth/login |
| 后端 API | http://localhost:8080/api/v1 |

### API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/auth/sms/send` | POST | 发送短信验证码 |
| `/api/v1/auth/sms/login` | POST | 短信验证码登录 |
| `/api/v1/auth/email/code/send` | POST | 发送邮箱验证码 |
| `/api/v1/auth/email/code/login` | POST | 邮箱验证码登录 |
| `/api/v1/auth/wechat/qr/generate` | GET | 生成微信二维码 |
