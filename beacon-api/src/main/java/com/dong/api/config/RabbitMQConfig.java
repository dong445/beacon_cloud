package com.dong.api.config;

import com.dong.common.constant.RabbitMQConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig{

    //对象序列化工具
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        // StandardSubmit 里有 LocalDateTime，必须注册 JavaTimeModule，否则序列化直接失败
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
        return QueueBuilder.durable(RabbitMQConstants.SMS_PRE_SEND).build();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,MessageConverter jsonMessageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);

        // confirm 机制：确认消息是否到达交换机
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("【接口模块-发送消息】 消息未到达交换机 correlationData = {} , cause = {}",
                        correlationData, cause);
            }
        });

        //return 机制：确认消息是否到了队列
        rabbitTemplate.setReturnsCallback(returned ->{
            log.error("消息路由失败: message={},exchange={}, routingKey={}",
                    returned.getMessage().toString(),returned.getExchange().toString(), returned.getRoutingKey().toString());
        });

        return rabbitTemplate;
    }
}
