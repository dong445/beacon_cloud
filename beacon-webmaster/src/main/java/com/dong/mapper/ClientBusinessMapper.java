package com.dong.mapper;

import com.dong.entity.ClientBusiness;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClientBusinessMapper {

    @Select("SELECT * FROM client_business WHERE is_delete = 0")
    List<ClientBusiness> findAll();

    @Select("SELECT * FROM client_business WHERE extend1 = #{userId} AND is_delete = 0")
    List<ClientBusiness> findByUserId(@Param("userId") String userId);
}
