package com.ej.hgj.vo;

import lombok.Data;

@Data
public class QrCodeResVo {

    // 小区号
    private String neighNo;

    // 二维码内容
    private String qrCode;

    // 卡号
    private String cardNo;

    // 类型（1.智慧管家生成的二维码 2.智慧管家生成的快速码 3.客服直接生成的二维码 4.客服通过快速码生成的二维码）
    private Integer type;

    // 生效日期 年月日
    private String expDate;

    // 开始时间 毫秒
    private Long startTime;

    // 结束时间 毫秒
    private Long endTime;

    // 生成人手机号
    private Long phone;

    // 快速码
    private Integer quickCode;

    // 人脸图片base64字符
    private String facePic;

    // 客户编号
    private String cstCode;

    // 客户名称
    private String cstName;

    // 单元号
    private Integer unitNum;

    // 楼层
    private Integer floor;

    // 室号
    private String room;

    // 房屋ID
    private String houseId;

    // 客服姓名
    private String serviceName;


}
