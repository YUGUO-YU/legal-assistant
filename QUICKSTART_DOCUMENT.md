# 法律文书生成功能 - 快速启动指南

## ⚡ 5 分钟快速测试

### 步骤 1: 初始化数据库

```bash
# 连接 MySQL
mysql -u root -p

# 执行建表 SQL
source /workspace/server/src/main/resources/sql/schema.sql;

# 验证表已创建
SHOW TABLES;
# 应该看到：document_templates, document_histories
```

### 步骤 2: 添加配置

编辑 `server/src/main/resources/application.yml`，在末尾添加：

```yaml
# 文书生成配置
legal:
  assistant:
    document:
      template-dir: /workspace/server/templates
      output-dir: /workspace/server/output
      file-retention-days: 1
```

### 步骤 3: 创建测试模板

手动创建一个简单的 Word 模板：

```bash
# 进入模板目录
mkdir -p /workspace/server/templates/civil

# 创建测试模板（手动或使用 Word）
cat > /workspace/server/templates/civil/test_template.txt << 'TEMPLATE'
测试文书

姓名：{{name}}
电话：{{phone}}
日期：{{date}}
内容：{{content}}
TEMPLATE

# 注意：需要用 Word 创建真正的 .docx 文件
# 参考：/workspace/server/templates/TEMPLATE_MANUAL.md
```

### 步骤 4: 启动后端服务

```bash
cd /workspace
./start.sh

# 或者直接启动后端
cd /workspace/server
java -jar target/legal-assistant-1.0.0.jar &

# 查看日志
tail -f /tmp/backend.log | grep "Started"
```

### 步骤 5: 测试 API

```bash
# 1. 获取模板列表
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/v1/documents/templates

# 2. 假设已有模板 ID=1，生成文书
curl -X POST http://localhost:8080/api/v1/documents/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "templateId": 1,
    "data": {
      "name": "张三",
      "phone": "13800138000",
      "date": "2024-06-04",
      "content": "这是一个测试"
    }
  }'

# 3. 下载生成的文档
# 响应中会包含 downloadUrl，使用 curl 下载
curl -o test.docx "http://localhost:8080/api/v1/documents/download?file=..."
```

---

## 📋 完整部署步骤

### 1. 安装依赖

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install -y redis-server mysql-server openjdk-17-jdk nodejs npm

# 启动服务
sudo systemctl start redis mysql
```

### 2. 配置数据库

```bash
mysql -u root -p

CREATE DATABASE legal_assistant DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'legal_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON legal_assistant.* TO 'legal_user'@'localhost';
FLUSH PRIVILEGES;

# 导入表结构
source /workspace/server/src/main/resources/sql/schema.sql;
EXIT;
```

### 3. 编译后端

```bash
cd /workspace/server
mvn clean package -DskipTests
```

### 4. 安装前端依赖

```bash
cd /workspace/client
npm install
```

### 5. 启动所有服务

```bash
cd /workspace
./start.sh
```

---

## 🔧 常用命令

```bash
# 查看服务状态
ps aux | grep -E "redis|java|vite|mysql" | grep -v grep

# 查看端口占用
netstat -tlnp | grep -E "6379|8080|5173|3306"

# 查看后端日志
tail -100 /tmp/backend.log

# 查看错误日志
tail -100 /tmp/backend.log | grep ERROR

# 重启后端
pkill -f legal-assistant
cd /workspace/server && java -jar target/legal-assistant-1.0.0.jar &

# 重启前端
pkill -f vite
cd /workspace/client && npm run dev
```

---

## 📖 相关文档

| 文档 | 说明 |
|------|------|
| `DOCUMENT_GENERATION_SUMMARY.md` | 完整实现总结 |
| `DOCUMENT_FEATURE_GUIDE.md` | 使用指南 |
| `INSTALL_AND_TEST.md` | 安装测试指南 |
| `.monkeycode/specs/260604-document-generation/` | 需求和设计文档 |

---

## ✅ 功能就绪检查清单

- [ ] Redis 运行正常
- [ ] MySQL 运行正常
- [ ] 数据库表已创建
- [ ] 后端编译成功
- [ ] 配置文件已更新
- [ ] 模板目录已创建
- [ ] 至少创建了 1 个测试模板
- [ ] 服务启动成功

全部勾选后即可使用！

---

**创建时间:** 2026-06-04  
**文档版本:** 1.0
