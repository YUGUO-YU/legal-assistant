# 法律助手 API 接口文档

## 1. 接口概述

### 1.1 基本信息

| 属性 | 值 |
|-----|-----|
| 基础路径 | `/api/v1` |
| 认证方式 | JWT Bearer Token |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |

### 1.2 全局响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

### 1.3 错误响应格式

```json
{
  "code": 4000,
  "message": "参数错误：手机号格式不正确",
  "data": null
}
```

### 1.4 错误码定义

| 错误码 | 说明 |
|-------|------|
| 0 | 成功 |
| 1000 | 系统内部错误 |
| 2000 | 认证错误（Token 无效/过期） |
| 3000 | 权限不足 |
| 4000 | 参数校验失败 |
| 5000 | 业务逻辑错误 |
| 6000 | 资源不存在 |

---

## 2. 认证接口

### 2.1 手机号登录

```
POST /api/v1/auth/phone/login
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| phone | string | 是 | 手机号 |
| code | string | 是 | 短信验证码 |

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 604800,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "phone": "13800138000",
      "nickname": "张三律师",
      "avatar": "https://example.com/avatar.jpg",
      "role": "lawyer"
    }
  }
}
```

### 2.2 发送短信验证码

```
POST /api/v1/auth/sms/send
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| phone | string | 是 | 手机号 |
| type | string | 否 | 验证码类型：login/register/reset（默认 login） |

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "expiresIn": 300
  }
}
```

### 2.3 微信登录

```
POST /api/v1/auth/wechat/login
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| code | string | 是 | 微信授权码 |

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "nickname": "张三",
      "avatar": "https://example.com/avatar.jpg",
      "role": "lawyer"
    }
  }
}
```

### 2.4 邮箱登录

```
POST /api/v1/auth/email/login
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| email | string | 是 | 邮箱地址 |
| password | string | 是 | 密码 |

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "zhangsan@example.com",
      "nickname": "张三律师",
      "role": "lawyer"
    }
  }
}
```

### 2.5 用户注册

```
POST /api/v1/auth/register
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| phone | string | 是 | 手机号 |
| code | string | 是 | 短信验证码 |
| password | string | 是 | 密码（8-20位） |
| nickname | string | 否 | 昵称 |

### 2.6 刷新 Token

```
POST /api/v1/auth/refresh
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| refreshToken | string | 是 | 刷新令牌 |

### 2.7 登出

```
POST /api/v1/auth/logout
```

---

## 3. 用户接口

### 3.1 获取用户信息

```
GET /api/v1/user/profile
```

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "nickname": "张三律师",
    "avatar": "https://example.com/avatar.jpg",
    "role": "lawyer",
    "team": {
      "id": "team-uuid",
      "name": "XX 律师事务所",
      "role": "owner"
    },
    "createdAt": "2026-01-01T00:00:00Z"
  }
}
```

### 3.2 更新用户信息

```
PUT /api/v1/user/profile
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| nickname | string | 否 | 昵称 |
| avatar | string | 否 | 头像 URL |
| email | string | 否 | 邮箱 |

### 3.3 修改密码

```
PUT /api/v1/user/password
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| oldPassword | string | 是 | 旧密码 |
| newPassword | string | 是 | 新密码 |

---

## 4. 文书接口

### 4.1 获取文书列表

```
GET /api/v1/documents
```

**查询参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | number | 否 | 页码（默认 1） |
| pageSize | number | 否 | 每页数量（默认 20） |
| type | string | 否 | 文书类型：contract/litigation/non_litigation |
| status | string | 否 | 状态：draft/final/archived |
| keyword | string | 否 | 关键词搜索 |
| caseId | string | 否 | 关联案件 ID |

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "doc-uuid",
        "title": "房屋租赁合同",
        "type": "contract",
        "status": "draft",
        "caseId": "case-uuid",
        "createdAt": "2026-01-01T00:00:00Z",
        "updatedAt": "2026-01-02T00:00:00Z"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

### 4.2 获取文书详情

```
GET /api/v1/documents/:id
```

### 4.3 创建文书

```
POST /api/v1/documents
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| title | string | 是 | 文书标题 |
| type | string | 是 | 文书类型 |
| content | string | 否 | 文书内容 |
| caseId | string | 否 | 关联案件 ID |
| templateId | string | 否 | 模板 ID |

### 4.4 更新文书

```
PUT /api/v1/documents/:id
```

### 4.5 删除文书

```
DELETE /api/v1/documents/:id
```

### 4.6 获取文书版本历史

```
GET /api/v1/documents/:id/versions
```

### 4.7 导出文书

```
POST /api/v1/documents/:id/export
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| format | string | 是 | 导出格式：pdf/word |

**响应**

返回文件流，Content-Type 为 `application/pdf` 或 `application/vnd.openxmlformats-officedocument.wordprocessingml.document`

---

## 5. 案例接口

### 5.1 搜索案例

```
GET /api/v1/cases/search
```

**查询参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| keyword | string | 否 | 关键词 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |
| caseType | string | 否 | 案件类型 |
| courtLevel | string | 否 | 法院层级 |
| region | string | 否 | 地域 |
| dateRange | string | 否 | 日期范围：last_week/last_month/last_year |

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "case-external-id",
        "caseNumber": "(2025)京01民终123号",
        "title": "原告张三与被告李四合同纠纷案",
        "court": "北京市第一中级人民法院",
        "caseType": "民事",
        "procedure": "二审",
        "judgmentDate": "2025-06-01",
        "plaintiff": "张三",
        "defendant": "李四"
      }
    ],
    "total": 1000,
    "page": 1,
    "pageSize": 20
  }
}
```

