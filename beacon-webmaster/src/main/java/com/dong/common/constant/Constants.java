package com.dong.common.constant;

public class Constants {
    // 登录用户在 Redis 里的 key 前缀
    public static final String LOGIN_USER_KEY = "login_user:";
    // 验证码在 Redis 里的 key 前缀
    public static final String CAPTCHA_CODE_KEY = "captcha_code:";
    // token 请求头前缀
    public static final String TOKEN_PREFIX = "Bearer ";
}