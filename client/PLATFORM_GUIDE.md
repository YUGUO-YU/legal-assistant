# Web 版和小程序版适配说明

本文档说明如何在 Web 版 (H5) 和微信小程序版之间适配和构建项目。

## 📱 平台特性

### 微信小程序版 (MP-WEIXIN)
- 运行在微信客户端内
- 支持微信原生能力（登录、支付、分享等）
- 使用微信小程序 API
- 需要通过微信审核

### Web 版 (H5)
- 运行在浏览器中
- 支持桌面和移动浏览器
- 响应式设计，自适应不同屏幕尺寸
- 无需审核，部署即可访问

## 🔧 开发命令

### 开发环境

```bash
# 微信小程序开发
npm run dev:mp-weixin

# H5 Web 开发
npm run dev:h5
```

### 生产构建

```bash
# 微信小程序生产构建
npm run build:mp-weixin

# H5 Web 生产构建
npm run build:h5

# H5 Web 生产构建（生产模式）
npm run build:h5:prod

# H5 Web 预览
npm run preview:h5
```

## 📦 输出目录

### 微信小程序
```
dist/build/mp-weixin/
├── project.config.json    # 微信项目配置
├── project.private.config.json
├── app.js                 # 小程序入口
├── app.json
├── app.wxss
└── pages/                 # 页面文件
```

### H5 Web
```
dist/build/h5/
├── index.html             # 入口 HTML
├── assets/                # 静态资源
│   ├── css/
│   └── js/
└── static/                # 复制的静态文件
```

## 🎯 平台特定代码

### 条件编译语法

uni-app 支持条件编译，可以针对不同平台编写特定代码：

```typescript
// 微信小程序特定代码
// #ifdef MP-WEIXIN
uni.login({
  provider: 'weixin'
})
// #endif

// H5 特定代码
// #ifdef H5
console.log('Running on H5')
// #endif

// 非 H5 平台代码
// #ifndef H5
console.log('Not running on H5')
// #endif
```

### 示例：微信登录

```typescript
async function wechatLogin() {
  // #ifdef MP-WEIXIN
  const res = await uni.login({
    provider: 'weixin'
  })
  // 处理微信登录
  
  // #endif
  
  // #ifdef H5
  uni.showToast({
    title: '请使用微信小程序访问',
    icon: 'none'
  })
  // #endif
}
```

## 🎨 平台适配

### 样式适配

项目已配置平台特定的样式：

- **H5**: 最大宽度 750px，桌面浏览器居中显示
- **小程序**: 使用全屏显示

### API 适配

HTTP 请求已统一封装在 `src/services/api.ts`，自动处理：

- Token 注入
- 错误处理
- 平台特定的请求头

### 登录方式

| 平台 | 支持的登录方式 |
|------|----------------|
| H5 | 邮箱密码登录、手机验证码登录 |
| 小程序 | 微信一键登录、手机验证码登录 |

## 🌐 环境配置

### H5 代理配置

开发环境下，H5 已配置代理到后端：

```typescript
// vite.config.ts
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

### 小程序域名配置

生产环境下，小程序需要在微信公众平台配置合法域名：

1. 登录 [微信公众平台](https://mp.weixin.qq.com/)
2. 进入「开发」>「开发管理」>「开发设置」
3. 配置「服务器域名」：
   - request 合法域名：`https://your-domain.com`

## 🚀 部署

### H5 Web 部署

```bash
# 1. 构建生产版本
npm run build:h5:prod

# 2. 上传 dist/build/h5/ 目录到 Web 服务器
# 可以使用 nginx、Apache 或其他 Web 服务器
```

### 微信小程序部署

```bash
# 1. 构建生产版本
npm run build:mp-weixin

# 2. 使用微信开发者工具导入 dist/build/mp-weixin/
# 3. 上传代码并提交审核
```

## 📋 检查清单

### H5 发布前检查

- [ ] 测试 Chrome、Safari、Firefox 等主流浏览器
- [ ] 测试桌面和移动端响应式布局
- [ ] 确认跨域问题已解决（生产环境）
- [ ] 检查 SEO 基础配置
- [ ] 配置 HTTPS（生产环境）

### 小程序发布前检查

- [ ] 测试微信开发者工具模拟器
- [ ] 测试真机（iOS 和 Android）
- [ ] 确认 AppID 和域名配置正确
- [ ] 检查微信登录等原生功能
- [ ] 提交代码审核

## 🐛 常见问题

### H5 跨域问题

**开发环境**: 已配置代理，不会出现跨域问题

**生产环境**: 
- 后端配置 CORS
- 使用 nginx 反向代理

### 小程序域名校验

关闭 URL 校验（仅开发环境）：
```json
// manifest.json
"mp-weixin": {
  "setting": {
    "urlCheck": false
  }
}
```

**注意**: 生产环境必须开启 URL 校验并配置合法域名。

## 📞 技术支持

遇到问题时，请检查：

1. uni-app 官方文档：https://uniapp.dcloud.net.cn/
2. 平台特定 API 文档
3. 项目代码注释和类型定义
