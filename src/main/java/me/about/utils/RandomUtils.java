package me.about.utils;

public class RandomUtils {

    public static String getRandom(int len) {
        int rand = (int) ((Math.random() * 9 + 1) * Math.pow(10, len - 1));
        return String.valueOf(rand);
    }

    public static void main(String[] args) {
        System.out.println(getRandom(4));
    }

}
