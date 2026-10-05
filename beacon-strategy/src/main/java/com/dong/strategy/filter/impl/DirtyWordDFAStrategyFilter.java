package com.dong.strategy.filter.impl;

import com.dong.common.model.StandardSubmit;
import com.dong.strategy.client.BeaconCacheClient;
import com.dong.strategy.filter.StrategyFilter;
import com.dong.strategy.util.DFAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service(value = "dfaDirtyWord")
@Slf4j
public class DirtyWordDFAStrategyFilter implements StrategyFilter {

    @Autowired
    private BeaconCacheClient cacheClient;

    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-敏感词校验02】   校验ing…………");
        //获取短信内容
        String text = submit.getText();


        /*
        *
        ● 类初始化（static 块）触发时机

        static 块在类首次被主动使用时执行，且只执行一次。触发条件：

        1. new 创建实例
        2. 读写静态变量（static final 编译期常量除外）
        3. 调用静态方法
        4. Class.forName(...)
        5. 反射访问该类
        6. 该类是 main 入口类

        不触发：import、仅声明变量、访问编译期常量
        * */
        //调用DFA查看敏感词
        // 调用静态方法 → 触发初始化
        Set<String> dirtyWords = DFAUtil.getDirtyWord(text);

        //根据返回的set集合，判断是否包含敏感词
        if (dirtyWords != null && dirtyWords.size() > 0) {
            //如果有敏感词，抛出异常 / 其他操作。。
            log.info("【策略模块-敏感词校验】   短信内容包含敏感词信息， dirtyWords = {}", dirtyWords);
            // 还需要做其他处理
        }
    }
}