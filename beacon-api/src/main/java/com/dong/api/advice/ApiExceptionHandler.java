package com.dong.api.advice;

import com.dong.api.util.R;
import com.dong.api.vo.ResultVO;
import com.dong.common.exception.ApiException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler{

    @ExceptionHandler(ApiException.class)
    public ResultVO apiException(ApiException ex){
        return R.error(ex);
    }
}
