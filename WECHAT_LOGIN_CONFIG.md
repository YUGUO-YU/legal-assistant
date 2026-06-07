# 微信登录配置指南

## 概述

法律助手支持微信扫码登录功能，需要配置微信开放平台（Web 端）或微信小程序（移动端）的认证信息。

## 配置方式

### 方式一：环境变量配置

在运行后端服务前，设置以下环境变量：

```bash
# 微信开放平台（Web 端扫码登录）
export WECHAT_OPEN_APPID=your_open_platform_appid
export WECHAT_OPEN_SECRET=your_open_platform_secret
export WECHAT_OPEN_REDIRECT_URI=http://your-domain.com/wechat/callback

# 微信小程序（手机登录）
export WECHAT_MINIAPP_APPID=your_miniapp_appid
export WECHAT_MINIAPP_SECRET=your_miniapp_secret

# 微信公众号（可选）
export WECHAT_OA_APPID=your_official_account_appid
export WECHAT_OA_SECRET=your_official_account_secret
export WECHAT_OA_TOKEN=your_token
export WECHAT_OA_AESKEY=your_aes_key
```

### 方式二：直接修改配置文件

编辑 `server/src/main/resources/application.yml`：

```yaml
wechat:
  open:
    appid: your_open_platform_appid      # 替换为你的 AppID
    secret: your_open_platform_secret    # 替换为你的 AppSecret
    redirect-uri: http://your-domain.com/wechat/callback

  miniapp:
    appid: your_miniapp_appid
    secret: your_miniapp_secret

  oa:
    appid: your_official_account_appid
    secret: your_official_account_secret
    token: your_token
    aes-key: your_aes_key
```

## 申请微信开放平台账号

### 1. 注册开放平台账号

1. 访问 [微信开放平台](https://open.weixin.qq.com)
2. 注册开发者账号（需要企业资质）
3. 完成开发者认证

### 2. 创建网站应用

1. 登录开放平台 → 管理中心 → 网站应用
2. 点击「创建网站应用」
3. 填写应用信息：
   - 应用名称：法律助手
   - 应用描述：法律效率工具
   - 官方网址：你的网站域名
4. 提交审核（通常 7 个工作日）

### 3. 获取 AppID 和 AppSecret

审核通过后，在应用详情页获取：
- AppID（应用唯一标识）
- AppSecret（应用密钥）

### 4. 配置回调域名

在开放平台设置「授权回调域」：
- 生产环境：填写你的正式域名
- 开发环境：`localhost`

## 申请微信小程序（手机登录）

### 1. 注册小程序账号

1. 访问 [微信公众平台](https://mp.weixin.qq.com)
2. 选择「小程序」类型注册
3. 完成主体认证

### 2. 获取 AppID 和 AppSecret

1. 登录小程序管理后台
2. 开发管理 → 开发设置
3. 获取 AppID 和 AppSecret

## 临时测试方案

如果暂时没有微信开放平台账号，可以：

### 1. 使用模拟登录

在开发环境注释掉微信登录相关代码，使用手机号或邮箱登录。

### 2. 修改登录页面

编辑 `client/src/pages/auth/login.vue`，临时隐藏微信登录入口：

```vue
<!-- 注释掉这行 -->
<!-- <view class="login-method" @click="switchToWechatLogin">微信登录</view> -->
```

## 配置检查

配置完成后，验证方式：

```bash
# 重启后端服务
cd server && mvn spring-boot:run

# 测试获取二维码
curl http://localhost:8080/api/v1/auth/wechat/qr/generate
```

如果返回二维码 URL，说明配置成功。

## 常见问题

### Q: 二维码不显示？

检查后端日志，确保 `WECHAT_OPEN_APPID` 和 `WECHAT_OPEN_SECRET` 已正确配置。

### Q: 扫码后提示 redirect_uri 错误？

在微信开放平台设置正确的授权回调域，与 `WECHAT_OPEN_REDIRECT_URI` 配置一致。

### Q: 提示「你未开设微信登录权限」？

需要先在微信开放平台创建网站应用并通过审核。

## 生产环境注意事项

1. **安全存储**：AppSecret 不要硬编码在代码中，使用环境变量或配置中心
2. **HTTPS**：生产环境必须使用 HTTPS
3. **回调域名**：使用已备案的正式域名
4. **错误处理**：生产环境应屏蔽详细的错误信息
