package com.ej.hgj.entity.visit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class VisitLog {

    private String id;

    private String passId;

    private String proNum;

    private String proName;

    private String passUrl;

    private String randNum;

    private String wxOpenId;

    private String cstCode;

    private String cstName;

    private String cstMobile;

    private String houseId;

    // 访客姓名
    private String visitName;

    // 车牌号
    private String carNum;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String houseName;

    // 生效日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date effectuateDate;

    private String startTime;

    private String endTime;
}
