package com.gaoyu.shiyanshi.rag;

import com.gaoyu.shiyanshi.entity.Recipe;
import com.gaoyu.shiyanshi.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:44
 * @Modified By:
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VectorStoreInitializer implements ApplicationRunner {

    private final RecipeMapper recipeMapper;
    private final VectorStore vectorStore;

    @Override
    public void run(ApplicationArguments args) {
        File file = new File("./data/vector-store.json");

        if (file.exists()) {
            try {
                ((SimpleVectorStore) vectorStore).load(file);
                log.info("向量库从文件加载完成");
                return;
            } catch (Exception e) {
                log.warn("向量库加载失败，将重建: {}", e.getMessage());
            }
        }

        List<Recipe> recipes = recipeMapper.selectList(null);
        if (recipes.isEmpty()) {
            log.warn("recipe 表为空，跳过向量库初始化");
            return;
        }

        List<Document> docs = recipes.stream().map(this::toDocument).toList();
        vectorStore.add(docs);

        try {
            file.getParentFile().mkdirs();
            ((SimpleVectorStore) vectorStore).save(file);
            log.info("向量库初始化完成，共 {} 道菜谱", recipes.size());
        } catch (Exception e) {
            log.error("向量库保存失败", e);
        }
    }

    private Document toDocument(Recipe r) {
        String content = String.format("""
                菜名：%s
                菜系：%s
                口味：%s
                材料：%s
                做法：%s
                """,
                r.getName(),
                r.getCuisine() == null ? "" : r.getCuisine(),
                r.getTasteTags() == null ? "" : r.getTasteTags(),
                r.getIngredients() == null ? "" : r.getIngredients(),
                r.getSteps() == null ? "" : r.getSteps());

        return new Document(content, Map.of(
                "recipeId", r.getId(),
                "name", r.getName()
        ));
    }
}