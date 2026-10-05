package com.dong.strategy.filter.impl;

import com.dong.common.constant.CacheConstant;
import com.dong.common.constant.RabbitMQConstants;
import com.dong.common.constant.SmsConstant;
import com.dong.common.enums.ExceptionEnums;
import com.dong.common.exception.StrategyException;
import com.dong.common.model.StandardReport;
import com.dong.common.model.StandardSubmit;
import com.dong.strategy.client.BeaconCacheClient;
import com.dong.strategy.filter.StrategyFilter;
import com.dong.strategy.util.ErrorSendMsgUtil;
import com.dong.strategy.util.HutoolDFAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service(value = "hutoolDFADirtyWord")
@Slf4j
public class DirtyWordHutoolDFAStrategyFilter implements StrategyFilter {
    @Autowired
    private ErrorSendMsgUtil sendMsgUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BeaconCacheClient cacheClient;

    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-敏感词校验03】   校验ing…………");
        //获取短信内容
        String text = submit.getText();

        //调用DFA查看敏感词
        List<String> dirtyWords = HutoolDFAUtil.getDirtyWord(text);

        //根据返回的set集合，判断是否包含敏感词
        if (dirtyWords != null && dirtyWords.size() > 0) {
            //如果有敏感词，抛出异常 / 其他操作。。
            log.info("【策略模块-敏感词校验】   短信内容包含敏感词信息， dirtyWords = {}", dirtyWords);
            // 封装错误信息
            sendMsgUtil.sendWriteLog(submit,dirtyWords);

            //发送状态报告的消息前，需要将report对象数据封装
            sendMsgUtil.sendPushReport(submit);

            // 还需要做其他处理
            throw new StrategyException(ExceptionEnums.HAVE_DIRTY_WORD);
        }
    }
}