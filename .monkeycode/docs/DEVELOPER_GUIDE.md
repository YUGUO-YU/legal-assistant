# 法律助手小程序开发指南

## 1. 环境准备

### 1.1 开发环境要求

| 工具 | 版本要求 | 说明 |
|-----|---------|------|
| Node.js | 18.0+ | 后端运行时 |
| npm / pnpm | 最新稳定版 | 包管理器 |
| Git | 2.0+ | 版本控制 |
| Docker | 24.0+ | 容器化环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.0+ | 缓存 |
| VS Code | 最新版 | 推荐 IDE |

### 1.2 开发工具安装

```bash
# 安装 Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 安装 pnpm
npm install -g pnpm

# 安装 Docker
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 1.3 IDE 插件推荐

#### VS Code 插件

- ESLint - 代码检查
- Prettier - 代码格式化
- TypeScript Vue Plugin - Vue 3 支持
- Prisma - 数据库 ORM 支持
- Thunder Client - API 测试

---

## 2. 项目初始化

### 2.1 克隆项目

```bash
git clone https://github.com/your-org/legal-assistant.git
cd legal-assistant
```

### 2.2 环境变量配置

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑环境变量
vim .env
```

#### 环境变量说明

```bash
# 应用配置
NODE_ENV=development
PORT=3000

# 数据库配置
DATABASE_URL="mysql://user:password@localhost:3306/legal_assistant"

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT 配置
JWT_SECRET=your-secret-key
JWT_EXPIRES_IN=7d
JWT_REFRESH_EXPIRES_IN=30d

# 微信小程序配置
WX_APP_ID=your-app-id
WX_APP_SECRET=your-app-secret

# 短信服务配置
SMS_SECRET_ID=your-secret-id
SMS_SECRET_KEY=your-secret-key
SMS_APP_ID=your-app-id
SMS_TEMPLATE_LOGIN=your-template-id

# 对象存储配置
COS_SECRET_ID=your-secret-id
COS_SECRET_KEY=your-secret-key
COS_BUCKET=your-bucket
COS_REGION=ap-guangzhou

# 第三方 API 配置
THIRD_PARTY_API_KEY=your-api-key
```

### 2.3 启动基础设施

```bash
# 启动 MySQL 和 Redis
docker-compose up -d mysql redis

# 等待服务启动
sleep 10

# 初始化数据库
cd server
pnpm prisma migrate dev
pnpm prisma generate
```

---

## 3. 后端开发

### 3.1 项目结构

```
server/
├── src/
│   ├── modules/                 # 功能模块
│   │   ├── auth/               # 认证模块
│   │   │   ├── auth.controller.ts
│   │   │   ├── auth.module.ts
│   │   │   ├── auth.service.ts
│   │   │   └── dto/            # 数据传输对象
│   │   │       ├── login.dto.ts
│   │   │       └── register.dto.ts
│   │   ├── user/               # 用户模块
│   │   ├── document/           # 文书模块
│   │   ├── case/               # 案例模块
│   │   ├── law/                # 法规模块
│   │   ├── company/            # 企业模块
│   │   └── lead/               # 案源模块
│   ├── common/                 # 公共模块
│   │   ├── decorators/         # 装饰器
│   │   ├── filters/            # 异常过滤器
│   │   ├── guards/             # 路由守卫
│   │   ├── interceptors/      # 拦截器
│   │   ├── middleware/         # 中间件
│   │   └── utils/              # 工具函数
│   ├── config/                 # 配置文件
│   │   └── configuration.ts
│   ├── database/               # 数据库
│   │   └── prisma/
│   │       └── schema.prisma
│   ├── dto/                    # 全局 DTO
│   ├── entities/               # 实体
│   ├── services/              # 全局服务
│   ├── types/                  # 类型定义
│   ├── app.module.ts          # 根模块
│   └── main.ts                # 入口文件
├── test/                      # 测试
├── prisma/                    # Prisma 配置
├── package.json
├── tsconfig.json
└── nest-cli.json
```

### 3.2 创建新模块

#### 3.2.1 创建模块骨架

```bash
# 创建模块目录
mkdir -p src/modules/your-module/{dto,entities}

# 创建模块文件
touch src/modules/your-module/your-module.controller.ts
touch src/modules/your-module/your-module.module.ts
touch src/modules/your-module/your-module.service.ts
```

#### 3.2.2 模块代码示例

```typescript
// your-module.service.ts
import { Injectable } from '@nestjs/common';
import { PrismaService } from '@/common/services/prisma.service';

@Injectable()
export class YourModuleService {
  constructor(private readonly prisma: PrismaService) {}

  async findAll() {
    return this.prisma.yourModel.findMany();
  }
}
```

