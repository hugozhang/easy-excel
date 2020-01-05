package me.about.poi.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.about.poi.test.User;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import me.about.poi.ExcelColumn;
import me.about.poi.ExcelDataFormatter;
import me.about.poi.reader.XLSXReader;

/**
 * 
 * @ClassName: XLSXWriter
 * @Description: Content Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml
 */
public class XLSXWriter {

    public static XLSXWriter builder() {
        return new XLSXWriter();
    }

    public <T> void toStream(List<T> data, OutputStream out) throws Exception {
        ExcelDataFormatter edf = new ExcelDataFormatter();
        try(Workbook workbook = writeToWorkBook(data, edf)) {
            workbook.write(out);
        }
    }

    public <T> Workbook writeToWorkBook(List<T> input, ExcelDataFormatter edf) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
//        workbook.setCompressTempFiles(true);
        if (input == null || input.isEmpty()) return workbook;
        Sheet sheet = workbook.createSheet();

        //head style 部分
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        headStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        font.setBold(true);
        headStyle.setFont(font);

        //head 部分
        CreationHelper createHelper = workbook.getCreationHelper();
        Field[] fields = input.get(0).getClass().getDeclaredFields();// 取类字段集合

        Row row = sheet.createRow(0);
        int headColumnIndex = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            if (ann == null) {
                continue;
            }
            sheet.setColumnWidth(headColumnIndex, ann.width() * 256);
            Cell cell = row.createCell(headColumnIndex);
            cell.setCellStyle(headStyle);
            cell.setCellValue(ann.name());
            headColumnIndex++;
        }

        //data 部分
        int rowIndex = 1;
        // 行
        for (T t : input) {
            row = sheet.createRow(rowIndex);
            int columnIndex = 0;
            // 列
            for (Field field : fields) {
                field.setAccessible(true);
                ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
                if (ann == null) {
                    continue;
                }
                // 列数据
                Object o = field.get(t);// 反射取值
                if (o == null) {
                    columnIndex++;// *****跳到下一列
                    continue;
                }
                Cell cell = row.createCell(columnIndex);
                // 处理数据类型
                if (o instanceof Date) {
                    CellStyle cs = workbook.createCellStyle();
                    cs.setDataFormat(createHelper.createDataFormat().getFormat(ann == null ? "yyyy-MM-dd HH:mm:ss" : ann.format()));
                    cell.setCellStyle(cs);
                    cell.setCellValue((Date) o);
                } else if (o instanceof Double || o instanceof Float) {
                    CellStyle cs = workbook.createCellStyle();
                    cs.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    cell.setCellStyle(cs);
                    cell.setCellValue((Double) o);
                } else if (o instanceof Boolean) {
                    Boolean bool = (Boolean) o;
                    if (edf == null) {
                        cell.setCellValue(bool);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            cell.setCellValue(bool);
                        } else {
                            cell.setCellValue(map.get(bool.toString().toLowerCase()));
                        }
                    }
                } else if (o instanceof Integer) {
                    Integer intValue = (Integer) o;
                    if (edf == null) {
                        cell.setCellValue(intValue);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            cell.setCellValue(intValue);
                        } else {
                            cell.setCellValue(map.get(intValue.toString()));
                        }
                    }
                } else if (o instanceof BigDecimal) {
                    CellStyle cs = workbook.createCellStyle();
                    cs.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    cell.setCellStyle(cs);
                    cell.setCellValue(((BigDecimal) o).doubleValue());
                } else {
                    cell.setCellValue(o.toString());
                }
                columnIndex++;
            }
            rowIndex++;
        }
        return workbook;
    }

    public static void main(String[] args) throws Exception {
        List<User> list = new ArrayList();

        for (int i = 0; i < 1000; i++) {
            User u = new User();
            u.setAge(i);
            u.setUsername("A" + i);
            u.setCompany("B"+i);
            u.setAddress("C" + i);
            u.setBirthday(new Date());
            u.setSalary(new BigDecimal(23.45));
            list.add(u);
        }
        Date s = new Date();
        System.out.println(s);
        FileOutputStream out = new FileOutputStream("test.xlsx");
        XLSXWriter.builder().toStream(list, out);
        Date e = new Date();
        System.out.println(e);
        System.out.println("耗时:" + (e.getTime() - s.getTime()) / 1000);
        out.close();
        
        
//        List<User> users = XLSXReader.fromInputStream(new FileInputStream("test.xlsx"), 1, User.class);
//        for(User u : users) {
//            System.out.println(u);
//        }

        File file = new File("/Users/hugozxh/workspace/easy-excel/test.xlsx");
        System.out.println(file.exists());
        List<User> users1 = XLSXReader.builder().open(new FileInputStream("test.xlsx")).parseArray(User.class);
        System.out.println(users1);

    }

}
