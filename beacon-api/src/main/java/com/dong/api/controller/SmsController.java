package com.dong.api.controller;


import com.dong.api.enums.SmsCodeEnum;
import com.dong.api.filter.CheckFilterContext;
import com.dong.api.form.SingleSendForm;
import com.dong.api.util.R;
import com.dong.api.vo.ResultVO;
import com.dong.common.model.StandardSubmit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sms")
@Slf4j
@RefreshScope
public class SmsController {

    /**
     * 客户端IP地址的请求头信息，多个用','隔开。
     */
    @Value("${headers}")
    private String headers;

    /**
     * 基于请求头获取信息时，可能获取到的未知信息
     */
    private final String UNKNOW = "unknow";

    /**
     * 如果是当前请求头获取IP地址，需要截取到第一个','未知
     */
    private final String X_FORWARDED_FOR = "x-forwarded-for";

    @Autowired
    private CheckFilterContext checkFilterContext;


    //produces = "application/json;charset=utf-8"等于告诉 Spring MVC：这个接口的响应体是 JSON，且用 UTF-8 编码
    //:80 那里有个 nginx/反代指到了 beacon-api
    @PostMapping(value = "/single_send",produces = "application/json;charset=utf-8")
    public ResultVO singleSend(@RequestBody @Validated SingleSendForm singleSendForm, BindingResult bindingResult, HttpServletRequest request){
        //校验参数
        if (bindingResult.hasErrors()){
            String msg = bindingResult.getFieldError().getDefaultMessage();
            log.info("【接口模块-单条短信Controller】 参数不合法 msg = {}",msg);
            return R.error(SmsCodeEnum.PARAMETER_ERROR.getCode(),msg);
        }

        //获取真实的IP地址
        String ip = this.getRealIP(request);

        //构建StandardSubmit，各种封装校验
        StandardSubmit submit = new StandardSubmit();
        submit.setRealIP(ip);
        submit.setApikey(singleSendForm.getApikey());
        submit.setMobile(singleSendForm.getMobile());
        submit.setText(singleSendForm.getText());
        submit.setState(singleSendForm.getState());
        submit.setUid(singleSendForm.getUid());

        //调用策略模式的校验链
        checkFilterContext.check(submit);

        //发送到MQ，交给策略模块处理
        return R.ok();
    }

    /**
     * 获取客户端真实的IP地址
     * @param req
     * @return
     */
    private String getRealIP(HttpServletRequest req){

        //0. 声明真实IP地址
        String  ip;

        //1. 基于x-forwarded-for请求头获取IP地址
        String ips = req.getHeader("x-forwarded-for");
        //  直接基于第一个,的位置，截取需要的IP地址
        if (StringUtils.isEmpty(ips) || "unknow".equalsIgnoreCase(ips)) {
            if(ips.contains(",")){
                return ips.substring(0, ips.indexOf(","));
            }else{
                return ips;
            }
        }

        // 2. 基于请求头获取IP地址，基于request请求头获取信息时，除了null和空串外，还有可能拿到unknow，
        ip = req.getHeader("x-real-ip");
        if (StringUtils.isEmpty(ip) || "unknow".equalsIgnoreCase(ip)){
            // x-real-ip没拿到，考虑一下其他的代理服务器
            //3.  Apache的服务器，请求头中携带真实IP的名称是 proxy-client-ip
            ip = req.getHeader("proxy-client-ip");
        }

        //4. 如果real没有拿到，判断apache是否拿到了。
        if (StringUtils.isEmpty(ip) || "unknow".equalsIgnoreCase(ip)){
            // 5. 如果Apache服务器没拿到，考虑一手WebLogic, wl-proxy-client-ip
            ip = req.getHeader("wl-proxy-client-ip");
        }

        //6. 判断WebLogic有木有拿到IP
        if (StringUtils.isEmpty(ip) || "unknow".equalsIgnoreCase(ip)){
            //7. 基于其他的代理服务器的方式获取请求头的IP地址
            ip = req.getHeader("http_client_ip");
        }
        //8. 如果上诉方式都获取不到，
        if (StringUtils.isEmpty(ip) || "unknow".equalsIgnoreCase(ip)){
            // 9. 基于传统方式获取IP
            ip = req.getRemoteAddr();
        }
        //10. 返回
        return ip;
    }


    private String getRealIP02(HttpServletRequest req) {
        //1. 声明返回的ip地址
        String ip;

        //2. 遍历请求头，并且通过req获取ip地址
        for (String header : headers.split(",")) {
            // 健壮性校验
            if (!StringUtils.isEmpty(header)) {
                // 基于req获取ip地址
                ip = req.getHeader(header);
                // 如果获取到的ip不为null，不为空串，并且不为unknow，就可以返回
                if (!StringUtils.isEmpty(ip) && !UNKNOW.equalsIgnoreCase(ip)) {
                    // 判断请求头是否是x-forwarded-for
                    if (X_FORWARDED_FOR.equalsIgnoreCase(header) && ip.indexOf(",") > 0) {
                        ip = ip.substring(0,ip.indexOf(","));
                    }
                    // 返回IP地址
                    return ip;
                }
            }
        }

        //3. 如果请求头都没有获取到IP地址，直接基于传统的方式获取一个IP
        return req.getRemoteAddr();
    }
}
