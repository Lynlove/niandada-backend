package com.lyn.niandada.scoring;

import com.lyn.niandada.common.ErrorCode;
import com.lyn.niandada.exception.BusinessException;
import com.lyn.niandada.model.entity.App;
import com.lyn.niandada.model.entity.UserAnswer;
import com.lyn.niandada.model.enums.AppScoringStrategyEnum;
import com.lyn.niandada.model.enums.AppTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Deprecated
public class ScoringStrategyContext {
    @Resource
    private CustomScoreScoringStrategy customScoreScoringStrategy;
    @Resource
    private CustomTestScoringStrategy customTestScoringStrategy;

    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        AppTypeEnum appTypeEnum = AppTypeEnum.getEnumByValue(app.getAppType());
        AppScoringStrategyEnum scoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(app.getScoringStrategy());
        if (appTypeEnum == null || scoringStrategyEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用类型或评分策略不存在");
        }
        switch (appTypeEnum) {
            case SCORE:
                switch (scoringStrategyEnum) {
                    case AI:
                        break;
                    case CUSTOM:
                        return customTestScoringStrategy.doScore(choices, app);
                    default:
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分策略不存在");
                }
                break;
            case ASSESSMENT:
                switch (scoringStrategyEnum) {
                    case AI:
                        break;
                    case CUSTOM:
                        return customScoreScoringStrategy.doScore(choices, app);
                    default:
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分策略不存在");
                }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
