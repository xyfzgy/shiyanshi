package com.gaoyu.shiyanshi.service;

import com.gaoyu.shiyanshi.entity.Recipe;
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
public class ShoppingService {

    private final RecipeService recipeService;

    /**
     * 生成采购清单文本（给工具用）
     */
    public String generateShoppingList(String dishName, int servings) {
        Recipe recipe = recipeService.getByName(dishName);
        if (recipe == null) {
            return "暂时没有找到「" + dishName + "」的材料信息。";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【").append(dishName).append("】采购清单（")
                .append(servings).append(" 人份）\n\n");
        sb.append("材料：\n").append(recipe.getIngredients()).append("\n\n");
        sb.append("提示：以上为原始材料，可根据家里已有食材勾选，只买缺的。");
        return sb.toString();
    }
}