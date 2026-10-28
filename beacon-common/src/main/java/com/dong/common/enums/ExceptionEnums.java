package com.dong.common.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnums {

    ERROR_APIKEY(-1,"非法的apikey"),
    IP_NOT_WHITE(-2,"请求的ip不在白名单内"),
    ERROR_SIGN(-3,"无可用签名"),
    ERROR_TEMPLATE(-4,"无可用模板"),
    ERROR_MOBILE(-5,"手机号格式不正确"),
    BALANCE_NOT_ENOUGH(-6,"手客户余额不足"),
    PARAMETER_ERROR(-10,"参数不合法！"),
    SNOWFLAKE_TIME_BACK(-11, "雪花算法机器id或服务id超出最大范围"),
    SNOWFLAKE_OUT_OF_RANGE(-12,"雪花算法服务器出现时间回拨问题" ),
    HAVE_DIRTY_WORD(-13,"当前短信内容中包含敏感词信息！"),
    BLACK_GLOBAL(-14,"当前手机号为平台黑名单！"),
    BLACK_CLIENT(-15,"当前手机号为客户黑名单！"),
    ONE_MINUTE_LIMIT(-16,"1分钟限流规则生效，无法发送短信"),
    ONE_HOUR_LIMIT(-17,"1小时限流规则生效，无法发送短信"),
    NO_CHANNEL(-18,"没有选择到合适的通道！"),
    SEARCH_INDEX_ERROR(-19,"添加文档信息失败！"),
    SEARCH_UPDATE_ERROR(-20,"修改文档信息失败！"),

    KAPACHA_ERROR(-100,"验证码错误！"),
    AUTHEN_ERROR(-101,"用户名或密码错误！"),
    NOT_LOGIN(-102,"用户未登录！"),
    USER_MENU_ERROR(-103,"查询用户的菜单信息失败！"),
    SMS_NO_AUTHOR(-104,"当前登录用户没有权限查询当前短信信息"),
    UNKNOWN_ERROR(-999,"未知错误！");
    private Integer code;

    private String msg;

    ExceptionEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
