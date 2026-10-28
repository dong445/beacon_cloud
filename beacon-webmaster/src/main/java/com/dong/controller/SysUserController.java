package com.dong.controller;

import com.dong.common.BaseResponse;
import com.dong.common.TableDataInfo;
import com.dong.domain.LoginUser;
import com.dong.domain.UpdatePasswordBody;
import com.dong.entity.SmsUser;
import com.dong.mapper.SysUserMapper;
import com.dong.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 当前登录用户信息 */
    @GetMapping("/info")
    public BaseResponse getInfo(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SmsUser user = loginUser.getUser();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("permissions", loginUser.getPermissions());
        return BaseResponse.success(data);
    }

    /** 修改密码：校验原密码后，用 BCrypt 加密新密码写回数据库 */
    @PostMapping("/password")
    public BaseResponse updatePassword(@RequestBody UpdatePasswordBody body, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return BaseResponse.error(401, "未登录");
        }
        if (body.getNewPassword() == null || body.getNewPassword().isEmpty()) {
            return BaseResponse.error(500, "新密码不能为空");
        }
        // 重新查库拿最新密码哈希校验，不依赖 Redis 缓存里的内容
        SmsUser dbUser = sysUserMapper.selectUserByUsername(loginUser.getUsername());
        if (dbUser == null || !passwordEncoder.matches(body.getPassword(), dbUser.getPassword())) {
            return BaseResponse.error(500, "原密码错误");
        }
        String newHash = passwordEncoder.encode(body.getNewPassword());
        sysUserMapper.updatePassword(dbUser.getId(), newHash);
        return BaseResponse.success();
    }

    /** 用户列表（bootstrapTable 分页，字段名对齐前端 user.js 的 columns） */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(defaultValue = "10") int limit,
                              @RequestParam(defaultValue = "0") int offset) {
        long total = sysUserMapper.countUsers();
        List<Map<String, Object>> rows = sysUserMapper.selectUserList(offset, limit);
        return TableDataInfo.of(total, rows);
    }
}
