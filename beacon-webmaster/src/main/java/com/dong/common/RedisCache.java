package com.dong.common;

import com.alibaba.fastjson.JSON;
import com.dong.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具：验证码用 String 存，登录用户用 fastjson 序列化后存 String。
 */
@Component
public class RedisCache {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /** 保存验证码 */
    public void setCaptcha(String key, String code, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, code, timeout, unit);
    }

    /** 读取验证码 */
    public String getCaptcha(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 保存登录用户（JSON 字符串） */
    public void setLoginUser(String key, LoginUser loginUser, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, JSON.toJSONString(loginUser), timeout, unit);
    }

    /** 读取登录用户 */
    public LoginUser getLoginUser(String key) {
        String json = redisTemplate.opsForValue().get(key);
        return StringUtils.hasText(json) ? JSON.parseObject(json, LoginUser.class) : null;
    }

    /** 删除 key */
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }
}
