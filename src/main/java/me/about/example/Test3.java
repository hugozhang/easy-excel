package me.about.example;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

import me.about.poi.reader.XlsxReader;

public class Test3 {

    public static void resume() throws Exception {
        StringBuilder buffer = new StringBuilder();
        List<DataResume> rows = XlsxReader.fromInputStream(new FileInputStream("E:/Book1.xlsx"),1,
                DataResume.class);
        for (DataResume m : rows) {
            //buffer.append("UPDATE reconcilication_for_payable_item SET settle_account_id = "+m.getSettle_account_id()+",settle_account_name = '"+m.getSettle_account_name()+"' WHERE reconcilication_item_id = "+m.getReconcilication_item_id()+";");
            buffer.append("UPDATE waybill SET vehicle_to_vendor = "+m.getSettle_account_id()+" WHERE waybill_id = "+m.getWaybill_id()+";");
           // buffer.append("\n");
        }

        System.out.println(buffer);
        FileWriter write = new FileWriter("E:/DataResume.sql", false);
        write.write(buffer.toString());
        write.close();
    }

    public static void main(String[] args) throws Exception {
        resume();
    }
}
