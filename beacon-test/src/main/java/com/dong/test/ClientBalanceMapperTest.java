package com.dong.test;

import com.dong.test.client.CacheClient;
import com.dong.test.mapper.ClientBalanceMapper;
import com.dong.test.mapper.entity.ClientBalance;
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
public class ClientBalanceMapperTest {

    @Autowired
    private ClientBalanceMapper mapper;

    @Autowired
    private CacheClient cacheClient;

    /**
     * 客户余额：string结构
     * key：client_balance:clientId
     * value：客户余额
     */
    @Test
    public void findByClientId() throws JsonProcessingException {
        ClientBalance cb=mapper.findByClientId(1L);
        System.out.println(cb);

        ObjectMapper objectMapper=new ObjectMapper();
        Map map=objectMapper.readValue(objectMapper.writeValueAsString(cb),Map.class);
        // 存成 hash：client_balance:1 -> { clientId: 1, balance: 1000000 }
        cacheClient.hmset("client_balance:1" , map);
    }
}
