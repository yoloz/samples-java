package com.yoloz.sample.cxf.dao.enitity;

import java.io.Serializable;

/**
 * 添加和修改服务时信息对象
 * User: lil
 * Date: 2011-1-14
 * Time: 14:55:06
 */
public class ServiceInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identification = "GXPT";       //身份
    private String ip;                              // ip地址，不是hostname
    private String version;                        //版本
    private String devId;                          // 设备编号，与集控对此设备配置的设备编号一致
    private String serviceId;                      // 服务编号
    private String serviceName;                    // 服务名称
    private String detailInfo;                    //服务的详细配置信息
    private String linkName;

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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }
}
