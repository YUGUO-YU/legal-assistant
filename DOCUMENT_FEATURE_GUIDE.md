# 法律文书生成功能使用指南

## ✅ 已完成功能

### 后端实现
- ✅ poi-tl Word 模板引擎集成
- ✅ 文书模板管理（CRUD）
- ✅ 文书生成服务
- ✅ 文件下载服务
- ✅ 用户历史记录
- ✅ 数据库表结构
- ✅ 后端 API 接口

### API 接口清单

| 接口 | 方法 | 端点 | 说明 |
|------|------|------|------|
| 获取模板列表 | GET | `/api/v1/documents/templates` | 所有公开模板 |
| 按分类获取 | GET | `/api/v1/documents/templates/category/{category}` | 按分类查询 |
| 模板详情 | GET | `/api/v1/documents/templates/{id}` | 查看详情和变量 |
| 生成文书 | POST | `/api/v1/documents/generate` | 生成 Word 文档 |
| 下载文书 | GET | `/api/v1/documents/download?file=` | 下载生成的文档 |
| 历史记录 | GET | `/api/v1/documents/history` | 用户生成历史 |

### 模板分类
- `civil_litigation` - 民事诉讼类
- `contract` - 合同类
- `marriage` - 婚姻家事项
- `labor` - 劳动仲裁类
- `other` - 其他

## 📝 使用流程

### 1. 准备模板文件

#### 创建 Word 模板

1. 使用 Microsoft Word 或 WPS 创建文档
2. 在需要替换的位置添加占位符，格式：`{{变量名}}`
3. 保存为 .docx 格式

#### 占位符语法

**文本变量：**
```
{{plaintiff_name}} - 原告姓名
{{defendant_name}} - 被告姓名
```

**日期变量：**
```
{{sign_date}} - 签署日期（系统自动格式化为"xxxx 年 xx 月 xx 日"）
```

**图片变量：**
```
{{@logo}} - 公司 Logo
```

**表格变量：**
```
{{#evidence_list}}
  {{name}} | {{description}} | {{page}}
{{/evidence_list}}
```

**条件渲染：**
```
{{?has_children}}
子女抚养：{{child_custody}}
{{/has_children}}
```

### 2. 上传模板

**方式一：使用 SQL 直接插入**
```sql
INSERT INTO document_templates (name, category, description, file_path, variables, is_public) VALUES
('民事起诉状', 'civil_litigation', '适用于民事案件起诉', 
 '/workspace/server/templates/civil/complaint.docx',
 '[{"name":"plaintiff_name","label":"原告姓名","type":"text","required":true}]', 1);
```

**方式二：通过管理后台上传（待实现）**

### 3. 调用 API 生成文书

**步骤 1: 查询模板**
```bash
curl http://localhost:8080/api/v1/documents/templates
```

**响应：**
```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "民事起诉状",
      "category": "civil_litigation",
      "description": "适用于民事案件起诉",
      "variables": [
        {"name": "plaintiff_name", "label": "原告姓名", "type": "text", "required": true}
      ]
    }
  ]
}
```

**步骤 2: 填写数据并生成**
```bash
curl -X POST http://localhost:8080/api/v1/documents/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "templateId": 1,
    "data": {
      "plaintiff_name": "张三",
      "plaintiff_phone": "13800138000",
      "defendant_name": "李四",
      "claim": "要求被告支付欠款 10 万元",
      "facts": "2023 年 1 月 1 日，被告向原告借款 10 万元...",
      "court_name": "北京市朝阳区人民法院",
      "sign_date": "2024-06-04"
    }
  }'
```

**响应：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "downloadUrl": "/api/v1/documents/download?file=%2Fworkspace%2Fserver%2Foutput%2F...",
    "filePath": "/workspace/server/output/民事起诉状_20240604_123456_abc123.docx",
    "expiresIn": "86400"
  }
}
```

**步骤 3: 下载文件**
```bash
curl -o 民事起诉状.docx "http://localhost:8080/api/v1/documents/download?file=..."
```

## 📋 示例模板变量配置

### 民事起诉状模板变量
```json
[
  {"name":"plaintiff_name","label":"原告姓名","type":"text","required":true,"placeholder":"请输入原告姓名"},
  {"name":"plaintiff_gender","label":"原告性别","type":"select","required":true,"options":[{"value":"男","label":"男"},{"value":"女","label":"女"}]},
  {"name":"plaintiff_ethnicity","label":"原告民族","type":"text","required":false},
  {"name":"plaintiff_birthdate","label":"原告出生日期","type":"date","required":false},
  {"name":"plaintifiant_address","label":"原告住址","type":"text","required":true},
  {"name":"plaintiff_phone","label":"原告电话","type":"text","required":true},
  
  {"name":"defendant_name","label":"被告姓名","type":"text","required":true},
  {"name":"defendant_gender","label":"被告性别","type":"select","required":false},
  {"name":"defendant_address","label":"被告住址","type":"text","required":true},
  
  {"name":"claim","label":"诉讼请求","type":"textarea","required":true,"rows":4},
  {"name":"facts","label":"事实与理由","type":"textarea","required":true,"rows":8},
  
  {"name":"court_name","label":"受理法院","type":"text","required":true},
  {"name":"sign_date","label":"签署日期","type":"date","required":true}
]
```

## 🔧 配置说明

### application.yml 配置项

```yaml
legal:
  assistant:
    document:
      # 模板文件目录
      template-dir: /workspace/server/templates
      # 输出文件目录
      output-dir: /workspace/server/output
      # 文件保留天数（秒）
      file-retention-days: 1
```

## ⏭️ 待实现功能

### 后端
- [ ] 模板文件上传接口
- [ ] 模板变量自动提取
- [ ] 定时清理过期文件任务
- [ ] 文书预览（转 PDF）
- [ ] 批量生成

### 前端
- [ ] 模板选择页面
- [ ] 动态表单生成
- [ ] 文书下载页面
- [ ] 历史记录列表
- [ ] 模板管理后台

## 🐛 问题排查

### 模板文件不存在
```
错误：模板不存在或已被删除
解决：检查 file_path 是否正确，模板文件是否存在
```

### 文档生成失败
```
错误：文档生成失败：xxx
解决：检查模板文件格式是否正确，变量名是否匹配
```

### 文件下载失败
```
错误：文件不存在或已过期
解决：检查文件是否还在临时目录，是否超过保留期限
```
