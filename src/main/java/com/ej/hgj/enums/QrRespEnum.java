package com.ej.hgj.enums;

public enum QrRespEnum {
    QR_IMG("IMG", "返回二维码图片地址"),
    QR_URL("URL", "返回二维码实际链接");

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

    private QrRespEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
