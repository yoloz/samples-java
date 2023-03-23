package indi.yolo.sample.cxf.dao.enitity;

import java.io.Serializable;

/**
 * 服务的审计统计信息对象
 * User: lil
 * Date: 2011-1-14
 * Time: 15:21:16
 */
public class ServiceAuditInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String serviceId ;	   //服务编号
    private long flow;           //累计流量，单位是B
    private long auditNum;       //累计条数
    private long alarmNum;       //累计报警数
    private String startTime;     //统计开始时间，格式yyyy-mm-dd HH:MM:SS
    private String endTime;	    //统计结束时间，格式yyyy-mm-dd HH:MM:SS
    private int reportTime;       //上报时间,值应是0~23之间

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }

    public long getAuditNum() {
        return auditNum;
    }

    public void setAuditNum(long auditNum) {
        this.auditNum = auditNum;
    }

    public long getAlarmNum() {
        return alarmNum;
    }

    public void setAlarmNum(long alarmNum) {
        this.alarmNum = alarmNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getReportTime() {
        return reportTime;
    }

    public void setReportTime(int reportTime) {
        this.reportTime = reportTime;
    }
}
