package com.gaoyu.shiyanshi.config;

import com.gaoyu.shiyanshi.tool.FeelTools;
import com.gaoyu.shiyanshi.tool.FoodRecommendTools;
import com.gaoyu.shiyanshi.tool.RecipeTools;
import com.gaoyu.shiyanshi.tool.ShoppingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-14:25
 * @Modified By:
 */
@Configuration
public class ChatClientConfig {

    @Value("classpath:prompts/system.st")
    private Resource systemPrompt;

    @Bean
    public ChatClient chatClient(
            ChatModel chatModel,
            ChatMemory chatMemory,
            VectorStore vectorStore,
            FoodRecommendTools foodRecommendTools,
            RecipeTools recipeTools,
            ShoppingTools shoppingTools,
            FeelTools feelTools) {

        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .topK(4)
                        .build())
                .build();

        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(foodRecommendTools, recipeTools, shoppingTools, feelTools)
                .build()
                .getToolCallbacks();

        return ChatClient.builder(chatModel)
                .defaultSystem(s -> s.text(systemPrompt).param("slotContext", "（暂无）"))
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        qaAdvisor
                )
                .defaultToolCallbacks(toolCallbacks)
                .build();
    }
}
