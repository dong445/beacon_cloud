package com.dong.service;

import com.dong.domain.MenuVO;

import java.util.List;

/**
 * 菜单业务。
 */
public interface SysMenuService {

    /** 查询用户可见的菜单并组装成树 */
    List<MenuVO> selectMenuTreeByUserId(Integer userId);
}
