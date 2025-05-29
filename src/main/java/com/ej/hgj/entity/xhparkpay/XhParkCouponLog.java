package com.ej.hgj.entity.xhparkpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class XhParkCouponLog {

    private String id;

    private String proNum;

    private String couponNo;

    private String couponKey;

    private String cardCstBatchId;

    private String carCode;

    private String wxOpenId;

    private String cstCode;

    private Integer deductionNum;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String startTime;

    private String endTime;

    private String proName;

    private String cardCode;

    private String cstName;

    private String userName;
}
