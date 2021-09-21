package com.yoloz.sample.sigar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class GatherTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
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