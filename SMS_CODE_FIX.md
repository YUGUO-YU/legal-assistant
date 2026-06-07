# 发送验证码功能修复完成

## ✅ 问题已解决

### 问题现象
发送验证码请求失败

### 根本原因
**Redis 服务未启动**导致后端无法存储验证码

### 解决方案
1. ✅ 安装并启动 Redis 服务
2. ✅ 重启后端服务
3. ✅ 验证短信接口正常工作

---

## 🔧 快速使用方法

### 启动所有服务

```bash
cd /workspace
./start.sh
```

这将自动：
- 检查并启动 Redis（端口 6379）
- 启动后端服务（端口 8080）
- 验证所有服务正常运行

### 停止所有服务

```bash
cd /workspace
./stop.sh
```

### 手动启动（可选）

```bash
# 1. 启动 Redis
redis-server --daemonize yes

# 2. 启动后端
cd /workspace/server
java -jar target/legal-assistant-1.0.0.jar
```

---

## 🧪 验证短信功能

### 1. 发送验证码

```bash
curl -X POST http://localhost:8080/api/v1/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","type":"login"}'
```

**成功响应**：
```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

### 2. 查看 Redis 中的验证码

```bash
# 查看验证码 key
redis-cli keys "sms:code:*"

# 查看验证码值（需要密码时加 -a <password>）
redis-cli -a 123456 GET "sms:code:13800138000"

# 查看剩余有效期（秒）
redis-cli -a 123456 TTL "sms:code:13800138000"
```

### 3. 测试带验证码登录

```bash
# 首先发送验证码（假设验证码是 123456）
curl -X POST http://localhost:8080/api/v1/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","type":"login"}'

# 然后使用验证码登录
curl -X POST http://localhost:8080/api/v1/auth/phone/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"123456"}'
```

---

## 📊 技术细节

### Redis 配置
- **主机**: localhost
- **端口**: 6379
- **密码**: 123456（开发环境）
- **数据库**: 0
- **验证码前缀**: `sms:code:`
- **有效期**: 5 分钟（300 秒）

### 短信 API
- **端点**: `POST /api/v1/auth/sms/send`
- **参数**:
  - `phone` (String): 手机号
  - `type` (String): 验证码类型（login, register, etc.）
- **认证**: 无需认证

### 验证码流程

```
用户请求 
  ↓
后端生成 6 位验证码
  ↓
存储到 Redis（key: sms:code:<phone>）
  ↓
设置 5 分钟过期
  ↓
日志记录验证码（开发环境）
  ↓
TODO: 调用第三方短信服务发送
```

---

## 🛠️ 故障排查

### 后端启动失败

```bash
# 查看日志
tail -f /tmp/backend.log

# 常见错误：端口被占用
netstat -tlnp | grep 8080
pkill -9 java
sleep 3
```

### Redis 启动失败

```bash
# 检查 Redis 是否安装
redis-server --version

# 如果未安装
apt-get install redis-server

# 查看 Redis 日志
tail -f /var/log/redis/redis-server.log
```

### 接口返回错误

```bash
# 后端未启动
curl http://localhost:8080/actuator/health

# Redis 未连接
redis-cli ping
# 应返回：PONG

# 查看后端日志
tail -100 /tmp/backend.log | grep -i error
```

---

## 📝 环境变量配置

### Redis 密码（可选）

如果使用了 Redis 密码，需要在 `application.yml` 中配置：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:123456}
      database: 0
```

启动时设置：
```bash
export REDIS_PASSWORD=123456
./start.sh
```

---

## 🚀 下一步

### 1. 集成真实短信服务

目前验证码仅记录在日志中，需要集成第三方服务商：

- **阿里云短信**
- **腾讯云短信**
- **七牛云短信**

### 2. 添加限流保护

```java
// 每小时最多发送 5 次
String rateLimitKey = "sms:rate:" + phone;
```

### 3. 测试前端

在 Web 版或小程序版中测试发送验证码功能：

```
1. 打开登录页面
2. 输入手机号
3. 点击"发送验证码"
4. 收到成功提示
5. 查看后端日志获取验证码
6. 输入验证码登录
```

---

## 📞 相关文档

- **修复报告**: `/workspace/SMS_FIX_REPORT.md`
- **快速参考**: `/workspace/QUICK_REFERENCE.md`
- **后端日志**: `/tmp/backend.log`

---

## ✅ 验证清单

- [x] Redis 服务已安装
- [x] Redis 服务已启动并运行
- [x] 后端服务已重启
- [x] 短信发送接口测试通过
- [x] Redis 存储验证码正常
- [x] 验证码有效期正常（5 分钟）
- [x] 启动脚本已创建
- [x] 停止脚本已创建

---

**修复完成时间**: 2026-06-03  
**修复状态**: ✅ 已完成  
**功能状态**: ✅ 正常运行
