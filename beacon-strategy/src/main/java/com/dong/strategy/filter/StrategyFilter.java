package com.dong.strategy.filter;

import com.dong.common.model.StandardSubmit;

public interface StrategyFilter{
    /**
     * 校验！！！！
     * @param submit
     */
    void strategy(StandardSubmit submit);
}
