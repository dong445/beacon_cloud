package com.dong.test.mapper;

import com.dong.test.mapper.entity.Channel;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChannelMapper {

    @Select("select * from channel where is_delete = 0")
    List<Channel> findAll();

}