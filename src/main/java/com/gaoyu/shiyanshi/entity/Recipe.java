package com.gaoyu.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-11:28
 * @Modified By:
 */
@Data
@TableName("recipe")
public class Recipe {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String cuisine;
    private String difficulty;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String ingredients;   // JSON 字符串
    private String steps;         // JSON 字符串
    private String tips;
    private String tasteTags;     // JSON 字符串
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}