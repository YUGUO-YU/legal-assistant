# Web 版和小程序版适配 - 快速参考

## 🚀 快速开始

### 开发模式

```bash
cd /workspace/client

# H5 Web 开发（带热重载）
npm run dev:h5

# 微信小程序开发（带热重载）
npm run dev:mp-weixin
```

### 生产构建

```bash
cd /workspace/client

# H5 Web 生产构建
npm run build:h5

# H5 Web 优化构建（推荐）
npm run build:h5:prod

# 微信小程序生产构建
npm run build:mp-weixin
```

### 使用构建脚本

```bash
cd /workspace
chmod +x build.sh
./build.sh
```

## 📁 输出目录

| 平台 | 目录 | 说明 |
|------|------|------|
| H5 | `dist/build/h5/` | Web 版本 |
| 小程序 | `dist/build/mp-weixin/` | 小程序版本 |

## 🎯 平台功能对比

| 功能 | H5 Web | 微信小程序 |
|------|--------|-----------|
| 邮箱登录 | ✅ | ❌ |
| 微信登录 | ❌ | ✅ |
| 手机验证码登录 | ✅ | ✅ |
| 响应式布局 | ✅ | ✅ |
| TAB 导航 | ✅ | ✅ |
| 页面路由 | ✅ | ✅ |

## 🌐 H5 部署

### 开发环境
```bash
# 启动本地开发服务器
npm run dev:h5

# 访问 http://localhost:5173
```

### 生产部署

```bash
# 1. 构建
npm run build:h5:prod

# 2. Nginx 配置示例
server {
    listen 80;
    server_name legal-assistant.example.com;
    root /var/www/legal-assistant;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# 3. 重启 Nginx
sudo systemctl restart nginx
```

## 📱 小程序部署

### 开发环境
```bash
# 1. 构建开发版
npm run build:mp-weixin

# 2. 配置 manifest.json
# 设置微信小程序 AppID

# 3. 导入微信开发者工具
# 文件 > 导入 > 选择 dist/build/mp-weixin/
```

### 生产部署

```bash
# 1. 构建生产版
npm run build:mp-weixin

# 2. 微信开发者工具
# - 导入 dist/build/mp-weixin/
# - 点击「上传」
# - 填写版本号和备注

# 3. 微信公众平台
# - 登录 https://mp.weixin.qq.com/
# - 版本管理 > 提交审核
```

## 🔧 配置文件

### manifest.json 关键配置

```json
{
  "mp-weixin": {
    "appid": "你的小程序AppID",
    "setting": {
      "urlCheck": true,
      "es6": true,
      "minified": true,
      "postcss": true
    }
  },
  "h5": {
    "title": "法律助手",
    "router": {
      "mode": "hash",
      "base": "/"
    },
    "devServer": {
      "proxy": {
        "/api": {
          "target": "http://localhost:8080",
          "changeOrigin": true
        }
      }
    }
  }
}
```

## 🎨 条件编译

### TypeScript/JavaScript

```typescript
// 仅微信小程序运行
// #ifdef MP-WEIXIN
uni.login({ provider: 'weixin' })
// #endif

// 仅 H5 运行
// #ifdef H5
console.log('H5')
// #endif

// 非 H5 平台运行
// #ifndef H5
console.log('Not H5')
// #endif
```

### CSS/SCSS

```scss
/* H5 特定样式 */
/* #ifdef H5 */
.container {
  max-width: 750px;
  margin: 0 auto;
}
/* #endif */

/* 小程序特定样式 */
/* #ifdef MP-WEIXIN */
.container {
  padding: 20rpx;
}
/* #endif */
```

## 🐛 常见问题

### H5 跨域问题

**开发环境**: 已配置代理，无需处理

**生产环境**:
```javascript
// 方案 1: 后端配置 CORS
response.setHeader('Access-Control-Allow-Origin', '*');

// 方案 2: Nginx 反向代理
location /api {
    proxy_pass http://backend:8080;
}
```

### 小程序域名配置

```
微信公众平台 > 开发 > 开发设置 > 服务器域名

request 合法域名: https://your-domain.com
```

### 构建失败

```bash
# 清理缓存
rm -rf node_modules
npm install

# 重新构建
npm run build:h5
```

## 📞 资源链接

- uni-app 官方：https://uniapp.dcloud.net.cn/
- 微信小程序：https://developers.weixin.qq.com/miniprogram/
- 平台指南：`client/PLATFORM_GUIDE.md`
- 适配总结：`client/ADAPTATION_SUMMARY.md`
