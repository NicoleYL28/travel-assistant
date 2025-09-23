# 旅行助手系统架构图

## 整体架构概览

```mermaid
graph TB
    %% 用户端
    subgraph "用户端 Client Side"
        WEB["Web浏览器 - 前端应用"]
        MOBILE["移动应用"]
        API_CLIENT["API客户端/测试工具"]
    end
    
    %% AI服务
    subgraph "AI服务 AI Services"
        COZE["Coze Agent API - 旅游方案生成"]
    end
    
    %% Railway云平台
    subgraph "Railway Cloud Platform"
        subgraph "应用服务 Application Service"
            APP["Spring Boot Application - travel-assistant:8080"]
        end
        
        subgraph "数据库服务 Database Service"
            DB[("MySQL Database - 用户数据 & 旅行计划")]
        end
    end
    
    %% 外部服务（未来扩展）
    subgraph "外部服务 External Services - 未来扩展"
        MAP["地图服务 API"]
        WEATHER["天气服务 API"]
    end
    
    %% 连接关系 - 体现业务流程
    WEB -->|1. 生成旅游方案| COZE
    COZE -->|2. 返回生成的方案| WEB
    WEB -->|3. 保存/管理方案| APP
    MOBILE --> COZE
    MOBILE --> APP
    API_CLIENT --> APP
    APP --> DB
    APP -.-> MAP
    APP -.-> WEATHER
    
    %% 样式
    classDef future fill:#f9f9f9,stroke:#999,stroke-dasharray: 5 5
    classDef ai fill:#e1f5fe,stroke:#0277bd,stroke-width:3px
    class MAP,WEATHER future
    class COZE ai
```

## 详细系统架构

```mermaid
graph TB
    %% 用户端
    subgraph "用户端 Client Side"
        WEB["Web浏览器 - 前端应用"]
        MOBILE["移动应用"]
        API_CLIENT["API客户端"]
    end
    
    %% AI服务层
    subgraph "AI服务层 AI Service Layer"
        COZE_API["Coze Agent API - AI旅游方案生成"]
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
    
    %% 业务流程连接关系
    WEB -->|1. 用户输入需求| COZE_API
    COZE_API -->|2. 生成旅游方案| WEB
    WEB -->|3. 用户认证| AUTH_CTRL
    WEB -->|4. 保存方案| PLAN_CTRL
    WEB -->|5. 用户管理| USER_CTRL
    
    MOBILE -->|AI方案生成| COZE_API
    MOBILE -->|数据交互| AUTH_CTRL
    API_CLIENT -->|健康检查| HEALTH
    
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
    
    %% 样式
    classDef ai fill:#e1f5fe,stroke:#0277bd,stroke-width:3px
    class COZE_API ai
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

## 业务流程架构

```mermaid
sequenceDiagram
    participant U as 👤 用户
    participant F as 🌐 前端应用
    participant C as 🤖 Coze Agent API
    participant B as 🏃‍♂️ Spring Boot 后端
    participant D as 🗄️ MySQL 数据库

    Note over U,D: 旅游方案生成与管理流程
    
    %% 用户注册登录
    U->>F: 1. 访问应用
    F->>B: 2. 用户注册/登录
    B->>D: 3. 验证用户信息
    D-->>B: 4. 返回用户数据
    B-->>F: 5. 返回JWT令牌
    
    %% AI方案生成
    U->>F: 6. 输入旅游需求
    F->>C: 7. 调用Coze Agent API
    Note over C: AI智能分析用户需求<br/>生成个性化旅游方案
    C-->>F: 8. 返回生成的旅游方案
    F-->>U: 9. 展示旅游方案
    
    %% 方案保存管理
    U->>F: 10. 选择保存方案
    F->>B: 11. 保存旅游方案 (JWT认证)
    B->>D: 12. 存储方案到数据库
    D-->>B: 13. 确认保存成功
    B-->>F: 14. 返回保存结果
    F-->>U: 15. 显示保存成功
    
    %% 方案查看管理
    U->>F: 16. 查看我的方案
    F->>B: 17. 获取用户方案列表
    B->>D: 18. 查询用户方案
    D-->>B: 19. 返回方案列表
    B-->>F: 20. 返回方案数据
    F-->>U: 21. 展示方案列表
```

## 技术栈总览

```mermaid
mindmap
  root((旅行助手 技术栈))
    AI服务
      Coze Agent API
      智能方案生成
      自然语言处理
      个性化推荐
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

## 分层技术架构图

