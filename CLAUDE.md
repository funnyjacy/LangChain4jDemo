# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 常用命令

```bash
# 运行项目（需先设置环境变量 API_KEY 和启动 Redis）
mvn spring-boot:run

# 编译
mvn compile

# 运行测试
mvn test

# 打包
mvn package -DskipTests
```

## 环境准备

运行前必须满足两个前提：

1. **Redis**：聊天记忆持久化依赖 Redis，默认连接 `localhost:6379`
   ```bash
   docker run -d -p 6379:6379 redis:7
   ```

2. **API Key**：通过环境变量 `API_KEY` 注入阿里云 DashScope 密钥，不可硬编码到配置文件
   ```bash
   # Windows PowerShell
   $env:API_KEY="your-api-key"
   ```

## 架构概览

项目是一个 Spring Boot + LangChain4j 的 RAG 聊天应用，使用阿里云通义千问（DashScope）作为 LLM 和 Embedding 模型后端。

**核心数据流：**
```
HTTP GET /chat?message=xxx&sessionId=xxx
  → ChatController
  → Assistant（@AiService，LangChain4j 自动实现）
      ├── ChatMemoryProvider（按 sessionId 隔离，Redis 持久化）
      └── ContentRetriever（RAG：向量检索 university/*.md 知识库）
  → DashScope qwen-plus
```

**关键设计决策：**

- `Assistant` 是一个 LangChain4j `@AiService` 接口，框架在启动时自动生成实现类并注入 Spring 容器，无需手动实现
- `@MemoryId String sessionId` 参数使每个 session 拥有独立的对话记忆，前端通过 `sessionStorage` 管理 sessionId 实现多标签页隔离
- RAG 知识库在应用启动时一次性加载（`RagConfig.universityEmbeddingStore`），存储在内存中（`InMemoryEmbeddingStore`），重启后重新构建
- 聊天记忆存储在 Redis（`RedisChatMemoryStore`），重启后不丢失
- System Prompt 通过 `@SystemMessage(fromResource = "personas/girlfriend.md")` 从 classpath 资源文件加载

**配置文件：**
- `application.yml`：实际配置（含 `${API_KEY}` 占位符）
- `application-example.yml`：配置说明模板，不含敏感信息

## RAG 知识库扩展

在 `src/main/resources/university/` 下新增 `.md` 文件即可自动加入知识库，无需修改代码。切割参数：每段最大 500 字符，重叠 50 字符；检索参数：最多返回 5 条，最低相似度 0.6。

## 角色人设扩展

`src/main/resources/personas/` 下存放 System Prompt 文件。如需切换角色，修改 `Assistant.java` 中 `@SystemMessage` 的 `fromResource` 路径即可。
