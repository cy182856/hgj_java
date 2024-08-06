package com.ej.hgj.entity.cstInto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class CstInto {
    private String id;

    private String projectNum;

    private String projectName;

    private String unionId;

    private String wxOpenId;

    private String cstCode;

    private String houseId;

    private String resName;

    private Integer intoRole;

    private Integer intoStatus;

    private String userName;

    private String phone;

    private String cstName;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String cstIntoHouseId;

    private Integer houseIntoStatus;

    private String startTime;

    private String endTime;

    private String intoStatusName;

    private String intoRoleName;

    private List<String> houseList;

}
