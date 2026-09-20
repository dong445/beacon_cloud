package com.dong.api.filter.impl;

import com.dong.api.filter.CheckFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service(value = "apikey")
//value 就是给这个 Bean 起名字（beanName）
@Slf4j
public class ApiKeyCheckFilter implements CheckFilter {
    @Override
    public void check(Object obj) {
        log.info("【接口模块-校验apikey】   校验ing…………");
    }


}
