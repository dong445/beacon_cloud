package com.dong.smsgateway.runnable;

import com.dong.common.constant.RabbitMQConstants;
import com.dong.common.constant.SmsConstant;
import com.dong.common.model.StandardReport;
import com.dong.common.model.StandardSubmit;
import com.dong.common.util.CMPP2ResultUtil;
import com.dong.common.util.CMPPDeliverMapUtil;
import com.dong.common.util.CMPPSubmitRepoMapUtil;
import com.dong.smsgateway.netty4.entity.CmppSubmitResp;
import com.dong.smsgateway.util.SpringUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;

public class SubmitRepoRunnable implements Runnable{

    private RabbitTemplate rabbitTemplate = SpringUtil.getBeanByClass(RabbitTemplate.class);
    private CmppSubmitResp submitResp;
    private final int OK = 0;

    public SubmitRepoRunnable(CmppSubmitResp submitResp) {
        this.submitResp = submitResp;
    }

    @Override
    public void run() {
        StandardReport report=null;
        //拿到自增ID，并且从ConcurrentHashMap中获取到存储的submit
        StandardSubmit submit= CMPPSubmitRepoMapUtil.remove(submitResp.getSequenceId());

        //根据运营商返回的result，确认短信状态并且封装submit
        int result = submitResp.getResult();
        if(result != OK){
            //说明运营商的提交应答中回馈的失败的情况
            String resultMessage= CMPP2ResultUtil.getResultMessage(result);
            submit.setReportState(SmsConstant.REPORT_FAIL);
            submit.setErrorMsg(resultMessage);
        }else{
            // 如果没进到if中，说明运营商已经正常的接收了发送短信的任务，这边完成3操作
            //将submit封装为Report，临时存储，以便运营商返回状态码时，可以再次获取到信息
            // 这里没有对其他信息做封装
            report = new StandardReport();
            BeanUtils.copyProperties(submit, report);
            CMPPDeliverMapUtil.put(submitResp.getMsgId() + "",report);
        }

        //将封装好的submit直接扔RabbitMQ中，让搜索模块记录信息
        rabbitTemplate.convertAndSend(RabbitMQConstants.SMS_WRITE_LOG,submit);
    }
}
