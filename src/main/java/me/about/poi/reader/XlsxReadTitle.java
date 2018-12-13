package me.about.poi.reader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class XlsxReadTitle {

    enum xssfDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER
    }

    public static List<String> fromInputStream(InputStream in, int headerRowIndex) throws Exception {
        List<String> rows = new ArrayList<String>();
        OPCPackage pkg = OPCPackage.open(in);
        XSSFReader r = new XSSFReader(pkg);
        StylesTable stylesTable = r.getStylesTable();
        SharedStringsTable sharedStringsTable = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(stylesTable, sharedStringsTable, rows, headerRowIndex);
        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
        return rows;
    }

    public static  XMLReader fetchSheetParser(StylesTable stylesTable, SharedStringsTable strings, List<String> rows, int headerRowIndex) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        ContentHandler handler = new SheetHandler(stylesTable, strings, rows, headerRowIndex);
        parser.setContentHandler(handler);
        return parser;
    }

    public static class SheetHandler extends DefaultHandler {

        /** Table with styles */
        private StylesTable stylesTable;
        /** Table with unique strings */
        private SharedStringsTable sharedStringsTable;

        private int headerRowIndex;//
        private List<String> rows;
        private int index;// 行

        // Set when V start element is seen
        private boolean vIsOpen;
        // Set when cell start element is seen;
        // used when cell close element is seen.
        private xssfDataType nextDataType;
        // Used to format numeric cell values.
        private short formatIndex;
        private String formatString;
        private DataFormatter formatter;
        // Gathers characters as they are seen.
        private StringBuffer value;

        private SheetHandler(StylesTable stylesTable, SharedStringsTable sharedStringsTable, List<String> rows, int headerRowIndex) {
            this.stylesTable = stylesTable;
            this.sharedStringsTable = sharedStringsTable;
            this.rows = rows;
            this.headerRowIndex = headerRowIndex;

            this.value = new StringBuffer();
            this.nextDataType = xssfDataType.NUMBER;
            this.formatter = new DataFormatter();

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
                // Set up defaults.
                this.nextDataType = xssfDataType.NUMBER;
                this.formatIndex = -1;
                this.formatString = null;
                String cellType = attributes.getValue("t");
                String cellStyleStr = attributes.getValue("s");
                if ("b".equals(cellType)) nextDataType = xssfDataType.BOOL;
                else if ("e".equals(cellType)) nextDataType = xssfDataType.ERROR;
                else if ("inlineStr".equals(cellType)) nextDataType = xssfDataType.INLINESTR;
                else if ("s".equals(cellType)) nextDataType = xssfDataType.SSTINDEX;
                else if ("str".equals(cellType)) nextDataType = xssfDataType.FORMULA;
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
                    val = value.toString();
                    break;
                case INLINESTR:
                    // TODO: have seen an example of this, so it's untested.
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    val = rtsi.toString();
                    break;
                case SSTINDEX:
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
                    rows.add(String.valueOf(val));
                }
                // Process the value contents as required.
                // Do now, as characters() may be called more than once
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
        List<String> rows = XlsxReadTitle.fromInputStream(new FileInputStream("D:/test2.xlsx"), 2);
        System.out.println(rows);
    }
}
