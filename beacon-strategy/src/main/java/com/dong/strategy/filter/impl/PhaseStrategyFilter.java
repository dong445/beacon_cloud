package com.dong.strategy.filter.impl;

import com.dong.common.constant.CacheConstant;
import com.dong.common.constant.RabbitMQConstants;
import com.dong.common.model.StandardSubmit;
import com.dong.common.util.OperatorUtil;
import com.dong.strategy.client.BeaconCacheClient;
import com.dong.strategy.filter.StrategyFilter;
import com.dong.strategy.util.MobileOperatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service(value = "phase")
@Slf4j
public class PhaseStrategyFilter implements StrategyFilter {

    @Autowired
    private MobileOperatorUtil mobileOperatorUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 切分手机号前7位
     */
    private final int MOBILE_START = 0;
    private final int MOBILE_END = 7;
    /**
     * 校验的长度
     */
    private final int LENGTH = 2;
    /**
     * 分割区域和运营商的标识
     */
    private final String SEPARATE = ",";
    /**
     * 未知的情况
     */
    private final String UNKNOWN = "未知 未知,未知";


    @Autowired
    private BeaconCacheClient cacheClient;


    @Override
    public void strategy(StandardSubmit submit) {
      log.info("【策略模块-号段补齐】   补全ing…………");

        //1、根据手机号前7位，查询手机号信息
        String mobile = submit.getMobile().substring(MOBILE_START, MOBILE_END);
        String mobileInfo = cacheClient.getString(CacheConstant.PHASE + mobile);
        log.info("【号段补齐】 mobile={}，Redis值={}", mobile, mobileInfo);


        /*
        * getMobileInfo: 是给 if 语句起的标签；
          break getMobileInfo; 表示跳出这个被标记的 if 块，
          * 从而跳过后面的 mobileInfo = UNKNOWN;
        * */
        getMobileInof: if (StringUtils.isEmpty(mobileInfo)){
            //查询不到，需要调用三方接口，查询手机号对应信息
            mobileInfo =mobileOperatorUtil.getMobileInfoBy360(mobile);
            if (!StringUtils.isEmpty(mobileInfo)){
                //调用三方查到信息后，发送消息到MQ，并且同步到MySQL和Redis
                rabbitTemplate.convertAndSend(RabbitMQConstants.MOBILE_AREA_OPERATOR,submit.getMobile());
                log.info("【号段补齐】 已发送MQ mobile={}", submit.getMobile());
                break getMobileInof;
            }
            mobileInfo= UNKNOWN;
        }


        //无论是Redis还是三方接口查询到之后，封装到StandardSubmit对象中
        String[] areaAndOperator = mobileInfo.split(SEPARATE);
        if (areaAndOperator.length == LENGTH) {
            submit.setArea(areaAndOperator[0]);
            submit.setOperatorId(OperatorUtil.getOperatorIdByOperatorName(areaAndOperator[1]));
        }
    }
}
