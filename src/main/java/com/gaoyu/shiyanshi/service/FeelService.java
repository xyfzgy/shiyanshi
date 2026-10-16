package com.gaoyu.shiyanshi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gaoyu.shiyanshi.entity.FeelRecord;
import com.gaoyu.shiyanshi.mapper.FeelRecordMapper;
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
public class FeelService {

    private final FeelRecordMapper feelRecordMapper;

    /**
     * 保存感受记录
     */
    public void save(FeelRecord record) {
        feelRecordMapper.insert(record);
    }

    /**
     * 查询用户评分较低的菜（用于下次推荐时避开）
     */
    public List<FeelRecord> listLowRated(int limit) {
        return feelRecordMapper.selectList(
                new LambdaQueryWrapper<FeelRecord>()
                        .lt(FeelRecord::getRating, 5)
                        .orderByDesc(FeelRecord::getCreatedAt)
                        .last("LIMIT " + limit)
        );
    }

    /**
     * 查询用户最近感受记录
     */
    public List<FeelRecord> listRecent(int limit) {
        return feelRecordMapper.selectList(
                new LambdaQueryWrapper<FeelRecord>()
                        .orderByDesc(FeelRecord::getCreatedAt)
                        .last("LIMIT " + limit)
        );
    }
}