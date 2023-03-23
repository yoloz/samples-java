package indi.yolo.sample.cxf.service;


import indi.yolo.sample.cxf.dao.enitity.NorResponse;
import indi.yolo.sample.cxf.dao.enitity.ServiceStatus;
import indi.yolo.sample.cxf.dao.enitity.AlarmInfo;
import indi.yolo.sample.cxf.dao.enitity.AuditResponse;
import indi.yolo.sample.cxf.dao.enitity.FlowInfo;
import indi.yolo.sample.cxf.dao.enitity.HourAudit;
import indi.yolo.sample.cxf.dao.enitity.ServiceDelInfo;
import indi.yolo.sample.cxf.dao.enitity.ServiceInfo;
import indi.yolo.sample.cxf.dao.enitity.TestInfo;

/**
 * 和集控交互的接口
 * User: lil
 * Date: 2011-1-14
 * Time: 14:53:46
 */
public interface IService {
    NorResponse addService(ServiceInfo serviceInfo);  //添加服务

    NorResponse modService(ServiceInfo serviceInfo);  //修改服务

    NorResponse delService(ServiceDelInfo serviceDelInfo);  //删除服务

    NorResponse updateServiceStatus(ServiceStatus serviceStatus);  //更新服务状态

    AuditResponse hourAudit(HourAudit hourAudit);  //服务小时审计统计

    NorResponse serviceFlowAuditInfo(FlowInfo flowInfo);  //流量审计信息

    NorResponse serviceAlarmAuditInfo(AlarmInfo alarmInfo);  //报警审计信息

    NorResponse reportTest(TestInfo testInfo);  //上报测试
}
