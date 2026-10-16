package com.gaoyu.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-11:29
 * @Modified By:
 */
@Data
@TableName("feel_record")
public class FeelRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long conversationId;
    private Long userId;
    private String dishName;
    private Integer rating;
    private String positive;
    private String improvement;
    private String emotionTags;   // JSON 字段，Java 侧先当 String
    private Boolean wantAgain;
    private LocalDateTime createdAt;
}