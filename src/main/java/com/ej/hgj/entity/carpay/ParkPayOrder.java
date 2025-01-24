package com.ej.hgj.entity.carpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ParkPayOrder {

    private String id;

    private String proNum;

    private String parkOrderNo;

    private String cardCstBatchId;

    private String carCode;

    private String wxOpenId;

    private String cstCode;

    private String cstName;

    private BigDecimal payAmount;

    private BigDecimal actAmount;

    private Integer amountTotal;

    private String ipItemName;

    private String transactionId;

    private String tradeType;

    private String tradeState;

    private String tradeStateDesc;

    private String bankType;

    private String successTime;

    private Integer isDeduction;

    private Integer deductionNum;

    private String inTime;

    private String outTime;

    private Integer orderStatus;

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

    private String createMsg;

    private String payCallBackMsg;
}
