# 旅行助手系统架构图

## 整体架构概览

```mermaid
graph TB
    %% 用户端
    subgraph "用户端 Client Side"
        WEB["Web浏览器"]
        MOBILE["移动应用"]
        API_CLIENT["API客户端/测试工具"]
    end
    
    %% Railway云平台
    subgraph "Railway Cloud Platform"
        subgraph "应用服务 Application Service"
            APP["Spring Boot Application - travel-assistant:8080"]
        end
        
        subgraph "数据库服务 Database Service"
            DB[("MySQL Database - 用户数据 & 旅行计划")]
        end
        
        subgraph "网络服务 Network Service"
            DOMAIN["自定义域名 + SSL"]
        end
    end
    
    %% 外部服务（未来扩展）
    subgraph "外部服务 External Services - 未来扩展"
        MAP["地图服务 API"]
        WEATHER["天气服务 API"]
    end
    
    %% 连接关系
    WEB --> DOMAIN
    MOBILE --> DOMAIN
    API_CLIENT --> DOMAIN
    DOMAIN --> APP
    APP --> DB
    APP -.-> MAP
    APP -.-> WEATHER
    
    %% 样式
    classDef future fill:#f9f9f9,stroke:#999,stroke-dasharray: 5 5
    class MAP,WEATHER future
```

## 详细系统架构

```mermaid
graph TB
    %% 用户端
    subgraph "用户端 Client Side"
        WEB["Web浏览器"]
        MOBILE["移动应用"]
        API_CLIENT["API客户端"]
    end
    
    %% Railway部署环境
    subgraph "Railway Cloud Platform"
        %% Spring Boot应用详细结构
        subgraph "Spring Boot 应用 Application"
            subgraph "控制层 Controller Layer"
                AUTH_CTRL["AuthController - 用户认证"]
                USER_CTRL["UserController - 用户管理"] 
                PLAN_CTRL["TravelPlanController - 旅行计划管理"]
            end
            
            subgraph "安全层 Security Layer"
                JWT_FILTER["JWT认证过滤器 - JwtRequestFilter"]
                SEC_CONFIG["安全配置 - SecurityConfig"]
                JWT_PROVIDER["JWT令牌提供者 - JwtTokenProvider"]
            end
            
            subgraph "服务层 Service Layer"
                USER_SVC["UserService - 用户业务逻辑"]
                PLAN_SVC["TravelPlanService - 旅行计划业务逻辑"]
                USER_DETAILS["CustomUserDetailsService - 用户详情服务"]
            end
            
            subgraph "数据访问层 Repository Layer"
                USER_REPO["UserRepository - 用户数据访问"]
                PLAN_REPO["TravelPlanRepository - 旅行计划数据访问"]
            end
            
            HEALTH["/health - 健康检查端点"]
        end
        
        %% 数据库
        subgraph "MySQL 数据库"
            USERS_TABLE["users表 - 用户信息"]
            PLANS_TABLE["travel_plans表 - 旅行计划"]
            DAILY_TABLE["daily_plans表 - 每日计划"]
            TIPS_TABLE["travel_tips表 - 旅行贴士"]
        end
        
        %% 数据库迁移
        FLYWAY["Flyway数据库迁移 - 版本控制"]
    end
    
    %% 连接关系
    WEB --> AUTH_CTRL
    WEB --> USER_CTRL
    WEB --> PLAN_CTRL
    MOBILE --> AUTH_CTRL
    API_CLIENT --> HEALTH
    
    AUTH_CTRL --> JWT_FILTER
    USER_CTRL --> JWT_FILTER
    PLAN_CTRL --> JWT_FILTER
    
    JWT_FILTER --> SEC_CONFIG
    JWT_FILTER --> JWT_PROVIDER
    
    AUTH_CTRL --> USER_SVC
    USER_CTRL --> USER_SVC
    PLAN_CTRL --> PLAN_SVC
    
    USER_SVC --> USER_DETAILS
    USER_SVC --> USER_REPO
    PLAN_SVC --> PLAN_REPO
    
    USER_REPO --> USERS_TABLE
    PLAN_REPO --> PLANS_TABLE
    PLAN_REPO --> DAILY_TABLE
    PLAN_REPO --> TIPS_TABLE
    
    FLYWAY --> USERS_TABLE
    FLYWAY --> PLANS_TABLE
    FLYWAY --> DAILY_TABLE
    FLYWAY --> TIPS_TABLE
```

