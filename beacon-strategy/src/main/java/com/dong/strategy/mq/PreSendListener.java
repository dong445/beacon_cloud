package com.dong.strategy.mq;

import com.dong.common.constant.RabbitMQConstants;
import com.dong.common.exception.StrategyException;
import com.dong.common.model.StandardSubmit;
import com.dong.strategy.filter.StrategyFilterContext;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PreSendListener{

    /**
     * 整个策略模块的校验
     */
    @Autowired
    private StrategyFilterContext filterContext;

    @RabbitListener(queues = RabbitMQConstants.SMS_PRE_SEND)
    public void listen(StandardSubmit submit, Message message, Channel channel) throws IOException {
        log.info("【策略模块-接收消息】 接收到接口模块发送的消息 submit = {}",submit);
        // 处理业务…………
        try {
            filterContext.strategy(submit);
            log.info("【策略模块-消费完毕】手动ack");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (StrategyException e) {
            log.error("【策略模块-消费失败】 检验未通过，msg={}",e.getMessage());
            // 校验失败是正常业务结果，已经写过日志、推过报告，这条消息不应再重放
            // 这个代码主要是为了让重启项目时，不再让sms_pre_send_topic队列的unack的消息重新跑一趟队列，增加消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }

    }
}
