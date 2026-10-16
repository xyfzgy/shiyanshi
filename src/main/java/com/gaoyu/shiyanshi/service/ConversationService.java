package com.gaoyu.shiyanshi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gaoyu.shiyanshi.entity.Conversation;
import com.gaoyu.shiyanshi.mapper.ConversationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:56
 * @Modified By:
 */
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationMapper conversationMapper;

    /**
     * 创建新对话
     */
    public Conversation create() {
        Conversation conv = new Conversation();
        conv.setSlots("{}");
        conv.setStatus("COLLECTING");
        conversationMapper.insert(conv);
        return conv;
    }

    /**
     * 按 ID 查
     */
    public Conversation getById(Long id) {
        return conversationMapper.selectById(id);
    }

    /**
     * 更新槽位和状态
     */
    public void updateSlots(Long id, String slotsJson, String status) {
        Conversation conv = conversationMapper.selectById(id);
        if (conv != null) {
            conv.setSlots(slotsJson);
            conv.setStatus(status);
            conversationMapper.updateById(conv);
        }
    }

    /**
     * 按用户查最近对话
     */
    public Conversation getLatestByUserId(Long userId) {
        return conversationMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, userId)
                        .orderByDesc(Conversation::getCreatedAt)
                        .last("LIMIT 1")
        );
    }
}