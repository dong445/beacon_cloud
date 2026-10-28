package com.dong.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SmsRoleMapper {

    @Select("SELECT r.name FROM sms_role r " +
            "JOIN sms_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_delete = 0 AND ur.is_delete = 0")
    List<String> selectRoleNamesByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM sms_role WHERE is_delete = 0")
    long countRoles();

    // 字段名对齐前端 role.js 的 columns：status 用 is_delete 反转，表里没有的 remark 返回 null
    @Select("SELECT id, name, NULL AS remark, (1 - is_delete) AS status " +
            "FROM sms_role WHERE is_delete = 0 ORDER BY id LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectRoleList(@Param("offset") int offset, @Param("limit") int limit);
}
