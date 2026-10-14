package com.dong.test;

import com.dong.test.client.CacheClient;
import com.dong.test.mapper.ChannelMapper;
import com.dong.test.mapper.entity.Channel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ChannelMapperTest {

    /*
    * Channel 是「通道本身长什么样」（通道名、价格、IP 端口等）
    * */

    @Autowired
    private ChannelMapper mapper;

    @Autowired
    private CacheClient cacheClient;


    @Test
    public void findAll() throws JsonProcessingException {
        List<Channel> list = mapper.findAll();
        for (Channel channel : list) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.readValue(objectMapper.writeValueAsString(channel), Map.class);
            cacheClient.hmset("channel:" + channel.getId(),map);
        }
    }
}