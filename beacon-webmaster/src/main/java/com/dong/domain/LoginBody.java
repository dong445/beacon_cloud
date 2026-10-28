package com.dong.domain;

import java.io.Serializable;

/**
 * 登录入参，对应前端 login.html 提交的数据。
 */
public class LoginBody implements Serializable {

    private String username;
    private String password;
    /** 验证码 */
    private String code;
    /** 验证码对应的 uuid */
    private String uuid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
