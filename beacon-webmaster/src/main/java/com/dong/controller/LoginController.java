package com.dong.controller;

import com.dong.common.BaseResponse;
import com.dong.domain.LoginBody;
import com.dong.service.LoginService;
import com.dong.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/sys/login")
    public BaseResponse login(@RequestBody LoginBody loginBody) {
        String token = loginService.login(
                loginBody.getUsername(), loginBody.getPassword(),
                loginBody.getCode(), loginBody.getUuid());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return BaseResponse.success(data);
    }

    /** 退出登录：删除 Redis 会话，前端同时清掉本地 token */
    @PostMapping("/logout")
    public BaseResponse logout(HttpServletRequest request) {
        tokenService.deleteLoginUser(request);
        return BaseResponse.success();
    }
}
