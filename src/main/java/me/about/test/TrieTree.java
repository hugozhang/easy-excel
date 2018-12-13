package me.about.test;

import java.util.HashMap;
import java.util.Map;

class TrieNode{//结点类
    
    private static final int  NUMBER = 26;
    private char _value;
    private boolean _isWord;//从根节点到这个节点存不存在一个单词
    TrieNode[] _children = new TrieNode[NUMBER];//子结点集合
    
    public TrieNode(char c) {
        this.setValue(c);
    }
    public char getValue() {
        return _value;
    }
    public void setValue(char _value) {
        this._value = _value;
    }
    public boolean isWord() {
        return _isWord;
    }
    public void setIsWord(boolean _isWord) {
        this._isWord = _isWord;
    }
    
 
}
 
public class TrieTree {
    
    static String[] _words = {"add","am","good","the","think"};//待插入单词
 
    private boolean searchWord(TrieNode _root, String _word) {
    
        if(null == _root || null == _word || "".equals(_word))
            return false;
        char[] cs = _word.toCharArray();//将字符串转化为字符数组
        for(int i = 0; i < cs.length; i++){
            
            int index;
            if(cs[i] >= 'A' && cs[i] <= 'Z'){
                index = cs[i]-'A';
            }
            else if(cs[i] >= 'a' && cs[i] <= 'z') 
                index = cs[i] - 'a';
            else
                return false;
            
            TrieNode child_node = _root._children[index];
                
            if(null != child_node){//找到相同字符
                if(child_node.isWord())//如果找到该单词
                    return true;
            }               
            
            if(null == child_node)//如果在i层没找到相同字符    
                return false;
            _root = child_node;//重设根节点
            
            
        }
        return false;
    }
 
 
    private void insertIntoTree(TrieNode _root, String _word) {//插入一个单词
        
        if(null == _root || null == _word || "".equals(_word))
            return;
        char[] cs = _word.toCharArray();//将字符串转化为字符数组
        for(int i = 0; i < cs.length; i++){
            
            int index;//对应的索引值
            if(cs[i] >= 'A' && cs[i] <= 'Z'){
                index = cs[i]-'A';
            }
            else if(cs[i] >= 'a' && cs[i] <= 'z') 
                index = cs[i] - 'a';
            else
                return;
            
            TrieNode child_node = _root._children[index];
            if(null == child_node){//如果没找到
                TrieNode new_node = new TrieNode(cs[i]);//创建新节点
                if(i == cs.length-1)//如果遍历到该单词最后一个字符
                    new_node.setIsWord(true);//把该单词存在树中
                _root._children[index] = new_node;//连接该节点
                _root = new_node;
                
            }else
                _root = child_node;//更新树根
            
            
        }
    }
 
    private void printTree(TrieNode _root,char[] _word,int index) {
        
        if(_root == null)
            return;
        if(_root.isWord()){//如果根节点到此节点构成一个单词则输出
            for(char c : _word){
                if(c != ' ')
                    System.out.print(c);
            }
                
            System.out.println();
        }
            
        for(TrieNode node : _root._children){//遍历树根孩子节点
            if(node != null){//回溯法遍历该树
                _word[index++] = node.getValue();
                printTree(node,_word,index);
                _word[index] = ' ';
                index--;
            }
        }
            
    }
    public static void main(String[] args){
        TrieTree _tree = new TrieTree();//创建一棵树
        TrieNode _root = new TrieNode(' ');//创建根节点
        for(String word : _words)//插入单词
            _tree.insertIntoTree(_root,word);
        char[] _word = new char[20];
        _tree.printTree(_root,_word,0);//打印树中单词
        boolean status = _tree.searchWord(_root,"thinks");//查询树中是否存在某单词
        System.out.println(status);
    }
    
    public int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(int i=0,len=nums.length;i<len;i++) {
            if(map.containsKey(target-nums[i])) {
                return new int[]{i,map.get(target-nums[i])};
            } else {
                map.put(nums[i], i);
            }
        }
        return result;
    }
}

