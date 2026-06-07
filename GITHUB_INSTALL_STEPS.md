# GitHub 下载安装测试步骤

## 方式一：Git 克隆（推荐）

### 1.1 安装 Git for Windows

1. 下载 Git: https://git-scm.com/download/win
2. 双击安装，默认选项即可
3. 安装完成后，右键菜单会出现「Git Bash Here」

### 1.2 克隆项目

打开 `Git Bash` 或 `CMD`，执行：

```bash
# 克隆到当前目录
git clone https://github.com/YUGUO-YU/legal-assistant.git

# 进入项目目录
cd legal-assistant

# 查看项目结构
ls -la
```

---

## 方式二：直接下载 ZIP

### 2.1 下载

1. 打开浏览器访问：
   ```
   https://github.com/YUGUO-YU/legal-assistant
   ```
2. 点击绿色的 **Code** 按钮
3. 点击 **Download ZIP**
4. 保存到 `D:\Projects\legal-assistant-main.zip`

### 2.2 解压

```bash
# 解压到 D:\Projects
cd D:\Projects
unzip legal-assistant-main.zip

# 重命名目录（可选）
mv legal-assistant-main legal-assistant
cd legal-assistant
```

---

## Windows 完整安装测试步骤

### 第一步：安装基础环境

#### 1.1 安装 Java JDK 17

**下载地址**: https://adoptium.net/temurin/releases/?version=17

```bash
# 下载 jdk-17.x.x+x_windows_x64_hotspot.msi
# 双击安装，路径设为 D:\Tools\Java\jdk-17.x.x
```

**配置环境变量**:
```
JAVA_HOME = D:\Tools\Java\jdk-17.x.x
Path 添加 = %JAVA_HOME%\bin
```

**验证**:
```bash
java -version
# 输出: openjdk version "17.x.x"
```

---

#### 1.2 安装 Maven

**下载地址**: https://maven.apache.org/download.cgi

```bash
# 下载 apache-maven-3.9.x-bin.zip
# 解压到 D:\Tools\apache-maven-3.9.x
```

**配置环境变量**:
```
MAVEN_HOME = D:\Tools\apache-maven-3.9.x
Path 添加 = %MAVEN_HOME%\bin
```

**验证**:
```bash
mvn -version
# 输出: Apache Maven 3.9.x
```

---

#### 1.3 安装 Node.js

**下载地址**: https://nodejs.org/ (LTS 版本)

```bash
# 下载 node-18.x.x-x64.msi
# 双击安装，默认选项即可
```

**验证**:
```bash
node -v
npm -v
```

---

#### 1.4 安装 MySQL

**下载地址**: https://dev.mysql.com/downloads/installer/

```bash
# 下载 mysql-installer-community-8.x.x.msi
# 双击安装，选择"完全安装"
# 设置 root 密码为: main0126
```

**创建数据库**:
```bash
mysql -u root -p
# 输入密码: main0126

CREATE DATABASE legal_assistant DEFAULT CHARACTER SET utf8mb4;
EXIT;
```

---

#### 1.5 安装 Redis

**下载地址**: https://github.com/microsoftarchive/redis/releases

```bash
# 下载 Redis-x64-3.0.504.msi
# 双击安装，默认选项即可
```

**启动 Redis**:
```bash
redis-server --daemonize yes
redis-cli ping
# 输出: PONG
```

---

### 第二步：下载项目

#### 2.1 Git 克隆方式

```bash
cd D:\Projects
git clone https://github.com/YUGUO-YU/legal-assistant.git
cd legal-assistant
```

#### 2.2 ZIP 下载方式

```bash
cd D:\Projects
wget https://github.com/YUGUO-YU/legal-assistant/archive/refs/heads/main.zip
unzip main.zip
cd legal-assistant-main
```

---

### 第三步：配置项目

#### 3.1 修改数据库配置

打开文件：
```
server\src\main\resources\application.yml
```

确认配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/legal_assistant?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: main0126
```

---

### 第四步：编译后端

#### 4.1 编译

```bash
cd D:\Projects\legal-assistant\server

