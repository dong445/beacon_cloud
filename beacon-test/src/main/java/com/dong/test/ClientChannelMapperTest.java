package com.dong.test;

import com.dong.test.client.CacheClient;
import com.dong.test.mapper.ClientChannelMapper;
import com.dong.test.mapper.entity.ClientChannel;
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
public class ClientChannelMapperTest {

    /*
    * ClientChannel是「哪个客户能用哪个通道」（clientId → channelId 的绑定关系）
    * */

    @Autowired
    private ClientChannelMapper mapper;

    @Autowired
    private CacheClient cacheClient;


    @Test
    public void findAll() throws JsonProcessingException {
        List<ClientChannel> list = mapper.findAll();
        for (ClientChannel clientChannel : list) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.readValue(objectMapper.writeValueAsString(clientChannel), Map.class);
            cacheClient.sadd("client_channel:" + clientChannel.getClientId(),map);
        }
    }
}