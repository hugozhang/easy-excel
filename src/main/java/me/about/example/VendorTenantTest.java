package me.about.example;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.about.poi.reader.XlsxReader;

public class VendorTenantTest {
    
    private static Map<String, Integer> map = new HashMap<String, Integer>();

    static {
        map.put("驹马配送", 9);
        map.put("配送", 9);
        map.put("驹马专车", 2);
        map.put("专车", 2);
        map.put("希地物流", 3);
        map.put("希地", 3);
        map.put("威盾卡车", 5);
        map.put("威盾", 5);
    }
    

    public static void main(String[] args) throws Exception {
        StringBuilder buffer = new StringBuilder();
        List<VendorTenant> rows = XlsxReader.fromInputStream(new FileInputStream("E:/承运商数据清洗2.xlsx"),1,
                VendorTenant.class);
        System.out.println(rows.size());
        for (VendorTenant m : rows) {
            buffer.append("INSERT INTO `vendor_tenant`(vendor_id,tenant_id,area_code,is_owner,create_user_id,create_time,last_update_time,last_update_user_id) ");
            buffer.append("SELECT vendor_id,19,`new_area_code`,1,create_user_id,create_time,last_update_time,last_update_user_id FROM `vendor_tenant` ");
            buffer.append("WHERE tenant_id = "+map.get(m.getTenantId())+" AND vendor_id = " + m.getVendorId() + ";");
            buffer.append("\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/change2.sql", false);
        write.write(buffer.toString());
        write.close();
    }
    
}
