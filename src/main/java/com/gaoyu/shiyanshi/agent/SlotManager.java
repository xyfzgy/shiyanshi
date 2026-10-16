package com.gaoyu.shiyanshi.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaoyu.shiyanshi.dto.SlotDelta;
import com.gaoyu.shiyanshi.dto.SlotState;
import com.gaoyu.shiyanshi.entity.Conversation;
import com.gaoyu.shiyanshi.mapper.ConversationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:54
 * @Modified By:
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SlotManager {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ChatClient chatClient;
    private final ConversationMapper conversationMapper;

    public SlotState extractAndUpdate(Long conversationId, String userMessage) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv == null) {
            log.warn("conversation 不存在: {}", conversationId);
            return new SlotState();
        }

        SlotState state = parseState(conv.getSlots());

        SlotDelta delta;
        try {
            delta = chatClient.prompt()
                    .user("从这句话提取槽位信息，没有提到的字段留空（null）。"
                            + "只返回 JSON，不要任何解释和代码块标记：" + userMessage)
                    .call()
                    .entity(SlotDelta.class);
        } catch (Exception e) {
            log.warn("LLM 抽取槽位失败，跳过本轮: {}", e.getMessage());
            return state;
        }
        if (delta == null) {
            return state;
        }

        state.merge(delta);

        conv.setSlots(serializeState(state));
        conversationMapper.updateById(conv);

        return state;
    }

    private SlotState parseState(String slotsJson) {
        if (slotsJson == null || slotsJson.isBlank()) {
            return new SlotState();
        }
        try {
            return MAPPER.readValue(slotsJson, SlotState.class);
        } catch (Exception e) {
            log.warn("slots JSON 解析失败，使用空状态: {}", slotsJson);
            return new SlotState();
        }
    }

    private String serializeState(SlotState state) {
        try {
            return MAPPER.writeValueAsString(state);
        } catch (Exception e) {
            log.error("slots JSON 序列化失败", e);
            return "{}";
        }
    }
}