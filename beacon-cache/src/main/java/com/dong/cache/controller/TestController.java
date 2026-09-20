package com.dong.cache.controller;

import com.msb.framework.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TestController {
//    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private RedisClient redisClient;

    //写测试
    @PostMapping("/test/set/{key}")
    public String set(@PathVariable String key, @RequestBody Map map){
//        redisTemplate.opsForHash().putAll(key,map);
        redisClient.set(key,map);
        return "ok";
    }

    // 读测试
    @GetMapping("/test/get/{key}")
    public Map get(@PathVariable String key){
//        Map<Object, Object> result = redisTemplate.opsForHash().entries(key);
        Map<Object,Object> result=redisClient.get(key);
        return result;
    }
}
