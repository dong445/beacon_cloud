package com.dong.common.util;

import com.dong.common.enums.CMPP2ResultEnums;

import java.util.HashMap;
import java.util.Map;

/**
 * CMPP2.0 提交短信应答（CMPP_SUBMIT_RESP）结果码工具
 */
public class CMPP2ResultUtil {

    private static Map<Integer,String> results = new HashMap<>();

    static{
        CMPP2ResultEnums[] cmpp2ResultEnums = CMPP2ResultEnums.values();
        for (CMPP2ResultEnums cmpp2ResultEnum : cmpp2ResultEnums) {
            results.put(cmpp2ResultEnum.getResult(),cmpp2ResultEnum.getMsg());
        }
    }

    /**
     * 通过result结果拿到对应的错误信息
     * @param result
     * @return
     */
    public static String getResultMessage(Integer result){
        return results.get(result);
    }

}
