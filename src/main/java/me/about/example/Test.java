package me.about.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import me.about.poi.reader.XlsxReader;

public class Test {

    public static void DepartmentOldNew() throws Exception {

        StringBuilder buffer = new StringBuilder();
        List<Certification> rows = XlsxReader.fromInputStream(new FileInputStream("E:/vendor (19).xlsx"),1,
                Certification.class);
        for (Certification m : rows) {
            if(m.getResult()!=null && m.getResult().contains("认证不通过")) continue;
            String vendorType = m.getVendorType();
            int vendorType_int = vendorType.equals("个人") ? 1
                    : (vendorType.equals("车队") ? 2 : (vendorType.equals("公司") ? 3 : 0));
            if(vendorType_int == 3) {
                buffer.append("UPDATE vendor SET vendor_type = "+vendorType_int+",juma_pin = '"+m.getResult()+"',vendor_name = '"+m.getVendorName()+"',enterprise_code = '"+m.getIdCardNo()+"'  WHERE vendor_id = "+m.getVendorId()+";");
            } else {
                buffer.append("UPDATE vendor SET vendor_type = "+vendorType_int+",juma_pin = '"+m.getResult()+"',vendor_name = '"+m.getVendorName()+"',id_card_no = '"+m.getIdCardNo()+"'  WHERE vendor_id = "+m.getVendorId()+";");
            }
            buffer.append("\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/vendor (19).sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void CustomerDepartmentMapping() throws Exception {

        StringBuilder buffer = new StringBuilder();
        List<CustomerDepartmentMapping> rows = XlsxReader.fromInputStream(new FileInputStream("E:/客户分公司190312.xlsx"),1,
                CustomerDepartmentMapping.class);
        for (CustomerDepartmentMapping m : rows) {
            buffer.append("UPDATE `waybill` SET `department_id` = " + m.getDepartment_id() + " WHERE `customer_id` = "
                    + m.getCustomer_id() + ";");
            buffer.append("\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/CustomerDepartmentMapping.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void customerDepartmentMapping() throws Exception {

        StringBuilder buffer = new StringBuilder();
        List<Lili> rows = XlsxReader.fromInputStream(new FileInputStream("E:/李力 数据迁移.xlsx"), 1,Lili.class);
        for (Lili m : rows) {

            /*
             * buffer.append("UPDATE `waybill` SET `customer_id` = " + m.getOld_tgm_customer_id() + ",`tenant_id` = 9,`area_code` = '0006020000'  WHERE `customer_id` = " +
             * m.getNew_tgm_customer_id() + " AND `tenant_id` = 19 AND waybill_id>=1371049 AND waybill_id <=1432788;"); buffer.append("\n");
             */
            buffer.append("UPDATE `waybill` SET `area_code` = '000503' WHERE `customer_id` = "
                    + m.getNew_tgm_customer_id() + " AND waybill_id>=1371049 AND waybill_id <=1432788;");
            buffer.append("\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/郑州-更新客户.sql", false);
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

    private static void transfer() throws Exception, FileNotFoundException, IOException {
        StringBuilder buffer = new StringBuilder();
        List<TransferMapping> rows = XlsxReader.fromInputStream(new FileInputStream("E:/2-26待处理.xlsx"),1,
                TransferMapping.class);
        for (TransferMapping m : rows) {
            if (m.getProject_id() != null) {
                buffer.append("UPDATE waybill SET tenant_id = 11,customer_id = " + m.getCustomer_id() + ",project_id = "
                        + m.getProject_id() + ", area_code = '" + m.getArea_code() + "', customer_manager_id = "
                        + m.getCustomer_manager_id() + " WHERE waybill_no = '" + m.getWaybillNo() + "';");
            } else {
                buffer.append("UPDATE waybill SET tenant_id = 11,customer_id = " + m.getCustomer_id() + ",area_code = '"
                        + m.getArea_code() + "', customer_manager_id = " + m.getCustomer_manager_id()
                        + " WHERE waybill_no = '" + m.getWaybillNo() + "';");
            }
            buffer.append("\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/change.sql", false);
        write.write(buffer.toString());
        write.close();
    }
}
