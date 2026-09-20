package com.dong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiStarterApp{
    public static void main(String[] args) {
        SpringApplication.run(ApiStarterApp.class,args);
        System.out.println("ApiStarterApp服务开启");
    }
}

