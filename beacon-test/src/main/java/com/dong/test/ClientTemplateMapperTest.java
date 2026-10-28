package com.dong.test;

import com.dong.test.client.CacheClient;
import com.dong.test.mapper.ClientTemplateMapper;
import com.dong.test.mapper.entity.ClientTemplate;
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
public class ClientTemplateMapperTest {

    @Autowired
    private ClientTemplateMapper mapper;

    @Autowired
    private CacheClient cacheClient;

    /**
     * 客户模板：set结构
     * key：client_template:signId
     * value：对应签名模板的json
     */
    @Test
    public void findBySignId() {
        List<ClientTemplate> ct1 = mapper.findBySignId(15L);
        List<ClientTemplate> ct2 = mapper.findBySignId(24L);
        for (ClientTemplate clientTemplate : ct1) {
            System.out.println(clientTemplate);
        }
        // ct2在现有的库中没有数据
        System.out.println(ct2);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map> value = ct1.stream().map(ct -> {
            try {
                return objectMapper.readValue(objectMapper.writeValueAsString(ct), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        cacheClient.sadd("client_template:15", value.toArray(new Map[]{}));
    }
}
