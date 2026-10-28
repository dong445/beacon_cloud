package com.dong.cache.controller;

import com.msb.framework.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * 缓存模块对外提供的存储接口
 */
@RestController
@Slf4j
public class CacheController {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RedisTemplate redisTemplate;

    //存整个 Hash
    @PostMapping(value = "/cache/hmset/{key}")
    public void hmset(@PathVariable(value = "key") String key, @RequestBody Map<String, Object> map) {
        log.info("【缓存模块】 hmset方法，存储key = {}，存储value = {}", key, map);
        redisClient.hSet(key, map);
    }

    //存字符串
    @PostMapping(value = "/cache/set/{key}")
    public void set(@PathVariable(value = "key") String key, @RequestParam(value = "value") String value) {
        log.info("【缓存模块】 set方法，存储key = {}，存储value = {}", key, value);
        redisClient.set(key, value);
    }

    // 往 Set 添加元素
    @PostMapping(value = "/cache/sadd/{key}")
    public void sadd(@PathVariable(value = "key") String key, @RequestBody Map<String, Object>... value) {
        log.info("【缓存模块】 sadd方法，存储key = {}，存储value = {}", key, value);
        redisClient.sAdd(key, value);
    }

    //取整个 Hash 的所有字段和值
    @GetMapping("/cache/hgetall/{key}")
    public Map hGetAll(@PathVariable(value = "key")String key){
        log.info("【缓存模块】 hGetAll方法，获取key ={} 的数据", key);
        Map<String, Object> value = redisClient.hGetAll(key);
        log.info("【缓存模块】 hGetAll方法，获取key ={} 的数据 value = {}", key,value);
        return value;
    }

    //取 Set 的全部成员
    @GetMapping("/cache/smember/{key}")
    public Set smember(@PathVariable String key) {
        log.info("【缓存模块】smember key = {}", key);
        Set<Object> values = redisClient.sMembers(key);
        log.info("【缓存模块】smember key = {} , value = {}", key, values);
        return values;
    }

    //按key和field取hash的某个值， IP 校验用它取 ipAddress；余额校验用它取 balance
    @GetMapping("/cache/hget/{key}/{field}")
    public Object hget(@PathVariable String key,@PathVariable String field){
        log.info("【缓存模块】hget key = {} , field = {}", key, field);
        Object value=redisClient.hGet(key, field);
        log.info("【缓存模块】hget key = {} , field = {} , value = {}", key, field, value);
        return value;
    }

    @PostMapping("/cache/pipeline/string")
    public void pipelineString(@RequestBody Map<String,String> map){
        log.info("【缓存模块】 pipelineString，获取到存储的数据，map的长度 ={}的数据", map.size());
        /*
        * for (Map.Entry<String, String> entry : map.entrySet()) 是增强 for 循环遍历 Map 的所有键值对，
        * 每次 entry 是一对键值，用 entry.getKey() 取键、entry.getValue() 取值
        * */
        redisClient.pipelined(operations -> {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                operations.opsForValue().set(entry.getKey(),entry.getValue());
            }
        });
    }

    @PostMapping("/cache/get/{key}")
    public Object get(@PathVariable(value = "key") String key){
        log.info("【缓存模块】get方法，查询key={}",key);
        Object value=redisClient.get(key);
        log.info("【缓存模块】get方法，查询key={} 对应的value={}",key,value);
        return value;
    }

    @PostMapping(value = "/cache/saddstr/{key}")
    public void saddStr(@PathVariable(value = "key")String key, @RequestBody String... value){
        log.info("【缓存模块】 saddStr方法，存储key = {}，存储value = {}", key, value);
        redisClient.sAdd(key,value);
    }

        /*
        * 如果黑名单有 10 万条，Java 里用 HashSet.retainAll() 算，
        * 需要先把 10 万条从 Redis 拉到内存，网络和内存开销都很大
        *  所以用 Redis 求交集，而不是在 Java 里算
        * */
    @PostMapping(value = "/cache/sinterstr/{key}/{sinterKey}")
    public Set<Object> sinterStr(@PathVariable(value = "key")String key, @PathVariable String sinterKey,@RequestBody String... value){
        log.info("【缓存模块】 sinterStr的交集方法，存储key = {}，sinterKey = {}，存储value = {}", key, sinterKey,value);
        //1、 存储数据到set集合
        redisClient.sAdd(key,value);
        //2、 需要将key和sinterKey做交集操作，并拿到返回的set（RedisClient没提供，自己写的~~）
        Set<Object> result = redisTemplate.opsForSet().intersect(key, sinterKey);
        //3、 将key删除
        redisClient.delete(key);
        //4、 返回交集结果
        return result;
    }

    //往 Redis 的某个 ZSet 里插入一条记录
    @PostMapping(value = "/cache/zadd/{key}/{score}/{member}")
    public Boolean zadd(@PathVariable(value = "key")String key,
                        @PathVariable(value = "score")Long score,
                        @PathVariable(value = "member")Object member){
        log.info("【缓存模块】 zaddLong方法，存储key = {}，存储score = {}，存储value = {}", key,score, member);
        Boolean result = redisClient.zAdd(key, member, score);
        return result;
    }

    //查某个 ZSet 里，分数在 [start, end] 范围内的记录有几条
    @GetMapping(value = "/cache/zrangebyscorecount/{key}/{start}/{end}")
    public int zRangeByScoreCount(@PathVariable(value = "key") String key,
                                  @PathVariable(value = "start") Double start,
                                  @PathVariable(value = "end") Double end) {
        log.info("【缓存模块】 zRangeByScoreCount方法，查询key = {},start = {},end = {}", key,start,end);
        Set<ZSetOperations.TypedTuple<Object>> values = redisTemplate.opsForZSet().rangeByScoreWithScores(key, start, end);
        if(values != null){
            return values.size();
        }
        return 0;
    }

    @DeleteMapping(value = "/cache/zremove/{key}/{member}")
    public void zRemove(@PathVariable(value = "key") String key,@PathVariable(value = "member") String member) {
        log.info("【缓存模块】 zRemove方法，删除key = {},member = {}", key,member);
        redisClient.zRemove(key,member);
    }

    //对 hash 的某个字段按 delta 自增/自减（delta 为正则加、为负则减），一般用于扣减客户余额（扣费时 delta 传负数），返回自增后的值
    @PostMapping(value = "/cache/hincrby/{key}/{field}/{delta}")
    public Long hIncrBy(@PathVariable(value = "key") String key,
                        @PathVariable(value = "field") String field,
                        @PathVariable(value = "delta") Long delta){
        log.info("【缓存模块】 hIncrBy方法，自增   key = {},field = {}，number = {}", key,field,delta);
        Long result = redisClient.hIncrementBy(key, field, delta);
        log.info("【缓存模块】 hIncrBy方法，自增   key = {},field = {}，number = {},剩余数值为 = {}", key,field,delta,result);
        return result;
    }

    @PostMapping(value = "/cache/keys/{pattern}")
    public Set<String> keys(@PathVariable String pattern){
        log.info("【缓存模块】 keys方法，根据pattern查询key的信息  pattern = {}" ,pattern);
        Set<String> keys = redisTemplate.keys(pattern);
        log.info("【缓存模块】 keys方法，根据pattern查询key的信息  pattern = {},查询出全部的key信息 keys = {}" ,pattern,keys);
        return keys;
    }
}
