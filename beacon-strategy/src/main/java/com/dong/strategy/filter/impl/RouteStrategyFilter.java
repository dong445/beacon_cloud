package com.dong.strategy.filter.impl;

import com.dong.common.constant.CacheConstant;
import com.dong.common.constant.RabbitMQConstants;
import com.dong.common.enums.ExceptionEnums;
import com.dong.common.exception.StrategyException;
import com.dong.common.model.StandardSubmit;
import com.dong.strategy.client.BeaconCacheClient;
import com.dong.strategy.filter.StrategyFilter;
import com.dong.strategy.util.ChannelTransferUtil;
import com.dong.strategy.util.ErrorSendMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(value = "route")
@Slf4j
public class RouteStrategyFilter implements StrategyFilter {
    @Autowired
    private BeaconCacheClient cacheClient;

    @Autowired
    private ErrorSendMsgUtil sendMsgUtil;

    /*
    * AmqpAdmin 是 Spring AMQP 封装的 RabbitMQ 基础设施管理工具，
    * 核心作用是在运行时动态创建、校验队列和交换机等资源，保障消息收发的目标基础设施存在
    * */
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-路由策略】 校验ing..........");
        //拿到客户Id
        Long clientId=submit.getClientId();

        //基于redis获取当前客户绑定的所有通道信息
        Set<Map> clientChannels=cacheClient.smember(CacheConstant.CLIENT_CHANNEL+clientId);

        //将获取到的客户通道信息根据权重做好排序
        List<Map> clientWeightChannels = new ArrayList<>(clientChannels);
        clientWeightChannels.sort((o1, o2) -> {
            int o1Weight = Integer.parseInt(o1.get("clientChannelWeight") + "");
            int o2Weight = Integer.parseInt(o2.get("clientChannelWeight") + "");
            return o2Weight - o1Weight;
        });

        boolean ok=false;
        Map channel=null;
        Map clientChannel=null;
        //基于排序好的通道选择，权重更高的
        for (Map clientWeightChannel : clientWeightChannels){
            //如果客户和通道的绑定关系可用，直接去基于Redis查询具体的通道信息
            if ((int) (clientWeightChannel.get("isAvailable")) !=0){
                // 当前关系不可用，直接进行下次循环，选择权重相对更低一点的
                continue;
            }

            //如果通道信息查询后，判断通道睡否可用，其次运营商可以匹配。
            channel = cacheClient.hGetAll(CacheConstant.CHANNEL + clientWeightChannel.get("channelId"));
            if((int)(channel.get("isAvailable")) != 0){
                // 当前通道不可用，选择权重更低的通道~
                continue;
            }

            // 获取通道的通讯方式
            Integer channelType = (Integer) channel.get("channelType");
            if (channelType != 0 && submit.getOperatorId() != channelType){
                // 通道不是全网通，并且和当前手机号运营商不匹配
                continue;
            }

            //如果后期涉及到的通道的转换，这里留一个口子
            channel = ChannelTransferUtil.transfer(submit, channel);

            // 找到可以使用的通道了
            ok = true;
            clientChannel = clientWeightChannel;
            break;
        }

        if(!ok){
            log.info("【策略模块-路由策略】   没有选择到可用的通道！！");
            submit.setErrorMsg(ExceptionEnums.NO_CHANNEL.getMsg());
            sendMsgUtil.sendWriteLog(submit);
            sendMsgUtil.sendPushReport(submit);
            throw new StrategyException(ExceptionEnums.NO_CHANNEL);
        }

        //基于选择的通道封装submit的信息
        submit.setChannelId(Long.parseLong(channel.get("id") + ""));
        submit.setSrcNumber("" + channel.get("channelNumber") + clientChannel.get("clientChannelNumber"));

        try {
            //声明好队列名称，并构建队列
            String queueName = RabbitMQConstants.SMS_GATEWAY + submit.getChannelId();
            amqpAdmin.declareQueue(QueueBuilder.durable(queueName).build());

            //发送消息到声明好的队列中
            rabbitTemplate.convertAndSend(queueName,submit);
        } catch (AmqpException e) {
            log.info("【策略模块-路由策略】   声明通道对应队列以及发送消息时出现了问题！");
            submit.setErrorMsg(e.getMessage());
            sendMsgUtil.sendWriteLog(submit);
            sendMsgUtil.sendPushReport(submit);
            throw new StrategyException(e.getMessage(),ExceptionEnums.UNKNOWN_ERROR.getCode());
        }
    }
}