```mermaid
graph TB
    %% 表现层 - Presentation Layer  
    subgraph "🎨 表现层 Presentation Layer"
        REACT["React前端应用<br/>- TagSelector<br/>- TripDetails<br/>- DayDetails<br/>- TripSummary"]
        MOBILE["移动端应用<br/>- 响应式设计<br/>- PWA支持"]
        BROWSER["Web浏览器<br/>- Chrome/Firefox<br/>- Safari/Edge"]
    end
    
    %% AI服务层 - AI Service Layer
    subgraph "🤖 AI服务层 AI Service Layer"
        COZE["Coze Agent API<br/>- 智能方案生成<br/>- 自然语言处理<br/>- 个性化推荐"]
        NLP["NLP引擎<br/>- 需求理解<br/>- 语义分析"]
        RECOM["推荐系统<br/>- 个性化算法<br/>- 用户画像"]
    end
    
    %% 网关层 - Gateway Layer  
    subgraph "🛡️ 网关层 Gateway Layer"
        CORS["CORS跨域配置<br/>- 前端域名白名单<br/>- 安全策略"]
        JWT_FILTER["JWT认证过滤器<br/>- Token验证<br/>- 权限检查"]
        SECURITY["Spring Security<br/>- 安全配置<br/>- 认证授权"]
    end
    
    %% 控制层 - Controller Layer
    subgraph "🎮 控制层 Controller Layer"
        AUTH_CTRL["AuthController<br/>- 用户登录注册<br/>- Token生成"]
        USER_CTRL["UserController<br/>- 用户信息管理<br/>- 个人资料"]
        TRAVEL_CTRL["TravelPlanController<br/>- 旅行计划CRUD<br/>- 方案管理"]
    end
    
    %% 服务层 - Service Layer
    subgraph "⚙️ 服务层 Service Layer"
        USER_SVC["UserService<br/>- 用户业务逻辑<br/>- 密码加密"]
        TRAVEL_SVC["TravelPlanService<br/>- 旅行计划逻辑<br/>- 数据处理"]
        SECURITY_SVC["CustomUserDetailsService<br/>- 用户详情加载<br/>- 权限管理"]
        JWT_PROVIDER["JwtTokenProvider<br/>- Token生成验证<br/>- 过期处理"]
    end
    
    %% 数据访问层 - Repository Layer
    subgraph "💾 数据访问层 Repository Layer"
        USER_REPO["UserRepository<br/>- 用户数据访问<br/>- JPA Repository"]
        TRAVEL_REPO["TravelPlanRepository<br/>- 旅行计划数据<br/>- 自定义查询"]
        JPA["Spring Data JPA<br/>- ORM映射<br/>- 事务管理"]
    end
    
    %% 数据库层 - Database Layer
    subgraph "🗄️ 数据库层 Database Layer"
        MYSQL["MySQL生产环境<br/>- Railway托管<br/>- 数据持久化"]
        H2["H2测试环境<br/>- 内存数据库<br/>- 单元测试"]
        FLYWAY["Flyway数据迁移<br/>- 版本控制<br/>- 自动化部署"]
    end
    
    %% 基础设施层 - Infrastructure Layer
    subgraph "🏗️ 基础设施层 Infrastructure Layer"
        RAILWAY["Railway云平台<br/>- 应用部署<br/>- 数据库托管"]
        DOCKER["Docker容器<br/>- 环境一致性<br/>- 容器化部署"]
        GITHUB["GitHub Actions<br/>- CI/CD流程<br/>- 自动化测试"]
        GRADLE["Gradle构建<br/>- 依赖管理<br/>- 项目构建"]
    end
    
    %% 分层连接关系
    REACT --> COZE
    MOBILE --> NLP
    BROWSER --> RECOM
    
    REACT --> CORS
    MOBILE --> JWT_FILTER
    BROWSER --> SECURITY
    
    COZE --> AUTH_CTRL
    NLP --> TRAVEL_CTRL
    CORS --> AUTH_CTRL
    JWT_FILTER --> USER_CTRL
    SECURITY --> TRAVEL_CTRL
    
    AUTH_CTRL --> USER_SVC
    USER_CTRL --> USER_SVC
    TRAVEL_CTRL --> TRAVEL_SVC
    AUTH_CTRL --> JWT_PROVIDER
    
    USER_SVC --> USER_REPO
    TRAVEL_SVC --> TRAVEL_REPO
    SECURITY_SVC --> JPA
    JWT_PROVIDER --> USER_REPO
    
    USER_REPO --> MYSQL
    TRAVEL_REPO --> H2
    JPA --> FLYWAY
    
    RAILWAY --> MYSQL
    DOCKER --> TRAVEL_SVC
    GITHUB --> GRADLE
    GRADLE --> DOCKER
    
    %% 样式定义
    classDef presentation fill:#e8f5e8,stroke:#4caf50,stroke-width:3px
    classDef ai fill:#e3f2fd,stroke:#2196f3,stroke-width:3px
    classDef gateway fill:#fff3e0,stroke:#ff9800,stroke-width:3px
    classDef controller fill:#f3e5f5,stroke:#9c27b0,stroke-width:3px
    classDef service fill:#e8f5e8,stroke:#4caf50,stroke-width:3px
    classDef repository fill:#e1f5fe,stroke:#03a9f4,stroke-width:3px
    classDef database fill:#ffebee,stroke:#f44336,stroke-width:3px
    classDef infrastructure fill:#f5f5f5,stroke:#607d8b,stroke-width:3px
    
    class REACT,MOBILE,BROWSER presentation
    class COZE,NLP,RECOM ai
    class CORS,JWT_FILTER,SECURITY gateway
    class AUTH_CTRL,USER_CTRL,TRAVEL_CTRL controller
    class USER_SVC,TRAVEL_SVC,SECURITY_SVC,JWT_PROVIDER service
    class USER_REPO,TRAVEL_REPO,JPA repository
    class MYSQL,H2,FLYWAY database
    class RAILWAY,DOCKER,GITHUB,GRADLE infrastructure
```

