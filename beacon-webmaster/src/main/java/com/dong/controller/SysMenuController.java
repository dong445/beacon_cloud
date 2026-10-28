package com.dong.controller;

import com.dong.common.BaseResponse;
import com.dong.common.TableDataInfo;
import com.dong.domain.LoginUser;
import com.dong.domain.MenuVO;
import com.dong.mapper.SmsMenuMapper;
import com.dong.service.SysMenuService;
import com.dong.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SmsMenuMapper smsMenuMapper;

    /** 当前用户可见的菜单树 */
    @GetMapping("/user")
    public BaseResponse getUserMenus(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        List<MenuVO> menus = sysMenuService.selectMenuTreeByUserId(loginUser.getUser().getId());
        return BaseResponse.success(menus);
    }

    /** 菜单列表（bootstrapTable 分页，字段名对齐前端 menu.js 的 columns） */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(defaultValue = "10") int limit,
                              @RequestParam(defaultValue = "0") int offset) {
        long total = smsMenuMapper.countMenus();
        List<Map<String, Object>> rows = smsMenuMapper.selectMenuList(offset, limit);
        return TableDataInfo.of(total, rows);
    }
}
