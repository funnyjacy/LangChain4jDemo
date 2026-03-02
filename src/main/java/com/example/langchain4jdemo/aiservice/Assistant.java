package com.example.langchain4jdemo.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {

    @SystemMessage(fromResource = "personas/girlfriend.md")
    String chat(@MemoryId String sessionId, @UserMessage String userMessage);
}
