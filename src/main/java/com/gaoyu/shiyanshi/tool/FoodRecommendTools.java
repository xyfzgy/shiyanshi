package com.gaoyu.shiyanshi.tool;

import com.gaoyu.shiyanshi.entity.Recipe;
import com.gaoyu.shiyanshi.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:55
 * @Modified By:
 */
@Component
@RequiredArgsConstructor
public class FoodRecommendTools {

    private final RecipeService recipeService;

    @Tool(description = "根据食材和口味推荐菜品，返回菜名、菜系、难度、时间")
    public String recommend(
            @ToolParam(description = "食材，如：牛肉、鸡胸肉") String ingredients,
            @ToolParam(description = "口味，如：辣、清淡、酸甜") String taste,
            @ToolParam(description = "是否在家做，true=在家做，false=外出吃") boolean cookAtHome) {

        // 简化版：按食材关键字在菜谱库里搜
        List<Recipe> candidates = recipeService.searchByName(ingredients);

        if (candidates.isEmpty()) {
            return "暂时没有找到含「" + ingredients + "」的菜谱。换个食材试试？";
        }

        // 优先匹配口味标签
        List<Recipe> matched = candidates.stream()
                .filter(r -> r.getTasteTags() != null && r.getTasteTags().contains(taste))
                .collect(Collectors.toList());

        List<Recipe> result = matched.isEmpty() ? candidates : matched;

        StringBuilder sb = new StringBuilder();
        sb.append("根据你提供的「").append(ingredients).append("」和「")
                .append(taste).append("」口味，推荐：\n\n");

        result.stream().limit(3).forEach(r -> {
            sb.append("• ").append(r.getName())
                    .append("（").append(r.getCuisine()).append("，")
                    .append(r.getDifficulty()).append("，约 ")
                    .append(r.getPrepTime() + r.getCookTime()).append(" 分钟）\n");
        });

        sb.append("\n需要哪道菜的食谱和材料清单？");
        return sb.toString();
    }
}