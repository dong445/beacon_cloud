package com.dong.service;

import com.dong.domain.LoginUser;
import com.dong.entity.SmsUser;
import com.dong.mapper.SmsRoleMapper;
import com.dong.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SmsRoleMapper smsRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SmsUser user = sysUserMapper.selectUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 权限集合：目前用角色名充当权限（sms_menu 没有独立 perms 字段）
        List<String> roleNames = smsRoleMapper.selectRoleNamesByUserId(user.getId());
        Set<String> permissions = new HashSet<>(roleNames);
        return new LoginUser(user, permissions);
    }
}
