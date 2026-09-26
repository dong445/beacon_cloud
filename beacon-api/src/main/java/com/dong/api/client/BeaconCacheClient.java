package com.dong.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.Set;

@FeignClient(value = "beacon-cache")
public interface BeaconCacheClient {
    @GetMapping("/cache/hgetall/{key}")
    //   /cache/hgetall 这个接口在 beacon-cache 模块里，它跑在 10002 端口
    Map hGetAll(@PathVariable(value = "key")String key);

    @GetMapping("/cache/smember/{key}")
    Set<Map> smember(@PathVariable("key") String key);

    // 取hash单字段，返回Object（用于取数字型字段，如余额）
    @GetMapping("/cache/hget/{key}/{field}")
    Object hget(@PathVariable("key") String key,@PathVariable("field") String field);

    // 取 hash 单字段，返回 String（用于取字符串字段，如 IP 白名单）
    // Feign 的 Jackson 解码器解不了 text/plain，直接报错,所以需要String
    @GetMapping("/cache/hget/{key}/{field}")
    String hgetString(@PathVariable("key") String key, @PathVariable("field") String field);
}
