package com.ej.hgj.vo;

import lombok.Data;

@Data
public class QrCodeLogReqVo {

    // 小区号
    private String neighNo;

    // 卡号
    private String cardNo;

    // 设备号
    private String deviceNo;

    // 2（已开进）4（已开出）
    private Integer isUnlock;

    // 刷卡时间 毫秒
    private Long eventTime;

}
