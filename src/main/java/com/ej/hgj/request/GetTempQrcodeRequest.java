package com.ej.hgj.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetTempQrcodeRequest implements Serializable {

    private static final long serialVersionUID = 2207756760470554236L;

    private String cstCode;

    private String proNum;

    // 房屋ID
    private String houseId;

    // 返回二维码类型  默认值 IMG
    private String qrRespType;

    // 二维码时效 单位 s 默认值 600
    private String second;

    // 入住类型
    private String intoType;

    // 入住id
    private String cstIntoId;

}
