package com.dong.test.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 调用缓存模块（beacon-cache）的OpenFeign客户端
 */
@FeignClient(value = "beacon-cache")
public interface CacheClient {

    /**
     * hash结构存储
     */
    @PostMapping(value = "/cache/hmset/{key}")
    void hmset(@PathVariable(value = "key") String key, @RequestBody Map<String, Object> map);

    /**
     * string结构存储
     */
    @PostMapping(value = "/cache/set/{key}")
    void set(@PathVariable(value = "key") String key, @RequestParam(value = "value") String value);

    /**
     * set结构存储
     */
    @PostMapping(value = "/cache/sadd/{key}")
    void sadd(@PathVariable(value = "key") String key, @RequestBody Map<String, Object>... maps);

    @PostMapping("/cache/pipeline/string")
    void pipelineString(@RequestBody Map<String,String> map);

    @PostMapping(value = "/cache/saddstr/{key}")
    void saddStr(@PathVariable(value = "key")String key, @RequestBody String... value);
}
