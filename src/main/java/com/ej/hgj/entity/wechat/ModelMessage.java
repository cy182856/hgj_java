package com.ej.hgj.entity.wechat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ModelMessage {
    private String touser;
    private String template_id;
    private String url;
    private TempleModel data;
    private Miniprogram miniprogram;

    public ModelMessage(String touser, String template_id, TempleModel data, Miniprogram miniprogram) {
        this.touser = touser;
        this.template_id = template_id;
        this.data = data;
        this.miniprogram = miniprogram;
    }

    public ModelMessage(String touser, String template_id, TempleModel data, Miniprogram miniprogram, String url) {
        this.touser = touser;
        this.template_id = template_id;
        this.url = url;
        this.data = data;
        this.miniprogram = miniprogram;
    }

    public ModelMessage() {
    }

    public ModelMessage(String touser, String template_id, TempleModel data, String url) {
        this.touser = touser;
        this.template_id = template_id;
        this.url = url;
        this.data = data;
    }

    public String getTouser() {
        return this.touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return this.template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TempleModel getData() {
        return this.data;
    }

    public void setData(TempleModel data) {
        this.data = data;
    }

    public Miniprogram getMiniprogram() {
        return this.miniprogram;
    }

    public void setMiniprogram(Miniprogram miniprogram) {
        this.miniprogram = miniprogram;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
