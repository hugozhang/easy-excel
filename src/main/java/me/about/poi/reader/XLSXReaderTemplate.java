package me.about.poi.reader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.about.poi.CellDataType;
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

import me.about.poi.Creator;

public abstract class XLSXReaderTemplate<T> {

    public List<T> fromInputStream(InputStream in, Class<T> clazz, int headerRowIndex) throws Exception {
        List<T> rows = new ArrayList<T>();
        OPCPackage pkg = OPCPackage.open(in);
        XSSFReader r = new XSSFReader(pkg);
        StylesTable stylesTable = r.getStylesTable();
        SharedStringsTable sharedStringsTable = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(stylesTable, sharedStringsTable, this, rows, clazz, headerRowIndex);
        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
        return rows;
    }

    private XMLReader fetchSheetParser(StylesTable stylesTable, SharedStringsTable strings, XLSXReaderTemplate<T> xlsxReaderTemplate, List<T> rows, Class<T> clazz,
                                       int headerRowIndex) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        ContentHandler handler = new SheetHandler<T>(stylesTable, strings, xlsxReaderTemplate, rows, clazz, headerRowIndex);
        parser.setContentHandler(handler);
        return parser;
    }

    public abstract Map<String, String> titleToFieldMapping();

    public static class SheetHandler<T> extends DefaultHandler {

        /** Table with styles */
        private StylesTable stylesTable;
        /** Table with unique strings */
        private SharedStringsTable sharedStringsTable;

        private XLSXReaderTemplate<T> xlsxReaderTemplate;

        private int headerRowIndex;//
        private Class<T> clazz;
        private T currentRow;
        private List<T> rows;
        private String columnName;
        private Map<String, Field> fieldMapping = new HashMap<String, Field>();
        private Map<String, String> titleMapping = new HashMap<String, String>();
        private int index;// 行

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

        private SheetHandler(StylesTable stylesTable, SharedStringsTable sharedStringsTable, XLSXReaderTemplate<T> xlsxReaderTemplate, List<T> rows, Class<T> clazz,
                             int headerRowIndex) {
            this.stylesTable = stylesTable;
            this.sharedStringsTable = sharedStringsTable;
            this.xlsxReaderTemplate = xlsxReaderTemplate;

            this.clazz = clazz;
            this.rows = rows;
            this.headerRowIndex = headerRowIndex;

            this.value = new StringBuffer();
            this.nextDataType = CellDataType.NUMBER;
            this.formatter = new DataFormatter();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                fieldMapping.put(field.getName(), field);
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
                else if (cellStyleStr != null) {
                    /*
                     * It's a number, but possibly has a style and/or special format. Nick Burch said to use org.apache.poi.ss.usermodel.BuiltinFormats, and I see javadoc for that
                     * at apache.org, but it's not in the POI 3.5 Beta 5 jars. Scheduled to appear in 3.5 beta 6.
                     */
                    int styleIndex = Integer.parseInt(cellStyleStr);
                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatString == null) this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                }
            } else if ("row".equals(name)) {
                index++;
                if (index > this.headerRowIndex) {
                    currentRow = Creator.of(this.clazz);
                }
            }
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            Object val = null;
            // v => contents of a cell
            if ("v".equals(name)) {
                switch (nextDataType) {
                case BOOLEAN:
                    char first = value.charAt(0);
                    val = first == '0' ? false : true;
                    break;
                case ERROR:
                    val = "\"ERROR:" + value.toString() + '"';
                    break;
                case FORMULA:
                    // A formula could result in a string value,
                    // so always add double-quote characters.
                    val = value.toString();
                    break;
                case INLINE_STRING:
                    // TODO: have seen an example of this, so it's untested.
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    val = rtsi.toString();
                    break;
                case SST_STRING:
                    String sstIndex = value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                        val = rtss.toString();
                    } catch (NumberFormatException ex) {
                    }
                    break;
                case NUMBER:
                    String n = value.toString();
                    if (DateUtil.isADateFormat(this.formatIndex, this.formatString)) {
                        val = DateUtil.getJavaDate(Double.parseDouble(n));
                    } else if (this.formatString != null) {
                        val = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex, this.formatString);
                    } else {
                        val = n;
                    }
                    break;
                default:
                    val = "(TODO: Unexpected type: " + nextDataType + ")";
                    break;
                }

                if (index == this.headerRowIndex) {
                    //eg:  A -> 电话
                    titleMapping.put(this.columnName, String.valueOf(val));
                }
                // Process the value contents as required.
                // Do now, as characters() may be called more than once

                if (index > this.headerRowIndex) {

                    Map<String, String> titleToField = xlsxReaderTemplate.titleToFieldMapping();

                    if (titleToField == null || titleToField.isEmpty()) throw new IllegalArgumentException("The method titleToFieldMapping return empty.");

                    Field field = fieldMapping.get(titleToField.get(titleMapping.get(this.columnName)));

                    if (field != null) {
                        try {
                            field.setAccessible(true);
                            if (int.class.equals(field.getType()) || Integer.class.equals(field.getType())) {
                                field.set(currentRow, Integer.valueOf(val.toString()));
                            } else if (long.class.equals(field.getType()) || Long.class.equals(field.getType())) {
                                field.set(currentRow, Long.valueOf(val.toString()));
                            } else if (float.class.equals(field.getType()) || Float.class.equals(field.getType())) {
                                field.set(currentRow, Float.valueOf(val.toString()));
                            } else if (double.class.equals(field.getType()) || Double.class.equals(field.getType())) {
                                field.set(currentRow, Double.valueOf(val.toString()));
                            } else if (boolean.class.equals(field.getType()) || Boolean.class.equals(field.getType())) {
                                field.set(currentRow, val);
                            } else if (java.util.Date.class.equals(field.getType())) {
                                field.set(currentRow, val);
                            } else if (BigDecimal.class.equals(field.getType())) {
                                field.set(currentRow, BigDecimal.valueOf(Double.parseDouble(val.toString())));
                            } else {
                                field.set(currentRow, val.toString());
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if ("row".equals(name)) {
                if (index > this.headerRowIndex) {
                    this.rows.add(currentRow);
                }
            }
        }

        /**
         * Captures characters only if a suitable element is open. Originally was just "v"; extended for inlineStr also.
         */
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (vIsOpen) value.append(ch, start, length);
        }

        public static void main(String[] args) throws FileNotFoundException, Exception {
            
        }
    }
}
