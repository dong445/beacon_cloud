package com.dong.service;

import com.dong.common.RedisCache;
import com.dong.common.constant.Constants;
import com.dong.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TokenService tokenService;

    public String login(String username, String password, String code, String uuid) {
        // 1. 校验验证码（一次性，用完即删）
        String captchaKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCaptcha(captchaKey);
        redisCache.deleteObject(captchaKey);
        if (captcha == null || !captcha.equalsIgnoreCase(code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 2. 校验账号密码（内部会调用 UserDetailsServiceImpl.loadUserByUsername）
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // 3. 生成 token
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return tokenService.createToken(loginUser);
    }
}
