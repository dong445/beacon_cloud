package com.dong.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardSubmit implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前短信的全局唯一标识（雪花算法生成） */
    private Long sequenceId;

    /** 客户ID（apikey 校验后回填） */
    private Long clientId;

    /** 客户请求携带的 apikey */
    private String apikey;

    /** 0-验证码 1-通知类 2-营销类 */
    private int state;

    /** 【本次请求】的真实客户端 IP（由请求头解析得到） */
    private String realIP;

    /** 【客户配置】的 IP 白名单，逗号分隔（IP校验时回填） */
    private String ip;

    /** 客户业务内的 uid */
    private String uid;

    /** 目标手机号 */
    private String mobile;

    /** 短信内容里解析出的签名（签名校验后回填） */
    private String sign;

    /** 签名ID（签名校验后回填，模板校验要用） */
    private Long signId;

    /** 短信完整内容，形如：【签名】正文 */
    private String text;

    /**
     * 短信发送时间。
     * 必须用 @JsonFormat 输出 "yyyy-MM-dd HH:mm:ss" 字符串，不能用 LocalDateTimeSerializer 默认序列化：
     * 默认会输出数组 [年,月,日,时,分,秒,纳秒]，而 ES 里 sendTime 是 date 类型，
     * ES 会把数组的每个元素当成 epoch_millis 解析，导致时间全变成 1970 年的脏数据。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /** 本条短信费用（余额校验时算出） */
    private Long fee;


    /**
     * 目标手机号的运营商（策略模块）
     */
    private Integer operatorId;


    /**
     * 目标手机号的归属地区号  0451  0455 （策略模块，三方查询不到，先不管）
     */
    private Integer areaCode;

    /**
     * 目标手机号的归属地  哈尔滨，  绥化~   （策略模块）
     */
    private String area;

    /**
     * 通道下发的源号码  106934985673485645  （策略模块）
     */
    private String srcNumber;

    /**
     * 通道的id信息   （策略模块）
     */
    private Long channelId;

    /**
     * 短信的发送状态， 0-等待/发送ing，1-成功，2-失败 ，默认情况就是0
     */
    private int reportState;

    /**
     * 短信发送失败的原因是什么，记录在当前属性
     */
    private String errorMsg;

    /**
     * 是否携号转网，  isTransfer = true，代表做了携号转网的判断并且做了操作
     */
    private Boolean isTransfer = false;


    private Long oneHourLimitMilli;
    // 后续再做封装~~~~

}
