package com.dong.api.filter.impl;

import com.dong.api.client.BeaconCacheClient;
import com.dong.api.filter.CheckFilter;
import com.dong.common.constant.ApiConstant;
import com.dong.common.constant.CacheConstant;
import com.dong.common.enums.ExceptionEnums;
import com.dong.common.exception.ApiException;
import com.dong.common.model.StandardSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author zjw
 * @description  校验短信的签名
 */
@Service(value = "sign")
@Slf4j
public class SignCheckFilter implements CheckFilter {

    @Autowired
    private BeaconCacheClient cacheClient;

    /** 截取签名内容的起始下标（跳过左括号【） */
    private static final int SIGN_START_INDEX = 1;

    /** 缓存中签名内容的字段名 */
    private static final String CLIENT_SIGN_INFO = "signInfo";

    /** 缓存中签名 ID 的字段名 */
    private static final String SIGN_ID = "id";

    @Override
    public void check(StandardSubmit submit) {
        log.info("【接口模块-校验签名】   校验ing…………");
        String text = submit.getText();

        //内容必须带【】，否则没有签名
        if (!StringUtils.hasText(text) || !text.startsWith(ApiConstant.SIGN_PREFIX)  || !text.contains(ApiConstant.SIGN_SUFFIX)){
            log.info("【接口模块-校验签名】 无可用签名 text = {}", text);
            throw new ApiException(ExceptionEnums.ERROR_SIGN);
        }

        //截取出签名
        String sign = text.substring(SIGN_START_INDEX, text.indexOf(ApiConstant.SIGN_SUFFIX));
        if (!StringUtils.hasText(sign)) {
            log.info("【接口模块-校验签名】 无可用签名 text = {}", text);
            throw new ApiException(ExceptionEnums.ERROR_SIGN);
        }

        //取客户已绑定的全部签名
        Set<Map> signs=cacheClient.smember(CacheConstant.CLIENT_SIGN+submit.getClientId());
        if (signs==null || signs.isEmpty()){
            log.info("【接口模块-校验签名】 客户未绑定任何签名 clientId = {}", submit.getClientId());
            throw new ApiException(ExceptionEnums.ERROR_SIGN);
        }


        //逐个比对；命中则把 sign / signId 回填，模板校验要靠它
        for (Map map : signs){
            if (sign.equals(map.get(CLIENT_SIGN_INFO))) {
                submit.setSign(sign);
                submit.setSignId(Long.parseLong(String.valueOf(map.get(SIGN_ID))));
                log.info("【接口模块-校验签名】 匹配到签名 sign = {} , signId = {}", sign, submit.getSignId());
                return;
            }
        }

        //没匹配上
        log.info("【接口模块-校验签名】 无可用签名 sign = {}", sign);
        throw new ApiException(ExceptionEnums.ERROR_SIGN);
    }
}