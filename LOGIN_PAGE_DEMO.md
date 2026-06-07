# 登录页面演示

## 快速访问

```
http://localhost:5173/#/pages/auth/login
```

## 功能演示

### 1️⃣ 手机号验证码登录
- 输入 11 位手机号
- 点击"发送验证码"（60 秒倒计时）
- 输入验证码
- 点击"立即登录"

### 2️⃣ 邮箱验证码登录
- 点击"使用邮箱验证码登录"或 📧 图标
- 输入邮箱地址
- 点击"发送验证码"（60 秒倒计时）
- 输入验证码
- 点击"立即登录"

### 3️⃣ 微信扫码登录
- 点击"扫码登录"或 💚 图标
- 弹窗显示二维码
- 使用微信扫描二维码
- 等待授权登录

## UI 特性

### 🎨 视觉效果
- ✨ Logo 浮动动画
- ✨ 标题呼吸效果
- ✨ 表单淡入动画
- ✨ 按钮 Hover 上浮
- ✨ 图标旋转特效

### 📱 响应式设计
- 🖥️ Desktop: 完整布局
- 📱 Tablet (≤768px): 紧凑布局
- 📱 Mobile (≤480px): 垂直布局

### 🎯 交互反馈
- Hover 效果（按钮、图标、链接）
- 点击反馈（按钮按下效果）
- 加载动画（旋转 loading）
- 禁用状态（半透明灰化）

## 动画演示

### 登录按钮 Hover
```
正常 → Hover: 上浮 3px + 阴影增强
       点击: 快速回弹
       加载: 旋转动画 + 文字淡出
```

### 其他登录方式图标
```
微信扫码：绿色渐变 + 绿色阴影
邮箱验证：蓝色渐变 + 蓝色阴影
Hover: 放大 1.1 倍 + 旋转 5 度
```

### 切换模式
```
点击"使用邮箱验证码登录":
- 表单淡出 → 切换图标 → 新表单淡入
- 平滑过渡动画 0.3s
```

## 配色方案

### 品牌色
- 🔵 主蓝色：`#1890ff`
- 🌊 深蓝色：`#096dd9`
- 💚 微信绿：`#07c160`

### 渐变效果
- 登录按钮：`#1890ff` → `#096dd9`
- 微信图标：`#07c160` → `#05a850`
- 邮箱图标：`#1890ff` → `#096dd9`
- 切换背景：`#f0f9ff` → `#e6f7ff`

## 测试命令

```bash
# 查看前端服务状态
ps aux | grep vite | grep -v grep

# 访问登录页面
curl http://localhost:5173

# 测试后端 API
curl -X POST http://localhost:8080/api/v1/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","type":"login"}'

# 测试邮箱验证码
curl -X POST http://localhost:8080/api/v1/auth/email/code/send \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","type":"login"}'

# 查看验证码
redis-cli GET "sms:code:13800138000"
redis-cli GET "email:code:test@example.com"
```

## 文件位置

- 登录页面：`/workspace/client/src/pages/auth/login.vue`
- 扫码组件：`/workspace/client/src/components/WechatQrLogin.vue`
- 美化文档：`/workspace/UI_IMPROVEMENTS.md`

## 注意事项

⚠️ **当前环境配置**
- Redis 必须运行（验证码存储）
- 后端服务必须运行（8080 端口）
- 前端服务必须运行（5173 端口）

ℹ️ **验证码查看**
- 开发模式：查看日志文件 `tail -100 /tmp/backend.log | grep 验证码`
- 或 Redis CLI: `redis-cli KEYS "*:code:*"`

🔧 **如需重启**
```bash
cd /workspace
./start.sh
```
