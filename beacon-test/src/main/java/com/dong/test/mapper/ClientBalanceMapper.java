package com.dong.test.mapper;

import com.dong.test.mapper.entity.ClientBalance;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientBalanceMapper {

    @Select("select * from client_balance where client_id = #{clientId}")
    ClientBalance findByClientId(@Param("clientId") Long clientId);

}
