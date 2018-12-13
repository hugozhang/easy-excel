package me.about.test;

import java.util.Stack;

public class Test {

    
    public static int reverse(int x) {
        long res = 0;
        while (x != 0) {
            res *= 10;
            res += x % 10;
            x /= 10;
        }
        return (int)res == res ? (int)res : 0;

    }
    
    public static boolean isPalindrome(int x) {
        int _x = x;
        if(_x<0) return false;
        int reverseNum = 0;
        while(_x!=0){
            reverseNum *= 10;
            reverseNum += _x % 10;
            _x /= 10;
        }
        return x == reverseNum;
    }

  //定义三组 配对
    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<Character>();
        for(Character c : s.toCharArray()) {
            if(c == '(') {
                stack.push(')');
            } else if(c == '[') {
                stack.push(']');
            } else if(c == '{') {
                stack.push('}');
            } else {
                return !(stack.isEmpty() || stack.pop() != c);
            }
        }
        
        return stack.isEmpty();
    }
    
    public static int strStr(String haystack, String needle) {
        int len1 = haystack.length();
        int len2 = needle.length();
        for(int i=0;i<=len1;i++){
            if(i+len2 >len1) return -1;
            if(haystack.substring(i,i+len2).equals(needle)) {
                return i;
            }
        }
        return -1;
    }
    

    public static void main(String[] args) {
        String s = "";
        System.out.println(strStr("",""));
    }
    
}
