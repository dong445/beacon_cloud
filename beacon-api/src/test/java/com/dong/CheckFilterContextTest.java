package com.dong;

import com.dong.api.filter.CheckFilterContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CheckFilterContextTest{
    @Autowired
    private CheckFilterContext checkFilterContext;

    @Test
    public void check(){
        Object obj=new Object();
        checkFilterContext.check(obj);
    }
}
