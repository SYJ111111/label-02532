## How to Run

```bash
# 克隆项目
git clone <repository-url>
cd label-02532

# 启动所有服务
docker-compose up -d --build

# 等待服务启动完成后访问
# 管理后台: http://localhost:8081
# 用户端: http://localhost:8082
```

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| frontend-admin | 8081 | 博客管理后台 |
| frontend-user | 8082 | 博客用户端 |
| backend | 8080 | 后端 API 服务 |
| mysql | 3307 | MySQL 数据库 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user | user123 |

## 题目内容

通过JAVA写一个个人博客系统包括登录注册页面

---

# 个人博客系统

基于 Spring Boot 3 + Vue 3 + Element Plus 构建的全栈个人博客系统，包含管理后台和用户端双端架构。

### 功能特性

**管理后台 (frontend-admin)**
- 用户登录 / 注册
- 文章管理（增删改查、发布 / 草稿切换）
- 分类管理
- 用户管理
- 操作日志查看
- 个人信息管理

**用户端 (frontend-user)**
- 博客首页（文章列表）
- 文章详情阅读
- 分类筛选
- 用户登录 / 注册

**注册功能**
- 用户名：必填，3-20 个字符，不可重复
- 密码：必填，6-20 个字符
- 昵称：必填，2-30 个字符，不可重复
- 邮箱：选填，需符合邮箱格式

### 技术栈

- **后端**: Java 17 + Spring Boot 3.2 + MyBatis-Plus 3.5 + MySQL 8.0
- **前端**: Vue 3 + Vite + Element Plus + Pinia + Axios + SCSS
- **部署**: Docker + Docker Compose + Nginx

### 项目结构

```
label-02532/
├── README.md
├── docker-compose.yml
├── .gitignore
├── docs/
│   └── project_design.md
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/blog/
│       │   ├── BlogApplication.java
│       │   ├── config/
│       │   ├── common/
│       │   ├── entity/
│       │   ├── mapper/
│       │   ├── service/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── vo/
│       │   ├── util/
│       │   ├── interceptor/
│       │   ├── annotation/
│       │   ├── aspect/
│       │   ├── exception/
│       │   └── init/
│       └── resources/
│           ├── application.yml
│           └── schema.sql
├── frontend-admin/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/
│       ├── router/
│       ├── store/
│       ├── styles/
│       └── views/
└── frontend-user/
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/
        ├── router/
        ├── store/
        ├── styles/
        └── views/
```
