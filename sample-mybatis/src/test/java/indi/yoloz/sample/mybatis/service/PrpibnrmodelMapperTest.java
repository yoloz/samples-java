package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.Prpibnrmodel;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @since 10/20/25
 */
public class PrpibnrmodelMapperTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
    }


    @Test
    void insertSelectiveBySDK() throws Exception {
        try {
            Path ibnrModelPath = Paths.get(System.getProperty("user.dir"), "src/test/resources/ibnrmodel.json");
            String ibnrModel = Files.readString(ibnrModelPath);
            sqlSession = MybatisUtils.getSqlSessionFactory("sdkDM").openSession();
            PrpibnrmodelMapper mapper = sqlSession.getMapper(PrpibnrmodelMapper.class);
            Prpibnrmodel prpibnrmodel = new Prpibnrmodel();
            prpibnrmodel.setIbnrmodel(ibnrModel);
            prpibnrmodel.setInsertdate(new Date());
            prpibnrmodel.setTaskcode("202308001002");
            prpibnrmodel.setTcol1("database");
            int i = mapper.insertSelective(prpibnrmodel);
            System.out.println("insert result: " + i);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void selectByPrimaryKeyBySDK() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("sdkDM").openSession();
            PrpibnrmodelMapper mapper = sqlSession.getMapper(PrpibnrmodelMapper.class);
            Prpibnrmodel prpibnrmodel = mapper.selectByPrimaryKey("202308001002");
            System.out.println("select result: " + prpibnrmodel);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}
