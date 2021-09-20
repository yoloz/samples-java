package com.yoloz.sample.cxf.dao.enitity;

import java.io.Serializable;

/**
 * 回应
 * User: lil
 * Date: 2011-1-14
 * Time: 14:55:45
 */
public class NorResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identification;                //身份
    private String ip;                             // ip地址，不是hostname
    private String version;                        //版本
    private String result;                         // 结果：“ok”是成功，“error”是失败
    private String desc;                           // 如果是失败，这一项就是失败的代码

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
}
