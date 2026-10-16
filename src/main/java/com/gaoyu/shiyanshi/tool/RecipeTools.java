package com.gaoyu.shiyanshi.tool;

import com.gaoyu.shiyanshi.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:55
 * @Modified By:
 */
@Component
@RequiredArgsConstructor
public class RecipeTools {

    private final RecipeService recipeService;

    @Tool(description = "根据菜名生成详细食谱，包含材料、步骤、火候、时间、翻车点")
    public String getRecipe(
            @ToolParam(description = "菜名") String dishName,
            @ToolParam(description = "几人份") int servings) {
        return recipeService.generateRecipe(dishName, servings);
    }
}