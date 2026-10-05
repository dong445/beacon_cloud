package com.dong.test.mapper.entity;

public class MobileTransfer {

    // 手机号
    private String transferNumber;

    // 最终运营商
    private Integer nowIsp;

    public String getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public Integer getNowIsp() {
        return nowIsp;
    }

    public void setNowIsp(Integer nowIsp) {
        this.nowIsp = nowIsp;
    }
}