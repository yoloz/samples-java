package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.IntegerTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author yolo
 */
class IntegerTestDaoTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
    }

    @Test
    void getDbList() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            IntegerTestDao mapper = sqlSession.getMapper(IntegerTestDao.class);
            List<IntegerTest> testList = mapper.getList();
            for (IntegerTest test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void insertDbByteTest() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            IntegerTestDao mapper = sqlSession.getMapper(IntegerTestDao.class);
            IntegerTest test = new IntegerTest();
            test.setCharInt(2);
            int i = mapper.insertIntegerTest(test);
            sqlSession.commit();
            assertEquals(1, i);
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
            IntegerTestDao mapper = sqlSession.getMapper(IntegerTestDao.class);
            List<IntegerTest> testList = mapper.getList();
            for (IntegerTest test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void insertSDKByteTest() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("sdk").openSession();
            IntegerTestDao mapper = sqlSession.getMapper(IntegerTestDao.class);
            IntegerTest test = new IntegerTest();
            test.setCharInt(2);
            int i = mapper.insertIntegerTest(test);
            sqlSession.commit();
            assertEquals(1, i);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}