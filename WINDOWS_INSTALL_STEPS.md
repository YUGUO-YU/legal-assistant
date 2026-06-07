# Windows 完整安装测试步骤

## 第一步：下载安装包

### 必须下载的软件（按顺序）

| 序号 | 软件 | 下载地址 | 安装包大小 |
|------|------|---------|-----------|
| 1 | Java JDK 17 | https://adoptium.net/temurin/releases/?version=17 | ~200MB |
| 2 | Maven 3.9 | https://maven.apache.org/download.cgi | ~10MB |
| 3 | Node.js | https://nodejs.org/ (LTS) | ~30MB |
| 4 | MySQL 8.0 | https://dev.mysql.com/downloads/installer/ | ~400MB |
| 5 | Redis | https://github.com/microsoftarchive/redis/releases | ~5MB |

**建议下载到 D:\install 目录，便于管理**

---

## 第二步：安装 Java JDK 17

### 2.1 安装

1. 双击下载的 `.msi` 文件
2. 点击 Next
3. 安装路径：`D:\Tools\Java\jdk-17.x.x`
4. 点击 Next -> Install
5. 等待安装完成，点击 Finish

### 2.2 配置环境变量

1. 右键「此电脑」-> 属性 -> 高级系统设置
2. 点击「环境变量」
3. 在「系统变量」中新建：
   ```
   JAVA_HOME = D:\Tools\Java\jdk-17.x.x
   ```
4. 在「系统变量」中找到 `Path`，添加：
   ```
   %JAVA_HOME%\bin
   ```

### 2.3 验证

打开 CMD，输入：
```cmd
java -version
```
应显示 `openjdk version "17.x.x"`

---

## 第三步：安装 Maven

### 3.1 安装

1. 下载 `apache-maven-3.9.x-bin.zip`
2. 解压到 `D:\Tools\apache-maven-3.9.x`

### 3.2 配置环境变量

在系统变量中新建：
```
MAVEN_HOME = D:\Tools\apache-maven-3.9.x
```

在 Path 中添加：`%MAVEN_HOME%\bin`

### 3.3 验证

```cmd
mvn -version
```

---

## 第四步：安装 Node.js

### 4.1 安装

1. 下载 LTS 版本（18.x 或 20.x）
2. 双击 `.msi` 文件
3. 安装路径：`D:\Tools\Nodejs`
4. 勾选「自动安装必要的工具」
5. 点击 Install

### 4.2 验证

```cmd
node -v
npm -v
```

---

## 第五步：安装 MySQL

### 5.1 安装

1. 双击 `mysql-installer-community-8.x.x.msi`
2. 选择「自定义安装」
3. 勾选：
   - MySQL Server 8.0.x
   - MySQL Workbench（可视化工具）
4. 点击 Next -> Execute
5. 等待下载和安装完成

### 5.2 配置

1. 选择「Standalone MySQL Server」
2. Port: 3306
3. 设置 root 密码：`main0126`（重要！）
4. 点击 Next -> Execute

### 5.3 验证

打开 CMD：
```cmd
mysql -u root -p
# 输入密码: main0126
```

### 5.4 创建数据库

```sql
CREATE DATABASE legal_assistant DEFAULT CHARACTER SET utf8mb4;
EXIT;
```

---

## 第六步：安装 Redis

### 6.1 安装

1. 下载 `Redis-x64-3.0.504.msi`
2. 双击安装
3. 安装路径：`D:\Tools\Redis`
4. 勾选「添加 Redis 到 PATH」
5. 点击 Install

### 6.2 启动 Redis

```cmd
redis-server --daemonize yes
```

### 6.3 验证

```cmd
redis-cli ping
# 应返回: PONG
```

---

## 第七步：下载项目

### 7.1 方式一：Git 克隆（推荐）

```cmd
git clone https://github.com/YUGUO-YU/legal-assistant.git
cd legal-assistant
```

### 7.2 方式二：下载 ZIP

