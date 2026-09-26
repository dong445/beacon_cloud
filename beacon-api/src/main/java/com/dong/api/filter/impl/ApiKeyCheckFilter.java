package com.dong.api.filter.impl;

import com.dong.api.client.BeaconCacheClient;
import com.dong.common.enums.ExceptionEnums;
import com.dong.api.filter.CheckFilter;
import com.dong.common.constant.CacheConstant;
import com.dong.common.exception.ApiException;
import com.dong.common.model.StandardSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service(value = "apikey")
//value 就是给这个 Bean 起名字（beanName）
@Slf4j
public class ApiKeyCheckFilter implements CheckFilter {

    @Autowired
    private BeaconCacheClient cacheClient;

    @Override
    public void check(StandardSubmit submit) {
        log.info("【接口模块-校验apikey】   校验ing…………");
        //1. 基于cacheClient查询客户信息
        Map clientBusiness = cacheClient.hGetAll(CacheConstant.CLIENT_BUSINESS + submit.getApikey());

        //2. 如果为null，直接扔异常
        if(clientBusiness == null || clientBusiness.size() == 0){
            log.info("【接口模块-校验apikey】 非法的apikey = {}",submit.getApikey());
            throw new ApiException(ExceptionEnums.ERROR_APIKEY);
        }

        //3. 正常封装数据
        submit.setClientId(Long.parseLong(clientBusiness.get("id") + ""));
        log.info("【接口模块-校验apikey】 查询到客户信息 clientBusiness = {}",clientBusiness);
    }

}