## 实体关系图 (ERD)

```mermaid
erDiagram
    USERS {
        bigint user_id PK
        varchar name UK
        varchar email UK
        varchar password
    }
    
    TRAVEL_PLANS {
        bigint id PK
        bigint user_id FK
        varchar title
        text overview
        int duration
        decimal total_budget
        decimal accommodation_budget
        decimal food_budget
        decimal transportation_budget
        decimal activities_budget
        decimal shopping_budget
        decimal other_budget
        timestamp created_at
        timestamp updated_at
    }
    
    DAILY_PLANS {
        bigint id PK
        bigint travel_plan_id FK
        int day
        varchar theme
        text morning
        text afternoon
        text evening
        varchar breakfast
        varchar lunch
        varchar dinner
        varchar accommodation
        text transportation_details
        decimal transportation_cost
        decimal daily_cost
    }
    
    TRAVEL_TIPS {
        bigint id PK
        bigint travel_plan_id FK
        text content
    }
    
    USERS ||--o{ TRAVEL_PLANS : creates
    TRAVEL_PLANS ||--o{ DAILY_PLANS : contains
    TRAVEL_PLANS ||--o{ TRAVEL_TIPS : includes
```

## CI/CD与部署架构

```mermaid
graph LR
    %% 开发流程
    subgraph "开发流程 Development Flow"
        DEV["开发者 - Developer"]
        LOCAL["本地开发环境"]
    end
    
    %% 代码仓库
    subgraph "GitHub Repository"
        REPO["travel-assistant 代码仓库"]
        MAIN["main分支"]
        PR["Pull Request"]
    end
    
    %% CI/CD自动化
    subgraph "GitHub Actions - CI/CD Pipeline"
        TRIGGER["触发器 - Push to main"]
        
        subgraph "测试阶段"
            CHECKOUT["代码检出"]
            JDK_SETUP["JDK 17环境"]
            TEST["运行单元测试"]
            REPORT["测试报告生成"]
        end
        
        subgraph "构建部署阶段"
            BUILD["Gradle构建"]
            DOCKER_BUILD["Docker镜像构建"]
            RAILWAY_DEPLOY["Railway自动部署"]
            HEALTH_CHECK["健康检查"]
        end
    end
    
    %% Railway生产环境
    subgraph "Railway Production Environment"
        APP_INSTANCE["Spring Boot实例"]
        DB_INSTANCE["MySQL数据库实例"]
        PUBLIC_URL["公网访问地址"]
    end
    
    %% 流程连接
    DEV --> LOCAL
    LOCAL --> REPO
    REPO --> MAIN
    PR --> MAIN
    MAIN --> TRIGGER
    TRIGGER --> CHECKOUT
    CHECKOUT --> JDK_SETUP
    JDK_SETUP --> TEST
    TEST --> REPORT
    REPORT --> BUILD
    BUILD --> DOCKER_BUILD
    DOCKER_BUILD --> RAILWAY_DEPLOY
    RAILWAY_DEPLOY --> APP_INSTANCE
    APP_INSTANCE --> DB_INSTANCE
    RAILWAY_DEPLOY --> HEALTH_CHECK
    HEALTH_CHECK --> PUBLIC_URL
```

## 技术栈总览

```mermaid
mindmap
  root((旅行助手 技术栈))
    后端技术
      Java 17
      Spring Boot 3.5.6
      Spring Security
      Spring Data JPA
      JWT认证
      Lombok
      Flyway数据库迁移
    数据库
      MySQL Railway托管
      H2测试环境
    构建与部署
      Gradle 8.14.3
      Gradle Wrapper
      Docker容器化
      GitHub Actions
      Railway云平台
    安全认证
      JWT Token
      BCrypt密码加密
      CORS配置
      Spring Security
    基础功能
      健康检查端点
      自动化测试
      数据库迁移
      RESTful API
```

## 简化部署架构图

