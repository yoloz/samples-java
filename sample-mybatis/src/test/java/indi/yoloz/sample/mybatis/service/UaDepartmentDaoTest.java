package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.MybatisUtils;
import indi.yoloz.sample.mybatis.entity.UaDepartment;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yolo
 */
class UaDepartmentDaoTest {

    SqlSession sqlSession;

    @BeforeEach
    void init() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
    }

    @Test
    void searchByDB() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            UaDepartmentDao mapper = sqlSession.getMapper(UaDepartmentDao.class);
            List<UaDepartment> testList = mapper.queryAll();
            for (UaDepartment test : testList) {
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
            UaDepartmentDao mapper = sqlSession.getMapper(UaDepartmentDao.class);
            List<UaDepartment> testList = mapper.queryAll();
            for (UaDepartment test : testList) {
                System.out.println(test);
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    private List<UaDepartment> generateTestData(int size, boolean isUpdate) {
        List<UaDepartment> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UaDepartment dept = new UaDepartment();
            if (isUpdate) {
                dept.setId(i + 1L);
                dept.setCode("DEPT_UPDATE_" + i);
                dept.setName("测试部门_更新_" + i);
                dept.setBusinessCode("BUS_UPDATE_" + i);
                dept.setCreator("creator_update_" + i);
                dept.setGmtCreated(new java.util.Date(System.currentTimeMillis() - 86400000)); // 设置为昨天
                dept.setModifier("modifier_update_" + i);
                dept.setGmtModified(new java.util.Date());
            } else {
                dept.setCode("DEPT" + i);
                dept.setName("测试部门" + i);
                dept.setBusinessCode("BUS" + i);
                dept.setCreator("creator" + i);
                dept.setGmtCreated(new java.util.Date());
                dept.setModifier("modifier" + i);
                dept.setGmtModified(new java.util.Date());
            }
            dept.setCompanyId((long) (i % 5 + 1));
            dept.setParentBusinessCode(i > 0 ? "BUS" + (i % 10) : null);
            dept.setSuperiorLeader((long) (i % 8));
            dept.setParentDeptId(i > 0 ? i % 10 : null);
            dept.setTreeCode("TREE" + i);
            dept.setChargeCustId((long) (i % 20));
            dept.setChargeCustName("负责人" + i);
            dept.setDelegateCustId((long) (i % 15));
            dept.setDelegateCustName("委托人" + i);
            dept.setDepartmentType(i % 3);
            dept.setStatus("1");
            dept.setIsDeleted("0");
            dept.setLocaleCode("zh_CN");
            dept.setLevel(String.valueOf(i % 5));
            dept.setParentCode(i > 0 ? "DEPT" + (i % 10) : null);
            dept.setFinanceDeptCode("FIN" + i);
            dept.setAddressCName("地址" + i);
            dept.setPostCode("10000" + (i % 9));
            dept.setPhoneNumber("1380000000" + (i % 10));
            dept.setCenterFlag(i % 2 == 0 ? "Y" : "N");
            dept.setWebAddress("http://dept" + i + ".com");
            dept.setServicePhone("400-000-000" + (i % 10));
            dept.setComFlag(i % 2 == 0 ? "N" : "F");
            dept.setDistrictName("区域" + i);
            dept.setDistrictPlatCode("DIST" + i);
            dept.setPolicyCityCode("CITY" + (i % 20));
            dept.setPolicyAreaCode("AREA" + (i % 15));
            dept.setContacts("联系人" + i);
            dept.setPdfSealRuleNum("SEAL" + i);
            dept.setExtraInfo("{\"test\":\"value" + i + "\"}");
            list.add(dept);
        }
        return list;
    }

    @Test
    void insertBatchByDB() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            List<UaDepartment> list = generateTestData(40, false);
            UaDepartmentDao mapper = sqlSession.getMapper(UaDepartmentDao.class);
            System.out.println(mapper.batchInsert(list));
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void insertBatchBySDK() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("sdk").openSession();
            List<UaDepartment> list = generateTestData(40, false);
            UaDepartmentDao mapper = sqlSession.getMapper(UaDepartmentDao.class);
            System.out.println(mapper.batchInsert(list));
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void updateBatchByDB() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("db").openSession();
            List<UaDepartment> list = generateTestData(40, true);
            UaDepartmentDao mapper = sqlSession.getMapper(UaDepartmentDao.class);
            System.out.println(mapper.batchUpdate(list));
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    void updateBatchBySDK() throws Exception {
        try {
            sqlSession = MybatisUtils.getSqlSessionFactory("sdk").openSession();
            List<UaDepartment> list = generateTestData(40, true);
            UaDepartmentDao mapper = sqlSession.getMapper(UaDepartmentDao.class);
            System.out.println(mapper.batchUpdate(list));
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}