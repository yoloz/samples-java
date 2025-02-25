package indi.yolo.sample;

import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        String passwd = "12345";
        Path path = Paths.get("/data/yolo/person_202405301145.zip");
        BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(path), 10000);

        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, passwd.toCharArray());

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        zipParameters.setFileNameInZip("person_202405301145.xml");

        zipOutputStream.putNextEntry(zipParameters);
        byte[] buff = new byte[4096];
        int readLen;
        try(InputStream inputStream = Files.newInputStream(Paths.get("/data/yolo/person_202405301145.xml"))) {
            while ((readLen = inputStream.read(buff)) != -1) {
                zipOutputStream.write(buff, 0, readLen);
            }
        }
        zipOutputStream.closeEntry();
        zipOutputStream.close();
    }
}
