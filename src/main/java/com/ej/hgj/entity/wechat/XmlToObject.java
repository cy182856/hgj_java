package com.ej.hgj.entity.wechat;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlToObject {

    private String FromUserName;
    private String ToUserName;
    private String MsgType;
    private long CreateTime;
    private String Event;
    private String EventKey;
    private String Ticket;
    private String MsgID;
    private String Status;
    private String KfAccount;
    private String CloseType;
    private String SuccOrderId;
    private String FailOrderId;
    private String AuthorizeAppId;
    private String Source;
    private String SPAppId;

}