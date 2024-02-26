package com.ej.hgj.request;
import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("xml")
public class ReqEventMessage extends ReqBaseMessage {
    @XStreamAlias("Event")
    private String Event;
    @XStreamAlias("EventKey")
    private String EventKey;
    @XStreamAlias("Ticket")
    private String Ticket;
    @XStreamAlias("MsgID")
    private String MsgID;
    @XStreamAlias("Status")
    private String Status;
    @XStreamAlias("KfAccount")
    private String KfAccount;
    @XStreamAlias("CloseType")
    private String CloseType;
    @XStreamAlias("SuccOrderId")
    private String SuccOrderId;
    @XStreamAlias("FailOrderId")
    private String FailOrderId;
    @XStreamAlias("AuthorizeAppId")
    private String AuthorizeAppId;
    @XStreamAlias("Source")
    private String Source;
    @XStreamAlias("SPAppId")
    private String SPAppId;

    public ReqEventMessage() {
    }

    public String getEventKey() {
        return this.EventKey;
    }

    public void setEventKey(String eventKey) {
        this.EventKey = eventKey;
    }

    public String getEvent() {
        return this.Event;
    }

    public void setEvent(String event) {
        this.Event = event;
    }

    public String getTicket() {
        return this.Ticket;
    }

    public void setTicket(String ticket) {
        this.Ticket = ticket;
    }

    public String getMsgID() {
        return this.MsgID;
    }

    public void setMsgID(String msgID) {
        this.MsgID = msgID;
    }

    public String getStatus() {
        return this.Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getKfAccount() {
        return this.KfAccount;
    }

    public void setKfAccount(String kfAccount) {
        this.KfAccount = kfAccount;
    }

    public String getCloseType() {
        return this.CloseType;
    }

    public void setCloseType(String closeType) {
        this.CloseType = closeType;
    }

    public String getSuccOrderId() {
        return this.SuccOrderId;
    }

    public String getFailOrderId() {
        return this.FailOrderId;
    }

    public String getAuthorizeAppId() {
        return this.AuthorizeAppId;
    }

    public String getSource() {
        return this.Source;
    }

    public String getSPAppId() {
        return this.SPAppId;
    }

    public void setSuccOrderId(String succOrderId) {
        this.SuccOrderId = succOrderId;
    }

    public void setFailOrderId(String failOrderId) {
        this.FailOrderId = failOrderId;
    }

    public void setAuthorizeAppId(String authorizeAppId) {
        this.AuthorizeAppId = authorizeAppId;
    }

    public void setSource(String source) {
        this.Source = source;
    }

    public void setSPAppId(String sPAppId) {
        this.SPAppId = sPAppId;
    }
}