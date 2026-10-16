package com.gaoyu.shiyanshi.config;

import com.gaoyu.shiyanshi.memory.MyBatisPlusChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-19:45
 * @Modified By:
 */
@Configuration
public class ChatMemoryConfig {

    @Bean
    public ChatMemory chatMemory(MyBatisPlusChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }
}