# 🍜 小食候 · AI 美食推荐 Agent

一个基于 **Spring Boot 3 + Spring AI** 的对话式美食推荐助手。用户给出模糊输入（有什么食材 / 想吃什么口味），Agent 通过多轮追问收敛需求，最终给出**推荐菜 + 食谱 + 材料清单 + 感受记录**的完整闭环。

---

## ✨ 功能特性

### 🗣️ 对话式推荐
- 用户输入模糊需求，如"我有牛肉"、"想吃点清淡的"
- Agent 自动追问缺失信息，一次只问一个
- 收敛后从菜谱库推荐最匹配的菜品

### 🎯 槽位驱动的对话收敛
- **P0 必填槽位**：食材、口味（代码强制检查）
- **P1-P3 可选槽位**：忌口、主食、配菜、人数、厨具、可用时间、难度、场景
- 模型按上下文动态决定追问哪些可选槽位

### 🔧 Function Calling 工具集
| 工具 | 职责 |
|------|------|
| `FoodRecommendTools` | 根据食材和口味推荐菜品 |
| `RecipeTools` | 生成详细食谱（步骤、火候、时间、翻车点） |
| `ShoppingTools` | 生成采购清单 |
| `FeelTools` | 记录用户对菜品的感受 |

### 📚 RAG 菜谱检索
- 基于 `SimpleVectorStore` + 百炼 `text-embedding-v4` 构建内存向量库
- 启动时从 MySQL `recipe` 表读取菜谱 → embedding → 持久化到 JSON
- 通过 `QuestionAnswerAdvisor` 自动注入检索结果到对话上下文

### 💬 多轮对话记忆
- 自定义 `ChatMemory` 实现，基于 MyBatis-Plus 持久化到 `message` 表
- 保留最近 20 条消息作为上下文
- 支持跨轮次理解，如"太辣了" → "那换个清淡的"

### 🌊 SSE 流式输出
- 基于 Spring WebFlux 实现 `text/event-stream`
- 逐字输出，体验接近 DeepSeek / ChatGPT

### 🛡️ 范围限制
- System Prompt 严格限定在美食领域
- 对编程、闲聊、角色扮演等无关问题统一拒绝

---

## 🏗️ 技术栈

| 组件 | 版本 / 选型 |
|------|------------|
| JDK | 21 |
| Spring Boot | 3.5.15 |
| Spring AI | 1.1.8 |
| Spring AI Alibaba | 1.1.2.0 |
| Web 框架 | Spring WebFlux（SSE 流式） |
| ORM | MyBatis-Plus 3.5.9 |
| 数据库 | MySQL 8.x |
| 对话模型 | 百炼 `qwen-plus` |
| Embedding 模型 | 百炼 `text-embedding-v4`（1024 维） |
| 向量存储 | `SimpleVectorStore` + JSON 持久化 |
| 构建工具 | Maven |

---

## 📁 项目结构

```
shiyanshi/
├── data/
│   └── vector-store.json              # SimpleVectorStore 持久化文件（运行时生成）
├── src/main/
│   ├── java/com/gaoyu/shiyanshi/
│   │   ├── ShiyanshiApplication.java
│   │   ├── config/
│   │   │   ├── ChatClientConfig.java  # ChatClient + Advisor + Tools 装配
│   │   │   ├── ChatMemoryConfig.java  # ChatMemory Bean
│   │   │   └── VectorStoreConfig.java # SimpleVectorStore Bean
│   │   ├── controller/
│   │   │   └── ChatController.java    # SSE 流式对话入口
│   │   ├── agent/
│   │   │   ├── FoodAgent.java         # 对话编排核心
│   │   │   └── SlotManager.java       # 槽位抽取与更新
│   │   ├── tool/
│   │   │   ├── FoodRecommendTools.java
│   │   │   ├── RecipeTools.java
│   │   │   ├── ShoppingTools.java
│   │   │   └── FeelTools.java
│   │   ├── rag/
│   │   │   └── VectorStoreInitializer.java  # 启动时 embedding
│   │   ├── memory/
│   │   │   └── MyBatisPlusChatMemoryRepository.java
│   │   ├── service/
│   │   │   ├── RecipeService.java
│   │   │   ├── ShoppingService.java
│   │   │   ├── FeelService.java
│   │   │   └── ConversationService.java
│   │   ├── mapper/                    # 5 个 MyBatis-Plus Mapper
│   │   ├── entity/                    # 5 个数据库实体
│   │   ├── dto/
│   │   │   ├── SlotState.java
│   │   │   └── SlotDelta.java
│   │   └── common/
│   │       ├── result/R.java
│   │       └── exception/GlobalExceptionHandler.java
│   └── resources/
│       ├── application.yml
│       ├── prompts/system.st          # 外置系统提示词
│       └── static/index.html          # 对话前端
└── src/test/
```

---

## 🚀 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.x
- 阿里云百炼 API Key

### 2. 创建数据库

```sql
CREATE DATABASE shiyanshi DEFAULT CHARSET utf8mb4;
```

