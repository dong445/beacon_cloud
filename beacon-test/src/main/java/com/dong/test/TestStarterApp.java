package com.dong.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@EnableDiscoveryClient
//@EnableFeignClients
@MapperScan(basePackages = "com.dong.test.mapper")
public class TestStarterApp {

    public static void main(String[] args) {
        SpringApplication.run(TestStarterApp.class,args);
    }
}