package com.dong.test;

import com.dong.test.client.CacheClient;
import com.dong.test.mapper.ClientSignMapper;
import com.dong.test.mapper.entity.ClientSign;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ClientSignMapperTest {

    @Autowired
    private ClientSignMapper mapper;

    @Autowired
    private CacheClient cacheClient;

    /**
     * 客户签名：set结构
     * key：client_sign:clientId
     * value：对应客户签名的json
     */
    @Test
    public void findByClientId() {
        List<ClientSign> list = mapper.findByClientId(1L);
        for (ClientSign clientSign : list) {
            System.out.println(clientSign);
        }

        //对象列表转成map列表
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map> value = list.stream().map(cs -> {
            try {
                return objectMapper.readValue(objectMapper.writeValueAsString(cs), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        cacheClient.sadd("client_sign:1", value.toArray(new Map[]{}));
    }
}
