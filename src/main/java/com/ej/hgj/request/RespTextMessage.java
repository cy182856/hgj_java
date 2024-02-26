package com.ej.hgj.request;

public class RespTextMessage extends RespBaseMessage {
    private String Content;

    public RespTextMessage() {
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }
}
