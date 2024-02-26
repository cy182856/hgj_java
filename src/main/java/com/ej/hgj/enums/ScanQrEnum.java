package com.ej.hgj.enums;

public enum ScanQrEnum {
    S01("S01", "扫码绑定二维码"),
    S02("S02", "扫标的二维码"),
    S03("S03", "扫仅带客户号的永久二维码");

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

    private ScanQrEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
