package com.dong.api.filter.impl;

import com.dong.api.filter.CheckFilter;
import com.dong.common.model.StandardSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zjw
 * @description  校验客户剩余的金额是否充足
 */
@Service(value = "fee")
@Slf4j
public class FeeCheckFilter implements CheckFilter {

    @Override
    public void check(StandardSubmit submit) {
        log.info("【接口模块-校验客户余额】   校验ing…………");
    }
}