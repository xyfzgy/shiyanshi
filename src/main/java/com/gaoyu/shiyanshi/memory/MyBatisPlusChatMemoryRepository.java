package com.gaoyu.shiyanshi.memory;

import com.gaoyu.shiyanshi.mapper.MessageMapper;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:56
 * @Modified By:
 */
@Repository
public class MyBatisPlusChatMemoryRepository implements ChatMemoryRepository {

    private final MessageMapper messageMapper;

    public MyBatisPlusChatMemoryRepository(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }
    /**
     * 返回所有存在消息的会话 ID 列表
     * 用于管理端查看、清理等场景
     */
    @Override
    public List<String> findConversationIds() {
        return messageMapper.selectDistinctConversationIds()
                .stream()
                .map(String::valueOf)
                .toList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return messageMapper.selectByConversationId(Long.valueOf(conversationId))
                .stream()
                .map(this::toMessage)
                .toList();
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        messageMapper.deleteByConversationId(Long.valueOf(conversationId));
        for (Message msg : messages) {
            com.gaoyu.shiyanshi.entity.Message entity = new com.gaoyu.shiyanshi.entity.Message();
            entity.setConversationId(Long.valueOf(conversationId));
            entity.setRole(msg.getMessageType().name());
            entity.setContent(msg.getText());
            messageMapper.insert(entity);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        messageMapper.deleteByConversationId(Long.valueOf(conversationId));
    }

    private Message toMessage(com.gaoyu.shiyanshi.entity.Message entity) {
        MessageType type = MessageType.valueOf(entity.getRole().toUpperCase());
        String content = entity.getContent();
        return switch (type) {
            case USER -> new UserMessage(content);
            case ASSISTANT -> new AssistantMessage(content);
            case SYSTEM -> new SystemMessage(content);
            default -> new UserMessage(content);
        };
    }
}