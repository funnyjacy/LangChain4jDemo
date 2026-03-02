package com.example.langchain4jdemo.controller;

import com.example.langchain4jdemo.aiservice.Assistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 使用 AiService 的聊天接口 - 由 LangChain4j 根据 @AiService 自动创建 Assistant 实现
 */
@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final Assistant assistant;

    public ChatController(Assistant assistant) {
        this.assistant = assistant;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "请介绍一下你自己") String message) {
        log.info("AiService 请求: message={}", message);
        String response = assistant.chat(message);
        log.debug("AiService 响应: {}", response);
        return response;
    }
}
