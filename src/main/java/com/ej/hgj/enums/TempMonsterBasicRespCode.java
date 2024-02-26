/**
 * 
 * 上海云之富金融信息服务有限公司
 * Copyright (c) 2014-2020 YunCF,Inc.All Rights Reserved.
 */
package com.ej.hgj.enums;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * @author juqi
 * @version $Id: TempMonsterBasicRespCode.java, v 0.1 Mar 11, 2020 4:21:53 PM juqi Exp $
 */
public enum TempMonsterBasicRespCode {

    USR_CONF_NOT_EXIST("013", "企业配置信息缺失"),
    USR_STAT_NOT_NORMAL("012", "企业信息非正常"),
    WECHAT_TEMPLATE_MSG_FAIL("011", "微信模板消息推送失败"),
    WECHAT_TEMPLATE_CLOSE("010", "微信模板消息关闭"),
    WECHAT_TEMPLATE_NO_EXIST("009", "微信模板消息不存在"),
    REFRESH_TOKEN_FAIL("008", "刷新accesstoken失败"),
    SELECT_DB_ALREADY_EXIST("007", "查询数据已存在"),
    BUSI_ERROR("006","业务发生异常"),
    WS_FAIL("005", "接口调用失败"),
    UPDATE_DB_FAIL("004", "更新数据失败"),
    ADD_DB_FAIL("003", "库表插入数据失败"),
    SELECT_DB_NULL("002", "查询数据为空"),
    REQ_PARAM_NULL("001", "请求参数为空"),
    SIGN_VERIFICATION_ABNORMAL("014", "验签发生异常"),
    REFUND_NOTIFY_UNKNOW_RESP_CODE("015", "退款通知未识别响应码"),
    HU_WX_BIND_NOT_EXIST("016", "用户未绑定"),
    
    FAIL("999","系统错误"),
    SUCCESS("000", "成功");
    
    
    /**
     * @param respCode
     * @param respDesc
     */
    private TempMonsterBasicRespCode(String respCode, String respDesc) {
        this.respCode = respCode;
        this.respDesc = respDesc;
    }
    
    private String respCode;
    private String respDesc;
    public String getRespCode() {
        return respCode;
    }
    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }
    public String getRespDesc() {
        return respDesc;
    }
    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
