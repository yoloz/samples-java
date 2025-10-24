package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.ByteTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yolo
 */
class ByteTestDaoTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
    }

    @Test
    void getDbList() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            ByteTestDao mapper = sqlSession.getMapper(ByteTestDao.class);
            List<ByteTest> testList = mapper.getList();
            for (ByteTest test : testList) {
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
            ByteTestDao mapper = sqlSession.getMapper(ByteTestDao.class);
            ByteTest test = new ByteTest();
            test.setName("db1");
            test.setByte1((byte) -36);
            test.setByte2((byte) 1);
            int i = mapper.insertByteTest(test);
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
            ByteTestDao mapper = sqlSession.getMapper(ByteTestDao.class);
            List<ByteTest> testList = mapper.getList();
            for (ByteTest test : testList) {
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
            ByteTestDao mapper = sqlSession.getMapper(ByteTestDao.class);
            ByteTest test = new ByteTest();
            test.setName("sdk1");
            test.setByte1((byte) -35);
            int i = mapper.insertByteTest(test);
            sqlSession.commit();
            assertEquals(1, i);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}