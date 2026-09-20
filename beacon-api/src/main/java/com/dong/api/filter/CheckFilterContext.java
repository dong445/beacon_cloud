package com.dong.api.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zjw
 * @description  策略模式的上下文对象
 */
@Component
@RefreshScope
public class CheckFilterContext{
    //Spring 自动把容器中所有实现了 CheckFilter 接口的 Bean 收集起来
    // 以 bean 名为 key、Bean 实例为 value 组装成一个 Map 注入进来
    @Autowired
    private Map<String,CheckFilter> checkFilterMap;

    //基于Nacos获取到执行的顺序和需要执行的校验对象
    @Value("${filters:apikey,ip,sign,template}")
    private String filter;


    //当前check方法用于管理校验链的顺序
    public void check(Object obj){
        //将获取到filters基于，做切分
        String[] filterArray=filter.split(",");
        //遍历数组即可
        for (String filter : filterArray){
            CheckFilter checkFilter=checkFilterMap.get(filter);
            checkFilter.check(obj);
        }
    }

}
