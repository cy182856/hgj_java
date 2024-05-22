package com.ej.hgj.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

public class SyPostClient {

    static  Logger logger = LoggerFactory.getLogger(SyPostClient.class);

    public static String post(String params, String token, String url){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            if(StringUtils.isNotBlank(token)) {
                httpPost.addHeader("Authorization", token);
            }
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            logger.info("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                ///logger.info("响应内容长度为:" + responseEntity.getContentLength() + "响应内容为:" + EntityUtils.toString(responseEntity));
                return EntityUtils.toString(responseEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    // 获取token-154号接口
    public static String getToken(String url) {
        StringBuffer params = new StringBuffer();
        params.append("p0=" + "UserAudit_GetTokenInfo");
        params.append("&p1=");
        params.append("&p2=");
        params.append("&p3=");
        params.append("&p4=");
        params.append("&p5=");
        params.append("&p6=");

        // 正式环境用户名密码
        String userId = "WeiXinXiaoChengXu";
        String pwd = CommonUtils.pwdMd5("Ff123!@#");
        params.append("&p7=" + "{UserId: \"" + userId + "\",\"Pwd\": \"" + pwd + "\"}");
        //params.append("&p7=" + "{UserId: \"WeiXinXiaoChengXu\",\"Pwd\": \"1a784d12004fd97b426e40f4b116bdda\"}");

        // 测试环境用户名密码
//        String userId = "system";
//        String pwd = CommonUtils.pwdMd5("syswin");
//        params.append("&p7=" + "{UserId: \"" + userId + "\",\"Pwd\": \"" + pwd + "\"}");
        //params.append("&p7=" + "{UserId: \"system\",\"Pwd\": \"8b717b79f8d498c14fa4f0320d4fb6c2\"}");

        String result =  SyPostClient.post(params.toString(), "", url);
        // 将字符串转为XML
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(result);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        List<Node> nodes = doc.selectNodes("string");
        // 获取返回的值
        String text = nodes.get(0).getText();
        // 转json
        JSONObject jsonObject = JSONObject.parseObject(text);
        // 获取UserAudit_GetTokenInfo
        String userAuditGetTokenInfo = jsonObject.get("UserAudit_GetTokenInfo") + "";
        // 将UserAudit_GetTokenInfo转为数组
        JSONArray jsonArray = JSON.parseArray(userAuditGetTokenInfo);
        // 获取数组中的属性
        String tokenInfo = JSONObject.toJSONString(jsonArray.get(0));
        JSONObject tokenInfoJson = JSONObject.parseObject(tokenInfo);
        // 获取syswin
        String sysWin = tokenInfoJson.get("Syswin") + "";
//        JSONArray jsonArraySysWin = JSON.parseArray(sysWin);
//        String infoStr = JSONObject.toJSONString(jsonArraySysWin.get(0));
        JSONObject jsonResult = JSONObject.parseObject(sysWin);
        // 获取token
        String token = jsonResult.get("TokenInfo") + "";
        // 获取token过期时间
        // String expireTime = jsonResult.get("ExpireTime") + "";
        return token;
    }


    // 94-客服回访
    public static JSONObject userRev2ServiceCustomerServiceReturn(String p0, String p7, String token, String url){
        StringBuffer params = new StringBuffer();
        params.append("p0=" + p0);
        params.append("&p1=" + "");
        params.append("&p2=" + "");
        params.append("&p3=" + "");
        params.append("&p4=" + "");
        params.append("&p5=" + "");
        params.append("&p6=" + "");
        params.append("&p7=" + p7);
        String result =  SyPostClient.post(params.toString(),token, url);
        // 将字符串转为XML
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(result);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        List<Node> nodes = doc.selectNodes("string");
        // 获取返回的值
        String text = nodes.get(0).getText();
        // 转json
        JSONObject jsonObject = JSONObject.parseObject(text);
        //String str = jsonObject.get(p0) + "";
        //JSONArray jsonArray = JSON.parseArray(str);
        // 获取数组中的属性
        //String info = JSONObject.toJSONString(jsonArray.get(0));
        //JSONObject infoJson = JSONObject.parseObject(info);
        // 获取syswin
        //String sysWin = infoJson.get("Syswin") + "";
        //JSONArray jsonArraySysWin = JSON.parseArray(sysWin);
        //String infoStr = JSONObject.toJSONString(jsonArraySysWin.get(0));
        //JSONObject jsonResult = JSONObject.parseObject(infoStr);
        return jsonObject;
    }

    public static void main(String[] args) throws DocumentException {
        StringBuffer params = new StringBuffer();
        params.append("p0=" + "UserAudit_GetTokenInfo");
        params.append("&p1=");
        params.append("&p2=");
        params.append("&p3=");
        params.append("&p4=");
        params.append("&p5=");
        params.append("&p6=");
        params.append("&p7=" + "{UserId: \"WeiXinXiaoChengXu\",\"Pwd\": \"1a784d12004fd97b426e40f4b116bdda\"}");
        String result =  SyPostClient.post(params.toString(),null,null);
        // 将字符串转为XML
        Document doc = DocumentHelper.parseText(result);
        List<Node> nodes = doc.selectNodes("string");
        // 获取返回的值
        String text = nodes.get(0).getText();
        // 转json
        JSONObject jsonObject = JSONObject.parseObject(text);
        // 获取UserAudit_GetTokenInfo
        String userAuditGetTokenInfo = jsonObject.get("UserAudit_GetTokenInfo") + "";
        // 将UserAudit_GetTokenInfo转为数组
        JSONArray jsonArray = JSON.parseArray(userAuditGetTokenInfo);
        // 获取数组中的属性
        String tokenInfo = JSONObject.toJSONString(jsonArray.get(0));
        JSONObject tokenInfoJson = JSONObject.parseObject(tokenInfo);
        // 获取syswin
        String syswin = tokenInfoJson.get("Syswin") + "";
        JSONObject syswinJson = JSONObject.parseObject(syswin);
        // 获取token
        String token = syswinJson.get("TokenInfo") + "";
        // 获取token过期时间
        String expireTime = syswinJson.get("ExpireTime") + "";
        System.out.println("token:" + token + "---------" + "expireTime:" + expireTime);

    }
}
