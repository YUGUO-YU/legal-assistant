# 从 GitHub 下载安装测试指南

## 方式一：克隆完整仓库（推荐）

```bash
# 1. 克隆仓库
git clone https://github.com/YUGUO-YU/legal-assistant.git

# 2. 进入项目目录
cd legal-assistant

# 3. 查看项目结构
ls -la
```

## 方式二：下载 ZIP 包（快速体验）

```bash
# 下载最新代码
wget https://github.com/YUGUO-YU/legal-assistant/archive/refs/heads/main.zip

# 解压
unzip main.zip

# 进入目录
cd legal-assistant-main
```

---

## 安装步骤

### 1. 安装基础依赖

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install -y openjdk-17-jdk maven nodejs npm redis-server
```

### 2. 配置 MySQL

```bash
# 安装 MySQL（参考上面的配置指南）
# 创建数据库和用户
mysql -u root -p

# 在 MySQL 中执行：
CREATE DATABASE legal_assistant DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'legal_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON legal_assistant.* TO 'legal_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 3. 修改配置文件

编辑 `server/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    password: your_password  # 修改为你的 MySQL 密码
```

### 4. 编译后端

```bash
cd server
mvn clean package -DskipTests
```

### 5. 安装前端依赖

```bash
cd ../client
npm install
```

### 6. 启动服务

```bash
# 启动 Redis
redis-server --daemonize yes

# 启动后端
cd ../server
nohup java -jar target/legal-assistant-1.0.0.jar > /tmp/backend.log 2>&1 &

# 启动前端
cd ../client
npm run dev
```

### 7. 访问测试

```
前端页面: http://localhost:5173
后端接口: http://localhost:8080
```

---

## GitHub 下载命令汇总

```bash
# 克隆仓库
git clone https://github.com/YUGUO-YU/legal-assistant.git

# 进入目录
cd legal-assistant

# 查看远程仓库
git remote -v

# 查看所有分支
git branch -a

# 切换到 main 分支
git checkout main

# 拉取最新代码
git pull origin main
```

---

## 如果你只想下载特定文件

可以使用 GitHub 的 raw.githubusercontent.com：

```bash
# 下载单个文件
wget https://raw.githubusercontent.com/YUGUO-YU/legal-assistant/main/README.md

# 下载整个文件夹（需要用 GitHub CLI）
gh repo clone YUGUO-YU/legal-assistant -- --path=server/src
```
