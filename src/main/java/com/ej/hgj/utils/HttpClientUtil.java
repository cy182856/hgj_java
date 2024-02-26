package com.ej.hgj.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtil {

    public static String doGet(String url) {
        CloseableHttpClient httpCilent2 = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)   //设置连接超时时间
                .setConnectionRequestTimeout(5000) // 设置请求超时时间
                .setSocketTimeout(5000)
                .setRedirectsEnabled(true)//默认允许自动重定向
                .build();
        HttpGet httpGet2 = new HttpGet(url);
        httpGet2.setConfig(requestConfig);
        httpGet2.setHeader("Content-Type", "application/json;charset=UTF-8");
        ;
        String srtResult = "";
        try {
            HttpResponse httpResponse = httpCilent2.execute(httpGet2);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                srtResult = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");//获得返回的结果
            } else if (httpResponse.getStatusLine().getStatusCode() == 400) {
                //..........
            } else if (httpResponse.getStatusLine().getStatusCode() == 500) {
                //.............
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpCilent2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return srtResult;

    }

    public static JSONObject sendPost(String url, String jsonMenu){
        cn.hutool.http.HttpResponse httpResponse = null;
        try {
            // 设置请求头
            Map<String, String > heads = new HashMap<String, String>();
            heads.put("Content-Type", "application/json;charset=UTF-8");
            httpResponse =  HttpRequest.post(url) // url
                    .body(jsonMenu) // json参数
                    .timeout(5 * 60 * 1000) // 超时
                    .execute(); // 请求
            if(httpResponse.getStatus() == 200){
                //成功后响应数据
                String result = httpResponse.body();
                JSONObject jsonResult = JSONObject.parseObject(result);
                return jsonResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                //释放连接
                if(httpResponse != null){
                    httpResponse.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }
}
