package com.lyn.niandada.scoring;

import com.lyn.niandada.common.ErrorCode;
import com.lyn.niandada.exception.BusinessException;
import com.lyn.niandada.model.entity.App;
import com.lyn.niandada.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScoringStrategyExecutor {
    // 策略列表
    @Resource
    private List<ScoringStrategy> scoringStrategyList;

    /**
     * 执行评分
     * @param choices
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Integer appType = app.getAppType();
        Integer scoringStrategy = app.getScoringStrategy();
        if (appType == null || scoringStrategy == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用类型或评分策略为空");
        }
        // 根据注解获取策略
        for (ScoringStrategy strategy : scoringStrategyList) {
            if (strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)) {
                ScoringStrategyConfig annotation = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                if (annotation.appType() == appType && annotation.scoringStrategy() == scoringStrategy) {
                    return strategy.doScore(choices, app);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未找到匹配的评分策略");
    }
}
