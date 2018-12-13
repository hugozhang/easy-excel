package me.about.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import me.about.poi.reader.XlsxReader;

public class Test2 {

    public static void main(String[] args) throws FileNotFoundException, Exception {
        
        List<CustomerMapping> rows = XlsxReader.fromInputStream(new FileInputStream("D:/撤销老对账统计模板--9.10.xlsx"), CustomerMapping.class, 1);
        
        StringBuilder buffer = new StringBuilder();
        for(CustomerMapping m : rows) {
            
            String start = new SimpleDateFormat("yyyy-MM-dd").format(m.getStart()) + " 00:00:00";
            
            String end = new SimpleDateFormat("yyyy-MM-dd").format(m.getEnd()) + " 23:59:59";
            
            /*buffer.append(" SELECT COUNT(1) FROM `waybill` ");
            buffer.append("  WHERE `customer_name` = '"+m.getCustomerName()+"' AND  `plan_delivery_time` >= '"+start+"' AND `plan_delivery_time` <= '"+end+"' AND `tenant_id` = 9 AND `reconciliation_status` = 2 AND `is_delete` = false;");
            buffer.append(" \r\n ");*/
            
            buffer.append(" UPDATE `waybill` SET `is_submit_to_erp` = 0,`reconciliation_status` = 1  ");
            buffer.append("  WHERE `customer_name` = '"+m.getCustomerName()+"' AND  `plan_delivery_time` >= '"+start+"' AND `plan_delivery_time` <= '"+end+"' AND `tenant_id` = 9 AND `reconciliation_status` = 2 AND `is_delete` = false;");
            buffer.append(" \r\n ");
        }
        
        System.out.println(buffer);
        FileWriter write = new FileWriter("D:/change.sql", false);
        write.write(buffer.toString());
        write.close();
        
    }
    
}
