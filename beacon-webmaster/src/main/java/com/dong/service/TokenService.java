package com.dong.service;

import com.dong.domain.LoginUser;

import javax.servlet.http.HttpServletRequest;

/**
 * token 生成 / 解析 / 续期。JWT 里只放一个 uuid，真正的会话信息存 Redis（若依风格）。
 */
public interface TokenService {

    /** 生成 token，并把登录用户写入 Redis */
    String createToken(LoginUser loginUser);

    /** 把登录用户（含过期时间）写入 Redis */
    void refreshToken(LoginUser loginUser);

    /** 从请求头解析 token，还原登录用户 */
    LoginUser getLoginUser(HttpServletRequest request);

    /** 临近过期时续期 */
    void verifyToken(LoginUser loginUser);

    /** 退出登录：删除 Redis 里的会话 */
    void deleteLoginUser(HttpServletRequest request);
}
