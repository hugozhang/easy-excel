package me.about.poi;

import java.util.HashMap;
import java.util.Map;

/*
 * 举例:<br>
 * 数据导出， {lock,{0:正常，1:锁定}}<br>
 * 数据导入，{lock,{正常:0，锁定:1}}
 * ExcelDataFormatter edf = new ExcelDataFormatter();
 * Map<String, String> map = new HashMap<String, String>();
 * map.put("真", "true");
 * map.put("假", "false");
 * edf.set("locked", map);
 */
public class ExcelDataFormatter {
    
    private Map<String, Map<String, String>> formatter = new HashMap<String, Map<String, String>>();

    public void set(String key, Map<String, String> map) {
        formatter.put(key, map);
    }

    public Map<String, String> get(String key) {
        return formatter.get(key);
    }
}
