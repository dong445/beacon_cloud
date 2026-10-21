package com.dong.smsgateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.dong.common.constant.RabbitMQConstants.*;

@Configuration
public class RabbitMQConfig {

    private final int TTL = 10000;
    private final String FANOUT_ROUTING_KEY = "";

    //  声明死信队列，需要准备普通交换机，普通队列，死信交换机，死信队列
    @Bean
    public Exchange normalExchange(){
        return ExchangeBuilder.fanoutExchange(SMS_GATEWAY_NORMAL_EXCHANGE).build();
    }

    @Bean
    public Queue normalQueue(){
        Queue queue = QueueBuilder.durable(SMS_GATEWAY_NORMAL_QUEUE)
                .withArgument("x-message-ttl",TTL)
                .withArgument("x-dead-letter-exchange",SMS_GATEWAY_DEAD_EXCHANGE)
                .withArgument("x-dead-letter-routing-key",FANOUT_ROUTING_KEY)
                .build();
        return queue;
    }

    @Bean
    public Binding normalBinding(Exchange normalExchange,Queue normalQueue){
        return BindingBuilder.bind(normalQueue).to(normalExchange).with("").noargs();
    }

    @Bean
    public Exchange deadExchange(){
        return ExchangeBuilder.fanoutExchange(SMS_GATEWAY_DEAD_EXCHANGE).build();
    }
    @Bean
    public Queue deadQueue(){
        return QueueBuilder.durable(SMS_GATEWAY_DEAD_QUEUE).build();
    }
    @Bean
    public Binding deadBinding(Exchange deadExchange,Queue deadQueue){
        return BindingBuilder.bind(deadQueue).to(deadExchange).with("").noargs();
    }


    //与策略模块保持一致：发/收消息统一用 JSON，避免默认 Java 序列化导致 serialVersionUID 冲突
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    //配置类的方式修改RabbitMQ消费的方式
    //@Bean
    public SimpleRabbitListenerContainerFactory gatewayContainerFactory(ConnectionFactory connectionFactory,
                                                                        SimpleRabbitListenerContainerFactoryConfigurer configurer){
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConcurrentConsumers(5);
        simpleRabbitListenerContainerFactory.setPrefetchCount(10);
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        configurer.configure(simpleRabbitListenerContainerFactory,connectionFactory);
        return simpleRabbitListenerContainerFactory;
    }

}