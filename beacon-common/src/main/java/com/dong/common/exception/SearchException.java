package com.dong.common.exception;

import com.dong.common.enums.ExceptionEnums;
import lombok.Getter;

@Getter
public class SearchException extends RuntimeException {

    private Integer code;

    public SearchException(String message, Integer code) {
        super(message);
        this.code = code;
    }


    public SearchException(ExceptionEnums enums) {
        super(enums.getMsg());
        this.code = enums.getCode();
    }

}
