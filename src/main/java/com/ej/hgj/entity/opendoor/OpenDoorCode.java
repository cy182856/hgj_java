package com.ej.hgj.entity.opendoor;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class OpenDoorCode {

    private String id;

    private String proNum;

    private String proName;

    private Integer type;

    private String expDate;

    private Long startTime;

    private Long endTime;

    private String cardNo;

    private String qrCodeContent;

    private String neighNo;

    private String addressNum;

    private String unitNum;

    private String floors;

    private String passUrl;

    private String randNum;

    private String wxOpenId;

    private String cstCode;

    private String cstName;

    private String phone;

    private String resCode;

    private String facePicPath;

    private String cstMobile;

    private String houseId;



    // 访客姓名
    private String visitName;

    // 车牌号
    private String carNum;

    // 可用次数
    private Integer expNum;

    // 剩余次数
    private Integer resNum;

    // 有效时间-小时
    private Integer effectiveTime;

    // 是否失效 0-有效 1-失效
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

}
