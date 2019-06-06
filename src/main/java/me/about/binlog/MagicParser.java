package me.about.binlog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

public class MagicParser {

    public static final byte[] MAGIC_HEADER = new byte[] { (byte) 0xfe, (byte) 0x62, (byte) 0x69, (byte) 0x6e };

    public static void main(String[] args) throws Exception {
        String filePath = "D:\\workspace\\MySQL\\MySQL Server 5.6\\data\\mysql-bin.000001";
        File binlogFile = new File(filePath);
        ByteArrayInputStream inputStream = null;
        inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(binlogFile));
        byte[] magicHeader = new byte[4];
        inputStream.read(magicHeader);
        System.out.println("魔数\\xfe\\x62\\x69\\x6e是否正确:" + Arrays.equals(MAGIC_HEADER, magicHeader));
        
       
    }

}
