package com.dong.smsgateway.config;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig{

    @Bean
    @DynamicThreadPool // 标记为Hippo4j动态线程池
    public ThreadPoolExecutor messageConsumeDynamicExecutor() {
        String threadPoolId = "cmpp-submit"; // 线程池唯一标识，与Hippo4j Server对应
        ThreadPoolExecutor messageConsumeDynamicExecutor = ThreadPoolBuilder.builder()
                // 指定线程名称的前缀，便于排查问题
                .threadFactory(threadPoolId)
                // 线程池在Hippo4j中的唯一标识
                .threadPoolId(threadPoolId)
                // 代表动态线程池，允许运行时调参
                .dynamicPool()
                .build();
        return messageConsumeDynamicExecutor;
    }

    @Bean
    @DynamicThreadPool // 标记为Hippo4j动态线程池
    public ThreadPoolExecutor messageProduceDynamicExecutor() {
        String threadPoolId = "cmpp-deliver";
        ThreadPoolExecutor messageProduceDynamicExecutor = ThreadPoolBuilder.builder()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .dynamicPool()
                .build();
        return messageProduceDynamicExecutor;
    }
}
