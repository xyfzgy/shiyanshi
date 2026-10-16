package com.gaoyu.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaoyu.shiyanshi.entity.Message;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-11:35
 * @Modified By:
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    @Select("SELECT * FROM message WHERE conversation_id = #{conversationId} ORDER BY created_at ASC, id ASC")
    List<Message> selectByConversationId(@Param("conversationId") Long conversationId);

    @Select("SELECT * FROM (SELECT * FROM message WHERE conversation_id = #{conversationId} ORDER BY created_at DESC, id DESC LIMIT #{lastN}) t ORDER BY created_at ASC, id ASC")
    List<Message> selectLastN(@Param("conversationId") Long conversationId, @Param("lastN") int lastN);

    @Delete("DELETE FROM message WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(@Param("conversationId") Long conversationId);
    @Select("SELECT DISTINCT conversation_id FROM message")
    List<Long> selectDistinctConversationIds();
}