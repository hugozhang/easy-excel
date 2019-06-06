package me.about.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class Test {

    public static void main(String[] args) throws InvalidPasswordException, IOException {

        PDDocument document = PDDocument.load(new File("C:/Users/Administrator/Downloads/电子发票-1.pdf"));
        document.getClass();

        if (!document.isEncrypted()) {

            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);

            PDFTextStripper tStripper = new PDFTextStripper();

            String pdfFileInText = tStripper.getText(document);
            System.out.println("Text:" + pdfFileInText);

            // split by whitespace
            String lines[] = pdfFileInText.split("\\r?\\n");
            for (String line : lines) {
                System.out.println(line);
            }
        }
    }
}
