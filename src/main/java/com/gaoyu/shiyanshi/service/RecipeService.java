package com.gaoyu.shiyanshi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gaoyu.shiyanshi.entity.Recipe;
import com.gaoyu.shiyanshi.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:56
 * @Modified By:
 */
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;

    /**
     * 按菜名模糊查询
     */
    public List<Recipe> searchByName(String keyword) {
        return recipeMapper.selectList(
                new LambdaQueryWrapper<Recipe>()
                        .like(Recipe::getName, keyword)
                        .last("LIMIT 10")
        );
    }

    /**
     * 按菜系查询
     */
    public List<Recipe> listByCuisine(String cuisine) {
        return recipeMapper.selectList(
                new LambdaQueryWrapper<Recipe>()
                        .eq(Recipe::getCuisine, cuisine)
                        .last("LIMIT 20")
        );
    }

    /**
     * 按菜名精确查询
     */
    public Recipe getByName(String name) {
        return recipeMapper.selectOne(
                new LambdaQueryWrapper<Recipe>()
                        .eq(Recipe::getName, name)
                        .last("LIMIT 1")
        );
    }

    /**
     * 生成食谱文本（给工具用）
     */
    public String generateRecipe(String dishName, int servings) {
        Recipe recipe = getByName(dishName);
        if (recipe == null) {
            return "暂时没有找到「" + dishName + "」的食谱，换个菜试试？";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【").append(recipe.getName()).append("】\n");
        sb.append("菜系：").append(recipe.getCuisine()).append("\n");
        sb.append("难度：").append(recipe.getDifficulty()).append("\n");
        sb.append("时间：准备 ").append(recipe.getPrepTime()).append(" 分钟，"
                + "烹饪 ").append(recipe.getCookTime()).append(" 分钟\n");
        sb.append("份量：").append(servings).append(" 人份\n\n");
        sb.append("【材料】\n").append(recipe.getIngredients()).append("\n\n");
        sb.append("【步骤】\n").append(recipe.getSteps()).append("\n\n");
        if (recipe.getTips() != null) {
            sb.append("【小贴士】\n").append(recipe.getTips());
        }
        return sb.toString();
    }
}