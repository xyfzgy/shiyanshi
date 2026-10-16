package com.gaoyu.shiyanshi.dto;

import lombok.Data;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:57
 * @Modified By:
 */
public record SlotDelta(
        String 食材,
        String 口味,
        String 忌口,
        String 主食,
        String 配菜,
        String 人数,
        String 厨具,
        String 可用时间,
        String 难度,
        String 场景
) {}