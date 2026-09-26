package com.dong.common.constant;

public interface ApiConstant {
    /** 签名左括号（必须用中文符号，这是短信行业规范） */
    String SIGN_PREFIX = "【";

    /** 签名右括号 */
    String SIGN_SUFFIX = "】";

    /**
     * 单条短信费用。
     * ⚠️ 单位必须和 client_balance.balance 保持一致（这里按"分"举例，一条 5 分）
     */
    long SINGLE_FEE = 5L;
}
