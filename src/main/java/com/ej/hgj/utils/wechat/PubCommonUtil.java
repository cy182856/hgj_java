/**
 * 
 * 上海云之富金融信息服务有限公司
 * Copyright (c) 2014-2020 YunCF,Inc.All Rights Reserved.
 */
package com.ej.hgj.utils.wechat;

import java.util.*;

/**
 * 
 * @author juqi
 * @version $Id: JiapubCommonUtil.java, v 0.1 Aug 31, 2020 5:24:23 PM juqi Exp $
 */
public class PubCommonUtil {

    /**
     * 生成要请求的字符串
     * @param requestParams 请求前的参数数组
     * @return
     */
    public static String buildRequestPramsToStr(Map<String, String> requestParams) {
        //除去数组中的空值和签名参数 sign_type signType sign
        Map<String, String> sPara = paraFilter(requestParams);
        return createLinkString(sPara);
    }
    
    /**
     * 
     * @param netParam
     * @return
     */
    private static Map<String, String> paraFilter(Map<String, String> netParam) {
        Map<String, String> result = new HashMap<String, String>();
        if(netParam == null || netParam.size() <= 0) {
            return result;
        }
        for(String key : netParam.keySet()) {
            String value = netParam.get(key);
            if (value == null || value.equals("") 
                    || key.equalsIgnoreCase("Sign")) {
                    continue;
                }
                result.put(key, value);
        }
        return result;
    }
    
    /**
     * 把数组所有元素排序，并按照"参数=参数值"且用"&"字符拼接成字符串
     * @param params
     * @return
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
    
}
