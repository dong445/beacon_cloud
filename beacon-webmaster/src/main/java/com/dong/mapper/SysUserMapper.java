package com.dong.mapper;

import com.dong.entity.SmsUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysUserMapper {

    @Select("SELECT * FROM sms_user WHERE username = #{username} AND is_delete = 0 LIMIT 1")
    SmsUser selectUserByUsername(@Param("username") String username);

    @Update("UPDATE sms_user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Select("SELECT COUNT(*) FROM sms_user WHERE is_delete = 0")
    long countUsers();

    // 字段名对齐前端 user.js 的 columns：usercode/realName/status 做映射，
    // 表里没有的 email/type/clientid 返回 null；password 不查出真实值
    @Select("SELECT id, username AS usercode, NULL AS password, NULL AS email, nickname AS realName, " +
            "NULL AS type, (1 - is_delete) AS status, NULL AS clientid " +
            "FROM sms_user WHERE is_delete = 0 ORDER BY id LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectUserList(@Param("offset") int offset, @Param("limit") int limit);
}
