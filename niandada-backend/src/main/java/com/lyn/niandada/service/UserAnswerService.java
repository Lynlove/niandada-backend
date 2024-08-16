package com.lyn.niandada.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyn.niandada.model.dto.userAnswer.UserAnswerQueryRequest;
import com.lyn.niandada.model.entity.UserAnswer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyn.niandada.model.vo.UserAnswerVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author lyn
* @description 针对表【user_answer(用户答题记录)】的数据库操作Service
* @createDate 2024-08-15 17:13:33
*/
public interface UserAnswerService extends IService<UserAnswer> {
    /**
     * 校验数据
     *
     * @param userAnswer
     * @param add 对创建的数据进行校验
     */
    void validUserAnswer(UserAnswer userAnswer, boolean add);

    /**
     * 获取查询条件
     *
     * @param userAnswerQueryRequest
     * @return
     */
    QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest userAnswerQueryRequest);

    /**
     * 获取用户答题记录封装
     *
     * @param userAnswer
     * @param request
     * @return
     */
    UserAnswerVO getUserAnswerVO(UserAnswer userAnswer, HttpServletRequest request);

    /**
     * 分页获取用户答题记录封装
     *
     * @param userAnswerPage
     * @param request
     * @return
     */
    Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage, HttpServletRequest request);
}
