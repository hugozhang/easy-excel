package me.about.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

public class CsvReader {

    private static final String SAMPLE_CSV_FILE_PATH = "E:/业务区域映射关系2.0.csv";

    public static void main(String[] args) throws IOException {

        //VendorAreaCode();
        
        Waybill19YearAreaCode();
    }
    
    private static void Waybill19YearAreaCode() throws IOException {
        StringBuilder buffer = new StringBuilder();
        Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH), Charset.forName("UTF-8"));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            Integer tenantId = null;
            String oldTenantName = csvRecord.get(0);
            if (oldTenantName.equals("驹马专车")) {
                tenantId = 2;
            } else if (oldTenantName.equals("希地物流")) {
                tenantId = 3;
            } else if (oldTenantName.equals("驹马配送")) {
                tenantId = 9;
            } else if (oldTenantName.equals("雏鹰")) {
                tenantId = 17;
            }

            String oldAreaCode = csvRecord.get(2);

            String newAreaCode = csvRecord.get(11);
            if (StringUtils.isBlank(oldAreaCode) || StringUtils.isBlank(newAreaCode)) {
                System.out.println("area code is valid.");
            }

            buffer.append("UPDATE `truck_customer` SET `area_code` = '" + newAreaCode + "'  WHERE `tenant_id` = "
                    + tenantId + " AND `area_code` = '" + oldAreaCode + "';");
            buffer.append("\n");

        }
        System.out.println(buffer.toString());
        
        FileWriter write = new FileWriter("E:/change2.sql", false);
        write.write(buffer.toString());
        write.close();
        System.out.println(buffer.toString());
        csvParser.close();
    }

    private static void VendorAreaCode() throws IOException {
        StringBuilder buffer = new StringBuilder();
        Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH), Charset.forName("UTF-8"));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            Integer tenantId = null;
            String oldTenantName = csvRecord.get(0);
            if (oldTenantName.equals("驹马专车")) {
                tenantId = 2;
            } else if (oldTenantName.equals("希地物流")) {
                tenantId = 3;
            } else if (oldTenantName.equals("驹马配送")) {
                tenantId = 9;
            } else if (oldTenantName.equals("雏鹰")) {
                tenantId = 17;
            }

            String oldAreaCode = csvRecord.get(2);

            String newAreaCode = csvRecord.get(11);
            if (StringUtils.isBlank(oldAreaCode) || StringUtils.isBlank(newAreaCode)) {
                System.out.println("area code is valid.");
            }

            buffer.append("UPDATE `vendor_tenant` SET `new_area_code` = '" + newAreaCode + "'  WHERE `tenant_id` = "
                    + tenantId + " AND `area_code` = '" + oldAreaCode + "';");
            buffer.append("\n");

        }
        FileWriter write = new FileWriter("E:/change2.sql", false);
        write.write(buffer.toString());
        write.close();
        System.out.println(buffer.toString());
        csvParser.close();
    }
}