### 3. 建表

执行 `src/main/resources/db/schema.sql`，创建 5 张表：

```sql
-- recipe / conversation / message / feel_record / user_profile
```

### 4. 导入菜谱数据

从 [HowToCook](https://github.com/Anduin2017/HowToCook) 导入菜谱到 `recipe` 表，或执行 `src/main/resources/data/recipes-demo.sql` 先塞几条测试数据。

### 5. 配置 API Key

设置环境变量：

```bash
# Windows PowerShell
$env:AI_DASHSCOPE_API_KEY="sk-xxxxxxxx"

# Linux / macOS
export AI_DASHSCOPE_API_KEY="sk-xxxxxxxx"
```

### 6. 启动项目

```bash
mvn spring-boot:run
```

首次启动会：
1. 从 `recipe` 表读取菜谱
2. 调用百炼 embedding 模型（372 道约 1 分钟）
3. 生成 `data/vector-store.json`

日志出现以下内容表示成功：
```
向量库初始化完成，共 372 道菜谱
```

### 7. 访问前端

```
http://localhost:9090/index.html
```

---

## 🔌 API 接口

### 创建新对话

```http
POST /api/chat/new
```

**响应**：对话 ID
```
1
```

### SSE 流式对话

```http
GET /api/chat/stream?message=我有牛肉&conversationId=1
```

**参数**：
| 参数 | 必填 | 说明 |
|------|------|------|
| `message` | ✅ | 用户输入 |
| `conversationId` | ❌ | 对话 ID，省略则自动创建 |

**响应**：`text/event-stream` 流式数据

---

## 💡 使用示例

```
用户：我有牛肉
Agent：你想吃什么口味的？

用户：辣的
Agent：根据你提供的「牛肉」和「辣」口味，推荐：
       • 小炒黄牛肉（湘菜，困难，约 15 分钟）
       • 水煮牛肉（川菜，中等，约 30 分钟）
       • 孜然牛肉（荤菜，中等，约 35 分钟）
       需要哪道菜的食谱和材料清单？

用户：要小炒黄牛肉的食谱
Agent：【小炒黄牛肉】
       菜系：湘菜
       难度：困难
       时间：准备 10 分钟，烹饪 5 分钟
       份量：2 人份
       【材料】
       牛肉 200g、青椒 2 个、蒜 3 瓣...
       【步骤】
       1. 牛肉切片，加料酒、生抽、淀粉腌制 10 分钟
       2. 青椒切丝，蒜切末
       ...

用户：今天这个有点咸，但还不错
Agent：已记录你对「小炒黄牛肉」的感受，下次推荐会参考～
```

---

## 🧩 核心设计

### 槽位模型

```
P0（必填）：食材、口味
P1（优先）：忌口、主食、配菜
P2（有线索才问）：人数、厨具、可用时间
P3（几乎不问）：难度、场景
```

### 对话状态流转

```
COLLECTING（收集槽位）→ READY（P0 已满）→ DONE（已推荐）
```

### 数据流

```
用户消息 → ChatController
              ↓
         读 conversation.slots → 注入 prompt
         读 message（最近 20 条）→ 注入上下文
              ↓
         Agent 对话 / 追问 / 调工具
              ↓
         写 message（用户 + 助手消息）
         更新 conversation.slots
              ↓
         P0 满 → FoodRecommendTools → RAG 检索 recipe 向量
              ↓
         推荐 → 问是否需要食谱 → RecipeTools + ShoppingTools
              ↓
         用户反馈 → FeelTools → feel_record 表
```

---

## 📊 数据库表

| 表名 | 用途 |
|------|------|
| `recipe` | 菜谱源数据（RAG 的源） |
| `conversation` | 对话记录 + 槽位状态（JSON 字段） |
| `message` | 对话消息持久化（ChatMemory 的数据源） |
| `feel_record` | 感受记录（评分、优点、改进点、情感标签） |
| `user_profile` | 用户画像（口味偏好、忌口、饮食目标） |

---

## 🔧 扩展方向

- [ ] **Multi-Agent**：Planner 拆解"今晚三菜一汤"为 DAG 子任务，Worker 并行查菜谱，Reviewer 校验营养均衡
- [ ] **MCP 协议**：接入美团 / 盒马 MCP Server，实现一键加购物车
- [ ] **Agentic Search**：基于 ripgrep 实现实时精确检索
- [ ] **关系图谱**：食材-菜品-口味 AST 关系图谱
- [ ] **用户系统**：接入登录，让 `user_id` 生效
- [ ] **长期记忆**：把口味偏好写入 `user_profile`，反哺推荐

---

## 📄 License

MIT

---

## 🙏 致谢

- [Spring AI](https://spring.io/projects/spring-ai)
- [Spring AI Alibaba](https://github.com/alibaba/spring-ai-alibaba)
- [HowToCook](https://github.com/Anduin2017/HowToCook) 菜谱数据来源
- [阿里云百炼](https://bailian.console.aliyun.com/) 模型服务

---

**「小食候」—— 每一餐，都值得被记住。**