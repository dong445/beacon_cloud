package com.dong.smsgateway.mq;

import com.dong.common.model.StandardSubmit;
import com.dong.common.util.CMPPSubmitRepoMapUtil;
import com.dong.smsgateway.netty4.NettyClient;
import com.dong.smsgateway.netty4.entity.CmppSubmit;
import com.dong.smsgateway.netty4.utils.Command;
import com.dong.smsgateway.netty4.utils.MsgUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SmsGatewayListener {

    @Autowired
    private NettyClient nettyClient;

    //可以在指定的@RabbitListener注解中指定使用ContainerFactory
    @RabbitListener(queues = "${gateway.sendtopic}"/*,containerFactory = "gatewayContainerFactory"*/)
    public void consume(StandardSubmit submit, Channel channel, Message message) throws IOException, InterruptedException, IOException {
        log.info("【短信网关模块】 接收到消息 submit = {}",submit);
        //完成运营商交互，发送一次请求，接收两次响应
        //获取需要的核心属性
        String srcNumber=submit.getSrcNumber();
        String mobile=submit.getMobile();
        String text=submit.getText();
        //这个序列是基于++实现的，当取值达到MAX时，会被重置，这个值是可以重复利用的。
        int sequence= MsgUtils.getSequence();
        //声明发送短信，需要CMPPSubmit对象
        CmppSubmit cmppSubmit=new CmppSubmit(Command.CMPP2_VERSION,srcNumber,sequence,mobile,text);
        //将submit对象做一个临时存储，在运营商第一次响应时，可以获取到。
        // todo 如果怕出问题，服务器宕机，数据丢失，可以上Redis
        CMPPSubmitRepoMapUtil.put(sequence,submit);

        //和运营商交互发送短信
        nettyClient.submit(cmppSubmit);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
