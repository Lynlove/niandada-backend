package com.lyn.niandada.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lyn.niandada.common.ErrorCode;
import com.lyn.niandada.exception.ThrowUtils;
import com.lyn.niandada.model.dto.question.QuestionContentDTO;
import com.lyn.niandada.model.entity.App;
import com.lyn.niandada.model.entity.Question;
import com.lyn.niandada.model.entity.ScoringResult;
import com.lyn.niandada.model.entity.UserAnswer;
import com.lyn.niandada.model.vo.QuestionVO;
import com.lyn.niandada.service.QuestionService;
import com.lyn.niandada.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义打分类应用评分策略
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        // 1. 根据 id 查询到题目和题目结果信息（按分数降序排序）
        Long appId = app.getId();
        Question question = questionService.getOne(Wrappers.lambdaQuery(Question.class)
                .eq(Question::getAppId, appId));
        List<ScoringResult> scoringResultList = scoringResultService.list(Wrappers.lambdaQuery(ScoringResult.class)
                .eq(ScoringResult::getAppId, appId)
                .orderByDesc(ScoringResult::getResultScoreRange));
        ThrowUtils.throwIf(scoringResultList.isEmpty(), ErrorCode.NOT_FOUND_ERROR, "app没有对应的评分结果数据");

        // 2. 统计用户的总得分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        // 遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            // 遍历答案列表
            for (String choice : choices) {
                // 遍历题目中的选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 如果答案和选项的key匹配
                    if (option.getKey().equals(choice)) {
                        totalScore += option.getScore();
                    }
                }
            }
        }

        // 3. 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = null;
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                maxScoringResult = scoringResult;
                break;
            }
        }

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(maxScoringResult.getResultScoreRange());
        return userAnswer;
    }
}
