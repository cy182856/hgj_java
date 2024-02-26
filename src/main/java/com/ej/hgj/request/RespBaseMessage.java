package com.ej.hgj.request;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RespBaseMessage {
    private String ToUserName;
    private String FromUserName;
    private long CreateTime;
    private String MsgType;
    private int FuncFlag;

    public RespBaseMessage() {
    }

    public String getToUserName() {
        return this.ToUserName;
    }

    public void setToUserName(String toUserName) {
        this.ToUserName = toUserName;
    }

    public String getFromUserName() {
        return this.FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.FromUserName = fromUserName;
    }

    public long getCreateTime() {
        return this.CreateTime;
    }

    public void setCreateTime(long createTime) {
        this.CreateTime = createTime;
    }

    public String getMsgType() {
        return this.MsgType;
    }

    public void setMsgType(String msgType) {
        this.MsgType = msgType;
    }

    public int getFuncFlag() {
        return this.FuncFlag;
    }

    public void setFuncFlag(int funcFlag) {
        this.FuncFlag = funcFlag;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
