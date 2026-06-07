# 文书模板制作说明

由于当前环境没有安装 python-docx 模块，请手动创建 Word 模板。

## 制作方法

### 1. 使用 Microsoft Word 或 WPS

1. 打开 Word 新建文档
2. 按照下面的模板内容输入
3. 在需要替换的位置使用占位符 `{{变量名}}`
4. 保存为 .docx 格式到对应目录

### 2. 民事起诉状模板示例

```
                         民事起诉状

原告基本信息
原告：{{plaintiff_name}}，性别：{{plaintiff_gender}}，民族：{{plaintiff_ethnicity}}
出生日期：{{plaintiff_birthdate}}，住址：{{plaintiff_address}}
联系电话：{{plaintiff_phone}}

被告基本信息  
被告：{{defendant_name}}，性别：{{defendant_gender}}
住址：{{defendant_address}}

诉讼请求
{{claim}}

事实与理由
{{facts}}

此致
{{court_name}}

                                    具状人：{{plaintiff_name}}
                                    日期：{{sign_date}}
```

### 3. 模板文件存放位置

- 民事类：`/workspace/server/templates/civil/`
- 合同类：`/workspace/server/templates/contract/`
- 婚姻类：`/workspace/server/templates/marriage/`
- 其他：`/workspace/server/templates/other/`

### 4. 占位符格式说明

**基本格式：**
```
{{变量名}}
```

**变量命名规则:**
- 使用小写字母和下划线
- 使用英文或拼音
- 见名知意，例如:
  - plaintiff_name (原告姓名)
  - defendant_name (被告姓名)
  - claim_amount (标的金额)
  - sign_date (签署日期)

**支持的占位符类型:**

| 类型 | 语法 | 示例 |
|------|------|------|
| 文本 | `{{name}}` | `{{plaintiff_name}}` |
| 图片 | `{{@image}}` | `{{@company_logo}}` |
| 表格 | `{{#table}}...{{/table}}` | `{{#evidence_list}}...{{/evidence_list}}` |
| 条件 | `{{?condition}}...{{/condition}}` | `{{?has_children}}...{{/has_children}}` |

## 测试模板

建议先手动创建一个简单的 test.docx：

内容：
```
测试文档

姓名：{{name}}
日期：{{date}}
内容：{{content}}
```

然后用 API 测试：
```bash
curl -X POST http://localhost:8080/api/v1/documents/generate \
  -H "Content-Type: application/json" \
  -d '{"templateId":1,"data":{"name":"张三","date":"2024-06-04","content":"测试内容"}}'
```

## 后续工作

1. 手动创建 Word 模板文件
2. 测试模板渲染
3. 验证生成的文档格式正确
