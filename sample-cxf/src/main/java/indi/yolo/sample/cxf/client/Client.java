package indi.yolo.sample.cxf.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import indi.yolo.sample.cxf.dao.enitity.NorResponse;
import indi.yolo.sample.cxf.dao.enitity.TestInfo;
import indi.yolo.sample.cxf.service.IService;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Client {

    private final static Logger logger = LoggerFactory.getLogger(Client.class);

    private Client() {
    }

    public static void main(String args[]) throws Exception {
        ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
        if (args != null && args.length > 0 && !"".equals(args[0])) {
            factory.setAddress(args[0]);
        } else {
            factory.setAddress("http://localhost:9000/cmserver/service");
        }
        IService client = factory.create(IService.class);
        logger.info("client success....");
        TestInfo testInfo = new TestInfo();
        testInfo.setIp("0.0.0.0");
        testInfo.setDevId("123456");
        testInfo.setVersion("1.0");
        NorResponse norResponse = client.reportTest(testInfo);
        logger.info(new Gson().toJson(norResponse, new TypeToken<NorResponse>() {
        }.getType()));
        System.exit(0);
    }

}
