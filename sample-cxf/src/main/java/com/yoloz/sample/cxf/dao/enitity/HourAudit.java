package com.yoloz.sample.cxf.dao.enitity;

import java.io.Serializable;
import java.util.List;

/**
 * 服务小时审计统计的信息.
 * User: lil
 * Date: 2011-1-14
 * Time: 15:16:31
 */
public class HourAudit implements Serializable {
    private static final long serialVersionUID = 1L;
    private String identification = "GXPT";  //身份，见《身份列表》
    private String ip;                         //ip地址，不要是hostname
    private String version;                    //版本，如平台4.5就写“4.5”
    private String devId;                      //设备编号，要与集控对此设备配置的设备编号一致
    private List auditInfo;                   //服务的审计统计信息列表，所包含的类型是ServiceAuditInfo

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

    public List getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(List auditInfo) {
        this.auditInfo = auditInfo;
    }
}
