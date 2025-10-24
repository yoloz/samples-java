package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.BoolTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yolo
 */
class BoolTestDaoTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
    }

    @Test
    void getDbList() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            BoolTestDao mapper = sqlSession.getMapper(BoolTestDao.class);
            List<BoolTest> testList = mapper.getList();
            for (BoolTest boolTest : testList) {
                System.out.println(boolTest);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void insertDbBoolTest() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            BoolTestDao mapper = sqlSession.getMapper(BoolTestDao.class);
            BoolTest boolTest = new BoolTest();
            boolTest.setName("db1");
            boolTest.setEmail("db1@qq.com");
            boolTest.setBoolChar(true);
            boolTest.setBool(true);
            boolTest.setBoolBit(true);
            int i = mapper.insertBoolTest(boolTest);
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
            BoolTestDao mapper = sqlSession.getMapper(BoolTestDao.class);
            List<BoolTest> testList = mapper.getList();
            for (BoolTest boolTest : testList) {
                System.out.println(boolTest);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void insertSDKBoolTest() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("sdk").openSession();
            BoolTestDao mapper = sqlSession.getMapper(BoolTestDao.class);
            BoolTest boolTest = new BoolTest();
            boolTest.setName("db1");
            boolTest.setEmail("db1@qq.com");
            boolTest.setBoolChar(true);
            boolTest.setBool(true);
            boolTest.setBoolBit(true);
            int i = mapper.insertBoolTest(boolTest);
            sqlSession.commit();
            assertEquals(1, i);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}