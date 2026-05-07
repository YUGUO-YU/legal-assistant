# 法律助手 (LegalAssistant) 项目文档

## 文档索引

### 项目概述
- **项目名称**：法律助手
- **项目类型**：微信小程序 + Web 应用双端
- **目标用户**：执业律师、法律顾问、法务人员
- **核心功能**：法律文书起草、案例查询、法规查询、企业查询、案源发现

---

## 文档目录

| 文档 | 说明 |
|-----|------|
| [ARCHITECTURE.md](./ARCHITECTURE.md) | 系统架构文档 |
| [DEVELOPER_GUIDE.md](./DEVELOPER_GUIDE.md) | 开发者指南 |
| [.monkeycode/specs/legal-assistant/requirements.md](../.monkeycode/specs/legal-assistant/requirements.md) | 需求规格说明书 |
| [.monkeycode/specs/legal-assistant/design.md](../.monkeycode/specs/legal-assistant/design.md) | 技术设计说明书 |

---

## 技术栈

### 前端
- **跨端框架**：uni-app (微信小程序 + H5)
- **Web 框架**：Vue 3 + Vite
- **状态管理**：Pinia
- **UI 组件**：uView / Element Plus

### 后端
- **运行时**：Java 17+
- **框架**：Spring Boot 3.x
- **ORM**：MyBatis-Plus
- **数据库**：MySQL 8.0
- **缓存**：Redis 7.0

---

## 核心模块

| 模块 | 说明 |
|-----|------|
| 用户认证 | 手机号登录、微信登录、邮箱登录 |
| 文书起草 | 模板库、智能生成、编辑导出 |
| 案例查询 | 裁判文书检索、案例详情、相似推荐 |
| 法规查询 | 法规检索、正文查看、关联法规 |
| 企业查询 | 工商信息、股东穿透、风险查询 |
| 案源管理 | 线索发现、状态跟踪 |
| 团队协作 | 团队管理、成员权限 |

---

## 快速链接

- [需求文档](../.monkeycode/specs/legal-assistant/requirements.md)
- [设计文档](../.monkeycode/specs/legal-assistant/design.md)
- [架构文档](./ARCHITECTURE.md)
- [开发指南](./DEVELOPER_GUIDE.md)