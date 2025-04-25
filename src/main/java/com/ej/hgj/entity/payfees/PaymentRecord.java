package com.ej.hgj.entity.payfees;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentRecord {

    private String id;

    // 思源订单号
    private String orderNo;

    // 预支付交易会话标识,该值有效期为2小时
    private String prepayId;

    // 微信支付订单号
    private String transactionId;

    // 交易类型
    private String tradeType;

    // 交易状态
    private String tradeState;

    // 交易状态描述
    private String tradeStateDesc;

    // 付款银行
    private String bankType;

    // 支付完成时间
    private String successTime;

    // 项目号
    private String proNum;

    // 微信openId
    private String wxOpenId;

    // 客户编号
    private String cstCode;

    // 客户名称
    private String cstName;

    // 金额
    private Integer amountTotal;

    // 本金应收
    private BigDecimal priRev;

    // 本金已收
    private BigDecimal priPaid;

    // 收付项目名称（此笔费用的类型，如“电费”，“水费”，“管理费”等）
    private String ipItemName;

    // 缴费状态 0-缴费中 1-已缴费
    private Integer paymentStatus;

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

}
