package me.about.poi.writer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import me.about.poi.ExcelColumn;
import me.about.poi.ExcelDataFormatter;
import me.about.poi.User;

public class XssfWriter {
    
    private SXSSFWorkbook workbook  = new SXSSFWorkbook();
    
    private CellStyle headStyle;
    
    private DataFormat df = workbook.createDataFormat();
    
    private ExcelDataFormatter formatter = new ExcelDataFormatter();
    
    public ExcelDataFormatter getFormatter() {
        return formatter;
    }
    
    public XssfWriter() {
        this.headStyle = workbook .createCellStyle();
        this.headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        this.headStyle.setFillBackgroundColor(HSSFColor.LIGHT_BLUE.index);
        this.headStyle.setAlignment(HorizontalAlignment.CENTER);
        this.headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setColor(HSSFColor.WHITE.index);
        font.setBold(true);
        this.headStyle.setFont(font);
    }
    
    public <T> XssfWriter appendToSheet(List<T> input) throws Exception {
        return appendToSheet(null, input);
    }

    public <T> XssfWriter appendToSheet(String sheetName,List<T> input) throws Exception {
        if(input == null || input.isEmpty()) return this;
        
        Sheet sheet = null;
        if(sheetName == null || sheetName.trim().length() == 0) {
            sheet = workbook.createSheet();
        } else {
            sheet = workbook.createSheet(sheetName.trim());
        }
        
        Field[] fields = input.get(0).getClass().getDeclaredFields();
        Row row = sheet.createRow(0);
        Cell cell = null;
        int columnIndex = 0;
        ExcelColumn ann = null;
        for (Field field : fields) {
            field.setAccessible(true);
            ann = field.getAnnotation(ExcelColumn.class);
            if (ann == null) {
                continue;
            }
            sheet.setColumnWidth(columnIndex, ann.width() * 256);
            cell = row.createCell(columnIndex);
            cell.setCellStyle(headStyle);
            cell.setCellValue(ann.name());
            columnIndex++;
        }
        int rowIndex = 1;
        CellStyle cellStyle = workbook .createCellStyle();
        // 行
        for (T t : input) {
            row = sheet.createRow(rowIndex);
            columnIndex = 0;
            Object o = null;
            // 列
            for (Field field : fields) {
                field.setAccessible(true);
                ann = field.getAnnotation(ExcelColumn.class);
                if (ann == null) {
                    continue;
                }
                // 列数据
                o = field.get(t);//反射取值
                if (o == null) {
                    columnIndex++;//需要计列数*****
                    continue;
                }
                cell = row.createCell(columnIndex);
                // 处理数据类型****
                if (o instanceof Date) {
                    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                    SimpleDateFormat sdf = new SimpleDateFormat(ann.format());
                    cell.setCellValue(sdf.format((Date) field.get(t)));
                    cell.setCellStyle(cellStyle);
                } else if (o instanceof Double || o instanceof Float) {
                    cell.setCellValue((Double) field.get(t));
                } else if (o instanceof Boolean) {
                    Boolean bool = (Boolean) field.get(t);
                    Map<String, String> map = this.formatter.get(field.getName());
                    if (map == null) {
                        cell.setCellValue(bool);
                    } else {
                        cell.setCellValue(map.get(bool.toString().toLowerCase()));
                    }
                } else if (o instanceof Integer) {
                    Integer intValue = (Integer) field.get(t);
                    Map<String, String> map = this.formatter.get(field.getName());
                    if (map == null) {
                        cell.setCellValue(intValue);
                    } else {
                        cell.setCellValue(map.get(intValue.toString()));
                    }
                } else if (o instanceof BigDecimal) {
                    cellStyle.setDataFormat(df.getFormat("0.00"));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(((BigDecimal) o).doubleValue());
                } else {
                    cell.setCellValue(field.get(t).toString());
                }
                columnIndex++;
            }
            rowIndex++;
        }
        return this;
    }
    
    public  void writeToOutputStream(OutputStream out) throws Exception {
        workbook.write(out);
        workbook.close();
    }
    
    public static void main(String[] args) throws Exception {
        
        List<User> list = new ArrayList<User>();

        for (int i = 0; i < 10000; i++) {
            User u = new User();
            u.setAge(i);
            u.setUsername("A" + i);
            //u.setCompany("B"+i);
            u.setAddress("C" + i);
            u.setBirthday(new Date());
            list.add(u);
        }
        FileOutputStream out = new FileOutputStream("D:/test.xlsx");
        new XssfWriter().appendToSheet(list).appendToSheet("测试",list).writeToOutputStream(out);
        
    }
    
}
