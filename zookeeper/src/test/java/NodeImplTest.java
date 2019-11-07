import org.apache.log4j.PropertyConfigurator;
import org.hyperic.sigar.SigarException;
import org.junit.*;

import java.nio.file.Paths;


public class NodeImplTest {

    private NodeImpl nodeImpl;

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configure(Paths.get(System.getProperty("user.dir"),
                "src/main/resources", "log4j.properties").toString());
        ZKClient.getInstance().connect("127.0.0.1:2181");
        nodeImpl = new NodeImpl("/test", "127.0.0.1", 8007);
    }

    @After
    public void tearDown() {
        if (nodeImpl != null) nodeImpl.close();
        ZKClient.getInstance().close();
    }

    @Test
    public void register() throws Exception {
        nodeImpl.register();
        int count = 10000000;
        while (count > 0) {
            Thread.sleep(3000);
            count--;
        }
    }

    @Test
    public void read() throws Exception {
        nodeImpl.register();
        int count = 5;
        while (count > 0) {
            System.out.println(ZKClient.getInstance().read(nodeImpl.getNodePath()));
            count--;
            Thread.sleep(2000);
        }

    }

    @Test
    public void localIPTest() throws SigarException {
        nodeImpl.getLocalIP();
    }


}