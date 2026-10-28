package com.dong.common;

import java.io.Serializable;

/**
 * 统一返回体。code = 0 表示成功（与前端 login.html / common.js 的 result.code == 0 对齐）。
 */
public class BaseResponse implements Serializable {

    private int code;
    private String msg;
    private Object data;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static BaseResponse success(Object data) {
        return new BaseResponse(0, "操作成功", data);
    }

    public static BaseResponse success() {
        return new BaseResponse(0, "操作成功", null);
    }

    public static BaseResponse error(int code, String msg) {
        return new BaseResponse(code, msg, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
