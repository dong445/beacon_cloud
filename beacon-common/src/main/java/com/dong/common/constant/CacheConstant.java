package com.dong.common.constant;

public interface CacheConstant{
    //客户信息
    String CLIENT_BUSINESS="client_business:";

    //客户签名
    String CLIENT_SIGN="client_sign:";

    //客户签名的模板
    String CLIENT_TEMPLATE="client_template:";

    //客户的余额
    String CLIENT_BALANCE="client_balance:";

    //号段补全
    String PHASE="phase:";

    /**
     *  敏感词前缀
     */
    String DIRTY_WORD = "dirty_word";

    /**
     *  黑名单的前缀
     */
    String BLACK = "black:";

    /**
     *  间隔符
     */
    String SEPARATE = ":";

    /**
     *  携号转网
     */
    String TRANSFER = "transfer:";

    /**
     * 1分钟的限流规则的key
     */
    String LIMIT_MINUTES = "limit:minutes:";

    /**
     * 1小时的限流规则的key
     */
    String LIMIT_HOURS = "limit:hours:";

    /**
     * 客户和通道绑定的信息
     */
    String CLIENT_CHANNEL = "client_channel:";

    /**
     * 通道信息的前缀
     */
    String CHANNEL = "channel:";
}