```mermaid
graph TB
    %% 开发者
    DEV["👨‍💻 开发者"]
    
    %% GitHub
    subgraph "GitHub"
        REPO["📁 代码仓库 - travel-assistant"]
        ACTIONS["⚙️ GitHub Actions - 自动化CI/CD"]
    end
    
    %% Railway云平台 - 一体化解决方案
    subgraph "🚂 Railway Cloud Platform"
        subgraph "应用运行环境"
            APP_SVC["🏃‍♂️ Spring Boot 应用"]
            HEALTH["💓 健康检查 /health"]
        end
        
        subgraph "数据存储"
            DB_SVC["🗄️ MySQL 数据库"]
        end
        
        subgraph "网络接入"
            HTTPS["🔒 HTTPS + 自定义域名"]
        end
    end
    
    %% 用户
    subgraph "👥 最终用户"
        WEB_USER["🌐 Web用户"]
        MOBILE_USER["📱 移动用户"]
        API_USER["🔧 API测试用户"]
    end
    
    %% 连接关系
    DEV --> REPO
    REPO --> ACTIONS
    ACTIONS --> APP_SVC
    APP_SVC --> DB_SVC
    APP_SVC --> HEALTH
    
    WEB_USER --> HTTPS
    MOBILE_USER --> HTTPS
    API_USER --> HTTPS
    HTTPS --> APP_SVC
```

## 安全架构

```mermaid
graph TB
    %% 请求入口
    CLIENT["客户端请求"]
    
    %% 安全层
    subgraph "安全防护层 Security Layer"
        CORS["CORS跨域配置 - @CrossOrigin"]
        JWT_FILTER["JWT认证过滤器 - JwtRequestFilter"]
        AUTH_MANAGER["认证管理器 - AuthenticationManager"]
        USER_DETAILS["用户详情服务 - CustomUserDetailsService"]
    end
    
    %% 业务层
    subgraph "业务处理层 Business Layer"
        PROTECTED_API["受保护的API端点 - 需要认证"]
        PUBLIC_API["公开API端点 - /health, /auth/**"]
    end
    
    %% 数据层
    subgraph "数据访问层 Data Access Layer"
        USER_REPO_SEC["用户仓库 - 密码BCrypt加密"]
        DB_SEC["数据库连接 - 环境变量配置"]
    end
    
    %% 安全流程
    CLIENT --> CORS
    CORS --> JWT_FILTER
    JWT_FILTER --> AUTH_MANAGER
    AUTH_MANAGER --> USER_DETAILS
    USER_DETAILS --> USER_REPO_SEC
    
    JWT_FILTER --> PROTECTED_API
    CORS --> PUBLIC_API
    
    PROTECTED_API --> DB_SEC
    PUBLIC_API --> DB_SEC
```

## 核心功能模块

| 模块 | 功能描述 | 主要组件 | 现状 |
|------|----------|----------|------|
| 用户管理 | 用户注册、登录、JWT认证 | AuthController, UserService, SecurityConfig | ✅ 已实现 |
| 旅行计划 | 创建、查看、删除旅行计划 | TravelPlanController, TravelPlanService | ✅ 已实现 |
| 预算管理 | 预算分配和跟踪 | BudgetBreakdownDTO, 预算相关实体字段 | ✅ 已实现 |
| 每日行程 | 详细的每日旅行安排 | DailyPlan实体, DailyPlanDTO | ✅ 已实现 |
| 旅行贴士 | 个性化旅行建议 | TravelTip实体, 相关服务方法 | ✅ 已实现 |
| 数据持久化 | 数据库操作和迁移 | JPA Repositories, Flyway | ✅ 已实现 |
| 安全认证 | JWT令牌管理和权限控制 | Spring Security, JWT工具类 | ✅ 已实现 |
| CI/CD | 自动化测试、构建、部署 | GitHub Actions, Railway平台 | ✅ 已实现 |
| 健康检查 | 应用状态监控 | /health端点 | ✅ 已实现 |

## 项目特点

### ✅ **已实现的核心特性**
- **简洁架构**: 无复杂的网关、负载均衡器
- **云原生**: 基于Railway平台的一体化部署
- **自动化**: GitHub Actions完整的CI/CD流程
- **安全性**: JWT认证 + Spring Security
- **数据完整性**: Flyway数据库版本控制
- **容器化**: Docker部署，环境一致性

### 🎯 **架构优势**
- **开发简单**: 专注业务逻辑，无需复杂基础设施
- **部署便捷**: Railway一键部署，自动扩缩容
- **成本控制**: 按使用量付费，适合初期项目
- **快速迭代**: 自动化CI/CD，快速交付新功能

### 🚀 **未来扩展计划**
- **外部API集成**: 地图服务、天气API等
- **前端应用**: Web或移动端用户界面
- **缓存层**: Redis缓存提升性能
- **文件存储**: 图片、文档上传功能
