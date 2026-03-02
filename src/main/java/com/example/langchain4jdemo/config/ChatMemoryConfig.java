package com.example.langchain4jdemo.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.community.store.memory.chat.redis.StoreType;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对话记忆配置 - 使用 Redis 持久化，服务器重启后聊天记录不丢失
 */
@Configuration
public class ChatMemoryConfig {

    private static final int MAX_MESSAGES = 20;
    private static final String KEY_PREFIX = "chat:";

    @Bean
    public ChatMemoryStore chatMemoryStore(
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6379}") int port,
            @Value("${spring.data.redis.password:}") String password) {
        RedisChatMemoryStore.Builder builder = RedisChatMemoryStore.builder()
                .host(host)
                .port(port)
                .prefix(KEY_PREFIX)
                .storeType(StoreType.STRING);  // 标准 Redis 使用 STRING，JSON 需 Redis Stack
        if (password != null && !password.isBlank()) {
            builder.user("default").password(password);
        }
        return builder.build();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider(ChatMemoryStore chatMemoryStore) {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(MAX_MESSAGES)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }
}
