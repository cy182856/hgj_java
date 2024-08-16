package com.ej.hgj.entity.repair;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RepairLog {

    private String id;

    private String projectNum;

    private String projectName;

    private String repairNum;

    private String wxOpenId;

    private String cstCode;

    private String cstName;

    private String cstMobile;

    private String houseId;

    private String serviceType;

    private String quesType;

    private String workPos;

    private String repairType;

    private String image;

    private String repairDesc;

    private String repairStatus;

    private String repairScore;

    private String satisFaction;

    private String repairMsg;

    private String orders;

    private BigDecimal labourCost;

    private BigDecimal materialCost;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ordersTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completionTime;

    private String startTime;

    private String endTime;

    private String repairStatusName;

    private String userName;
}
