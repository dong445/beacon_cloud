package com.dong.smsgateway.netty4;


import com.dong.smsgateway.netty4.entity.CmppDeliver;
import com.dong.smsgateway.netty4.entity.CmppSubmitResp;
import com.dong.smsgateway.netty4.utils.MsgUtils;
import com.dong.smsgateway.runnable.DeliverRunnable;
import com.dong.smsgateway.runnable.SubmitRepoRunnable;
import com.dong.smsgateway.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * 主要业务 handler,运营商响应信息
 *
 * CMPPHandler 和 SubmitRepoRunnable 都是手动 new 出来的非 Spring 对象，@Autowired 对它们无效，
 * 所以它们只能通过 SpringUtil.getBeanByName/getBeanByClass 主动去 Spring 容器里取线程池和 RabbitTemplate
 */
public class CMPPHandler extends SimpleChannelInboundHandler {
    private final static Logger log = LoggerFactory.getLogger(CMPPHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object msg) throws Exception {

        if (msg instanceof CmppSubmitResp){
            CmppSubmitResp resp=(CmppSubmitResp)msg;
            log.info("-------------接收到短信提交应答-------------");
            log.info("----自增id："+resp.getSequenceId());
            log.info("----状态："+ resp.getResult());
            log.info("----第一次响应："+resp.getMsgId());
            ThreadPoolExecutor cmppSubmitPool = (ThreadPoolExecutor) SpringUtil.getBeanByName("cmppSubmitPool");
            cmppSubmitPool.execute(new SubmitRepoRunnable(resp));
        }

        if (msg instanceof CmppDeliver){
            CmppDeliver resp=(CmppDeliver)msg;
            // 是否为状态报告 0：非状态报告1：状态报告
            if (resp.getRegistered_Delivery() == 1) {
                // 如果是状态报告的话
                log.info("-------------状态报告---------------");
                log.info("----第二次响应："+resp.getMsg_Id_DELIVRD());
                log.info("----手机号："+resp.getDest_terminal_Id());
                log.info("----状态："+resp.getStat());
                ThreadPoolExecutor cmppDeliverPool = (ThreadPoolExecutor) SpringUtil.getBeanByName("cmppDeliverPool");
                cmppDeliverPool.execute(new DeliverRunnable(resp.getMsg_Id_DELIVRD(),resp.getStat()));
            } else {
                //用户回复会打印在这里
                log.info(""+ MsgUtils.bytesToLong(resp.getMsg_Id()));
                log.info(resp.getSrc_terminal_Id());
                log.info(resp.getMsg_Content());
            }
        }
    }

}
