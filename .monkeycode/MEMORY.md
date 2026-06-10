
## 法律助手项目修复记录 (2026-06-10)

### 修复的问题
- **userId类型统一为String**：JWT中的userId是UUID字符串，修改了以下文件：
  - `DocumentService.java`/`DocumentServiceImpl.java`：generateDocument/getUserHistory 参数改为String
  - `DocumentHistory.java`：`userId` 字段改为String
  - `DocumentHistoryMapper.java`：添加@Select注解查询
  - `JwtAuthenticationFilter.java`：userId不再转为Long
  - `DocumentController.java`：移除Long.parseLong调用
  - 数据库`document_histories`表：`user_id`字段改为VARCHAR(50)

- **模板路径修复**：`DocumentServiceImpl.java` 中路径拼接去重处理

### 验证结果
- 短信验证码登录：正常
- 文书生成：成功（民事起诉状.docx，37KB）
- 历史记录查询：正常

### 测试命令
```bash
# 登录
curl -X POST http://localhost:8080/api/v1/auth/sms/send -H "Content-Type: application/json" -d '{"phone":"13900001111","type":"login"}'
CODE=$(redis-cli -a 123456 GET "sms:code:13900001111" | tr -d '"')
curl -X POST http://localhost:8080/api/v1/auth/phone/login -H "Content-Type: application/json" -d "{\"phone\":\"13900001111\",\"code\":\"$CODE\"}"

# 生成文书
curl -X POST http://localhost:8080/api/v1/documents/generate -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"templateId":1,"data":{"plaintiff_name":"张三","plaintiff_gender":"男","plaintiff_address":"北京市朝阳区","defendant_name":"李四","defendant_address":"上海市浦东新区","claim":"归还借款10万元","facts":"2024年1月借款至今未还","court_name":"北京市朝阳区人民法院","sign_date":"2024年6月7日"}}'

# 查看历史
curl http://localhost:8080/api/v1/documents/history -H "Authorization: Bearer $TOKEN"
```
