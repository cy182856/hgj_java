package com.ej.hgj.entity.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class CardCst {

    private String id;

    private String proNum;

    private Integer cardId;

    private Integer cardType;

    private String cardCode;

    private String cstCode;

    private Integer isExp;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String tagId;

    private String proName;

    private String cstName;

    private String cardName;

    private String cardTypeName;

    private List<String> cstCodeList;

    // 剩余
    private Integer expNum;

    private String startTime;

    private String endTime;

    private String startYear;

    private String startYearMonth;

    private String endYear;

    private String endYearMonth;

    private Integer rechargeNum;

    private String expDate;

    private String startExpDate;

    private String endExpDate;

}
