package me.about.example;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

import me.about.poi.reader.XlsxReader;

public class Test3 {

    public static void main(String[] args) throws  Exception {
       
        StringBuilder buffer = new StringBuilder();
        List<MergeProject> rows = XlsxReader.fromInputStream(new FileInputStream("E:/Book2.xlsx"), MergeProject.class, 1);
        for(MergeProject m : rows) {
            /*buffer.append("UPDATE `waybill` SET `project_id` = "+m.getProjectId()+",`project_name` = '"+m.getProjectName()+"' WHERE `tenant_id` = 9 AND  `customer_id` = "+m.getCustomerId()+" AND `reconciliation_status`  = 1 AND  `is_delete` = false;");
            buffer.append("\n");*/
            buffer.append("UPDATE `project` SET `is_enable` = false WHERE `customer_id` ="+m.getCustomerId()+"  AND  `project_id` != "+m.getProjectId()+";");
            buffer.append("\n");
        }
        
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/change.sql", false);
        write.write(buffer.toString());
        write.close();
    }
}
