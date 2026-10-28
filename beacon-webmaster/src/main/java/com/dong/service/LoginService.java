package com.dong.service;

/**
 * 登录业务。
 */
public interface LoginService {

    /** 校验验证码 + 账号密码，成功后返回 token */
    String login(String username, String password, String code, String uuid);
}