### 5.2 获取案例详情

```
GET /api/v1/cases/:id
```

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "case-external-id",
    "caseNumber": "(2025)京01民终123号",
    "title": "原告张三与被告李四合同纠纷案",
    "court": "北京市第一中级人民法院",
    "caseType": "民事",
    "procedure": "二审",
    "judgmentDate": "2025-06-01",
    "plaintiff": "张三",
    "defendant": "李四",
    "judge": "王法官",
    "lawyer": "赵律师",
    "content": "裁判文书全文...",
    "relatedLaws": ["《中华人民共和国民法典》第三编合同"],
    "similarCases": []
  }
}
```

### 5.3 获取相似案例

```
GET /api/v1/cases/:id/similar
```

### 5.4 收藏案例

```
POST /api/v1/cases/bookmark
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| externalId | string | 是 | 案例外部 ID |
| source | string | 是 | 数据来源 |
| title | string | 否 | 案例标题 |
| note | string | 否 | 备注 |

### 5.5 获取收藏列表

```
GET /api/v1/cases/bookmarks
```

---

## 6. 法规接口

### 6.1 搜索法规

```
GET /api/v1/laws/search
```

**查询参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| keyword | string | 否 | 关键词 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |
| level | string | 否 | 法规层级：law/regulation/local |
| organ | string | 否 | 发布机关 |
| dateRange | string | 否 | 发布日期范围 |

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "law-external-id",
        "title": "《中华人民共和国民法典》",
        "level": "law",
        "organ": "全国人民代表大会",
        "issueDate": "2020-05-28",
        "effectiveDate": "2021-01-01",
        "status": "effective"
      }
    ],
    "total": 500,
    "page": 1,
    "pageSize": 20
  }
}
```

### 6.2 获取法规详情

```
GET /api/v1/laws/:id
```

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "law-external-id",
    "title": "《中华人民共和国民法典》",
    "level": "law",
    "organ": "全国人民代表大会",
    "issueDate": "2020-05-28",
    "effectiveDate": "2021-01-01",
    "status": "effective",
    "content": "第一编 总则\n第一章 基本规定...",
    "chapters": [
      { "title": "第一编 总则", "articles": "第1条至第204条" }
    ],
    "relatedLaws": [
      { "id": "related-id", "title": "《最高人民法院关于适用〈中华人民共和国民法典〉合同编通则若干问题的解释》" }
    ]
  }
}
```

### 6.3 获取相关法规

```
GET /api/v1/laws/:id/related
```

---

## 7. 企业接口

### 7.1 搜索企业

```
GET /api/v1/companies/search
```

**查询参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| keyword | string | 是 | 企业名称或统一社会信用代码 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

**响应示例**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "company-external-id",
        "name": "示例科技有限公司",
        "creditCode": "91110000XXXXXXXX",
        "legalPerson": "张三",
        "capital": "1000万元",
        "establishDate": "2010-01-01",
        "status": "存续"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

### 7.2 获取企业详情

```
GET /api/v1/companies/:id
```

### 7.3 获取股东信息

```
GET /api/v1/companies/:id/shareholders
```

### 7.4 获取企业风险信息

```
GET /api/v1/companies/:id/risk
```

### 7.5 获取企业关系图谱

```
GET /api/v1/companies/:id/graph
```

---

## 8. 案源接口

### 8.1 获取案源列表

```
GET /api/v1/leads
```

**查询参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |
| status | string | 否 | 状态 |

### 8.2 创建案源

```
POST /api/v1/leads
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| title | string | 是 | 案源标题 |
| description | string | 否 | 描述 |
| source | string | 否 | 来源 |
| tags | string[] | 否 | 标签 |

### 8.3 更新案源

```
PUT /api/v1/leads/:id
```

### 8.4 更新案源状态

```
PUT /api/v1/leads/:id/status
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| status | string | 是 | new/contacted/following/converted/discarded |

### 8.5 删除案源

```
DELETE /api/v1/leads/:id
```

---

## 9. 团队接口

### 9.1 创建团队

```
POST /api/v1/admin/team
```

### 9.2 获取团队详情

```
GET /api/v1/admin/team/:id
```

### 9.3 添加团队成员

```
POST /api/v1/admin/team/:id/members
```

### 9.4 移除团队成员

```
DELETE /api/v1/admin/team/:id/members/:userId
```

---

## 10. 附录

### 10.1 角色定义

| 角色 | 值 | 说明 |
|-----|------|------|
| 管理员 | admin | 系统管理员 |
| 律师 | lawyer | 执业律师 |
| 助理 | assistant | 律师助理 |
| 访客 | guest | 访客用户 |

### 10.2 文书类型

| 类型 | 值 | 说明 |
|-----|------|------|
| 合同类 | contract | 各类合同文书 |
| 诉讼类 | litigation | 诉讼文书 |
| 非诉类 | non_litigation | 非诉文书 |
| 其他 | other | 其他文书 |

### 10.3 案源状态

| 状态 | 值 | 说明 |
|-----|------|------|
| 新建 | new | 新发现的案源 |
| 已联系 | contacted | 已联系潜在客户 |
| 跟进中 | following | 正在跟进 |
| 已转化 | converted | 已转化为正式客户 |
| 已放弃 | discarded | 放弃跟进 |