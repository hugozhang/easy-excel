package me.about.poi.reader;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import me.about.poi.Creators;
import me.about.poi.ExcelColumn;
import me.about.poi.Mapping;

public class XlsxReader {

    enum CellDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,DATE,NULL
    }

    public static <T> List<T> fromInputStream(InputStream in, Class<T> clazz) throws Exception {
        return fromInputStream(in, clazz, 1);
    }

    public static <T> List<T> fromInputStream(InputStream in, Class<T> clazz, int headerRowIndex) throws Exception {
        List<T> rows = new ArrayList<T>();
        OPCPackage pkg = OPCPackage.open(in);
        XSSFReader r = new XSSFReader(pkg);
        StylesTable stylesTable = r.getStylesTable();
        SharedStringsTable sharedStringsTable = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(stylesTable, sharedStringsTable, rows, clazz, headerRowIndex);
        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
            break;
        }
        return rows;
    }

    public static <T> XMLReader fetchSheetParser(StylesTable stylesTable, SharedStringsTable strings, List<T> rows,
            Class<T> clazz, int headerRowIndex) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        ContentHandler handler = new SheetHandler<T>(stylesTable, strings, rows, clazz, headerRowIndex);
        parser.setContentHandler(handler);
        return parser;
    }

    public static class SheetHandler<T> extends DefaultHandler {

        /** Table with styles */
        private StylesTable stylesTable;
        /** Table with unique strings */
        private SharedStringsTable sharedStringsTable;

        private int headerRowIndex;//
        private Class<T> clazz;
        private T currentRow;
        private List<T> rows;
        private String columnName;
        private Map<String, Field> fieldMapping = new HashMap<String, Field>();
        private Map<String, String> titleMapping = new HashMap<String, String>();
        private int rowNumber;// 行
        private int cellNumber;//列

        // Set when V start element is seen
        private boolean vIsOpen;
        // Set when cell start element is seen;
        // used when cell close element is seen.
        private CellDataType nextDataType;
        // Used to format numeric cell values.
        private short formatIndex;
        private String formatString;
        private DataFormatter formatter;
        // Gathers characters as they are seen.
        private StringBuffer value;

        private SheetHandler(StylesTable stylesTable, SharedStringsTable sharedStringsTable, List<T> rows,
                Class<T> clazz, int headerRowIndex) {
            this.stylesTable = stylesTable;
            this.sharedStringsTable = sharedStringsTable;
            this.clazz = clazz;
            this.rows = rows;
            this.headerRowIndex = headerRowIndex;

            this.value = new StringBuffer();
            this.nextDataType = CellDataType.NUMBER;
            this.formatter = new DataFormatter();

            ExcelColumn ann = null;
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ann = field.getAnnotation(ExcelColumn.class);
                if (ann != null) {
                    fieldMapping.put(ann.name(), field);
                }
            }
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

            if ("inlineStr".equals(name) || "v".equals(name)) {
                vIsOpen = true;
                // Clear contents cache
                value.setLength(0);
            }
            // c => cell
            else if ("c".equals(name)) {
                cellNumber ++ ;
                String r = attributes.getValue("r");
                StringBuffer column = new StringBuffer();
                for (int c = 0, len = r.length(); c < len; ++c) {
                    if (Character.isLetter(r.charAt(c))) {
                        column.append(r.charAt(c));
                    }
                }
                this.columnName = column.toString();
                // Set up defaults.
                this.nextDataType = CellDataType.NUMBER;
                this.formatIndex = -1;
                this.formatString = null;
                String cellType = attributes.getValue("t");
                String cellStyleStr = attributes.getValue("s");
                if ("b".equals(cellType)) nextDataType = CellDataType.BOOL;
                else if ("e".equals(cellType)) nextDataType = CellDataType.ERROR;
                else if ("inlineStr".equals(cellType)) nextDataType = CellDataType.INLINESTR;
                else if ("s".equals(cellType)) nextDataType = CellDataType.SSTINDEX;
                else if ("str".equals(cellType)) nextDataType = CellDataType.FORMULA;
                else if (cellStyleStr != null) {
                    /*
                     * It's a number, but possibly has a style and/or special format. Nick Burch said to use org.apache.poi.ss.usermodel.BuiltinFormats, and I see javadoc for that
                     * at apache.org, but it's not in the POI 3.5 Beta 5 jars. Scheduled to appear in 3.5 beta 6.
                     */
                    int styleIndex = Integer.parseInt(cellStyleStr);
                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatString == null)
                        this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                }
            } else if ("row".equals(name)) {
                rowNumber++;
                cellNumber = 0;
                if (rowNumber > this.headerRowIndex) {
                    currentRow = Creators.of(this.clazz).get();
                }
            }
        }

        public void endElement(String uri, String localName, String name) throws SAXException {

            Object val = null;
            // v => contents of a cell
            if ("v".equals(name)) {

                switch (nextDataType) {
                case BOOL:
                    char first = value.charAt(0);
                    val = first == '0' ? false : true;
                    break;
                case ERROR:
                    val = "\"ERROR:" + value.toString() + '"';
                    break;
                case FORMULA:
                    // A formula could result in a string value,
                    // so always add double-quote characters.
                    val = value.toString().trim();
                    break;
                case INLINESTR:
                    // TODO: have seen an example of this, so it's untested.
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    val = rtsi.toString().trim();
                    break;
                case SSTINDEX:
                    String sstIndex = value.toString().trim();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                        val = rtss.toString();
                    } catch (NumberFormatException ex) {
                    }
                    break;
                case NUMBER:
                    String n = value.toString().trim();
                    if (DateUtil.isADateFormat(this.formatIndex, this.formatString)) {
                        val = DateUtil.getJavaDate(Double.parseDouble(n));
                    } else if (this.formatString != null) {
                        val = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex,
                                this.formatString);
                    } else {
                        val = n;
                    }
                    break;
                default:
                    val = "(TODO: Unexpected type: " + nextDataType + ")";
                    break;
                }
                if (rowNumber == this.headerRowIndex) {
                    titleMapping.put(this.columnName, val == null ? null : String.valueOf(val).trim());
                }
                // Process the value contents as required.
                // Do now, as characters() may be called more than once
                if (rowNumber > this.headerRowIndex) {
                    checkXlsxTitle(fieldMapping, titleMapping);
                    Field field = fieldMapping.get(titleMapping.get(this.columnName));
                    if (field != null) {
                        try {
                            field.setAccessible(true);
                            if (int.class.equals(field.getType()) || Integer.class.equals(field.getType())) {
                                checkValueType(field, titleMapping.get(this.columnName), val.toString());
                                field.set(currentRow, NumberUtils.toInt(val.toString()));
                            } else if (long.class.equals(field.getType()) || Long.class.equals(field.getType())) {
                                checkValueType(field, titleMapping.get(this.columnName), val.toString());
                                field.set(currentRow, NumberUtils.toLong(val.toString()));
                            } else if (float.class.equals(field.getType()) || Float.class.equals(field.getType())) {
                                checkValueType(field, titleMapping.get(this.columnName), val.toString());
                                field.set(currentRow, NumberUtils.toFloat(val.toString()));
                            } else if (double.class.equals(field.getType()) || Double.class.equals(field.getType())) {
                                checkValueType(field, titleMapping.get(this.columnName), val.toString());
                                field.set(currentRow, NumberUtils.toDouble(val.toString()));
                            } else if (boolean.class.equals(field.getType()) || Boolean.class.equals(field.getType())) {
                                field.set(currentRow, val);
                            } else if (java.util.Date.class.equals(field.getType())) {
                                field.set(currentRow, val);
                            } else if (BigDecimal.class.equals(field.getType())) {
                                checkValueType(field, titleMapping.get(this.columnName), val.toString());
                                field.set(currentRow, BigDecimal.valueOf(Double.parseDouble(val.toString())));
                            } else {
                                field.set(currentRow, val.toString().trim());
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            System.out.println("行号:"+rowNumber+",值:"+val);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if ("row".equals(name)) {
                if (rowNumber > this.headerRowIndex) {
                    this.rows.add(currentRow);
                }
            }
        }

        private void checkValueType(Field field, String columnName, String value) {
            if (!NumberUtils.isCreatable(value)) {
                throw new RuntimeException("(行："+rowNumber+"，列："+cellNumber+")列名为'" + columnName + "'，值" + value + "，不能转为数值类型");
            }
        }


        private void checkXlsxTitle(Map<String, Field> fieldMapping, Map<String, String> titleMapping) {
            if (titleMapping == null || titleMapping.isEmpty()) {
                throw new RuntimeException("Excel文件标题栏错误，请检查下");
            }
            if (fieldMapping == null || fieldMapping.isEmpty()) {
                throw new RuntimeException("没有读到需要的数据列，请检查下");
            }
            Iterator<Map.Entry<String, Field>> it = fieldMapping.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Field> entry = it.next();
                if (!titleMapping.containsValue(entry.getKey())) {
                    throw new RuntimeException("Excel模板列名为'" + entry.getKey() + "'不存在，请检查下");
                }
            }
        }

        /**
         * Captures characters only if a suitable element is open. Originally was just "v"; extended for inlineStr also.
         */
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (vIsOpen) value.append(ch, start, length);
        }

    }

    public static void main(String[] args) throws Exception {
        List<Mapping> rows = XlsxReader.fromInputStream(new FileInputStream("D:/change.xlsx"), Mapping.class, 1);
        StringBuilder buffer = new StringBuilder();
        for (Mapping mapping : rows) {
            if(StringUtils.isBlank(mapping.getOld_area_code()) || StringUtils.isBlank(mapping.getNew_area_code())) continue;
           
            buffer.append("UPDATE `waybill` SET `area_code` = REPLACE(`area_code`,'" + mapping.getOld_area_code()+ "','" + mapping.getNew_area_code() + "') WHERE `area_code` = '" + mapping.getOld_area_code()+ "' AND tenant_id = 9;\r\n");
            buffer.append("UPDATE `truck` SET `area_code` = REPLACE(`area_code`,'" + mapping.getOld_area_code() + "','"+ mapping.getNew_area_code() + "') WHERE `area_code` = '" + mapping.getOld_area_code()+ "' AND tenant_id = 9;\r\n");
            buffer.append("UPDATE `driver` SET `area_code` = REPLACE(`area_code`,'" + mapping.getOld_area_code() + "','"+ mapping.getNew_area_code() + "') WHERE `area_code` = '" + mapping.getOld_area_code()+ "' AND tenant_id = 9;\r\n");
            buffer.append("UPDATE `customer_info` SET `area_code` = REPLACE(`area_code`,'" + mapping.getOld_area_code()+ "','" + mapping.getNew_area_code() + "') WHERE `area_code` = '" + mapping.getOld_area_code()+ "' AND tenant_id = 9;\r\n");
            buffer.append("UPDATE `truck_customer` SET `area_code` = REPLACE(`area_code`,'" + mapping.getOld_area_code()+ "','" + mapping.getNew_area_code() + "') WHERE `area_code` = '" + mapping.getOld_area_code()+ "' AND tenant_id = 9;\r\n");
        
            //专车迁移
            /*
             * if(mapping.getCrm_customer_isdelete() == null ||mapping.getCrm_customer_isdelete().trim().length() == 0 ||mapping.getCrm_cutomer_id() == null
             * ||mapping.getCrm_cutomer_id().trim().length() == 0 ||mapping.getCrm_isdelete() == null ||mapping.getCrm_isdelete().trim().length() == 0
             * ||mapping.getTgm_customer_id() == null ||mapping.getTgm_customer_id().trim().length() == 0 ||mapping.getTgm_isdelete() == null
             * ||mapping.getTgm_isdelete().trim().length() == 0) { continue; }
             */
        }
        System.out.println(buffer);

        FileWriter write = new FileWriter("D:/change.sql", false);
        write.write(buffer.toString());
        write.close();
        /*
         * List<Mapping> rows = new ArrayList<Mapping>(); Mapping maping = new Mapping(); maping.setOldAreacode("000303000500"); maping.setNewAreacode("000303000300");
         * rows.add(maping); StringBuilder buffer = new StringBuilder(); for(Mapping mapping : rows) { buffer.append(
         * "UPDATE `waybill` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); buffer.append("\r\n"); buffer.append(
         * "UPDATE `truck` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); buffer.append("\r\n"); buffer.append(
         * "UPDATE `driver` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); buffer.append("\r\n"); buffer.append(
         * "UPDATE `customer_info` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); buffer.append("\r\n"); buffer.append(
         * "UPDATE `truck_customer` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); buffer.append("\r\n"); buffer.append(
         * "UPDATE `config_param_option` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); buffer.append("\r\n"); buffer.append(
         * "UPDATE `important_notice` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); buffer.append("\r\n"); // buffer.append(
         * "UPDATE `phase_freight_list` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); // buffer.append("\r\n"); buffer.append(
         * "UPDATE `truck_fleet` SET `tenant_id` = 9,`tenant_code` = '000000004',`area_code` = '"+mapping.getNewAreacode().substring(0, j-1)+"'  WHERE `area_code`= '"
         * +mapping.getOldAreacode().substring(0, i-1)+"' AND `tenant_id` = 2;"); buffer.append("\r\n"); buffer.append("\r\n"); buffer.append("\r\n"); }
         */

    }
}
