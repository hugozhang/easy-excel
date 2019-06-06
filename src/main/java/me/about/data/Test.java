package me.about.data;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

import me.about.poi.reader.XlsxReader;

public class Test {


    public static void DepartmentOldNew() throws Exception {

        StringBuilder buffer = new StringBuilder();
        List<Yingfu> rows = XlsxReader.fromInputStream(new FileInputStream("E:/6、检查子公司.xlsx"),1, Yingfu.class);
        for (Yingfu m : rows) {
            buffer.append("UPDATE `reconcilication_for_payable` SET `department_id` = "+m.getDepartment_id()+" WHERE `reconcilication_no` = '"+m.getAp_no()+"';");
            buffer.append("\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/6、检查子公司.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void main(String[] args) throws Exception {
        /*
         * Map<String,TransferMapping> customerMapping = new HashMap<String,TransferMapping>(); List<TransferMapping> rows = XlsxReader.fromInputStream(new
         * FileInputStream("D:/专车走科技客户名称.xlsx"), TransferMapping.class, 1); for(TransferMapping m : rows) { customerMapping.put(m.getCustomer_name(), m);
         * if(StringUtils.isNotBlank(m.getOld_customer_name())) { customerMapping.put(m.getOld_customer_name(), m); } }
         */
        DepartmentOldNew();
    }

}
