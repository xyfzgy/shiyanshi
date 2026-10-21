package com.gaoyu.shiyanshi.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:57
 * @Modified By:
 */
@Data
public class SlotState {

    private String 食材;
    private String 口味;
    private String 忌口;
    private String 主食;
    private String 配菜;
    private String 人数;
    private String 厨具;
    private String 可用时间;
    private String 难度;
    private String 场景;

    public void merge(SlotDelta delta) {
        if (delta == null) {
            return;
        }
        setIfNotBlank(delta.食材(), v -> this.食材 = v);
        setIfNotBlank(delta.口味(), v -> this.口味 = v);
        setIfNotBlank(delta.忌口(), v -> this.忌口 = v);
        setIfNotBlank(delta.主食(), v -> this.主食 = v);
        setIfNotBlank(delta.配菜(), v -> this.配菜 = v);
        setIfNotBlank(delta.人数(), v -> this.人数 = v);
        setIfNotBlank(delta.厨具(), v -> this.厨具 = v);
        setIfNotBlank(delta.可用时间(), v -> this.可用时间 = v);
        setIfNotBlank(delta.难度(), v -> this.难度 = v);
        setIfNotBlank(delta.场景(), v -> this.场景 = v);
    }

    public boolean isP0Complete() {
        return notBlank(食材) && notBlank(口味);
    }

    public String nextMissingP0() {
        if (!notBlank(食材)) {
            return "食材";
        }
        if (!notBlank(口味)) {
            return "口味";
        }
        return null;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        putIfNotBlank(map, "食材", 食材);
        putIfNotBlank(map, "口味", 口味);
        putIfNotBlank(map, "忌口", 忌口);
        putIfNotBlank(map, "主食", 主食);
        putIfNotBlank(map, "配菜", 配菜);
        putIfNotBlank(map, "人数", 人数);
        putIfNotBlank(map, "厨具", 厨具);
        putIfNotBlank(map, "可用时间", 可用时间);
        putIfNotBlank(map, "难度", 难度);
        putIfNotBlank(map, "场景", 场景);
        return map;
    }

    private void setIfNotBlank(String v, java.util.function.Consumer<String> setter) {
        if (v != null && !v.isBlank()) {
            setter.accept(v);
        }
    }

    private void putIfNotBlank(Map<String, String> map, String k, String v) {
        if (v != null && !v.isBlank()) {
            map.put(k, v);
        }
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}