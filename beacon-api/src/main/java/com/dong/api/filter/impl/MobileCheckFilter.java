package com.dong.api.filter.impl;

import com.dong.api.filter.CheckFilter;
import com.dong.api.util.PhoneFormatCheckUtil;
import com.dong.common.enums.ExceptionEnums;
import com.dong.common.exception.ApiException;
import com.dong.common.model.StandardSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author zjw
 * @description  校验手机号的格式合法性
 */
@Service(value = "mobile")
@Slf4j
public class MobileCheckFilter implements CheckFilter {

    @Override
    public void check(StandardSubmit submit) {
        log.info("【接口模块-校验手机号】   校验ing…………");
        String mobile = submit.getMobile();

        if (StringUtils.hasText(mobile)  && PhoneFormatCheckUtil.isChinaPhone(mobile)){
            log.info("【接口模块-校验手机号】 格式合法 mobile = {}", mobile);
            return;
        }

        log.info("【接口模块-校验手机号】 格式不正确 mobile = {}", mobile);
        throw new ApiException(ExceptionEnums.ERROR_MOBILE);
    }
}