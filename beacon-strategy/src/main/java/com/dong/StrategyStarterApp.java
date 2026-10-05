package com.dong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class StrategyStarterApp{
    public static void main(String[] args){
        SpringApplication.run(StrategyStarterApp.class);
        System.out.println("策略模块开始启动");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