```typescript
// your-module.controller.ts
import { Controller, Get } from '@nestjs/common';
import { YourModuleService } from './your-module.service';
import { Public } from '@/common/decorators/public.decorator';

@Controller('your-module')
export class YourModuleController {
  constructor(private readonly yourModuleService: YourModuleService) {}

  @Get()
  async findAll() {
    return this.yourModuleService.findAll();
  }
}
```

```typescript
// your-module.module.ts
import { Module } from '@nestjs/common';
import { YourModuleService } from './your-module.service';
import { YourModuleController } from './your-module.controller';
import { PrismaService } from '@/common/services/prisma.service';

@Module({
  controllers: [YourModuleController],
  providers: [YourModuleService, PrismaService],
})
export class YourModuleModule {}
```

### 3.3 数据库操作

#### 3.3.1 定义数据模型

```prisma
// prisma/schema.prisma
model YourModel {
  id        String   @id @default(uuid())
  name      String
  email     String   @unique
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt

  @@map("your_model")
}
```

#### 3.3.2 数据库迁移

```bash
# 创建迁移
pnpm prisma migrate dev --name add_your_model

# 应用迁移
pnpm prisma migrate deploy

# 重置数据库（慎用）
pnpm prisma migrate reset
```

### 3.4 API 开发规范

#### 3.4.1 路由命名

| 操作 | HTTP 方法 | URL 命名 |
|-----|----------|---------|
| 列表 | GET | /resources |
| 详情 | GET | /resources/:id |
| 创建 | POST | /resources |
| 更新 | PUT | /resources/:id |
| 删除 | DELETE | /resources/:id |
| 批量操作 | POST | /resources/batch |

#### 3.4.2 响应格式

```typescript
// 成功响应
{
  "code": 0,
  "message": "success",
  "data": {}
}

// 错误响应
{
  "code": 错误码,
  "message": "错误信息",
  "data": null
}
```

#### 3.4.3 错误码规范

| 错误码 | 说明 |
|-------|------|
| 0 | 成功 |
| 1000 | 系统错误 |
| 2000 | 认证错误 |
| 3000 | 权限错误 |
| 4000 | 参数错误 |
| 5000 | 业务逻辑错误 |

### 3.5 运行测试

```bash
# 运行所有测试
pnpm test

# 运行单元测试
pnpm test:unit

# 运行 e2e 测试
pnpm test:e2e

# 查看测试覆盖率
pnpm test:cov
```

---

## 4. 前端开发

### 4.1 项目结构

```
client/
├── src/
│   ├── pages/                 # 页面
│   │   ├── index/            # 首页
│   │   ├── auth/             # 认证相关
│   │   ├── document/         # 文书模块
│   │   ├── case/             # 案例模块
│   │   ├── law/              # 法规模块
│   │   ├── company/          # 企业模块
│   │   ├── lead/             # 案源模块
│   │   └── user/             # 用户中心
│   ├── components/          # 组件
│   │   ├── common/          # 通用组件
│   │   ├── document/        # 文书组件
│   │   ├── case/            # 案例组件
│   │   └── ...
│   ├── stores/              # Pinia 状态
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   └── ...
│   ├── services/           # API 服务
│   │   ├── api.ts          # API 封装
│   │   ├── auth.ts
│   │   ├── document.ts
│   │   └── ...
│   ├── utils/              # 工具函数
│   ├── static/             # 静态资源
│   ├── App.vue
│   ├── main.ts
│   ├── pages.json          # 页面路由配置
│   └── manifest.json      # 小程序配置
├── public/                # 公共资源
├── package.json
├── vite.config.ts
└── tsconfig.json
```

### 4.2 创建新页面

#### 4.2.1 页面模板

```vue
<!-- pages/your-page/index.vue -->
<template>
  <view class="container">
    <page-header title="页面标题" />
    <view class="content">
      <!-- 页面内容 -->
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onLoad } from '@utils/hooks'
import { useYourStore } from '@/stores/your'

const store = useYourStore()
const loading = ref(false)

onLoad(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    await store.fetchData()
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 24rpx;
}
</style>
```

### 4.3 组件开发规范

#### 4.3.1 组件模板

