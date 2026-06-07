# 短信发送功能修复报告

## 🐛 问题描述

用户报告：**发送验证码请求失败**

## 🔍 问题诊断

### 1. 后端服务状态检查

**初始检查**：
- 后端 Java 进程未运行或已停止
- 端口 8080 未监听

**第一次重启**：
- 后端启动成功
- 但 SMS 接口返回错误：`{"code":1000,"message":"系统内部错误","data":null}`

### 2. 错误根因分析

**后端日志显示**：
```
Caused by: io.lettuce.core.RedisConnectionException: 
Unable to connect to localhost/<unresolved>:6379
```

**分析结果**：
- AuthServiceImpl 的 `sendSmsCode` 方法依赖 RedisTemplate
- Redis 服务没有运行
- 导致验证码无法存储，抛出系统异常

## ✅ 解决方案

### 1. 安装 Redis（如果未安装）

```bash
# 检查 Redis 是否安装
redis-server --version

# 如果未安装，执行
apt-get update
apt-get install -y redis-server
```

### 2. 启动 Redis 服务

```bash
# 后台启动 Redis
redis-server --daemonize yes --protected-mode no

# 验证 Redis 是否运行
redis-cli ping
# 应返回：PONG
```

### 3. 重启后端服务

```bash
# 停止现有后端进程
pkill -9 java

# 等待端口释放
sleep 3

# 重启后端
cd /workspace/server
java -jar target/legal-assistant-1.0.0.jar
```

### 4. 验证修复

```bash
# 测试短信发送接口
curl -X POST http://localhost:8080/api/v1/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","type":"login"}'

# 预期成功响应
# {"code":0,"message":"success","data":null}

# 验证 Redis 中存储的验证码
redis-cli keys "sms:code:*"
redis-cli ttl "sms:code:13800138000"
```

## 📊 测试结果

### ✅ 后端连接
- Redis 连接：✅ 成功
- Tomcat 启动：✅ 成功（端口 8080）
- 后端应用：✅ 已启动

### ✅ 短信接口
- API 端点：`POST /api/v1/auth/sms/send`
- 请求参数：`phone`, `type`
- 响应状态：`code: 0` (成功)
- Redis 存储：✅ 验证码已存储
- 有效期：5 分钟

### ✅ 验证码功能
- 验证码生成：✅ 6 位数字
- Redis 存储：✅ key: `sms:code:<phone>`
- 过期时间：✅ 300 秒（5 分钟）
- 验证码验证：✅ `validateSmsCode` 方法可用

## 🔧 代码分析

### AuthServiceImpl.sendSmsCode()

```java
@Override
public void sendSmsCode(SendSmsRequest request) {
    String phone = request.getPhone();
    String code = generateSmsCode();

    // 存储验证码到 Redis
    String redisKey = SMS_CODE_PREFIX + phone;
    redisTemplate.opsForValue().set(redisKey, code, SMS_CODE_EXPIRE, TimeUnit.MINUTES);

    log.info("发送短信验证码：phone={}, code={}", phone, code);

    // TODO: 实际调用短信服务发送验证码
}
```

### 工作流程

1. 接收手机号和验证码类型
2. 生成 6 位随机验证码
3. 存储到 Redis，key: `sms:code:<phone>`
4. 设置 5 分钟过期时间
5. 记录日志（开发调试用）
6. TODO: 调用第三方短信服务商 API

## 📝 配置说明

### Redis 配置

**文件**：`server/src/main/resources/application.yml`

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      # 如果设置了密码
      # password: your-password
```

### Redis 启动参数

```bash
redis-server --daemonize yes --protected-mode no
```

- `--daemonize yes`: 后台运行
- `--protected-mode no`: 允许远程连接（开发环境）

## 🚀 开机自启（可选）

如果希望 Redis 服务开机自启：

```bash
# 使用 systemd 管理服务
sudo systemctl enable redis-server
sudo systemctl start redis-server

# 检查服务状态
sudo systemctl status redis-server
```

## ⚠️  注意事项

### 开发环境

1. **Redis 必须运行**：后端启动前确保 Redis 已启动
2. **检查端口**：确保 6379 端口可访问
3. **日志监控**：查看后端日志确认 Redis 连接成功

### 生产环境

1. **Redis 密码**：配置 Redis 密码增强安全性
2. **Redis 集群**：考虑使用 Redis 集群提高可用性
3. **短信服务商**：集成阿里云、腾讯云等短信服务
4. **限流保护**：添加短信发送频率限制

## 🛠️ 快速修复脚本

```bash
#!/bin/bash

echo "=== 短信发送功能修复脚本 ==="
echo ""

# 检查 Redis
if ! command -v redis-cli &> /dev/null; then
    echo "❌ Redis 未安装，正在安装..."
    apt-get update
    apt-get install -y redis-server
fi

# 检查 Redis 是否在运行
if ! redis-cli ping &> /dev/null; then
    echo "⚠️  Redis 未运行，正在启动..."
    redis-server --daemonize yes --protected-mode no
    sleep 2
fi

# 验证 Redis
if redis-cli ping &> /dev/null; then
    echo "✅ Redis 运行正常"
else
    echo "❌ Redis 启动失败"
    exit 1
fi

# 重启后端
echo "🔄 重启后端服务..."
pkill -9 java 2>/dev/null
sleep 3
cd /workspace/server
java -jar target/legal-assistant-1.0.0.jar &

# 等待后端启动
sleep 10

# 测试接口
echo "🧪 测试短信接口..."
curl -X POST http://localhost:8080/api/v1/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","type":"login"}'

echo ""
echo "=== 修复完成 ==="
```

## 📞 后续改进建议

### 1. 集成真实短信服务

```java
// TODO: 实际调用短信服务
// 示例：阿里云短信服务
AliyunSmsClient client = new AliyunSmsClient(accessKey, secret);
client.sendSms(phone, code);
```

### 2. 添加发送频率限制

```java
// 防止短信轰炸
String rateLimitKey = "sms:rate:" + phone;
Long count = redisTemplate.opsForValue().increment(rateLimitKey);
if (count == 1) {
    redisTemplate.expire(rateLimitKey, 1, TimeUnit.HOURS);
}
if (count > 5) {
    throw new BusinessException("发送过于频繁，请稍后再试");
}
```

### 3. 添加验证码校验次数限制

```java
// 防止暴力破解
String verifyLimitKey = "sms:verify:" + phone;
Long verifyCount = redisTemplate.opsForValue().increment(verifyLimitKey);
if (verifyCount > 3) {
    redisTemplate.delete(redisKey); // 删除验证码
    throw new BusinessException("验证码校验次数过多，请重新获取");
}
```

## ✅ 总结

**问题原因**：Redis 服务未启动导致验证码无法存储

**解决方案**：
1. ✅ 安装并启动 Redis
2. ✅ 重启后端服务
3. ✅ 验证短信发送功能

**当前状态**：功能已恢复正常

**修复时间**：2026-06-03  
**修复状态**：✅ 已完成
