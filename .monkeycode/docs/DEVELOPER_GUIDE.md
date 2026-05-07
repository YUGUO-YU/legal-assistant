# 法律助手小程序开发指南

## 1. 环境准备

### 1.1 开发环境要求

| 工具 | 版本要求 | 说明 |
|-----|---------|------|
| JDK | 17+ | 后端运行时 |
| Maven / Gradle | 3.8+ / 8.0+ | 包管理器 |
| Git | 2.0+ | 版本控制 |
| Docker | 24.0+ | 容器化环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.0+ | 缓存 |
| IDEA | 最新版 | 推荐 IDE |

### 1.2 开发工具安装

```bash
# 安装 JDK 17
sudo apt update
sudo apt install openjdk-17-jdk

# 验证 Java 版本
java -version

# 安装 Maven
sudo apt install maven

# 安装 Docker
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 1.3 IDE 插件推荐

#### IDEA 插件

- Spring Boot Assistant - Spring Boot 支持
- MyBatisX - MyBatis 映射文件跳转
- Maven Helper - Maven 依赖分析
- Lombok - 简化 Java 代码
- SonarLint - 代码质量检查

---

## 2. 项目初始化

### 2.1 克隆项目

```bash
git clone https://github.com/your-org/legal-assistant.git
cd legal-assistant
```

### 2.2 环境变量配置

```bash
# 复制环境变量模板
cp server/src/main/resources/application.example.yml server/src/main/resources/application.yml

# 编辑环境变量
vim server/src/main/resources/application.yml
```

#### 环境变量说明

```yaml
# 应用配置
server:
  port: 8080

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/legal_assistant?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your-password

# Redis 配置
  redis:
    host: localhost
    port: 6379
    password:

# JWT 配置
jwt:
  secret: your-secret-key
  expiration: 604800000
  refresh-expiration: 2592000000

# 微信小程序配置
wechat:
  app-id: your-app-id
  app-secret: your-app-secret

# 短信服务配置
sms:
  secret-id: your-secret-id
  secret-key: your-secret-key
  app-id: your-app-id
  template-id: your-template-id

# 对象存储配置
cos:
  secret-id: your-secret-id
  secret-key: your-secret-key
  bucket: your-bucket
  region: ap-guangzhou

# 第三方 API 配置
third-party:
  api-key: your-api-key
```

### 2.3 启动基础设施

```bash
# 启动 MySQL 和 Redis
docker-compose up -d mysql redis

# 等待服务启动
sleep 10

# 初始化数据库（执行 SQL 脚本）
mysql -u root -p < scripts/init.sql
```

---

## 3. 后端开发 (Spring Boot)

### 3.1 项目结构

```
server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/legal/assistant/
│   │   │       ├── LegalAssistantApplication.java  # 启动类
│   │   │       │
│   │   │       ├── config/                         # 配置类
│   │   │       │   ├── SecurityConfig.java          # 安全配置
│   │   │       │   ├── RedisConfig.java            # Redis 配置
│   │   │       │   ├── CorsConfig.java             # 跨域配置
│   │   │       │   └── WebConfig.java               # Web 配置
│   │   │       │
│   │   │       ├── module/                         # 功能模块
│   │   │       │   ├── auth/                       # 认证模块
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   └── AuthController.java
│   │   │       │   │   ├── service/
│   │   │       │   │   │   ├── AuthService.java
│   │   │       │   │   │   └── AuthServiceImpl.java
│   │   │       │   │   ├── mapper/
│   │   │       │   │   │   └── AuthMapper.java
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   └── User.java
│   │   │       │   │   └── dto/
│   │   │       │   │       ├── LoginRequest.java
│   │   │       │   │       └── LoginResponse.java
│   │   │       │   │
│   │   │       │   ├── user/                       # 用户模块
│   │   │       │   ├── document/                   # 文书模块
│   │   │       │   ├── case/                       # 案例模块
│   │   │       │   ├── law/                        # 法规模块
│   │   │       │   ├── company/                   # 企业模块
│   │   │       │   └── lead/                       # 案源模块
│   │   │       │
│   │   │       ├── common/                         # 公共模块
│   │   │       │   ├── result/                     # 统一返回
│   │   │       │   │   ├── Result.java
│   │   │       │   │   └── ResultCode.java
│   │   │       │   ├── exception/                  # 异常处理
│   │   │       │   │   ├── GlobalExceptionHandler.java
│   │   │       │   │   └── BusinessException.java
│   │   │       │   ├── security/                   # 安全相关
│   │   │       │   │   ├── JwtTokenProvider.java
│   │   │       │   │   ├── JwtAuthenticationFilter.java
│   │   │       │   │   └── UserDetailsServiceImpl.java
│   │   │       │   └── utils/                      # 工具类
│   │   │       │       ├── AesUtil.java
│   │   │       │       ├── RedisUtil.java
│   │   │       │       └── IpUtil.java
│   │   │       │
│   │   │       └── dto/                            # 全局 DTO
│   │   │
│   │   └── resources/
│   │       ├── application.yml                      # 主配置文件
│   │       ├── application-dev.yml                  # 开发环境
│   │       ├── application-prod.yml                 # 生产环境
│   │       └── mapper/                             # MyBatis XML
│   │           ├── auth/
│   │           │   └── AuthMapper.xml
│   │           └── user/
│   │               └── UserMapper.xml
│   │
│   └── test/                                       # 测试文件
│       └── java/
│           └── com/legal/assistant/
│               └── LegalAssistantApplicationTests.java
│
├── pom.xml                                         # Maven 配置
└── scripts/
    └── init.sql                                     # 数据库初始化脚本
