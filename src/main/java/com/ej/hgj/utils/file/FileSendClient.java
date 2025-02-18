package com.ej.hgj.utils.file;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.entity.file.FileMessage;
import com.ej.hgj.entity.file.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.xml.ws.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FileSendClient {

    static Logger logger = LoggerFactory.getLogger(FileSendClient.class);

    private static final int TIMEOUT_SECONDS = 60;

    public static void sendFile(FileMessage fileMessage){
        String clientId = "6c476887-9258-d9xx966e-e0d3";
        String secret = "4251-ac0b-f33af9922533-4a38-adf6-0f8f50dfa04f-1ca0f257-5e7a-4ec2-bc44-480103e09042";
        String url = Constant.REMOTE_FILE_URL+"/File";
        try {
            executeAsync(fileMessage,clientId,secret,url).get();
        } catch (Exception e) {
            logger.info("Error in Send: " + e.getMessage());
        }
    }

    public static <T> Response executeAsync(T data, String clientId, String secret, String url) throws Exception {
        String nonce = UUID.randomUUID().toString();  // 生成随机的nonce值
        long timestamp = System.currentTimeMillis();  // 获取当前时间戳

        // 构建签名字符串
        String joinSignStr = joinSignatureStr(clientId, url, timestamp, nonce);
        // 使用HMAC生成签名
        String signature = genHmac(joinSignStr, secret);

        // 使用AES256对数据进行加密
        String encryptionKey = clientId + secret;
        String encryptedData = encryptData(new ObjectMapper().writeValueAsString(data), encryptionKey);

        // 创建请求对象
        Request reqDto = new Request(clientId, encryptedData, signature, nonce, timestamp);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        // 将请求对象转为JSON字符串
        String reqStr = objectMapper.writeValueAsString(reqDto);

        // 发送请求并获得响应
        String response = sendAsync(reqStr, url);
        // 将响应结果反序列化为Response对象并返回
        return objectMapper.readValue(response, Response.class);
    }

    /**
     * 发送请求并获取响应（同步操作）
     * @param bodyStr 请求体内容
     * @param url 请求的URL
     * @return 响应内容的字符串
     * @throws IOException
     */
    private static String sendAsync(String bodyStr, String url) throws Exception {
        HttpURLConnection connection = null;
        try {
            // 创建HTTP连接
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            // 设置连接和读取超时时间
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
            connection.setDoOutput(true); // 设置输出流
            connection.setRequestProperty("Content-Type", "application/json"); // 设置请求头
            // 写入请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = bodyStr.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            // 获取响应码
            int statusCode = connection.getResponseCode();
            // 判断是否成功
            if (statusCode == HttpURLConnection.HTTP_OK) {
                logger.info("文件发送成功：url:" + url+"||data:"+bodyStr);
                // 读取响应体并返回
                try (InputStream is = connection.getInputStream()) {
                    return connection.getContent()+"";
                }
            } else {
                logger.info("文件发送失败：url:" + url+"||data:"+bodyStr);
                // 处理错误响应
                try (InputStream is = connection.getErrorStream()) {
                    String responseBody = connection.getContent()+"";
                    throw new Exception("Request failed: " + statusCode + ", Response: " + responseBody);
                }
            }
        } finally {
            // 关闭连接
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 使用AES算法加密数据
     * @param plaintext 要加密的数据
     * @param key 密钥
     * @return 加密后的数据（Base64编码）
     * @throws Exception
     */
    private static String encryptData(String plaintext, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = getKeyBytes(key, 256 / 8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] iv = cipher.getIV();  // 获取IV向量
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(iv);
        outputStream.write(encryptedBytes);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * 从密钥中生成固定大小的字节数组
     * @param key 密钥
     * @param keySize 密钥的大小
     * @return 密钥字节数组
     * @throws NoSuchAlgorithmException
     */
    private static byte[] getKeyBytes(String key, int keySize) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(key.getBytes());
        byte[] keySizedBytes = new byte[keySize];
        System.arraycopy(keyBytes, 0, keySizedBytes, 0, keySizedBytes.length);
        return keySizedBytes;
    }

    /**
     * 使用HMAC算法生成签名
     * @param data 要签名的数据
     * @param key 密钥
     * @return 签名结果（Base64编码）
     * @throws Exception
     */
    private static String genHmac(String data, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        byte[] hashBytes = mac.doFinal(data.getBytes());

        // 将字节数组转换为Hex字符串，再转换为Base64
        String hex = bytesToHex(hashBytes);
        return Base64.getEncoder().encodeToString(hex.getBytes("UTF-8"));
    }

    /**
     * 将字节数组转换为Hex字符串
     * @param bytes 字节数组
     * @return Hex字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * 构建签名字符串
     * @param clientId 客户端ID
     * @param url 请求的URL
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @return 签名字符串
     */
    private static String joinSignatureStr(String clientId, String url, long timestamp, String nonce) {
        String method = url.substring(url.lastIndexOf("/") + 1);  // 从URL中提取方法
        return String.format("clientId=%s&method=%s&timestamp=%d&nonce=%s", clientId, method, timestamp, nonce);
    }

    /**
     * 下载文件并返回字文件内容
     * @param fileUrl
     * @return
     */
    public static String downloadFileContent(String fileUrl)  {
        String base64Img = "";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                base64Img += line;
            }
            // 关闭文件
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64Img;
    }

    /**
     * 下载文件并返回字节数组
     * @param fileUrl
     * @return
     */
    public static byte[] downloadFileByte(String fileUrl)  {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件并返回BufferedImage
     * @param fileUrl
     * @return
     */
    public static BufferedImage downloadFileBufferedImage(String fileUrl)  {
        BufferedImage bufferedImage = null;
        ImageInputStream imageInputStream = null;
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            inputStream = connection.getInputStream();
            // 将InputStream包装成ImageInputStream
            imageInputStream = new MemoryCacheImageInputStream(inputStream);
            // 从ImageInputStream中读取图像
            bufferedImage = ImageIO.read(imageInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    // 关闭流（很重要，避免资源泄露）
                    inputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return bufferedImage;
    }

    /**
     * 下载文件并返回inputStream
     * @param fileUrl
     * @return
     */
    public static InputStream downloadFileInputStream(String fileUrl)  {
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            inputStream = connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
