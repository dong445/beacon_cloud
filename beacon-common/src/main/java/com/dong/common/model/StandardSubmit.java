package com.dong.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardSubmit {
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

    /** 短信发送时间 */
    private LocalDateTime sendTime;

    /** 本条短信费用（余额校验时算出） */
    private Long fee;

    // 后续再做封装~~~~

}
