package com.dong.strategy.util;

public class ClientBalanceUtil {


    /**
     * 后期如果要给客户指定欠费的额度等级，再重写方法
     * @param clientId
     * @return
     */
    public static Long getClientAmountLimit(Long clientId){
        return -10000L;
    }

}