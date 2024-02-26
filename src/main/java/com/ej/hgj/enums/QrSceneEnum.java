package com.ej.hgj.enums;

public enum QrSceneEnum {
    QR_SCENE("QR_SCENE", "临时的整型参数值"),
    QR_STR_SCENE("QR_STR_SCENE", "临时的字符串参数值"),
    QR_LIMIT_SCENE("QR_LIMIT_SCENE", "永久的整型参数值"),
    QR_LIMIT_STR_SCENE("QR_LIMIT_STR_SCENE", "永久的字符串参数值");

    private String code;
    private String desc;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private QrSceneEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
