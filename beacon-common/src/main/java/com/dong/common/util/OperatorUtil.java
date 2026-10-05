package com.dong.common.util;

import com.dong.common.enums.MobileOperatorEnum;

import java.util.HashMap;
import java.util.Map;

public class OperatorUtil {
    private static Map<String,Integer> operators=new HashMap<>();

    static {
        MobileOperatorEnum[] operatorEnums=MobileOperatorEnum.values();
        for (MobileOperatorEnum operatorEnum :operatorEnums){
            operators.put(operatorEnum.getOperatorName(),operatorEnum.getOperatorId());
        }
    }

    //通过运营商名称获取运营商Id
    public static Integer getOperatorIdByOperatorName(String operatorName){
        return operators.get(operatorName);
    }
}
