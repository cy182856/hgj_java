package com.ej.hgj.entity.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class CardCstBatch {

    private String id;

    private String proNum;

    private Integer cardType;

    private Integer cardId;

    private String cardCode;

    private String cstCode;

    private Integer totalNum;

    private Integer applyNum;

    private String expDate;

    private String startTime;

    private String endTime;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 充值次数/小时
    private Integer rechargeNum;

    private List<String> cstCodeList;

}
