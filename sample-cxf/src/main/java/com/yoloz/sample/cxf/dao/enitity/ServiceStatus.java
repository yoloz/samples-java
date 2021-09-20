package com.yoloz.sample.cxf.dao.enitity;

import java.io.Serializable;

/**
 * 更新服务状态是的对象
 * User: lil
 * Date: 2011-1-14
 * Time: 15:10:16
 */
public class ServiceStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identification = "GXPT";   	//身份，见《身份列表》
    private String ip; 	                        //ip地址，不要是hostname
    private String version	;	                    //版本，如平台4.5就写“4.5”
    private String devId;	                        //设备编号，要与集控对此设备配置的设备编号一致
    private String serviceId;	                    //要添加服务的服务编号
    private String insideStatus;                  //内网服务状态，如果不存在内外网，就只填写此项。见《服务状态》
    private String outsideStatus;                 //外网服务状态。见《服务状态》

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

    public String getInsideStatus() {
        return insideStatus;
    }

    public void setInsideStatus(String insideStatus) {
        this.insideStatus = insideStatus;
    }

    public String getOutsideStatus() {
        return outsideStatus;
    }

    public void setOutsideStatus(String outsideStatus) {
        this.outsideStatus = outsideStatus;
    }
}
