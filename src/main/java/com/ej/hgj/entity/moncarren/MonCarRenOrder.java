package com.ej.hgj.entity.moncarren;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MonCarRenOrder {

    private String id;

    private String proNum;

    private String carCode;

    private String carTypeNo;

    private String wxOpenId;

    private String cstCode;

    private String cstName;

    private BigDecimal payAmount;

    private Integer amountTotal;

    private String ipItemName;

    private String transactionId;

    private String tradeType;

    private String tradeState;

    private String tradeStateDesc;

    private String bankType;

    private String successTime;

    private String beginTime;

    private String endTime;

    private Integer monthNum;

    private String renewBeginTime;

    private String renewEndTime;

    private String userName;

    private String userTel;

    private String userAddress;

    private String ruleId;

    private String ruleName;

    private String ruleType;

    private String ruleCount;

    private String ruleAmount;

    private Integer orderStatus;

    private String callBackCode;

    private String callBackMsg;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String startDate;

    private String endDate;

    private String proName;

    private Integer invoiceStatus;

    private String resCode;

    private String resMsg;
}
