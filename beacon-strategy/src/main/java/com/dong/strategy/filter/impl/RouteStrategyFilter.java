package com.dong.strategy.filter.impl;

import com.dong.common.model.StandardSubmit;
import com.dong.strategy.filter.StrategyFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service(value = "route")
@Slf4j
public class RouteStrategyFilter implements StrategyFilter {

    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-路由策略】 校验ing..........");
    }
}
