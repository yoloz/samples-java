package indi.yoloz.sample.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yolo
 */
public class MybatisUtils {

    public static SqlSessionFactory getSqlSessionFactory(String environmentId) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        return new SqlSessionFactoryBuilder().build(inputStream, environmentId);
    }
}

