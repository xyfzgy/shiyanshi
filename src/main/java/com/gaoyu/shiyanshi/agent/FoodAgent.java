package com.gaoyu.shiyanshi.agent;

import com.gaoyu.shiyanshi.dto.SlotState;
import com.gaoyu.shiyanshi.entity.Conversation;
import com.gaoyu.shiyanshi.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:54
 * @Modified By:
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FoodAgent {

    private final ChatClient chatClient;
    private final ConversationService conversationService;
    private final SlotManager slotManager;

    @Value("classpath:prompts/system.st")
    private Resource systemPrompt;

    public Flux<String> chat(Long conversationId, String userMessage) {
        Conversation conv = conversationService.getById(conversationId);
        if (conv == null) {
            conv = conversationService.create();
            conversationId = conv.getId();
        }

        // 抽取槽位（现在有返回值）
        SlotState state = slotManager.extractAndUpdate(conversationId, userMessage);

        String slotContext = buildSlotContext(state);
        final Long convId = conversationId;

        return chatClient.prompt()
                .user(userMessage)
                .system(s -> s.text(systemPrompt).param("slotContext", slotContext))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, convId.toString()))
                .stream()
                .content();
    }

    private String buildSlotContext(SlotState state) {
        StringBuilder sb = new StringBuilder();
        state.toMap().forEach((k, v) -> sb.append(k).append("：").append(v).append("\n"));
        return sb.isEmpty() ? "（暂无）" : sb.toString();
    }
}
