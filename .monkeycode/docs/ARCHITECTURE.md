# 法律助手 (LegalAssistant) 系统架构文档

## 1. 项目概述

### 1.1 项目简介

法律助手是一款面向律师和法律从业者的效率工具小程序，提供法律文书起草、案例查询、法规查询、企业调查、案源发现等一站式服务。

### 1.2 项目信息

| 属性 | 值 |
|-----|-----|
| 项目名称 | 法律助手 (LegalAssistant) |
| 项目类型 | 微信小程序 + Web 应用双端 |
| 技术架构 | 前后端分离 |
| 目标用户 | 执业律师、法律顾问、法务人员 |
| 开发阶段 | MVP (最小可行产品) |

---

## 2. 系统架构

### 2.1 整体架构

```mermaid
graph TB
    subgraph 客户端层
        WX["微信小程序"]
        WEB["Web 应用 H5"]
    end

    subgraph 网关层
        NGINX["Nginx 反向代理"]
        APIGW["API 网关"]
    end

    subgraph 服务层
        API["REST API 服务"]
        JOB["定时任务服务"]
    end

    subgraph 数据层
        MYSQL["MySQL 8.0"]
        REDIS["Redis 7.0"]
        OSS["对象存储 COS"]
    end

    subgraph 第三方服务
        SMS["腾讯云短信"]
        WX["微信 API"]
        THIRD["第三方数据 API"]
    end

    WX --> NGINX
    WEB --> NGINX
    NGINX --> API
    API --> MYSQL
    API --> REDIS
    API --> OSS
    API --> SMS
    API --> WX
    API --> THIRD
    JOB --> MYSQL
    JOB --> THIRD
```

### 2.2 技术栈概览

#### 前端技术栈

| 类别 | 技术选型 | 说明 |
|-----|---------|------|
| 跨端框架 | uni-app | 一套代码编译为微信小程序 + H5 |
| Web 框架 | Vue 3 + Vite | 现代响应式前端框架 |
| 状态管理 | Pinia | Vue 3 官方推荐状态管理库 |
| UI 组件 | uView (小程序) / Element Plus (Web) | 成熟 UI 组件库 |
| HTTP 库 | Axios | HTTP 请求封装 |
| 路由 | uni-app 路由 / Vue Router | 路由管理 |

#### 后端技术栈

| 类别 | 技术选型 | 说明 |
|-----|---------|------|
| 运行时 | Node.js 18+ | JavaScript 运行时环境 |
| 框架 | NestJS | 企业级 Node.js 框架 |
| ORM | Prisma | 现代数据库 ORM |
| 数据库 | MySQL 8.0 | 关系型数据库 |
| 缓存 | Redis 7.0 | 高性能缓存 |
| 文件存储 | 腾讯云 COS | 对象存储服务 |
| 认证 | JWT | 无状态身份令牌 |

---

## 3. 模块架构

### 3.1 后端模块划分

```mermaid
graph TB
    subgraph API 服务
        AUTH["认证模块 auth"]
        USER["用户模块 user"]
        DOC["文书模块 document"]
        CASE["案例模块 case"]
        LAW["法规模块 law"]
        BIZ["企业模块 company"]
        LEAD["案源模块 lead"]
        ADMIN["管理模块 admin"]
    end

    subgraph 公共模块
        DB["数据库连接 database"]
        CACHE["缓存服务 cache"]
        VALIDATE["参数验证 validate"]
        LOGGER["日志服务 logger"]
        EXCEPTION["异常处理 exception"]
    end

    subgraph 基础设施
        OSS["文件存储 oss"]
        SMS["短信服务 sms"]
        THIRD["第三方 API third"]
    end

    AUTH --> DB
    USER --> DB
    DOC --> DB
    CASE --> DB
    LAW --> DB
    BIZ --> DB
    LEAD --> DB
    ADMIN --> DB

    AUTH --> CACHE
    USER --> CACHE
    CASE --> CACHE
    LAW --> CACHE

    AUTH --> VALIDATE
    USER --> VALIDATE
    DOC --> VALIDATE

    AUTH --> EXCEPTION
    USER --> EXCEPTION

    DOC --> OSS
    USER --> SMS
    CASE --> THIRD
    LAW --> THIRD
    BIZ --> THIRD
```

### 3.2 前端模块划分

```mermaid
graph LR
    subgraph 小程序端 uni-app
        WX_AUTH["认证页面"]
        WX_HOME["首页"]
        WX_DOC["文书模块"]
        WX_CASE["案例模块"]
        WX_LAW["法规模块"]
        WX_BIZ["企业模块"]
        WX_LEAD["案源模块"]
        WX_MINE["我的"]
    end

    subgraph Web 端 Vue3
        WEB_AUTH["认证页面"]
        WEB_HOME["工作台"]
        WEB_DOC["文书管理"]
        WEB_CASE["案例库"]
        WEB_LAW["法规库"]
        WEB_BIZ["企业查询"]
        WEB_LEAD["案源管理"]
        WEB_ADMIN["管理后台"]
    end

    subgraph 公共模块
        STORE["Pinia Store"]
        API["API Service"]
        UTIL["工具函数"]
    end
```

---

## 4. 数据架构

### 4.1 数据库架构

