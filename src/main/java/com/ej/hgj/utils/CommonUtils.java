package com.ej.hgj.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtils {

    public static String pwdMd5(String pwd){
        StringBuilder result = new StringBuilder();
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] data = md5.digest(pwd.getBytes());
        byte[] output = md5.digest(data);
        int j;
        for (int i = 0; i < output.length; i++) {
            j = output[i];
            if(j<0){
                j+=256;
            }
            if(j<16){
                result.append("0");
                //result.append(Integer.toHexString(j));
            }
            result.append(Integer.toHexString(output[i] & 0xFF));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(pwdMd5("syswin"));
    }
}
