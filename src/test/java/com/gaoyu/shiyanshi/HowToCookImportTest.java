package com.gaoyu.shiyanshi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaoyu.shiyanshi.entity.Recipe;
import com.gaoyu.shiyanshi.mapper.RecipeMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
class HowToCookImportTest {

    @Autowired
    private RecipeMapper recipeMapper;

    /** Jackson 自带，直接注入或用 new 都行 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DISHES_ROOT = "dishes";

    private static final Pattern TITLE_PATTERN =
            Pattern.compile("^#\\s+(.+?)\\s*的做法\\s*$", Pattern.MULTILINE);
    private static final Pattern H2_PATTERN =
            Pattern.compile("^##\\s+(.+?)\\s*$", Pattern.MULTILINE);

    @Test
    void importAllRecipes() throws Exception {
        recipeMapper.delete(null);
        log.info("已清空 recipe 表");

        Path root = Paths.get(DISHES_ROOT);
        if (!Files.exists(root)) {
            log.error("目录不存在: {}", root.toAbsolutePath());
            return;
        }

        List<Path> mdFiles = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(p -> p.toString().endsWith(".md"))
                    .filter(p -> !p.toString().contains("template"))
                    .forEach(mdFiles::add);
        }
        log.info("发现 {} 个菜谱文件", mdFiles.size());

        int success = 0, skipped = 0, failed = 0;
        for (Path mdFile : mdFiles) {
            try {
                Recipe recipe = parseMarkdown(mdFile);
                if (recipe == null) {
                    skipped++;
                    continue;
                }
                recipeMapper.insert(recipe);
                success++;
            } catch (Exception e) {
                failed++;
                log.error("解析/插入失败: {}", mdFile, e);
            }
        }

        Long total = recipeMapper.selectCount(null);
        log.info("========================================");
        log.info("导入完成：成功 {}，跳过 {}，失败 {}", success, skipped, failed);
        log.info("数据库 recipe 表总数: {}", total);
        log.info("========================================");
    }

    // ---------- 解析逻辑 ----------

    private Recipe parseMarkdown(Path mdFile) throws Exception {
        String content = Files.readString(mdFile);

        Matcher titleMatcher = TITLE_PATTERN.matcher(content);
        if (!titleMatcher.find()) return null;

        String dishName = titleMatcher.group(1).trim();
        Map<String, String> sections = splitSections(content);

        String ingredientsRaw = (sections.getOrDefault("必备原料和工具", "") + "\n"
                + sections.getOrDefault("计算", "")).trim();
        String stepsRaw = sections.getOrDefault("操作", "").trim();
        String tips = sections.getOrDefault("附加内容", "").trim();

        int stars = countStars(content);
        String difficulty = switch (stars) {
            case 0, 1 -> "简单";
            case 2, 3 -> "中等";
            default -> "困难";
        };

        Recipe r = new Recipe();
        r.setName(dishName);
        r.setCuisine(inferCuisine(mdFile));
        r.setDifficulty(difficulty);
        r.setIngredients(parseIngredientsToJson(ingredientsRaw));
        r.setSteps(parseStepsToJson(stepsRaw));
        r.setTips(tips);
        r.setTasteTags(parseTasteTags(content));
        r.setSource("HowToCook");
        return r;
    }

    private Map<String, String> splitSections(String content) {
        Map<String, String> sections = new LinkedHashMap<>();
        Matcher m = H2_PATTERN.matcher(content);
        List<int[]> positions = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        while (m.find()) {
            positions.add(new int[]{m.start(), m.end()});
            titles.add(m.group(1).trim());
        }

        for (int i = 0; i < positions.size(); i++) {
            int start = positions.get(i)[1];
            int end = (i + 1 < positions.size()) ? positions.get(i + 1)[0] : content.length();
            String body = content.substring(start, end)
                    .replaceAll("<!--[\\s\\S]*?-->", "")
                    .trim();
            sections.put(titles.get(i), body);
        }
        return sections;
    }

    private String inferCuisine(Path mdFile) {
        Path parent = mdFile.getParent();
        if (parent == null) return null;
        String dir = parent.getFileName().toString();
        return switch (dir) {
            case "vegetable_dish" -> "素菜";
            case "meat_dish" -> "荤菜";
            case "aquatic" -> "水产";
            case "breakfast" -> "早餐";
            case "staple" -> "主食";
            case "soup" -> "汤与粥";
            case "drink" -> "饮料";
            case "condiment" -> "酱料";
            case "dessert" -> "甜品";
            case "semi-finished" -> "半成品";
            default -> dir;
        };
    }

    private int countStars(String content) {
        int count = 0;
        for (char c : content.toCharArray()) {
            if (c == '★') count++;
        }
        return count;
    }

    private String parseIngredientsToJson(String raw) throws Exception {
        if (raw == null || raw.isBlank()) return "[]";
        List<Map<String, String>> list = new ArrayList<>();
        for (String line : raw.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || !line.startsWith("-")) continue;
            String item = line.substring(1).trim();
            if (item.isEmpty()) continue;
            Map<String, String> map = new HashMap<>();
            map.put("name", item);
            list.add(map);
        }
        return objectMapper.writeValueAsString(list);
    }

    private String parseStepsToJson(String raw) throws Exception {
        if (raw == null || raw.isBlank()) return "[]";
        List<Map<String, Object>> list = new ArrayList<>();
        int order = 1;
        for (String line : raw.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String action = line.replaceFirst("^\\d+[.、)]\\s*", "")
                    .replaceFirst("^-\\s*", "")
                    .trim();
            if (action.isEmpty()) continue;
            Map<String, Object> map = new HashMap<>();
            map.put("order", order++);
            map.put("action", action);
            list.add(map);
        }
        return objectMapper.writeValueAsString(list);
    }

    private String parseTasteTags(String content) throws Exception {
        List<String> tags = new ArrayList<>();
        String intro = content.replaceFirst("^#[^\\n]*\\n", "").split("##")[0];
        String[] keywords = {"辣", "清淡", "酸甜", "咸香", "麻辣", "鲜", "甜", "酸"};
        for (String kw : keywords) {
            if (intro.contains(kw)) tags.add(kw);
        }
        return objectMapper.writeValueAsString(tags);
    }
}