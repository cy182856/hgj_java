package com.ej.hgj.utils.wechat;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.entity.cst.QrCode;
import com.ej.hgj.enums.QrRespEnum;
import com.ej.hgj.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;

public class WechatPubNumUtils {

    public WechatPubNumUtils() {
    }

    public static String qrcode(String actionName, String second, String param, String respType, String accessToken) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN".replaceAll("ACCESS_TOKEN", accessToken);
        QrCode qrCode = null;
        if (actionName.contains("STR")) {
            qrCode = new QrCode(second, actionName, (String)null, param);
        } else {
            qrCode = new QrCode(second, actionName, param, (String)null);
        }

        String qrcodeJson = JsonUtils.writeEntiry2JSON(qrCode);
        JSONObject jsonObject = httpRequest(url, "POST", qrcodeJson);
        if (StringUtils.equals(respType, QrRespEnum.QR_URL.getCode())) {
            return jsonObject.getString("url");
        } else {
            String ticket = urlEncodeUTF8(jsonObject.getString("ticket"));
            return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET".replaceAll("TICKET", ticket);
        }
    }

    public static String urlEncodeUTF8(String source) {
        String result = source;

        try {
            result = URLEncoder.encode(source, "utf-8");
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return result;
    }

    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) throws IOException {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();

        try {
            TrustManager[] tm = new TrustManager[]{new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[])null, tm, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection)url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setRequestMethod(requestMethod);
            if ("GET".equalsIgnoreCase(requestMethod)) {
                httpUrlConn.connect();
            }

            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                System.out.println(outputStr);
                outputStream.close();
            }

            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;

            while((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            httpUrlConn.disconnect();
            jsonObject =  JSONObject.parseObject(buffer.toString());
        } catch (ConnectException | NoSuchAlgorithmException | KeyManagementException e) {
            e.getMessage();
        }

        return jsonObject;
    }

}
