package com.example.langchain4jdemo.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService // 声明式 AiService，动态代理生成 Assistant 实现
// 默认用注入的chatModel来实现
// langChain4j-community-dashscope-spring-boot-starter 会自动注入一个 chatModel 实现
// @Aiservice就注入这个chatModel实现
// 也可以自定义实现
public interface Assistant {

    @SystemMessage("你是一个礼貌、专业的 AI 助手，用中文回答问题。") // 系统提示词
    String chat(String userMessage); // 聊天方法
}
