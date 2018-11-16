package beans;


import common.JsonUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class MetricInfoTest {


    public static void main(String[] args) {
        List<MetricInfo.InnerData> innerDataList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            MetricInfo.InnerData innerData = new MetricInfo().new InnerData();
            innerData.setCpu((float) 12.0);
            innerData.setDisk((float) 25);
            innerData.setMem((float) 30);
            innerData.setMillis(123456789);
            innerDataList.add(innerData);
        }
        MetricInfo metricInfo = new MetricInfo();
        metricInfo.setServiceId("testService");
        metricInfo.setInnerData(innerDataList);
        System.out.println(JsonUtils.toJson(metricInfo));
    }

}