package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.entity.BoolTest;

import java.util.List;

/**
 * @author yolo
 */
public interface BoolTestDao {

    List<BoolTest> getList();

    int insertBoolTest(BoolTest boolTest);

    int batchInsertBoolTest(List<BoolTest> boolTests);
}
