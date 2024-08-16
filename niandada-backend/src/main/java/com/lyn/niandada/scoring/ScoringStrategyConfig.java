package com.lyn.niandada.scoring;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScoringStrategyConfig {
    /**
     * 获取应用类型
     * @return
     */
    int appType();

    /**
     * 获取评分策略
     * @return
     */
    int scoringStrategy();
}
