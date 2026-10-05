package com.dong.strategy.filter.impl;

import com.dong.common.constant.RabbitMQConstants;
import com.dong.common.constant.SmsConstant;
import com.dong.common.enums.ExceptionEnums;
import com.dong.common.exception.StrategyException;
import com.dong.common.model.StandardSubmit;
import com.dong.strategy.filter.StrategyFilter;
import com.dong.strategy.util.HutoolDFAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "hutoolDFADirtyWord")
@Slf4j
public class DirtyWordHutoolDFAStrategyFilter implements StrategyFilter {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-敏感词校验03】   校验ing…………");
        //1、 获取短信内容
        String text = submit.getText();

        //2、 调用DFA查看敏感词
        List<String> dirtyWords = HutoolDFAUtil.getDirtyWord(text);

        //4、 根据返回的set集合，判断是否包含敏感词
        if (dirtyWords != null && dirtyWords.size() > 0) {
            //5、 如果有敏感词，抛出异常 / 其他操作。。
            log.info("【策略模块-敏感词校验】   短信内容包含敏感词信息， dirtyWords = {}", dirtyWords);
            // 封装错误信息
            submit.setErrorMsg(ExceptionEnums.HAVE_DIRTY_WORD.getMsg() + "dirtyWords = " + dirtyWords.toString());
            submit.setReportState(SmsConstant.REPORT_FAIL);
            //发送信息到日志队列
            rabbitTemplate.convertAndSend(RabbitMQConstants.SMS_WRITE_LOG,submit);

            // 还需要做其他处理
            throw new StrategyException(ExceptionEnums.HAVE_DIRTY_WORD);
        }
    }
}