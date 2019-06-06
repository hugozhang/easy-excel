package me.about.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.List;

import me.about.poi.reader.XlsxReader;

public class Test2 {

    public static void main(String[] args) throws FileNotFoundException, Exception {
        
        // 1-50w.xlsx   50w-100w.xlsx   100w-150w.xlsx  150w-180w.xlsx
        List<WaybillDriverTypeToTruckType> rows = XlsxReader.fromInputStream(new FileInputStream("E:/waybill-导出司机类型结果/150w-180w.xlsx"),1, WaybillDriverTypeToTruckType.class);
        
        StringBuilder buffer = new StringBuilder();
        for(WaybillDriverTypeToTruckType m : rows) {
            buffer.append("UPDATE waybill SET vehicle_type = "+m.getDriver_type()+" WHERE waybill_id = "+m.getWaybill_id()+";\n");
        }
        
        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/waybill-导出司机类型结果/150w-180w.sql", false);
        write.write(buffer.toString());
        write.close();
    }
    
}
