package com.dong.mapper;

import com.dong.entity.SmsUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper {

    @Select("SELECT * FROM sms_user WHERE username = #{username} AND is_delete = 0 LIMIT 1")
    SmsUser selectUserByUsername(@Param("username") String username);
}
