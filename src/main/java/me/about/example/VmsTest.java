package me.about.example;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import me.about.example.vms.VmsData;
import me.about.example.vms.VmsData2;
import me.about.example.vms.VmsData3;
import me.about.example.vms.VmsData4;
import me.about.poi.reader.XlsxReader;

public class VmsTest {

    public static void VmsData() throws Exception {

        // 运力数据初始化-提交（5-7) .xlsx
        // 运力数据初始化-提交（4-19).xlsx
        StringBuilder buffer = new StringBuilder();
        List<VmsData> rows = XlsxReader.fromInputStream(new FileInputStream("E:/运力数据初始化-提交（5-7) .xlsx"), 1,
                VmsData.class);
        for (VmsData m : rows) {
            buffer.append("UPDATE truck_tenant SET truck_belong_to_company = " + m.getTruckBelongToCompany()
                    + "  WHERE tenant_id = " + m.getTenantId() + " AND truck_id = " + m.getTruckId());
            buffer.append(";\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/vendor-1.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void VmsData2() throws Exception {

        StringBuilder buffer = new StringBuilder();
        List<VmsData2> rows = XlsxReader.fromInputStream(new FileInputStream("E:/运力数据初始化-提交（5-7) .xlsx"), 2,
                VmsData2.class);
        for (VmsData2 m : rows) {
            if (m.getOldVendorId() == null) {
                buffer.append(
                        "INSERT INTO vendor_truck(tenant_id,vehicle_id,truck_id,vendor_id) VALUES (" + m.getTenantId()
                                + "," + m.getVehicleId() + "," + m.getTruckId() + "," + m.getVendorId() + ")");
            } else {
                buffer.append("UPDATE vendor_truck SET vendor_id = " + m.getVendorId() + " WHERE tenant_id = "
                        + m.getTenantId() + " AND truck_id = " + m.getTruckId() + " AND vehicle_id = "
                        + m.getVehicleId());
            }
            buffer.append(";\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/vendor-2.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void VmsData5() throws Exception {

        Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();

        StringBuilder buffer = new StringBuilder();
        List<VmsData3> rows = XlsxReader.fromInputStream(new FileInputStream("E:/运力数据初始化-提交（5-7) .xlsx"), 3,
                VmsData3.class);
        for (VmsData3 m : rows) {
            if (m.getVendorId() != null) {
                if (map.containsKey(m.getVendorId())) {
                    map.get(m.getVendorId()).add(m.getDriverId());
                } else {
                    List<Integer> driverIds = new ArrayList<Integer>();
                    driverIds.add(m.getDriverId());
                    map.put(m.getVendorId(), driverIds);
                }
            }
        }

        if (!map.isEmpty()) {
            Iterator<Entry<Integer, List<Integer>>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Integer, List<Integer>> entry = it.next();
                buffer.append("DELETE FROM vendor_driver WHERE vendor_id = " + entry.getKey() + " AND driver_id NOT IN("
                        + StringUtils.join(entry.getValue(), ",") + ");");
            }
        }

        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/vendor-5.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void VmsData6() throws Exception {
        Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();

        
        StringBuilder buffer = new StringBuilder();
        List<VmsData4> rows = XlsxReader.fromInputStream(new FileInputStream("E:/运力数据初始化-提交（5-7) .xlsx"), 4,
                VmsData4.class);
        for (VmsData4 m : rows) {
            if (m.getVendorId() != null) {
                if (map.containsKey(m.getVendorId())) {
                    map.get(m.getVendorId()).add(m.getTruckId());
                } else {
                    List<Integer> truckIds = new ArrayList<Integer>();
                    truckIds.add(m.getTruckId());
                    map.put(m.getVendorId(), truckIds);
                }
            }
        }
        if (!map.isEmpty()) {
            Iterator<Entry<Integer, List<Integer>>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Integer, List<Integer>> entry = it.next();
                buffer.append("DELETE FROM vendor_truck WHERE vendor_id = " + entry.getKey() + " AND truck_id NOT IN("
                        + StringUtils.join(entry.getValue(), ",") + ");\n");
                //buffer.append("UPDATE capacity_pool SET `status` = 0 WHERE vendor_id = " + entry.getKey() + " AND truck_id NOT IN("+StringUtils.join(entry.getValue(), ",")+");\n");
                
            }
        }

        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/vendor-6.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void VmsData3() throws Exception {

        StringBuilder buffer = new StringBuilder();
        List<VmsData3> rows = XlsxReader.fromInputStream(new FileInputStream("E:/运力数据初始化-提交（5-7) .xlsx"), 3,
                VmsData3.class);
        for (VmsData3 m : rows) {
            if (m.getOldVendorId() == null) {
                if (m.getVendorId() != null) {
                    /*buffer.append("INSERT INTO vendor_driver(tenant_id,driver_id,vendor_id) VALUES (" + m.getTenantId()
                            + "," + m.getDriverId() + "," + m.getVendorId() + ")");
                    buffer.append(";\n");*/
                }
            } else {
                /*buffer.append("UPDATE vendor_driver SET vendor_id = " + m.getVendorId() + "  WHERE tenant_id = "
                        + m.getTenantId() + " AND driver_id = " + m.getDriverId());*/
/*                buffer.append("DELETE FROM vendor_driver WHERE tenant_id = "+m.getTenantId()+" AND driver_id = "+m.getDriverId()+" AND vendor_id = (SELECT driver_id FROM driver WHERE ams_driver_id = "+m.getDriverId()+")");
*/                /*buffer.append("INSERT INTO vendor_driver(tenant_id,vendor_id,driver_id) VALUES (" + m.getTenantId()
                + "," + m.getVendorId() + ",(SELECT driver_id FROM driver WHERE ams_driver_id = "+m.getDriverId()+"))");*/
                
                buffer.append("DELETE FROM vendor_driver WHERE driver_id = ((SELECT driver_id FROM driver WHERE ams_driver_id = "+m.getDriverId()+")) AND vendor_id != "+m.getVendorId()+";");
                
            }
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/vendor-3.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void VmsData4() throws Exception {

        StringBuilder buffer = new StringBuilder();
        List<VmsData4> rows = XlsxReader.fromInputStream(new FileInputStream("E:/运力数据初始化-提交（5-7) .xlsx"), 4,
                VmsData4.class);
        for (VmsData4 m : rows) {
            
            if (m.getOldVendorId() == null) {
                if (m.getVendorId() != null) {
                    buffer.append("INSERT INTO vendor_truck(tenant_id,vehicle_id,vendor_id,truck_id) VALUES ("
                            + m.getTenantId() + "," + m.getVehicleId() + "," + m.getVendorId() + "," + m.getTruckId()
                            + ")");
                }
            } else {
                buffer.append("UPDATE vendor_truck SET vendor_id = " + m.getVendorId() + " WHERE tenant_id = "
                        + m.getTenantId() + " AND truck_id = " + m.getTruckId());
            }
            
            buffer.append(";\n");
        }
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/vendor-4.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void main(String[] args) throws Exception {
        /*
         * VmsData(); VmsData2(); VmsData3(); VmsData4();
         */
        VmsData3();
        /*VmsData5();
        VmsData6();*/
    }

}