```mermaid
graph TB
    subgraph 应用库 legal_assistant
        USER_TBL["user 用户表"]
        TEAM_TBL["team 团队表"]
        TEAM_MEMBER_TBL["team_member 团队成员表"]
        DOC_TBL["document 文书表"]
        DOC_VER_TBL["document_version 文书版本表"]
        CASE_TBL["case 案件表"]
        CASE_BM_TBL["case_bookmark 案例收藏表"]
        LEAD_TBL["lead 案源表"]
    end

    subgraph 缓存 Redis
        SESSION["session 会话"]
        TOKEN["token 黑名单"]
        CACHE_CASE["case_search 案例缓存"]
        CACHE_LAW["law_search 法规缓存"]
        CACHE_COMPANY["company_search 企业缓存"]
    end

    subgraph 文件存储 COS
        DOC_FILE["文书附件"]
        USER_AVATAR["用户头像"]
        EXPORT_FILE["导出文件"]
    end
```

### 4.2 数据流设计

```mermaid
sequenceDiagram
    participant Client as 客户端
    participant Gateway as API 网关
    participant Cache as Redis
    participant DB as MySQL
    participant Third as 第三方 API

    Client->>Gateway: 请求
    Gateway->>Gateway: 认证校验
    Gateway->>Cache: 检查缓存
    Cache-->>Gateway: 缓存命中
    Gateway-->>Client: 返回缓存数据

    Note over Client,Third: 缓存未命中场景
    Client->>Gateway: 请求
    Gateway->>Cache: 检查缓存
    Cache-->>Gateway: 缓存未命中
    Gateway->>Third: 调用第三方 API
    Third-->>Gateway: 返回数据
    Gateway->>DB: 存储/更新数据
    Gateway->>Cache: 写入缓存
    Gateway-->>Client: 返回数据
```

---

## 5. 安全架构

### 5.1 认证流程

```mermaid
sequenceDiagram
    participant User as 用户
    participant Client as 客户端
    participant API as API 服务
    participant Redis as Redis

    User->>Client: 输入手机号
    Client->>API: POST /auth/sms/send
    API->>API: 生成验证码
    API->>Redis: 存储验证码 TTL=5min
    API-->>Client: 发送短信
    Client->>API: POST /auth/phone/login
    API->>Redis: 验证验证码
    Redis-->>API: 验证通过
    API->>API: 生成 JWT Token
    API-->>Client: 返回 Token
    Client->>API: 请求业务接口 + Token
    API->>API: 验证 Token
    API-->>Client: 返回业务数据
```

### 5.2 权限控制

| 角色 | 权限说明 |
|-----|---------|
| admin | 系统管理，拥有所有权限 |
| lawyer | 律师，可管理自己的文书、案例、案源 |
| assistant | 助理，可协助律师处理事务 |
| guest | 访客，仅可浏览公开信息 |

---

## 6. 部署架构

### 6.1 物理拓扑

```mermaid
graph TB
    subgraph 用户侧
        WX["微信"]
        BROWSER["浏览器"]
    end

    subgraph 云服务 AWS/腾讯云
        subgraph 接入层
            CDN["CDN"]
            LB["负载均衡 SLB"]
        end

        subgraph 计算层
            API1["API 实例 1"]
            API2["API 实例 2"]
            JOB["定时任务实例"]
        end

        subgraph 存储层
            RDS["MySQL 主从"]
            REDIS["Redis 主从"]
            COS["对象存储"]
        end
    end

    WX --> CDN
    BROWSER --> CDN
    CDN --> LB
    LB --> API1
    LB --> API2
    API1 --> RDS
    API2 --> RDS
    API1 --> REDIS
    API2 --> REDIS
    API1 --> COS
    API2 --> COS
```

### 6.2 容器架构

```mermaid
graph LR
    subgraph Docker Compose
        NGINX["nginx"]
        API["api"]
        JOB["job"]
        MYSQL["mysql"]
        REDIS["redis"]
    end

    subgraph 网络
        BACKEND["backend network"]
    end

    NGINX --> API
    API --> MYSQL
    API --> REDIS
    JOB --> MYSQL
    JOB --> REDIS
```

---

## 7. 监控与运维

### 7.1 监控指标

| 类别 | 指标 | 告警阈值 |
|-----|------|---------|
| 服务可用性 | API 成功率 | < 99% 告警 |
| 响应时间 | P95 延迟 | > 2s 告警 |
| 系统负载 | CPU 使用率 | > 80% 告警 |
| 存储 | 磁盘使用率 | > 85% 告警 |
| 业务 | 登录失败率 | > 10% 告警 |

### 7.2 日志规范

- 格式：JSON
- 级别：ERROR、WARN、INFO、DEBUG
- 保留：30 天
- 采集：ELK / 腾讯云日志服务

---

## 8. 扩展性设计

### 8.1 水平扩展

- API 服务无状态，可根据负载增加实例
- 使用负载均衡分发请求
- Redis 集群支持数据分片

### 8.2 垂直扩展

- 数据库读写分离
- 缓存预热和持久化
- 静态资源 CDN 加速

### 8.3 模块化扩展

- 微服务架构预留
- 消息队列解耦（后期引入）
- 插件机制扩展功能