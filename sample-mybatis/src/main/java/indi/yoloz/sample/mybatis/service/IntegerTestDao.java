package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.entity.IntegerTest;

import java.util.List;

/**
 * @author yolo
 */
public interface IntegerTestDao {

    List<IntegerTest> getList();

    int insertIntegerTest(IntegerTest integerTest);

}
