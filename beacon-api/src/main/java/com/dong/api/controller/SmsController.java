package com.dong.api.controller;

import com.dong.api.enums.SmsCodeEnum;
import com.dong.api.form.SingleSendForm;
import com.dong.api.util.R;
import com.dong.api.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsController {
    //produces = "application/json;charset=utf-8"等于告诉 Spring MVC：这个接口的响应体是 JSON，且用 UTF-8 编码
    @PostMapping(value = "/single_send",produces = "application/json;charset=utf-8")
    public ResultVO singleSend(@RequestBody @Validated SingleSendForm singleSendForm, BindingResult bindingResult){
        //1. 校验参数
        if (bindingResult.hasErrors()){
            String msg = bindingResult.getFieldError().getDefaultMessage();
            log.info("【接口模块-单条短信Controller】 参数不合法 msg = {}",msg);
            return R.error(SmsCodeEnum.PARAMETER_ERROR.getCode(),msg);
        }
        return R.ok();
    }
}
