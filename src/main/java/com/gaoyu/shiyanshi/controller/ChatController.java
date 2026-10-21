package com.gaoyu.shiyanshi.controller;

import com.gaoyu.shiyanshi.agent.FoodAgent;
import com.gaoyu.shiyanshi.entity.Conversation;
import com.gaoyu.shiyanshi.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-14:26
 * @Modified By:
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final FoodAgent foodAgent;
    private final ConversationService conversationService;

    /**
     * 创建新对话
     */
    @PostMapping("/new")
    public Long newConversation() {
        Conversation conv = conversationService.create();
        return conv.getId();
    }

    /**
     * SSE 流式对话
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(
            @RequestParam String message,
            @RequestParam(required = false) Long conversationId) {

        if (conversationId == null) {
            conversationId = conversationService.create().getId();
        }

        return foodAgent.chat(conversationId, message);
    }
}