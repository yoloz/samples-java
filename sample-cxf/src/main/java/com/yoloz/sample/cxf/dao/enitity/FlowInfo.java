package com.yoloz.sample.cxf.dao.enitity;

import java.io.Serializable;

/**
 * 流量审计信息的对象.
 * User: lil
 * Date: 2011-1-14
 * Time: 15:31:13
 */
public class FlowInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identification = "GXPT";       //身份，见《身份列表》
    private String ip;                              //ip地址，不是hostname
    private String version;                        //版本，如平台4.5就写“4.5”
    private String devId;                          //设备编号
    private String serviceId;                      //服务编号
    private String auditDate;                      //审计日期，格式“yyyy-mm-dd”
    private String auditTime;                      //审计详细时间，格式“yyyy年mm月dd日HH时MM分SS秒”
    private String source;                         //审计主体，即访问端
    private String action;                         //动作类型，例如：访问
    private String dest;                           //审计客体，即被访问端
    private long flow;                            //流量，单位是B
    private long auditNum;                        //审计数
    private String result;                         //结果，成果还是失败，见附录《审计结果》
    private String desc;                           //描述

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
