package com.dong.controller;

import com.dong.common.BaseResponse;
import com.dong.common.RedisCache;
import com.dong.common.constant.Constants;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class CaptchaController {

    @Autowired
    private RedisCache redisCache;

    @GetMapping("/captchaImage")
    public BaseResponse getCode() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String code = captcha.text().toLowerCase();

        // 存 Redis，5 分钟过期
        redisCache.setCaptcha(Constants.CAPTCHA_CODE_KEY + uuid, code, 5, TimeUnit.MINUTES);

        Map<String, Object> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("img", captcha.toBase64());
        return BaseResponse.success(data);
    }
}
