package indi.yoloz.sample.jndi;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * WebLogic添加自定义SDK测试连接包:${WL_HOME}/wlserver/modules/com.bea.core.datasource6.jar:weblogic.jdbc.common.internal.DataSourceUtil.testConnection0
 *
 * @author yolo
 */
public class JndiTestWithoutContainer {

    public static void main(String[] args) throws NamingException, SQLException {

        // 配置JNDI环境参数
        Hashtable<String, String> env = new Hashtable<>();
        // 通过org.apache.naming.java.javaURLContextFactory实现独立JNDI上下文‌
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        env.put(Context.PROVIDER_URL, "localhost"); // 虚拟地址，仅用于上下文绑定

        // 创建初始上下文
        // 使用java:comp/env/前缀符合J2EE命名规范，便于与容器环境保持兼容‌
        InitialContext context = new InitialContext(env);
        context.createSubcontext("java:comp");
        context.createSubcontext("java:comp/env");
        context.createSubcontext("java:comp/env/jdbc");

        // 配置数据源
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@//192.168.124.252:10000/oracleTest");
//        dataSource.setUrl("jdbc:oracle:thin:@//192.168.124.231:1521/XEPDB1");
        dataSource.setDriverClassName("com.yzsec.dsg.sdk.jdbc.YzSecDriver");
//        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUsername("test");
        dataSource.setPassword("test");
        dataSource.setInitialSize(5); // 初始化连接数

        // 绑定数据源到JNDI
        context.bind("java:comp/env/jdbc/MyDataSource", dataSource);

        // 测试获取连接
        DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/MyDataSource");
        try (Connection conn = ds.getConnection()) {
            System.out.println("成功获取JNDI连接: " + !conn.isClosed());
        }
    }
}
