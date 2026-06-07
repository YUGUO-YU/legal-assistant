# 法律文书生成功能实现计划

## 当前进度（2026-06-04）

### ✅ 已完成

1. **需求文档** - `.monkeycode/specs/260604-document-generation/requirements.md`
   - 6 个核心需求定义
   - 14 种首批模板清单
   - 技术选型说明

2. **设计文档** - `.monkeycode/specs/260604-document-generation/design.md`
   - 系统架构图
   - 核心类和 API 设计
   - 数据模型定义
   - 错误处理策略
   - 测试方案

3. **后端准备**
   - ✅ 添加 poi-tl 依赖（pom.xml）
   - ✅ 添加 commons-io 依赖
   - ✅ 创建实体类（DocumentTemplate、DocumentHistory、TemplateVariable）
   - ✅ 创建 TypeHandler（FastjsonTypeHandler）
   - ✅ 创建 DTO（DocumentGenerateRequest、DocumentGenerateResponse）
   - ✅ 编译成功

### ⏳ 待实现

#### 后端任务
1. 创建 MyBatis Mapper 接口
2. 创建 Repository 层
3. 创建 DocumentService 服务
4. 创建 DocumentController 控制器
5. 创建数据库迁移脚本（建表 SQL）
6. 实现模板文件上传功能
7. 实现 poi-tl 模板渲染逻辑
8. 实现文件下载接口
9. 添加定时清理任务（临时文件）

#### 前端任务
1. 创建文书生成页面路由
2. 创建模板列表组件
3. 创建动态表单组件
4. 创建文档下载组件
5. 创建历史记录页面
6. 集成 API 调用

#### 模板收集
1. 收集 14 种法律文书模板（DOCX 格式）
2. 标注模板变量占位符
3. 导入模板到系统

### 📋 待确认事项

#### 问题 1: 模板来源
以下模板获取方式，请确认优先顺序：

**选项 A: 免费公开模板**
- 中国法律服务网 (12348.gov.cn)
- 法书网 (fabook.net)
- 110 法律网 (110.com)
- 优点：免费，快速启动
- 缺点：质量参差不齐，需要筛选

**选项 B: 专业模板库**
- 购买商业授权模板
- 与律所合作获取
- 优点：专业、规范
- 缺点：成本、时间

**建议**: 先使用选项 A 收集基础模板，后期逐步替换为专业模板

---

#### 问题 2: 文档存储策略

**方案 A: 仅浏览器下载（推荐初期使用）**
- 生成的文档直接下载给用户
- 服务器不保留副本
- 优点：节省存储空间，无隐私风险
- 缺点：用户无法重新下载

**方案 B: 服务器保留 6 个月**
- 生成的文档保存在服务器
- 用户可随时重新下载
- 优点：用户体验好
- 缺点：存储成本，每月约需 10-50GB（按 1000 份/月，每份 500KB 计算）

**建议**: MVP版本使用方案 A，正式版使用方案 B

---

#### 问题 3: 功能开放策略

**选项 A: 全部免费**
- 所有模板免费使用
- 优点：快速获客
- 缺点：无直接收入

**选项 B: 基础模板免费 + 高级模板付费**
- 起诉状、律师函等基础功能免费
- 合同、协议书等高级模板需会员
- 优点：有盈利模式
- 缺点：用户体验割裂

**建议**: 初期使用选项 A 积累用户，后期转为选项 B

---

## 下一步工作

### 待您确认后可继续：

1. **确认上述 3 个问题**
   - 模板来源：A / B / A+B
   - 存储策略：A / B
   - 开放策略：A / B

2. **创建数据库表**
   - 执行建表 SQL
   - 初始化模板分类

3. **收集首批模板**
   - 确定 5-10 个最常用模板
   - 标注变量占位符

4. **完成后端核心逻辑**
   - DocumentService 实现
   - DocumentController 实现
   - 文件上传/下载接口

5. **开发前端页面**
   - 模板选择页
   - 表单填写页
   - 下载和历史记录页

---

## 模板变量标注示例

以"民事起诉状"为例：

```
{{plaintiff_name}} - 原告姓名
{{plaintiff_gender}} - 原告性别
{{plaintiff_ethnicity}} - 原告民族
{{plaintiff_birthdate}} - 原告出生日期
{{plaintiff_address}} - 原告住址
{{plaintiff_phone}} - 原告电话

{{defendant_name}} - 被告姓名
{{defendant_gender}} - 被告性别
{{defendant_address}} - 被告住址

{{claim_title}} - 诉讼请求标题
{{claim_items}} - 诉讼请求项目（列表）

{{facts_description}} - 事实与理由描述
{{evidence_list}} - 证据列表（表格）

{{court_name}} - 法院名称
{{sign_date}} - 签署日期
```

---

## 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| Word 引擎 | poi-tl | 1.12.2 |
| 后端框架 | Spring Boot | 3.2.0 |
| 前端框架 | Vue 3 | 3.4.x |
| UI 组件 | Element Plus | latest |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 7.0+ |

---

## 快速原型（本周可完成）

1. 实现单个模板（民事起诉状）的生成
2. 前端简单表单填写
3. 下载 DOCX 文件
4. 验证完整流程

确认后即可开始实施！
