package com.dong.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    //与策略模块保持一致：发/收消息统一用 JSON，避免默认 Java 序列化导致 serialVersionUID 冲突
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    public static final String DELAYED_EXCHANGE="push_delayed_exchange";

    public static final String DELAYED_QUEUE="push_delayed_queue";

    public static final String DELAYED_EXCHANGE_TYPE="x-delayed-message";

    private static final String DELAYED_ROUTING_TYPE_KEY = "x-delayed-type";

    //决定交换机路由策略的key
    private static final String DELAYED_ROUTING_TYPE_FANOUT = "fanout";

    @Bean
    public Exchange delayedExchange(){
        Map<String, Object> args = new HashMap<>();
        args.put(DELAYED_ROUTING_TYPE_KEY,DELAYED_ROUTING_TYPE_FANOUT);
        Exchange delayedExchange = new CustomExchange(DELAYED_EXCHANGE,DELAYED_EXCHANGE_TYPE,false,false,args);
        return delayedExchange;
    }

    @Bean
    public Queue delayedQueue(){
        return QueueBuilder.durable(DELAYED_QUEUE).build();
    }

    @Bean
    public Binding delayedBinding(Exchange delayedExchange, Queue delayedQueue){
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with("").noargs();
    }

}
