package indi.yolo.sample.sigar;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

public class GatherTest {

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void gather() {
        String os = System.getProperty("os.name").toLowerCase();
        String java_libPath = System.getProperty("java.library.path");
        System.out.printf("%s\t%s\n", os, java_libPath);
        String sigar_libPath = new File("").getAbsolutePath() + "/lib/sigar";
        if (!java_libPath.contains(sigar_libPath)) {
            if (os.contains("win")) {
                java_libPath += ";" + sigar_libPath;
            } else {
                java_libPath += ":" + sigar_libPath;
            }
            System.setProperty("java.library.path", java_libPath);
        }
        System.out.println(System.getProperty("java.library.path"));
    }
}