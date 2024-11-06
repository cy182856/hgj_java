package com.ej.hgj.entity.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class CouponGrant {

    private String id;

    private String batchId;

    private String couponId;

    private String tagId;

    private String typeCode;

    private String typeName;

    private Integer couNum;

    // 已用小时
    private Integer applyNum;

    private String startTime;

    private String endTime;

    private String cstCode;

    private String wxOpenId;

    private String userName;

    private Integer range;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tagName;

    private String cstName;

    private String title;

    // 充值小时
    private Integer rechargeNum;

}
