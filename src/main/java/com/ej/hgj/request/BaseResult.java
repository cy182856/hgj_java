package com.ej.hgj.request;

import java.io.Serializable;

/**
 * 
 * @author  xia
 * @version $Id: BaseResult.java, v 0.1 2020年8月6日 下午3:50:54 xia Exp $
 */
public class BaseResult implements Serializable {

    /**  */
    private static final long serialVersionUID = 2585339599036429552L;
    
    private String respCode;
    
    private String errCode;
    
    private String errDesc;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

}
