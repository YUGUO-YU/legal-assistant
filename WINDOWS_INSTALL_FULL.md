# Windows 安装测试完整步骤

## 📦 一、准备工作

### 1.1 必装软件清单

| 序号 | 软件 | 下载地址 | 大小 |
|------|------|---------|------|
| 1 | Git | https://git-scm.com/download/win | ~45MB |
| 2 | Java JDK 17 | https://adoptium.net/temurin/releases/?version=17 | ~200MB |
| 3 | Maven | https://maven.apache.org/download.cgi | ~10MB |
| 4 | Node.js | https://nodejs.org/ | ~30MB |
| 5 | MySQL | https://dev.mysql.com/downloads/installer/ | ~400MB |
| 6 | Redis | https://github.com/microsoftarchive/redis/releases | ~5MB |

**创建安装目录**：在 D 盘新建 `Tools` 文件夹

```
D:\Tools\
├── Java\jdk-17.x.x\
├── Maven\apache-maven-3.9.x\
├── Nodejs\
└── Redis\
```

---

## 📥 二、安装软件

### 2.1 安装 Git

1. 双击 `Git-2.x.x-64-bit.exe`
2. 安装路径：`D:\Tools\Git`
3. 点击 Next -> Next -> Install
4. 完成

**验证**：右键桌面 -> Git Bash Here
```bash
git --version
```

---

### 2.2 安装 Java JDK 17

1. 双击 `jdk-17.x.x+x_windows_x64_hotspot.msi`
2. 安装路径：`D:\Tools\Java\jdk-17.x.x`
3. Next -> Install

**配置环境变量**：
```
JAVA_HOME = D:\Tools\Java\jdk-17.x.x
Path 添加 = %JAVA_HOME%\bin
```

**验证**：
```bash
java -version
# 输出: openjdk version "17.x.x"
```

---

### 2.3 安装 Maven

1. 下载 `apache-maven-3.9.x-bin.zip`
2. 解压到 `D:\Tools\Maven\apache-maven-3.9.x`

**配置环境变量**：
```
MAVEN_HOME = D:\Tools\Maven\apache-maven-3.9.x
Path 添加 = %MAVEN_HOME%\bin
```

**验证**：
```bash
mvn -version
# 输出: Apache Maven 3.9.x
```

---

### 2.4 安装 Node.js

1. 双击 `node-18.x.x-x64.msi`
2. 安装路径：`D:\Tools\Nodejs`
3. Next -> Install

**验证**：
```bash
node -v
npm -v
```

---

### 2.5 安装 MySQL

1. 双击 `mysql-installer-community-8.x.x.msi`
2. 选择「Custom」-> Next
3. 勾选「MySQL Server 8.0.x」-> Add
4. 点击 Next -> Execute
5. 等待下载安装完成
6. **设置 root 密码为：`main0126`**
7. Next -> Execute -> Finish

**创建数据库**：
```bash
mysql -u root -p
# 输入密码: main0126

CREATE DATABASE legal_assistant DEFAULT CHARACTER SET utf8mb4;
EXIT;
```

---

### 2.6 安装 Redis

1. 双击 `Redis-x64-3.0.504.msi`
2. 安装路径：`D:\Tools\Redis`
3. 勾选「Add the Redis installation folder to the PATH」
4. Next -> Install

**启动 Redis**：
```bash
redis-server --daemonize yes
```

**验证**：
```bash
redis-cli ping
# 输出: PONG
```

---

## 📥 三、下载项目

### 3.1 Git 克隆（推荐）

打开 `Git Bash`：

```bash
cd D:\Projects
git clone https://github.com/YUGUO-YU/legal-assistant.git
cd legal-assistant
```

### 3.2 ZIP 下载

1. 打开 https://github.com/YUGUO-YU/legal-assistant
2. 点击 `Code` -> `Download ZIP`
3. 解压到 `D:\Projects\legal-assistant`

---

## ⚙️ 四、配置项目

### 4.1 修改配置文件

