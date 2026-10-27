package com.dong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WebMasterStarterApp {

    public static void main(String[] args) {
        SpringApplication.run(WebMasterStarterApp.class, args);
        System.out.println("后台管理模块开启");
    }

}