```

### 3.2 创建新模块

#### 3.2.1 模块结构示例

```java
// 1. Entity 实体类
package com.legal.assistant.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String phone;
    private String email;
    private String passwordHash;
    private String nickname;
    private String avatarUrl;

    @TableField("role")
    private String role;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

```java
// 2. Mapper 接口
package com.legal.assistant.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.legal.assistant.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

```java
// 3. Service 接口
package com.legal.assistant.module.user.service;

import com.legal.assistant.module.user.entity.User;

public interface UserService {
    User getUserById(String id);
    User getUserByPhone(String phone);
    void updateUser(User user);
}
```

```java
// 4. Service 实现
package com.legal.assistant.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.legal.assistant.module.user.entity.User;
import com.legal.assistant.module.user.mapper.UserMapper;
import com.legal.assistant.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getUserById(String id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }
}
```

```java
// 5. Controller
package com.legal.assistant.module.user.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.user.entity.User;
import com.legal.assistant.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByPhone(userDetails.getUsername());
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }
}
```

### 3.3 数据库操作

#### 3.3.1 MyBatis-Plus 常用操作

```java
// 条件查询
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getPhone, phone)
       .like(User::getNickname, keyword)
       .orderByDesc(User::getCreatedAt);
List<User> users = userMapper.selectList(wrapper);

// 分页查询
Page<User> page = new Page<>(1, 20);
IPage<User> result = userMapper.selectPage(page, wrapper);

// 插入
userMapper.insert(user);

// 更新
userMapper.updateById(user);

// 删除
userMapper.deleteById(id);
```

#### 3.3.2 数据库迁移

使用 MyBatis-Plus 的自动建表功能，或手动执行 SQL 脚本：

```bash
# 执行 SQL 脚本
mysql -u root -p legal_assistant < scripts/init.sql
```

### 3.4 API 开发规范

#### 3.4.1 RESTful 路由规范

| 操作 | HTTP 方法 | URL 命名 |
|-----|----------|---------|
| 列表 | GET | /resources |
| 详情 | GET | /resources/{id} |
| 创建 | POST | /resources |
| 更新 | PUT | /resources/{id} |
| 删除 | DELETE | /resources/{id} |

#### 3.4.2 统一响应格式

```java
// 成功响应
{
    "code": 0,
    "message": "success",
    "data": {}
}

// 错误响应
{
    "code": 4000,
    "message": "参数错误：手机号格式不正确",
    "data": null
}
```

```java
// 统一返回类
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

#### 3.4.3 错误码规范

