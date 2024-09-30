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

    private String cardCode;

    private String cstCode;

    private Integer totalNum;

    private Integer applyNum;

    private String startTime;

    private String endTime;

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

    // 充值次数
    private Integer rechargeNum;


}
