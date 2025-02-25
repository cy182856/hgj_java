package com.ej.hgj.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomNumberGenerator {

    public static Set<Integer> generateRandomNumbers(int min, int max, int count) {
        Set<Integer> numbers = new HashSet<>();
        Random random = new Random();
        while (numbers.size() < count) {
            // 生成随机数字
            int randomNumber = random.nextInt(max - min + 1) + min;

            // 将数字加入Set集合中
            numbers.add(randomNumber);
        }
        return numbers;
    }

    public static void main(String[] args) {
        // 生成6位不重复数字
        Set<Integer> numbers = generateRandomNumbers(0, 999999, 1);
        // 打印生成的数字
        System.out.print(numbers);

    }
}
