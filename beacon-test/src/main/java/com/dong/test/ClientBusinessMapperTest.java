package com.dong.test;

import com.dong.test.client.CacheClient;
import com.dong.test.mapper.ClientBusinessMapper;
import com.dong.test.mapper.entity.ClientBusiness;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ClientBusinessMapperTest {

    @Autowired
    private ClientBusinessMapper mapper;

    @Autowired
    private CacheClient cacheClient;

    /**
     * 客户信息：hash结构
     * key：client_business:apikey
     * value：用户信息的json
     */
    @Test
    public void findById() throws JsonProcessingException {
        ClientBusiness cb = mapper.findById(1L);
        System.out.println(cb);

        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(objectMapper.writeValueAsString(cb), Map.class);
        cacheClient.hmset("client_business:" + cb.getApikey(), map);
    }
}
