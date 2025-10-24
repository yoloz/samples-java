package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.entity.CrmStrategyChild;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yolo
 */
public interface CrmStrategyChildDao {

    List<CrmStrategyChild> getList(@Param("status") List<Integer> status, @Param("endTime") LocalDateTime endTime);

    int insertCrmStrategyChild(CrmStrategyChild child);
}
