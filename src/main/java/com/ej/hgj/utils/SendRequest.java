package com.ej.hgj.utils;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.io.IOException;
public class SendRequest {
    static Logger logger = LoggerFactory.getLogger(SendRequest.class);

    public static String get() {
        int timeout = 120000;
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
        // 参数
        StringBuffer params = new StringBuffer();
        params.append("?");
        params.append("pageNum=1");
        // 创建Get请求
        HttpGet httpGet = new HttpGet("http://192.168.79.5:9999/smp/testproject/test/list" + params);
        httpGet.addHeader("Content-Type","application/json;charset=UTF-8");
        httpGet.setConfig(defaultRequestConfig);
        // 响应模型
        CloseableHttpResponse response = null;
        //返回数据
        String res = "";
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            logger.info("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                res = EntityUtils.toString(responseEntity,"UTF-8");
                logger.info("响应内容为:" + res);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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
        return res;
    }

    public static String post(){
        int timeout = 120000;
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
        // 参数
        StringBuffer params = new StringBuffer();
        try {
            // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
            //第一个参数是名字，第二个参数是编码格式
            params.append("?name=" + URLEncoder.encode("张三", "utf-8"));
            params.append("&age=" + URLEncoder.encode("10", "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://192.168.79.5:9999/smp/testproject/test/save" + params);
        // 设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.addHeader("Content-Type","application/json;charset=UTF-8");
        httpPost.setConfig(defaultRequestConfig);
        // 响应模型
        CloseableHttpResponse response = null;
        String res = "";
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            res = (response.getStatusLine().getStatusCode())+"";
            logger.info("响应状态为:" + response.getStatusLine().getStatusCode());
            if (responseEntity != null) {
                logger.info("响应内容为:" + EntityUtils.toString(responseEntity,"UTF-8"));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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
        return  res;
    }

}
