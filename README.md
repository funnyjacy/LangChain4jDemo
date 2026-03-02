# Spring 集成 LangChain4j Demo

基于 Spring Boot 与 LangChain4j 的演示项目，使用阿里云通义千问（DashScope）作为大语言模型后端。

## 技术栈

- **Spring Boot** 3.4.x
- **Java** 17
- **LangChain4j** 1.11.0
- **阿里云 DashScope**（通义千问）
- **Redis**（聊天记忆持久化）

## 快速开始

### 1. 启动 Redis

聊天记录使用 Redis 持久化，重启服务后不丢失。请先确保 Redis 已启动：

```bash
# Docker 方式
docker run -d -p 6379:6379 redis:7
```

默认连接 `localhost:6379`，可在 `application.yml` 中修改。

### 2. 获取 API Key

在 [阿里云百炼平台](https://bailian.console.aliyun.com/) 申请 DashScope API Key。

### 3. 配置 API Key（通过环境变量）

在 `application.yml` 中通过 `${API_KEY}` 引用本机环境变量，需预先设置：

```bash
# Linux / macOS
export API_KEY=your-api-key

# Windows PowerShell
$env:API_KEY="your-api-key"
```

### 4. 运行项目

```bash
mvn spring-boot:run
```

### 5. 测试接口

```bash
curl "http://localhost:8080/chat?message=请介绍一下你自己"
```

## 项目结构

```
src/main/java/com/example/langchain4jdemo/
├── Langchain4jDemoApplication.java    # 启动类
├── config/
│   └── ChatMemoryConfig.java          # Redis 持久化对话记忆
├── controller/
│   └── ChatController.java            # 使用 AiService 的聊天接口
└── aiservice/
    └── Assistant.java                 # @AiService 接口定义
```

## API 说明

| 接口 | 方法 | 说明 |
|------|------|------|
| `/chat?message=xxx` | GET | 使用声明式 AiService 的聊天接口 |

## 模型配置

默认使用 `qwen-plus`，可在配置中修改为：

- `qwen-turbo`：更快、成本更低
- `qwen-plus`：均衡
- `qwen-max`：更强能力

## 参考

- [LangChain4j 官方文档](https://docs.langchain4j.dev/)
- [Spring Boot 集成教程](https://docs.langchain4j.dev/tutorials/spring-boot-integration/)
- [DashScope 通义千问](https://help.aliyun.com/zh/model-studio/)
