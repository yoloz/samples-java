package indi.yolo.sample.jsch.impl;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Utils {

    static void extractTar_Gz(String compressFile, String targetDir) throws IOException {
        Files.createDirectories(Paths.get(targetDir));
        Path sf = Paths.get(compressFile);
        String unSuffix = sf.getFileName().toString().replace(".tar.gz", "/");
        boolean first = true;
        try (InputStream fi = Files.newInputStream(sf);
             BufferedInputStream bi = new BufferedInputStream(fi);
             GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
             ArchiveInputStream archiveInputStream = new TarArchiveInputStream(gzi)) {
            ArchiveEntry entry;
            while ((entry = archiveInputStream.getNextEntry()) != null) {
                if (!archiveInputStream.canReadEntryData(entry)) {
                    System.out.println(entry.getName() + "can not read....");
                    continue;
                }
                if (first) {
                    if (!targetDir.endsWith("/")) targetDir = targetDir + "/";
                    if (!targetDir.endsWith(unSuffix))
                        targetDir = Paths.get(targetDir, unSuffix).toString();
                    first = false;
                }
                String entryName = entry.getName();
                File f;
                if (entryName.startsWith(unSuffix))
                    f = Paths.get(targetDir, entryName.replaceFirst(unSuffix, "")).toFile();
                else f = Paths.get(targetDir, entryName).toFile();
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) throw new IOException("failed to create directory " + f);
                } else {
                    File parentFile = f.getParentFile();
                    if (!parentFile.exists()) Files.createDirectories(parentFile.toPath());
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(archiveInputStream, o);
                    }
                }
            }
        }
    }
}
