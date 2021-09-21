package com.yoloz.sample.sigar.output;

import org.junit.Before;
import org.junit.Test;

public class KafkaTest {

    Output kafka;
    @Before
    public void setUp() throws Exception {
        kafka = Output.getOutput("kafka","10.68.23.11:9092","gatherTest");
    }

    @Test
    public void apply() {
        while (true){
            try {
                kafka.apply("/home/ylzhang/fileBeatTest/test2","test22".getBytes("UTF-8"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}