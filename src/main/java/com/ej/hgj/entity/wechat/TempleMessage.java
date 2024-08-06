package com.ej.hgj.entity.wechat;

import lombok.Data;

@Data
public class TempleMessage {
    private String touser;
    private String template_id;
    private String url;
    private String data;
    private Miniprogram miniprogram;

}
