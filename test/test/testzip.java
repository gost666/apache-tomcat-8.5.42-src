package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class testzip {
    public static void main(String[] args) throws Exception {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("example.zip"));
            File xml_folder = new File("F:\\xml_folder\\122.xml");
            File[] files = xml_folder.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    FileInputStream inputStream = new FileInputStream(file);
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    zipOutputStream.close();
                }
            }
            zipOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
