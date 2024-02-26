package com.ej.hgj.entity.wechat;

public class Miniprogram {
    private String appid;
    private String pagepath;

    public Miniprogram(String appid, String pagepath) {
        this.appid = appid;
        this.pagepath = pagepath;
    }

    public Miniprogram() {
    }

    public String getAppid() {
        return this.appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPagepath() {
        return this.pagepath;
    }

    public void setPagepath(String pagepath) {
        this.pagepath = pagepath;
    }
}
