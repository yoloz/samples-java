package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.entity.Date2Timestamp;

import java.util.List;

/**
 * @author yolo
 */
public interface Date2TimestampDao {

    List<Date2Timestamp> query();

}
