package com.dong.service;

import com.dong.domain.MenuVO;
import com.dong.mapper.SmsMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SysMenuService {

    @Autowired
    private SmsMenuMapper smsMenuMapper;

    /** 查询用户可见的菜单并组装成树 */
    public List<MenuVO> selectMenuTreeByUserId(Integer userId) {
        List<MenuVO> menus = smsMenuMapper.selectMenusByUserId(userId);
        return buildTree(menus, 0L);
    }

    private List<MenuVO> buildTree(List<MenuVO> all, long parentId) {
        List<MenuVO> result = new ArrayList<>();
        for (MenuVO menu : all) {
            if (menu.getParentId() != null && menu.getParentId() == parentId) {
                long id = menu.getMenuId() == null ? 0L : menu.getMenuId().longValue();
                menu.setList(buildTree(all, id));
                result.add(menu);
            }
        }
        result.sort(Comparator.comparing(MenuVO::getOrderNum,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return result;
    }
}
