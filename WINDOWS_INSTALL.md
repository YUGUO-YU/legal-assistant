# Windows 安装测试指南

## 📋 目录

1. [安装前准备](#1-安装前准备)
2. [安装 Java JDK 17](#2-安装-java-jdk-17)
3. [安装 Maven](#3-安装-maven)
4. [安装 Node.js](#4-安装-nodejs)
5. [安装 Redis](#5-安装-redis)
6. [安装 MySQL](#6-安装-mysql)
7. [下载项目](#7-下载项目)
8. [配置与编译](#8-配置与编译)
9. [启动服务](#9-启动服务)
10. [常见问题](#10-常见问题)

---

## 1. 安装前准备

### 1.1 下载工具

需要下载以下软件：
- Java JDK 17: https://adoptium.net/temurin/releases/?version=17
- Maven: https://maven.apache.org/download.cgi
- Node.js: https://nodejs.org/ (选 LTS 版本)
- Redis: https://github.com/microsoftarchive/redis/releases
- MySQL: https://dev.mysql.com/downloads/installer/

### 1.2 检查是否已安装

打开 PowerShell 或 CMD：

```powershell
# 检查 Java
java -version

# 检查 Maven
mvn -version

# 检查 Node
node -v
npm -v
```

---

## 2. 安装 Java JDK 17

### 2.1 下载安装

1. 访问 https://adoptium.net/temurin/releases/?version=17
2. 下载 `jdk-17.x.x+x_windows_x64_hotspot.msi`
3. 双击运行安装程序
4. 安装路径建议：`C:\Program Files\Eclipse Adoptium\jdk-17.x.x+x`

### 2.2 配置环境变量

```
JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17.x.x+x
Path 添加 = %JAVA_HOME%\bin
```

### 2.3 验证安装

```powershell
java -version
# 应显示: openjdk version "17.x.x" ...
```

---

## 3. 安装 Maven

### 3.1 下载安装

1. 访问 https://maven.apache.org/download.cgi
2. 下载 `apache-maven-3.9.x-bin.zip`
3. 解压到 `C:\tools\apache-maven-3.9.x`

### 3.2 配置环境变量

```
MAVEN_HOME = C:\tools\apache-maven-3.9.x
Path 添加 = %MAVEN_HOME%\bin
```

### 3.3 验证安装

```powershell
mvn -version
# 应显示: Apache Maven 3.9.x ...
```

---

## 4. 安装 Node.js

### 4.1 下载安装

1. 访问 https://nodejs.org/
2. 下载 LTS 版本（18.x 或 20.x）
3. 双击运行安装程序
4. 安装路径默认即可

### 4.2 验证安装

```powershell
node -v
npm -v
```

---

## 5. 安装 Redis

### 5.1 下载安装

1. 访问 https://github.com/microsoftarchive/redis/releases
2. 下载 `Redis-x64-3.0.504.msi` 或最新版本
3. 双击运行安装程序
4. 安装路径建议：`C:\tools\Redis`

### 5.2 启动 Redis

```powershell
# 启动 Redis 服务
redis-server --daemonize yes

# 测试连接
redis-cli ping
# 应返回: PONG
```

---

## 6. 安装 MySQL

### 6.1 下载安装

1. 访问 https://dev.mysql.com/downloads/installer/
2. 下载 MySQL Installer
3. 双击运行，选择「完全安装」
4. 设置 root 密码为：`main0126`

### 6.2 创建数据库

打开 MySQL Command Line Client 或 PowerShell：

```powershell
mysql -u root -p
# 输入密码: main0126

# 创建数据库
CREATE DATABASE legal_assistant DEFAULT CHARACTER SET utf8mb4;

# 退出
EXIT;
```

---

## 7. 下载项目

### 7.1 方式一：Git 克隆

```powershell
git clone https://github.com/YUGUO-YU/legal-assistant.git
cd legal-assistant
```

### 7.2 方式二：下载 ZIP

1. 访问 https://github.com/YUGUO-YU/legal-assistant
2. 点击 Code -> Download ZIP
3. 解压到 `C:\projects\legal-assistant`

---

## 8. 配置与编译

### 8.1 修改配置文件

编辑 `server/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    password: main0126  # 确认密码为 main0126
```

### 8.2 编译后端

打开 PowerShell，进入项目目录：

```powershell
cd C:\projects\legal-assistant\server

# 编译
mvn clean package -DskipTests

# 等待编译完成（约 5-10 分钟）
```

### 8.3 安装前端依赖

```powershell
cd ..\client

npm install
```

---

## 9. 启动服务

### 9.1 启动 Redis

```powershell
redis-server --daemonize yes
```

### 9.2 启动后端

```powershell
cd C:\projects\legal-assistant\server

java -jar target\legal-assistant-1.0.0.jar
```

等待看到 `Started LegalAssistantApplication` 后表示启动成功。

### 9.3 启动前端

新开一个 PowerShell 窗口：

```powershell
cd C:\projects\legal-assistant\client

npm run dev
```

### 9.4 访问测试

```
前端页面: http://localhost:5173
后端接口: http://localhost:8080
```

---

## 10. 常见问题

### Q1: java -version 显示版本不对

**解决**: 检查 JAVA_HOME 环境变量，指向正确的 JDK 17 目录。

### Q2: Maven 编译失败

**解决**: 
- 确保网络畅通
- 删除 `.m2` 缓存目录后重试
- 检查 Maven 配置

### Q3: Redis 连接失败

**解决**:
```powershell
# 检查 Redis 是否运行
redis-cli ping

# 如果没运行，启动它
redis-server --daemonize yes
```

### Q4: MySQL 连接失败

**解决**:
- 确认 MySQL 服务已启动
- 检查用户名密码是否正确
- 确认数据库已创建

### Q5: 前端 `npm install` 失败

**解决**:
```powershell
# 清除缓存
npm cache clean --force

# 使用淘宝镜像
npm install --registry=https://registry.npmmirror.com
```

---

## 🎉 快速安装脚本

复制以下内容保存为 `install.ps1`，右键以管理员运行：

```powershell
# PowerShell 快速安装脚本
# 保存为 install.ps1，右键点击"使用 PowerShell 运行"

Write-Host "=== 法律助手 Windows 快速安装 ===" -ForegroundColor Green

# 1. 检查 Java
Write-Host "`n检查 Java..." -ForegroundColor Yellow
java -version 2>$null
if ($LASTEXITCODE -ne 0) { Write-Host "请先安装 Java JDK 17" -ForegroundColor Red }

# 2. 检查 Maven  
Write-Host "`n检查 Maven..." -ForegroundColor Yellow
mvn -version 2>$null
if ($LASTEXITCODE -ne 0) { Write-Host "请先安装 Maven" -ForegroundColor Red }

# 3. 检查 Node
Write-Host "`n检查 Node.js..." -ForegroundColor Yellow
node -v 2>$null
if ($LASTEXITCODE -ne 0) { Write-Host "请先安装 Node.js" -ForegroundColor Red }

Write-Host "`n=== 基础环境检查完成 ===" -ForegroundColor Green
Write-Host "请手动启动 MySQL 和 Redis 服务" -ForegroundColor Cyan
Write-Host "然后运行: mvn clean package -DskipTests" -ForegroundColor Cyan
```

---

## 📞 验证命令汇总

```powershell
# 检查环境
java -version
mvn -version
node -v
redis-cli ping
mysql -u root -p -e "SHOW DATABASES;"

# 编译后端
cd server
mvn clean package -DskipTests

# 启动后端
java -jar target/legal-assistant-1.0.0.jar

# 启动前端
cd client
npm run dev
```

---

**安装完成后访问**: http://localhost:5173
