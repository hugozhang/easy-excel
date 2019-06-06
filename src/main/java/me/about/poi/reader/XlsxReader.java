package me.about.poi.reader;

import static org.apache.poi.xssf.usermodel.XSSFRelation.NS_SPREADSHEETML;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.storage.HeaderBlockConstants;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
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

/**
 * 
 * @ClassName: XlsxReader
 * @Description: Content Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml
 * @author: Administrator
 * @date: 2019年4月23日 下午8:02:32
 *
 * @Copyright: 2019 www.jumapeisong.com Inc. All rights reserved.
 */
public class XlsxReader {

    enum CellDataType {
        BOOLEAN, ERROR, FORMULA, INLINE_STRING, SST_STRING, NUMBER, DATE, NULL
    }

    public static void verifyZipHeader(InputStream stream) throws IOException {
        byte[] data = new byte[8];
        IOUtils.readFully(stream, data);

        long signature = LittleEndian.getLong(data);
        if (signature == HeaderBlockConstants._signature) {
            throw new RuntimeException("不支持老版本Excel，请另存为xlsx类型");
        }
    }

    public static <T> List<T> fromInputStream(InputStream in, Integer sheetIndex, int headerRowIndex, Class<T> clazz)
            throws Exception {
        verifyZipHeader(in);
        List<T> rows = new ArrayList<T>();
        OPCPackage pkg = OPCPackage.open(in);
        XSSFReader r = new XSSFReader(pkg);

        StylesTable stylesTable = r.getStylesTable();
        SharedStringsTable sharedStringsTable = r.getSharedStringsTable();
        XMLReader parser = XMLReaderFactory.createXMLReader();
        ContentHandler handler = new SheetHandler<T>(stylesTable, sharedStringsTable, sheetIndex, headerRowIndex, rows,
                clazz);
        parser.setContentHandler(handler);

        InputStream inputStream = r.getSheet("rId" + (sheetIndex + 1));
        InputSource sheetSource = new InputSource(inputStream);
        parser.parse(sheetSource);
        inputStream.close();

        return rows;
    }

    public static <T> List<T> fromInputStream(InputStream in, int headerRowIndex, Class<T> clazz) throws Exception {

        List<T> rows = new ArrayList<T>();
        OPCPackage pkg = OPCPackage.open(in);
        XSSFReader r = new XSSFReader(pkg);

        StylesTable stylesTable = r.getStylesTable();
        SharedStringsTable sharedStringsTable = r.getSharedStringsTable();
        XMLReader parser = XMLReaderFactory.createXMLReader();

        int sheetIndex = 0;
        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            sheetIndex++;
            ContentHandler handler = new SheetHandler<T>(stylesTable, sharedStringsTable, sheetIndex, headerRowIndex,
                    rows, clazz);
            parser.setContentHandler(handler);
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
        return rows;
    }

    public static class SheetHandler<T> extends DefaultHandler {

        /** Table with styles */
        private StylesTable stylesTable;
        /** Table with unique strings */
        private SharedStringsTable sharedStringsTable;

        private int sheetIndex;
        private int headerRowIndex;
        private Class<T> clazz;
        private T currentRow;
        private List<T> rows;
        private String columnName;
        private Map<String, Field> fieldMapping = new HashMap<String, Field>();
        private Map<String, String> titleMapping = new HashMap<String, String>();
        private int rowNumber;// row
        private int cellNumber;// column

        // row is empty?
        private boolean rIsEmpty = true;
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

