package com.ej.hgj.request;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XStreamAlias("xml")
public class ReqBaseMessage {
    @XStreamAlias("FromUserName")
    private String FromUserName;
    @XStreamAlias("ToUserName")
    private String ToUserName;
    @XStreamAlias("MsgType")
    private String MsgType;
    @XStreamAlias("CreateTime")
    private long CreateTime;

    public ReqBaseMessage() {
    }

    public String getFromUserName() {
        return this.FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.FromUserName = fromUserName;
    }

    public String getToUserName() {
        return this.ToUserName;
    }

    public void setToUserName(String toUserName) {
        this.ToUserName = toUserName;
    }

    public String getMsgType() {
        return this.MsgType;
    }

    public void setMsgType(String msgType) {
        this.MsgType = msgType;
    }

    public long getCreateTime() {
        return this.CreateTime;
    }

    public void setCreateTime(long createTime) {
        this.CreateTime = createTime;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
