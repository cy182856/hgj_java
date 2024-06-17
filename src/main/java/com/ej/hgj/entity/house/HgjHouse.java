package com.ej.hgj.entity.house;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class HgjHouse {

    private String id;

    private String orgId;

    private String projectNum;

    private String budId;

    private String budCode;

    private String budName;

    private String grpId;

    private String grpCode;

    private String grpName;

    private String resCode;

    private String resName;

    private String resNo;

    private String unitNo;

    private BigDecimal budArea;

    private String houseFloor;

    private Integer floorNum;

    private BigDecimal rentalArea;

    private BigDecimal feeArea;

    private BigDecimal useArea;

    private String resType;

    private String porpUser;

    private String rentStatus;

    private String resStatus;

    private String decoStatus;

    private String finStatus;

    private String serStatus;

    private Integer isAffect;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String cstCode;

    private String cstName;

}
