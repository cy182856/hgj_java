package com.ej.hgj.enums.wechat;

public enum PubMsgTypeEnum {
    EVENT("event", "事件类型消息"),
    TEXT("text", "文本类型消息"),
    MUSIC("music", "音乐类型消息"),
    NEWS("news", "图文类型消息"),
    IMAGE("image", "图片类型消息"),
    LINK("link", "链接类型消息"),
    LOCATION("location", "地理位置类型消息"),
    VOICE("voice", "音频类型消息"),
    VIDEO("video", "视屏类型消息"),
    SHORTVIDEO("shortvideo", "小视频类型消息");

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

    PubMsgTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