```vue
<!-- components/common/your-component/index.vue -->
<template>
  <view class="your-component">
    <slot name="header" />
    <view class="content">
      <slot />
    </view>
    <slot name="footer" />
  </view>
</template>

<script setup lang="ts">
interface Props {
  title?: string
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  disabled: false,
})

const emit = defineEmits<{
  (e: 'update'): void
  (e: 'click', data: any): void
}>()

function handleClick() {
  if (!props.disabled) {
    emit('click', data)
  }
}
</script>

<style lang="scss" scoped>
.your-component {
  // styles
}
</style>
```

### 4.4 状态管理

```typescript
// stores/auth.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  async function login(phone: string, code: string) {
    const res = await api.auth.login({ phone, code })
    token.value = res.token
    userInfo.value = res.user
  }

  function logout() {
    token.value = ''
    userInfo.value = null
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    logout,
  }
})
```

### 4.5 API 调用

```typescript
// services/document.ts
import { api } from './api'
import type { Document, DocumentListParams } from '@/types'

export const documentService = {
  async getList(params: DocumentListParams) {
    return api.get<{ list: Document[]; total: number }>('/documents', params)
  },

  async getById(id: string) {
    return api.get<Document>(`/documents/${id}`)
  },

  async create(data: Partial<Document>) {
    return api.post<Document>('/documents', data)
  },

  async update(id: string, data: Partial<Document>) {
    return api.put<Document>(`/documents/${id}`, data)
  },

  async delete(id: string) {
    return api.delete<void>(`/documents/${id}`)
  },
}
```

### 4.6 运行和调试

```bash
# 安装依赖
pnpm install

# 开发模式（小程序）
pnpm dev:mp-weixin

# 开发模式（H5）
pnpm dev:h5

# 构建
pnpm build:mp-weixin
pnpm build:h5
```

---

## 5. 代码规范

### 5.1 Git 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档变更
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具
```

### 5.2 TypeScript 规范

- 启用严格模式
- 使用 interface 定义对象类型
- 使用 type 定义联合类型、别名
- 避免使用 any，使用 unknown 代替
- 使用可选链和空值合并

### 5.3 CSS 规范

- 使用 SCSS
- 采用 BEM 命名规范
- 移动端使用 rpx 单位
- 避免行内样式

---

## 6. 测试

### 6.1 单元测试

```typescript
// auth.service.spec.ts
describe('AuthService', () => {
  let service: AuthService

  beforeEach(async () => {
    const module = await Test.createTestingModule({
      providers: [AuthService, PrismaService],
    }).compile()

    service = module.get<AuthService>(AuthService)
  })

  it('should be defined', () => {
    expect(service).toBeDefined()
  })

  describe('login', () => {
    it('should return tokens when credentials are valid', async () => {
      const result = await service.login('13800138000', '123456')
      expect(result).toHaveProperty('token')
      expect(result).toHaveProperty('refreshToken')
    })
  })
})
```

### 6.2 E2E 测试

```typescript
// auth.e2e-spec.ts
describe('Auth (e2e)', () => {
  let app: INestApplication

  beforeAll(async () => {
    const moduleFixture = await Test.createTestingModule({
      imports: [AppModule],
    }).compile()

    app = moduleFixture.createNestApplication()
    await app.init()
  })

  afterAll(async () => {
    await app.close()
  })

  it('/api/v1/auth/phone/login (POST)', () => {
    return request(app.getHttpServer())
      .post('/api/v1/auth/phone/login')
      .send({ phone: '13800138000', code: '123456' })
      .expect(200)
  })
})
```

---

## 7. 部署

### 7.1 生产环境构建

```bash
# 后端构建
cd server
pnpm build

# 前端构建
cd ../client
pnpm build:mp-weixin
pnpm build:h5
```

### 7.2 Docker 部署

```bash
# 构建镜像
docker build -t legal-assistant-api -f docker/Dockerfile.api .
docker build -t legal-assistant-client -f docker/Dockerfile.client .

# 运行容器
docker-compose up -d
```

### 7.3 小程序发布

1. 登录微信公众平台
2. 上传代码包
3. 提交审核
4. 发布上线

---

## 8. 常见问题

### 8.1 数据库连接失败

```bash
# 检查 MySQL 是否运行
docker ps | grep mysql

# 检查端口
netstat -an | grep 3306

# 检查连接
mysql -h localhost -P 3306 -u user -p
```

### 8.2 第三方 API 调用失败

1. 检查 API Key 是否配置正确
2. 检查网络连通性
3. 查看日志确认错误信息
4. 联系第三方技术支持

### 8.3 小程序无法登录

1. 检查微信 AppID 配置
2. 检查是否有域名备案
3. 检查 HTTPS 证书
4. 确认接口域名已加入白名单