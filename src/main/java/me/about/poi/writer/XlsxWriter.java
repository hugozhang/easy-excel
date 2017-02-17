package me.about.poi.writer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import me.about.poi.User;

public class XlsxWriter {

    public static <T> void toOutputStream(List<T> data, OutputStream out) throws Exception {
        ExcelDataFormatter edf = new ExcelDataFormatter();
        Workbook wb = writeToWorkBook(data, edf);
        wb.write(out);
    }

    public static <T> Workbook writeToWorkBook(List<T> data, ExcelDataFormatter edf) throws Exception {
        Workbook wb = new SXSSFWorkbook();
        if (data == null || data.size() == 0) return wb;
        Sheet sheet = wb.createSheet();
        CreationHelper createHelper = wb.getCreationHelper();
        Field[] fields = data.get(0).getClass().getDeclaredFields();
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        titleStyle.setFillBackgroundColor(HSSFColor.LIGHT_BLUE.index);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = wb.createFont();
        font.setColor(HSSFColor.WHITE.index);
        font.setBold(true);
        titleStyle.setFont(font);

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
            cell.setCellStyle(titleStyle);
            cell.setCellValue(ann.name());
            columnIndex++;
        }

        int rowIndex = 1;
        CellStyle cs = wb.createCellStyle();
        cs.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        // 行
        for (T t : data) {
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
                cell = row.createCell(columnIndex);
                o = field.get(t);
                if (o == null) {
                    columnIndex++;// *****跳到下一列
                    continue;
                }
                // 处理表数据类型
                if (o instanceof Date) {
                    cell.setCellStyle(cs);
                    cell.setCellValue((Date) field.get(t));
                } else if (o instanceof Double || o instanceof Float) {
                    cell.setCellValue((Double) field.get(t));
                } else if (o instanceof Boolean) {
                    Boolean bool = (Boolean) field.get(t);
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
                    Integer intValue = (Integer) field.get(t);
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
                    cs.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));
                    cell.setCellStyle(cs);
                    cell.setCellValue(((BigDecimal) o).doubleValue());
                } else {
                    cell.setCellValue(field.get(t).toString());
                }
                columnIndex++;
            }
            rowIndex++;
        }
        return wb;
    }

    public static void main(String[] args) throws Exception {
        List<User> list = new ArrayList<User>();

        for (int i = 0; i < 1000000; i++) {
            User u = new User();
            u.setAge(i);
            u.setUsername("A" + i);
            u.setCompany("B"+i);
            u.setAddress("C" + i);
            list.add(u);
        }
        Date s = new Date();
        System.out.println(s);
        FileOutputStream out = new FileOutputStream("D:/test.xlsx");
        XlsxWriter.toOutputStream(list, out);
        Date e = new Date();
        System.out.println(e);
        System.out.println("耗时:" + (e.getTime() - s.getTime()) / 1000);
        out.close();
    }

}
