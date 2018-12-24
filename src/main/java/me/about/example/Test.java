package me.about.example;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

import me.about.poi.reader.XlsxReader;

public class Test {

    public static void main(String[] args) throws  Exception {
        /*Map<String,TransferMapping> customerMapping = new HashMap<String,TransferMapping>();
        List<TransferMapping> rows = XlsxReader.fromInputStream(new FileInputStream("D:/专车走科技客户名称.xlsx"), TransferMapping.class, 1);
        for(TransferMapping m : rows) {
            customerMapping.put(m.getCustomer_name(), m);
            if(StringUtils.isNotBlank(m.getOld_customer_name())) {
                customerMapping.put(m.getOld_customer_name(), m);
            }
        }*/
        StringBuilder buffer = new StringBuilder();
        List<TransferMapping> rows = XlsxReader.fromInputStream(new FileInputStream("E:/专车订单迁移科技汇总.xlsx"), TransferMapping.class, 1);
        for(TransferMapping m : rows) {
            if(m.getProject_id() != null) {
                buffer.append("UPDATE waybill SET tenant_id = 11,customer_id = "+m.getCustomer_id()+",customer_name = '" +m.getCustomer_name()+"',project_id = "+m.getProject_id()+", area_code = '"+m.getArea_code()+"', customer_manager_id = "+m.getCustomer_manager_id()+" WHERE waybill_no = '"+m.getWaybillNo()+"';");
            } else {
                buffer.append("UPDATE waybill SET tenant_id = 11,customer_id = "+m.getCustomer_id()+",customer_name = '" +m.getCustomer_name()+"',area_code = '"+m.getArea_code()+"', customer_manager_id = "+m.getCustomer_manager_id()+" WHERE waybill_no = '"+m.getWaybillNo()+"';");
            }
            buffer.append("\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("D:/change.sql", false);
        write.write(buffer.toString());
        write.close();
    }
}
