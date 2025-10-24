package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.entity.ByteTest;

import java.util.List;

/**
 * @author yolo
 */
public interface ByteTestDao {

    List<ByteTest> getList();

    int insertByteTest(ByteTest byteTest);
}
