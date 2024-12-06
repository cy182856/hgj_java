package com.ej.hgj.entity.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class CardCstBill {

    private String id;

    private String cardCstBatchId;

    private String proNum;

    private Integer cardType;

    private Integer cardId;

    private String cardCode;

    private String cstCode;

    private Integer billNum;

    private Integer billType;

    private String wxOpenId;

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

    private String cstName;

    private String cardTypeName;

    private String userName;

    private String intoName;

    private String expDate;

    private String startExpDate;

    private String endExpDate;
}
