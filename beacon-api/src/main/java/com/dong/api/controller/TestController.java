package com.dong.api.controller;

import com.dong.api.filter.CheckFilterContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController{
    @Autowired
    private CheckFilterContext checkFilterContext;

    @GetMapping("/api/test")
    public void test(){
        //接口的 void 返回值让 Spring 去找名为 api/test 的视图，项目里没有视图解析器，所以 404
        //程序本身没错
        System.out.println("====================================");
        //checkFilterContext.check(new Object());
    }
}
