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

        // 1. 抽取槽位
        SlotState state = slotManager.extractAndUpdate(conversationId, userMessage);
        String slotContext = buildSlotContext(state);

        // 2. 检查 P0 是否完整
        boolean isReady = state.isP0Complete();
        String nextQuestion = state.nextMissingP0();

        final Long convId = conversationId;

        // 3. 如果 P0 没满，强制大模型去追问，并且禁用工具调用
        if (!isReady) {
            return chatClient.prompt()
                    .user(userMessage)
                    .system(s -> s.text("""
                        当前已收集的槽位信息：
                        {slotContext}
                        
                        【系统指令】：当前缺少必要信息「{nextQuestion}」，请直接用轻松的语气向用户询问，绝对不要调用任何工具！
                        """).param("slotContext", slotContext).param("nextQuestion", nextQuestion))
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, convId.toString()))
                    // 注意：这里不要传 .tools() 或者 .defaultToolCallbacks()，或者传了模型也不会调
                    .stream()
                    .content();
        }

        // 4. 如果 P0 满了，正常对话，允许调工具
        return chatClient.prompt()
                .user(userMessage)
                .system(s -> s.text("""
                    当前已收集的槽位信息：
                    {slotContext}
                    
                    【系统指令】：基本信息已收集完毕，请根据用户需求调用工具推荐菜品。
                    """).param("slotContext", slotContext))
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