打开文件：
```
D:\Projects\legal-assistant\server\src\main\resources\application.yml
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

## 🔨 五、编译后端

### 5.1 打开命令行

按 `Win+R`，输入 `cmd`，回车

```bash
cd D:\Projects\legal-assistant\server
```

### 5.2 编译

```bash
mvn clean package -DskipTests
```

**等待时间**：5-10 分钟（首次编译需下载依赖）

### 5.3 验证编译成功

```bash
dir target\*.jar
```

应显示：`legal-assistant-1.0.0.jar`

---

## 📦 六、安装前端依赖

### 6.1 安装

```bash
cd D:\Projects\legal-assistant\client
npm install
```

**等待时间**：3-5 分钟

---

## 🚀 七、启动服务

### 7.1 启动 MySQL（如果未启动）

```bash
net start mysql80
```

### 7.2 启动 Redis（如果未启动）

```bash
redis-server --daemonize yes
```

### 7.3 启动后端

```bash
cd D:\Projects\legal-assistant\server
java -jar target\legal-assistant-1.0.0.jar
```

**等待出现**：`Started LegalAssistantApplication in xx seconds`

### 7.4 启动前端（新开命令行窗口）

```bash
cd D:\Projects\legal-assistant\client
npm run dev
```

**等待出现**：`ready in xx ms`

---

## ✅ 八、访问测试

打开浏览器访问：

```
http://localhost:5173
```

---

## 📋 完整命令汇总

### 复制以下命令，保存为 `install.bat`

```batch
@echo off
chcp 650nnul
echo ========================================
echo   法律助手 - Windows 一键安装
echo ========================================

echo.
echo [1/8] 检查 Java...
java -version

echo.
echo [2/8] 检查 Maven...
mvn -version

echo.
echo [3/8] 检查 Node.js...
node -v

echo.
echo [4/8] 启动 MySQL...
net start mysql80

echo.
echo [5/8] 启动 Redis...
redis-server --daemonize yes

echo.
echo [6/8] 创建数据库...
mysql -u root -p"main0126" -e "CREATE DATABASE IF NOT EXISTS legal_assistant DEFAULT CHARACTER SET utf8mb4;" 2nul

echo.
echo [7/8] 编译后端...
cd server
call mvn clean package -DskipTests

echo.
echo [8/8] 安装前端...
cd ../client
call npm install

echo.
echo ========================================
echo   安装完成！
echo ========================================
echo.
echo 下一步启动服务：
echo   1. 启动后端: java -jar server\target\legal-assistant-1.0.0.jar
echo   2. 启动前端: cd client ^&^& npm run dev
echo   3. 访问地址: http://localhost:5173
echo.
pause
```

---

## 🔍 九、验证检查

### 环境验证
```bash
java -version    # 17.x
mvn -version    # 3.9.x
node -v         # 18.x 或 20.x
npm -v          # 9.x 或 10.x
```

### 服务验证
```bash
mysql -u root -p    # 输入 main0126，能连接
redis-cli ping       # 返回 PONG
```

### 项目验证
```bash
# 编译成功
D:\Projects\legal-assistant\server\target\legal-assistant-1.0.0.jar 存在

# 前端依赖安装成功
D:\Projects\legal-assistant\client\node_modules 目录存在
```

### 访问验证
```
http://localhost:5173     # 前端页面
http://localhost:8080     # 后端接口
```

---

## 🐛 十、常见问题解决

### 问题 1：编译失败，下载依赖慢

**解决**：配置 Maven 阿里云镜像

在 `D:\Tools\Maven\apache-maven-3.9.x\conf\settings.xml` 中添加：
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
    <mirrorOf>central</mirrorOf>
  </mirror>
</mirrors>
```

---

### 问题 2：Redis 连接失败

```bash
redis-server --daemonize yes
redis-cli ping
```

---

### 问题 3：MySQL 连接被拒绝

```bash
net start mysql80
mysql -u root -p
# 输入 main0126
```

---

### 问题 4：端口被占用

```bash
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F
```

---

### 问题 5：npm install 失败

```bash
npm cache clean --force
npm install --registry=https://registry.npmmirror.com
```

---

## 📍 十一、项目地址

```
GitHub: https://github.com/YUGUO-YU/legal-assistant
```

---

## 📞 十二、技术支持

如有问题，请检查：
1. 环境变量是否配置正确
2. MySQL 和 Redis 是否已启动
3. 数据库是否已创建
4. 端口是否被占用

---

**安装成功！享受法律助手！⚖️**