package com.dong.mapper;

import com.dong.domain.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SmsMenuMapper {

    @Select("SELECT DISTINCT m.id AS menuId, m.name, m.parent_id AS parentId, m.url, " +
            "m.icon, m.type, m.sort AS orderNum " +
            "FROM sms_menu m " +
            "JOIN sms_role_menu rm ON m.id = rm.menu_id " +
            "JOIN sms_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.type IN (0, 1) " +
            "AND m.is_delete = 0 AND rm.is_delete = 0")
    List<MenuVO> selectMenusByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM sms_menu WHERE is_delete = 0")
    long countMenus();

    // 字段名对齐前端 menu.js 的 columns：parentName 由父菜单 join 出来、orderNum 映射 sort，
    // 表里没有的 perms 返回 null
    @Select("SELECT m.id, m.name, p.name AS parentName, m.icon, m.url, NULL AS perms, m.type, m.sort AS orderNum " +
            "FROM sms_menu m LEFT JOIN sms_menu p ON m.parent_id = p.id " +
            "WHERE m.is_delete = 0 ORDER BY m.sort, m.id LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectMenuList(@Param("offset") int offset, @Param("limit") int limit);
}
