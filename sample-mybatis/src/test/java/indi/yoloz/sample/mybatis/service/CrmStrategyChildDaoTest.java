package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.CrmStrategyChild;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author yolo
 */
class CrmStrategyChildDaoTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
    }

    @Test
    void getDbList() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            CrmStrategyChildDao mapper = sqlSession.getMapper(CrmStrategyChildDao.class);
            List<Integer> status = Arrays.asList(10, 11, 12);
            List<CrmStrategyChild> testList = mapper.getList(status, LocalDateTime.now().minusDays(1));
            for (CrmStrategyChild test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void insertDbCrmStrategyChild() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            CrmStrategyChildDao mapper = sqlSession.getMapper(CrmStrategyChildDao.class);
            CrmStrategyChild test = new CrmStrategyChild();
            test.setName("ceshi2");
            test.setStrategyCode("123456");
            test.setReachWays(123);
            test.setTemplateId(456L);
            test.setObjective(78);
            test.setStartTime(LocalDateTime.now());
            test.setEndTime(LocalDateTime.now().plusSeconds(10));
            test.setImplementCron("ceshi2");
            test.setStatus(11);
            test.setBeanName("com.yzsec.dsg.ceshi2");
            int i = mapper.insertCrmStrategyChild(test);
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
            CrmStrategyChildDao mapper = sqlSession.getMapper(CrmStrategyChildDao.class);
            List<Integer> status = Arrays.asList(10, 11, 12);
            List<CrmStrategyChild> testList = mapper.getList(status, LocalDateTime.now().minusDays(1));
            for (CrmStrategyChild test : testList) {
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
            CrmStrategyChildDao mapper = sqlSession.getMapper(CrmStrategyChildDao.class);
            CrmStrategyChild test = new CrmStrategyChild();
            test.setName("ceshi2");
            test.setStrategyCode("123456");
            test.setReachWays(123);
            test.setTemplateId(456L);
            test.setObjective(78);
            test.setStartTime(LocalDateTime.now());
            test.setEndTime(LocalDateTime.now().plusSeconds(10));
            test.setImplementCron("ceshi2");
            test.setStatus(11);
            test.setBeanName("com.yzsec.dsg.ceshi2");
            int i = mapper.insertCrmStrategyChild(test);
            sqlSession.commit();
            assertEquals(1, i);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}