1. 打开 https://github.com/YUGUO-YU/legal-assistant
2. 点击绿色的「Code」按钮
3. 点击「Download ZIP」
4. 解压到 `D:\Projects\legal-assistant`

---

## 第八步：配置项目

### 8.1 修改数据库密码

打开文件：
```
D:\Projects\legal-assistant\server\src\main\resources\application.yml
```

找到 `password: main0126`，确认密码正确。

### 8.2 创建目录

```cmd
cd D:\Projects\legal-assistant
mkdir server\templates
mkdir server\output
```

---

## 第九步：编译后端

### 9.1 编译

```cmd
cd D:\Projects\legal-assistant\server
mvn clean package -DskipTests
```

**等待时间：5-10 分钟（首次编译需要下载依赖）**

### 9.2 验证编译成功

```cmd
dir target\*.jar
```

应看到 `legal-assistant-1.0.0.jar`

---

## 第十步：安装前端依赖

### 10.1 安装

```cmd
cd D:\Projects\legal-assistant\client
npm install
```

**等待时间：3-5 分钟**

---

## 第十一步：启动服务

### 11.1 启动 MySQL

（如果 MySQL 服务未启动）

```cmd
net start mysql80
```

### 11.2 启动 Redis

```cmd
redis-server --daemonize yes
```

### 11.3 启动后端

新开一个 CMD 窗口：

```cmd
cd D:\Projects\legal-assistant\server
java -jar target\legal-assistant-1.0.0.jar
```

**等待出现 `Started LegalAssistantApplication` 表示启动成功**

### 11.4 启动前端

再新开一个 CMD 窗口：

```cmd
cd D:\Projects\legal-assistant\client
npm run dev
```

**等待出现 `ready` 表示启动成功**

---

## 第十二步：访问测试

打开浏览器访问：

```
http://localhost:5173
```

---

## 完整命令汇总

复制以下内容，保存为 `install.bat`，双击运行：

```batch
@echo off
echo ========================================
echo   法律助手 - Windows 一键安装脚本
echo ========================================

echo.
echo [1/7] 检查 Java...
java -version

echo.
echo [2/7] 检查 Maven...
mvn -version

echo.
echo [3/7] 检查 Node.js...
node -v

echo.
echo [4/7] 启动 MySQL 服务...
net start mysql80 2>nul
if %errorlevel% neq 0 echo MySQL 服务已启动或未安装

echo.
echo [5/7] 启动 Redis 服务...
redis-server --daemonize yes 2>nul
echo Redis 已启动

echo.
echo [6/7] 编译后端...
cd server
call mvn clean package -DskipTests
cd ..

echo.
echo [7/7] 安装前端依赖...
cd client
call npm install
cd ..

echo.
echo ========================================
echo   安装完成！
echo ========================================
echo.
echo 下一步：
echo   1. 启动后端: java -jar server\target\legal-assistant-1.0.0.jar
echo   2. 启动前端: cd client ^&^& npm run dev
echo   3. 访问: http://localhost:5173
echo.
pause
```

---

## 常见问题解决

### 问题 1：编译失败，提示网络错误

**解决**：配置 Maven 镜像

在 `D:\Tools\apache-maven-3.9.x\conf\settings.xml` 中添加：

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

### 问题 2：Redis 连接失败

**解决**：
```cmd
redis-server --daemonize yes
redis-cli ping
```

### 问题 3：MySQL 连接被拒绝

**解决**：
```cmd
net start mysql80
mysql -u root -p
# 输入密码: main0126
```

### 问题 4：端口被占用

**解决**：关闭占用端口的程序
```cmd
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F
```

---

## 快速验证清单

完成安装后，逐项检查：

```
[ ] java -version 显示 17.x
[ ] mvn -version 显示 3.9.x
[ ] node -v 显示 18.x 或 20.x
[ ] mysql -u root -p 能连接
[ ] redis-cli ping 返回 PONG
[ ] server/target/legal-assistant-1.0.0.jar 存在
[ ] client/node_modules 目录存在
[ ] http://localhost:5173 能访问
```

---

**安装成功！享受法律助手带来的便捷体验！**