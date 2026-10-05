package com.dong.test.mapper;

import com.dong.test.mapper.entity.ClientBusiness;
import com.dong.test.mapper.entity.MobileArea;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientBusinessMapper {

    @Select("select * from client_business where id = #{id}")
    ClientBusiness findById(@Param("id") Long id);


    //这里不能驼峰，驼峰在java类中
    @Select("select mobile_number, mobile_area, mobile_type from mobile_area")
    List<MobileArea> findAll();


}