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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义测评类评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        Question question = questionService.getOne(Wrappers.lambdaQuery(
                Question.class).eq(Question::getAppId, appId));
        List<ScoringResult> scoringResultList = scoringResultService.list(Wrappers.lambdaQuery(
                ScoringResult.class).eq(ScoringResult::getAppId, appId));
        ThrowUtils.throwIf(scoringResultList.isEmpty(), ErrorCode.NOT_FOUND_ERROR, "app没有对应的评分结果数据");
        // 2. 统计用户每个选择对应的属性个数，如 I = 10 个，E = 5 个
        // 初始化一个Map，用于存储每个选项的计数
        Map<String, Integer> optionCounts = new HashMap<>();

        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        // 遍历题目列表
        for(QuestionContentDTO contentDTO : questionContent){
            // 遍历答案列表
            for (String choice : choices) {
                // 遍历题目中的选项
                for (QuestionContentDTO.Option option : contentDTO.getOptions()) {
                    // 如果答案和选项匹配，则计数加1
                    if (option.getKey().equals(choice)){
                        String result = option.getResult();
                        optionCounts.put(result, optionCounts.getOrDefault(result, 0) + 1);
                    }
                }
            }
        }

        // 3. 遍历每种评分结果，计算哪个结果的得分更高
        // 初始化最高分数和最高分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultList.get(0);

        // 遍历评分结果列表
        for (ScoringResult scoringResult : scoringResultList) {
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            // 计算当前评分结果的分数，[I, E] => [10, 5] => 15
            int score = resultProp.stream()
                    .mapToInt(prop -> optionCounts.getOrDefault(prop, 0))
                    .sum();
            // 如果当前分数更高，则更新最高分数和最高分数对应的评分结果
            if (score > maxScore) {
                maxScore = score;
                maxScoringResult = scoringResult;
            }
        }
        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;

    }
}
