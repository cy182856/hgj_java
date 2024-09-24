package com.ej.hgj.entity.opendoor;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class OpenDoorQuickCode {

    private String id;

    private String proNum;

    private String proName;

    private String expDate;

    private Long startTime;

    private Long endTime;

    private String cardNo;

    private String qrCodeContent;

    private String neighNo;

    private String addressNum;

    private String unitNum;

    private String floors;

    private String quickCode;

    private String wxOpenId;

    private String cstCode;

    private String cstName;

    private String phone;

    private String resCode;

    private String facePicPath;

    private String houseId;

    // 是否失效 0-无效 1-有效
    private Integer isExpire;

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
}
