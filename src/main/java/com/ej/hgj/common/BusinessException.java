/**
 * 
 * 上海云之富金融信息服务有限公司
 * Copyright (c) 2014-2020 YunCF,Inc.All Rights Reserved.
 */
package com.ej.hgj.common;

import com.ej.hgj.enums.TempMonsterBasicRespCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * @author juqi
 * @version $Id: BusinessException.java, v 0.1 Aug 11, 2020 10:50:43 AM juqi Exp $
 */
public class BusinessException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = 1706834787717796585L;
    
    private String errCode;
    private String errDesc;
    
    /**
     * @param errCode
     * @param errDesc
     */
    public BusinessException(String errCode, String errDesc) {
        super();
        this.errCode = errCode;
        this.errDesc = errDesc;
    }
    
    /**
     * 
     * @param tempMonsterBasicRespCode
     */
    public BusinessException(TempMonsterBasicRespCode tempMonsterBasicRespCode) {
        super();
        this.errCode = tempMonsterBasicRespCode.getRespCode();
        this.errDesc = tempMonsterBasicRespCode.getRespDesc();
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
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
