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
@TableName("user_profile")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;

    /** JSON 字符串 */
    private String tastePreference;

    /** JSON 字符串 */
    private String taboo;

    private String dietGoal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}