package com.dong.strategy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/cache/hget/{key}/{field}")
    Integer hgetInteger(@PathVariable(value = "key")String key, @PathVariable(value = "field")String field);

    //往 Redis 的某个 ZSet 里插入一条记录
    //score	发送时间戳	排序依据，供 ZRANGEBYSCORE 查
    //member	发送时间戳	记录的唯一标识
    @PostMapping(value = "/cache/zadd/{key}/{score}/{member}")
    Boolean zadd(@PathVariable(value = "key")String key,
                        @PathVariable(value = "score")Long score,
                        @PathVariable(value = "member")Object member);

    //查某个 ZSet 里，score在 [start, end] 范围内的记录有几条
    @GetMapping(value = "/cache/zrangebyscorecount/{key}/{start}/{end}")
    int zRangeByScoreCount(@PathVariable(value = "key") String key,
                                  @PathVariable(value = "start") Double start,
                                  @PathVariable(value = "end") Double end);


    @DeleteMapping(value = "/cache/zremove/{key}/{member}")
    void zRemove(@PathVariable(value = "key") String key,@PathVariable(value = "member") String member);

    @PostMapping(value = "/cache/hincrby/{key}/{field}/{delta}")
    Long hIncrBy(@PathVariable(value = "key") String key,
                        @PathVariable(value = "field") String field,
                        @PathVariable(value = "delta") Long delta);
}