mvn clean package -DskipTests
```

**等待时间**: 5-10 分钟（首次编译）

#### 4.2 验证编译成功

```bash
ls -la target/*.jar
# 应看到: legal-assistant-1.0.0.jar
```

---

### 第五步：安装前端依赖

#### 5.1 安装

```bash
cd D:\Projects\legal-assistant\client

npm install
```

**等待时间**: 3-5 分钟

---

### 第六步：启动服务

#### 6.1 启动 MySQL

```bash
net start mysql80
```

#### 6.2 启动 Redis

```bash
redis-server --daemonize yes
```

#### 6.3 启动后端

```bash
cd D:\Projects\legal-assistant\server

java -jar target/legal-assistant-1.0.0.jar
```

**等待出现**: `Started LegalAssistantApplication in xx seconds`

#### 6.4 启动前端

新开一个终端窗口：

```bash
cd D:\Projects\legal-assistant\client

npm run dev
```

**等待出现**: `ready in xx ms`

---

### 第七步：访问测试

打开浏览器访问：

```
http://localhost:5173
```

---

## 快速命令汇总

### 完整安装命令（复制执行）

```bash
# 1. 克隆项目
git clone https://github.com/YUGUO-YU/legal-assistant.git
cd legal-assistant

# 2. 编译后端
cd server
mvn clean package -DskipTests

# 3. 安装前端
cd ../client
npm install

# 4. 启动后端
cd ../server
java -jar target/legal-assistant-1.0.0.jar

# 5. 启动前端（另开窗口）
cd ../client
npm run dev
```

### 一键启动脚本

保存为 `start.bat`：

```batch
@echo off
echo ========================================
echo   法律助手 - 启动服务
echo ========================================

echo.
echo [1] 启动 MySQL...
net start mysql80

echo.
echo [2] 启动 Redis...
redis-server --daemonize yes

echo.
echo [3] 启动后端...
cd server
start java -jar target/legal-assistant-1.0.0.jar

echo.
echo [4] 启动前端...
cd ../client
start npm run dev

echo.
echo ========================================
echo   服务启动中，请稍候...
echo   访问地址: http://localhost:5173
echo ========================================
pause
```

---

## GitHub 相关命令

### 常用 Git 命令

```bash
# 克隆仓库
git clone https://github.com/YUGUO-YU/legal-assistant.git

# 进入目录
cd legal-assistant

# 查看状态
git status

# 查看远程仓库
git remote -v

# 拉取最新代码
git pull origin main

# 查看所有分支
git branch -a

# 切换分支
git checkout main
```

### 如果你只想下载部分文件

```bash
# 使用 GitHub CLI（推荐先安装）
gh auth login
gh repo clone YUGUO-YU/legal-assistant -- --path=server/src/main/resources

# 或者直接下载单个文件
wget https://raw.githubusercontent.com/YUGUO-YU/legal-assistant/main/README.md
```

---

## 验证检查清单

完成安装后，逐项检查：

```
[ ] git clone 成功，项目目录存在
[ ] java -version 显示 17.x
[ ] mvn -version 显示 3.9.x
[ ] node -v 显示 18.x 或 20.x
[ ] npm -v 显示 9.x 或 10.x

[ ] mysql -u root -p 能连接（密码 main0126）
[ ] redis-cli ping 返回 PONG

[ ] server/target/legal-assistant-1.0.0.jar 存在
[ ] client/node_modules 目录存在

[ ] http://localhost:5173 能访问
[ ] http://localhost:8080/api/v1 能访问
```

---

## 常见问题

### 问题 1：git clone 失败

**解决**：检查网络，或使用 ZIP 下载

```bash
# 检查网络
ping github.com

# 使用代理（如果有）
git config --global http.proxy http://proxy:8080
git config --global https.proxy https://proxy:8080
```

### 问题 2：编译失败

**解决**：
```bash
# 清理后重新编译
cd server
mvn clean
mvn clean package -DskipTests
```

### 问题 3：端口被占用

**解决**：
```bash
# 查看端口占用
netstat -ano | findstr :8080
netstat -ano | findstr :5173

# 关闭占用进程
taskkill /PID <进程ID> /F
```

### 问题 4：npm install 失败

**解决**：
```bash
# 清除缓存
npm cache clean --force

# 使用淘宝镜像
npm install --registry=https://registry.npmmirror.com
```

---

## 项目地址

```
GitHub: https://github.com/YUGUO-YU/legal-assistant
作者: YUGUO-YU
```

---

**安装成功！访问 http://localhost:5173 开始使用！**