## 技术栈详细说明

### 🎨 表现层 (Presentation Layer)
| 技术组件 | 版本/说明 | 主要功能 | 文件位置 |
|---------|----------|----------|----------|
| **React** | 18.x | 前端框架，组件化开发 | `travel-assistant-frontend/src/` |
| **TagSelector** | 自研组件 | 旅游偏好标签选择 | `components/TagSelector.js` |
| **TripDetails** | 自研组件 | 旅行方案详情展示 | `pages/TripDetailsPage.js` |
| **DayDetails** | 自研组件 | 每日行程详情 | `components/DayDetails.js` |
| **TripSummary** | 自研组件 | 旅行方案概览 | `components/TripSummary.js` |

### 🤖 AI服务层 (AI Service Layer)
| 技术组件 | 版本/说明 | 主要功能 | 集成方式 |
|---------|----------|----------|----------|
| **Coze Agent API** | v1.0 | 智能旅游方案生成 | REST API调用 |
| **自然语言处理** | Coze内置 | 用户需求理解与分析 | 前端直接调用 |
| **个性化推荐** | AI算法 | 基于用户偏好推荐 | `clients/cozeClient.js` |
| **重新规划服务** | 自研 | AI方案重新生成 | `services/cozeReplanService.js` |

### 🛡️ 网关层 (Gateway Layer)
| 技术组件 | 版本/说明 | 主要功能 | 实现文件 |
|---------|----------|----------|----------|
| **CORS配置** | Spring Boot | 跨域资源共享 | `SecurityConfig.java` |
| **JWT过滤器** | 自研 | Token验证与权限检查 | `JwtAuthenticationFilter.java` |
| **Spring Security** | 6.x | 安全框架配置 | `SecurityConfig.java` |

### 🎮 控制层 (Controller Layer)
| 控制器 | 主要端点 | 功能描述 | 实现文件 |
|---------|----------|----------|----------|
| **AuthController** | `/auth/register`, `/auth/login` | 用户认证管理 | `AuthController.java` |
| **UserController** | `/users/**` | 用户信息管理 | `UserController.java` |
| **TravelPlanController** | `/travel-plans/**` | 旅行计划CRUD | `TravelPlanController.java` |

### ⚙️ 服务层 (Service Layer)
| 服务类 | 主要职责 | 核心方法 | 实现文件 |
|---------|----------|----------|----------|
| **UserService** | 用户业务逻辑 | 注册、登录、密码加密 | `UserService.java` |
| **TravelPlanService** | 旅行计划逻辑 | 创建、查询、删除方案 | `TravelPlanService.java` |
| **CustomUserDetailsService** | 用户详情服务 | 权限管理、用户加载 | `CustomUserDetailsService.java` |
| **JwtTokenProvider** | JWT令牌管理 | 生成、验证、过期处理 | `JwtTokenProvider.java` |

