package com.dong.exception;

import com.dong.common.BaseResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public BaseResponse badCredentials(BadCredentialsException e) {
        return BaseResponse.error(500, "用户名或密码错误");
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse exception(Exception e) {
        return BaseResponse.error(500, e.getMessage());
    }
}
