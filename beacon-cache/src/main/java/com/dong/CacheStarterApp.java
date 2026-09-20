package com.dong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CacheStarterApp {

    public static void main(String[] args) {
        SpringApplication.run(CacheStarterApp.class,args);
        System.out.println("缓存模块开启");
    }

}