### 💾 数据访问层 (Repository Layer)
| 仓库接口 | 继承关系 | 主要功能 | 实现文件 |
|---------|----------|----------|----------|
| **UserRepository** | JpaRepository | 用户数据CRUD | `UserRepository.java` |
| **TravelPlanRepository** | JpaRepository | 旅行计划数据管理 | `TravelPlanRepository.java` |
| **Spring Data JPA** | 框架 | ORM映射、事务管理 | 自动配置 |

### 🗄️ 数据库层 (Database Layer)
| 数据库 | 环境 | 用途 | 配置文件 |
|---------|----------|----------|----------|
| **MySQL** | 生产环境 | Railway云平台托管 | `application.yml` |
| **H2** | 测试环境 | 内存数据库 | `application-test.yaml` |
| **Flyway** | 迁移工具 | 数据库版本控制 | `V1__create_users_table.sql` |

### 🏗️ 基础设施层 (Infrastructure Layer)
| 组件 | 版本 | 主要功能 | 配置文件 |
|---------|----------|----------|----------|
| **Railway云平台** | SaaS | 应用部署、数据库托管 | 环境变量 |
| **Docker** | 24.x | 容器化部署 | `Dockerfile` |
| **GitHub Actions** | CI/CD | 自动化测试、构建、部署 | `.github/workflows/` |
| **Gradle** | 8.14.3 | 构建工具、依赖管理 | `build.gradle` |

## 核心技术选型说明

### 🎯 **架构优势**
- **分层清晰**: 严格按照分层架构设计，职责明确
- **技术现代**: 采用Spring Boot 3.x + React 18最新技术栈
- **AI驱动**: 核心功能基于Coze AI平台，智能化程度高
- **云原生**: 基于Railway平台，支持自动扩缩容
- **安全可靠**: JWT + Spring Security双重安全保障

### 🔧 **技术亮点**
- **前后端分离**: React前端专注用户体验，Spring Boot后端专注业务逻辑
- **AI集成**: 前端直接调用Coze API，减少后端AI处理复杂度
- **数据库迁移**: Flyway确保数据库版本一致性
- **容器化部署**: Docker保证环境一致性
- **自动化CI/CD**: GitHub Actions实现全流程自动化

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
    
    %% AI服务
    subgraph "🤖 Coze AI Platform"
        COZE_AGENT["🧠 Coze Agent - 旅游方案生成"]
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
    
    %% 用户业务流程
    WEB_USER -->|1. 生成方案| COZE_AGENT
    WEB_USER -->|2. 保存方案| APP_SVC
    MOBILE_USER -->|AI生成| COZE_AGENT
    MOBILE_USER -->|数据管理| APP_SVC
    API_USER --> APP_SVC
    
    %% 样式
    classDef ai fill:#e1f5fe,stroke:#0277bd,stroke-width:3px
    class COZE_AGENT ai
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
| **AI方案生成** | **Coze Agent智能生成旅游方案** | **前端调用Coze API** | **🔥 核心功能** |
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
- **AI驱动**: 前端集成Coze Agent API，智能生成个性化旅游方案
- **前后端分离**: 前端负责AI交互，后端专注数据管理
- **简洁架构**: 无复杂的网关、负载均衡器
- **云原生**: 基于Railway平台的一体化部署
- **自动化**: GitHub Actions完整的CI/CD流程
- **安全性**: JWT认证 + Spring Security
- **数据完整性**: Flyway数据库版本控制
- **容器化**: Docker部署，环境一致性

### 🎯 **架构优势**
- **智能化**: Coze AI提供专业的旅游方案生成能力
- **职责清晰**: 前端处理AI交互，后端负责数据持久化
- **开发简单**: 专注业务逻辑，无需复杂基础设施
- **部署便捷**: Railway一键部署，自动扩缩容
- **成本控制**: 按使用量付费，适合初期项目
- **快速迭代**: 自动化CI/CD，快速交付新功能

### 🔥 **核心业务流程**
1. **用户输入** → 前端收集旅游需求
2. **AI生成** → Coze Agent API智能生成方案
3. **用户确认** → 前端展示生成的方案
4. **数据保存** → 后端API保存用户选择的方案
5. **方案管理** → 后端提供CRUD操作

### 🚀 **未来扩展计划**
- **AI能力增强**: 集成更多AI服务，提升方案质量
- **实时更新**: WebSocket实现方案生成进度推送
- **外部API集成**: 地图服务、天气API、酒店预订等
- **移动端应用**: 原生移动应用开发
- **缓存层**: Redis缓存提升性能
- **文件存储**: 图片、文档上传功能
