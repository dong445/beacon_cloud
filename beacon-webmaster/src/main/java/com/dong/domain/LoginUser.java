package com.dong.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dong.entity.SmsUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户，Spring Security 通过它认识当前登录者。
 * 存入 Redis 时用 fastjson 序列化，密码和权限列表不落库（序列化时忽略）。
 */
public class LoginUser implements UserDetails, Serializable {

    private SmsUser user;
    /** 权限集合（这里存角色名） */
    private Set<String> permissions;
    /** 本次登录的唯一标识 uuid */
    private String token;
    private Long loginTime;
    private Long expireTime;

    public LoginUser() {
    }

    public LoginUser(SmsUser user, Set<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    @Override
    @JSONField(serialize = false)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public SmsUser getUser() {
        return user;
    }

    public void setUser(SmsUser user) {
        this.user = user;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
