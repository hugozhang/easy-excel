package me.about.example;

import java.util.LinkedList;
import java.util.List;

import me.about.example.Solution.ListNode;

public class Solution {
    
    class ListNode{
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
        @Override
        public String toString() {
            return "ListNode [val=" + val + "]";
        }
    }
    
    public static ListNode deleteDuplicates(ListNode head) {
        if(head == null || head.next == null)return head;
        System.out.println(head.next);
        head.next = deleteDuplicates(head.next);
        return head.val == head.next.val ? head.next : head;
}

    //这个解法思路来自递归
    public static List<String> letterCombinations(String digits) {
        
        
        LinkedList<String> ans = new LinkedList<String>();
        if(digits.isEmpty()) return ans;
        String[] mapping = new String[] {"0", "1", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        ans.add("");
        while(ans.peek().length()!=digits.length()){
            String remove = ans.remove();
            String map = mapping[digits.charAt(remove.length())-'0'];
            for(char c: map.toCharArray()){
                ans.addLast(remove+c);
            }
        }
        return ans;
    }
    
    
    public static int lengthOfLastWord(String s) {
        int start = s.lastIndexOf(" ") - 1;
        System.out.print(s.length() + ":"+start);
        int len = s.length();
        return len - start;
    }
    
    public static void main(String[] args) {
        //System.out.println(letterCombinations("23456789"));
        //System.out.println(lengthOfLastWord("Hello World"));
        
        ListNode head = new Solution().new ListNode(1);
        head.next = new Solution().new ListNode(2);
        head.next.next = new Solution().new ListNode(2);
        head.next.next.next = new Solution().new ListNode(3);
        deleteDuplicates(head);
        System.out.println(head);
    }
    
}
