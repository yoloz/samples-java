package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.entity.RoleTest;

import java.util.List;
import java.util.Map;

/**
 * @since 10/14/25
 */
public interface RoleTestDao {

    List<RoleTest> search(Map<String, Object> condition);

}
