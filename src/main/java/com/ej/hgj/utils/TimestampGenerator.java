package com.ej.hgj.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 使用时间戳生成流水号
 */
public class TimestampGenerator {

    private static int counter = 0;

    public static synchronized String generateSerialNumber() {
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // 定义日期格式化模式
        String formattedDateTime = sdf.format(new Date(timestamp)); // 将长时间转换为指定格式的字符串
        formattedDateTime += RandomStringUtils.getRandomStringByLength(6);
        return formattedDateTime;
        //return String.format("%d%05d", formattedDateTime, counter++);
    }

    public static void main(String[] args) {
        System.out.println(TimestampGenerator.generateSerialNumber());
    }
}