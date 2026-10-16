package com.gaoyu.shiyanshi.tool;

import com.gaoyu.shiyanshi.service.ShoppingService;
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
public class ShoppingTools {

    private final ShoppingService shoppingService;

    @Tool(description = "根据菜名生成采购清单，按品类分组，支持排除已有食材")
    public String generateShoppingList(
            @ToolParam(description = "菜名") String dishName,
            @ToolParam(description = "几人份") int servings) {
        return shoppingService.generateShoppingList(dishName, servings);
    }
}