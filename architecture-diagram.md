# 旅行助手系统架构图

## 整体架构概览

```mermaid
graph TB
    %% 前端层
    subgraph "前端层 (Frontend Layer)"
        FE[前端应用<br/>Vue.js/React<br/>或其他SPA框架]
    end
    
    %% API网关/负载均衡
    subgraph "网关层 (Gateway Layer)"
        LB[负载均衡器<br/>Load Balancer]
        GW[API Gateway<br/>Spring Cloud Gateway<br/>或 Nginx]
    end
    
    %% 应用层
    subgraph "应用层 (Application Layer)"
        APP[Spring Boot Application<br/>travel-assistant:8080]
    end
    
    %% 数据层
    subgraph "数据层 (Data Layer)"
        DB[(MySQL Database<br/>用户数据 & 旅行计划)]
    end
    
    %% 外部服务
    subgraph "外部服务 (External Services)"
        MAP[地图服务 API<br/>Google Maps/百度地图]
        WEATHER[天气服务 API<br/>OpenWeatherMap]
        PAYMENT[支付服务<br/>PayPal/Stripe]
    end
    
    %% 连接关系
    FE --> LB
    LB --> GW
    GW --> APP
    APP --> DB
    APP --> MAP
    APP --> WEATHER
    APP --> PAYMENT
```

## 详细系统架构

```mermaid
graph TB
    %% 客户端
    subgraph "客户端 (Client Side)"
        WEB[Web浏览器]
        MOBILE[移动应用]
        API_CLIENT[API客户端]
    end
    
    %% 应用服务层详细结构
    subgraph "Spring Boot 应用服务 (Application Services)"
        subgraph "控制层 (Controller Layer)"
            AUTH_CTRL[AuthController<br/>用户认证]
            USER_CTRL[UserController<br/>用户管理]
            PLAN_CTRL[TravelPlanController<br/>旅行计划管理]
        end
        
        subgraph "安全层 (Security Layer)"
            JWT_FILTER[JWT认证过滤器<br/>JwtRequestFilter]
            SEC_CONFIG[安全配置<br/>SecurityConfig]
            JWT_PROVIDER[JWT令牌提供者<br/>JwtTokenProvider]
        end
        
        subgraph "服务层 (Service Layer)"
            USER_SVC[UserService<br/>用户业务逻辑]
            PLAN_SVC[TravelPlanService<br/>旅行计划业务逻辑]
            USER_DETAILS[CustomUserDetailsService<br/>用户详情服务]
        end
        
        subgraph "数据访问层 (Repository Layer)"
            USER_REPO[UserRepository<br/>用户数据访问]
            PLAN_REPO[TravelPlanRepository<br/>旅行计划数据访问]
        end
        
        subgraph "数据传输对象 (DTOs)"
            LOGIN_DTO[UserLoginDTO]
            REGISTER_DTO[UserRegisterDTO]
            PLAN_DTO[TravelPlanRequestDTO]
            BUDGET_DTO[BudgetBreakdownDTO]
            DAILY_DTO[DailyPlanDTO]
        end
    end
    
    %% 数据库详细结构
    subgraph "数据库层 (Database Layer)"
        subgraph "MySQL数据库"
            USERS_TABLE[users表<br/>用户信息]
            PLANS_TABLE[travel_plans表<br/>旅行计划]
            DAILY_TABLE[daily_plans表<br/>每日计划]
            TIPS_TABLE[travel_tips表<br/>旅行贴士]
        end
        
        FLYWAY[Flyway数据库迁移<br/>版本控制]
    end
    
    %% 连接关系
    WEB --> AUTH_CTRL
    WEB --> USER_CTRL
    WEB --> PLAN_CTRL
    MOBILE --> AUTH_CTRL
    API_CLIENT --> AUTH_CTRL
    
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

## CI/CD流水线架构

```mermaid
graph LR
    %% 代码仓库
    subgraph "代码管理 (Source Control)"
        GH[GitHub Repository<br/>travel-assistant]
        MAIN[main分支]
        PR[Pull Request]
    end
    
    %% CI/CD流水线
    subgraph "CI/CD Pipeline (GitHub Actions)"
        subgraph "测试阶段 (Test Stage)"
            CHECKOUT[代码检出<br/>checkout@v4]
            JDK_SETUP[JDK 17设置<br/>setup-java@v4]
            CACHE[Gradle缓存<br/>cache@v4]
            TEST[运行测试<br/>./gradlew test]
            REPORT[测试报告<br/>test-reporter@v1]
        end
        
        subgraph "构建阶段 (Build Stage)"
            BUILD[构建应用<br/>./gradlew build]
            DOCKER_BUILD[Docker镜像构建]
        end
        
        subgraph "部署阶段 (Deploy Stage)"
            RAILWAY_CLI[Railway CLI安装]
            DEPLOY[部署到Railway<br/>railway up]
            HEALTH_CHECK[健康检查<br/>curl /health]
        end
    end
    
    %% 部署环境
    subgraph "部署环境 (Deployment Environments)"
        DEV[开发环境<br/>Railway Dev]
        STAGING[测试环境<br/>Railway Staging]
        PROD[生产环境<br/>Railway Prod]
    end
    
    %% 监控和日志
    subgraph "监控与日志 (Monitoring & Logging)"
        LOG[应用日志]
        METRICS[性能指标]
        ALERT[告警系统]
    end
    
    %% 流程连接
    GH --> MAIN
    PR --> MAIN
    MAIN --> CHECKOUT
    CHECKOUT --> JDK_SETUP
    JDK_SETUP --> CACHE
    CACHE --> TEST
    TEST --> REPORT
    REPORT --> BUILD
    BUILD --> DOCKER_BUILD
    DOCKER_BUILD --> RAILWAY_CLI
    RAILWAY_CLI --> DEPLOY
    DEPLOY --> HEALTH_CHECK
    HEALTH_CHECK --> DEV
    DEV --> STAGING
    STAGING --> PROD
    
    DEV --> LOG
    DEV --> METRICS
    METRICS --> ALERT
