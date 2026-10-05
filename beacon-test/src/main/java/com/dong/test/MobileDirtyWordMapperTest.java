package com.dong.test;

import com.dong.test.client.CacheClient;
import com.dong.test.mapper.MobileDirtyWordMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MobileDirtyWordMapperTest {

    @Autowired
    private MobileDirtyWordMapper mapper;

    @Autowired
    private CacheClient cacheClient;

    @Test
    public void findAll() {
        List<String> dirtyWords = mapper.findDirtyWord();

        cacheClient.saddStr("dirty_word",dirtyWords.toArray(new String[]{}));
    }
}