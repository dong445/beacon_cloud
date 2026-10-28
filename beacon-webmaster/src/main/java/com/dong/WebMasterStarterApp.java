package com.dong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.dong.mapper")
public class WebMasterStarterApp {

    public static void main(String[] args) {
        SpringApplication.run(WebMasterStarterApp.class, args);
        System.out.println("后台管理模块开启");
    }

}