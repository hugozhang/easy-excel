package me.about.gc;

public class Test {

    public static void test2(){
        byte[] byte1 = new byte[1024*1024/2];
        byte[] byte2 = new byte[1024*1024*8];
        byte2 = null;
        byte2 = new byte[1024*1024*8];
        System.gc();    //注释此行
    }

    public static void main(String[] args) {
        test2();
    }
}