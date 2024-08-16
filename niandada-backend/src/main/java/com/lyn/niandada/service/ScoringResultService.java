package com.lyn.niandada.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyn.niandada.model.dto.scoringResult.ScoringResultQueryRequest;
import com.lyn.niandada.model.entity.ScoringResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyn.niandada.model.vo.ScoringResultVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author lyn
* @description 针对表【scoring_result(评分结果)】的数据库操作Service
* @createDate 2024-08-15 17:13:33
*/
public interface ScoringResultService extends IService<ScoringResult> {

    /**
     * 校验数据
     *
     * @param scoringResult
     * @param add 对创建的数据进行校验
     */
    void validScoringResult(ScoringResult scoringResult, boolean add);

    /**
     * 获取查询条件
     *
     * @param scoringResultQueryRequest
     * @return
     */
    QueryWrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringResultQueryRequest);

    /**
     * 获取评分结果封装
     *
     * @param scoringResult
     * @param request
     * @return
     */
    ScoringResultVO getScoringResultVO(ScoringResult scoringResult, HttpServletRequest request);

    /**
     * 分页获取评分结果封装
     *
     * @param scoringResultPage
     * @param request
     * @return
     */
    Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringResultPage, HttpServletRequest request);
}
