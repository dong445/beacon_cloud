package com.dong.strategy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@FeignClient(value = "beacon-cache")
public interface BeaconCacheClient {

    /*
    * 服务端用 Object 是因为 Redis 取值类型不确定、要通用；
    * 客户端用 String 是因为 HTTP 响应本质就是文本、要原样接收
    * */
    @GetMapping("/cache/hget/{key}/{field}")
    String hget(@PathVariable(value = "key")String key, @PathVariable(value = "field")String field);

    @PostMapping("/cache/get/{key}")
    //方法名都可以不一样，返回类型看情况
    String getString(@PathVariable(value = "key") String key);

    @PostMapping(value = "/cache/sinterstr/{key}/{sinterKey}")
    Set<Object> sinterStr(@PathVariable(value = "key")String key, @PathVariable String sinterKey, @RequestBody String... value);

    //取 Set 的全部成员
    @GetMapping("/cache/smember/{key}")
    Set smember(@PathVariable String key);
}
