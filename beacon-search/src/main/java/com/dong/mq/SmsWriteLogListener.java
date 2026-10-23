package com.dong.mq;

import com.dong.common.constant.RabbitMQConstants;
import com.dong.common.exception.SearchException;
import com.dong.common.model.StandardSubmit;
import com.dong.common.util.JsonUtil;
import com.dong.service.SearchService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class SmsWriteLogListener {

    @Autowired
    private SearchService searchService;

    private final String INDEX = "sms_submit_log_";

    @RabbitListener(queues = RabbitMQConstants.SMS_WRITE_LOG)
    public void consume(StandardSubmit submit, Channel channel, Message message) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String index = INDEX + getYear();
        String id = submit.getSequenceId().toString();
        String json = JsonUtil.obj2JSON(submit);

        log.info("接收到存储日志的信息   submit = {}", submit);

        //调用搜索模块的添加方法，完成添加操作。失败重试 3 次
        boolean success = false;
        for (int i = 1; i <= 3 && !success; i++) {
            try {
                searchService.index(index, id, json);
                success = true;
            } catch (SearchException e) {
                //限流导致重复写入
                //文档已存在（重复写），幂等成功，不重试、不记错误
                log.info("【搜索模块-写入】 文档已存在，视为成功 index = {}, id = {}", index, id);
                success = true;
            } catch (Exception e) {
                // 只有其他异常（如 IOException 超时）才重试
                log.error("【搜索模块-写入失败，第 {} 次重试】 index = {}, id = {}", i, index, id, e);
            }
        }
        if (!success) {
            // 重试仍失败
            //todo 死信队列     可在此投递死信队列补偿，避免日志丢失
            log.error("【搜索模块-写入最终失败】 index = {}, id = {}, submit = {}", index, id, submit);
        }

        //无论成功失败都手动ack，释放unacked状态，防止项目重启后消息回滚重投
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            // ack失败说明通道异常，消息会由Broker重新投递，这里只记录
            log.error("【搜索模块-ack失败】 id = {}", id, e);
        }
    }

    public String getYear(){
        return LocalDateTime.now().getYear() + "";
    }
}
