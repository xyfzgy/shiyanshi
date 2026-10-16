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
@TableName("conversation")
public class Conversation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;

    /** JSON 字符串 {"食材":"牛肉","口味":"辣"} */
    private String slots;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}