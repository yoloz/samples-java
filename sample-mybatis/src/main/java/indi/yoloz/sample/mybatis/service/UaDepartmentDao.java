package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.entity.UaDepartment;
//import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//@Mapper
public interface UaDepartmentDao {

    int deleteByPrimaryKey(Long id);

    int insert(UaDepartment record);

    int batchInsert(List<UaDepartment> records);

    int insertSelective(UaDepartment record);

    UaDepartment selectByPrimaryKey(Long id);

    List<UaDepartment> queryAll();

    UaDepartment selectByBusinessCode(String businessCode);

    UaDepartment selectByCode(String code);

    UaDepartment selectNwByCode(String code);

    UaDepartment selectByLocaleCode(String localeCode);

    UaDepartment selectByCompanyId(Long companyId);

    int updateByPrimaryKeySelective(UaDepartment record);

    int updateByCode(UaDepartment record);

    int updateByBusinessCode(UaDepartment record);

    int updateByPrimaryKey(UaDepartment record);

    int batchUpdate(List<UaDepartment> records);

    List<UaDepartment> selectByQuery(UaDepartment query);

    List<UaDepartment> selectByAccount(@Param("account") String account);

//    List<UaDepartmentBO> findUaDepartmentTree(UaDepartment query);

    List<UaDepartment> findPageByQuery(@Param("query") String query);

    List<UaDepartment> queryByPage(UaDepartment query);

    UaDepartment findDepartmentName(Long  departmentId);

    UaDepartment  selectByParentBusinessCode(String parentCode);


}