| 错误码 | 说明 |
|-------|------|
| 0 | 成功 |
| 1000 | 系统内部错误 |
| 2000 | 认证错误 |
| 3000 | 权限不足 |
| 4000 | 参数校验失败 |
| 5000 | 业务逻辑错误 |
| 6000 | 资源不存在 |

### 3.5 运行测试

```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest=*ServiceTest

# 运行 e2e 测试
mvn test -Dtest=*ControllerTest

# 查看测试覆盖率
mvn test jacoco:report
```

---

## 4. 前端开发

### 4.1 项目结构

```
client/
├── src/
│   ├── pages/                 # 页面
│   │   ├── index/            # 首页
│   │   ├── auth/             # 认证相关
│   │   ├── document/         # 文书模块
│   │   ├── case/             # 案例模块
│   │   ├── law/              # 法规模块
│   │   ├── company/          # 企业模块
│   │   ├── lead/             # 案源模块
│   │   └── user/             # 用户中心
│   ├── components/           # 组件
│   │   ├── common/           # 通用组件
│   │   ├── document/         # 文书组件
│   │   ├── case/             # 案例组件
│   │   └── ...
│   ├── stores/               # Pinia 状态
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   └── ...
│   ├── services/             # API 服务
│   │   ├── api.ts            # API 封装
│   │   ├── auth.ts
│   │   ├── document.ts
│   │   └── ...
│   ├── utils/                # 工具函数
│   ├── static/               # 静态资源
│   ├── App.vue
│   ├── main.ts
│   ├── pages.json             # 页面路由配置
│   └── manifest.json         # 小程序配置
├── public/                   # 公共资源
├── package.json
├── vite.config.ts
└── tsconfig.json
```

### 4.2 运行和调试

```bash
# 安装依赖
pnpm install

# 开发模式（小程序）
pnpm dev:mp-weixin

# 开发模式（H5）
pnpm dev:h5

# 构建
pnpm build:mp-weixin
pnpm build:h5
```

---

## 5. 代码规范

### 5.1 Git 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档变更
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具
```

### 5.2 Java 规范

- 遵循 Google Java Style Guide
- 类名使用 UpperCamelCase
- 方法名、变量名使用 lowerCamelCase
- 常量使用 UPPER_SNAKE_CASE
- 使用 Lombok 简化代码
- 使用 MyBatis-Plus LambdaQueryWrapper

### 5.3 前端规范

- TypeScript 启用严格模式
- 使用 interface 定义对象类型
- 使用 SCSS，采用 BEM 命名
- 移动端使用 rpx 单位

---

## 6. 测试

### 6.1 单元测试

```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testGetUserByPhone() {
        User user = userService.getUserByPhone("13800138000");
        assertNotNull(user);
        assertEquals("13800138000", user.getPhone());
    }
}
```

### 6.2 Controller 测试

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLogin() throws Exception {
        mockMvc.perform(post("/api/v1/auth/phone/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"phone\":\"13800138000\",\"code\":\"123456\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
```

---

## 7. 部署

### 7.1 后端构建

```bash
# Maven 构建
cd server
mvn clean package -DskipTests

# 构建 Docker 镜像
docker build -t legal-assistant-api -f docker/Dockerfile.api .
```

### 7.2 前端构建

```bash
# 前端构建
cd ../client
pnpm build:mp-weixin
pnpm build:h5
```

### 7.3 Docker 部署

```bash
# 一键启动所有服务
docker-compose up -d
```

### 7.4 小程序发布

1. 登录微信公众平台
2. 上传代码包
3. 提交审核
4. 发布上线

---

## 8. 常见问题

### 8.1 数据库连接失败

```bash
# 检查 MySQL 是否运行
docker ps | grep mysql

# 检查端口
netstat -an | grep 3306

# 测试连接
mysql -h localhost -P 3306 -u root -p
```

### 8.2 Redis 连接失败

```bash
# 检查 Redis 是否运行
docker ps | grep redis

# 测试连接
redis-cli ping
```

### 8.3 第三方 API 调用失败

1. 检查 API Key 是否配置正确
2. 检查网络连通性
3. 查看日志确认错误信息
4. 联系第三方技术支持