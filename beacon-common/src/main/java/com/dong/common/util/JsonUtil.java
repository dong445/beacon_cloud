package com.dong.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author zjw
 * @description
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String obj2JSON(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("转换JSON失败！");
        }
    }

}
