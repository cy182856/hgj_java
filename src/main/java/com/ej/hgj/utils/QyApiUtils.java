package com.ej.hgj.utils;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.utils.wechat.MyX509TrustManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;

/**
 * 企业微信服务端API
 * @author  xia
 * @version $Id: QyApiUtils.java, v 0.1 2020年3月11日 下午4:49:30 xia Exp $
 */
public class QyApiUtils {

    Logger logger = LoggerFactory.getLogger(getClass());

    private static final String POST = "POST";
    
    private static final String GET = "GET";
    
    /** 获取服务商应用凭证 */
    private static final String GET_PROVIDER_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token";
    
    /** 获取第三方应用凭证 */
    private static final String GET_SUITE_TOKEN = " https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token";

    /** 获取应用凭证 */
    private static final String GET_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRECT";

    /** 获取预授权码 */
    private static final String GET_PRE_AUTH_CODE = " https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=SUITE_ACCESS_TOKEN";
    
    /** 获取企业永久授权码 */
    private static final String GET_PERMANENT_CODE = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=SUITE_ACCESS_TOKEN";
    
    /** 获取企业授权信息 */
    private static final String GET_AUTH_INFO = "https://qyapi.weixin.qq.com/cgi-bin/service/get_auth_info?suite_access_token=SUITE_ACCESS_TOKEN";
    
    /** 获取企业凭证 */
    private static final String GET_CORP_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=SUITE_ACCESS_TOKEN";
    
    /** 获取授权企业管理员列表 */
    private static final String GET_ADMIN_LIST = "https://qyapi.weixin.qq.com/cgi-bin/service/get_admin_list?suite_access_token=SUITE_ACCESS_TOKEN";
    
    /** 设置授权配置 */
    private static final String SET_SESSION_INFO = "https://qyapi.weixin.qq.com/cgi-bin/service/set_session_info?suite_access_token=SUITE_ACCESS_TOKEN";
    
    /** 获取部门列表 */
    private static final String GET_DEPARTMENT_LIST = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=";
    
    /** 获取部门成员 */
    private static final String GET_SIMPLE_LIST = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=";
    
    /** 获取部门成员详情 */
    private static final String GET_USER_LIST = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=";
    
    /** 读取成员 */
    private static final String GET_USER = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=";
    
    /** 上传待转译文件 */
    private static final String MEDIA_UPLOAD = "https://qyapi.weixin.qq.com/cgi-bin/service/media/upload?provider_access_token=ACCESS_TOKEN&type=TYPE";
    
    /** 异步通讯录id转译 */
    private static final String ID_TRANSLATE = "https://qyapi.weixin.qq.com/cgi-bin/service/contact/id_translate?provider_access_token=ACCESS_TOKEN";
    
    /** 获取异步任务结果 */
    private static final String BATCH_JOB_RESULT = "https://qyapi.weixin.qq.com/cgi-bin/service/batch/getresult?provider_access_token=";
    
    /** 临时登录凭证校验接口 */
    private static final String JS_CODE2SESSION  = "https://qyapi.weixin.qq.com/cgi-bin/service/miniprogram/jscode2session?suite_access_token=";

    private static final String JS_CODE2_SESSION  = "https://qyapi.weixin.qq.com/cgi-bin/miniprogram/jscode2session?access_token=ACCESS_TOKEN&js_code=CODE&grant_type=authorization_code";
    /** 获取指定应用详情 */
    private static final String GET_SUITE_DETAIL_BY_AGENT_ID= "https://qyapi.weixin.qq.com/cgi-bin/agent/get?access_token=";

    public static JSONObject jsCode2_Session(String token, String code) {
        JSONObject jsonObject = httpRequest(JS_CODE2_SESSION.replace("ACCESS_TOKEN", token).replace("CODE",code), GET, null);
        return jsonObject;
    }

    /**
     * 获内部应用token
     * @return
     */
    public static String getToken(String corpid, String corpsecret) {
        JSONObject jsonObject = httpRequest(GET_TOKEN.replace("ID", corpid).replace("SECRECT",corpsecret), GET, null);
        if (jsonObject != null && jsonObject.containsKey("access_token")) {
            return jsonObject.getString("access_token");
        }
        return StringUtils.EMPTY;
    }

    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {  
        JSONObject jsonObject = null;  
        StringBuffer buffer = new StringBuffer();  
        try {  
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化  
            TrustManager[] tm = { new MyX509TrustManager() };
            //SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE"); 
            SSLContext sslContext = SSLContext.getInstance("TLS");  
            sslContext.init(null, tm, new java.security.SecureRandom());  
            // 从上述SSLContext对象中得到SSLSocketFactory对象  
            SSLSocketFactory ssf = sslContext.getSocketFactory();  
  
            URL url = new URL(requestUrl);  
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();  
            httpUrlConn.setSSLSocketFactory(ssf);  
  
            httpUrlConn.setDoOutput(true);  
            httpUrlConn.setDoInput(true);  
            httpUrlConn.setUseCaches(false);  
            // 设置请求方式（GET/POST）  
            httpUrlConn.setRequestMethod(requestMethod);  
  
            if ("GET".equalsIgnoreCase(requestMethod))  
                httpUrlConn.connect();  
  
            // 当有数据需要提交时  
            if (null != outputStr) {  
                OutputStream outputStream = httpUrlConn.getOutputStream();  
                // 注意编码格式，防止中文乱码  
                outputStream.write(outputStr.getBytes("UTF-8"));  
                outputStream.close();  
            }  
  
            // 将返回的输入流转换成字符串  
            InputStream inputStream = httpUrlConn.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
  
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
            bufferedReader.close();  
            inputStreamReader.close();  
            // 释放资源  
            inputStream.close();  
            inputStream = null;  
            httpUrlConn.disconnect();  
            System.out.println("HTTP请求返回：" + buffer.toString());
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce) {
        } catch (Exception e) {
        }
        return jsonObject;  
    }
}
