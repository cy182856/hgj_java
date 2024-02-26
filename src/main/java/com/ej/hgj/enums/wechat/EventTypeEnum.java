package com.ej.hgj.enums.wechat;

public enum EventTypeEnum {
    UNSUBSCRIBE("unsubscribe", "取消关注事件"),
    SUBSCRIBE("subscribe", "关注事件"),
    SCAN("SCAN", "已经关注,扫二维码事件"),
    LOCATION("LOCATION", "上报地理位置事件"),
    TEMPLATESENDJOBFINISH("TEMPLATESENDJOBFINISH", "模板消息发送后结果通知事件"),
    USER_AUTHORIZE_INVOICE("user_authorize_invoice", "用户授权开票"),
    CLICK("CLICK", "菜单点击事件"),
    VIEW("VIEW", "菜单点击跳转链接事件");

    private String type;
    private String typeDesc;

    private EventTypeEnum() {
    }

    private EventTypeEnum(String type, String typeDesc) {
        this.type = type;
        this.typeDesc = typeDesc;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeDesc() {
        return this.typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public EventTypeEnum getEventTypeEnum(String type) {
        EventTypeEnum[] var2 = values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            EventTypeEnum value = var2[var4];
            if (type.equals(value.getType())) {
                return value;
            }
        }

        return null;
    }
}