```

## 技术栈总览

```mermaid
mindmap
  root((旅行助手<br/>技术栈))
    后端技术
      Java 17
      Spring Boot 3.5.6
      Spring Security
      Spring Data JPA
      JWT认证
      Lombok
      Flyway数据库迁移
    数据库
      MySQL
      H2(测试环境)
    构建工具
      Gradle 8.14.3
      Gradle Wrapper
    容器化
      Docker
      Dockerfile
    CI/CD
      GitHub Actions
      Railway部署平台
      自动化测试
      健康检查
    安全
      JWT Token
      BCrypt密码加密
      CORS配置
      认证授权
    监控
      应用健康检查
      测试报告
      部署状态监控
```

## 部署架构图

```mermaid
graph TB
    %% 开发者
    DEV[开发者<br/>Developer]
    
    %% 代码仓库
    subgraph "GitHub"
        REPO[代码仓库<br/>travel-assistant]
    end
    
    %% CI/CD
    subgraph "GitHub Actions"
        PIPELINE[CI/CD流水线<br/>自动测试构建部署]
    end
    
    %% 云平台
    subgraph "Railway Cloud Platform"
        subgraph "应用服务"
            APP_SVC[Spring Boot 应用<br/>Port: 8080]
        end
        
        subgraph "数据库服务"
            DB_SVC[MySQL 数据库<br/>持久化存储]
        end
        
        subgraph "网络配置"
            DOMAIN[自定义域名]
            SSL[SSL证书]
            CDN[CDN加速]
        end
    end
    
    %% 用户
    subgraph "用户端 (End Users)"
        WEB_USER[Web用户]
        MOBILE_USER[移动用户]
        API_USER[API用户]
    end
    
    %% 连接关系
    DEV --> REPO
    REPO --> PIPELINE
    PIPELINE --> APP_SVC
    APP_SVC --> DB_SVC
    
    WEB_USER --> DOMAIN
    MOBILE_USER --> DOMAIN
    API_USER --> DOMAIN
    DOMAIN --> SSL
    SSL --> CDN
    CDN --> APP_SVC
```

## 安全架构

```mermaid
graph TB
    %% 请求入口
    CLIENT[客户端请求]
    
    %% 安全层
    subgraph "安全防护层 (Security Layer)"
        CORS[CORS跨域配置<br/>@CrossOrigin]
        JWT_FILTER[JWT认证过滤器<br/>JwtRequestFilter]
        AUTH_MANAGER[认证管理器<br/>AuthenticationManager]
        USER_DETAILS[用户详情服务<br/>CustomUserDetailsService]
    end
    
    %% 业务层
    subgraph "业务处理层 (Business Layer)"
        PROTECTED_API[受保护的API端点<br/>需要认证]
        PUBLIC_API[公开API端点<br/>/health, /auth/**]
    end
    
    %% 数据层
    subgraph "数据访问层 (Data Access Layer)"
        USER_REPO_SEC[用户仓库<br/>密码BCrypt加密]
        DB_SEC[数据库连接<br/>环境变量配置]
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

| 模块 | 功能描述 | 主要组件 |
|------|----------|----------|
| 用户管理 | 用户注册、登录、JWT认证 | AuthController, UserService, SecurityConfig |
| 旅行计划 | 创建、查看、删除旅行计划 | TravelPlanController, TravelPlanService |
| 预算管理 | 预算分配和跟踪 | BudgetBreakdownDTO, 预算相关实体字段 |
| 每日行程 | 详细的每日旅行安排 | DailyPlan实体, DailyPlanDTO |
| 旅行贴士 | 个性化旅行建议 | TravelTip实体, 相关服务方法 |
| 数据持久化 | 数据库操作和迁移 | JPA Repositories, Flyway |
| 安全认证 | JWT令牌管理和权限控制 | Spring Security, JWT工具类 |
| CI/CD | 自动化测试、构建、部署 | GitHub Actions, Railway平台 |

这个架构图展示了你的旅行助手应用的完整技术架构，包括：

1. **分层架构**: 控制层、服务层、仓库层、安全层
2. **数据模型**: 用户、旅行计划、每日计划、旅行贴士
3. **安全机制**: JWT认证、Spring Security配置
4. **CI/CD流程**: GitHub Actions自动化部署到Railway
5. **技术栈**: Spring Boot 3.x, MySQL, Docker, Gradle
6. **部署方式**: 容器化部署到云平台

这是一个现代化的、生产就绪的Spring Boot应用架构。
