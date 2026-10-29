package com.dong.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "beacon-search")   // 名字 = beacon-search 在 Nacos 注册的服务名
public interface SearchClient {

    @PostMapping("/search/list")
    Map<String, Object> findSmsByParameters(@RequestBody Map<String, Object> parameters);
}