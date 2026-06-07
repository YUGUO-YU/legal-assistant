# 前端安装启动步骤（Windows）

## 前置要求

确保已安装：
- Node.js 18+ (https://nodejs.org/)
- npm 9+ (随 Node.js 安装)

验证安装：
```bash
node -v
npm -v
```

---

## 方式一：npm 命令启动（推荐）

### 1. 进入前端目录

```bash
cd D:\Projects\legal-assistant\client
```

### 2. 安装依赖

```bash
npm install
```

**说明**：首次安装需要 3-5 分钟，会下载项目所需的所有依赖包。

### 3. 启动开发服务器

```bash
npm run dev
```

### 4. 等待启动完成

看到以下信息表示启动成功：
```
VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: http://xxx.xxx.x.x:5173/
```

### 5. 访问

打开浏览器访问：`http://localhost:5173`

---

## 方式二：使用 Yarn（可选）

### 1. 安装 Yarn

```bash
npm install -g yarn
```

### 2. 使用 Yarn 安装依赖

```bash
yarn install
```

### 3. 启动

```bash
yarn dev
```

---

## 方式三：使用 pnpm（推荐更快的安装）

### 1. 安装 pnpm

```bash
npm install -g pnpm
```

### 2. 使用 pnpm 安装依赖

```bash
pnpm install
```

### 3. 启动

```bash
pnpm dev
```

---

## 常见问题

### 问题 1：npm install 很慢

**解决**：使用淘宝镜像

```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### 问题 2：安装失败，缺少依赖

```bash
# 清除缓存
npm cache clean --force

# 删除 node_modules
rmdir /s /q node_modules

# 重新安装
npm install
```

### 问题 3：端口 5173 被占用

```bash
# 查看占用端口的进程
netstat -ano | findstr :5173

# 关闭进程
taskkill /PID <进程ID> /F
```

### 问题 4：编译错误

```bash
# 检查 node 版本
node -v  # 需要 18+

# 重新安装依赖
rmdir /s /q node_modules package-lock.json
npm install
```

---

## 启动命令汇总

```bash
# 1. 进入目录
cd D:\Projects\legal-assistant\client

# 2. 安装依赖（首次）
npm install

# 3. 启动开发服务器
npm run dev

# 4. 访问
# 浏览器打开 http://localhost:5173
```

---

## 一键启动脚本

保存为 `start-frontend.bat`：

```batch
@echo off
echo ========================================
echo   法律助手前端启动
echo ========================================

echo.
echo [1] 进入前端目录...
cd /d D:\Projects\legal-assistant\client

echo.
echo [2] 检查 Node.js...
node -v

echo.
echo [3] 安装依赖（首次需要）...
call npm install

echo.
echo [4] 启动开发服务器...
call npm run dev

echo.
echo ========================================
echo   访问地址: http://localhost:5173
echo ========================================
pause
```

---

## 目录结构

```
client/
├── node_modules/          # 依赖包（安装后生成）
├── public/                # 静态资源
├── src/                   # 源代码
│   ├── pages/             # 页面组件
│   ├── components/        # 公共组件
│   ├── services/          # API 服务
│   ├── stores/            # 状态管理
│   └── style/             # 样式文件
├── package.json           # 项目配置
├── vite.config.js         # Vite 配置
└── index.html             # 入口 HTML
```

---

## package.json 常用命令

| 命令 | 说明 |
|------|------|
| `npm run dev` | 启动开发服务器 |
| `npm run build` | 打包生产版本 |
| `npm run preview` | 预览打包结果 |
| `npm install` | 安装依赖 |
| `npm update` | 更新依赖 |

---

## 访问地址

- **开发服务器**: http://localhost:5173
- **后端 API**: http://localhost:8080
- **API 文档**: http://localhost:8080/swagger-ui.html

---

**启动成功！访问 http://localhost:5173**
