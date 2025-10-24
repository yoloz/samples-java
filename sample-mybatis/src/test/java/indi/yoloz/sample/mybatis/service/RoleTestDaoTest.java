package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.RoleTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author yolo
 */
class RoleTestDaoTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
    }

    @Test
    void searchByDB() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            RoleTestDao mapper = sqlSession.getMapper(RoleTestDao.class);
            Map<String, Object> condition = new HashMap<>();
            condition.put("userName", "1101050000001");
            condition.put("service", "zis-cloud-portal");
            List<RoleTest> testList = mapper.search(condition);
            for (RoleTest test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }


    @Test
    void searchBySDK() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("sdk").openSession();
            RoleTestDao mapper = sqlSession.getMapper(RoleTestDao.class);
            Map<String, Object> condition = new HashMap<>();
            condition.put("userName", "1101050000001");
            condition.put("service", "zis-cloud-portal");
            List<RoleTest> testList = mapper.search(condition);
            for (RoleTest test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}