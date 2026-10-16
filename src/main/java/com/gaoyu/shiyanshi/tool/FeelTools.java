package com.gaoyu.shiyanshi.tool;

import com.gaoyu.shiyanshi.entity.FeelRecord;
import com.gaoyu.shiyanshi.mapper.FeelRecordMapper;
import com.gaoyu.shiyanshi.service.FeelService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:55
 * @Modified By:
 */
@Component
@RequiredArgsConstructor
public class FeelTools {

    private final FeelService feelService;

    @Tool(description = "记录用户对某道菜的感受，包括评分、优点、改进点、情感标签等")
    public String recordFeel(
            @ToolParam(description = "菜名") String dishName,
            @ToolParam(description = "评分，1-10 的整数") int rating,
            @ToolParam(description = "优点，用户觉得好的地方") String positive,
            @ToolParam(description = "改进点，用户觉得需要调整的地方") String improvement,
            @ToolParam(description = "情感标签，如：治愈、满足、踩雷、想再做") List<String> emotionTags,
            @ToolParam(description = "是否想再做这道菜") boolean wantAgain) {

        FeelRecord record = new FeelRecord();
        record.setDishName(dishName);
        record.setRating(rating);
        record.setPositive(positive);
        record.setImprovement(improvement);
        record.setEmotionTags(String.join(",", emotionTags));
        record.setWantAgain(wantAgain);

        feelService.save(record);
        return "已记录你对「" + dishName + "」的感受，下次推荐会参考～";
    }
}