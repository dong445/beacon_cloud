package com.dong.strategy.config;

import com.dong.common.constant.RabbitMQConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

   //告诉 RabbitMQ，发消息时把 Java 对象转成 JSON 字符串，收消息时把 JSON 字符串转回 Java 对象
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }


    /**
     * 接口模块发送消息到策略模块的队列
     * @return
     */
    @Bean
    public Queue preSendQueue(){
        return QueueBuilder.durable(RabbitMQConstants.MOBILE_AREA_OPERATOR).build();
    }

    /**
     * 写日志的队列。
     * @return
     */
    @Bean
    public Queue writeLogQueue(){
        return QueueBuilder.durable(RabbitMQConstants.SMS_WRITE_LOG).build();
    }

    /**
     * 状态报告的队列。
     * @return
     */
    @Bean
    public Queue pushReportQueue(){
        return QueueBuilder.durable(RabbitMQConstants.SMS_PUSH_REPORT).build();
    }
}