        private SheetHandler(StylesTable stylesTable, SharedStringsTable sharedStringsTable, int sheetIndex,
                int headerRowIndex, List<T> rows, Class<T> clazz) {
            this.stylesTable = stylesTable;
            this.sharedStringsTable = sharedStringsTable;
            this.sheetIndex = sheetIndex;
            this.clazz = clazz;
            this.rows = rows;
            this.headerRowIndex = headerRowIndex;

            this.value = new StringBuffer();
            this.nextDataType = CellDataType.NUMBER;
            this.formatter = new DataFormatter();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
                if (ann != null) {
                    fieldMapping.put(ann.name(), field);
                }
            }
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {

            if (isTextTag(localName)) {
                vIsOpen = true;
                // Clear contents cache
                value.setLength(0);
            }
            // c => cell
            else if ("c".equals(localName)) {
                cellNumber++;
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
                if ("b".equals(cellType)) nextDataType = CellDataType.BOOLEAN;
                else if ("e".equals(cellType)) nextDataType = CellDataType.ERROR;
                else if ("inlineStr".equals(cellType)) nextDataType = CellDataType.INLINE_STRING;
                else if ("s".equals(cellType)) nextDataType = CellDataType.SST_STRING;
                else if ("str".equals(cellType)) nextDataType = CellDataType.FORMULA;
                else {
                    // Number, but almost certainly with a special style or format
                    XSSFCellStyle style = null;
                    if (stylesTable != null) {
                        if (cellStyleStr != null) {
                            int styleIndex = Integer.parseInt(cellStyleStr);
                            style = stylesTable.getStyleAt(styleIndex);
                        } else if (stylesTable.getNumCellStyles() > 0) {
                            style = stylesTable.getStyleAt(0);
                        }
                    }
                    if (style != null) {
                        this.formatIndex = style.getDataFormat();
                        this.formatString = style.getDataFormatString();
                        if (this.formatString == null)
                            this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                    }
                }
            } else if ("row".equals(localName)) {
                rowNumber++;
                cellNumber = 0;
                rIsEmpty = true;
                if (rowNumber > this.headerRowIndex) {
                    currentRow = Creators.of(this.clazz).get();
                }
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {

            if (uri != null && !uri.equals(NS_SPREADSHEETML)) {
                return;
            }
            String thisStr = null;
            // v => contents of a cell
            if (isTextTag(localName)) {
                vIsOpen = false;
                switch (nextDataType) {
                case BOOLEAN:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;
                case ERROR:
                    thisStr = "ERROR:" + value.toString();
                    break;
                case FORMULA:
                    String fv = value.toString();
                    if (this.formatString != null) {
                        try {
                            // Try to use the value as a formattable number
                            double d = Double.parseDouble(fv);
                            thisStr = formatter.formatRawCellContents(d, this.formatIndex, this.formatString);
                        } catch (NumberFormatException e) {
                            // Formula is a String result not a Numeric one
                            thisStr = fv;
                        }
                    } else {
                        // No formating applied, just do raw value in all cases
                        thisStr = fv;
                    }
                    break;
                case INLINE_STRING:
                    // TODO: Can these ever have formatting on them?
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    thisStr = rtsi.toString();
                    break;
                case SST_STRING:
                    String sstIndex = value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                        thisStr = rtss.toString();
                    } catch (NumberFormatException ex) {

                    }
                    break;
                case NUMBER:
                    String n = value.toString().trim();
                    if (DateUtil.isADateFormat(this.formatIndex, this.formatString)) {
                        // thisStr = DateUtil.getJavaDate(Double.parseDouble(n));
                    } else if (this.formatString != null) {
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex,
                                this.formatString);
                    } else {
                        thisStr = n;
                    }
                    break;
                default:
                    thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                    break;
                }
                if (rowNumber == this.headerRowIndex) {
                    titleMapping.put(this.columnName, thisStr);
                }
                // Process the value contents as required.
                // Do now, as characters() may be called more than once
                if (rowNumber > this.headerRowIndex) {
                    checkXlsxTitle(fieldMapping, titleMapping);
                    Field field = fieldMapping.get(titleMapping.get(this.columnName));
                    if (field == null) return;
                    ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
                    if (ann == null) return;
                    Class<?> clz = field.getType();
                    try {
                        field.setAccessible(true);
                        if (int.class.equals(clz) || Integer.class.equals(clz)) {
                            checkValueType(field, titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, NumberUtils.toInt(thisStr.equals("") ? null : thisStr));
                        } else if (long.class.equals(clz) || Long.class.equals(clz)) {
                            checkValueType(field, titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, NumberUtils.toLong(thisStr));
                        } else if (float.class.equals(clz) || Float.class.equals(clz)) {
                            checkValueType(field, titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, NumberUtils.toFloat(thisStr));
                        } else if (double.class.equals(clz) || Double.class.equals(clz)) {
                            checkValueType(field, titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, NumberUtils.toDouble(thisStr));
                        } else if (boolean.class.equals(clz) || Boolean.class.equals(clz)) {
                            field.set(currentRow, thisStr);
                        } else if (java.util.Date.class.equals(clz)) {
                            String format = ann.format() == null ? "yyyy-MM-dd HH:mm:ss" : ann.format();
                            Date d = new SimpleDateFormat(format).parse(thisStr);
                            field.set(currentRow, d);
                        } else if (BigDecimal.class.equals(clz)) {
                            checkValueType(field, titleMapping.get(this.columnName), thisStr);
                            field.set(currentRow, BigDecimal.valueOf(Double.parseDouble(thisStr)));
                        } else {
                            field.set(currentRow, thisStr);
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else if ("row".equals(qName)) {
                if (rowNumber > this.headerRowIndex && !rIsEmpty) {
                    this.rows.add(currentRow);
                }
            }
        }

        private boolean isTextTag(String name) {
            if ("v".equals(name)) {
                // Easy, normal v text tag
                // 列有值，行肯定不为空
                rIsEmpty = false;
                return true;
            }
            if ("inlineStr".equals(name)) {
                // Easy inline string
                return true;
            }
            /*
             * if("t".equals(name) && isIsOpen) { // Inline string <is><t>...</t></is> pair return true; }
             */
            // It isn't a text tag
            return false;
        }

        private void checkValueType(Field field, String columnName, String value) {
            if (!NumberUtils.isCreatable(value) && value != null && !value.equals("")) {
                throw new RuntimeException("(标签页：" + sheetIndex + "，行：" + rowNumber + "，列：" + cellNumber + ")列名为'"
                        + columnName + "'，值" + value + "，不能转为数值类型");
            }
        }

        private void checkXlsxTitle(Map<String, Field> fieldMapping, Map<String, String> titleMapping) {
            if (titleMapping == null || titleMapping.isEmpty()) {
                throw new RuntimeException("标签页：" + sheetIndex + "，Excel文件标题栏错误，请检查下");
            }
            if (fieldMapping == null || fieldMapping.isEmpty()) {
                throw new RuntimeException("标签页：" + sheetIndex + "，没有读到需要的数据列，请检查下");
            }
            Iterator<Map.Entry<String, Field>> it = fieldMapping.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Field> entry = it.next();
                if (!titleMapping.containsValue(entry.getKey())) {
                    throw new RuntimeException("标签页：" + sheetIndex + "，列名为'" + entry.getKey() + "'不存在，请检查下");
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
        List<Mapping> rows = XlsxReader.fromInputStream(new FileInputStream("D:/change.xls"), 1, Mapping.class);
        StringBuilder buffer = new StringBuilder();
        for (Mapping mapping : rows) {
            buffer.append(mapping.getOld_area_code());
        }
        System.out.println(buffer);

        FileWriter write = new FileWriter("D:/change.sql", false);
        write.write(buffer.toString());
        write.close();

    }
}
