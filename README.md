# 个人博客系统

基于 Spring Boot 3 + Vue 3 + Element Plus 构建的全栈个人博客系统，包含管理后台和用户端双端架构。

## 题目内容
通过JAVA写一个个人博客系统包括登录注册页面


## 快速开始

### 环境要求

- Docker Desktop
  - macOS: 支持 Intel (x86_64) 和 Apple Silicon (M1/M2/M3/M4)
  - Windows: Windows 10/11 64-bit
  - Linux: 支持主流发行版

### 启动项目

**macOS / Linux:**
```bash
# 克隆项目
git clone <repository-url>
cd blog-system

# 启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

**Windows (PowerShell):**
```powershell
# 克隆项目
git clone <repository-url>
cd blog-system

# 启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 管理后台 | http://localhost:8081 | 管理员管理文章、用户、分类 |
| 用户端 | http://localhost:8082 | 用户浏览、写作文章 |
| 后端 API | http://localhost:8080 | RESTful API 服务 |
| MySQL | localhost:3307 | 数据库 |

### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user | user123 |


## 功能特性

### 管理后台 (frontend-admin)

- 用户登录 / 注册
- 文章管理（增删改查、审核发布）
- 分类管理（增删改查、排序）
- 用户管理（启用 / 禁用）
- 操作日志查看
- 个人信息管理

### 用户端 (frontend-user)

- 博客首页（文章列表、分类筛选）
- 文章详情阅读
- 用户登录 / 注册
- 文章写作（创建、编辑、删除）
- 我的文章管理
- 文章提交审核

### 文章审核流程

```
草稿(DRAFT) → 待审核(PENDING) → 已发布(PUBLISHED)
                    ↓
               已拒绝(REJECTED) → 重新提交
```

### 注册功能

- 用户名：必填，3-20 个字符，不可重复
- 密码：必填，6-20 个字符
- 昵称：必填，2-30 个字符，不可重复
- 邮箱：选填，需符合邮箱格式

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17 + Spring Boot 3.2.2 + MyBatis-Plus 3.5.5 + MySQL 8.0 |
| 前端 | Vue 3.4 + Vite 5 + Element Plus 2.5 + Pinia 2.1 + Axios 1.6 |
| 部署 | Docker + Docker Compose + Nginx |
| 认证 | JWT (jjwt 0.12.3) |

## 项目结构

```
├── backend/                    # 后端服务 (Spring Boot)
│   ├── src/main/java/com/blog/
│   │   ├── controller/         # 控制器
│   │   ├── service/            # 业务逻辑
│   │   ├── mapper/             # 数据访问
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── vo/                 # 视图对象
│   │   ├── config/             # 配置类
│   │   ├── interceptor/        # 拦截器
│   │   ├── annotation/         # 自定义注解
│   │   ├── aspect/             # AOP 切面
│   │   ├── init/               # 数据初始化
│   │   ├── util/               # 工具类
│   │   └── exception/          # 异常处理
│   └── src/main/resources/
│       ├── application.yml     # 应用配置
│       └── schema.sql          # 数据库脚本
├── frontend-admin/             # 管理后台 (Vue 3)
│   └── src/
│       ├── api/                # API 接口
│       ├── views/              # 页面组件
│       ├── router/             # 路由配置
│       ├── store/              # 状态管理
│       └── styles/             # 样式文件
├── frontend-user/              # 用户端 (Vue 3)
│   └── src/
│       ├── api/                # API 接口
│       ├── views/              # 页面组件
│       ├── router/             # 路由配置
│       ├── store/              # 状态管理
│       └── styles/             # 样式文件
├── docs/                       # 文档
│   └── project_design.md       # 设计文档
└── docker-compose.yml          # Docker 编排
```

## 常用命令

**启动/停止服务:**
```bash
# 启动服务
docker-compose up -d --build

# 停止服务
docker-compose down

# 清理数据重新开始
docker-compose down -v
docker-compose up -d --build
```

**查看日志:**
```bash
# 查看所有日志
docker-compose logs -f

# 查看指定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend-admin
docker-compose logs -f frontend-user
docker-compose logs -f mysql
```

**本地开发 (可选):**
```bash
# 后端 (需要 JDK 17 + Maven)
cd backend
mvn spring-boot:run

# 前端管理端 (需要 Node.js 18+)
cd frontend-admin
npm install
npm run dev

# 前端用户端
cd frontend-user
npm install
npm run dev
```

## 跨平台支持

项目使用的所有 Docker 镜像均支持多架构 (linux/amd64, linux/arm64)：

| 镜像 | 说明 |
|------|------|
| mysql:8.0 | 官方 MySQL 镜像，支持多架构 |
| maven:3.9-eclipse-temurin-17 | 构建阶段使用 |
| eclipse-temurin:17-jre | 后端运行时 |
| node:18-alpine | 前端构建阶段 |
| nginx:alpine | 前端运行时 |

在 macOS (Intel/Apple Silicon)、Windows、Linux 上均可直接运行，无需额外配置。

## 注意事项

- 首次启动需要等待 MySQL 初始化完成（约 30 秒）
- 如遇端口冲突，可修改 `docker-compose.yml` 中的端口映射
- 数据库数据持久化在 Docker volume `mysql-data` 中
