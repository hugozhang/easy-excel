package me.about.example;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.about.poi.reader.XlsxReader;

public class ReconalicionBackTest {

    private static Map<String, Integer> map = new HashMap<String, Integer>();

    static {
        map.put("电商", 1);
        map.put("快销", 5);
        map.put("冷链", 7);
        map.put("物流/快递", 4);
        map.put("单车", 3);
        map.put("新零售", 2);
        map.put("医药", 6);
        map.put("其他", 8);
    }

    public static void main(String[] args) throws Exception {
        StringBuilder buffer = new StringBuilder();
        List<ReconalicionHis> rows = XlsxReader.fromInputStream(new FileInputStream("E:/物流产品标签 0110-1.xlsx"),1,
                ReconalicionHis.class);
        System.out.println(rows.size());
        int i = 0;
        for (ReconalicionHis m : rows) {
            if (!map.containsKey(m.getLabel())) {
                i++;
                continue;
            }
            buffer.append("UPDATE `project` SET `logistics_label` = '" + map.get(m.getLabel())
                    + "'  WHERE project_id = " + m.getProjectId() + ";");
            buffer.append("\n");
        }
        System.out.println("其它：" + i);
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/change2.sql", false);
        write.write(buffer.toString());
        write.close();
        System.out.println(rows.size());
    }

}
