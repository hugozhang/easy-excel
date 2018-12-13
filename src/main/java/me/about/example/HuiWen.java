package me.about.example;

public class HuiWen {
    public static void main(String[] args) {
        //System.out.println(longestPalindrome2("1263625"));// babcbabcbaccba
        System.out.println(longestPalindromeSubseq("1263625"));
    }

    public static String longestPalindrome2(String s) {
        if (s == null) return null;
        if (s.length() <= 1) return s;
        int maxLen = 0;
        String longestStr = null;
        int length = s.length();
        int[][] table = new int[length][length];
        // every single letter is palindrome
        for (int i = 0; i < length; i++) {
            table[i][i] = 1;
        }
        printTable(table);
        // e.g. bcba
        // two consecutive same letters are palindrome
        for (int i = 0; i <= length - 2; i++) {
            System.out.println("i="+i+" "+s.charAt(i));
            System.out.println("i="+i+" "+s.charAt(i+1));
            if (s.charAt(i) == s.charAt(i + 1)) {
                table[i][i + 1] = 1;
                longestStr = s.substring(i, i + 2);
            }
        }
        System.out.println(longestStr);
        printTable(table);
        // condition for calculate whole table
        for (int l = 3; l <= length; l++) {
            for (int i = 0; i <= length - l; i++) {
                int j = i + l - 1;
                if (s.charAt(i) == s.charAt(j)) {
                    table[i][j] = table[i + 1][j - 1];
                    if (table[i][j] == 1 && l > maxLen) longestStr = s.substring(i, j + 1);
                } else {
                    table[i][j] = 0;
                }
                printTable(table);
            }
        }
        return longestStr;
    }

    public static void printTable(int[][] x) {
        for (int[] y : x) {
            for (int z : y) {
                System.out.print(z + " ");
            }
            System.out.println();
        }
        System.out.println("------");
    }
    
    public static int longestPalindromeSubseq(String s) {
        int len = s.length();
        int [][] dp = new int[len][len];
        for(int i = len - 1; i>=0; i--){
            dp[i][i] = 1;
            for(int j = i+1; j < len; j++){
                if(s.charAt(i) == s.charAt(j))
                    dp[i][j] = dp[i+1][j-1] + 2;
                else
                    dp[i][j] = Math.max(dp[i+1][j], dp[i][j-1]);
            }
        }
        printTable(dp);
        return dp[0][len-1];
    }

}
