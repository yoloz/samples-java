package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.Date2Timestamp;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TimeZone;

/**
 * @author yolo
 */
class Date2TimestampTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
        TimeZone.setDefault(TimeZone.getTimeZone("+0800"));
    }

    @Test
    void getDbList() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            Date2TimestampDao mapper = sqlSession.getMapper(Date2TimestampDao.class);
            List<Date2Timestamp> testList = mapper.query();
            for (Date2Timestamp test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void getSDKList() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("sdk").openSession();
            Date2TimestampDao mapper = sqlSession.getMapper(Date2TimestampDao.class);
            List<Date2Timestamp> testList = mapper.query();
            for (Date2Timestamp test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

}