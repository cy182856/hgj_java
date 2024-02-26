package com.ej.hgj.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    public static String post(StringBuffer params){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://192.168.5.201:4321/NetApp/CstService.asmx/GetService");
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(params.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            logger.info("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                //logger.info("响应内容长度为:" + responseEntity.getContentLength() + "响应内容为:" + EntityUtils.toString(responseEntity));
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
        String result =  SyPostClient.post(params);
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
