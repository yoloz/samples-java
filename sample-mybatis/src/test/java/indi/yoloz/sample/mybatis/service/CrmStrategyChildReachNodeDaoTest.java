package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.CrmStrategyChildReachNodeDTO;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


/**
 * @author yolo
 */
class CrmStrategyChildReachNodeDaoTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
    }

    @Test
    void getDbList() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            CrmStrategyChildReachNodeDao mapper = sqlSession.getMapper(CrmStrategyChildReachNodeDao.class);
            List<CrmStrategyChildReachNodeDTO> testList = mapper.getList();
            for (CrmStrategyChildReachNodeDTO test : testList) {
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
            CrmStrategyChildReachNodeDao mapper = sqlSession.getMapper(CrmStrategyChildReachNodeDao.class);
            List<CrmStrategyChildReachNodeDTO> testList = mapper.getList();
            for (CrmStrategyChildReachNodeDTO test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }


}