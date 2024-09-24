package com.ej.hgj.entity.active;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class CouponQrCode {

    private String id;

    private String proNum;

    private String proName;

    private String expDate;

    private Long startTime;

    private Long endTime;

    private String cardNo;

    private String couponId;

    private String qrCodeContent;

    private String neighNo;

    private String addressNum;

    private String unitNum;

    private String floors;

    private String wxOpenId;

    private String cstCode;

    private String cstName;

    private String resCode;

    private Integer isExpire;

    private String errorMsg;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
